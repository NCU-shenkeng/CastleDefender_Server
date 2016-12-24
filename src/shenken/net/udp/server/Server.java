package shenken.net.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server implements Runnable
{
	private static Thread instance;
	private static Server UDPUS;

	/**
	 * ��l�ưߤ@����
	 */
	public static void initUDPServer()
	{
		if (instance == null)
		{
			UDPUS = new Server(3010, null);
			instance = new Thread(UDPUS);
			instance.start();
		}
	}

	/**
	 * ����ߤ@����
	 */
	public static void stopUDPServer()
	{
		if (instance != null)
		{
			UDPUS.runFlag = false;
			instance = null;
		}
	}

	/**
	 * ���o�ߤ@����Server����
	 * 
	 * @return Server����
	 */
	public static Server getUDPUS()
	{
		return UDPUS;
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

	private int port; // �s����
	private int bufferSize; // �T���Ȧs�ϳ̤j���T���j�p
	private Boolean runFlag = true; // ����O��
	ReceiveAction action; // �����T����ʧ@

	/**
	 * @param ��ť�s����
	 */
	public Server(int Port)
	{
		port = Port;
		bufferSize = 8192; // �w�]
		action = new ReceiveAction()
		{
			@Override
			public void run(String msg)
			{
				// do nothing
			}
		};
	}

	/**
	 * @param ��ť�s����
	 * @param �����T����ƥ�
	 */
	public Server(int Port, ReceiveAction Action)
	{
		this(Port);
		action = Action;
	}

	/**
	 * @param ��ť�s����
	 * @param �����T����ƥ�
	 * @param �T���Ȧs�ϳ̤j���T���j�p
	 */
	public Server(int Port, ReceiveAction Action, int BufferSize)
	{
		this(Port, Action);
		bufferSize = BufferSize;
	}

	/**
	 * ����
	 */
	public void run()
	{
		System.out.println("Server Running at port : " + port);
		byte buffer[] = new byte[bufferSize]; // �]�w�T���Ȧs��
		while (runFlag)
		{
			// System.out.println("Server Listening");
			try
			{
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				DatagramSocket socket = new DatagramSocket(port); // �]�w������ UDP
																	// Socket.
				socket.receive(packet); // �����ʥ]�C
				String msg = new String(buffer, 0, packet.getLength()); // �N�����T���ഫ���r��C
				if (action == null)
				{
					System.out.println("receive = " + msg); // �L�X�����쪺�T���C
				} else
				{
					action.run(msg); // ����ʧ@
				}
				socket.close(); // ���� UDP Socket.
			} catch (IOException e)
			{
				setRunFlag(false);
				e.printStackTrace();
			}
		}
		System.out.println("Server Stop");
	}

	/**
	 * ���o�ثe��ťPort
	 */
	public int getPort()
	{
		return this.port;
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
	 * �]�w�����ƥ�
	 */
	public void setReceiveAction(ReceiveAction action)
	{
		this.action = action;
	}
}
