package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ShieldBuffThree extends ShieldBuff
{
	private int fullSheild = 20;
	private int sheild = fullSheild;
	
	public ShieldBuffThree(int fullTime)
	{
		super(11, fullTime);
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
