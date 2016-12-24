package shenken.server.gui;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import shenken.server.cdc.Castle;
import shenken.server.cdc.Player;
import shenken.server.cdc.Room;

@SuppressWarnings("serial")
public class PlayerInfo extends JPanel implements ActionListener
{
	ArrayList<JLabel> HPLabels;
	Vector<Player> players; 
	
	public PlayerInfo()
	{
		this.setLayout(new GridLayout(10, 10));
		HPLabels = new ArrayList<>();
		HPLabels.add(new JLabel("Player 0"));
		HPLabels.add(new JLabel("Player 1"));
		HPLabels.add(new JLabel("Player 2"));
		HPLabels.add(new JLabel("Player 3"));
		HPLabels.add(new JLabel("Player 4"));
		HPLabels.add(new JLabel("Player 5"));
		for (int i = 0; i < HPLabels.size(); i++)
		{
			GridBagConstraints temp = new GridBagConstraints();
			temp.gridx = 1;
			temp.gridy = i;
			temp.fill = GridBagConstraints.HORIZONTAL;
			temp.anchor = GridBagConstraints.EAST;
			add(HPLabels.get(i), temp);
		}
		new Timer(Room.refreshTime, this).start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (players != null)
		{
			for (int i = 0; i < players.size(); i++)
			{
				Player temp = players.get(i);
				HPLabels.get(i).setForeground(players.get(i).getMapViewColor());
				HPLabels.get(i).setText(String.format("Player %s Team:%s HP:%s ATK:%s X:%s Y:%s Dir:%s isDead:%s DeadTime:%s", i,temp.getTeamID(), temp.getHP(),temp.getATK(),temp.getX(),temp.getY(),temp.getDir(),temp.getIsDead(),temp.getDeadTime()));
			}
		}
		repaint();
	}
	
	public void setPlayers(Vector<Player> players)
	{
		this.players = players;
	}
}
