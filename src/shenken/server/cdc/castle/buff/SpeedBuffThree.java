package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class SpeedBuffThree extends CastleBuff
{

	public SpeedBuffThree(int fullTime)
	{
		super(3, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setSpeed((castle.getSpeed() / 100) * 85);
	}
}
