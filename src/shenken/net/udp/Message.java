package shenken.net.udp;

public class Message
{ // int [.....]
	public static final int PLAYER_SELECT_OK = 1;// playerID,teamid,type(0=護衛,1=法師,3=平民)
	public static final int GAME_START = 2;// (棄用)
	public static final int PLAYER_HP_CHANGE = 3;// playerID,HP  ok
	public static final int PLAYER_ATTACK = 4;// playerID  ok
	public static final int PLAYER_INJURY = 5;// playerID  ok
	public static final int PLAYER_LOCATION_CHANGE = 6;// playerID,x,y,面向,isA  ok
	public static final int PLAYER_DEAD = 7;// playerID  ok
	public static final int PLAYER_REVIVE = 8;// playerID  ok
	public static final int PLAYER_ITEM_START = 9;// playerID,time,fullTime  ok
	public static final int PLAYER_ITEM_SUCCESS = 10;// playerID  ok
	public static final int PLAYER_ITEM_FAIL = 11;// playerID  ok
	public static final int MAP_ITEM_ADD = 12;// type,x,y  ok
	public static final int MAP_ITEM_REMOVE = 13;// x,y  ok
	public static final int CASTLE_HP_CHANGE = 14;// teamID,HP  ok
	public static final int CASTLE_BUFF_ADD = 15;// teamID,itemType,time (棄用)
	public static final int CASTLE_BUFF_REMOVE = 16;// teamID,itemType,time (棄用)
	public static final int GAME_OVER = 17;// winTeamID
	public static final int CASTLE_BUFF_LIST = 18;// teamID,[type,time,fullTime],,,  ok
	public static final int PLAYER_TELEPORT = 19;// playid,x,y,dir
	public static final int PLAYER_STATE_CHANGE = 20;// playid,ATK,SPEED,ATK_SPEED,MAXHP
	public static final int CASTLE_BUFF_HP_CAHGE = 21;//
	
	private Message()
	{

	}
}
