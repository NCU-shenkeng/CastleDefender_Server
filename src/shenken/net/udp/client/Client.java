package shenken.net.udp.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import shenken.net.PlayerIPaddress;

/**
 * UDP Client
 */
public class Client implements Runnable
{
	private static Thread instance;
	private static Client UDPBC;

	private int cycle = 200;

	private Vector<IPtable> serverTable; // 伺服器位置表
	private SendAction action; // 傳送訊息事件
	private Boolean runFlag = true; // 執行記號

	private class IPtable
	{
		InetAddress address;
		int port;

		public IPtable(String address, int port) throws UnknownHostException
		{
			this.address = InetAddress.getByName(address);
			this.port = port;
		}
	}

	/**
	 * 初始化唯一實體
	 */
	public static void startUDPBroadCast()
	{
		if (instance == null)
		{
			UDPBC = new Client();
			instance = new Thread(UDPBC);
			instance.start();
		}
	}

	/**
	 * 停止唯一實體
	 */
	public static void stopUDPBroadCast()
	{
		if (instance != null)
		{
			UDPBC.runFlag = false;
			instance = null;
		}
	}

	/**
	 * 取得唯一實體Client物件
	 * 
	 * @return Client物件
	 */
	public static Client getUDPBC()
	{
		return UDPBC;
	}

	/**
	 * 取得唯一實體Thread
	 * 
	 * @return Thread物件
	 */
	public static Thread getInstance()
	{
		return instance;
	}

	/**
	 * 
	 */
	public Client()
	{
		serverTable = new Vector<>();
		action = new SendAction()
		{
			@Override
			public String send()
			{
				return "";
			}
		};
	}

	/**
	 * @param 設定連接IP
	 *            or Domain
	 * @param 設定連接埠
	 */
	public Client(String Server, int Port) throws UnknownHostException
	{
		this();
		serverTable.add(new IPtable(Server, Port));
	}

	/**
	 * @param 設定連接IP
	 *            or Domain
	 * @param 設定連接埠
	 * @param 設定傳送事件
	 */
	public Client(String Server, int Port, SendAction Action) throws UnknownHostException
	{
		this(Server, Port);
		action = Action;
	}

	/**
	 * 執行
	 */
	@Override
	public void run()
	{
		// System.out.println("Client Running");
		while (runFlag)
		{
			try
			{
				byte buffer[] = action.send().getBytes(); // 將事件回傳字串轉換為位元串。
				// 封裝該位元串成為封包 DatagramPacket，同時指定發送對象。
				for (IPtable server : serverTable)
				{
//					System.out.println("sendTo:" + server.port);
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server.address, server.port);
					DatagramSocket socket;

					socket = new DatagramSocket();
					socket.send(packet); // 發送
					socket.close(); // 關閉 UDP socket.
				}
				Thread.sleep(cycle);
			} catch (Exception e)
			{
				runFlag = false;
				e.printStackTrace();
			}
		}
	}

	/**
	 * 設定執行標誌
	 * 
	 * @param 執行標誌
	 */
	public void setRunFlag(Boolean flag)
	{
		this.runFlag = flag;
	}

	/**
	 * 取得執行標誌
	 */
	public boolean getRunFlag()
	{
		return runFlag;
	}

	/**
	 * 設定傳送事件
	 */
	public void setSendAction(SendAction action)
	{
		this.action = action;
	}

	/**
	 * 取得傳送事件
	 */
	public SendAction getSendAction()
	{
		return action;
	}

	public void setClientIPTable(Vector<PlayerIPaddress> table) throws UnknownHostException
	{
		serverTable.clear();
		for (PlayerIPaddress playerIPaddress : table)
		{
			serverTable.add(new IPtable(playerIPaddress.getAddress().getHostAddress(), playerIPaddress.getUDPPort()));
		}
	}

	public Vector<InetAddress> getServerIPTable()
	{
		Vector<InetAddress> temp = new Vector<>();
		for (IPtable table : serverTable)
		{
			temp.add(table.address);
		}
		return temp;
	}
	
	public Vector<IPtable> getServerTable()
	{
		return serverTable;
	}

}
