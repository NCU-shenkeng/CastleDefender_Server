package shenken.net.tcp;
import java.net.Socket;
import java.util.Scanner;

public class main_client {
@SuppressWarnings("static-access")
public static void main(String[] args){
		TCPClient.getInstance().initTCPClient(new IReceive() {
			
			@Override
			public void onReceive(String msg) {
				System.out.println("xxxx" + msg);
			}

			@Override
			public void afterAccept(Socket client)
			{
				// TODO Auto-generated method stub
				
			}
		});
		while(true){
			Scanner scan = new Scanner(System.in);
			TCPClient.getInstance().send(scan.nextLine());
		}
		
	}
}
