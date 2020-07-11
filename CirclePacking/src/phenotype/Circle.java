package phenotype;

import java.awt.Graphics;

public class Circle {

	private float north, south, west, east, d;
	private int x, y;

	public Circle(float x, float y, float r) {
		this.north = y - r;
		this.south = y + r;
		this.west = x - r;
		this.east = x + r;
		this.d = 2 * r;
		this.x = (int) x;
		this.y = (int) y;
	}

	public void draw(Graphics g, int offsetX, int offsetY, boolean centerMark) {
		int top = (int) (north);
		int left = (int) (west);
		int d = (int) (this.d);
		g.drawOval(left + offsetX, top + offsetY, d, d);
		if (centerMark) {
			g.fillOval(x - 2 + offsetX, y - 2 + offsetY, 4, 4);
		}
	}

	public float getTop() {
		return north;
	}

	public float getBottom() {
		return south;
	}

	public float getLeft() {
		return west;
	}

	public float getRight() {
		return east;
	}

	public double getArea() {
		return Math.PI * (d / 2) * (d / 2);
	}
}
