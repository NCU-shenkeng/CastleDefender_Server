package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class SpeedBuffTwo extends CastleBuff
{

	public SpeedBuffTwo(int fullTime)
	{
		super(1, fullTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calcBuff(Castle castle)
	{
		//System.out.println(String.format("(%s / 100) * 90 = %s", castle.getSpeed(), (castle.getSpeed() / 100) * 90));
		castle.setSpeed((castle.getSpeed() / 100) * 90);
	}
}
