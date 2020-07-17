package genotype;

import java.util.ArrayList;

public class StrategyGenome extends Genome {
		
	public StrategyGenome(ArrayList<Float> radius, GaussianRangeSource angleMutationRange) {
		super(radius);
	}

	protected StrategyGenome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation) {
		super(radius, angles, permutation);
	}

	public StrategyGenome getNewChild(BinaryDecisionSource permutationMutation, BinaryDecisionSource angleMutation, GaussianRangeSource angleMutationRange) {
		ArrayList<Float> newAngles = new ArrayList<Float>(super.angles.size());
		for (Float angle : super.angles) {
			if (angleMutation.nextDecision()) {
				float newAngle = (float)(angle + angleMutationRange.nextRange());
				newAngle %= 360;
				newAngles.add(newAngle);
			}
			else {
				newAngles.add(angle);
			}
		}
		
		int n = super.permutation.size();
		ArrayList<Integer> newPermutation = new ArrayList<Integer>(n);
		for (int p : super.permutation) {
			newPermutation.add(p);
		}		
		for (int i = 0; i < n; i++) {
			if (permutationMutation.nextDecision()) {
				int otherIndex = super.random.nextInt(n);
				int other = newPermutation.get(otherIndex);
				int buf = newPermutation.get(i);
				newPermutation.set(i, other);
				newPermutation.set(otherIndex, buf);
			}
		}
		
		StrategyGenome child = new StrategyGenome(super.radius, newAngles, newPermutation);
		return child;
	}

	public StrategyGenome clone() {
		return new StrategyGenome(super.radius, super.angles, super.permutation);
	}
}
