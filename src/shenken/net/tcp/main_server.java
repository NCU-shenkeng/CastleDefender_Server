package shenken.net.tcp;
import java.net.Socket;
import java.util.Scanner;

public class main_server {
	
	
	
	public static void main(String[] args)
	{
		TCPServer.getInstance().initTCPServer();
		TCPServer.getInstance().registReceiveAction(new IReceive() {
			@Override
			public void onReceive(String msg) {
				System.out.println(msg);
			}

			@Override
			public void afterAccept(Socket client)
			{
				System.out.println("accept");
				TCPServer.getInstance().sendMsg(0, "welcom");
			}
		});
		while(true){
			Scanner scan = new Scanner(System.in);
			TCPServer.getInstance().broadcast(scan.nextLine());;
		}
	}
}
