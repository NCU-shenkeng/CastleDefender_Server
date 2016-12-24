package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class SpeedBuff extends CastleBuff
{

	public SpeedBuff(int fullTime)
	{
		super(0, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		System.out.println(String.format("(%s / 100) * 95 = %s", castle.getSpeed(), (castle.getSpeed() / 100) * 95));
		castle.setSpeed((castle.getSpeed() / 100) * 95);
	}
}
