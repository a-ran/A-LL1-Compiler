/**
 * @author Randy
 * @date 2013-05-27
 * @function ���ṹ
 */
package compilers.util;

import java.util.ArrayList;

public class Tree
{
	public ArrayList<TreeNode> nodes = null;

	/**
	 * constructor
	 */
	public Tree()
	{
		nodes = new ArrayList<TreeNode>();
	}

	/**
	 * ��ӽڵ�
	 * 
	 * @param node
	 * @return �ڵ����� index
	 */
	public int addNode(TreeNode node)
	{
		nodes.add(node);
		int index = nodes.size() - 1;
		node.index = index;
		return index;
	}
}
