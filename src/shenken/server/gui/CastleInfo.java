package shenken.server.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import shenken.server.cdc.Castle;
import shenken.server.cdc.Room;
import shenken.server.cdc.castle.buff.CastleBuff;

@SuppressWarnings("serial")
public class CastleInfo extends JPanel implements ActionListener
{
	ArrayList<JLabel> HPLabels;
	Vector<Castle> castles; 
	
	public CastleInfo()
	{
		this.setLayout(new FlowLayout());
		HPLabels = new ArrayList<>();
		for (int i = 0; i < 2; i++)
		{
			JLabel temp = new JLabel("Castle " + i);
			HPLabels.add(temp);
		}
		for (int i = 0; i < HPLabels.size(); i++)
		{
			GridBagConstraints temp = new GridBagConstraints();
			add(HPLabels.get(i), temp);
		}
		new Timer(Room.refreshTime, this).start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (castles != null)
		{
			for (int i = 0; i < castles.size(); i++)
			{
				Castle temp = castles.get(i);
				String buffString = "";
				for (CastleBuff castleBuff : temp.getBuffList())
				{
					buffString += String.format("<br>%s Time:%s FullTime:%s<br>", castleBuff.getClass().getName(),castleBuff.getTime(),castleBuff.getFullTime());
				}
				HPLabels.get(i).setText(String.format("<HTML>--Castle %s <br> HP %s <br> ATK %s <br> ATK2 %s <br> Speed %s <br> Shield %s <br> BuffList %s</HTML>", i,temp.getHP(),temp.getATK(),temp.getDisplayATK(),temp.getSpeed(),temp.getShield(),buffString));
			}
		}
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
}
