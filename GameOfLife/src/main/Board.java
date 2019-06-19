package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;

public class Board extends JPanel{

	private int mWidthPx, mHeightPx, mNoOfTilesWidth, mNoOfTilesHeight;
	private Tile[][] mTiles;
	private GameScreen mScreen;
	private int mTileOffset;
	
	public Board(int width, int height, int tileOffset, int noOfTilesWidth, int noOfTilesHeight, GameScreen screen) {
		mTiles = new Tile[noOfTilesWidth][noOfTilesHeight];
		mWidthPx = width;
		mHeightPx = height;
		mTileOffset = tileOffset;
		mNoOfTilesWidth = noOfTilesWidth;
		mNoOfTilesHeight = noOfTilesHeight;
		mScreen = screen;
		mScreen.setContentPane(this);
		CreateTiles(mTiles);
	}
	
	private void CreateTiles(Tile[][] tiles) {
		if (mNoOfTilesWidth == 0 || mNoOfTilesHeight == 0) 
			return;
		
		int minWidthHeight, maxNoOfTiles, tileSize = 0;
		float trueBoardSize = 1;
		Random rnd = new Random();
		
		minWidthHeight = Math.min(mWidthPx, mHeightPx);
		maxNoOfTiles = Math.max(mNoOfTilesHeight, mNoOfTilesWidth);
		trueBoardSize = minWidthHeight - (mTileOffset * maxNoOfTiles);

		while (tileSize * maxNoOfTiles >= minWidthHeight || tileSize == 0) {
			tileSize = (int) (Math.max(1, trueBoardSize) / Math.max(1, maxNoOfTiles--));
		}
		
		for (int x = 0; x < mNoOfTilesWidth; x++) {
			for (int y = 0; y < mNoOfTilesHeight; y++) {
				mTiles[x][y] = new Tile(tileSize, false, this);
				
				int chance = rnd.nextInt(10);
				if (chance > 5)
					mTiles[x][y].setAlive(true);
				
			}
		}
		
	}
	
	public void paintComponent(Graphics g) {
		int posX = 0, posY = 0;
		int tileSize = 0;
		
		if (mTiles != null) {
			for (int x = 0; x < mNoOfTilesWidth; x++) {
				for (int y = 0; y < mNoOfTilesHeight; y++) {
					if (mTiles[x][y].isAlive()) 
						g.setColor(Color.red);
					else
						g.setColor(Color.blue);
					
					tileSize = mTiles[x][y].getSize();
					
					g.fillRect(posX, posY, tileSize, tileSize);
					posX += tileSize + mTileOffset;
				}
				posX = 0;
				posY += tileSize + mTileOffset;
			}
		}
	}
}
