package main;

public class Tile {
	
	private Board mBoard;
	private int mSize;
	private boolean mAlive;
	
	public Tile(int size, boolean alive, Board board) {
		mSize = size;
		mAlive = alive;
		mBoard = board;
	}
	
	public boolean isAlive() {
		return mAlive;
	}
	
	public void setAlive(boolean alive) {
		mAlive = alive;
	}

	public int getSize() {
		return mSize;
	}
}
