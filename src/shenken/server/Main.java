package shenken.server;

import java.awt.BorderLayout;
import java.net.UnknownHostException;
import java.util.Vector;

import shenken.net.PlayerIPaddress;
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
	
	public static void main(String[] args)
	{	
		TCPServer.getInstance().initTCPServer();
		Client.startUDPBroadCast();
		
		newGame();
		
		MapView mapView = new MapView();
		PlayerInfo playerView = new PlayerInfo();
		CastleInfo castleInfo = new CastleInfo();
		mapView.setPlayerTable(room.getPlayerTable());
		mapView.setMapTable(room.getMap().getBlock());
		mapView.setMap(room.getMap());
		playerView.setPlayers(room.getPlayerTable());
		castleInfo.setCastles(room.getCastles());
		MainWindow.getWindow().getContentPane().setLayout(new BorderLayout());
		MainWindow.getWindow().getContentPane().add(BorderLayout.WEST,mapView);
		MainWindow.getWindow().getContentPane().add(BorderLayout.SOUTH,playerView);
		MainWindow.getWindow().getContentPane().add(BorderLayout.CENTER,castleInfo);
		//MainWindow.getWindow().pack();
	}
	
	public static void newGame()
	{
		if (room == null)
		{
			room = new Room();
			
			Vector<PlayerIPaddress> iptable = new Vector<PlayerIPaddress>();
			try
			{
//				iptable.add(new PlayerIPaddress("127.0.0.1",3010));
//				iptable.add(new PlayerIPaddress("140.115.59.204",3010));
//				iptable.add(new PlayerIPaddress("140.115.52.17",3010));
				Client.getUDPBC().setClientIPTable(iptable);
				Client.getUDPBC().setSendAction(room);
				TCPServer.getInstance().registReceiveAction(room);
				
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			
			new Thread(room, "Game Room").start();
		}
	}
	
	public static void closeGame()
	{
		if (room != null)
		{
			room.setRunFlag(false);
			room = null;
		}
	}
}
