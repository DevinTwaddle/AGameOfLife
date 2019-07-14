package game;

public class Main implements Runnable {

	private GameOfLife mGame;
	private long mDelayTime = 75;

	// Upon initialising the program, create a new thread for updating.
	public static void main(String[] args) {
		new Thread(new Main()).start();
	}

	@Override
	public void run() {
		// If "Game" doesn't exist, then create it.
		if (mGame == null) {
			mGame = new GameOfLife();
			mGame.createGame();
		}

		// Constantly loop the game.
		while (true) {
			mGame.repaint();
			// Sleep method to artificially slow down the games looping.
			try {
				Thread.sleep(mDelayTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Update game.
			mGame.update();
		}

	}

}
