package shenken.server.cdc.job;

public abstract class Job
{
	private int ATK = 0;
	private int PickSpeed = 3000;
	private int MoveSpeed = 1000;
	private int ATKSpeed = 1000;
	private int MaxHP = 3;
	
	public static Job selectJob(int i)
	{
		switch (i)
		{
		case 0:
			return new GuardJob();
		case 1:
			return new SageJob();
		case 2:
			return new SworadManJob();
		default:
			return new GuardJob();
		}
	}
	
	public static Job selectJob(String i)
	{
		try
		{
			return selectJob(Integer.valueOf(i));
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void setATK(int aTK)
	{
		ATK = aTK;
	}
	
	public void setMaxHP(int maxHP)
	{
		MaxHP = maxHP;
	}
	
	public void setPickSpeed(int pickSpeed)
	{
		PickSpeed = pickSpeed;
	}
	
	public void setATKSpeed(int aTKSpeed)
	{
		ATKSpeed = aTKSpeed;
	}
	
	public void setMoveSpeed(int moveSpeed)
	{
		MoveSpeed = moveSpeed;
	}
	
	public int getATK()
	{
		return ATK;
	}
	
	public int getMaxHP()
	{
		return MaxHP;
	}
	
	public int getPickSpeed()
	{
		return PickSpeed;
	}
	
	public int getMoveSpeed()
	{
		return MoveSpeed;
	}
	
	public int getATKSpeed()
	{
		return ATKSpeed;
	}
}
