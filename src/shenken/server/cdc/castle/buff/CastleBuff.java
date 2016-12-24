package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public abstract class CastleBuff
{
	int typeID = 0;
	int time = 0;
	int fullTime = 0;
	int ownID = 0;
	
	public int getTypeID()
	{
		return typeID;
	}
	
	public CastleBuff(int typeID, int fullTime)
	{
		this.typeID = typeID;
		this.fullTime = fullTime;
		this.time = this.fullTime;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}
	
	public void setFullTime(int fullTime)
	{
		this.fullTime = fullTime;
	}
	
	public int getFullTime()
	{
		return fullTime;
	}
	
	public int getOwnID()
	{
		return ownID;
	}
	
	public void setOwnID(int ownID)
	{
		this.ownID = ownID;
	}
	
	public abstract void calcBuff(Castle castle);
}
