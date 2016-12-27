package shenken.net.tcp;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPServerListenClient implements Runnable {
	
	private Socket client;
	private DataInputStream input = null;
	private boolean running = true;
	private IReceive receive = null;
	
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
			try 
			{
				while(running)
				{
					String msg = input.readUTF();
					if (receive != null)
					{
						receive.onReceive(msg);
					}
				}
			}
			catch (IOException e) 
			{
			}
	}
	
	public boolean isEqual(Socket socket){
		return (this.client == socket);
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

	public IReceive getReceive()
	{
		return receive;
	}
	
	public void setReceive(IReceive receive)
	{
		this.receive = receive;
	}
}
