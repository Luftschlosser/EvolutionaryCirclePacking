package genotype;

import java.awt.Graphics;
import java.util.*;

public class Genome {

	protected Random random = new Random();

	protected ArrayList<Float> radius;
	protected ArrayList<Float> angles;
	protected ArrayList<Integer> permutation;

	public Genome(ArrayList<Float> radius) {
		this.radius = radius;
		int n = radius.size();
		angles = new ArrayList<Float>(n - 1);
		permutation = new ArrayList<Integer>(n);

		for (int i = 0; i < n; i++) {

			int permutationIndex;
			do {
				permutationIndex = random.nextInt(n);
			} while (permutation.contains(permutationIndex));
			permutation.add(permutationIndex);

			if (i > 0) {
				angles.add((float) (Math.random() * 360));
			}
		}
	}

	protected Genome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation) {
		this.radius = radius;

		this.angles = new ArrayList<Float>(angles.size());
		for (float f : angles)
			this.angles.add(f);

		this.permutation = new ArrayList<Integer>(permutation.size());
		for (int i : permutation)
			this.permutation.add(i);
	}

	public float getRadius(int index) {
		return radius.get(permutation.get(index));
	}

	public float getAngle(int index) {
		if (index <= 0) {
			return 0;
		} else {
			return angles.get(index - 1);
		}
	}

	public int getN() {
		return radius.size();
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		for (float a : angles) {
			double rad = Math.toRadians((double) a);
			int x = (int) (Math.cos(rad) * 1000);
			int y = (int) (Math.sin(rad) * 1000);
			g.drawLine(offsetX, offsetY, x + offsetX, y + offsetY);
		}
	}

	public Genome clone() {
		return new Genome(this.radius, this.angles, this.permutation);
	}
}
