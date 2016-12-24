package shenken.net.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer implements Runnable
{

	private static TCPServer instance;
	private static ServerSocket server;
	private final static int listenPort = 3319;
	private final static int MAX_CONN = 4;
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static Vector<Socket> clientTable = new Vector<Socket>();
	private static Vector<TCPServerListenClient> listenTable = new Vector<TCPServerListenClient>();
	private static IReceive receiver = null;

	private boolean running = true;

	private TCPServer()
	{
	}

	/**
	 * init tcp server
	 */

	public void initTCPServer()
	{
		try
		{
			server = new ServerSocket(listenPort);
			new Thread(instance).start();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("server waiting for connection");
	}

	@Override
	public void run()
	{
		try
		{
			while (running)
			{
				Socket client = server.accept();
				TCPServerListenClient listen = new TCPServerListenClient(client);
				addClientTable(client);
				addListenTable(listen);
				executor.execute(listen);
				if (receiver != null)
				{
					receiver.afterAccept(client);
				}
				System.out.println("client count " + clientTable.size());
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (executor != null)
				executor.shutdown();
			if (server != null)
				try
				{
					server.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

	/**
	 * server onReceive action , must regist in advance
	 */

	public void onReceive(String msg)
	{
		if (receiver == null)
			throw new NullPointerException("receiver null exception");
		receiver.onReceive(msg);
	}

	/**
	 * return the only instance
	 */

	public static TCPServer getInstance()
	{
		if (instance == null)
		{
			synchronized (TCPServer.class)
			{
				instance = new TCPServer();
			}
		}
		return instance;
	}

	/**
	 * regist server receive action
	 */

	public void registReceiveAction(IReceive receiver)
	{
		this.receiver = receiver;
	}

	/**
	 * set server is running
	 */

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	/**
	 * get client table
	 */
	public Vector<Socket> getClientTable()
	{
		return this.clientTable;
	}

	/**
	 * return client count
	 */

	public int getClientCount()
	{
		return this.clientTable.size();
	}

	/**
	 * get listen port
	 */

	public int getPort()
	{
		return this.listenPort;
	}

	/**
	 * server brocast to all client
	 */

	public void broadcast(String msg)
	{
		if (getClientCount() <= 0)
			return;
		for (Socket client : clientTable)
			sendMsg(client, msg);
	}

	/**
	 * send message to client by number
	 */

	public void sendMsg(int number, String msg)
	{
		Socket client = getClientTable().get(number);
		if (client == null)
			throw new NullPointerException("send msg fail , client null");
		this.sendMsg(client, msg);
	}

	/**
	 * add to client table
	 */

	private void addClientTable(Socket client)
	{
		if (this.clientTable.size() >= MAX_CONN)
			return;
		clientTable.add(client);
	}

	/**
	 * one client has only one listen thread , listen the message from client
	 * message
	 */
	private void addListenTable(TCPServerListenClient listen)
	{
		if (this.clientTable.size() >= MAX_CONN)
			return;
		listenTable.add(listen);
	}

	/**
	 * send message to one client
	 */

	private void sendMsg(Socket client, String msg)
	{
		DataOutputStream output = null;
		try
		{
			output = new DataOutputStream(client.getOutputStream());
			output.writeUTF(msg);
			System.out.println(client.getRemoteSocketAddress());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * stop listen from one client
	 */

	private void stopListen(Socket client)
	{
		for (TCPServerListenClient listen : listenTable)
			if (listen.isEqual(client))
				listen.setRunning(false);
	}
}
