package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ShieldBuffTwo extends ShieldBuff
{
	private int fullSheild = 15;
	private int sheild = fullSheild;
	
	public ShieldBuffTwo(int fullTime)
	{
		super(10, fullTime);
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setShield(sheild);
	}
	
	public void setSheild(int sheild)
	{
		this.sheild = sheild;
	}
	
	public int getSheild()
	{
		return sheild;
	}
	
	public int getFullSheild()
	{
		return fullSheild;
	}
}
