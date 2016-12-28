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
import shenken.server.cdc.castle.buff.CastleHPChangeEachBuff;
import shenken.server.cdc.castle.buff.HealthBuff;
import shenken.server.cdc.castle.buff.OpportunityBuff;
import shenken.server.cdc.castle.buff.ShieldBuffInvincible;
import shenken.server.cdc.castle.buff.ShieldBuffOne;
import shenken.server.cdc.castle.buff.ShieldBuffThree;
import shenken.server.cdc.castle.buff.ShieldBuffTwo;
import shenken.server.cdc.castle.buff.SpeedBuff;
import shenken.server.cdc.castle.buff.SpeedBuffThree;
import shenken.server.cdc.castle.buff.SpeedBuffTwo;
import shenken.server.cdc.castle.buff.StealBuff;
import shenken.server.cdc.castle.buff.TeleportToTargetBuff;
import shenken.server.cdc.job.Job;

public class Room implements Runnable, SendAction, IReceive
{
	public static final int refreshTime = 100;
	private int startPlayerCount = 2;
	private int maxPlayer = startPlayerCount;
	private int gameStartDelay = 3000;
	private int randomCreatItemCD = 5000;
	private int initCreatItemCount = 50;
	private int teleportTargetRange = 6;
	private boolean runFlag = true;
	private boolean gameStart = false;
	private boolean gameIsEnd = false;
	private Map map;
	private Vector<Player> playerTable;
	private Vector<String> boardcastQueue;
	private Vector<Castle> castles;
	private Color[] playerMapViewColor =
	{ Color.MAGENTA, Color.RED, Color.BLUE, Color.PINK, Color.CYAN, Color.YELLOW };

	public Room()
	{
		map = new Map();
		boardcastQueue = new Vector<>();
		castles = new Vector<>();
		playerTable = new Vector<>();
		// debugMode();
	}
	
	/**
	 * 初始化遊戲
	 */
	public void initRoom()
	{
		initCastles();
		initDefaultItem();
	}

	/**
	 * 初始產生道具
	 */
	private void initDefaultItem()
	{
		for (int i = 0; i < initCreatItemCount; i++)
		{
			randomCreatItem();
		}
	}

	/**
	 * 初始化城堡數據
	 */
	private void initCastles()
	{
		castles.add(new Castle(0));
		castles.add(new Castle(1));

		castles.get(0).getEnemyList().add(castles.get(1));
		castles.get(1).getEnemyList().add(castles.get(0));
		castles.get(0).getBuffList().add(new ShieldBuffInvincible(5000));
		castles.get(1).getBuffList().add(new ShieldBuffInvincible(5000));
	}

