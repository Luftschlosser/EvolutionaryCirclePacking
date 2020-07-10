package genotype;

import java.awt.Graphics;

public class Genome {
	
	private float[] radius;
	private float[] angles;
	private int[] permutation;
	
	public Genome(int n, float rmin, float rmax) {
		radius = new float[n];
		angles = new float[n-1];
		permutation = new int[n];
		
		for (int i = 0; i < n; i++) {
			permutation[i] = i;
			radius[i] = (float)((rmax-rmin)*Math.random()+rmin);
			if (i < (n-1))
				angles[i] = (float)(Math.random() * 360);
		}
	}
	
	public float getRadius(int index) {
		return radius[permutation[index]];
	}
	
	public float getAngle(int index) {
		if (index <= 0) {
			return 0;
		}
		else {
			return angles[index -1];
		}
	}
	
	public int getN() {
		return radius.length;
	}
	
	public void draw(Graphics g, int offsetX, int offsetY) {
		for(float a : angles) {
			double rad = Math.toRadians((double)a);
			int x = (int)(Math.cos(rad)*1000);
			int y = (int)(Math.sin(rad)*1000);
			g.drawLine(offsetX, offsetY, x+offsetX, y+offsetY);
		}
	}
}
