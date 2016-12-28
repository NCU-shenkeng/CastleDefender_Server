package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;

public class HealthBuff extends CastleBuff
{
	private int helthPoint = 100;

	public HealthBuff()
	{
		super(6, 1000);
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setHP(castle.getHP() + helthPoint);
		helthPoint = 0;
	}
}
