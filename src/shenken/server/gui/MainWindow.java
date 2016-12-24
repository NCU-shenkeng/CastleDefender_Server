package shenken.server.gui;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class MainWindow extends JFrame
{

	public class Window {
		public final static int HEIGHT = 768;
		public final static int WIDTH = 1024;
	}
	
	private static MainWindow window = null;
	
	private MainWindow(){
		setTitle("CastleDenfender - Server");
		setSize(MainWindow.Window.WIDTH , MainWindow.Window.HEIGHT);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}
	
	//Singleton pattern
	public static MainWindow getWindow(){
		if(window == null){
				window = new MainWindow();
		}
		return window;
	}
		
}
