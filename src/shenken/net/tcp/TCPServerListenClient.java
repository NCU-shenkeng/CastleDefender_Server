package shenken.net.tcp;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPServerListenClient implements Runnable {
	
	private Socket client;
	private DataInputStream input = null;
	private boolean running = true;
	
	public TCPServerListenClient(Socket client)
	{
		if(client == null ) throw new NullPointerException();
		try
		{
			this.client = client;
			this.input = new DataInputStream(this.client.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run() 
	{
		{
			try 
			{
				while(running)
				{
					String msg = input.readUTF();
					TCPServer.getInstance().onReceive(msg);
				}
			}
			catch (IOException e) 
			{
			}
		}
	}
	
	public boolean isEqual(Socket socket){
		return (this.client == socket);
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

}
