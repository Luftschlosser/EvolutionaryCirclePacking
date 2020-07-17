package genotype;

import java.util.Random;

public class ConstantProbability implements BinaryDecisionSource {

	private final float probability;
	private Random random;

	public ConstantProbability(float probability) {
		this.probability = probability;
		this.random = new Random();
	}

	@Override
	public boolean nextDecision() {
		return this.probability >= random.nextFloat();
	}

	@Override
	public void incrementGeneration() {
	}

	@Override
	public void reset() {
	}

}
