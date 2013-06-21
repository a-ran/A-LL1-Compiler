/**
 * @author Randy
 * @date 2013-05-25
 * @function ����LL1�ı��������ʷ����﷨�����壩����IDE
 * @test 
int area, r ;
area = r * ( r + r ) ;
 */
package compilers.main_ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainUI extends JFrame implements WindowListener
{
	// >>>>>>>>>>>>>> start main function >>>>>>>>>>>>>>//

	public static void main(String[] args)
	{
		MainUI mainUI = new MainUI();
	}

	// <<<<<<<<<<<<<<< end main function <<<<<<<<<<<<<<<//

	/**
	 * @param args
	 */
	Operaor operaor = null;

	MyMenuBar myMenuBar = null;
	MyToolBar myToolBar = null;
	MyMainPanel myMainPanel = null;

	/**
	 * constructor
	 */
	public MainUI()
	{
		// initial components
		myMainPanel = new MyMainPanel();
		operaor = new Operaor(myMainPanel.codeText, this);
		myMenuBar = new MyMenuBar(operaor);
		myToolBar = new MyToolBar(operaor);

		// add components
		this.setJMenuBar(myMenuBar);
		this.add(myToolBar, BorderLayout.PAGE_START);
		this.add(myMainPanel, BorderLayout.CENTER);

		// add action
		this.addWindowListener(this);

		// set JFrame
		this.setTitle("null - Ran Compiler");
		this.setIconImage(new ImageIcon("images/ran.png").getImage());
		this.setSize(800, 600);  
		this.setLocation(200, 100);
		// Ҫ��д(�����˳����������) //�ѽ��
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// ����windows�����ʽ����ϵͳ�ı�
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		this.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		// TODO Auto-generated method stub
		operaor.quit();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}
}
