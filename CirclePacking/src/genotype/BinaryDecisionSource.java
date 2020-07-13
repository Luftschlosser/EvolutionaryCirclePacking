package genotype;

public interface BinaryDecisionSource {

	public boolean nextDecision();
	public void incrementGeneration();
	public void reset();
}
