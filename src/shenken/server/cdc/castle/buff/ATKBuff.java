package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ATKBuff extends CastleBuff
{

	public ATKBuff(int fullTime)
	{
		super(3, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setATK(castle.getATK() + 1);
	}
}
