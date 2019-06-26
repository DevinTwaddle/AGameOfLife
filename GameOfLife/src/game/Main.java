package game;

public class Main implements Runnable {
	
	GameOfLife game;
	
	public static void main(String[] args) {
		new Thread(new Main()).start();
	}

	@Override
	public void run() {
		if (game == null) {
			game = new GameOfLife();
			game.createGame();
		}
		
		while(true)
		game.update();
	}

}
