package phenotype;

import java.awt.Graphics;
import java.util.ArrayList;

public class Individual {

	private ArrayList<Circle> circles;
	private int circleCount;
	private Rectangle aabb = null;

	public Individual(int circleCount) {
		this.circleCount = circleCount;
		circles = new ArrayList<Circle>(circleCount);
	}

	public Individual(ArrayList<Circle> circles) {
		this.circleCount = circles.size();
		this.circles = circles;
	}

	public void add(Circle c) {
		circles.add(c);
	}

	public boolean isComplete() {
		return circleCount == circles.size();
	}

	private void computeAABB() {
		float top, bottom, left, right;
		top = bottom = left = right = 0;

		for (Circle c : circles) {
			if (c.getTop() < top)
				top = c.getTop();
			if (c.getBottom() > bottom)
				bottom = c.getBottom();
			if (c.getLeft() < left)
				left = c.getLeft();
			if (c.getRight() > right)
				right = c.getRight();
		}

		this.aabb = new Rectangle(left, top, right - left, bottom - top);
	}

	public void draw(Graphics g, int offsetX, int offsetY, boolean centerMark) {
		for (Circle c : circles) {
			c.draw(g, offsetX, offsetY, centerMark);
		}

		if (this.aabb == null) {
			computeAABB();
		}
		this.aabb.draw(g, offsetX, offsetY);
	}

	public float getScore() {
		if (this.aabb == null) {
			computeAABB();
		}
		return this.aabb.getArea();
	}

	public float getTotalArea() {
		double circleArea = 0;
		for (Circle c : circles) {
			circleArea += c.getArea();
		}
		return (float) circleArea;
	}

	public float getDensity() {
		return this.getTotalArea() / this.getScore();
	}
}
