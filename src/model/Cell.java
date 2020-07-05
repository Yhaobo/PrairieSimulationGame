package model;

import java.awt.*;

public interface Cell {
    void draw(Graphics g, int x, int y, int size);

    void setLocation(int row, int column);

    Location getLocation();




//	private boolean alive = false;
//	
//	public void die() { alive = false; }
//	public void reborn() { alive = true; }
//	public boolean isAlive() { return alive; }
//	
//	public void draw(Graphics g, int x, int y, int size) {
//		g.drawRect(x, y, size, size);
//		if ( alive ) {
//			g.fillRect(x, y, size, size);
//		}
//	}
}
