package shenken.net;

import java.util.ArrayList;

public class Packet
{
	private int Command = 9999;
	private ArrayList<String> args;
	
	public Packet()
	{
		args = new ArrayList<>();
	}
	
	public int getCommand()
	{
		return Command;
	}
	
	public void setCommand(int command)
	{
		Command = command;
	}
	
	public ArrayList<String> getArgs()
	{
		return args;
	}
	
	@Override
	public String toString()
	{
		String temp = String.valueOf(Command);
		for (String string : args)
		{
			temp += "," + string;
		}
		return temp;
	}
	
}
