/**
 * @author Randy
 * @date 2013-05-25
 * @function ���������ı�����
 */
package compilers.main_ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

public class MyText extends JTextArea
{
	// define variable
	boolean isChanged = false;
	String fileName = null;
	UndoManager undoManager = null;

	/**
	 * constructor
	 */
	public MyText()
	{
		// �����ı��Ƿ����仯�������Ż���ȡ���������Ժ���˵��������
		this.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				// TODO Auto-generated method stub
				// System.out.println("remove");
				if (isChanged == false)
				{
					isChanged = true;
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				// TODO Auto-generated method stub
				// System.out.println("insert");
				if (isChanged == false)
				{
					isChanged = true;
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				// TODO Auto-generated method stub
				// System.out.println("changed");
				if (isChanged == false)
				{
					isChanged = true;
				}
			}
		});

		// ���� undo and redo
		undoManager = new UndoManager();
		this.getDocument().addUndoableEditListener(undoManager);
	}
}
