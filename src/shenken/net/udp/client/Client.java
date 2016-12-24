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

	private Vector<IPtable> serverTable; // ���A����m��
	private SendAction action; // �ǰe�T���ƥ�
	private Boolean runFlag = true; // ����O��

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
	 * ��l�ưߤ@����
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
	 * ����ߤ@����
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
	 * ���o�ߤ@����Client����
	 * 
	 * @return Client����
	 */
	public static Client getUDPBC()
	{
		return UDPBC;
	}

	/**
	 * ���o�ߤ@����Thread
	 * 
	 * @return Thread����
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
	 * @param �]�w�s��IP
	 *            or Domain
	 * @param �]�w�s����
	 */
	public Client(String Server, int Port) throws UnknownHostException
	{
		this();
		serverTable.add(new IPtable(Server, Port));
	}

	/**
	 * @param �]�w�s��IP
	 *            or Domain
	 * @param �]�w�s����
	 * @param �]�w�ǰe�ƥ�
	 */
	public Client(String Server, int Port, SendAction Action) throws UnknownHostException
	{
		this(Server, Port);
		action = Action;
	}

	/**
	 * ����
	 */
	@Override
	public void run()
	{
		// System.out.println("Client Running");
		while (runFlag)
		{
			try
			{
				byte buffer[] = action.send().getBytes(); // �N�ƥ�^�Ǧr���ഫ���줸��C
				// �ʸ˸Ӧ줸�ꦨ���ʥ] DatagramPacket�A�P�ɫ��w�o�e��H�C
				for (IPtable server : serverTable)
				{
//					System.out.println("sendTo:" + server.port);
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server.address, server.port);
					DatagramSocket socket;

					socket = new DatagramSocket();
					socket.send(packet); // �o�e
					socket.close(); // ���� UDP socket.
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
	 * �]�w����лx
	 * 
	 * @param ����лx
	 */
	public void setRunFlag(Boolean flag)
	{
		this.runFlag = flag;
	}

	/**
	 * ���o����лx
	 */
	public boolean getRunFlag()
	{
		return runFlag;
	}

	/**
	 * �]�w�ǰe�ƥ�
	 */
	public void setSendAction(SendAction action)
	{
		this.action = action;
	}

	/**
	 * ���o�ǰe�ƥ�
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
