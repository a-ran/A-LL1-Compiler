/**
 * @author Randy
 * @date 2013-05-27
 * @function ����Ԫ�صĽṹ
 */
package compilers.util;

public class TreeElem
{	
	// ��Ա
	public int val = -1; // �ۺ����ԣ���ΪidTable�е�index
	public int inh = -1; // �̳����ԣ�ͬ��
	public String type = null; // id �����ͣ���Ϊ int �� floatS
	
	public Object token;
	
	/**
	 * constructor 
	 */
	public TreeElem()
	{
	}

	/**
	 * constructor @param token
	 */
	public TreeElem(Object token)
	{
		this.token = token;
	}

	/**
	 * constructor @param val
	 * constructor @param inh
	 * constructor @param type
	 * constructor @param token
	 */
	public TreeElem(int val, int inh, String type, Object token)
	{
		this.val = val;
		this.inh = inh;
		this.type = type;
		this.token = token;
	}
}
