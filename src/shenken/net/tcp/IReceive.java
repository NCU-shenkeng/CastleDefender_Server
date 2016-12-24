package shenken.net.tcp;

import java.net.Socket;

public interface IReceive {
	public void onReceive(String msg);
	public void afterAccept(Socket client);
}
