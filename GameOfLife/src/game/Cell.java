package game;

import java.awt.geom.Rectangle2D;

public class Cell extends Rectangle2D.Double{
	
	private boolean mLiving, mVisible;
	
	public Cell(double x, double y, double size) {
		super(x, y, size, size);
		mVisible = true;
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
	public void setX(double x) {
		super.x = x;
	}
	public void setY(double y) {
		super.y = y;
	}
	public void setVisible(boolean visible) {
		mVisible = visible;
	}
	public boolean isVisible() {
		return mVisible;
	}
	
	@Override
	public boolean equals(Object obj) {
		Cell temp = null;
		if ((obj instanceof Cell))
			temp = (Cell) obj;
		return (temp.getX() == x && temp.getY() ==y);
		
	}
}