	/**
	 * 遊戲運作流程
	 */
	@Override
	public void run()
	{
		while (runFlag)
		{
			try
			{
				if (gameStart)
				{
					boardcastQueue.add(String.format("%s", Message.GAME_START));
					calcCastleStats();
					calcCastleDamage();
					calcGemaIsEnd();
					if (!gameIsEnd)
					{
						calcRandomCreatItem();
						boradcastPlayStats();
						boradcastCastleHPAndBuffList();
						boradcastLoaction();
						boardcastMapItemList();
					}
				} else
				{
					boardcastMapItemList();
					boradcastCastleHPAndBuffList();
					
					if (playerTable.size() == startPlayerCount)
					{
						if (checkAllPlayerSelectJob() || startPlayerCount == 0)
						{
							if (gameStartDelay < 1)
							{
								gameStart = true;
								boardcastQueue.add(String.format("%s", Message.GAME_START));
								System.out.println("Message.GAME_START");
							} else
							{
								for (Player player : playerTable)
								{
										boardcastQueue.add(String.format("%s,%s,%s,%s", Message.PLAYER_SELECT_OK, player.getID(),
												player.getTeamID(), player.getJobID()));
								}
								for (Player player : playerTable)
								{
									if (player.getIsSelectJob())
									{
										boardcastQueue.add(String.format("%s,%s,%s,%s,%s", Message.PLAYER_TELEPORT, player.getID(),
												player.getX(), player.getY(), player.getDir()));
									}
								}
								gameStartDelay -= refreshTime;
							}
						}
					}
				}

				Thread.sleep(refreshTime);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * 所以玩家角色選擇狀態
	 * 
	 * @return 真 or 假
	 */
	private boolean checkAllPlayerSelectJob()
	{
		int trueSlect = 0;
		for (Player player : playerTable)
		{
			if (player.getIsSelectJob())
			{
				trueSlect++;
			}
		}
		//System.out.println(String.format("Player Select %s %s", trueSlect,playerTable.size()));
		return (trueSlect == playerTable.size());
	}

	/**
	 * 計算城堡狀態及檢測可否攻擊
	 */

	private void calcCastleStats()
	{
		for (Castle castle : castles)
		{
			castle.update();
			castle.canAttack();
		}
	}

	/**
	 * 計算城堡受到的傷害
	 */
	private void calcCastleDamage()
	{
		for (Castle castle : castles)
		{
			castle.calcDamage();
		}
	}

	/**
	 * 檢測遊戲結束條件
	 */
	private void calcGemaIsEnd()
	{
		if (castles.get(0).getHP() < 1)
		{
			castles.get(0).setHP(0);
			boradcastCastleHPAndBuffList();
			boardcastQueue.add(String.format("%s,%s", Message.GAME_OVER, 1));
			runFlag = false;
			gameIsEnd = true;
		} else if (castles.get(1).getHP() < 1)
		{
			castles.get(1).setHP(0);
			boradcastCastleHPAndBuffList();
			boardcastQueue.add(String.format("%s,%s", Message.GAME_OVER, 0));
			runFlag = false;
			gameIsEnd = true;
		}
	}

	/**
	 * 計算城堡交換血量
	 * 
	 * @param aCastle
	 * @param bCastle
	 */
	private void calcCastleChangeHPEach(Castle aCastle, Castle bCastle)
	{
		int aCastleHP = aCastle.getHP();
		int bCastleHP = bCastle.getHP();
		aCastle.setHP(bCastleHP);
		bCastle.setHP(aCastleHP);
	}

	private void calcCastleStealBuff(Player player)
	{
		Castle selfTeam = castles.get(player.getTeamID());
		Castle enemyTeam = selfTeam.getEnemyList().get(0);
		Random random = new Random();
		int buffSize = enemyTeam.getBuffList().size();
		if (buffSize > 0)
		{
			int stealIndex = random.nextInt(enemyTeam.getBuffList().size());
			CastleBuff stealBuff = enemyTeam.getBuffList().get(stealIndex);
			enemyTeam.getBuffList().remove(stealIndex);
			stealBuff.setOwnID(player.getID());
			selfTeam.getBuffList().add(stealBuff);
		}
	}

	/**
	 * 計算兩個玩家攻擊並廣播被攻擊玩家
	 * 
	 * @param 攻擊方
	 * @param 被攻擊方
	 */
	private void calcPlayerAttackPlayer(Player attack, Player injury)
	{
		calcPlayerAttackPlayer(attack, injury, false);
	}

	/**
	 * 計算兩個玩家攻擊並廣播被攻擊玩家
	 * 
	 * @param 攻擊方
	 * @param 被攻擊方
	 * @param 友方傷害
	 */
	private void calcPlayerAttackPlayer(Player attack, Player injury, boolean allianceAttack)
	{
		if (injury.getID() != attack.getID() && (allianceAttack ? true : injury.getTeamID() != attack.getTeamID()))
		{
			if (injury.beAttackRange(attack.getX(), attack.getY(), attack.getDir()))
			{
				int HP = injury.getHP();
				int ATK = attack.getATK();
				injury.setHP(HP - ATK);
				boardcastQueue.add(String.format("%s,%s", Message.PLAYER_INJURY, injury.getID()));
			}
		}
	}

	/**
	 * 計算玩家撿取狀態及特殊立即性道具效果計算
	 * 
	 * @param player
	 */
	private void calcPlayerPickState(Player player)
	{
		if (player.getDoPicking())
		{
			if (player.getPickCD() < 1)
			{
				CastleBuff temp = map.getBlockItem(player.getX(), player.getY());
				if (temp instanceof OpportunityBuff)
				{
					temp = randomBuffGet();
				}
				if (temp instanceof CastleHPChangeEachBuff)
				{
					calcCastleChangeHPEach(castles.get(0), castles.get(1));
					boardcastQueue.addElement(String.format("%s", Message.CASTLE_BUFF_HP_CAHGE));
				}
				if (temp instanceof StealBuff)
				{
					calcCastleStealBuff(player);
				}
				if (temp instanceof TeleportToTargetBuff)
				{
					calcTeleportPlayerToRandomTarget(player);
				}
				if (temp != null)
				{
					temp.setOwnID(player.getID());
					castles.get(player.getTeamID()).getBuffList().add(temp);
				}
				map.removeBlockItem(player.getX(), player.getY());
				boardcastQueue.add(String.format("%s,%s", Message.PLAYER_ITEM_SUCCESS, player.getID()));
				boardcastQueue.add(String.format("%s,%s,%s", Message.MAP_ITEM_REMOVE, player.getX(), player.getY()));
				player.setDoPicking(false);
			} else
			{
				player.setPickCD(player.getPickCD() - Room.refreshTime);
				boardcastQueue.add(String.format("%s,%s,%s,%s", Message.PLAYER_ITEM_START, player.getID(),
						player.getPickCD(), player.getPickFullCD()));
			}
		}
	}

	/**
	 * 計算玩家血量及是否死亡，死亡則移除擁有的BUFF
	 * 
	 * @param player
	 */
	private void calcPlayerHPIsDead(Player player)
	{
		if (player.getHP() < 1 && !player.getIsDead())
		{
			player.setDeadTime(player.getReviveTime());
			player.setIsDead(true);
			Castle temp = castles.get(player.getTeamID());
			if (temp != null)
			{
				Iterator<CastleBuff> it = temp.getBuffList().iterator();
				while (it.hasNext())
				{
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

	/**
	 * 計算角色傳送位置
	 * 
	 * @param player
	 * @param x
	 * @param y
	 * @param dir
	 */
	private void calcTeleportPlayer(Player player, int x, int y, int dir)
	{
		// player.setLocation(x, y);
		// player.setDir(dir);
		boardcastQueue.add(String.format("%s,%s,%s,%s,%s", Message.PLAYER_TELEPORT, player.getID(), x, y, dir));
	}

	/**
	 * 傳送玩家至隨機其他玩家，如果只有一位玩家則隨機傳送地點
	 * 
	 * @param player
	 */
	private void calcTeleportPlayerToRandomTarget(Player player)
	{
		Random random = new Random();
		boolean needRandomLocation = true;
		boolean needRandomPlayer = true;
		int randomX = 0;
		int randomY = 0;
		int randomTargetID = 0;
		int targetX;
		int targetY;
		Player randomTarget;

		if (playerTable.size() < 2)
		{
			do
			{
				randomX = random.nextInt(Map.blockWidth);
				randomY = random.nextInt(Map.blockHeight);
				if (map.getBlockCanPass(randomX, randomY))
				{
					needRandomLocation = false;
				}
			} while (needRandomLocation);
		} else
		{
			do
			{
				int tempID = random.nextInt(playerTable.size());
				if (tempID != player.getID())
				{
					randomTargetID = tempID;
					needRandomPlayer = false;
				}
			} while (needRandomPlayer);

			randomTarget = playerTable.get(randomTargetID);
			targetX = randomTarget.getX();
			targetY = randomTarget.getY();

			do
			{
				randomX = targetX + (random.nextInt(teleportTargetRange) - (teleportTargetRange / 2));
				randomY = targetY + (random.nextInt(teleportTargetRange) - (teleportTargetRange / 2));
				if (map.getBlockCanPass(randomX, randomY))
				{
					needRandomLocation = false;
				}
			} while (needRandomLocation);
		}

		calcTeleportPlayer(player, randomX, randomY, 4);

	}

	/**
	 * 判斷隨機產生道具
	 */
	private void calcRandomCreatItem()
	{
		if (randomCreatItemCD < 1)
		{
			randomCreatItem();
			randomCreatItemCD += 5000;
		} else
		{
			randomCreatItemCD -= refreshTime;
		}
	}

	/**
	 * 在地圖上隨機產生一個道具
	 */
	private void randomCreatItem()
	{
		boolean needRandom = true;
		Random random = new Random();
		while (needRandom)
		{
			int randomX = random.nextInt(Map.blockWidth);
			int randomY = random.nextInt(Map.blockHeight);

			if (map.getBlockCanPass(randomX, randomY))
			{
				if (map.getBlockItem(randomX, randomY) == null)
				{
					switch (random.nextInt(25) + 1)
					{
					case 1:
						map.setBlockITem(randomX, randomY, new ATKBuff(5000));
						break;
					case 2:
						map.setBlockITem(randomX, randomY, new ATKBuffTwo(5000));
						break;
					case 3:
						map.setBlockITem(randomX, randomY, new ATKBuffThree(5000));
						break;
					case 4:
						map.setBlockITem(randomX, randomY, new SpeedBuff(5000));
						break;
					case 5:
						map.setBlockITem(randomX, randomY, new SpeedBuffTwo(5000));
						break;
					case 6:
						map.setBlockITem(randomX, randomY, new SpeedBuffThree(5000));
						break;
						
						
					case 7:
					case 8:
					case 9:
						map.setBlockITem(randomX, randomY, new ShieldBuffOne(30000));
						break;
						
					case 10:
					case 11:
					case 12:
						map.setBlockITem(randomX, randomY, new ShieldBuffTwo(30000));
						break;
						
					case 13:
					case 14:
						map.setBlockITem(randomX, randomY, new ShieldBuffThree(30000));
						break;
						
					case 15:
						map.setBlockITem(randomX, randomY, new CastleHPChangeEachBuff());
						break;
						
						
					case 16:
					case 17:
						map.setBlockITem(randomX, randomY, new HealthBuff());
						break;
						
					case 18:
						map.setBlockITem(randomX, randomY, new TeleportToTargetBuff());
						break;
					case 19:
					case 20:
						map.setBlockITem(randomX, randomY, new StealBuff());
						break;
						
					default:
						map.setBlockITem(randomX, randomY, new OpportunityBuff());
						break;
					}
					needRandom = false;
				}
			}
		}
	}

	/**
	 * 隨機建立一個城堡Buff
	 * 
	 * @return CastleBuff
	 */
	private CastleBuff randomBuffGet()
	{
		Random random = new Random();
		CastleBuff temp;
		switch (random.nextInt(13) + 1)
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
			temp = new HealthBuff();
			break;
		case 8:
			temp = new StealBuff();
			break;
		case 9:
			temp = new ShieldBuffOne(50000);
			break;
		case 10:
			temp = new ShieldBuffTwo(50000);
			break;
		case 11:
			temp = new ShieldBuffThree(50000);
			break;
		case 12:
			temp = new CastleHPChangeEachBuff();
			break;
		default:
			temp = new TeleportToTargetBuff();
			break;
		}
		return temp;
	}

	/**
	 * 廣播目前地圖上道具清單
	 */
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

	/**
	 * 計算並廣播目前玩家狀態
	 */
	private void boradcastPlayStats()
	{
		for (Player player : playerTable)
		{
			calcPlayerHPIsDead(player);
			calcPlayerPickState(player);
		}
	}

	/**
	 * 廣播玩家位置
	 */
	private void boradcastLoaction()
	{
		for (Player player : playerTable)
		{
			boardcastQueue.add(String.format("%d,%d,%d,%d,%d,%d", Message.PLAYER_LOCATION_CHANGE, player.getID(),
					player.getX(), player.getY(), player.getDir(), player.getIsA()));
		}
	}

	/**
	 * 廣播城堡血量及Buff列表
	 */
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

			boardcastQueue.add(String.format("%s,%s%s", Message.CASTLE_BUFF_LIST, castle.getID(), buffString));
		}
	}

	/**
	 * 接受玩家選擇角色
	 * 
	 * @param player
	 * @param packet
	 */
	private void doPlayerSelectChart(Player player, Packet packet)
	{
		Job newJob = null;

		if (packet != null)
		{
			newJob = Job.selectJob(packet.getArgs().get(1));
		}

		if (player != null)
		{
			if (newJob != null)
			{
				player.setJobID(Integer.valueOf(packet.getArgs().get(1)));
				player.setATK(newJob.getATK());
				player.setHP(newJob.getMaxHP());
				player.setMaxHP(newJob.getMaxHP());
				player.setPickFullCDDefault(newJob.getPickSpeed());
				player.setIsSelectJob(true);
			}
		}
	}

	/**
	 * 接受玩家撿取道具
	 * 
	 * @param player
	 * @param packet
	 */
	private void doPlayerPickItem(Player player, Packet packet)
	{
		try
		{
			if (player != null)
			{
				CastleBuff tempBuff = map.getBlockItem(player.getX(), player.getY());
				if (tempBuff != null)
				{
					if (tempBuff instanceof CastleHPChangeEachBuff)
					{
						player.setPickCD(player.getPickFullCDDefault() * 3);
						player.setPickFullCD(player.getPickFullCDDefault() * 3);
					} else
					{
						player.setPickCD(player.getPickFullCDDefault());
						player.setPickFullCD(player.getPickFullCDDefault());
					}
					player.setDoPicking(true);
				} else
				{
					map.removeBlockItem(player.getX(), player.getY());
					boardcastQueue
							.add(String.format("%s,%s,%s", Message.MAP_ITEM_REMOVE, player.getX(), player.getY()));
				}

			}
		} catch (Exception e)
		{
			System.out.println("PLAYER_GET_ITEM Error:");
			String.format("%s", packet.toString());
		}
	}

	/**
	 * 接受玩家攻擊
	 * 
	 * @param player
	 * @param packet
	 */
	private void doPlayerAttack(Player player, Packet packet)
	{
		try
		{
			if (player != null)
			{
				// System.out.println(String.format("Message.PLAYER_ATTACK: %s",
				// packet.toString()));
				boardcastQueue.add(String.format("%s,%s", Message.PLAYER_ATTACK, player.getID()));
				for (Player beAttack : playerTable)
				{
					calcPlayerAttackPlayer(player, beAttack);
				}
			}
		} catch (Exception e)
		{
			System.out.println("PLAYER_ATTACK Error:");
			String.format("%s", packet.toString());
		}
	}

	/**
	 * 接受玩家移動
	 * 
	 * @param player
	 * @param packet
	 */
	private void doPlayMove(Player player, Packet packet)
	{
		try
		{
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
	}

	/**
	 * 實作UDP廣播界面
	 */
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

	/**
	 * 實作TCP接收介面
	 */
	@Override
	public void onReceive(String msg)
	{
		if (gameIsEnd)
		{
			return;
		}
		MessageDecoder decoder = new MessageDecoder();
		Vector<Packet> packets = decoder.decode(msg);
		for (Packet packet : packets)
		{
			Player player = null;
			int playerID = Integer.parseInt(packet.getArgs().get(0));
			
			try
			{
				if (playerID >= playerTable.size())
				{
					return;
				}
				player = playerTable.get(playerID);
				switch (packet.getCommand())
				{
				case shenken.net.tcp.Message.PLAYER_MOVE:
					doPlayMove(player, packet);
					break;

				case shenken.net.tcp.Message.PLAYER_ATTACK:
					doPlayerAttack(player, packet);
					break;

				case shenken.net.tcp.Message.PLAYER_GET_ITEM:
					doPlayerPickItem(player, packet);
					break;

				case shenken.net.tcp.Message.PLAYER_SELECT_CHART:
					System.out.println(String.format("SELECT %s %s", packet.getArgs().get(0),packet.getArgs().get(1)));
					doPlayerSelectChart(player, packet);
					break;

				default:
					break;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

	/**
	 * 實作TCP接受新連線後動作
	 */
	@Override
	public void afterAccept(Socket client)
	{
		int playerID = TCPServer.getInstance().getClientCount() - 1;
		if (playerID >= maxPlayer)
		{
			try
			{
				TCPServer.getInstance().sendMsg(playerID, String.format("%s", shenken.net.tcp.Message.ROOM_FULL));
				client.close();
				TCPServer.getInstance().getClientTable().remove(playerID);
			} catch (IOException e)
			{
				System.out.println("kick player:room full");
				e.printStackTrace();
			}
		} else
		{
			Player newPlayer = new Player(playerID);
			playerTable.add(newPlayer);
			newPlayer.setSocket(client);
			newPlayer.setTeamID(playerID % 2);
			Random random = new Random();
//			if (newPlayer.getTeamID() == 0)
//			{
//				newPlayer.setLocation(random.nextInt(3) + 3, random.nextInt(3) + 51);
//			} else
//			{
//				newPlayer.setLocation(random.nextInt(3) + 49, random.nextInt(3) + 3);
//			}
			
			
			int randomX;
			int randomY;
			boolean needRandomLocation = true;
			do
			{
				randomX = random.nextInt(Map.blockWidth);
				randomY = random.nextInt(Map.blockHeight);
				if (map.getBlockCanPass(randomX, randomY))
				{
					needRandomLocation = false;
				}
			} while (needRandomLocation);
			newPlayer.setLocation(randomX, randomY);
			newPlayer.setDir(4);
			
			
			if (playerID < playerMapViewColor.length)
			{
				newPlayer.setMapViewColor(playerMapViewColor[playerID]);
			} else
			{
				newPlayer.setMapViewColor(Color.YELLOW);
			}
			TCPServer.getInstance().sendMsg(playerID, String.format("%s,%s,%s,%s,%s", shenken.net.tcp.Message.WELCOM,
					newPlayer.getID(), newPlayer.getTeamID(), newPlayer.getX(), newPlayer.getY()));

			Vector<PlayerIPaddress> iptable = new Vector<PlayerIPaddress>();
			try
			{
				iptable.add(new PlayerIPaddress("127.0.0.1",3011));
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
		System.out.println(String.format("PlayerTableCount:%s", playerTable.size()));
	}
	
	public void setMaxPlayer(int maxPlayer)
	{
		this.maxPlayer = maxPlayer;
		this.startPlayerCount = maxPlayer;
	}
	
	public void setInitCreatItemCount(int initCreatItemCount)
	{
		this.initCreatItemCount = initCreatItemCount;
	}
	
	public void setRandomCreatItemCD(int randomCreatItemCD)
	{
		this.randomCreatItemCD = randomCreatItemCD;
	}
	
	public void setDefaultCastleHP(int HP)
	{
		for (Castle castle : castles)
		{
			castle.setHP(HP);
		}
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

	public boolean getGameIsEnd()
	{
		return gameIsEnd;
	}

	public void setRunFlag(boolean runFlag)
	{
		this.runFlag = runFlag;
	}

	// private void debugMode()
	// {
	// playerTable.add(new Player(0));
	// playerTable.add(new Player(1));
	// playerTable.add(new Player(2));
	// playerTable.add(new Player(3));
	// playerTable.get(0).setLocation(15, 33);
	// playerTable.get(1).setLocation(8, 33);
	// playerTable.get(2).setLocation(30, 33);
	// playerTable.get(3).setLocation(50, 33);
	// playerTable.get(0).setMapViewColor(Color.blue);
	// playerTable.get(1).setMapViewColor(Color.green);
	// playerTable.get(2).setMapViewColor(Color.ORANGE);
	// playerTable.get(3).setMapViewColor(Color.red);
	// // castles.get(1).getBuffList().add(new ATKBuff(10000));
	//
	// // castles.get(0).setDefaultSpeed(200);
	// }

	// private void randomPlayerWalk()
	// {
	// Random ran = new Random();
	// for (int i = 1; i < playerTable.size(); i++)
	// {
	// Player player = playerTable.get(i);
	// boolean canPass = false;
	// while (!canPass)
	// {
	// switch (ran.nextInt(10))
	// {
	// case 0:
	// if (map.getBlockCanPass(player.getX() + 1, player.getY()))
	// {
	// player.setLocation(player.getX() + 1, player.getY());
	// player.setDir(4);
	// player.setIsA(1);
	// canPass = true;
	// }
	// break;
	// case 1:
	// if (map.getBlockCanPass(player.getX(), player.getY() + 1))
	// {
	// player.setLocation(player.getX(), player.getY() + 1);
	// player.setDir(6);
	// player.setIsA(1);
	// canPass = true;
	// }
	// break;
	// case 2:
	// if (map.getBlockCanPass(player.getX() - 1, player.getY()))
	// {
	// player.setLocation(player.getX() - 1, player.getY());
	// player.setDir(0);
	// player.setIsA(1);
	// canPass = true;
	// }
	// break;
	// case 3:
	// if (map.getBlockCanPass(player.getX(), player.getY() - 1))
	// {
	// player.setLocation(player.getX(), player.getY() - 1);
	// player.setDir(2);
	// player.setIsA(1);
	// canPass = true;
	// }
	// break;
	// default:
	// player.setIsA(0);
	// canPass = true;
	// break;
	// }
	// }
	// }
	// }
}
