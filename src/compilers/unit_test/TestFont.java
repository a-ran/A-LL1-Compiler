/**
 * @author Randy
 * @date Mar 11, 2012
 * @function 
 */
package compilers.unit_test;

import java.awt.GraphicsEnvironment;


public class TestFont
{// ��õ�ǰϵͳ����

	public TestFont()
	{
	}// ������

	public void getfont()
	{//

		String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();// ��õ�ǰϵͳ����

		for (int i = 0; i < fontnames.length; i++)
		{// �����������
			System.out.println(fontnames[i]);
		}
	}

	public static void main(String[] args)
	{
		TestFont f = new TestFont();
		f.getfont();
	}
}