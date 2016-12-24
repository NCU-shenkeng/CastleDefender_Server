package shenken.server.cdc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

import shenken.server.cdc.castle.buff.CastleBuff;


public class Map
{
	public static final int blockWidth = 60;
	public static final int blockHeight = 56;
	private boolean[][] block = new boolean[blockWidth][blockHeight];
	private CastleBuff[][] mapItem = new CastleBuff[blockWidth][blockHeight];
	
	public Map()
	{
		FileReader fr;
		try
		{
			fr = new FileReader("mapfile.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			int i = 0;
			int j = 0;
			while ((line = br.readLine()) != null)
			{
				for ( i = 0; i < line.length(); i++)
				{
					System.out.println("[" + i + "," + j + "] = " + line.charAt(i));
					
					int blocktype = Character.getNumericValue(line.charAt(i));
					
					if (blocktype == 1)
						block[i][j] = true;
					else
						block[i][j] = false;
				}
				j ++;
			}
			for (int mi = 0; i < blockWidth; i++)
			{
				for (int mj = 0; j < blockHeight; j++)
				{
					mapItem[mi][mj] = null;
				}
			}
			br.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean getBlockCanPass(int x, int y){
		if (x < 0 || y < 0 || x > block.length || y > block[0].length)
		{
			return false;
		}
		return block[x][y];
	}
	
	public CastleBuff getBlockItem(int x, int y)
	{
		CastleBuff temp = null;
		
		try
		{
			temp = mapItem[x][y];
		} catch (Exception e)
		{
			temp = null;
			e.printStackTrace();
		}
		
		return temp;
	}
	
	public void removeBlockItem(int x,int y)
	{
		try
		{
			mapItem[x][y] = null;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public CastleBuff[][] getMapItem()
	{
		return mapItem;
	}
	
	public void setBlockITem(int x, int y, CastleBuff buff)
	{
		try
		{
			mapItem[x][y] = buff;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean[][] getBlock()
	{
		return block;
	}
}
