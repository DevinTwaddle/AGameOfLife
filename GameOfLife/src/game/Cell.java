package game;

import java.awt.geom.Rectangle2D;

public class Cell extends Rectangle2D.Double{
	
	private boolean mLiving;
	
	public Cell(double x, double y, double size) {
		super(x, y, size, size);
	}
	
	public boolean isLiving() {
		return mLiving;
	}
	public void invertLivingState() {
		mLiving = !mLiving;
	}
	public void setLivingState(boolean state) {
		mLiving = state;
	}
	
}
