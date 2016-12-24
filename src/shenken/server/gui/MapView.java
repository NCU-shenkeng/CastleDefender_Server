package shenken.server.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

import shenken.server.cdc.Map;
import shenken.server.cdc.Player;

@SuppressWarnings("serial")
public class MapView extends JPanel implements ActionListener
{
	private Vector<Player> playerTable;
	Map map;
	private int blockX = 60;
	private int blockY = 65;
	private boolean[][] mapTable;
	private int zoom = 8;

	public MapView()
	{

		this.setBounds(0, 0, blockX * zoom, blockY * zoom);
		setFocusable(true);
		requestFocusInWindow();
		new Timer(100, this).start();
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(blockX * zoom, blockY * zoom);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, blockX * zoom, blockY * zoom);
		paintGameCanWalkMap(g);
		paintGameItemMap(g);
		if (playerTable != null)
		{
			for (Player player : playerTable)
			{
				g.setColor(Color.PINK);
				if (player.getMapViewColor() != null)
				{
					g.setColor(player.getMapViewColor());
				}
				g.fillRect(player.getX() * zoom, player.getY() * zoom, zoom, zoom);
			}
		}
	}

	private void paintGameCanWalkMap(Graphics g)
	{
		if (mapTable != null)
		{
			for (int i = 0; i < mapTable.length; i++)
			{
				for (int j = 0; j < mapTable[i].length; j++)
				{
					if (mapTable[i][j])
					{
						g.setColor(Color.WHITE);
						g.fillRect(i * zoom, j * zoom, zoom, zoom);
						g.setColor(Color.GRAY);
						g.drawRect(i * zoom, j * zoom, zoom, zoom);
					}
				}
			}
		}
	}

	private void paintGameItemMap(Graphics g)
	{
		if (map != null)
		{
			Stroke temp = ((Graphics2D)g).getStroke();
			((Graphics2D)g).setStroke(new BasicStroke(2));
			g.setColor(Color.RED);
			
			for (int i = 0; i < Map.blockWidth; i++)
			{
				for (int j = 0; j < Map.blockHeight; j++)
				{
					if (map.getMapItem()[i][j] != null)
					{
						g.drawLine(i * zoom, j * zoom, (i + 1) * zoom , (j + 1) * zoom);
						g.drawLine((i + 1) * zoom, j * zoom, i * zoom , (j + 1) * zoom);
					}
				}
			}
			
			((Graphics2D)g).setStroke(new BasicStroke(3));
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		repaint();
	}

	public void setPlayerTable(Vector<Player> playerTable)
	{
		this.playerTable = playerTable;
	}

	public Vector<Player> getPlayerTable()
	{
		return playerTable;
	}

	public void setMapTable(boolean[][] mapTable)
	{
		this.mapTable = mapTable;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}
}
