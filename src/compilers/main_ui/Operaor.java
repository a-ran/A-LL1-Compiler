/**
 * @author Randy 
 * @date 2013-05-25
 * @function �Ը�������Ԫ�صĲ���
 */
package compilers.main_ui;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import compilers.javabean.IdentifierBean;
import compilers.javabean.LL1ProcessLog;
import compilers.javabean.Nonterminal;
import compilers.javabean.Production;
import compilers.javabean.Terminal;
import compilers.parsers.GrammarParserByLL1;
import compilers.scanners.LexicalAnalysis;
import compilers.tool.HandleXMLFile;

public class Operaor
{
	// define variable
	MainUI mainUI = null;
	MyText myText = null;
	LexicalAnalysis lexicalAnalysis = null;
	boolean isInitSeman = false; // �Ƿ����������ʱ���ķ��Ƚ�����
	boolean isSemanticAnalysis = false; // �Ƿ�������������
	GrammarParserByLL1 grammarParserByLL1 = null;
	String grammarFilePath = "data/gra.grammar";

	/**
	 * constructor
	 */
	public Operaor(MyText myText, MainUI mainUI)
	{
		this.myText = myText;
		this.mainUI = mainUI;
	}

	public void saveFile(String filePath, String text)
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try
		{
			fw = new FileWriter(new File(filePath));
			bw = new BufferedWriter(fw);

			String[] ws = text.split("\n");
			for (int i = 0; i < ws.length - 1; i++)
			{
				bw.write(ws[i] + "\n");
			}
			bw.write(ws[ws.length - 1]);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{	// TODO ���쳣�Ĵ���
			try
			{
				if (bw != null)
				{
					bw.close();
				}
				if (fw != null)
				{
					fw.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// save the file when know the file path
	public void save(String filePath)
	{
		this.saveFile(filePath, myText.getText());
		mainUI.setTitle(filePath + " - Ran Compiler");
		myText.isChanged = false;
	}

	// ִ�н��ա�ctrl-save������ܲ���
	public void ctrlSave()
	{
		if (myText.fileName == null)
		{
			this.saveAs();
		}
		else
		{
			this.save(myText.fileName);
			myText.requestFocus(true);
		}
	}

	// save as the contents to another file
	public boolean saveAs()
	{
		JFileChooser jfc = new JFileChooser("userdata");
		jfc.setDialogTitle("Save as ...");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Ran Language File", "ran");
		jfc.setFileFilter(filter);

		int result = jfc.showSaveDialog(null);
		jfc.setVisible(true);
		
		// �ж϶Ի��򷵻�����
		if(result != JFileChooser.APPROVE_OPTION)
		{
			return false;
		}

		File saveFile = jfc.getSelectedFile();
		if (saveFile != null)
		{
			myText.fileName = saveFile.getAbsolutePath();
			// �ж��ļ����ͣ�Ĭ��Ϊ *.ran��
			int index = myText.fileName.lastIndexOf(".");
			if (-1 == index)
			{
				myText.fileName += ".ran";
			}
			this.save(myText.fileName);
		}
		return true;
	}

	// judge if save the contents
	public int judgeSaveAs()
	{
		int op = 0;
		op = JOptionPane.showInternalConfirmDialog(mainUI.myMenuBar,
				"Do you want to save after changing ?", "information",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
		switch (op)
		{
		case JOptionPane.YES_OPTION:
			if (myText.fileName == null)
			{
				this.saveAs();
			}
			else
			{
				this.save(myText.fileName);
			}
			break;

		case JOptionPane.NO_OPTION:
			break;
		case JOptionPane.CANCEL_OPTION:
			break;
		}
		return op;

	}

	// open a file
	public boolean openFile()
	{
		JFileChooser jfcOpen = new JFileChooser("userdata");
		jfcOpen.setDialogTitle("Please select the file ...");
		int result = jfcOpen.showOpenDialog(null);
		jfcOpen.setVisible(true);

		if(result != JFileChooser.APPROVE_OPTION)
		{
			return false;
		}
		
		File selectedFile = jfcOpen.getSelectedFile();

		if (selectedFile != null)
		{
			myText.fileName = selectedFile.getAbsolutePath();

			FileReader fr = null;
			BufferedReader br = null;

			try
			{
				myText.setText("");//��ʼ�����

				fr = new FileReader(myText.fileName);
				br = new BufferedReader(fr);

				String string = "";

				while ((string = br.readLine()) != null)
				{
					myText.append(string + "\n");
				}
				String jtaString = myText.getText();
				if (jtaString.length() - 1 >= 0)
				{
					myText.setText(jtaString.substring(0,
							jtaString.length() - 1));
				}
				myText.isChanged = false;
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
			finally
			{
				try
				{
					fr.close();
					br.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		return true;
	}

	// quit the compiler
	public void quit()
	{
		if (myText.isChanged == true)
		{
			int op = this.judgeSaveAs();
			if (op == JOptionPane.CANCEL_OPTION)
			{
				myText.requestFocus(true);
			}
			else
			{
				System.exit(0);
			}
		}
		else
		{
			System.exit(0);
		}
	}

	// ��Ӧ�����¼��Ĳ���������ͨ�� action command��
	public void operateAction(String cmd)
	{
		// create a new notepad
		if (cmd.equals("ctrl-new"))
		{
			int op = 0;
			if (myText.isChanged == true)
			{
				op = this.judgeSaveAs();
			}
			if (op != JOptionPane.CANCEL_OPTION)
			{
				myText.setText("");
				myText.isChanged = false;
				myText.requestFocus(true);
				mainUI.setTitle("null - Ran Compiler");
			}
		}

		// open a file
		else if (cmd.equals("ctrl-open"))
		{
			int op = 0;
			if (myText.isChanged == true)
			{
				op = this.judgeSaveAs();
			}
			if (op != JOptionPane.CANCEL_OPTION)
			{
				if(! this.openFile())
					return;
				myText.requestFocus(true);
			}
			mainUI.setTitle(myText.fileName + " - Ran Compiler");
		}
		
		// ѡ���û��Զ�����﷨�ļ�
		else if (cmd.equals("ctrl-select_grammar_file"))
		{
			JFileChooser jfcSelect = new JFileChooser("userdata");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Grammar File", "grammar");
			jfcSelect.setFileFilter(filter);
			jfcSelect.setDialogTitle("Please select the grammar file ...");
			int result = jfcSelect.showOpenDialog(null);
			jfcSelect.setVisible(true);

			if(result != JFileChooser.APPROVE_OPTION)
			{
				return ;
			}
			
			File selectedFile = jfcSelect.getSelectedFile();

			if (selectedFile != null)
			{
				this.grammarFilePath = selectedFile.getAbsolutePath();
				this.isInitSeman = false;
				this.isSemanticAnalysis = false;
				// ��ʾ��ѡ�����µ��Զ����﷨
				JOptionPane.showMessageDialog(mainUI, "\n��ѡ�����µ��Զ����﷨�����Խ�����Ӧ�ķ����ˡ�\n", 
						grammarFilePath, JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// save the contents to a file
		else if (cmd.equals("ctrl-save"))
		{
			this.ctrlSave();
		}

		// save the contents to a another file
		else if (cmd.equals("ctrl-saveAs"))
		{
			if(! this.saveAs())
				return ;
		}

		// quit the notepad
		else if (cmd.equals("ctrl-quit"))
		{
			this.quit();
		}

		// undo
		else if (cmd.equals("ctrl-undo"))
		{
			if (myText.undoManager.canUndo())
			{
				myText.undoManager.undo();
			}
		}
		// redo
		else if (cmd.equals("ctrl-redo"))
		{
			if (myText.undoManager.canRedo())
			{
				myText.undoManager.redo();
			}
		}
		else if (cmd.equals("ctrl-selectAll"))
		{
			myText.selectAll();
		}
		else if (cmd.equals("ctrl-cut"))
		{
			myText.cut();
		}
		else if (cmd.equals("ctrl-copy"))
		{
			myText.copy();
		}
		else if (cmd.equals("ctrl-paste"))
		{
			myText.paste();
		}
		// delete ��ѡ��
		else if (cmd.equals("ctrl-delete"))
		{
			myText.replaceSelection("");
		}
		else if (cmd.equals("ctrl-find"))
		{
			// TODO find
		}
		else if (cmd.equals("ctrl-findNext"))
		{
			// TODO findNext
		}
		else if (cmd.equals("ctrl-replace"))
		{
			// TODO replace
		}
		// auto line wrap
		else if (cmd.equals("ctrl-lineWrap"))
		{
			myText.setLineWrap(!myText.getLineWrap());
			mainUI.myMenuBar.jcbmiLineWrap
					.setSelected(!mainUI.myMenuBar.jcbmiLineWrap.isSelected());
		}
		// set toolbar visible
		else if (cmd.equals("ctrl-toolbar"))
		{
			mainUI.myToolBar.setVisible(mainUI.myMenuBar.jcbmiToolbar
					.isSelected());
		}
		else if (cmd.equals("ctrl-font"))
		{
			// TODO font
		}
		else if (cmd.equals("ctrl-color"))
		{
			// TODO color
		}
		else if (cmd.equals("ctrl-helpContent"))
		{
			// TODO helpContent
		}
		else if (cmd.equals("ctrl-about"))
		{
			JOptionPane.showMessageDialog(mainUI, "\n����ԭ���ѧ������� v4.5\n"
					+ "���ߣ�Randy\n" + "Email��gchinaran@gmail.com\n", 
					"�������", JOptionPane.INFORMATION_MESSAGE);
		}
		/*********** �������� �ʷ����� ***********/
		else if (cmd.equals("ctrl-lexical_analysis"))
		{
			this.ctrlSave();
			// ִ�дʷ�����
			if (myText.fileName != null)
			{
				// ��ȡ��ʼʱ��
				long startTime = System.currentTimeMillis();

				lexicalAnalysis = new LexicalAnalysis(myText.fileName);
				// ��ʼ �ʷ�����
				lexicalAnalysis.scanRows();
				String showErrorMsg = "> ��ʼ�ʷ����� (" + myText.fileName + ")\n";
				showErrorMsg += lexicalAnalysis.handleErrorMessage();
				if (lexicalAnalysis.errorMessageBeans.size() == 0)
				{
					showErrorMsg += ">\n> �ʷ����� �ɹ���\n";
				}
				else
				{
					showErrorMsg += ">\n> �ʷ����� ʧ�ܡ�\n";
				}
				// ��ȡ����ʱ��
				long endTime = System.currentTimeMillis();
				showErrorMsg += "> ����ʱ��: " + (endTime - startTime) + " (����)\n";
				showErrorMsg += "================    �ʷ����� �׶�    ================";
				mainUI.myMainPanel.errorText.setText(showErrorMsg);

				// ����Ԫ�� д���ļ���"data/dual.lex"��
				this.saveFile("data/dual.lex", lexicalAnalysis.getDualExpression());
						
				// ����ʷ������ɹ������������ʾ�ʷ��������
				if (lexicalAnalysis.errorMessageBeans.size() == 0)
				{
					this.operateAction("ctrl-dual-expression_table");
					mainUI.myMainPanel.jstRight.setDividerLocation(0.5);
				}
				else
				{
					mainUI.myMainPanel.jstRight.setDividerLocation(0.9);
				}
			}
		}
		// ��ʾ�ʷ������������Ԫ���� ctrl-dual-expression_table
		else if (cmd.equals("ctrl-dual-expression_table"))
		{
			if (isLexicalAnalysis())
			{
				// ͨ��Label��Ƕƴ��HTML�ķ�ʽ��û��ʹ��java��table���о��ò�����
				String dualTable = "<html><body><center>";
				dualTable += "<h2>Dual-expression Table</h2><br>";
				String[] dualExpression = lexicalAnalysis.getDualExpression().split(" ");
						
				int tdNum = 10;//��ʼ������
				int trNum = dualExpression.length / tdNum;//����������
				if (dualExpression.length % tdNum != 0)
				{
					trNum++;
				}
				dualTable += "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
				for (int i = 0; i < trNum; i++)
				{
					dualTable += "<tr>";
					for (int j = 0; (j < tdNum) && (i * tdNum + j < dualExpression.length); j++)
					{
						dualTable += "<td> " + dualExpression[i * tdNum + j] + "</td>";
					}
					dualTable += "</tr>";
				}
				dualTable += "</table></center></body></html>";
				mainUI.myMainPanel.jstRight.setDividerLocation(0.2);
				mainUI.myMainPanel.jlMsg.setText(dualTable);
			}
		}
		// ��ʾxml�еĹؼ��ֱ� ctrl-show_Keywords_table
		else if (cmd.equals("ctrl-show_Keywords_table"))
		{
			HandleXMLFile handleXMLFile = new HandleXMLFile(new File("data/Keywords.xml"));
			String keywordsTable = "<html><body><center>";
			keywordsTable += "<h2>KeyWords Table</h2><br>";
			String[] subElements = { "id", "word" };
			
			keywordsTable += handleXMLFile.getElementTable("kw", subElements);
			keywordsTable += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.5);
			mainUI.myMainPanel.jlMsg.setText(keywordsTable);
		}
		// ��ʾxml�е���ϸ������Ϣ�� ctrl-show_error_messages_table
		else if (cmd.equals("ctrl-show_error_messages_table"))
		{
			HandleXMLFile handleXMLFile = new HandleXMLFile(new File(
					"data/ErrorMessage.xml"));
			String errorMsgTable = "<html><body><center>";
			errorMsgTable += "<h2>Error Messages Table</h2><br>";
			String[] subElements =
			{ "code", "error_info" };
			errorMsgTable += handleXMLFile
					.getElementTable("error", subElements);
			errorMsgTable += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.2);
			mainUI.myMainPanel.jlMsg.setText(errorMsgTable);
		}
		// ��ʾ��ʶ���� ctrl-show_identifier_table ctrl-show_constant_table"
		else if (cmd.equals("ctrl-show_identifier_table"))
		{
			if (isLexicalAnalysis())
			{
				String identifierTable = "<html><body><center>";
				identifierTable += "<h2>Identifier Table</h2><br>";
				identifierTable += "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
				identifierTable += "<tr><th> index </th><th> name </th><th> type </th><th> address </th></tr>";
				int len = lexicalAnalysis.identifierList.size();
				for (int i = 0; i < len; i++)
				{
					IdentifierBean identifierBean = lexicalAnalysis.identifierList
							.get(i);
					identifierTable += "<tr>";
					identifierTable += "<td> &nbsp;" + i + " &nbsp; </td>";
					identifierTable += "<td> &nbsp;" + identifierBean.getName()
							+ " &nbsp; </td>";
					identifierTable += "<td> &nbsp;" + identifierBean.getType()
							+ " &nbsp; </td>";
					identifierTable += "<td> &nbsp;"
							+ identifierBean.getAddress() + " &nbsp; </td>";
					identifierTable += "</tr>";
				}
				identifierTable += "</table></center></body></html>";
				mainUI.myMainPanel.jstRight.setDividerLocation(0.4);
				mainUI.myMainPanel.jlMsg.setText(identifierTable);
			}
		}
		// ��ʾ���������֣��ַ����ַ����� ctrl-show_constant_table
		else if (cmd.equals("ctrl-show_constant_table"))
		{
			if (isLexicalAnalysis())
			{
				String constantTable = "<html><body>";

				// ����3��div��ʢ��3��������
				constantTable += "<div style=\"float: left; margin-left: 25\">";
				constantTable += "<h2>Number List</h2><br>";
				constantTable += this.constantTable(lexicalAnalysis.numberList);
				constantTable += "</div>";

				constantTable += "<div style=\"float: left; margin-left: 25\">";
				constantTable += "<h2>String List</h2><br>";
				constantTable += this.constantTable(lexicalAnalysis.stringList);
				constantTable += "</div>";

				constantTable += "<div style=\"float: left; margin-left: 25\">";
				constantTable += "<h2>Char List</h2><br>";
				constantTable += this.constantTable(lexicalAnalysis.charList);
				constantTable += "</div>";
				
				constantTable += "</body></html>";
				mainUI.myMainPanel.jstRight.setDividerLocation(0.5);
				mainUI.myMainPanel.jlMsg.setText(constantTable);
			}
		}
		// �Ե�ǰ�ı������ķ�������ͨ��LL1Ԥ�������
		else if (cmd.equals("ctrl-syntax_analysis"))
		{
			// ��ȡ��ʼ���е�ʱ��
			long startTime = System.currentTimeMillis();

			// �ȱ���
			this.ctrlSave();
			
			// -TODO �����Ż�������ÿ�ζ���ͷ����
			if (!isInitSeman)
			{
				// ���ս�����ͷ��ս�������
				Terminal.terminals.clear();
				Nonterminal.nonterminals.clear();

				// ���ôʷ������������ķ�
				lexicalAnalysis = new LexicalAnalysis(grammarFilePath);
				grammarParserByLL1 = new GrammarParserByLL1(
						lexicalAnalysis);
				// 1. LL1�ķ�������1��: ��ʼ������ʽ productions
				if (!grammarParserByLL1.FindProductions())
				{
					this.showGrammarParserResult(lexicalAnalysis, startTime);
					return;
				}
				// 2. LL1�ķ�������2��: ����first����FirstSets)
				if (!grammarParserByLL1.ComputeFirstSets())
				{
					this.showGrammarParserResult(lexicalAnalysis, startTime);
					return;
				}
				// 3. LL1�ķ�������3��: ����follow����followSets)
				if (!grammarParserByLL1.ComputeFollowSets())
				{
					this.showGrammarParserResult(lexicalAnalysis, startTime);
					return;
				}
				// 4. LL1�ķ�������4��: ����LL1Ԥ�������
				if (!grammarParserByLL1.buildLL1Table())
				{
					this.showGrammarParserResult(lexicalAnalysis, startTime);
					return;
				}
				isInitSeman = true;
			}
			// 5. LL1�ķ�������5��: ��������Ԥ���﷨����
			if (!grammarParserByLL1.LL1Parser(myText.fileName))
			{
				this.showGrammarParserResult(lexicalAnalysis, startTime);
				return;
			}
			this.showGrammarParserResult(lexicalAnalysis, startTime);
			// ��ʼ�� LL1TableforShow��productionArrayforShow��Ϊ����ʾLL1Ԥ���
			GrammarParserByLL1.LL1TableforShow = grammarParserByLL1
					.getLL1Table();
			GrammarParserByLL1.productionArrayforShow = grammarParserByLL1
					.getProductionArray();
			
			showLL1ParseLog(grammarParserByLL1.getlL1ParseLog(), mainUI);
		}
		// �Ե�ǰ�ı��������������ͨ�������ķ��������
		else if (cmd.equals("ctrl-semantic_analysis"))
		{
			if(this.grammarFilePath != "data/gra.grammar")
			{
				JOptionPane.showMessageDialog(mainUI, "\n�������ֻ������[data/gra.grammar]�ļ���\n" +
						"ϵͳ�����Ĭ���ķ���\n", 
						"���棺�ķ���ƥ��", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// ��ȡ��ʼ���е�ʱ��
			long startTime = System.currentTimeMillis();
			
			// �ȱ���
			this.ctrlSave();
			
			if (!isInitSeman)
			{
				// ���ս�����ͷ��ս�������
				Terminal.terminals.clear();
				Nonterminal.nonterminals.clear();
				
				// ���ôʷ������������ķ�
				lexicalAnalysis = new LexicalAnalysis(grammarFilePath);
				grammarParserByLL1 = new GrammarParserByLL1(
						lexicalAnalysis);
				// 1. LL1�ķ�������1��: ��ʼ������ʽ productions
				if (!grammarParserByLL1.FindProductions())
				{
					this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
					return;
				}
				// 2. LL1�ķ�������2��: ����first����FirstSets)
				if (!grammarParserByLL1.ComputeFirstSets())
				{
					this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
					return;
				}
				// 3. LL1�ķ�������3��: ����follow����followSets)
				if (!grammarParserByLL1.ComputeFollowSets())
				{
					this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
					return;
				}
				// 4. LL1�ķ�������4��: ����LL1Ԥ�������
				if (!grammarParserByLL1.buildLL1Table())
				{
					this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
					return;
				}
				isInitSeman = true;
			}
			// 5. LL1�ķ�������5��: �����ķ��������
			if (!grammarParserByLL1.semanticAnalysis(myText.fileName))
			{
				isSemanticAnalysis = true;
				this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
				return;
			}
			this.showSemanticAnalysisResult(lexicalAnalysis, startTime, grammarParserByLL1);
			// ��ʼ�� LL1TableforShow��productionArrayforShow��Ϊ����ʾLL1Ԥ���
			GrammarParserByLL1.LL1TableforShow = grammarParserByLL1
					.getLL1Table();
			GrammarParserByLL1.productionArrayforShow = grammarParserByLL1
					.getProductionArray();
			
			showQuaternionType(grammarParserByLL1.getQuaternions(), mainUI);
		}
		// ��ʾLL1Ԥ����������ɵ�LL1Ԥ��������棬�Ϳα���ʾ��һ����
		else if (cmd.equals("ctrl-show_ll(1)_table_normal"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			int nullTerminalIndex = Terminal.IndexOf("null");
			String LL1Table = "<html><body><center>";
			LL1Table += "<h2>LL1 Table (normal)</h2><br>";
			LL1Table += "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
			LL1Table += "<tr>";
			LL1Table += "<th width=\"50px\">  </th>";
			int terminalsLength = Terminal.terminals.size();
			for (int i = 0; i < terminalsLength; i++)
			{
				if (i != nullTerminalIndex)
				{
					LL1Table += "<th width=\"50px\"> " + Terminal.get(i)
							+ " </th>";
				}
			}
			LL1Table += "</tr>";
			int[][] LL1TableforShow = GrammarParserByLL1.LL1TableforShow;
			for (int i = 0; i < LL1TableforShow.length; i++)
			{
				String nonTerminalString = Nonterminal.get(i);
				LL1Table += "<tr>";
				LL1Table += "<td align=\"center\"><b> " + nonTerminalString
						+ " </b></td>";
				nonTerminalString += "��";
				for (int j = 0; j < terminalsLength; j++)
				{
					if (j != nullTerminalIndex)
					{
						LL1Table += "<td align=\"center\"> ";
						// get ����ʽ
						int proIndex = LL1TableforShow[i][j];
						if (proIndex == -2 || proIndex == -1)
						{
							LL1Table += "";
						}
						else
						{
							Object[] production = GrammarParserByLL1.productionArrayforShow[proIndex];
							String proStr = nonTerminalString;
							for (int k = 0; k < production.length; k++)
							{
								if (GrammarParserByLL1
										.isTerminalObject(production[k]))
								{
									Terminal terminal = (Terminal) production[k];
									proStr += Terminal.get(terminal.index)
											+ " ";
								}
								else
								{
									Nonterminal nonterminal = (Nonterminal) production[k];
									proStr += Nonterminal
											.get(nonterminal.index) + " ";
								}
							}
							LL1Table += proStr;
						}
						LL1Table += " </td>";
					}
				}
				LL1Table += "</tr>";
			}
			LL1Table += "</table>";
			LL1Table += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.2);
			mainUI.myMainPanel.jlMsg.setText(LL1Table);
		}
		// ��ʾLL1Ԥ����������ɵ�LL1Ԥ�������棬����������ʹ�õ�LL1��
		else if (cmd.equals("ctrl-show_ll(1)_table_program"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			int nullTerminalIndex = Terminal.IndexOf("null");
			String LL1Table = "<html><body><center>";
			LL1Table += "<h2>LL1 Table (program)</h2><br>";
			LL1Table += "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
			LL1Table += "<tr>";
			LL1Table += "<th width=\"50px\">  </th>";
			int terminalsLength = Terminal.terminals.size();
			for (int i = 0; i < terminalsLength; i++)
			{
				if (i != nullTerminalIndex)
				{
					LL1Table += "<th width=\"50px\"> " + Terminal.get(i)
							+ " </th>";
				}
			}
			LL1Table += "</tr>";
			int[][] LL1TableforShow = GrammarParserByLL1.LL1TableforShow;
			for (int i = 0; i < LL1TableforShow.length; i++)
			{
				String nonTerminalString = Nonterminal.get(i);
				LL1Table += "<tr>";
				LL1Table += "<td align=\"center\"><b> " + nonTerminalString
						+ " </b></td>";
				nonTerminalString += "��";
				for (int j = 0; j < terminalsLength; j++)
				{
					if (j != nullTerminalIndex)
					{
						LL1Table += "<td align=\"center\"> ";
						// get ����ʽ
						int proIndex = LL1TableforShow[i][j];
						if (proIndex == -2)
						{
							LL1Table += "-2";
						}
						else if (proIndex == -1)
						{
							LL1Table += "-1";
						}
						else
						{
							Object[] production = GrammarParserByLL1.productionArrayforShow[proIndex];
							String proStr = nonTerminalString;
							for (int k = 0; k < production.length; k++)
							{
								if (GrammarParserByLL1
										.isTerminalObject(production[k]))
								{
									Terminal terminal = (Terminal) production[k];
									proStr += Terminal.get(terminal.index)
											+ " ";
								}
								else
								{
									Nonterminal nonterminal = (Nonterminal) production[k];
									proStr += Nonterminal
											.get(nonterminal.index) + " ";
								}
							}
							LL1Table += proStr;
						}
						LL1Table += " </td>";
					}
				}
				LL1Table += "</tr>";
			}
			LL1Table += "</table>";
			LL1Table += "<br>ע��������ʹ�� ��-2����������ʶ����-1���������ָ���ʶ��";
			LL1Table += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.2);
			mainUI.myMainPanel.jlMsg.setText(LL1Table);
		}
		// ��ʾ���������Ĳ���ʽ
		else if (cmd.equals("ctrl-show_productions"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			String showProductions = "<html><body><center>";

			// ����1��div��ʢ��productions
			showProductions += "<div style=\"float: left; margin-left: 30\">";
			showProductions += "<h2>Used Productions</h2>";
			
			showProductions += "<table style=\"float: left; margin-left: 10;font-size: 14; \">";
			ArrayList<Production> productions = grammarParserByLL1.getProductions();
			for (int i = 0; i < productions.size(); i++) 
			{
				showProductions += productions.get(i).showProduction_html();
			}
			showProductions += "</table>";
			showProductions += "</div>";

			showProductions += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.4);
			mainUI.myMainPanel.jlMsg.setText(showProductions);
		}
		// ��ʾFirst�� ���� �������
		else if (cmd.equals("ctrl-show_first_set"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			String showFirstSet = "<html><body><center>";
			
			// ����2��div��ʢ�� firstset �� �������
			showFirstSet += "<div style=\"float: left; margin-left: 30\">";
			showFirstSet += "<h2>First Set</h2>";
			
			showFirstSet += "<table style=\"float: left; margin-left: 10;font-size: 14; \">";
			HashSet<Integer>[] firstSets = grammarParserByLL1.getFirstSets();
			for (int i = 0; i < firstSets.length; i++) 
			{
				showFirstSet += "<tr>";
				showFirstSet += "<td>First(" + Nonterminal.get(i) + ")</td>";
				showFirstSet += "<td> = </td>";
				showFirstSet += "<td>{ ";
				
				Iterator<Integer> iterator = firstSets[i].iterator();
				while (iterator.hasNext())
				{
					showFirstSet += Terminal.get(iterator.next()) + " ";
				}
				
				showFirstSet += "}</td>";
				showFirstSet += "</tr>";
			}
			showFirstSet += "</table>";
			showFirstSet += "</div>";
			
			/*showFirstSet += "<div style=\"float: left; margin-left: 25; \">";
			showFirstSet += "<h2>The Generate Process</h2>";
			
			// ����First���Ĺ���
			showFirstSet += "����һ��������ڲ���ʽX->a������a��Vt�����a���뵽FIRST��X���У�<br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;������X->��,�򽫦�Ҳ���뵽FIRST��X���С�<br><br>";
			showFirstSet += "�����������X->Y������Y��Vn����FIRST��Y���е����зǦ��ս�����뵽FITST��X���У�<br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;����X->Y1Y2��Yk����Y1��Y2���Ƿ��ս������Y1��Yi-1�ĺ�ѡʽ���ЦŴ��ڣ�<br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;���FIRST��Yj����j=1,2����i�������зǦ��ս�����뵽FIRST��X���У�<br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;�ر��ǵ�Y1��Yk�����ЦŲ���ʽʱ��Ӧ�Ѧ�Ҳ����FIRST��X���С�<br><br>";
			// ��ʾ������̡�
			showFirstSet += "<hr>";
			showFirstSet += "<table>";
			ArrayList<String> firstSetLogs = grammarParserByLL1.getFirstSetLog();
			for (String log : firstSetLogs) {
				showFirstSet += "<tr><td>" + log + "</td></tr>";
			}
			showFirstSet += "</table>";
			
			showFirstSet += "</div>";*/
			
			showFirstSet += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.4);
			mainUI.myMainPanel.jlMsg.setText(showFirstSet);
		}
		// ��ʾFollow�� ���� �������
		else if (cmd.equals("ctrl-show_follow_set"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			String showFollowSet = "<html><body><center>";
			
			// ����2��div��ʢ��followset �� �������
			showFollowSet += "<div style=\"float: left; margin-left: 30\">";
			showFollowSet += "<h2>Follow Set</h2>";
			
			showFollowSet += "<table style=\"float: left; margin-left: 10;font-size: 14; \">";
			HashSet<Integer>[] followSets = grammarParserByLL1.getFollowSets();
			for (int i = 0; i < followSets.length; i++) 
			{
				showFollowSet += "<tr>";
				showFollowSet += "<td>First(" + Nonterminal.get(i) + ")</td>";
				showFollowSet += "<td> = </td>";
				showFollowSet += "<td>{ ";

				Iterator<Integer> iterator = followSets[i].iterator();
				while (iterator.hasNext())
				{
					showFollowSet += Terminal.get(iterator.next()) + " ";
				}
				
				showFollowSet += "}</td>";
				showFollowSet += "</tr>";
			}
			showFollowSet += "</table>";
			showFollowSet += "</div>";

			/*showFollowSet += "<div style=\"float: left; margin-left: 25\">";
			showFollowSet += "<h2>The Generate Process</h2><br>";
			
			
			
			showFollowSet += "</div>";*/

			showFollowSet += "</center></body></html>";
			mainUI.myMainPanel.jstRight.setDividerLocation(0.4);
			mainUI.myMainPanel.jlMsg.setText(showFollowSet);
		}
		// ��ʾLL��1���﷨��������
		else if (cmd.equals("ctrl-show_parse_process"))
		{
			if (! isSyntaxAnalysis())
			{
				return;
			}
			showLL1ParseLog(grammarParserByLL1.getlL1ParseLog(), mainUI);
		}
		// ��ʾ���������������Ԫʽ
		else if (cmd.equals("ctrl-show_quaternion_type"))
		{
			if (! isSemanticAnalysis)
			{
				JOptionPane.showMessageDialog(null,
						"Please do the Semantic Analysis Firstly !", "alert",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			showQuaternionType(grammarParserByLL1.getQuaternions(), mainUI);
		}
	}

	public void showGrammarParserResult(LexicalAnalysis lexicalAnalysis,
			long startTime)
	{
		String showResult = "> ��ʼ�﷨���� (" + myText.fileName + ")\n";
		showResult += lexicalAnalysis.handleErrorMessage();
		if (lexicalAnalysis.errorMessageBeans.size() == 0)
		{
			showResult += ">\n> �﷨���� �ɹ���\n";
		}
		else
		{
			showResult += ">\n> �﷨���� ʧ�ܡ�\n";
		}
		// ��ȡ����ʱ��
		long endTime = System.currentTimeMillis();
		showResult += "> ����ʱ��: " + (endTime - startTime) + " (����)\n";
		showResult += "================    �ķ����� �׶�    ================";
		mainUI.myMainPanel.errorText.setText(showResult);
	}
	
	public void showSemanticAnalysisResult(LexicalAnalysis lexicalAnalysis,
			long startTime, GrammarParserByLL1 grammarParserByLL1)
	{
		String showResult = "> ��ʼ������� (" + myText.fileName + ")\n";
		showResult += lexicalAnalysis.handleErrorMessage();
		if (lexicalAnalysis.errorMessageBeans.size() == 0)
		{
			showResult += ">\n> ������� �ɹ���\n";
		}
		else
		{
			showResult += ">\n> ������� ʧ�ܡ�\n";
		}
		// ��ȡ����ʱ��
		long endTime = System.currentTimeMillis();
		showResult += "> ����ʱ��: " + (endTime - startTime) + " (����)\n";
		mainUI.myMainPanel.errorText.setText(showResult);
	}

	// �ж��Ƿ� �����˴ʷ�����
	public boolean isLexicalAnalysis()
	{
		if (lexicalAnalysis == null)
		{
			JOptionPane.showMessageDialog(null,
					"Please do the Lexical Analysis Firstly !", "alert",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	// �ж��Ƿ� �������﷨����
	public boolean isSyntaxAnalysis()
	{
		//if (GrammarParserByLL1.LL1TableforShow == null)
		if (isInitSeman == false)
		{
			JOptionPane.showMessageDialog(null,
					"Please do the Syntax Analysis Firstly !", "alert",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	//
	public String constantTable(ArrayList constantList)
	{
		String constantTable = "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
		constantTable += "<tr><th> index </th><th> value </th></tr>";
		int len = constantList.size();
		for (int i = 0; i < len; i++)
		{
			constantTable += "<tr>";
			constantTable += "<td> &nbsp;" + i + " &nbsp; </td>";
			constantTable += "<td> &nbsp;" + constantList.get(i)
					+ " &nbsp; </td>";
			constantTable += "</tr>";
		}
		constantTable += "</table>";
		return constantTable;
	}
	
	// ��ʾLL1�﷨��������
	public String showLL1ParseLog(ArrayList<LL1ProcessLog> LL1ParseLog, MainUI mainUI)
	{
		String out = "<html><body><center>";
		out += "<h2>LL(1) Parse Process</h2>";
		out += "<table border=1 style=\"font-size: 16;margin-left:25;border-color: lightblue;background-color:#CCE8CF\">";
		out += LL1ProcessLog.getLL1Log_tablehead();
		int len = LL1ParseLog.size();
		for (int i = 0; i < len; i++)
		{
			out += LL1ParseLog.get(i).getLL1Log_html();
		}
		out += "</table>";
		out += "</center></body></html>";
		
		mainUI.myMainPanel.jstRight.setDividerLocation(0.3);
		mainUI.myMainPanel.jlMsg.setText(out);
		return out;
	}
	
	// ��ʾ���������������Ԫʽ
	public String showQuaternionType(ArrayList<String> quaternions, MainUI mainUI)
	{
		String out = "<html><body><center>";
		out += "<h2>&nbsp;&nbsp;Quaternion Type����Ԫʽ��</h2><br>";
		out += "<table style=\"font-size: 16;margin-left:25;\">";

		int len = quaternions.size();
		for (int i = 0; i < len; i++)
		{
			out += "<tr><td>" + quaternions.get(i) + "</td></tr>";
		}
		
		out += "</table>";
		out += "</center></body></html>";
		
		mainUI.myMainPanel.jstRight.setDividerLocation(0.5);
		mainUI.myMainPanel.jlMsg.setText(out);
		return out;
	}
}
