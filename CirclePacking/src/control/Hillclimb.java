package control;

import genotype.*;
import gui.*;
import phenotype.*;

public class Hillclimb {

	private HillclimbGenome bestGenome, currentGenome, initialGenome;
	private float bestScore;
	private Individual bestPhenotype;

	private CircleCanvas circleCanvas;
	private Statistics statistics;
	private EvolutionCanvas graph;
	private Controls controls;

	public Hillclimb(HillclimbGenome initial, CircleCanvas circleCanvas, Statistics statistics, EvolutionCanvas graph, Controls controls) {
		this.circleCanvas = circleCanvas;
		this.statistics = statistics;
		this.graph = graph;

		this.bestGenome = this.currentGenome = this.initialGenome = initial;
		this.bestPhenotype = new Decoder(currentGenome).decode();
		this.bestScore = this.bestPhenotype.getScore();

		this.statistics.reset();
		this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
		this.statistics.setScore(this.bestPhenotype.getScore());
		this.statistics.setDensity(this.bestPhenotype.getDensity());
		this.graph.reset(1000/*dirty fix*/, this.bestPhenotype.getTotalArea(), this.bestPhenotype.getScore());
		this.controls = controls;
	}

	public void start(BinaryDecisionSource permutationRate, BinaryDecisionSource angleMutationRate, GaussianRangeSource angleMutationRange) {
		System.out.println("\nStarting Hillclimb:");

		// reset
		int gen;
		this.bestGenome = this.initialGenome.clone();
		this.currentGenome = this.bestGenome.clone();
		this.bestPhenotype = new Decoder(currentGenome).decode();
		this.bestScore = this.bestPhenotype.getScore();
		this.graph.reset(this.controls.getGenerations(), this.bestPhenotype.getTotalArea(), this.bestPhenotype.getScore());

		this.circleCanvas.reset();
		this.statistics.reset();
		this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
		this.statistics.setScore(this.bestPhenotype.getScore());
		this.statistics.setDensity(this.bestPhenotype.getDensity());

		// run
		for (gen = 0; gen <= this.controls.getGenerations(); gen++) {
			this.currentGenome.mutatePermutation(permutationRate);
			this.currentGenome.mutateAngles(angleMutationRate, angleMutationRange);
			Individual newPhenotype = new Decoder(currentGenome).decode();
			float newScore = newPhenotype.getScore();

			System.out.println("Gen " + gen + ": " + newScore);
			
			if (newScore < this.bestScore) {
				this.bestScore = newScore;
				this.bestGenome = this.currentGenome;
				this.bestPhenotype = newPhenotype;
				this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
				this.statistics.setScore(newScore);
				this.statistics.setDensity(this.bestPhenotype.getDensity());
				if (this.controls.getDelay() > 0) {
					try {
						Thread.sleep(this.controls.getDelay());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					Thread.yield();
				}
			}
			
			this.statistics.setGeneration(gen);
			this.graph.addValue(this.bestScore);
			this.currentGenome = this.bestGenome.clone();
			
			permutationRate.incrementGeneration();
			angleMutationRate.incrementGeneration();
			angleMutationRange.incrementGeneration();
		}

		// after
		System.out.println("Best Individual from " + --gen + " Generations: Area of " + this.bestScore + ", Density of " + this.bestPhenotype.getDensity() + "%.\n");
	}
}
