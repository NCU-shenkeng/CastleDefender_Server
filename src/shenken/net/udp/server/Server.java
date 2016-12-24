package shenken.net.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server implements Runnable
{
	private static Thread instance;
	private static Server UDPUS;

	/**
	 * 初始化唯一實體
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
	 * 停止唯一實體
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
	 * 取得唯一實體Server物件
	 * 
	 * @return Server物件
	 */
	public static Server getUDPUS()
	{
		return UDPUS;
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

	private int port; // 連接埠
	private int bufferSize; // 訊息暫存區最大的訊息大小
	private Boolean runFlag = true; // 執行記號
	ReceiveAction action; // 接受訊息後動作

	/**
	 * @param 監聽連接埠
	 */
	public Server(int Port)
	{
		port = Port;
		bufferSize = 8192; // 預設
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
	 * @param 監聽連接埠
	 * @param 接收訊息後事件
	 */
	public Server(int Port, ReceiveAction Action)
	{
		this(Port);
		action = Action;
	}

	/**
	 * @param 監聽連接埠
	 * @param 接收訊息後事件
	 * @param 訊息暫存區最大的訊息大小
	 */
	public Server(int Port, ReceiveAction Action, int BufferSize)
	{
		this(Port, Action);
		bufferSize = BufferSize;
	}

	/**
	 * 執行
	 */
	public void run()
	{
		System.out.println("Server Running at port : " + port);
		byte buffer[] = new byte[bufferSize]; // 設定訊息暫存區
		while (runFlag)
		{
			// System.out.println("Server Listening");
			try
			{
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				DatagramSocket socket = new DatagramSocket(port); // 設定接收的 UDP
																	// Socket.
				socket.receive(packet); // 接收封包。
				String msg = new String(buffer, 0, packet.getLength()); // 將接收訊息轉換為字串。
				if (action == null)
				{
					System.out.println("receive = " + msg); // 印出接收到的訊息。
				} else
				{
					action.run(msg); // 執行動作
				}
				socket.close(); // 關閉 UDP Socket.
			} catch (IOException e)
			{
				setRunFlag(false);
				e.printStackTrace();
			}
		}
		System.out.println("Server Stop");
	}

	/**
	 * 取得目前監聽Port
	 */
	public int getPort()
	{
		return this.port;
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
	 * 設定接收事件
	 */
	public void setReceiveAction(ReceiveAction action)
	{
		this.action = action;
	}
}
