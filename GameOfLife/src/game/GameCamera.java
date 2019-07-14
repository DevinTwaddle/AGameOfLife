package game;

public class GameCamera {
	
	private double x, y, originX, originY;
	private int mSpeed = 0;
	
	public GameCamera(int x, int y) {
		this.x = x;
		this.y = y;
		originX = x;
		originY = y;
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
	
	public void moveCameraX(double xValue) {
		x += xValue;
	}
	public void moveCameraY(double yValue) {
		y += yValue;
	}
	public void setSpeed(int speed) {
		mSpeed = speed;
	}
	public int getSpeed() {
		return mSpeed;
	}
}
