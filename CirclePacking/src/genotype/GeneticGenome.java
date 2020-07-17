package genotype;

import java.util.ArrayList;

public class GeneticGenome extends Genome {

	public GeneticGenome(ArrayList<Float> radius) {
		super(radius);
	}

	protected GeneticGenome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation) {
		super(radius, angles, permutation);
	}

	public GeneticGenome recombineByOrder(GeneticGenome other) { // not suitable as it seems
		int n = super.getN();
		ArrayList<Float> newAngles = new ArrayList<Float>(super.angles.size());
		ArrayList<Integer> newPermutation = new ArrayList<Integer>(super.permutation.size());

		// 1-point-crossover
		int crosspoint = Math.abs((super.random.nextInt() % (n - 1)) + 1);

		// Ordnungsrekombination
		int i = 0;
		while (i < crosspoint) {
			newPermutation.add(super.permutation.get(i));
			if (i > 0) {
				newAngles.add(super.angles.get(i - 1));
			}
			i++;
		}

		int k = 0;
		while (i < n) {

			int permutationBuf = 0;

			for (; k < n; k++) {
				permutationBuf = other.permutation.get(k);
				if (!newPermutation.contains(permutationBuf)) {
					break;
				}
			}

			newPermutation.add(permutationBuf);
			if (i > 0) {
				if (k > 0 && k < n) {
					newAngles.add(other.angles.get(k - 1));
				} else {
					newAngles.add(other.angles.get(i - 1));
				}
			}
			i++;
		}

		return new GeneticGenome(super.radius, newAngles, newPermutation);
	}

	public GeneticGenome recombineByAngle(GeneticGenome other) {
		int n = super.getN();
		ArrayList<Float> newAngles = new ArrayList<Float>(super.angles.size());
		ArrayList<Integer> newPermutation = new ArrayList<Integer>(super.permutation.size());

		// select angle range
		float min = Math.abs(this.random.nextFloat() % 360);
		float max = Math.abs(this.random.nextFloat() % 360);
		if (min > max) {
			float buf = min;
			min = max;
			max = buf;
		}

		// Parent 1
		newPermutation.add(super.permutation.get(0));
		for (int i = 1; i < n; i++) {
			float angle = super.angles.get(i - 1);
			if (angle >= min && angle <= max) {
				newAngles.add(angle);
				newPermutation.add(super.permutation.get(i));
			} else {
				newAngles.add(null);
				newPermutation.add(null);
			}
		}

		// Parent 2
		for (int i = 1, k = 0; i < n; i++) {
			if (newPermutation.get(i) == null) {
				int permutation;
				do {
					permutation = other.permutation.get(k++);
				} while (newPermutation.contains(permutation));
				newPermutation.set(i, permutation);
				newAngles.set(i - 1, (k - 2) > 0 ? other.angles.get(k - 2) : super.random.nextFloat() * 360);
			}
		}

		return new GeneticGenome(super.radius, newAngles, newPermutation);
	}

	public void mutatePermutationByMove() { // not suitable as it seems
		int n = super.permutation.size();
		int from = Math.abs(super.random.nextInt() % n);
		int to = Math.abs(super.random.nextInt() % n);

		if (from > to) { // rotate forward
			int buf = super.permutation.get(from);
			int i = from - 1;
			while (i >= to) {
				super.permutation.set(i + 1, super.permutation.get(i));
				i--;
			}
			super.permutation.set(i + 1, buf);
		} else if (from < to) { // rotate backwards
			int buf = super.permutation.get(from);
			int i = from + 1;
			while (i <= to) {
				super.permutation.set(i - 1, super.permutation.get(i));
				i++;
			}
			super.permutation.set(i - 1, buf);
		}
		return;
	}

	public void mutatePermutationBySwitch(BinaryDecisionSource permutate) {
		int n = super.permutation.size();
		for (int i = 0; i < n; i++) {
			if (permutate.nextDecision()) {
				int otherIndex = super.random.nextInt(n);
				int other = super.permutation.get(otherIndex);
				int buf = super.permutation.get(i);
				super.permutation.set(i, other);
				super.permutation.set(otherIndex, buf);
			}
		}
	}

	public void mutateAngle(GaussianRangeSource range) {
		int n = super.angles.size();
		int i = Math.abs(super.random.nextInt() % n);
		float angle = super.angles.get(i);
		angle += range.nextRange();
		angle %= 360;
		if (angle < 0) {
			angle += 360;
		}
		super.angles.set(i, angle);
	}

	public GeneticGenome clone() {
		return new GeneticGenome(super.radius, super.angles, super.permutation);
	}
}
