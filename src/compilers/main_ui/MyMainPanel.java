/**
 * @author Randy
 * @date 2013-05-25
 * @function for designing the main interface�������������
 */
package compilers.main_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class MyMainPanel extends JPanel
{
	// define variable

	JLabel jlMsg = null;
	JLabel jlState = null;
	MyText codeText = null;
	MyText errorText = null;

	JScrollPane jspCode = null;
	JScrollPane jspError = null;
	JScrollPane jspMsg = null;

	JPanel jpLeftReserve = null;
	JPanel jpRight = null;
	JPanel jpMiddle = null;
	JPanel jpRightMsg = null;
	JPanel jpMiddleCode = null;
	JPanel jpMiddleError = null;

	JSplitPane jstLeft = null;
	JSplitPane jstRight = null;
	JSplitPane jstUpDown = null;

	/**
	 * constructor
	 */
	public MyMainPanel()
	{
		// initial components

		this.setLayout(new BorderLayout());

		//** �������ô���Ԫ�أ��������Ϲ���
		// ״̬��
		jlState = new JLabel(" Compiler v4.5");

		// ����Ϣ������ʾһЩ��񡣡���
		jlMsg = new JLabel("");

		codeText = new MyText();
		errorText = new MyText();

		errorText.setEditable(false);

		codeText.setFont(new Font("Dialog", Font.PLAIN, 17));
		errorText.setFont(new Font("Dialog", Font.PLAIN, 13));

		codeText.setBackground(new Color(204, 232, 207));//ǳ��ɫ����������
		errorText.setBackground(new Color(204, 232, 207));//ǳ��ɫ����������
		codeText.setMinimumSize(new Dimension(0, 0));
		errorText.setMinimumSize(new Dimension(0, 0));
		errorText.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		jspCode = new JScrollPane(codeText);
		jspError = new JScrollPane(errorText);
		jspMsg = new JScrollPane(jlMsg);

		// ���� codeText ״̬����ʾ��ǰ�кŵ��¼�
		codeText.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				try
				{
					// Show the caret's position in the status bar.
					int row = codeText.getLineOfOffset(e.getDot());
					int column = e.getDot() - codeText.getLineStartOffset(row);
					jlState.setText("Line: " + (row + 1) + ", Column: "
							+ column);
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		});

		// �м�������jpanel
		jpLeftReserve = new JPanel(new BorderLayout());
		jpRight = new JPanel(new BorderLayout());
		jpMiddle = new JPanel(new BorderLayout());
		jpRightMsg = new JPanel(new BorderLayout());
		jpMiddleCode = new JPanel(new BorderLayout());
		jpMiddleError = new JPanel(new BorderLayout());

		// �ָ����
		jstLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				jpLeftReserve, jpRight);
		jstRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				jpMiddle, jpRightMsg);
		jstUpDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				jpMiddleCode, jpMiddleError);

		// ���÷ָ������
		jstLeft.setDividerSize(7);
		jstRight.setDividerSize(7);
		jstUpDown.setDividerSize(7);

		jpRight.add(jstRight, BorderLayout.CENTER);
		jpMiddle.add(jstUpDown, BorderLayout.CENTER);

		jstUpDown.setOneTouchExpandable(true);
		jstRight.setOneTouchExpandable(true);
		jstLeft.setOneTouchExpandable(true);

		// add component
		jpRightMsg.add(jspMsg, BorderLayout.CENTER);
		jpMiddleCode.add(jspCode, BorderLayout.CENTER);
		jpMiddleError.add(jspError, BorderLayout.CENTER);
		jpMiddle.add(jlState, BorderLayout.SOUTH);
		this.add(jstLeft);

		// ���÷ָ���������ʾ����
		jstLeft.setDividerLocation(0);
		jstRight.setDividerLocation(700);
		jstUpDown.setDividerLocation(370);

		this.setVisible(true);
	}
}
