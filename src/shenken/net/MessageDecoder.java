package shenken.net;

import java.util.Vector;


public class MessageDecoder
{
	public Vector<Packet> decode(String msg)
	{
		Vector<Packet> packets = new Vector<>();
		String[] packetTable;
		String[] packet;
		int command;
		packetTable = msg.split("\n");

		for (String msgString : packetTable)
		{
			Packet temp = new Packet();
			packet = msgString.split(",");
			try
			{
				command = Integer.parseInt(packet[0]);
			} catch (Exception e)
			{
				command = 9999;
			}

			temp.setCommand(command);
			for (int i = 1; i < packet.length; i++)
			{
				temp.getArgs().add(packet[i]);
			}
			packets.add(temp);
		}
		return packets;
	}
}
