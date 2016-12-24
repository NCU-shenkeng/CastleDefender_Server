package shenken.server.cdc;

import java.awt.Color;
import java.net.Socket;

public class Player
{
	private int ID = 0;
	private int teamID = 0;
	private int HP = 3;
	private int MaxHP = 3;
	private int ATK = 1;
	private int X = 0;
	private int Y = 0;
	private int isA = 0;
	private int dir = 0;
	private int deadTime = 0;
	private boolean isDead = false;
	private int pickCD = 0;
	private int pickFullCD = 2000;
	private boolean doPicking = false;
	private Socket socket = null;
	private Color mapViewColor = Color.RED;

	public Player(int ID)
	{
		this.ID = ID;
	}

	public void setTeamID(int teamID)
	{
		this.teamID = teamID;
	}

	public int getTeamID()
	{
		return teamID;
	}

	public int getID()
	{
		return ID;
	}

	public int getHP()
	{
		return HP;
	}
	
	public void setHP(int hP)
	{
		HP = hP;
	}

	public int getMaxHP()
	{
		return MaxHP;
	}
	
	public void setMaxHP(int maxHP)
	{
		MaxHP = maxHP;
	}

	public void setIsDead(boolean isDead)
	{
		this.isDead = isDead;
	}
	
	public boolean getIsDead()
	{
		return this.isDead;
	}
	
	public int getX()
	{
		return X;
	}

	public int getY()
	{
		return Y;
	}
	
	public void setATK(int aTK)
	{
		ATK = aTK;
	}
	
	public int getATK()
	{
		return ATK;
	}

	public void setLocation(int x, int y)
	{
		X = x;
		Y = y;
	}

	public void setDir(int dir)
	{
		this.dir = dir;
	}

	public int getDir()
	{
		return dir;
	}

	public void setIsA(int isA)
	{
		this.isA = isA;
	}

	public int getIsA()
	{
		return isA;
	}

	public void setMapViewColor(Color mapViewColor)
	{
		this.mapViewColor = mapViewColor;
	}

	public Color getMapViewColor()
	{
		return mapViewColor;
	}
	
	public int getPickCD()
	{
		return pickCD;
	}
	
	public void setPickCD(int pickCD)
	{
		this.pickCD = pickCD;
	}
	
	public void setPickFullCD(int pickFullCD)
	{
		this.pickFullCD = pickFullCD;
	}
	
	public int getPickFullCD()
	{
		return pickFullCD;
	}

	public void setDir(String string)
	{
		try
		{
			dir = Integer.parseInt(string);
		} catch (Exception e)
		{
			dir = 0;
		}
	}

	public void setIsA(String string)
	{
		try
		{
			isA = Integer.parseInt(string);
		} catch (Exception e)
		{
			isA = 0;
		}
	}
	
	public void setDeadTime(int deadTime)
	{
		this.deadTime = deadTime;
	}
	
	public int getDeadTime()
	{
		return deadTime;
	}
	
	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
	
	public Socket getSocket()
	{
		return socket;
	}

	public void setDoPicking(boolean doPicking)
	{
		this.doPicking = doPicking;
	}
	
	public boolean getDoPicking()
	{
		return this.doPicking;
	}
	
	public void updatePickState()
	{
	}

	public void setLocation(String string, String string2)
	{
		try
		{
			X = Integer.parseInt(string);
			Y = Integer.parseInt(string2);
		} catch (Exception e)
		{
			X = 0;
			Y = 0;
		}
	}
	
	public boolean isLocationIn(int x, int y){
		if (X == x && Y == y)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public boolean beAttackRange(int x,int y,int dir)
	{
		/**
		 *    107
		 *    2L6
		 *    345
		 *    
		 *    L:檢測中心點 X,Y
		 *    dir:方向
		 *    檢測是否玩家在方向三格範圍內
		 */
		boolean temp = false;
		switch (dir)
		{
		case 0:
			temp = (temp ? true : isLocationIn(x + 1, y - 1));
			temp = (temp ? true : isLocationIn(x, y - 1));
			temp = (temp ? true : isLocationIn(x - 1, y - 1));
			break;
		case 1:
			temp = (temp ? true : isLocationIn(x, y - 1));
			temp = (temp ? true : isLocationIn(x - 1, y - 1));
			temp = (temp ? true : isLocationIn(x - 1, y));
			break;
		case 2:
			temp = (temp ? true : isLocationIn(x - 1, y - 1));
			temp = (temp ? true : isLocationIn(x - 1, y));
			temp = (temp ? true : isLocationIn(x - 1, y + 1));
			break;
		case 3:
			temp = (temp ? true : isLocationIn(x - 1, y));
			temp = (temp ? true : isLocationIn(x - 1, y + 1));
			temp = (temp ? true : isLocationIn(x, y + 1));
			break;
		case 4:
			temp = (temp ? true : isLocationIn(x - 1, y + 1));
			temp = (temp ? true : isLocationIn(x, y + 1));
			temp = (temp ? true : isLocationIn(x + 1, y + 1));
			break;
		case 5:
			temp = (temp ? true : isLocationIn(x, y + 1));
			temp = (temp ? true : isLocationIn(x + 1, y + 1));
			temp = (temp ? true : isLocationIn(x + 1, y));
			break;
		case 6:
			temp = (temp ? true : isLocationIn(x + 1, y + 1));
			temp = (temp ? true : isLocationIn(x + 1, y));
			temp = (temp ? true : isLocationIn(x + 1, y - 1));
			break;
		case 7:
			temp = (temp ? true : isLocationIn(x + 1, y));
			temp = (temp ? true : isLocationIn(x + 1, y - 1));
			temp = (temp ? true : isLocationIn(x, y - 1));
			break;

		default:
			break;
		}
		
		return temp;
	}
}
