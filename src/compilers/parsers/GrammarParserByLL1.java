/**
 * @author Randy
 * @date 2013-05-30
 * @function �� LL1�� �����ķ�
 */
package compilers.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import compilers.javabean.ErrorMessageBean;
import compilers.javabean.IdentifierBean;
import compilers.javabean.LL1ProcessLog;
import compilers.javabean.Nonterminal;
import compilers.javabean.Production;
import compilers.javabean.Terminal;
import compilers.scanners.LexicalAnalysis;
import compilers.tool.HandleXMLFile;
import compilers.util.Tree;
import compilers.util.TreeElem;
import compilers.util.TreeNode;

public class GrammarParserByLL1
{
	// define member variable
	LexicalAnalysis lexicalAnalysis = null;
	ArrayList<Production> productions = new ArrayList<Production>();
	HandleXMLFile handleXMLFile = new HandleXMLFile(new File(
			"data/ErrorMessage.xml"));
	HashSet<Integer>[] firstSets; // ��ʱ����ʼ������Ϊ���Ȳ�֪��
	ArrayList<String> firstSetLog = new ArrayList<String>();// ��¼�������
	HashSet<Integer>[] followSets;
	ArrayList<String> followSetLog = new ArrayList<String>();// ��¼�������
	int nullIndexInTerminals = -1;
	int[][] LL1Table = null;
	ArrayList<LL1ProcessLog> LL1ParseLog = new ArrayList<LL1ProcessLog>();// ��¼LL1�﷨�������̹���
	// ��¼Ҫ������ÿ�����
	ArrayList<Integer[]> expressions = new ArrayList<Integer[]>();
	// ��¼Ҫ������ÿ������к���ID���ڱ�ʶ�����е�����
	ArrayList<Integer[]> expsIdIndex = new ArrayList<Integer[]>();
	Object[][] productionArray = null;
	public static int[][] LL1TableforShow = null;
	public static Object[][] productionArrayforShow = null;
	// ��¼��Ԫʽ
	ArrayList<String> quaternions = new ArrayList<String>();

	public ArrayList<String> getQuaternions()
	{
		return quaternions;
	}

	/**
	 * constructor
	 */
	public GrammarParserByLL1(LexicalAnalysis lexicalAnalysis)
	{
		this.lexicalAnalysis = lexicalAnalysis;
	}

	public ArrayList<Integer[]> getExpressions()
	{
		return expressions;
	}

	public Object[][] getProductionArray()
	{
		return productionArray;
	}

	public int[][] getLL1Table()
	{
		return LL1Table;
	}

	public ArrayList<Production> getProductions() {
		return productions;
	}

	public HashSet<Integer>[] getFirstSets()
	{
		return firstSets;
	}

	public HashSet<Integer>[] getFollowSets()
	{
		return followSets;
	}

	public ArrayList<String> getFirstSetLog() {
		return firstSetLog;
	}

	public ArrayList<String> getFollowSetLog() {
		return followSetLog;
	}

	public ArrayList<LL1ProcessLog> getlL1ParseLog() {
		return LL1ParseLog;
	}

	/**
	 * ========================================================================
	 * LL1�ķ�������1��: ��ʼ������ʽ productions
	 * 
	 * @return ����ķ���ȷ������true������false����ʼ��productionsʧ�ܣ�
	 */
	public boolean FindProductions()
	{
		// 1. �����ķ����ҳ��ս��
		lexicalAnalysis.clearLexicalAnalysisResult(); // ��մʷ�������ؽ����
		lexicalAnalysis.scanRows(); // �ʷ�����
		if (lexicalAnalysis.errorMessageBeans.size() != 0)
		{
			// �����ϼ����ó��������
			return false;
		}
		String[] duals = lexicalAnalysis.getDualExpression().split(" ");
		int nonterminalIndex = 0;
		if (duals[0].charAt(1) != '%' || duals[1].charAt(1) != '%')
		{
			// �����ķ����������"%%"��ͷ
			this.addGrammarParserError("B0001", getDualLeft(duals[0])
					+ getDualLeft(duals[1]), lexicalAnalysis.errorMessageBeans);
			return false;			
		}
		// ƥ�䵽 "%%" �ս����ʼ
		for (int i = 2; i < duals.length - 1; i++)
		{
			//						������%%��Ϊ���жϺ���Ĳ���ʽ
			if (duals[i].charAt(1) == '%' && duals[i + 1].charAt(1) == '%')				
			{
				nonterminalIndex = i;
				break;
			}
			String dual = duals[i];
			int j = dual.lastIndexOf(",");
			String terminal = dual.substring(1, j);
			// ���Ϊ��ʶ�����ѱ�ʶ���ҵ�����ӵ��ս������
			if (terminal.equals("ID"))
			{
				j = Integer.parseInt(dual.substring(j + 1,
						dual.indexOf(")")));
				terminal = lexicalAnalysis.identifierList.get(j)
						.getName();
			}
			Terminal.terminals.add(terminal);
		}

		// 2. �����ķ����ҳ����ս��
		if (nonterminalIndex == 0)
		{
			// ��������ʽ������"%%"��ͷ
			this.addGrammarParserError("B0002", "",
					lexicalAnalysis.errorMessageBeans);
			return false;
		}
		// ���ս����ӵ��ؼ��ֱ���
		for (int i = 0; i < Terminal.terminals.size(); i++)
		{
			lexicalAnalysis.keywordsHashMap.put(Terminal.get(i), -1);
		}
		// ��� lexicalAnalysis.identifierList��
		lexicalAnalysis.clearLexicalAnalysisResult();
		lexicalAnalysis.scanRows(); // �ʷ�����(!!! �ؼ��ֱ��Ѿ��ı�)
		duals = lexicalAnalysis.getDualExpression().split(" ");
		// ��ʼ�����ս��
		for (int i = 0; i < lexicalAnalysis.identifierList.size(); i++)
		{
			Nonterminal.nonterminals.add(lexicalAnalysis.identifierList.get(i)
					.getName());
		}
		// ��ò���ʽ
		nonterminalIndex += 2;
		for (int i = nonterminalIndex; i < duals.length; i++)
		{
			Production production = new Production();
			if (! this.getDualLeft(duals[i]).equals("ID"))
			{
				// ��������ʽͷ������Ϊ���ս��
				this.addGrammarParserError("B0003", "",
						lexicalAnalysis.errorMessageBeans);
				return false;
			}
			production.head = this.getDualRight(duals[i]);
			i++;
			if (duals[i].charAt(1) != ':')
			{
				// ��������ʽ�󲿺��Ҳ�������':'����
				this.addGrammarParserError("B0004", "",
						lexicalAnalysis.errorMessageBeans);
				return false;				
			}
			i++;
			String dual = duals[i];
			while (dual.charAt(1) != ';')
			{
				if (this.getDualLeft(dual).equals("ID"))
				{
					production.production.add(new Nonterminal(this
							.getDualRight(dual)));
				}
				else
				{
					int index = Terminal
							.IndexOf(this.getDualLeft(dual));
					production.production.add(new Terminal(index));
				}
				// �жϲ���ʽ�Ƿ���';'��β
				if (i + 1 < duals.length)
				{
					dual = duals[++i];
				}
				else
				{
					// ��������ʽ������';'��β
					this.addGrammarParserError("B0005", "",
							lexicalAnalysis.errorMessageBeans);
					return false;
				}
			}
			
			productions.add(production);
		}
		return true;
	}

