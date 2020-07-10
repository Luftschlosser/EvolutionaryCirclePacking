package phenotype;

import java.awt.Graphics;

public class Rectangle {

	private float x, y, w, h;

	public Rectangle(float x, float y, float width, float height) {
		this.y = y;
		this.x = x;
		this.w = width;
		this.h = height;
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		g.drawRect((int) (x) + offsetX, (int) (y) + offsetY, (int) (w), (int) (h));
	}

	public float getArea() {
		return w * h;
	}

}
