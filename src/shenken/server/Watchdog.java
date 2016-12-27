package shenken.server;

public class Watchdog implements Runnable
{
	private int refeshTime = 1000;
	private int newGameDelayTime = 1000;
	@Override
	public void run()
	{
		while (true)
		{
			if (Main.room != null)
			{
				if (Main.room.getGameIsEnd())
				{
					try
					{
						Main.closeGame();
						Thread.sleep(newGameDelayTime);
						Main.newGame();
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try
			{
				Thread.sleep(refeshTime);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
