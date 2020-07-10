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

	public Hillclimb(HillclimbGenome initial, CircleCanvas circleCanvas, Statistics statistics) {
		this.circleCanvas = circleCanvas;
		this.statistics = statistics;

		this.bestGenome = this.currentGenome = this.initialGenome = initial;
		this.bestPhenotype = new Decoder(currentGenome).decode();
		this.bestScore = this.bestPhenotype.getScore();

		this.statistics.reset();
		this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
		this.statistics.setScore(this.bestPhenotype.getScore());
	}

	public void start(int generations, int drawDelay) {
		System.out.println("Starting Hillclimb:");

		// reset
		int gen;
		this.bestGenome = this.initialGenome.clone();
		this.currentGenome = this.bestGenome.clone();
		this.bestPhenotype = new Decoder(currentGenome).decode();
		this.bestScore = this.bestPhenotype.getScore();
		float mutateRate = 1 / (float) this.currentGenome.getN();

		this.circleCanvas.reset();
		this.statistics.reset();
		this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
		this.statistics.setScore(this.bestPhenotype.getScore());

		// run
		for (gen = 0; gen <= generations; gen++) {
			this.currentGenome.mutatePermutation(mutateRate);
			this.currentGenome.mutateAngles(mutateRate, 180);
			Individual newPhenotype = new Decoder(currentGenome).decode();
			float newScore = newPhenotype.getScore();

			System.out.println("Gen " + gen + ": " + newScore);

			this.statistics.setGeneration(gen);

			if (newScore < this.bestScore) {
				this.bestScore = newScore;
				this.bestGenome = this.currentGenome;
				this.bestPhenotype = newPhenotype;
				this.circleCanvas.update(this.bestPhenotype, this.bestGenome);
				this.statistics.setScore(newScore);
				Thread.yield();
				if (drawDelay > 0) {
					try {
						Thread.sleep(drawDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			this.currentGenome = this.bestGenome.clone();
		}

		// after
		System.out.println("Best Individual from " + --gen + " Generations: " + this.bestScore + "\n");
	}
}