	/**
	 * ========================================================================
	 * LL1�ķ�������2��: ����first����FirstSets��
	 * 
	 * @return ��ȷ����õ���first��������true
	 */
	@SuppressWarnings("unchecked")
	public boolean ComputeFirstSets()
	{
		if (this.productions.size() == 0)
		{
			// ��������ʽ�գ����ܽ����ķ�����
			this.addGrammarParserError("B0006", "",
					lexicalAnalysis.errorMessageBeans);
			return false;
		}
		// ��'#'���뵽�ս����
		Terminal.terminals.add("#");

		// ��ʼ�� first��
		this.firstSets = new HashSet[Nonterminal.nonterminals.size()];
		for (int i = 0; i < firstSets.length; i++)
		{
			firstSets[i] = new HashSet<Integer>();
		}
		// 1. ���ݹ���һ��X->a...
		for (int i = 0; i < productions.size(); i++)
		{
			Production production = productions.get(i);
			// �����ͷΪ�ս��������first��
			if (isTerminalObject(production.production.get(0)))
			{
				Terminal terminal = (Terminal) production.production.get(0);
				firstSets[production.head].add(terminal.index);
				// write log
				String head = Nonterminal.get(production.head);
				String firstTerminal = Terminal.get(terminal.index);
				firstSetLog.add("���ݹ���һ���Ѳ���ʽ[" + production.showProduction() 
						+ "]�е�[" + firstTerminal + "]���뵽First(" + head + "),��"
						+ this.getOneFirstSet(production.head));
			}
		}
		// 2. ���ݹ������X->Y...��YΪ���ս����
		nullIndexInTerminals = Terminal.IndexOf("null");
		// ѭ��Ѱ�ң�ֱ��ÿ��first���еĸ�����������
		int num = 0;
		int count=0;
		while (num != (num = this.getNumOfAllSetElements(firstSets)))
		{
			for (int i = 0; i < productions.size(); i++)
			{
				Production production = productions.get(i);
				boolean allContainNull = true;
				int j = 0;
				for (; !isTerminalObject(production.production.get(j))
						&& j < production.production.size(); j++)
				{
					Nonterminal nonterminal = (Nonterminal) production.production
							.get(j);
					// ���first���а�����
					if (firstSets[nonterminal.index]
							.contains(nullIndexInTerminals))
					{
						this.addNotContainsNullFirstSetToAnother(
								firstSets[nonterminal.index],
								firstSets[production.head],
								nullIndexInTerminals);
					}
					else
					{
						allContainNull = false;
						this.addOneSetToAnother(firstSets[nonterminal.index],
								firstSets[production.head]);
						break;
					}
				}
				// ��� X->YZ...K����YZ...Kȫ�������գ����null��ӵ�first(X)��
				if (allContainNull && j == production.production.size())
				{
					firstSets[production.head].add(nullIndexInTerminals);
				}
			}
		}
		return true;
	}

