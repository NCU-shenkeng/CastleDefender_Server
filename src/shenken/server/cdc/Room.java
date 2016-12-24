package shenken.server.cdc;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import shenken.net.MessageDecoder;
import shenken.net.Packet;
import shenken.net.PlayerIPaddress;
import shenken.net.tcp.IReceive;
import shenken.net.tcp.TCPServer;
import shenken.net.udp.Message;
import shenken.net.udp.client.Client;
import shenken.net.udp.client.SendAction;
import shenken.server.cdc.castle.buff.ATKBuff;
import shenken.server.cdc.castle.buff.ATKBuffThree;
import shenken.server.cdc.castle.buff.ATKBuffTwo;
import shenken.server.cdc.castle.buff.CastleBuff;
import shenken.server.cdc.castle.buff.OpportunityBuff;
import shenken.server.cdc.castle.buff.ShieldBuff;
import shenken.server.cdc.castle.buff.ShieldBuffOne;
import shenken.server.cdc.castle.buff.SpeedBuff;
import shenken.server.cdc.castle.buff.SpeedBuffThree;
import shenken.server.cdc.castle.buff.SpeedBuffTwo;

public class Room implements Runnable, SendAction, IReceive
{
	public static final int refreshTime = 100;
	private int startPlayerCount = 0;
	int randomItemCreatCD = 5000;
	boolean runFlag = true;
	boolean gameStart = false;
	Map map;
	Vector<Player> playerTable;
	Vector<String> boardcastQueue;
	Vector<Castle> castles;
	Color[] playerMapViewColor =
	{ Color.ORANGE, Color.RED, Color.BLUE, Color.PINK , Color.CYAN, Color.YELLOW };

	public Room()
	{
		map = new Map();
		boardcastQueue = new Vector<>();
		castles = new Vector<>();
		playerTable = new Vector<>();
		initCastles();
		initDefaultItem();
		// debugMode();
	}

	private void initDefaultItem()
	{
		for (int i = 0; i < 50; i++)
		{
			randomCreatItem();
		}
	}

	private void initCastles()
	{
		castles.add(new Castle(0));
		castles.add(new Castle(1));

		castles.get(0).getEnemyList().add(castles.get(1));
		castles.get(1).getEnemyList().add(castles.get(0));

	}

	private void debugMode()
	{
		playerTable.add(new Player(0));
		playerTable.add(new Player(1));
		playerTable.add(new Player(2));
		playerTable.add(new Player(3));
		playerTable.get(0).setLocation(15, 33);
		playerTable.get(1).setLocation(8, 33);
		playerTable.get(2).setLocation(30, 33);
		playerTable.get(3).setLocation(50, 33);
		playerTable.get(0).setMapViewColor(Color.blue);
		playerTable.get(1).setMapViewColor(Color.green);
		playerTable.get(2).setMapViewColor(Color.ORANGE);
		playerTable.get(3).setMapViewColor(Color.red);
		// castles.get(1).getBuffList().add(new ATKBuff(10000));

		// castles.get(0).setDefaultSpeed(200);
	}

