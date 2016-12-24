package shenken.net.udp.client;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import shenken.net.PlayerIPaddress;
import shenken.net.udp.server.ReceiveAction;
import shenken.net.udp.server.Server;

public class ClientTest
{
	private Client client;
	
	@Rule public TestName name = new TestName();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		System.out.println("Client Test Start!!\n");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		System.out.println("Client Test Done!!");
	}

	@Before
	public void setUp() throws Exception
	{
		System.out.println("Testing:" + name.getMethodName());
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (client != null)
		{
			client = null;
		}
		System.out.println("Testing End:" + name.getMethodName() + "\n");
	}

	@Test
	public void testStartUDPBroadCast()
	{
		Client.startUDPBroadCast();
		assertNotNull(Client.getInstance());
		Client.stopUDPBroadCast();
	}

	@Test
	public void testStopUDPBroadCast()
	{
		Client.startUDPBroadCast();
		Client.stopUDPBroadCast();
		assertNull(Client.getInstance());
	}

	@Test
	public void testGetInstanceAndUDPBC()
	{
		Client.startUDPBroadCast();
		client = Client.getUDPBC();
		assertNotNull(client);
		Client.stopUDPBroadCast();
	}

	@Test
	public void testClient()
	{
		Client client;
		client = new Client();
		assertNotNull(client);
	}

	@Test
	public void testClientStringInt()
	{
		try
		{
			client = new Client("localhost", 1231);
			assertNotNull(client);
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testClientStringIntSendAction()
	{
		try
		{
			client = new Client("localhost", 1231,new SendAction()
			{
				@Override
				public String send()
				{
					// TODO Auto-generated method stub
					return "";
				}
			});
			assertNotNull(client);
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test(timeout=5000)
	public void testRun() throws UnknownHostException, InterruptedException
	{
		final Vector<Server> testServers = new Vector<>();
		final Vector<PlayerIPaddress> testPlayers = new Vector<>();
		final Vector<String> reviceString = new Vector<>();
		
		final String sendString = "test";
		
		testPlayers.add(new PlayerIPaddress("localhost", 1212));
		testPlayers.add(new PlayerIPaddress("localhost", 1213));
		testPlayers.add(new PlayerIPaddress("localhost", 1214));
		testPlayers.add(new PlayerIPaddress("localhost", 1215));
		
		// Server
		for (PlayerIPaddress player : testPlayers)
		{
			int portI = player.getUDPPort();
			Server testServer = new Server(portI);
			ReceiveAction testAction = new ReceiveAction()
			{
				@Override
				public void run(String msg)
				{
					System.out.println("testServer " + portI + " :" + msg);
					testServer.setRunFlag(false);
					reviceString.add(msg);
				}
			};
			testServer.setReceiveAction(testAction);
			testServers.add(testServer);
			new Thread(testServer).start();
		}
		// Server End
		Thread.sleep(500);
		// Client
		Client client = new Client();
		client.setClientIPTable(testPlayers);
		SendAction testSendAction = new SendAction()
		{
			@Override
			public String send()
			{
				client.setRunFlag(false);
				return sendString;
			}
		};
		client.setSendAction(testSendAction);
		Thread testC = new Thread(client);
		testC.start();
		// Client End
		
		// Test Case
		try
		{
			testC.join();
			Thread.sleep(200);
			assertEquals("只有 " + reviceString.size() + "個 Server收到，共要 " + testPlayers.size() + " 個", testPlayers.size(), reviceString.size());
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// Test Case End
	}

	@Test
	public void testSetRunFlag()
	{
		boolean test = false;
		client = new Client();
		client.setRunFlag(test);
		assertFalse(client.getRunFlag());
	}

	@Test
	public void testGetRunFlag()
	{
		client = new Client();
		assertTrue(client.getRunFlag());
	}

	@Test
	public void testSetAndGetSendAction()
	{
		client = new Client();
		SendAction testAction = new SendAction()
		{
			@Override
			public String send()
			{
				return "";
			}
		};
		client.setSendAction(testAction);
		assertEquals(testAction, client.getSendAction());
	}
	

	@Test
	public void testSetClientIPTable()
	{
		Vector<PlayerIPaddress> testTable = new Vector<>();
		try
		{
			PlayerIPaddress testPlayer = new PlayerIPaddress("127.0.0.1", 1212);
			testTable.add(testPlayer);

			client = new Client();
			client.setClientIPTable(testTable);
			
//			InetAddress testAddress = client.getClientIPTable().get(0);
//			assertEquals(testAddress.getHostAddress(), testPlayer.getAddress().getHostAddress());
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetClientIPTable() throws UnknownHostException
	{
		String testAddress = "127.0.0.1";
		client = new Client(testAddress, 1212);
//		assertEquals(client.getClientIPTable().get(0).getHostAddress().toString(), testAddress);
	}

}
