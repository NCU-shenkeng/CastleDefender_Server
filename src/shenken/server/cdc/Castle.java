package shenken.server.cdc;

import java.util.Iterator;
import java.util.Vector;

import shenken.server.cdc.castle.buff.CastleBuff;
import shenken.server.cdc.castle.buff.ShieldBuff;

public class Castle
{	
	private int ID = 0;
	private int HP = 301;
	private int defaultATK = 1;
	private int displayATK = 0;
	private int tempATK = 0;
	private int defaultSpeed = 1000;
	private int tempSpeed = 0;
	private int tempShield = 0;
	private int attackCD = 0;
	private Vector<CastleBuff> buffList;
	private Vector<Castle> enemyList;
	
	public Castle(int ID)
	{
		this.ID = ID;
		buffList = new Vector<>();
		enemyList = new Vector<>();
	}
	
	public int getID()
	{
		return ID;
	}
	
	public Vector<CastleBuff> getBuffList()
	{
		return buffList;
	}
	
	public Vector<Castle> getEnemyList()
	{
		return enemyList;
	}		
			
	public int getHP()
	{
		return HP;
	}
	
	public void setHP(int hP)
	{
		HP = hP;
	}
	
	
	public void update()
	{
		tempATK = defaultATK;
		tempSpeed = defaultSpeed;
		tempShield = 0;
		
		for (int i = 0; i < buffList.size(); i++)
		{
			CastleBuff castleBuff = buffList.get(i);
			if (castleBuff.getTime() > 0)
			{
				castleBuff.calcBuff(this);
				castleBuff.setTime(castleBuff.getTime()-Room.refreshTime);
			}else {
				buffList.remove(i);
			}
		}
		
		displayATK = tempATK;
	}
	
	public void calcDamage()
	{
		int totalDamage = 0;
		
		for (Castle castle : enemyList)
		{
				totalDamage += castle.getATK();
		}
		
		Iterator<CastleBuff> it = getBuffList().iterator();
		
		while (it.hasNext()) {
			CastleBuff castleBuff = it.next();
			if (castleBuff instanceof ShieldBuff)
			{
				ShieldBuff buff = ((ShieldBuff)castleBuff);
				int nowSheild = buff.getSheild();
				int leftSheild = nowSheild - totalDamage;
				if (leftSheild >= 0)
				{
					buff.setSheild(leftSheild);
					totalDamage = 0;
				}else{
					buff.setSheild(0);
					totalDamage += leftSheild;
				}
				if (buff.getSheild() < 1)
				{
					it.remove();
				}
			}
		}

		HP -= totalDamage;
//		System.out.println(String.format("Castle %s HP:%s ATK:%s CD:%s", getID(),getHP(),getATK(),attackCD));
	}
	
	public void setATK(int ATK)
	{
		tempATK = ATK;
	}
	
	public int getATK(){
		return this.tempATK;
	}
	

	public void setSpeed(int tempSpeed)
	{
		this.tempSpeed = tempSpeed;
	}
	
	public int getSpeed()
	{
		return tempSpeed;
	}
	
	public void setShield(int tempShield)
	{
		this.tempShield = tempShield;
	}
	
	public int getShield()
	{
		return tempShield;
	}
	
	
	public void setDefaultSpeed(int defaultSpeed)
	{
		this.defaultSpeed = defaultSpeed;
	}
	
	public void canAttack()
	{
		if (attackCD < 1)
		{
			attackCD += tempSpeed;
		} else
		{
			attackCD -= Room.refreshTime;
			tempATK = 0;
		}
	}
	
	public int getDisplayATK()
	{
		return this.displayATK;
	}
	
}
