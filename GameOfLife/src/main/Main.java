package main;

public class Main implements Runnable {
	
	GameScreen gameScreen = new GameScreen(800, 800, "A Game Of Life");
	
	public static void main (String[] args) {
		new Thread(new Main()).start();
	}
	
	
	@Override
	public void run() {
		System.out.println("Running");
		gameScreen.repaint();
	}
}
