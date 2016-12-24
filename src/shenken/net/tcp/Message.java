package shenken.net.tcp;

public class Message
{ // int [.....]
	public static final int PLAYER_SELECT_CHART = 1;// jobID
	public static final int LOAD_DONE = 2;// playID
	public static final int PLAYER_MOVE = 3;// playID:x:y:dir:isA
	public static final int PLAYER_ATTACK = 4;// playID
	public static final int PLAYER_GET_ITEM = 5;// playID
	public static final int HEART_BEAT = 6;// (±ó¥Î)
	public static final int WELCOM = 7;//playID,teamID
	
	
	private Message()
	{
		
	}
}
