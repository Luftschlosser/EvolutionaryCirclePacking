package control;

import java.util.*;
import java.util.Map.Entry;
import genotype.*;
import gui.*;
import phenotype.*;

public class GeneticSearch {

	private CircleCanvas circleCanvas;
	private Statistics statistics;
	private EvolutionCanvas graph;
	private Controls controls;

	private ArrayList<Float> radius;
	private ArrayList<Entry<Individual, GeneticGenome>> population;

	private Random random;

	public GeneticSearch(ArrayList<Float> radius, CircleCanvas circleCanvas, Statistics statistics, EvolutionCanvas graph, Controls controls) {
		this.circleCanvas = circleCanvas;
		this.statistics = statistics;
		this.graph = graph;
		this.controls = controls;
		this.radius = radius;
		this.random = new Random();
	}

	public void start(BinaryDecisionSource permutationMutationRate, BinaryDecisionSource angleMutationRate, GaussianRangeSource angleMutationRange) {
		System.out.println("\nStarting genetic Algorithm:");

		// init
		final int populationSize = this.controls.getPopulation();
		final int generations = this.controls.getGenerations();
		this.population = new ArrayList<Entry<Individual, GeneticGenome>>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			GeneticGenome newGenome = new GeneticGenome(this.radius);
			Decoder decoder = new Decoder(newGenome);
			this.population.add(new MapEntry<Individual, GeneticGenome>(decoder.decode(), newGenome));
		}
		final int bestIndex = getBestIndexFromPopulation();
		Individual bestPhenotype = this.population.get(bestIndex).getKey();
		GeneticGenome bestGenotype = this.population.get(bestIndex).getValue();
		float bestScore = bestPhenotype.getScore();
		this.statistics.reset();
		this.statistics.setScore(bestScore);
		this.statistics.setDensity(bestPhenotype.getDensity());
		this.graph.reset(generations, bestPhenotype.getTotalArea(), bestScore);
		this.circleCanvas.reset();
		this.circleCanvas.update(bestPhenotype, bestGenotype);

		// run
		int gen = 0;
		while (++gen <= generations) {

			// making love
			ArrayList<Entry<Individual, GeneticGenome>> childs = new ArrayList<Entry<Individual, GeneticGenome>>(populationSize);
			for (int i = 0; i < populationSize; i++) {
				ArrayList<Integer> parents = tournamentSelection(2, (populationSize / 8) + 1);
				GeneticGenome parent1 = this.population.get(parents.get(0)).getValue();
				GeneticGenome parent2 = this.population.get(parents.get(1)).getValue();
				GeneticGenome child1 = parent1.recombineByAngle(parent2);
				childs.add(new MapEntry<Individual, GeneticGenome>(new Decoder(child1).decode(), child1));
				i++;
				if (i < populationSize) {
					GeneticGenome child2 = parent2.recombineByAngle(parent1);
					childs.add(new MapEntry<Individual, GeneticGenome>(new Decoder(child2).decode(), child2));
				}
			}

			// mutate
			for (Entry<Individual, GeneticGenome> child : childs) {
				child.getValue().mutatePermutationBySwitch(permutationMutationRate);
				if (angleMutationRate.nextDecision()) {
					child.getValue().mutateAngle(angleMutationRange);
				}
			}
			permutationMutationRate.incrementGeneration();
			angleMutationRate.incrementGeneration();
			angleMutationRange.incrementGeneration();

			this.population = childs;

			// evaluate and update gui
			int newBestIndex = getBestIndexFromPopulation();
			Individual newBestPhenotype = this.population.get(newBestIndex).getKey();
			GeneticGenome newBestGenotype = this.population.get(newBestIndex).getValue();
			float newBestScore = newBestPhenotype.getScore();
			if (newBestScore < bestScore) {
				bestPhenotype = newBestPhenotype;
				bestGenotype = newBestGenotype;
				bestScore = newBestScore;
			}
			this.circleCanvas.update(newBestPhenotype, newBestGenotype);
			this.graph.addValue(newBestScore);
			this.statistics.setScore(newBestScore);
			this.statistics.setDensity(newBestPhenotype.getDensity());
			this.statistics.setGeneration(gen);
			System.out.println("Gen " + gen + ": " + newBestScore);

			// pause
			if (this.controls.getDelay() > 0) {
				try {
					Thread.sleep(this.controls.getDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				Thread.yield();
			}
		}

		// after
		System.out.println("Best Individual from " + --gen + " Generations: Area of " + bestScore + ", Density of " + bestPhenotype.getDensity() * 100 + "%.\n");
	}

	private int getBestIndexFromPopulation() {
		float bestScore = Float.MAX_VALUE;
		int bestIndex = 0;

		for (int i = 0; i < this.population.size(); i++) {
			float score = this.population.get(i).getKey().getScore();
			if (score < bestScore) {
				bestScore = score;
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	private ArrayList<Integer> tournamentSelection(int numberOfWinners, int numberOfOpponents) {
		ArrayList<Integer> winners = new ArrayList<Integer>(numberOfWinners);

		for (int w = 0; w < numberOfWinners; w++) {
			ArrayList<Entry<Float, Integer>> scorePerIndex = new ArrayList<Entry<Float, Integer>>(numberOfOpponents + 1);

			for (int i = 0; i <= numberOfOpponents; i++) {
				int index = Math.abs(this.random.nextInt() % this.population.size());
				GeneticGenome g = this.population.get(index).getValue();
				Individual s = new Decoder(g).decode();
				scorePerIndex.add(new MapEntry<Float, Integer>(s.getScore(), index));
			}

			scorePerIndex.sort((first, second) -> {
				return Float.compare(first.getKey(), second.getKey());
			});

			winners.add(scorePerIndex.get(0).getValue());
		}

		return winners;
	}

	@SuppressWarnings("unused")
	private ArrayList<Integer> rankSelection(int numberOfWinners) { // not suitable as it seems
		ArrayList<Integer> winners = new ArrayList<Integer>(numberOfWinners);
		ArrayList<Entry<Float, Integer>> scorePerIndex = new ArrayList<Entry<Float, Integer>>(this.population.size());

		for (int i = 0; i < this.population.size(); i++) {
			scorePerIndex.add(new MapEntry<Float, Integer>(this.population.get(i).getKey().getScore(), i));
		}

		scorePerIndex.sort((first, second) -> {
			return Float.compare(first.getKey(), second.getKey());
		});

		for (int winner = 0; winner < numberOfWinners; winner++) {
			float rand = this.random.nextFloat();
			for (int i = 0; i < scorePerIndex.size(); i++) {
				rand -= (2.0 / scorePerIndex.size()) * (1.0 - (i / (scorePerIndex.size() - 1.0)));
				if (rand < 0) {
					winners.add(scorePerIndex.get(i).getValue());
					break;
				}
			}
		}

		return winners;
	}

	private class MapEntry<K, V> implements Entry<K, V> {

		private K key;
		private V value;

		public MapEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return value;
		}
	}
}
