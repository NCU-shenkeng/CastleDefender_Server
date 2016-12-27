package shenken.server;

import java.awt.BorderLayout;import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shenken.net.PlayerIPaddress;
import shenken.net.tcp.IReceive;
import shenken.net.tcp.Message;
import shenken.net.tcp.TCPServer;
import shenken.net.udp.client.Client;
import shenken.server.cdc.Room;
import shenken.server.gui.CastleInfo;
import shenken.server.gui.MainWindow;
import shenken.server.gui.MapView;
import shenken.server.gui.PlayerInfo;

public class Main
{
	static Room room = null;
	static MapView mapView = new MapView();
	static PlayerInfo playerView = new PlayerInfo();
	static CastleInfo castleInfo = new CastleInfo();
	
	public static void main(String[] args)
	{	
		try
		{
			TCPServer.getInstance().initTCPServer();
			Client.startUDPBroadCast();

			newGame();
			new Thread(new Watchdog(),"Watchdog").start();
			
			JPanel gameSitch = new JPanel();
			gameSitch.setLayout(new BorderLayout(15, 0));
			JButton gameStart = new JButton("Game Start");
			gameStart.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Main.newGame();
					
				}
			});
			JButton gameClose = new JButton("Game Close");
			gameClose.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Main.closeGame();
					
				}
			});
			gameSitch.add(BorderLayout.CENTER, gameStart);
			gameSitch.add(BorderLayout.EAST, gameClose);
			MainWindow.getWindow().getContentPane().setLayout(new BorderLayout(3,3));
			MainWindow.getWindow().getContentPane().add(BorderLayout.NORTH,gameSitch);
			MainWindow.getWindow().getContentPane().add(BorderLayout.CENTER,mapView);
			MainWindow.getWindow().getContentPane().add(BorderLayout.SOUTH,playerView);
			MainWindow.getWindow().getContentPane().add(BorderLayout.EAST,castleInfo);
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Server setup fail,TCP port be used!");
			//MainWindow.getWindow().dispatchEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSING));
			System.exit(0);
		}
		
		//MainWindow.getWindow().pack();
	}
	
	public static void newGame()
	{
		if (room != null)
		{
			closeGame();
		}
			room = new Room();
			
			Vector<PlayerIPaddress> iptable = new Vector<PlayerIPaddress>();
			try
			{
				iptable.add(new PlayerIPaddress("127.0.0.1",3011));
//				iptable.add(new PlayerIPaddress("140.115.59.204",3010));
//				iptable.add(new PlayerIPaddress("140.115.52.17",3010));
				Client.getUDPBC().setClientIPTable(iptable);
				Client.getUDPBC().setSendAction(room);
				TCPServer.getInstance().registReceiveAction(room);
				
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			

			mapView.setPlayerTable(room.getPlayerTable());
			mapView.setMapTable(room.getMap().getBlock());
			mapView.setMap(room.getMap());
			playerView.setPlayers(room.getPlayerTable());
			castleInfo.setCastles(room.getCastles());
			
			new Thread(room, "Game Room").start();
	}
	
	public static void closeGame()
	{
		if (room != null)
		{
			room.setRunFlag(false);
			Vector<Socket> clients = TCPServer.getInstance().getClientTable();
			for (Socket client : clients)
			{
				try
				{
					client.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clients.removeAllElements();
			playerView.setPlayers(null);
			castleInfo.setCastles(null);
			Client.getUDPBC().setSendAction(null);
			TCPServer.getInstance().registReceiveAction(new IReceive()
			{

				@Override
				public void onReceive(String msg)
				{
				}
				
				@Override
				public void afterAccept(Socket client)
				{
					try
					{
						TCPServer.getInstance().sendMsg(TCPServer.getInstance().getClientCount()-1, String.format("%s", Message.ROOM_FULL));
						client.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					TCPServer.getInstance().getClientTable().removeAllElements();
				}
			});
			room = null;
		}
	}
}
