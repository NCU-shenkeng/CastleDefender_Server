package shenken.server.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
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
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		HPLabels = new ArrayList<>();
		for (int i = 0; i < 2; i++)
		{
			JLabel temp = new JLabel("Castle " + i);
			HPLabels.add(temp);
		}
		for (int i = 0; i < HPLabels.size(); i++)
		{
			add(HPLabels.get(i));
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
				try
				{
					Castle temp = castles.get(i);
					String buffString = "";
					for (CastleBuff castleBuff : temp.getBuffList())
					{
						buffString += String.format("<br>%s Time:%s FullTime:%s<br>", castleBuff.getClass().getName().replace("shenken.server.cdc.castle.buff.", ""),castleBuff.getTime(),castleBuff.getFullTime());
					}
					HPLabels.get(i).setText(String.format("<HTML>--Castle %s <br> HP %s <br> ATKSwitch %s <br> ATK %s <br> Speed %s <br> Shield %s <br> BuffList %s</HTML>", i,temp.getHP(),temp.getATK(),temp.getDisplayATK(),temp.getSpeed(),temp.getShield(),buffString));	
				} catch (NullPointerException  e)
				{
					HPLabels.get(i).setText(String.format("<HTML>--Castle %s <br> HP %s <br> ATKSwitch %s <br> ATK %s <br> Speed %s <br> Shield %s <br> BuffList %s</HTML>",i,0,0,0,0,0,""));
				}
			}
		}else{
			for (int i = 0; i < HPLabels.size(); i++)
			{
				HPLabels.get(i).setText(String.format("<HTML>--Castle %s <br> HP %s <br> ATKSwitch %s <br> ATK %s <br> Speed %s <br> Shield %s <br> BuffList %s</HTML>",i,0,0,0,0,0,""));
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
