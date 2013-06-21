/**
 * @author Randy
 * @date 2013-04-27
 * @function ����ʽ�Ľṹ
 */
package compilers.javabean;

import java.util.ArrayList;

import compilers.parsers.GrammarParserByLL1;

public class Production
{
	// define member variable
	public ArrayList<Object> production = new ArrayList<Object>();
	public int head = 0;

	/**
	 * constructor 1
	 */
	public Production()
	{
		
	}

	/**
	 * constructor 2
	 */
	public Production(int head, ArrayList<Object> production)
	{
		this.production = production;
		this.head = head;
	}
	
	/**
	 * ��ʾһ������ʽ
	 */
	public String showProduction()
	{
		String out = "";
		out += Nonterminal.get(head) + "��";
		for (int k = 0; k < production.size(); k++)
		{
			if (GrammarParserByLL1
					.isTerminalObject(production.get(k)))
			{
				Terminal terminal = (Terminal) production.get(k);
				out += Terminal.get(terminal.index)
						+ " ";
			}
			else
			{
				Nonterminal nonterminal = (Nonterminal) production.get(k);
				out += Nonterminal
						.get(nonterminal.index) + " ";
			}
		}
		return out;
	}
	/**
	 * ��ʾһ������ʽ��html�����ʽ
	 */
	public String showProduction_html()
	{
		String out = "<tr><td>";
		out += Nonterminal.get(head) + "</td>" +
				"<td> �� </td>" +
				"<td>";
		for (int k = 0; k < production.size(); k++)
		{
			if (GrammarParserByLL1
					.isTerminalObject(production.get(k)))
			{
				Terminal terminal = (Terminal) production.get(k);
				out += Terminal.get(terminal.index)
						+ " ";
			}
			else
			{
				Nonterminal nonterminal = (Nonterminal) production.get(k);
				out += Nonterminal
						.get(nonterminal.index) + " ";
			}
		}
		out += "</td></tr>";
		return out;
	}
	/**
	 * �жϲ���ʽ���Ƿ����null
	 */
	public boolean isContainNull()
	{
		if (production.get(production.size() - 1) instanceof Terminal) 
		{
			Terminal terminal = (Terminal)production.get(production.size() - 1);
			if (terminal.index == Terminal.IndexOf("null")) 
			{
				return true;
			}
		}
		return false;
	}
}