	/**
	 * ========================================================================
	 * LL1�ķ�������3��: ����follow����followSets��
	 * 
	 * @return ��ȷ����õ���follow��������true
	 */
	@SuppressWarnings("unchecked")
	public boolean ComputeFollowSets()
	{
		if (this.productions.size() == 0)
		{
			// ��������ʽ�գ����ܽ����ķ�����
			this.addGrammarParserError("B0006", "",
					lexicalAnalysis.errorMessageBeans);
			return false;
		}

		// ��ʼ�� follow��
		this.followSets = new HashSet[Nonterminal.nonterminals.size()];
		for (int i = 0; i < followSets.length; i++)
		{
			followSets[i] = new HashSet<Integer>();
		}
		// 1. ���ݹ���һ�������ķ���ʼ����S����'#'����follow(S)��
		followSets[0].add(Terminal.IndexOf("#"));

		// 2. ���ݹ����������A->...Bb���򽫷ǿ�first��b���ӵ�follow��B����
		for (int i = 0; i < productions.size(); i++)
		{
			Production production = productions.get(i);
			for (int j = 0; j < production.production.size() - 1; j++)
			{
				// ֻ�Է��ս������
				if (!isTerminalObject(production.production.get(j)))
				{
					Nonterminal nonterminal = (Nonterminal) production.production
							.get(j);
					if (isTerminalObject(production.production.get(j + 1)))
					{
						// ���ս����ӵ�follow����
						Terminal terminal = (Terminal) production.production
								.get(j + 1);
						followSets[nonterminal.index].add(terminal.index);
						j++;
					}
					else
					{
						// ���ǿ�first��j+1���ӵ�follow��j����
						Nonterminal nonterminal2 = (Nonterminal) production.production
								.get(j + 1);
						if (firstSets[nonterminal2.index]
								.contains(nullIndexInTerminals))
						{
							this.addNotContainsNullFirstSetToAnother(
									firstSets[nonterminal2.index],
									followSets[nonterminal.index],
									nullIndexInTerminals);
						}
						else
						{
							this.addOneSetToAnother(
									firstSets[nonterminal2.index],
									followSets[nonterminal.index]);
						}
					}
				}// end if
			}
		}
		// 3. ���ݹ�����������A->...Bb���򽫷ǿ�follow��b���ӵ�follow��B����
		nullIndexInTerminals = Terminal.IndexOf("null");
		// ѭ��Ѱ�ң�ֱ��ÿ��follow���еĸ�����������
		int num = 0;
		while (num != (num = this.getNumOfAllSetElements(followSets)))
		{
			for (int i = 0; i < productions.size(); i++)
			{
				Production production = productions.get(i);
				int productionEleNum = production.production.size();
				for (int j = 0; j < productionEleNum; j++)
				{
					// ֻ�Է��ս������
					if (!isTerminalObject(production.production.get(j)))
					{
						Nonterminal nonterminal = (Nonterminal) production.production
								.get(j);
						if (j == productionEleNum - 1)
						{
							addOneSetToAnother(followSets[production.head],
									followSets[nonterminal.index]);
							break;
						}
						if (isTerminalObject(production.production.get(j + 1)))
						{
							j++;
						}
						else
						{
							Nonterminal nonterminal2 = (Nonterminal) production.production
									.get(j + 1);
							// ���b�ǿգ�
							if (firstSets[nonterminal2.index]
									.contains(nullIndexInTerminals))
							{
								addOneSetToAnother(followSets[production.head],
										followSets[nonterminal.index]);
							}
						}
					}// end if
				}// end inner for
			}// end outer for
		}// end while
		return true;
	}

	/**
	 * ========================================================================
	 * LL1�ķ�������4��: ����LL1Ԥ�������
	 * 
	 * @return ��ȷ������ LL1Table ������true
	 */
	public boolean buildLL1Table()
	{
		if (this.productions.size() == 0)
		{
			// ��������ʽ�գ����ܽ����ķ�����
			this.addGrammarParserError("B0006", "",
					lexicalAnalysis.errorMessageBeans);
			return false;
		}

		// ��ʼ�� LL1Table
		LL1Table = new int[Nonterminal.nonterminals.size()][Terminal.terminals.size()];
		// �� LL1Table �е�ÿ��Ԫ�س�ʼ��Ϊ-2��-2��ʾ����
		for (int i = 0; i < LL1Table.length; i++)
		{
			for (int j = 0; j < LL1Table[i].length; j++)
			{
				LL1Table[i][j] = -2;
			}
		}
		productionArray = new Object[productions.size()][];
		nullIndexInTerminals = Terminal.IndexOf("null");
		for (int i = 0; i < productionArray.length; i++)
		{
			productionArray[i] = productions.get(i).production.toArray();
		}
		/**
		 * ����ÿ������ʽ A�������������²���
		 * 
		 * 1. ����first�������е�ÿ���ս����a����A��a���뵽LL1Table[A,a]��
		 * 
		 * 2. ���null��first�������У���ô����follow��A���е�ÿ���ս����b����A�������뵽LL1Table[A,b]�С�
		 * ...���null��first��������,��'#'��follow��A���У�Ҳ��A�������뵽LL1Table[A,#]��
		 * 
		 * 3. [����ָ�]��follow��A�������з���c�ŵ�LL1Table[A,c]�У���-1��䡣
		 */
		for (int i = 0; i < productionArray.length; i++)
		{
			int head = productions.get(i).head;
			// �����Ϊ�ս��
			if (isTerminalObject(productionArray[i][0]))
			{
				Terminal terminal = (Terminal) productionArray[i][0];
				// ����ս��Ϊ��
				if (terminal.index == nullIndexInTerminals)
				{
					this.setLL1TableRow(head, followSets[head], i);
				}
				// ����ս����Ϊ��
				else
				{
					LL1Table[head][terminal.index] = i;

					// 3. [����ָ�]��follow��A�������з���c�ŵ�LL1Table[A,c]�У���-1��䡣
					this.setLL1TableRow(head, followSets[head], -1);
				}
			}
			// �����Ϊ���ս��
			else
			{
				Nonterminal nonterminal = (Nonterminal) productionArray[i][0];
				// ����first�������е�ÿ���ս����a����A��a���뵽LL1Table[A,a]��
				this.setLL1TableRow(head, firstSets[nonterminal.index], i);
				// ���null��first��������
				if (firstSets[nonterminal.index].contains("null"))
				{
					this.setLL1TableRow(head, followSets[head], i);
				}
				else
				{
					// 3. [����ָ�]��follow��A�������з���c�ŵ�LL1Table[A,c]�У���-1��䡣
					this.setLL1TableRow(head, followSets[head], -1);
				}
			}
		}// end for
		return true;
	}

