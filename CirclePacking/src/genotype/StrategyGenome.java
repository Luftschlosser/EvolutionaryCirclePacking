package genotype;

import java.util.ArrayList;

public class StrategyGenome extends Genome {
	
	private double sigma;
	
	public StrategyGenome(ArrayList<Float> radius, double sigma) {
		super(radius);
		this.sigma = sigma;
	}

	protected StrategyGenome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation, double sigma) {
		super(radius, angles, permutation);
		this.sigma = sigma;
	}

	public StrategyGenome getNewChild(BinaryDecisionSource permutationMutation, BinaryDecisionSource angleMutation) {
		double newSigma = this.sigma * Math.exp(super.random.nextGaussian());
		
		ArrayList<Float> newAngles = new ArrayList<Float>(super.angles.size());
		for (Float angle : super.angles) {
			if (angleMutation.nextDecision()) {
				float newAngle = (float)(angle + newSigma * super.random.nextGaussian());
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
		
		StrategyGenome child = new StrategyGenome(super.radius, newAngles, newPermutation, newSigma);
		return child;
	}

	public StrategyGenome clone() {
		return new StrategyGenome(super.radius, super.angles, super.permutation, this.sigma);
	}
}
