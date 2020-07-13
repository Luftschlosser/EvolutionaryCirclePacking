package genotype;

import java.util.ArrayList;

public class HillclimbGenome extends Genome {

	// initialize with random radius
	public HillclimbGenome(int n, float rmin, float rmax) {
		super(n, rmin, rmax);
	}

	// initialize with given radius
	public HillclimbGenome(ArrayList<Float> radius) {
		super(radius);
	}

	protected HillclimbGenome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation) {
		super(radius, angles, permutation);
	}

	public void mutatePermutation(BinaryDecisionSource permutate) {
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

	public void mutateAngles(BinaryDecisionSource mutate, GaussianRangeSource range) {
		int n = super.angles.size();
		for (int i = 0; i < n; i++) {
			if (mutate.nextDecision()) {
				float angle = super.angles.get(i);
				angle += range.nextRange();
				angle %= 360;
				super.angles.set(i, angle);
			}
		}
	}

	public HillclimbGenome clone() {
		return new HillclimbGenome(this.radius, this.angles, this.permutation);
	}
}