	/**
	 * ========================================================================
	 * LL1�ķ�������5��: ��������Ԥ���﷨����
	 * 
	 * @return �﷨������ȷ ������true�����򷵻�false��������Ϣ������
	 *         lexicalAnalysis.errorMessageBeans �У�
	 */
	public boolean LL1Parser(String fileName)
	{
		lexicalAnalysis.setFileName(fileName);
		lexicalAnalysis.clearLexicalAnalysisResult();
		this.clearSyntaxAnalysisResult();
		lexicalAnalysis.scanRows();
		if (lexicalAnalysis.errorMessageBeans.size() != 0)
		{
			// �����ϼ����ó��������
			return false;
		}
		if (lexicalAnalysis.getDualExpression() == "") 
		{
			// �������ӣ����ʽ��Ϊ�գ�
			this.addGrammarParserError("B0008", "",
					lexicalAnalysis.errorMessageBeans);
			return false;
		}
		String[] duals = lexicalAnalysis.getDualExpression().split(" ");
		// 1. �õ�ÿ��Ҫ�����ľ��ӣ����ʽ��
		Integer[] exp;
		int endIndex = Terminal.IndexOf("#"); // �õ���������#��������
		for (int i = 0; i < duals.length; i++)
		{
			int j = i;
			while (duals[j].charAt(1) != ';')
			{
				if (++j == duals.length)
				{
					// �������ӣ����ʽ��������';'��β
					this.addGrammarParserError("B0007", "",
							lexicalAnalysis.errorMessageBeans);
					return false;
				}
			}
			exp = new Integer[j - i + 1];
			int k = 0;
			for (k = 0; k < exp.length - 1; k++)
			{
				String terminal = getDualLeft(duals[i++]);
				if (terminal.equals("ID"))
				{
					terminal = "id";
				}
				int index = Terminal.IndexOf(terminal);
				exp[k] = index;
			}
			exp[k] = endIndex; // ����#���������ӵ����ӣ����ʽ��ĩβ����ʾ����
			expressions.add(exp);
		}
		// 2. ʹ���㷨��ÿ�����ӣ����ʽ�������ķ�����
		int len = expressions.size();
		for (int i = 0; i < len; i++)
		{
			// ����ջ
			exp = expressions.get(i);
			int j = 0;
			Stack<Object> stack = new Stack<Object>();
			stack.push(new Nonterminal(0));// �ѿ�ʼ����S��ջ
			
			LL1ProcessLog LL1Log = new LL1ProcessLog();
			LL1Log.symbolStack = getSymbolStack(stack);
			LL1Log.currentSymbol = "";
			LL1Log.inputString = getExpression(j, exp);
			LL1Log.instruction = "�ѿ�ʼ����ѹ��ջ";
			LL1ParseLog.add(LL1Log);
			
			while (! stack.empty())
			{
				// ���ջ��Ϊ�ս��
				if (isTerminalObject(stack.peek()))
				{
					// TODO ��д
					Terminal terminal = (Terminal) stack.peek();
					if (terminal.index == exp[j])
					{
						LL1ProcessLog LL1Log1 = new LL1ProcessLog();
						LL1Log1.symbolStack = getSymbolStack(stack);
						LL1Log1.currentSymbol = Terminal.get(exp[j]);
						LL1Log1.inputString = getExpression(j+1, exp);
						LL1Log1.instruction = "ƥ�䣬����ջ������" + Terminal.get(exp[j])
								+ "����������һ����" + Terminal.get(exp[j+1]);
						LL1ParseLog.add(LL1Log1);
						
						stack.pop();
						j++; // TODO �Ƿ�Ҫ�ж�j��ֵ����ֹ���������
					}
					else
					{
						stack.pop();
						// �������ӣ����ʽ����ȱ�ٵ�ǰ�ս�����ķ�ƥ��
						this.addGrammarParserError("B0009", "",
								lexicalAnalysis.errorMessageBeans);
					}
				}
				// ���ջ��Ϊ���ս��
				else
				{
					Nonterminal nonterminal = (Nonterminal) stack.peek();
					if (LL1Table[nonterminal.index][exp[j]] == -2)
					{
						// �������ӣ����ʽ���е�ǰ�ս�����࣬��ʹ��ָ������һ��λ�ã���������
						this.addGrammarParserError("B0010", "",
								lexicalAnalysis.errorMessageBeans);
						
						LL1ProcessLog LL1Log4 = new LL1ProcessLog();
						LL1Log4.symbolStack = getSymbolStack(stack);
						LL1Log4.currentSymbol = Terminal.get(exp[j]);
						LL1Log4.inputString = getExpression(j+1, exp);
						LL1Log4.instruction = "�������봮�е�ǰ�ս��" + Terminal.get(exp[j]) 
								+ "���࣬ʹ��ָ������һ��λ�ã���������";
						LL1ParseLog.add(LL1Log4);
						
						j++;
					}
					else if (LL1Table[nonterminal.index][exp[j]] == -1)
					{
						// �������ӣ����ʽ����ȱ��A��ʾ�Ľṹ����ջ�е���A����������
						this.addGrammarParserError("B0011", "",
								lexicalAnalysis.errorMessageBeans);
						
						LL1ProcessLog LL1Log5 = new LL1ProcessLog();
						LL1Log5.symbolStack = getSymbolStack(stack);
						LL1Log5.currentSymbol = Terminal.get(exp[j]);
						LL1Log5.inputString = getExpression(j+1, exp);
						Nonterminal nonterminal2 = (Nonterminal)stack.peek();
						String nontermialString = Nonterminal.get(nonterminal2.index);
						LL1Log5.instruction = "�������봮����ȱ��" + nontermialString 
								+ "��ʾ�Ľṹ����ջ�е���" + nontermialString + "����������";
						LL1ParseLog.add(LL1Log5);
						
						stack.pop();
					}
					// û�г�����������
					else
					{
						LL1ProcessLog LL1Log2 = new LL1ProcessLog();
						LL1Log2.symbolStack = getSymbolStack(stack);
						LL1Log2.currentSymbol = Terminal.get(exp[j]);
						LL1Log2.inputString = getExpression(j+1, exp);
						
						stack.pop();
						int proNum = LL1Table[nonterminal.index][exp[j]];
						Object[] production = productionArray[proNum];

						if (productions.get(proNum).isContainNull()) 
						{
							LL1Log2.instruction = "չ������ǰ����ʽ��" + productions.get(proNum).showProduction()
									+ "��������ջ������";
						}
						else
						{
							LL1Log2.instruction = "չ������ǰ����ʽ��" + productions.get(proNum).showProduction()
												+ "���������Ҳ�������ջ";
						}						
						LL1ParseLog.add(LL1Log2);

						if (!isTerminalObject(production[0])
								|| ((Terminal) production[0]).index != nullIndexInTerminals)
						{
							for (int k = production.length - 1; k > -1; k--)
							{
								stack.push(production[k]);
							}
						}
					}
				}// end else
			}// end while
			LL1ProcessLog LL1Log3 = new LL1ProcessLog();
			LL1Log3.symbolStack = getSymbolStack(stack);
			LL1Log3.currentSymbol = Terminal.get(exp[j]);
			LL1Log3.inputString = "";
			LL1Log3.instruction = "ƥ�䣬�����ɹ� ^_^";
			LL1ParseLog.add(LL1Log3);
			
			if (i < len) 
			{
				LL1ParseLog.add(new LL1ProcessLog());
			}
		}
		return (lexicalAnalysis.errorMessageBeans.size() == 0);
	}
	
