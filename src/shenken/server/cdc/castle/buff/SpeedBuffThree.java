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
		//System.out.println(String.format("(%s / 100) * 85 = %s", castle.getSpeed(), (castle.getSpeed() / 100) * 85));
		castle.setSpeed((castle.getSpeed() / 100) * 85);
	}
}
