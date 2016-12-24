package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public abstract class ShieldBuff extends CastleBuff
{
	public ShieldBuff(int typeID, int fullTime)
	{
		super(typeID, fullTime);
	}

	private int fullSheild = 10;
	private int sheild = fullSheild;

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
