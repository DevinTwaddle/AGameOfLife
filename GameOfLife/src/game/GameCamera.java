package game;

public class GameCamera {
	
	private double x, y;
	
	public GameCamera(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public GameCamera() {
		this(0, 0);
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public void setCameraPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
