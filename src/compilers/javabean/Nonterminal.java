/**
 * @author Randy
 * @date 2013-04-27
 * @function Nonterminal
 */
package compilers.javabean;

import java.util.ArrayList;

public class Nonterminal
{
	// define member variable
	public static ArrayList<String> nonterminals = new ArrayList<String>(); // ���ս������
	public int index;

	/**
	 * constructor
	 */
	public Nonterminal(int index)
	{
		this.index = index;
	}

	/**
	 * ��ʼ����̬���� nonterminals
	 * 
	 * @param terminal
	 */
	public static void setNonterminals(ArrayList<String> nonterminals)
	{
		Nonterminal.nonterminals = nonterminals;
	}

	/**
	 * ���ָ�������� nonterminals �е�ֵ
	 * 
	 * @param index
	 * @return Nonterminal.nonterminals[index];
	 */
	public static String get(int index)
	{
		return Nonterminal.nonterminals.get(index);
	}
}