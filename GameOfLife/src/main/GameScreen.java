package main;



public class GameScreen extends Screen {
	
	private Board board;
	
	public GameScreen(int width, int height, String title) {
		super(width, height, title);
		board = new Board(600, 600, 2, 40, 40, this);
		// TODO Auto-generated constructor stub
	}

	
}