	@Override
	public void run()
	{
		while (runFlag)
		{
			try
			{
				if (gameStart)
				{
					boradcastCastleHPAndBuffList();

					for (Castle castle : castles)
					{
						castle.update();
						castle.canAttack();
					}
					for (Castle castle : castles)
					{
						castle.calcDamage();
					}
					
					if (castles.get(0).getHP() < 1)
					{
						castles.get(0).setHP(0);
						boradcastCastleHPAndBuffList();
						boardcastQueue.add(String.format("%s,%s", Message.GAME_OVER,1));
						runFlag = false;
					} else if (castles.get(1).getHP() < 1) {
						castles.get(1).setHP(0);
						boradcastCastleHPAndBuffList();
						boardcastQueue.add(String.format("%s,%s", Message.GAME_OVER,0));
						runFlag = false;
					}
					
					if (randomItemCreatCD < 1)
					{
						randomCreatItem();
						randomItemCreatCD += 5000;
					} else
					{
						randomItemCreatCD -= refreshTime;
					}

					boradcastPlayStats();
					boradcastLoaction();
					boardcastMapItemList();
				} else
				{
					if (playerTable.size() == startPlayerCount)
					{
						gameStart = true;
					}
				}

				Thread.sleep(refreshTime);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	private void randomCreatItem()
	{
		boolean pass = true;
		Random random = new Random();
		while (pass)
		{
			int randomX = random.nextInt(Map.blockWidth);
			int randomY = random.nextInt(Map.blockHeight);

			if (map.getBlockCanPass(randomX, randomY))
			{
				if (map.getBlockItem(randomX, randomY) == null)
				{
					switch (random.nextInt(12)+1)
					{
					case 1:
						map.setBlockITem(randomX, randomY, new ATKBuff(50000));
						break;
					case 2:
						map.setBlockITem(randomX, randomY, new ATKBuffTwo(50000));
						break;
					case 3:
						map.setBlockITem(randomX, randomY, new ATKBuffThree(50000));
						break;
					case 4:
						map.setBlockITem(randomX, randomY, new SpeedBuff(50000));
						break;
					case 5:
						map.setBlockITem(randomX, randomY, new SpeedBuffTwo(50000));
						break;
					case 6:
						map.setBlockITem(randomX, randomY, new SpeedBuffThree(50000));
						break;
					case 7:
					case 8:
					case 9:
						map.setBlockITem(randomX, randomY, new ShieldBuffOne(1200000));
						break;
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
						map.setBlockITem(randomX, randomY, new OpportunityBuff());
						break;
					default:
						break;
					}
					pass = false;
				}
			}

		}
	}

	private void boardcastMapItemList()
	{
		for (int i = 0; i < map.getMapItem().length; i++)
		{
			for (int j = 0; j < map.getMapItem()[i].length; j++)
			{
				if (map.getBlockItem(i, j) != null)
				{
					CastleBuff temp = map.getMapItem()[i][j];
					boardcastQueue.add(String.format("%s,%s,%s,%s", Message.MAP_ITEM_ADD, temp.getTypeID(), i, j));
				}
			}
		}
	}

	@Override
	public String send()
	{
		String sendMessage = "";
		try
		{
			synchronized (boardcastQueue)
			{
				for (String string : boardcastQueue)
				{
					
						sendMessage += string + "\n";
					
				}
			}
			boardcastQueue.clear();
		} catch (Exception e)
		{
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			String strDate = sdFormat.format(date);
			System.out.println(strDate);
			e.printStackTrace();
		}
		// System.out.println(sendMessage);
		return sendMessage;
	}

	public Vector<Player> getPlayerTable()
	{
		return playerTable;
	}

	public Map getMap()
	{
		return map;
	}

	public Vector<Castle> getCastles()
	{
		return castles;
	}

	public void setRunFlag(boolean runFlag)
	{
		this.runFlag = runFlag;
	}

	private void boradcastPlayStats()
	{
		for (Player player : playerTable)
		{
			playerCalcHPIsDead(player);
			playerupdatePickState(player);
		}
	}

	private void boradcastLoaction()
	{
		for (Player player : playerTable)
		{
			boardcastQueue.add(String.format("%d,%d,%d,%d,%d,%d", Message.PLAYER_LOCATION_CHANGE, player.getID(),
					player.getX(), player.getY(), player.getDir(), player.getIsA()));
		}
	}

	private void boradcastCastleHPAndBuffList()
	{
		for (Castle castle : castles)
		{
			boardcastQueue.add(String.format("%d,%d,%d", Message.CASTLE_HP_CHANGE, castle.getID(), castle.getHP()));
			String buffString = "";
			for (CastleBuff buff : castle.getBuffList())
			{
				buffString += String.format(",%s,%s,%s", buff.getTypeID(), buff.getTime(), buff.getFullTime());
			}
			//System.out.println(String.format("Buff list %s,%s%s", Message.CASTLE_BUFF_LIST, castle.getID(), buffString));
			boardcastQueue.add(String.format("%s,%s%s", Message.CASTLE_BUFF_LIST, castle.getID(), buffString));
		}
	}

	private void playerupdatePickState(Player player)
	{
		//System.out.println("playerupdatePickState");
		if (player.getDoPicking())
		{
			//System.out.println(String.format("pick item %s %s %s",player.getDoPicking(),player.getPickCD(),player.getPickFullCD()));
			if (player.getPickCD() < 1)
			{
				CastleBuff temp = map.getBlockItem(player.getX(), player.getY());
				if (temp instanceof OpportunityBuff)
				{
					temp = randomBuffGet();
				}
				if (temp != null)
				{
					temp.setOwnID(player.getID());
					castles.get(player.getTeamID()).getBuffList().add(temp);
					System.out.println(String.format("player %s team %s pick buff %s",player.getID(),player.getTeamID(),temp.getTypeID()));
				}
				map.removeBlockItem(player.getX(), player.getY());
				boardcastQueue.add(String.format("%s,%s", Message.PLAYER_ITEM_SUCCESS,player.getID()));
				boardcastQueue.add(String.format("%s,%s,%s", Message.MAP_ITEM_REMOVE, player.getX(), player.getY()));
				player.setDoPicking(false);
			} else
			{
				player.setPickCD(player.getPickCD() - Room.refreshTime);
//				boardcastQueue.add(String.format("%s,%s,%s,%s", Message.PLAYER_ITEM_START, player.getID(),
//						((player.getPickCD() < 1000 && player.getPickCD() > 100)?1000:player.getPickCD()), player.getPickFullCD()));
				boardcastQueue.add(String.format("%s,%s,%s,%s", Message.PLAYER_ITEM_START, player.getID(),
						player.getPickCD(), player.getPickFullCD()));
			}
		}
	}
	
	private CastleBuff randomBuffGet()
	{
		Random random = new Random();
		CastleBuff temp;
		switch (random.nextInt(9)+1)
		{
		case 1:
			temp = new ATKBuff(50000);
			break;
		case 2:
			temp = new ATKBuffTwo(50000);
			break;
		case 3:
			temp = new ATKBuffThree(50000);
			break;
		case 4:
			temp = new SpeedBuff(50000);
			break;
		case 5:
			temp = new SpeedBuffTwo(50000);
			break;
		case 6:
			temp = new SpeedBuffThree(50000);
			break;
		case 7:
		case 8:
		case 9:
			temp = new ShieldBuffOne(1200000);
			break;
		default:
			temp = null;
			break;
		}
		return temp;
	}

	private void playerAttackPlayer(Player a, Player b)
	{
		if (b.getID() != a.getID())
		{
			if (b.beAttackRange(a.getX(), a.getY(), a.getDir()))
			{
				int HP = b.getHP();
				int ATK = a.getATK();
				b.setHP(HP - ATK);
				boardcastQueue.add(String.format("%s,%s", Message.PLAYER_INJURY, b.getID()));
			}
		}
	}

	private void playerCalcHPIsDead(Player player)
	{
		if (player.getHP() < 1 && !player.getIsDead())
		{
			player.setDeadTime(5000);
			player.setIsDead(true);
			Castle temp = castles.get(player.getTeamID());
			if (temp != null)
			{
				Iterator<CastleBuff> it = temp.getBuffList().iterator();
				while (it.hasNext()) {
					CastleBuff castleBuff = it.next();
					if (castleBuff.getOwnID() == player.getID())
					{
						it.remove();
					}
				}
			}
			boardcastQueue.add(String.format("%s,%s,%s", Message.PLAYER_DEAD, player.getID(), player.getDeadTime()));
		} else if (player.getHP() < 1 && player.getIsDead() && player.getDeadTime() < 1)
		{
			player.setIsDead(false);
			player.setDeadTime(0);
			player.setHP(player.getMaxHP());
			boardcastQueue.add(String.format("%s,%s", Message.PLAYER_REVIVE, player.getID()));
			boardcastQueue.add(String.format("%s,%s,%s", Message.PLAYER_HP_CHANGE, player.getID(), player.getHP()));
		} else if (player.getIsDead())
		{
			player.setDeadTime(player.getDeadTime() - Room.refreshTime);
			boardcastQueue.add(String.format("%s,%s,%s", Message.PLAYER_DEAD, player.getID(), player.getDeadTime()));
		} else
		{
			boardcastQueue.add(String.format("%s,%s,%s", Message.PLAYER_HP_CHANGE, player.getID(), player.getHP()));
		}
	}

	@Override
	public void onReceive(String msg)
	{
		MessageDecoder decoder = new MessageDecoder();
		Vector<Packet> packets = decoder.decode(msg);
		for (Packet packet : packets)
		{
			Player player = null;
			switch (packet.getCommand())
			{
			case shenken.net.tcp.Message.PLAYER_MOVE:
				try
				{
					player = playerTable.get(Integer.parseInt(packet.getArgs().get(0)));
					int newX = Integer.valueOf(packet.getArgs().get(1));
					int newY = Integer.valueOf(packet.getArgs().get(2));
					int newDir = Integer.valueOf(packet.getArgs().get(3));
					int newIsA = Integer.valueOf(packet.getArgs().get(4));
					if (player != null)
					{
						if (player.getDoPicking())
						{
							if (player.getX() != newX || player.getY() != newY)
							{
								player.setDoPicking(false);
								boardcastQueue.add(String.format("%s,%s", Message.PLAYER_ITEM_FAIL, player.getID()));
							}
						}
						player.setLocation(newX, newY);
						player.setDir(newDir);
						player.setIsA(newIsA);
					}
				} catch (Exception e)
				{
					System.out.println("PLAYER_MOVE Error:");
					String.format("%s", packet.toString());
				}
				break;

			case shenken.net.tcp.Message.PLAYER_ATTACK:
				try
				{
					player = playerTable.get(Integer.parseInt(packet.getArgs().get(0)));
					if (player != null)
					{
//						System.out.println(String.format("Message.PLAYER_ATTACK: %s", packet.toString()));
						boardcastQueue.add(String.format("%s,%s", Message.PLAYER_ATTACK, player.getID()));
						for (Player beAttack : playerTable)
						{
							playerAttackPlayer(player, beAttack);
						}
					}
					if (player != null)
					{
						CastleBuff tempBuff = map.getBlockItem(player.getX(), player.getY());
						if (tempBuff != null)
						{
							player.setPickCD(player.getPickFullCD());
							player.setDoPicking(true);
						} else
						{
							map.removeBlockItem(player.getX(), player.getY());
							boardcastQueue.add(
									String.format("%s,%s,%s", Message.MAP_ITEM_REMOVE, player.getX(), player.getY()));
						}

					}
				} catch (Exception e)
				{
					System.out.println("PLAYER_ATTACK Error:");
					String.format("%s", packet.toString());
				}
				break;

			case shenken.net.tcp.Message.PLAYER_GET_ITEM:
				try
				{
					player = playerTable.get(Integer.parseInt(packet.getArgs().get(0)));
					if (player != null)
					{
						CastleBuff tempBuff = map.getBlockItem(player.getX(), player.getY());
						if (tempBuff != null)
						{
							player.setPickCD(player.getPickFullCD());
							player.setDoPicking(true);
						} else
						{
							map.removeBlockItem(player.getX(), player.getY());
							boardcastQueue.add(
									String.format("%s,%s,%s", Message.MAP_ITEM_REMOVE, player.getX(), player.getY()));
						}

					}
				} catch (Exception e)
				{
					System.out.println("PLAYER_GET_ITEM Error:");
					String.format("%s", packet.toString());
				}
				break;

			default:
				break;
			}
		}

	}

	@Override
	public void afterAccept(Socket client)
	{
		if (playerTable.size() > 3)
		{
			try
			{
				client.close();
			} catch (IOException e)
			{
				System.out.println("kick player:room full");
				e.printStackTrace();
			}
		} else
		{
			int playerID = TCPServer.getInstance().getClientCount() - 1;
			Player newPlayer = new Player(playerID);
			playerTable.add(newPlayer);
			newPlayer.setSocket(client);
			newPlayer.setTeamID(playerID % 2);
			Random random = new Random();
			if (newPlayer.getTeamID() == 1)
			{
				newPlayer.setLocation(random.nextInt(3)+3, random.nextInt(3)+51);
			}else
			{

				newPlayer.setLocation(random.nextInt(3)+49, random.nextInt(3)+3);
			}
			if (playerID < playerMapViewColor.length)
			{
				newPlayer.setMapViewColor(playerMapViewColor[playerID]);
			} else
			{
				newPlayer.setMapViewColor(Color.YELLOW);
			}
			TCPServer.getInstance().sendMsg(playerID,
					String.format("%s,%s,%s,%s,%s", shenken.net.tcp.Message.WELCOM, newPlayer.getID(), newPlayer.getTeamID(),newPlayer.getX(),newPlayer.getY()));

			Vector<PlayerIPaddress> iptable = new Vector<PlayerIPaddress>();
			try
			{
				iptable.add(new PlayerIPaddress("127.0.0.1"));
				for (Player player : playerTable)
				{
					iptable.add(new PlayerIPaddress(player.getSocket().getInetAddress().getHostAddress()));
				}
				iptable.add(new PlayerIPaddress(client.getInetAddress().getHostAddress()));
				Client.getUDPBC().setClientIPTable(iptable);
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	private void randomPlayerWalk()
	{
		Random ran = new Random();
		for (int i = 1; i < playerTable.size(); i++)
		{
			Player player = playerTable.get(i);
			boolean canPass = false;
			while (!canPass)
			{
				switch (ran.nextInt(10))
				{
				case 0:
					if (map.getBlockCanPass(player.getX() + 1, player.getY()))
					{
						player.setLocation(player.getX() + 1, player.getY());
						player.setDir(4);
						player.setIsA(1);
						canPass = true;
					}
					break;
				case 1:
					if (map.getBlockCanPass(player.getX(), player.getY() + 1))
					{
						player.setLocation(player.getX(), player.getY() + 1);
						player.setDir(6);
						player.setIsA(1);
						canPass = true;
					}
					break;
				case 2:
					if (map.getBlockCanPass(player.getX() - 1, player.getY()))
					{
						player.setLocation(player.getX() - 1, player.getY());
						player.setDir(0);
						player.setIsA(1);
						canPass = true;
					}
					break;
				case 3:
					if (map.getBlockCanPass(player.getX(), player.getY() - 1))
					{
						player.setLocation(player.getX(), player.getY() - 1);
						player.setDir(2);
						player.setIsA(1);
						canPass = true;
					}
					break;
				default:
					player.setIsA(0);
					canPass = true;
					break;
				}
			}
		}
	}
}
