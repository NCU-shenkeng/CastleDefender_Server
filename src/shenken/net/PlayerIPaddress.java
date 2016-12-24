package shenken.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlayerIPaddress
{
	private InetAddress address;
	private int UDPPort = 3010;
	
	public PlayerIPaddress(String address) throws UnknownHostException
	{
		this.address = InetAddress.getByName(address);
	}
	
	public PlayerIPaddress(String address,int UDPPort) throws UnknownHostException
	{
		this.address = InetAddress.getByName(address);
		this.UDPPort = UDPPort;
	}
	
	public InetAddress getAddress(){
		return address;
	}
	
	public int getUDPPort(){
		return UDPPort;
	}
	
	public void setAddress(String address) throws UnknownHostException{
		this.address = InetAddress.getByName(address);
	}
	
	public void setUDPPort(int UDPPort){
		this.UDPPort = UDPPort;
	}
}
