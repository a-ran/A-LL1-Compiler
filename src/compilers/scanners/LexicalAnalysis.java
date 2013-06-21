/**
 * @author Randy
 * @date 2013-05-25
 * @function Lexical Analysis(�ʷ�����)
 */
package compilers.scanners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compilers.javabean.ErrorMessageBean;
import compilers.javabean.IdentifierBean;
import compilers.tool.HandleXMLFile;

public class LexicalAnalysis
{
	String fileName = null;

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	String rowStr = "";
	public ArrayList<String> numberList = new ArrayList<String>();
	public ArrayList<String> stringList = new ArrayList<String>();
	public ArrayList<Character> charList = new ArrayList<Character>();
	public ArrayList<IdentifierBean> identifierList = new ArrayList<IdentifierBean>();
	public ArrayList<ErrorMessageBean> errorMessageBeans = new ArrayList<ErrorMessageBean>();
	boolean foundMultiComments = false; // ����ע��

	// ֻ��ű�ʶ�������ڼ�����ʶ���Ƿ��Ѵ��ڣ��������Ч�ʣ���ռ����һ�����ڴ�ռ䣨�Կռ任ʱ�䣩
	public HashMap<String, Integer> identifierHashMap = new HashMap<String, Integer>();
	public HashMap<String, Integer> keywordsHashMap = new HashMap<String, Integer>();
	public String dualExpression = "";

	// Ϊ�����µ���ʱ����������(_Tn)
	int newTempIndex = 0;

	public String getDualExpression()
	{
		return dualExpression;
	}

	// ��¼��ǰ����
	int countRows = 0;

	// �ļ���
	FileReader fr = null;
	BufferedReader br = null;

	// ������ʽ ƥ��
	Pattern pattern = null;
	Matcher matcher = null;

	// ������ʽ
	static String regexFirstWord = "[a-zA-Z\u4e00-\u9fa5_]"; //��������
	static String regexRestWord = "[a-zA-Z0-9\u4e00-\u9fa5_]*";
	static String regexOperator = "[+\\-*/%=<>!��^&|.,��:��;��()����\\[\\]����{}]";
	static String regexContainIllegalWord = "[^+\\-*/%=<>!��^&|.,��:��;��()����\\[\\]����{} \t]*";
	static String regexNumber = "[0-9]";
	static String regexRestNumber = "[0-9]*(\\.[0-9]+)|([0-9]*)";
	static String regexContainIllegalNumber = "[\\w\u4e00-\u9fa5\\.]*";
	static String regexWord = "[a-zA-Z\u4e00-\u9fa5]";
	static String regexSpecialChar = "(([bfnrt\'\"\\\\])|(u[0-9A-Fa-f]{4}))['����]";
	static String regexNormalChar = ".['����]";
	static String regexNormalString = "(.*[^\\\\][\"����])|([\"����])";
	static String regexSpecialString = "[\\\\](([bfnrt\'\"\\\\])|(u[0-9A-Fa-f]{4}))";
	static String regexContainIllegalString = "([\\\\](([^bfnrt\'\"\\\\])|(^(u([0-9A-Fa-f]{4})))))+[\"]";

	/**
	 * Constructor
	 */
	public LexicalAnalysis(String fileName)
	{
		this.fileName = fileName;
		keywordsHashMap = new HandleXMLFile(new File("data/Keywords.xml"))
				.getKeywordsHashMap();
	}

