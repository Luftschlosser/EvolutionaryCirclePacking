package genotype;

import java.util.Random;

public class ConvergingProbability implements BinaryDecisionSource, GaussianRangeSource {
	
	private final double initialRate;
	private final double dampingFactor;
	private double currentRate;
	private Random random;
	
	
	public ConvergingProbability(double initialRate, double dampingFactor) {
		this.initialRate = initialRate;
		this.dampingFactor = dampingFactor;
		this.currentRate = initialRate;
		this.random = new Random();
	}

	@Override
	public boolean nextDecision() {
		return this.currentRate >= random.nextDouble();
	}

	@Override
	public void incrementGeneration() {
		this.currentRate *= this.dampingFactor;
	}
	
	@Override
	public void reset() {
		this.currentRate = this.initialRate;
	}

	@Override
	public float nextRange() {
		return (float) (random.nextGaussian() * currentRate);
	}
}
