package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ShieldBuffInvincible extends ShieldBuff
{
	private int fullSheild = 999999999;
	private int sheild = fullSheild;
	
	public ShieldBuffInvincible(int fullTime)
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
