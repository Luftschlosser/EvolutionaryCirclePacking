package gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EvolutionCanvas extends JPanel {

	private static final int baseX = 100;

	private int generations;
	private float yStart, yStop;

	private ArrayList<Float> areas;

	public EvolutionCanvas() {
		this.reset(600, 0, 10000);
	}

	public void reset(int generations, float yStart, float yStop) {
		this.areas = new ArrayList<Float>(generations);
		this.generations = generations;
		this.yStart = yStart;
		this.yStop = yStop;
		repaint();
	}

	public void addValue(float area) {
		this.areas.add(area);
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		int topY = 80;
		int baseY = this.getHeight() - 80;
		int xScale = this.generations / (this.getWidth() - baseX - 80) + 1;
		float yScale = (baseY - topY) / (yStop - yStart);

		g.setColor(Color.GRAY);
		g.drawLine(baseX - 5, baseY, this.getWidth() - 60, baseY); // x
		g.drawLine(baseX, baseY + 5, baseX, topY); // y
		int xMark = baseX + this.generations / xScale;
		g.drawLine(xMark, baseY, xMark, baseY + 5);
		g.drawLine(baseX, topY, baseX - 5, topY);
		g.drawString(Integer.toString(Math.round(yStop)), 25, topY + 5);
		g.drawString(Integer.toString(Math.round(yStart)), 25, baseY + 5);
		g.drawString("0", baseX - 3, baseY + 30);
		g.drawString(Integer.toString(this.generations), baseX - 10 + this.generations / xScale, baseY + 30);

		g.setColor(Color.BLACK);
		int lastX = baseX;
		int lastY = topY;
		for (int x = 1, index = 0; index < this.generations; index += xScale, x++) {
			if (index < this.areas.size()) {
				int coordinate = baseY - Math.round((this.areas.get(index) - this.yStart) * yScale);
				g.drawLine(baseX + x, coordinate, lastX, lastY);
				lastX = baseX + x;
				lastY = coordinate;
			} else {
				break;
			}
		}
	}
}
