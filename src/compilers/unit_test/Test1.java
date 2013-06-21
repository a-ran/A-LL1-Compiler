/**
 * @author Randy
 * @date Mar 9, 2012
 * @function 
 */
package compilers.unit_test;

import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

public class Test1
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.contains(null);
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		String s1 = hashtable.put("1", "1");
		String s2 = hashtable.put("1", "2");
		System.out.println("s1=" + s1);
		System.out.println("s2=" + s2);
		System.out.println("hashtable.size()=" + hashtable.size());



	}

	public static void addIndentFunction(final JTextArea textArea)
	{
		Keymap parent = textArea.getKeymap();
		Keymap newMap = JTextComponent.addKeymap(textArea.getName(), parent);
		// ��������newMap.addActionForKeyStroke(KeyStroke.getKeyStroke("pressed TAB"),
		// new
		// AbstractAction(){@Overridepublic
		// void
		// actionPerformed(
		// ActionEvent
		// e
		// ){//
		// ���ѡ���ı�String
		// selection
		// =
		// textArea.getSelectedText();//
		// δ��ѡ���ı�ֱ�Ӳ����Ʊ��if(
		// selection
		// ==
		// null
		// )textArea.insert("/t",
		// textArea.getCaretPosition());//
		// ��ѡ���ı���ѡ���о�������else{//
		// ��ѡ���ı����йϷ�String[]
		// lines
		// =
		// selection.split("/r?/n");if(
		// lines.length
		// ==
		// 0
		// )return;StringBuilder
		// builder
		// =
		// new
		// StringBuilder();//
		// ��Ϊ��һ�������ÿ����������һ������builder.append(lines[0]);for(
		// int
		// i
		// =
		// 1;
		// i
		// <
		// lines.length;
		// i++
		// )builder.append(NEWLINE).append("/t").append(lines[i]);//
		// ������ѡ�оֲ��Ŀ�ʼλ��:
		// ���ѡ�б�����ʼλ�õ��ַ�//
		// ��Ϊ���׽����һ������,
		// ���λ�ú���һλ�������ջ�int
		// selectionStart
		// =
		// textArea.getSelectionStart()
		// +
		// 1;//
		// ������ѡ�оֲ��ĸ���λ��:
		// ���ѡ�б�������λ�õ��ַ�//
		// �ѿ�ʼλ�����ƫ��
		// ѡ�оֲ�����������ĳ���int
		// selectionEnd
		// =
		// selectionStart
		// +
		// builder.length();try{//
		// ---
		// ����ѡ�оֲ���һ�����׵�����
		// ://
		// �˱������ڴ洢��һ�е�����λ��,
		// ��ʼֵ:
		// ѡ�оֲ���ʼλ�õ�ǰһ��λ��int
		// lineHead
		// =
		// textArea.getSelectionStart()
		// -
		// 1;//
		// Ѱ����һ����ĩ;
		// ������һ����δѡ�оֲ����ӽ���,
		// ��������һ��while(
		// lineHead
		// >=
		// 0
		// &&
		// !���ӷ��textArea.getText(lineHead,
		// 1).equals("/n")
		// ){builder.insert(0,
		// textArea.getText(lineHead,
		// 1));lineHead--;}//
		// ȷ������λ��lineHead++;//
		// Ϊ��һ��������������builder.insert(0,
		// "/t");//
		// ��
		// ��һ�����׵�ԭѡ�оֲ�����λ��
		// �ֻ�Ϊ�����������Ĵ����ջ�textArea.replaceRange(builder.toString(),
		// lineHead,
		// textArea.getSelectionEnd());//
		// ���ô�����ѡ�оֲ�textArea.setSelectionStart(selectionStart);textArea.setSelectionEnd(selectionEnd);}catch(
		// BadLocationException
		// ex
		// ){Logger.getLogger(TextComponentUtils.class.getName()).log(Level.SEVERE,
		// null,
		// ex);}}}});//
		// �޳�����newMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
		// Event.SHIFT_MASK),
		// new
		// AbstractAction(){@Overridepublic
		// void
		// actionPerformed(
		// ActionEvent
		// e
		// ){//
		// ���ѡ���ı�String
		// selection
		// =
		// textArea.getSelectedText();//
		// δ��ѡ���ı�ʲô��Ҳ����if(
		// selection
		// ==
		// null
		// )return;//
		// ��ѡ���ı����йϷ�String[]
		// lines
		// =
		// selection.split("/r?/n");if(
		// lines.length
		// ==
		// 0
		// )return;StringBuilder
		// builder
		// =
		// new
		// StringBuilder();//
		// ���޳���һ������ÿ������se.seopu.com��һ������for(
		// int
		// i
		// =
		// 1;
		// i
		// <
		// lines.length;
		// i++
		// ){builder.append(NEWLINE);if(
		// lines[i].startsWith("/t")
		// )builder.append(lines[i].substring(1));else
		// if(
		// lines[i].startsWith(" ")
		// ){int
		// start
		// =
		// 0;//
		// �ĸ��ո�����һ������while(
		// start
		// <
		// lines[i].length()
		// &&
		// lines[i].charAt(start)
		// ==
		// ' '
		// &&
		// start
		// <
		// 4
		// )start++;builder.append(lines[i].substring(start));}elsebuilder.append(lines[i]);}try{//
		// ---
		// ����ѡ�оֲ���һ�����׵�����
		// ://
		// �˱������ڴ洢��һ�е�����λ��,
		// ��ʼֵ:
		// ѡ�оֲ���ʼλ�õ�ǰһ��λ��int
		// lineHead
		// =
		// textArea.getSelectionStart()
		// -
		// 1;//
		// Ѱ����һ����ĩ;
		// ������һ����δѡ�оֲ����ӽ���,
		// ���޳���һ�е�����while(
		// lineHead
		// >=
		// 0
		// &&
		// !textArea.getText(lineHead,
		// 1).equals("/n")
		// )lineHead--;//
		// ȷ������λ��lineHead++;//
		// �޳�������ѡ�оֲ��Ŀ�ʼλ��:
		// ���ѡ�б�����ʼλ�õ��ַ�int
		// selectionStart
		// =
		// textArea.getSelectionStart();//
		// ������ѡ�оֲ��ĸ���λ��:
		// ���ѡ�б�������λ�õ��ַ�//
		// �ѿ�ʼλ�����ƫ��
		// ѡ�оֲ��޳�������ĳ���int
		// selectionEnd
		// =
		// selectionStart
		// +
		// builder.length()
		// +
		// lines[0].length();String
		// firstLine
		// =
		// textArea.getText(lineHead,
		// textArea.getSelectionStart()
		// -
		// lineHead)
		// +
		// lines[0];//
		// Ϊ��һ�������޳�����if(
		// firstLine.startsWith("/t")
		// ){//
		// �����޳�����������ѡ�оֲ�λ��selectionStart--;selectionEnd--;builderhttp://www.soyes168.com.insert(0,
		// firstLine.substring(1));}else
		// if(
		// firstLine.startsWith(" ")
		// ){int
		// start
		// =
		// 0;//
		// �ĸ��ո�����һ������while(
		// start
		// <
		// firstLine.length()
		// &&
		// firstLine.charAt(start)
		// ==
		// ' '
		// &&
		// start
		// <
		// 4
		// ){//
		// �����޳�����������ѡ�оֲ�λ��selectionStart--;selectionEnd--;start++;}builder.insert(0,
		// firstLine.substring(start));}elsebuilder.insert(0,
		// firstLine);//
		// ��
		// ��һ�����׵�ԭѡ�оֲ�����λ��
		// �ֻ�Ϊ�����������Ĵ����ջ�textArea.replaceRange(builder.toString(),
		// lineHead,
		// textArea.getSelectionEnd());//
		// ���ô�����ѡ�оֲ�textArea.setSelectionStart(selectionStart);textArea.setSelectionEnd(selectionEnd);}catch(
		// BadLocationException
		// ex
		// ){Logger.getLogger(TextComponentUtils.class.getName()).log(Level.SEVERE,
		// null,
		// ex);}}});textArea.setKeymap(newMap);}
	}

}


