package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ATKBuffTwo extends CastleBuff
{

	public ATKBuffTwo(int fullTime)
	{
		super(4, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setATK(castle.getATK() + 3);
	}
}
