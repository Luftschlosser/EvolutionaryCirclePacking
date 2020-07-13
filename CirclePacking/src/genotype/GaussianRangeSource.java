package genotype;

public interface GaussianRangeSource {

	public float nextRange();
	public void incrementGeneration();
	public void reset();
}