	/**
	 * ========================================================================
	 * LL1�ķ�������6��: �����ķ��������
	 * 
	 * @return ���������ȷ ������true�����򷵻�false��������Ϣ������
	 *         lexicalAnalysis.errorMessageBeans �У�
	 */
	public boolean semanticAnalysis(String fileName)
	{
		lexicalAnalysis.setFileName(fileName);
		lexicalAnalysis.clearLexicalAnalysisResult();
		this.clearSyntaxAnalysisResult();
		lexicalAnalysis.scanRows();
		if (lexicalAnalysis.errorMessageBeans.size() != 0)
		{
			// �����ϼ����ó��������
			return false;
		}
		String[] duals = lexicalAnalysis.getDualExpression().split(" ");
		// 1. �õ�ÿ��Ҫ�����ľ��ӣ����ʽ��
		Integer[] exp;
		Integer[] expIdIndex;
		int endIndex = Terminal.IndexOf("#"); // �õ���������#��������
		for (int i = 0; i < duals.length; i++)
		{
			int j = i;
			while (duals[j].charAt(1) != ';')
			{
				if (++j == duals.length)
				{
					// �������ӣ����ʽ��������';'��β
					this.addGrammarParserError("B0007", "",
							lexicalAnalysis.errorMessageBeans);
					return false;
				}
			}
			exp = new Integer[j - i + 1];
			expIdIndex = new Integer[j - i + 1];
			int k = 0;
			for (k = 0; k < exp.length - 1; k++)
			{
				String terminal = getDualLeft(duals[i++]);
				int idIndex = -1;
				if (terminal.equals("ID"))
				{
					terminal = "id";
					idIndex = getDualRight(duals[i - 1]);
				}
				int index = Terminal.IndexOf(terminal);
				exp[k] = index;
				expIdIndex[k] = idIndex;
			}
			exp[k] = endIndex; // ����#���������ӵ����ӣ����ʽ��ĩβ����ʾ����
			expIdIndex[k] = -1;
			expressions.add(exp);
			expsIdIndex.add(expIdIndex);
		}
		// 2. ʹ���㷨��ÿ�����ӣ����ʽ�������ķ�����

		// ��ʼ���ս���ͷ��ս����������Ӧ�Ĺ�ϵ���ӿ�����ٶ�
		HashMap<String, Integer> terminalIndex = new HashMap<String, Integer>();
		HashMap<String, Integer> nonterminalIndex = new HashMap<String, Integer>();
		for (int i = 0; i < Terminal.terminals.size(); i++)
		{
			terminalIndex.put(Terminal.get(i), i);
		}
		for (int i = 0; i < Nonterminal.nonterminals.size(); i++)
		{
			nonterminalIndex.put(Nonterminal.get(i), i);
		}

		for (int i = 0; i < expressions.size(); i++)
		{
			// ����ջ
			exp = expressions.get(i);
			expIdIndex = expsIdIndex.get(i);
			int j = 0;
			Stack<Object> stack = new Stack<Object>(); // �﷨ջ
			Stack<Integer> semanStack = new Stack<Integer>(); // ����ջ
			stack.push(new Nonterminal(0));// �ѿ�ʼ����S��ջ

			// ��ʼ�����﷨�� ���Ѹ��ڵ���ջ
			Tree semanTree = new Tree();
			Nonterminal tempNonterminal = new Nonterminal(
					nonterminalIndex.get("G"));
			Terminal tempTerminal = null;
			int nodeIndex = semanTree.addNode(new TreeNode(new TreeElem(
					tempNonterminal), 0, -1, -1)); // father�ڵ���Ϊ0���䲻������Ϊ��������򣬲���Ӱ��
			semanStack.push(nodeIndex);

			// �﷨ջ�ս���
			while (!stack.empty())
			{
				// TODO �������1�� ���ս����ջʱ���������嶯��
				// ���ջ��Ϊ�ս��
				if (isTerminalObject(stack.peek()))
				{
					// TODO ��д
					Terminal terminal = (Terminal) stack.peek();
					if (terminal.index == exp[j])
					{
						stack.pop();
						j++; // TODO �Ƿ�Ҫ�ж�j��ֵ����ֹ���������

						// ������� int, float, id
						int currentNode = semanStack.pop();
						TreeNode currentTreeNode = semanTree.nodes
								.get(currentNode);
						TreeNode fatherTreeNode = semanTree.nodes
								.get(currentTreeNode.father);
						// int ��ջ
						if (((Terminal) (currentTreeNode.elem.token)).index == terminalIndex
								.get("int"))
						{
							fatherTreeNode.elem.type = "int";
						}
						// float ��ջ
						else if (((Terminal) (currentTreeNode.elem.token)).index == terminalIndex
								.get("float"))
						{
							fatherTreeNode.elem.type = "float";
						}
						// + ��ջ
						else if (((Terminal) (currentTreeNode.elem.token)).index == terminalIndex
								.get("+"))
						{
							currentTreeNode.elem.val = lexicalAnalysis
									.newTempInIdentifierList();
						}
						// * ��ջ
						else if (((Terminal) (currentTreeNode.elem.token)).index == terminalIndex
								.get("*"))
						{
							currentTreeNode.elem.val = lexicalAnalysis
									.newTempInIdentifierList();
						}
						// id ��ջ
						else if (((Terminal) (currentTreeNode.elem.token)).index == terminalIndex
								.get("id"))
						{
							int id_entry = expIdIndex[j - 1];

							// ���ڵ� �� ��F���� F �� id
							if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
									.get("F"))
							{
								fatherTreeNode.elem.val = id_entry;
							}
							// ���ڵ� �� ��C����C1���� C��id C1 �� C1��, id C1
							else if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
									.get("C")
									|| ((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
											.get("C1"))
							{
								// ������Ԫʽ fill(id.type = c/c1.inh)
								IdentifierBean id = lexicalAnalysis.identifierList
										.get(id_entry);
								String type = fatherTreeNode.elem.type;
								quaternions.add("fill(" + id.getName()
										+ ".type = " + type + ")");
								id.setType(type);
							}
							// ���ڵ� �� ��F���� F �� id
							else if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
									.get("A"))
							{
								fatherTreeNode.elem.inh = id_entry;
							}
						}
					}
					else
					{
						stack.pop();
						semanStack.pop(); // TODO ��ʱ�������������
						// �������ӣ����ʽ����ȱ�ٵ�ǰ�ս�����ķ�ƥ��
						this.addGrammarParserError("B0009", "",
								lexicalAnalysis.errorMessageBeans);
					}
				}
				// ���ջ��Ϊ���ս��
				else
				{
					Nonterminal nonterminal = (Nonterminal) stack.peek();
					if (LL1Table[nonterminal.index][exp[j]] == -2)
					{
						// �������ӣ����ʽ���е�ǰ�ս�����࣬��ʹ��ָ������һ��λ�ã���������
						this.addGrammarParserError("B0010", "",
								lexicalAnalysis.errorMessageBeans);
						j++;
					}
					else if (LL1Table[nonterminal.index][exp[j]] == -1)
					{
						// �������ӣ����ʽ����ȱ��A��ʾ�Ľṹ����ջ�е���A����������
						this.addGrammarParserError("B0011", "",
								lexicalAnalysis.errorMessageBeans);
						stack.pop();
						semanStack.pop(); // TODO ��ʱ�������������
					}
					// û�г�����������
					else
					{
						// TODO �������2�� �����ս����ջʱ�����е����嶯��
						stack.pop(); // �﷨��ջ
						int currentNode = semanStack.pop(); // �����ջ
						int proNum = LL1Table[nonterminal.index][exp[j]];
						Object[] production = productionArray[proNum];

						// ���� null ��ջ
						if (!isTerminalObject(production[0])
								|| ((Terminal) production[0]).index != nullIndexInTerminals)
						{
							for (int k = production.length - 1; k > -1; k--)
							{
								stack.push(production[k]);
								// Ϊ���������Ҷ��
								TreeElem treeElem = new TreeElem(production[k]);
								TreeNode treeNode = new TreeNode(treeElem,
										currentNode, -1, -1);
								// add node
								int temp = semanTree.addNode(treeNode);
								// ���� nextSibling
								if (production.length - k > 1)
								{
									semanTree.nodes.get(temp).nextSibling = temp - 1;
								}
								// ���� currentNode �� firstChild
								if (k == 0)
								{
									semanTree.nodes.get(currentNode).firstChild = temp;
								}
								semanStack.push(temp);
							}

							// ���ս�� ��ջ ���嶯��
							TreeNode currentTreeNode = semanTree.nodes
									.get(currentNode);
							TreeNode fatherTreeNode = semanTree.nodes
									.get(currentTreeNode.father);
							// D��B C U : C.type = B.type
							if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("C"))
							{
								currentTreeNode.elem.type = semanTree.nodes
										.get(fatherTreeNode.firstChild).elem.type;
							}
							// C��id C1 �� C1��, id C1 : C1.type = C/C1.type
							else if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("C1"))
							{
								currentTreeNode.elem.type = semanTree.nodes
										.get(fatherTreeNode.index).elem.type;
							}
							else if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("T1"))
							{
								// T��F T1 : T1.inh = F.val
								if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("T"))
								{
									currentTreeNode.elem.inh = semanTree.nodes
											.get(fatherTreeNode.firstChild).elem.val;
								}
							}
						}
						// �Ƴ� null ʱ
						else
						{
							// null ��ջ ���嶯��
							TreeNode currentTreeNode = semanTree.nodes
									.get(currentNode);
							TreeNode fatherTreeNode = semanTree.nodes
									.get(currentTreeNode.father);

							// E1��null
							if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("E1"))
							{
								int k = stack.size() - 1;
								for (; isTerminalObject(stack.get(k)); k--)
									;
								TreeNode topTreeNode = semanTree.nodes
										.get(semanStack.get(k));// ����ֱ�����Ƴ�null�Ľڵ�

								// E1��+ T E1, E1��null
								if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("E1"))
								{ // (+, id, id, T1)
									currentTreeNode.elem.val = semanTree.nodes
											.get(fatherTreeNode.firstChild).elem.val; // '+'
																						// ���ɵ���ʱ����
									IdentifierBean id1 = lexicalAnalysis.identifierList
											.get(fatherTreeNode.elem.inh);
									IdentifierBean id2 = lexicalAnalysis.identifierList
											.get(currentTreeNode.elem.inh);
									IdentifierBean id3 = lexicalAnalysis.identifierList
											.get(currentTreeNode.elem.val);
									quaternions.add("(+, " + id1.getName()
											+ ", " + id2.getName() + ", "
											+ id3.getName() + ")"); // ����(+, id,
																	// id, T1)

									while (fatherTreeNode.nextSibling != topTreeNode.index)
									{
										fatherTreeNode.elem.val = currentTreeNode.elem.val;
										currentTreeNode = fatherTreeNode;
										fatherTreeNode = semanTree.nodes
												.get(currentTreeNode.father);
									}
									fatherTreeNode.elem.val = currentTreeNode.elem.val;
									topTreeNode.elem.inh = fatherTreeNode.elem.val;
								}
								// E��T E1, E1��null : E1.inh = T.val , ���ϼ̳�
								else if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("E"))
								{
									currentTreeNode.elem.val = currentTreeNode.elem.inh;
									while (fatherTreeNode.nextSibling != topTreeNode.index)
									{
										fatherTreeNode.elem.val = currentTreeNode.elem.val;
										currentTreeNode = fatherTreeNode;
										fatherTreeNode = semanTree.nodes
												.get(fatherTreeNode.father);
									}
									fatherTreeNode.elem.val = currentTreeNode.elem.val;
									topTreeNode.elem.inh = fatherTreeNode.elem.val;
								}
							}
							// T1��null
							else if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("T1"))
							{
								int k = stack.size() - 1;
								for (; isTerminalObject(stack.get(k)); k--)
									;
								TreeNode topTreeNode = semanTree.nodes
										.get(semanStack.get(k));// ����ֱ�����Ƴ�null�Ľڵ�

								// T��F T1, T1��* F T1
								if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("T1"))
								{ // (*, id, id, T1)
									currentTreeNode.elem.val = semanTree.nodes
											.get(fatherTreeNode.firstChild).elem.val; // '*'
																						// ���ɵ���ʱ����
									TreeNode fathersFirstChild = semanTree.nodes
											.get(fatherTreeNode.firstChild);
									currentTreeNode.elem.inh = semanTree.nodes
											.get(fathersFirstChild.nextSibling).elem.val;
									IdentifierBean id1 = lexicalAnalysis.identifierList
											.get(fatherTreeNode.elem.inh);
									IdentifierBean id2 = lexicalAnalysis.identifierList
											.get(currentTreeNode.elem.inh);
									IdentifierBean id3 = lexicalAnalysis.identifierList
											.get(currentTreeNode.elem.val);
									quaternions.add("(*, " + id1.getName()
											+ ", " + id2.getName() + ", "
											+ id3.getName() + ")"); // ����(*, id,
																	// id, T1)
									while (fatherTreeNode.nextSibling != topTreeNode.index)
									{
										fatherTreeNode.elem.val = currentTreeNode.elem.val;
										currentTreeNode = fatherTreeNode;
										fatherTreeNode = semanTree.nodes
												.get(currentTreeNode.father);
									}
									fatherTreeNode.elem.val = currentTreeNode.elem.val;
									topTreeNode.elem.inh = fatherTreeNode.elem.val;
								}
								// T��F T1, E1��null : T1.inh = F.val, T1.val =
								// T1.inh , ���ϼ̳�
								else if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("T"))
								{
									currentTreeNode.elem.inh = semanTree.nodes
											.get(fatherTreeNode.firstChild).elem.val;
									currentTreeNode.elem.val = currentTreeNode.elem.inh;
									while (fatherTreeNode.nextSibling != topTreeNode.index)
									{
										fatherTreeNode.elem.val = currentTreeNode.elem.val;
										currentTreeNode = fatherTreeNode;
										fatherTreeNode = semanTree.nodes
												.get(fatherTreeNode.father);
									}
									fatherTreeNode.elem.val = currentTreeNode.elem.val;
									topTreeNode.elem.inh = fatherTreeNode.elem.val;
								}
							}
							// A��id = E U, U��null
							else if (((Nonterminal) (currentTreeNode.elem.token)).index == nonterminalIndex
									.get("U"))
							{
								if (((Nonterminal) (fatherTreeNode.elem.token)).index == nonterminalIndex
										.get("A"))
								{
									// (=, id, _, T1)
									IdentifierBean id1 = lexicalAnalysis.identifierList
											.get(fatherTreeNode.elem.inh);
									IdentifierBean id3 = lexicalAnalysis.identifierList
											.get(currentTreeNode.elem.inh);
									quaternions.add("(=, " + id1.getName()
											+ ", _, " + id3.getName() + ")");
								}
							}
						}
					}
				}
			}
		}
		return (lexicalAnalysis.errorMessageBeans.size() == 0);
	}

	public void setLL1TableRow(int row, HashSet<Integer> set, int value)
	{
		Iterator<Integer> iterator = set.iterator();
		while (iterator.hasNext())
		{
			LL1Table[row][iterator.next()] = value;
		}
	}

	/**
	 * ����hashSets������Ԫ�صĸ���
	 * 
	 * @param hashSets
	 * @return
	 */
	public int getNumOfAllSetElements(HashSet<Integer>[] hashSets)
	{
		int num = 0;
		for (int i = 0; i < hashSets.length; i++)
		{
			num += hashSets[i].size();
		}
		return num;
	}

	/**
	 * ��һ��first���е�Ԫ�أ�ȫ����ӵ���һ��first����
	 * 
	 * @param fromSet
	 * @param toSet
	 */
	public void addOneSetToAnother(HashSet<Integer> fromSet,
			HashSet<Integer> toSet)
	{
		Iterator<Integer> iterable = fromSet.iterator();
		while (iterable.hasNext())
		{
			toSet.add(iterable.next());
		}
	}

	/**
	 * ��һ������first���еķǿ�Ԫ�أ�ȫ����ӵ���һ��first����
	 * 
	 * @param fromSet
	 * @param toSet
	 */
	public void addNotContainsNullFirstSetToAnother(HashSet<Integer> fromSet,
			HashSet<Integer> toSet, int nullIndexInTerminals)
	{
		Iterator<Integer> iterable = fromSet.iterator();
		while (iterable.hasNext())
		{
			int terminalIndex = iterable.next();
			if (terminalIndex != nullIndexInTerminals)
			{
				toSet.add(terminalIndex);
			}
		}
	}

	/**
	 * �ж϶����Ƿ�Ϊ Terminal ����
	 * 
	 * @param testObject
	 * @return
	 */
	public static boolean isTerminalObject(Object testObject)
	{
		return testObject instanceof Terminal;
	}

	/**
	 * ��Ӵ��󵽴����б� lexicalAnalysis.errorMessageBeans
	 * 
	 * @param errorCode
	 * @param errorMessageBeans
	 */
	public void addGrammarParserError(String errorCode, String errorWord,
			ArrayList<ErrorMessageBean> errorMessageBeans)
	{
		ErrorMessageBean e = new ErrorMessageBean(errorCode, errorWord, -1);
		errorMessageBeans.add(e);
	}

	/**
	 * ���� ����(ID,2) ������ֵ(�Ҳ�)
	 * 
	 * @param dual
	 * @return ��������ֵ�� ���� (ID,2) ����2
	 */
	public int getDualRight(String dual)
	{
		int last = dual.lastIndexOf(",");
		return Integer.parseInt(dual.substring(last + 1,
				dual.indexOf(")", last)));
	}

	/**
	 * ���� ����(ID,2) ����ֵ
	 * 
	 * @param dual
	 * @return ������ֵ�� ���� (ID,2) ���� "ID"
	 */
	public String getDualLeft(String dual)
	{
		return dual.substring(1, dual.lastIndexOf(","));
	}

	/*
	 * public boolean isNodeEqual(Object node1) { if (isTerminalObject(node)) {
	 * 
	 * } }
	 */
	
	// ����ָ����First��
	public String getOneFirstSet(int head) 
	{
		String out = "";
		out += "First(" + Nonterminal.get(head) + ") = ";
		out += "{ ";
		Iterator<Integer> iterator = firstSets[head].iterator();
		while (iterator.hasNext())
		{
			out += Terminal.get(iterator.next()) + " ";
		}
		out += "}";
		return out;
	}
	
	// ����ָ����Follow��
	public String getOneFollowSet(int head) 
	{
		String out = "";
		out += "Follow(" + Nonterminal.get(head) + ") = ";
		out += "{ ";
		Iterator<Integer> iterator = followSets[head].iterator();
		while (iterator.hasNext())
		{
			out += Terminal.get(iterator.next()) + " ";
		}
		out += "}";
		return out;
	}
	
	// ��ñ��ʽ���ַ�����ʾ��ʽ expression
	public String getExpression(int startIndex, Integer[] expression) 
	{
		String out = "";
		for (int i = startIndex; i < expression.length; i++) 
		{
			out += Terminal.get(expression[i]) + " ";
		}		
		return out;
	}
	
	// ��÷���ջ�е��ַ�����ʾ��ʽ
	public String getSymbolStack(Stack<Object> stack)
	{
		String out = "# ";
		for (int i = 0; i < stack.size(); i++) 
		{
			if (isTerminalObject(stack.get(i))) 
			{
				Terminal terminal = (Terminal)stack.get(i);
				out += Terminal.get(terminal.index) + " ";
			}
			else
			{
				Nonterminal nonterminal = (Nonterminal)stack.get(i);
				out += Nonterminal.get(nonterminal.index) + " ";
			}
		}
		return out;
	}
	
	// ����﷨�������
	public void clearSyntaxAnalysisResult() 
	{
		this.LL1ParseLog.clear();
		this.expressions.clear();
		this.quaternions.clear();
		this.expressions.clear();
		this.expsIdIndex.clear();
	}
}
