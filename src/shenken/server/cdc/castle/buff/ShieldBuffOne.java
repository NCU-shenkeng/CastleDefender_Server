package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ShieldBuffOne extends ShieldBuff
{
	private int fullSheild = 10;
	private int sheild = fullSheild;
	
	public ShieldBuffOne(int fullTime)
	{
		super(9, fullTime);
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
