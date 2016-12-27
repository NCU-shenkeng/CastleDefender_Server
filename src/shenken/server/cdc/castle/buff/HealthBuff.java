package shenken.server.cdc.castle.buff;

import shenken.server.cdc.Castle;
import shenken.server.cdc.Room;

public class HealthBuff extends CastleBuff
{
	private int helthPoint = 100;

	public HealthBuff()
	{
		super(6, Room.refreshTime);
	}

	@Override
	public void calcBuff(Castle castle)
	{
		castle.setHP(castle.getHP() + helthPoint);
	}
}
