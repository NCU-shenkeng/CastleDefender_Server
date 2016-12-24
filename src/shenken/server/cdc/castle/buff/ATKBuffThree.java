package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class ATKBuffThree extends CastleBuff
{

	public ATKBuffThree(int fullTime)
	{
		super(5, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setATK(castle.getATK() + 5);
	}
}