	// ɨ��ÿһ��(every string line)
	public void scanRows()
	{
		try
		{
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			while ((rowStr = br.readLine()) != null)
			{
				countRows++;
				// ����ÿһ�е��ַ���
				this.handleRow();
				// TODO ????
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
		finally
		{
			try
			{
				if (br != null)
				{
					fr.close();
				}
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	// ����ǰ�е��ַ����������ַ�ɨ�裩
	public void handleRow()
	{
		char singleChar = ' ';

		for (int i = 0; i < rowStr.length(); i++)
		{
			singleChar = rowStr.charAt(i);
			// �ж������ �ո� �� �Ʊ��
			if (' ' == singleChar || '\t' == singleChar)
			{
				continue;
			}
			// ����� Ӣ����ĸ || �����ַ� || '_'
			else if (Pattern.matches(regexFirstWord, String.valueOf(singleChar)))
			{
				i = this.handleFirstWord(i, singleChar);
			}
			// ����� �����������������
			else if (Pattern.matches(regexOperator, String.valueOf(singleChar)))
			{
				i = this.handleOperator(i, singleChar);
			}
			// ����� ���ֳ���
			else if (Pattern.matches(regexNumber, String.valueOf(singleChar)))
			{
				// ����Ϊ ����
				i = this.handleNumber(i, singleChar);
			}
			// ����� �ַ�����
			else if ('\'' == singleChar || '��' == singleChar
					|| '��' == singleChar)
			{
				i = this.handleChar(i, singleChar);
			}
			// ����� �ַ�������
			else if ('"' == singleChar || '��' == singleChar)
			{
				i = this.handleString(i, singleChar);
			}
			// �Ƿ��ַ�
			else
			{
				// ����
				errorMessageBeans.add(new ErrorMessageBean("A0001", singleChar
						+ "", countRows));
			}
		}// for loop end
	}

	// ��������ĸ�������Ǳ�ʶ����ؼ��ֵ����
	public int handleFirstWord(int i, char singleChar)
	{
		String singleWord = null;
		// ����Ϊ ��ʶ�� �� �ؼ���
		pattern = Pattern.compile(regexRestWord);
		matcher = pattern.matcher(rowStr);
		matcher.find(i + 1);
		// set i
		int mayCorrect_i = matcher.end() - 1;
		singleWord = String.valueOf(singleChar) + matcher.group();
		// ��Ҫ�ж�Ϊʲôƥ������ģ��Ƿ�Ϊ�Ƿ��ַ�

		pattern = Pattern.compile(regexContainIllegalWord);
		matcher = pattern.matcher(rowStr);
		matcher.find(i + 1);
		int mayWrong_i = matcher.end() - 1;
		if (mayWrong_i != mayCorrect_i)
		{
			// �������ڷǷ��ַ�
			i = mayWrong_i;
			String errorStr = String.valueOf(singleChar) + matcher.group();
			errorMessageBeans.add(new ErrorMessageBean("A0001", errorStr,
					countRows));
		}
		else
		{
			// ��ȷƥ�䣬�����ж� �ؼ��֣����Ǳ�ʶ��
			i = mayCorrect_i;
			this.handleSingleWord(singleWord);
		}
		return i;
	}

	// ���������ʣ���ʶ�� �� �ؼ��֣�
	public void handleSingleWord(String singleWord)
	{
		// ���Ȳ����Ƿ�δ�ؼ��֣���������ʶ����
		// �ж��Ƿ�Ϊ�ؼ���
		if (keywordsHashMap.containsKey(singleWord))
		{
			dualExpression += "(" + singleWord + ",_) ";
		}
		// ����Ϊ��ʶ�������뵽��ʶ����
		else
		{
			int index = isExistidentifier(singleWord);
			// ��ʶ�������ڣ������ʶ����
			if (-1 == index)
			{
				identifierList.add(new IdentifierBean(singleWord, null, 0));
				index = identifierList.size() - 1;
				identifierHashMap.put(singleWord, index);
			}
			// ��Ӷ�Ԫ��
			dualExpression += "(ID," + index + ") ";
		}
	}

	// ��������ĸΪ���ֵ����
	public int handleNumber(int i, char singleChar)
	{
		String singleNumber = null;
		// ����Ϊ ��ʶ�� �� �ؼ���
		pattern = Pattern.compile(regexRestNumber);
		matcher = pattern.matcher(rowStr);
		matcher.find(i + 1);
		// set i
		int mayCorrect_i = matcher.end() - 1;
		singleNumber = String.valueOf(singleChar) + matcher.group();

		// ��Ҫ�ж�Ϊʲôƥ������ģ��Ƿ�Ϊ�Ƿ��ַ�
		pattern = Pattern.compile(regexContainIllegalNumber);
		matcher = pattern.matcher(rowStr);
		matcher.find(i + 1);
		int mayWrong_i = matcher.end() - 1;
		// �������󣬱���
		if (mayWrong_i != mayCorrect_i)
		{
			i = mayWrong_i;
			String errorStr = String.valueOf(singleChar) + matcher.group();
			// �жϴ�������
			if (errorStr.contains("."))
			{
				// ���ݴ��� // ���о���һЩ���⣬ eg: 6a.name, ��ʲô��Ҳ���ԣ������Ǵ��
				errorMessageBeans.add(new ErrorMessageBean("A0002", errorStr,
						countRows));
			}
			else
			{
				// ��ʶ�����������ֿ�ͷ
				errorMessageBeans.add(new ErrorMessageBean("A0005", errorStr,
						countRows));
			}
		}
		// ��ȷƥ�䣬�ѳ������뵽���ֱ���
		// �����뵽��Ԫ����
		else
		{
			i = mayCorrect_i;
			int index = isExistString(numberList, singleNumber);
			// ���ֳ��������ڣ��������ֱ�
			if (-1 == index)
			{
				numberList.add(singleNumber);
				index = numberList.size() - 1;
			}
			// ��Ӷ�Ԫ��
			dualExpression += "(NUM," + index + ") ";
		}
		return i;
	}

	// ��������ĸΪ �� || ' �����(�ַ�������)
	public int handleChar(int i, char singleChar)
	{
		// ���Ϊ�����ַ�
		if (isNextCharExistAndEqual(i, '\\'))
		{
			boolean isError = false;
			char matchChar = ' ';
			pattern = Pattern.compile(regexSpecialChar);
			matcher = pattern.matcher(rowStr);
			if (rowStr.substring(i + 2, i + 4).equals("\'\'"))
			{
				i = i + 3;
				matchChar = '\'';
			}
			// �ַ�����
			else if (rowStr.substring(i + 2, i + 4).equals("\\"))
			{
				isError = true;
				String errorStr = rowStr.substring(i);
				i = rowStr.length() - 1;
				errorMessageBeans.add(new ErrorMessageBean("A0006", errorStr,
						countRows));
			}
			else if (rowStr.substring(i + 2, i + 4).equals("\\\'"))
			{
				i = i + 3;
				matchChar = '\\';
			}
			else if (matcher.find(i + 1))
			{
				String temp = matcher.group();
				matchChar = handleSpecialChar(temp.substring(0,
						temp.length() - 1));
				i = matcher.end() - 1;
			}
			else
			{
				// �ַ�����
				isError = true;
				String errorStr = rowStr.substring(i);
				i = rowStr.length() - 1;
				errorMessageBeans.add(new ErrorMessageBean("A0006", errorStr,
						countRows));
			}
			if (!isError)
			{
				int index = isExistChar(matchChar);
				// �ַ����������ڣ������ַ���
				if (-1 == index)
				{
					charList.add(matchChar);
					index = charList.size();
				}
				// ��Ӷ�Ԫ��
				dualExpression += "(CHAR," + index + ") ";
			}
		}
		// ���Ϊ��ͨ�ַ�
		else
		{
			char matchChar = ' ';
			pattern = Pattern.compile(regexNormalChar);
			matcher = pattern.matcher(rowStr);
			if (matcher.find(i + 1))
			{
				matchChar = rowStr.charAt(i + 1);
				i = matcher.end() - 1;
				int index = isExistChar(matchChar);
				// �ַ����������ڣ������ַ���
				if (-1 == index)
				{
					charList.add(matchChar);
					index = charList.size() - 1;
				}
				// ��Ӷ�Ԫ��
				dualExpression += "(CHAR," + index + ") ";
			}
			else
			{
				// �ַ�����
				String errorStr = rowStr.substring(i);
				i = rowStr.length() - 1;
				errorMessageBeans.add(new ErrorMessageBean("A0007", errorStr,
						countRows));
			}
		}
		return i;
	}

	// ���������ַ� ([b,f,n,r,t,',\",\\\\])
	public char handleSpecialChar(String specialStr)
	{
		switch (specialStr.charAt(0))
		{
		case 'b':
			return '\b';
		case 'f':
			return '\f';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 't':
			return '\t';
		case '\'':
			return '\'';
		case '\"':
			return '\"';
		case '\\':
			return '\\';
		default:
			int t = Integer.valueOf(specialStr.substring(1), 16);
			return (char) t;
		}
	}

	// ת�����������ַ����ַ�������
	public String convertSpecialString(String cs)
	{
		String convertedStr = cs;
		String temp = null;
		pattern = Pattern.compile(regexSpecialString);
		matcher = pattern.matcher(cs);
		while (matcher.find())
		{
			temp = matcher.group();
			convertedStr = convertedStr.replace(temp,
					handleSpecialChar(temp.substring(1)) + "");
		}
		return convertedStr;
	}

	// ��������ĸΪ " || �� �����(�ַ�������)
	public int handleString(int i, char singleChar)
	{
		pattern = Pattern.compile(regexNormalString);
		matcher = pattern.matcher(rowStr);
		// ƥ�䵽�� ������
		if (matcher.find(i + 1))
		{
			boolean isError = false;
			String correctString = "";

			int mayCorrect_i = matcher.end() - 1;
			String mayCorrectString = matcher.group().substring(0,
					matcher.group().length() - 1);

			// ��Ȼ�ҵ��ַ���������������Ȼ��Ҫ�жϷ�б��(\)������
			pattern = Pattern.compile("\\\\");
			matcher = pattern.matcher(mayCorrectString);
			if (matcher.find(0))
			{
				// �Ƿ�ƥ�������ַ�
				pattern = Pattern.compile(regexSpecialString);
				matcher = pattern.matcher(mayCorrectString);
				if (matcher.find(0))
				{
					correctString = convertSpecialString(mayCorrectString);
				}
				else
				{
					// �������������ַ�
					isError = true;
					String errorStr = rowStr.substring(i);
					i = rowStr.length() - 1;
					errorMessageBeans.add(new ErrorMessageBean("A0008",
							errorStr, countRows));
				}
			}
			// ����ǿմ������� String s = ""; ����һ���մ�
			else if (mayCorrectString.equals("\""))
			{
				correctString = "";
			}
			// û���ҵ���б�ܣ������ַ���
			else
			{
				correctString = mayCorrectString;
			}
			// ���û�г����κδ�����������
			if (!isError)
			{
				i = mayCorrect_i;
				int index = isExistString(stringList, correctString);
				// �ַ����������ڣ������ַ���
				if (-1 == index)
				{
					stringList.add(correctString);
					index = stringList.size() - 1;
				}
				// ��Ӷ�Ԫ��
				dualExpression += "(STRING," + index + ") ";
			}
		}
		// ����ȱ����������֮ƥ��
		else
		{
			String errorStr = rowStr.substring(i);
			i = rowStr.length() - 1;
			errorMessageBeans.add(new ErrorMessageBean("A0009", errorStr,
					countRows));
		}
		return i;
	}

	/**
	 * �жϱ�ʶ���Ƿ����
	 * 
	 * @return -1��û�У���index�����ڣ��ڱ�ʶ�����е�����[��0��ʼ]��
	 */
	public int isExistidentifier(String singleWord)
	{
		if (this.identifierHashMap.containsKey(singleWord))
		{
			return this.identifierHashMap.get(singleWord);
		}
		return -1;
	}

	/**
	 * �ж����ֳ������ַ������� �Ƿ���ֹ�
	 * 
	 * @return -1��û�У���index�����ڣ���arraylist�е� [����]��
	 */
	public int isExistString(ArrayList<String> stringList, String singleString)
	{
		for (int i = 0; i < stringList.size(); i++)
		{
			if (stringList.get(i).equals(singleString))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * backslash �ж��ַ������Ƿ���ֹ�
	 * 
	 * @return -1��û�У���index�����ڣ���arraylist�е� [���� + 1]��
	 */
	public int isExistChar(char singleChar)
	{
		for (int i = 0; i < charList.size(); i++)
		{
			if (charList.get(i) == singleChar)
			{
				return i;
			}
		}
		return -1;
	}

	// ���������
	public int handleOperator(int i, char singleChar)
	{
		switch (singleChar)
		{
		case '+':
		case '-':
			if (isNextCharExistAndEqual(i, singleChar))
			{
				dualExpression += "(" + singleChar + singleChar + ",_) ";
				i++;
			}
			else if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(" + singleChar + "=,_) ";
				i++;
			}
			else
			{
				dualExpression += "(" + singleChar + ",_) ";
			}
			break;
		case '%':
			if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(" + singleChar + "=,_) ";
				i++;
			}
			else
			{
				dualExpression += "(" + singleChar + ",_) ";
			}
			break;
		// ��������ע������
		case '*':
			if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(" + singleChar + "=,_) ";
				i++;
			}
			// ���ֻ�� */ ������
			else if (isNextCharExistAndEqual(i, '/'))
			{
				errorMessageBeans.add(new ErrorMessageBean("A0004",
						"comments error", countRows));
				i++;
			}
			else
			{
				dualExpression += "(" + singleChar + ",_) ";
			}
			break;
		// ��������ע������
		case '/':
			if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(/=,_) ";
				i++;
			}
			// ��Ϊ // ������ȫ������
			else if (isNextCharExistAndEqual(i, '/'))
			{
				i = rowStr.length() - 1;
			}
			// ��Ϊ /* ���������ע��
			else if (isNextCharExistAndEqual(i, '*'))
			{
				foundMultiComments = true;

				pattern = Pattern.compile("[*][/]");
				matcher = pattern.matcher(rowStr);
				// �ڱ����ҵ�le */
				if (i + 2 < rowStr.length() && matcher.find(i + 2))
				{
					foundMultiComments = false;
					i = matcher.end() - 1;
				}
				// �ڱ���û���ҵ� */
				else
				{
					try
					{
						while ((rowStr = br.readLine()) != null)
						{
							countRows++;
							// ����ǰ�е��ַ��� �ж��Ƿ�Ϊ */
							matcher = pattern.matcher(rowStr);
							// �ڱ����ҵ�le */
							if (matcher.find(0))
							{
								foundMultiComments = false;
								i = matcher.end() - 1;// ��Ϊ�����ѭ����Ҫ��һ
								break;
							}
							// �ڱ���û���ҵ� "*/", then continue;
						}
						if (rowStr == null)
						{
							rowStr = "";
							i = -1;
						}
						if (foundMultiComments)
						{
							errorMessageBeans.add(new ErrorMessageBean("A0003",
									"comments error", countRows));
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				dualExpression += "(/,_) ";
			}
			break;
		case '=':
			if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(relop," + singleChar + "=) ";
				i++;
			}
			else
			{
				dualExpression += "(=,_) ";
			}
			break;
		case '<':
		case '>':
		case '!':
		case '��':
			if (isNextCharExistAndEqual(i, '='))
			{
				dualExpression += "(relop," + singleChar + "=) ";
				i++;
			}
			else
			{
				dualExpression += "(relop," + singleChar + ") ";
			}
			break;
		case '^':
			dualExpression += "(relop,_) ";
			break;
		case '&':
		case '|':
			if (isNextCharExistAndEqual(i, singleChar))
			{
				dualExpression += "(relop," + singleChar + singleChar + ") ";
				i++;
			}
			else
			{
				dualExpression += "(relop," + singleChar + ") ";
			}
			break;
		// case '}':case '{':case '��':case '��':case ']':case '[':case ',':case
		// '��':case ';':case '��':case '(':case ')':case '��':case '��':
		default:
			dualExpression += "(" + singleChar + "," + "_) ";
			break;
		}
		return i;
	}

	// �ж���һ���ַ��Ƿ���ڣ��ҵ���Ҫƥ����ַ�
	public boolean isNextCharExistAndEqual(int i, char operator)
	{
		if (i + 1 < rowStr.length() && rowStr.charAt(i + 1) == operator)
		{
			return true;
		}
		return false;
	}

	// �����Ԫ����Ϣ
	public void handleDualExpression()
	{
		// ��ʱ��������
		System.out.println(dualExpression);
	}

	// ���������Ϣ
	public String handleErrorMessage()
	{
		// ���û�д�����ʾ �ʷ����� �ɹ�
		String showLexicalMsg = "";
		if (errorMessageBeans.size() != 0)
		{
			HandleXMLFile handleXMLFile = new HandleXMLFile(new File(
					"data/ErrorMessage.xml"));
			String errorCode = null;
			for (int i = 0; i < errorMessageBeans.size(); i++)
			{
				ErrorMessageBean er = errorMessageBeans.get(i);
				errorCode = er.getErrorCode();
				showLexicalMsg += "> Line(" + er.getErrorLine() + "): error "
						+ errorCode + ": [" + er.getErrorWord() + "]: "
						+ handleXMLFile.getErrorMsg(errorCode) + "\n";
			}
		}
		return showLexicalMsg;
	}

	/**
	 * ����ʷ���������һЩ��Ž���ı���ֵ���Ա������һ�δʷ�����
	 * ������numberList��stringList��charList��identifierList��
	 * ....identifierHashMap��errorMessageBeans��dualExpression
	 * ���������ؼ��ֱ�keywordsHashMap��
	 */
	public void clearLexicalAnalysisResult()
	{
		numberList.clear();
		stringList.clear();
		charList.clear();
		identifierList.clear();
		identifierHashMap.clear();
		errorMessageBeans.clear();
		dualExpression = "";
		newTempIndex = 0;
	}

	public int newTempInIdentifierList()
	{
		String newTemp = "_T" + (++newTempIndex);
		identifierList.add(new IdentifierBean(newTemp, null, 0));
		return (identifierList.size() - 1);
	}
}
