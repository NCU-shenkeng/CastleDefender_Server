package shenken.net.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TCPClient{
	
	private static TCPClient instance = null;
	private static IReceive receiver = null;
	
	private static final String host = "127.0.0.1";
	private static final int port = 3319;
	
	private static Socket socket = null;
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static TCPClientListenServer listen= null;
	private static Thread listenThread= null;
	
	private boolean running = true;
	
	private static DataOutputStream output;
	
	/**
	 * init tcp client
	 */
	
	public static void initTCPClient(){
		if(instance == null) throw new NullPointerException("instance null");
		try
		{
			socket = new Socket(host , port);
			listen = new TCPClientListenServer(socket);
			output = new DataOutputStream(socket.getOutputStream());
					
			listenThread = new Thread(listen);
			listenThread.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("client init");
	}
	
	public static void initTCPClient(IReceive action){
		if(instance == null) throw new NullPointerException("instance null");
		try
		{
			receiver = action;
			socket = new Socket(host , port);
			listen = new TCPClientListenServer(socket);
			output = new DataOutputStream(socket.getOutputStream());
					
			listenThread = new Thread(listen);
			listenThread.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("client init");
	}
	
	private TCPClient(){}
	
	/**
	 * get host
	 */
	
	public String getHost(){
		return this.host;
	}
	
	/**
	 * regist receive action
	 */
	
	public void registReceiveAction(IReceive receiver){
		this.receiver = receiver;
	}
	/**
	 * client handle receive message from server
	 */
	public void onReceive(String msg){
		if(receiver == null) throw new NullPointerException("receiver null");
		receiver.onReceive(msg);
	}
	
	/**
	 * get tcp client instance
	 */
	
	public static TCPClient getInstance(){
		if(instance == null)
		{
			synchronized (TCPClient.class) {
				instance = new TCPClient();
			}
		}
		return instance;
	}
	
	/**
	 * send message to server
	 */
	
	public void send(String msg){
		if(socket == null) throw new NullPointerException("socket null");
		if(output == null) throw new NullPointerException("output stream null");
		try {
			output.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stop listen from server
	 */
	
	public void stopListen(){
		this.listen.setRunning(false);
	}
}
