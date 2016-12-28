package shenken.server.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import shenken.server.cdc.Castle;
import shenken.server.cdc.Room;

@SuppressWarnings("serial")
public class GameSetInfo extends JPanel implements ActionListener
{
	ArrayList<JLabel> HPLabels;
	JTextField maxPlayerField = new JTextField("2");
	JTextField castelDefaultHP = new JTextField("300");
	JTextField defaultCreatIitemCount = new JTextField("50");
	JTextField randomCreatIitemCD = new JTextField("5000");
	Vector<Castle> castles; 
	
	public GameSetInfo()
	{
		this.setLayout(new GridLayout(4,2));
		add(new JLabel("MaxPlayer"));
		maxPlayerField.setHorizontalAlignment(JTextField.CENTER);
		add(maxPlayerField);
		add(new JLabel("DefaultCastleHP"));
		castelDefaultHP.setHorizontalAlignment(JTextField.CENTER);
		add(castelDefaultHP);
		add(new JLabel("DefaultCreatIitemCount"));
		defaultCreatIitemCount.setHorizontalAlignment(JTextField.CENTER);
		add(defaultCreatIitemCount);
		add(new JLabel("RandomCreatIitemCD(ms)"));
		randomCreatIitemCD.setHorizontalAlignment(JTextField.CENTER);
		add(randomCreatIitemCD);
		new Timer(Room.refreshTime, this).start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		repaint();
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(300, 800);
	}
	
	public void setCastles(Vector<Castle> vector)
	{
		this.castles = vector;
	}
	
	public int getMaxPlayer()
	{
		int temp = 4;
		try
		{
			temp = Integer.valueOf(maxPlayerField.getText());
		} catch (Exception e)
		{
			maxPlayerField.setText("4");
			JOptionPane.showMessageDialog(null, "getMaxPlayer not Integer,change to default 4");
		}
		return temp;
	}
	
	public int getCastelDefaultHP()
	{
		int temp = 300;
		try
		{
			temp = Integer.valueOf(castelDefaultHP.getText());
		} catch (Exception e)
		{
			castelDefaultHP.setText("300");
			JOptionPane.showMessageDialog(null, "castelDefaultHP not Integer,change to default 300");
		}
		return temp;
	}
	
	public int getDefaultCreatIitemCount()
	{
		int temp = 50;
		try
		{
			temp = Integer.valueOf(defaultCreatIitemCount.getText());
		} catch (Exception e)
		{
			defaultCreatIitemCount.setText("50");
			JOptionPane.showMessageDialog(null, "DefaultCreatIitemCount not Integer,change to default 50");
		}
		return temp;
	}
	
	public int getRandomCreatIitemCD()
	{
		int temp = 5000;
		try
		{
			temp = Integer.valueOf(randomCreatIitemCD.getText());
		} catch (Exception e)
		{
			randomCreatIitemCD.setText("5000");
			JOptionPane.showMessageDialog(null, "RandomCreatIitemCD not Integer,change to default 5000");
		}
		return temp;
	}
}
