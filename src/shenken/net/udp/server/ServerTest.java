package shenken.net.udp.server;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import shenken.net.udp.client.Client;
import shenken.net.udp.client.SendAction;

public class ServerTest
{
	private Server testServer;
	
	@Rule public TestName name = new TestName();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		System.out.println("Server Test Start!!\n");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		System.out.println("Server Test Done!!");
	}

	@Before
	public void setUp() throws Exception
	{
		System.out.println("Testing:" + name.getMethodName());
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (testServer != null)
		{
			testServer = null;
		}
		System.out.println("Testing End:" + name.getMethodName() + "\n");
	}
	
	@Test
	public void testInitUDPServer() throws InterruptedException
	{
		Server.initUDPServer();
		Server.getUDPUS().setRunFlag(false);
		Server.getInstance().join();
		assertNotNull("不應該是NULL", Server.getInstance());
		Server.stopUDPServer();
	}

	@Test
	public void testStopUDPServer() throws InterruptedException
	{
		Server.initUDPServer();
		Server.getUDPUS().setRunFlag(false);
		Server.getInstance().join();
		Server.stopUDPServer();
		assertNull("應該是NULL", Server.getInstance());
	}

	@Test
	public void testGetUDPUS() throws InterruptedException
	{
		Server.initUDPServer();
		Server.getUDPUS().setRunFlag(false);
		Server.getInstance().join();
		assertNotNull("不應該是NULL", Server.getUDPUS());
		Server.stopUDPServer();
	}

	@Test
	public void testGetInstance() throws InterruptedException
	{
		Server.initUDPServer();
		Server.getUDPUS().setRunFlag(false);
		Server.getInstance().join();
		assertNotNull("不應該是NULL", Server.getInstance());
		Server.stopUDPServer();
	}

	@Test
	public void testServerInt()
	{
		testServer = new Server(1212);
		assertNotNull("不應該是NULL", testServer);
	}

	@Test
	public void testServerIntReceiveAction()
	{
		ReceiveAction testAction = new ReceiveAction()
		{
			@Override
			public void run(String msg)
			{
			}
		};
		testServer = new Server(1212, testAction);
		assertNotNull(testServer);
	}

	@Test
	public void testServerIntReceiveActionInt()
	{
		ReceiveAction testAction = new ReceiveAction()
		{
			@Override
			public void run(String msg)
			{
			}
		};
		testServer = new Server(1212, testAction,8193);
		assertNotNull(testServer);
	}

	@Test(timeout = 2000)
	public void testRun() throws UnknownHostException
	{
		int port = 1212;
		final String sendString = "test";
		final Vector<String> reviceString = new Vector<String>();

		// Server
		testServer = new Server(port);
		ReceiveAction testAction = new ReceiveAction()
		{
			@Override
			public void run(String msg)
			{
				System.out.println("testRun:" + msg);
				testServer.setRunFlag(false);
				reviceString.add(msg);
			}
		};
		// testAction = new ServerMessageDecoder();
		testServer.setReceiveAction(testAction);
		Thread testS = new Thread(testServer);
		testS.start();
		// Server End

		// Client
		Client testClient = new Client("localhost", port);
		SendAction testSendAction = new SendAction()
		{
			@Override
			public String send()
			{
				testClient.setRunFlag(false);
				return sendString;
			}
		};
		testClient.setSendAction(testSendAction);
		new Thread(testClient).start();
		// Client End

		// Test Case
		try
		{
			testS.join();
			assertEquals("要是\"" + sendString + "\"", sendString, reviceString.get(0));
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// Test Case End

	}

	@Test
	public void testGetPort()
	{
		int port = 1212;
		testServer = new Server(port);
		assertEquals("Port要等於 " + port, port, testServer.getPort());
	}

	@Test
	public void testSetRunFlag()
	{
		int port = 1212;
		boolean flag = false;
		testServer = new Server(port);
		testServer.setRunFlag(flag);
		assertFalse("RunFlag 要等於 " + flag, testServer.getRunFlag());
	}

	@Test
	public void testGetRunFlag()
	{
		int port = 1212;
		testServer = new Server(port);
		assertTrue("RunFlag 要等於 true", testServer.getRunFlag());
	}

}
