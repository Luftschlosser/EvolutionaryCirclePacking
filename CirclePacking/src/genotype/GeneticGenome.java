package genotype;

import java.util.ArrayList;


public class GeneticGenome extends Genome {


	public GeneticGenome(ArrayList<Float> radius) {
		super(radius);
	}
	
	protected GeneticGenome(ArrayList<Float> radius, ArrayList<Float> angles, ArrayList<Integer> permutation) {
		super(radius, angles, permutation);
	}
	
	public GeneticGenome recombine(GeneticGenome other) {
		int n = super.getN();
		ArrayList<Float> newAngles = new ArrayList<Float>(super.angles.size());
		ArrayList<Integer> newPermutation = new ArrayList<Integer>(super.permutation.size());
		
		//1-point-crossover
		int crosspoint = Math.abs((super.random.nextInt() % (n-1)) + 1);
		
		//Ordnungsrekombination
		int i = 0;
		while (i < crosspoint) {
			newPermutation.add(super.permutation.get(i));
			if (i > 0) {
				newAngles.add(super.angles.get(i-1));
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
					newAngles.add(other.angles.get(k-1));
				}
				else {
					newAngles.add(other.angles.get(i-1));
				}
			}
			i++;
		}
		
		return new GeneticGenome(super.radius, newAngles, newPermutation);		
	}
	
	public void mutatePermutation() {
		int n = super.permutation.size();
		int from = Math.abs(super.random.nextInt() % n);
		int to = Math.abs(super.random.nextInt() % n);
		
		if (from > to) { //rotate forward
			int buf = super.permutation.get(from);
			int i = from-1;
			while (i >= to) {
				super.permutation.set(i+1, super.permutation.get(i));
				i--;
			}
			super.permutation.set(i+1, buf);
		}
		else if (from < to) { //rotate backwards
			int buf = super.permutation.get(from);
			int i = from+1;
			while (i <= to) {
				super.permutation.set(i-1, super.permutation.get(i));
				i++;
			}
			super.permutation.set(i-1, buf);
		}
		return;
	}

	public void mutateAngle(GaussianRangeSource range) {
		int n = super.angles.size();
		int i = Math.abs(super.random.nextInt() % n);
		float angle = super.angles.get(i);
		angle += range.nextRange();
		angle %= 360;
		super.angles.set(i, angle);
	}
	
	public GeneticGenome clone() {
		return new GeneticGenome(super.radius, super.angles, super.permutation);
	}
}
