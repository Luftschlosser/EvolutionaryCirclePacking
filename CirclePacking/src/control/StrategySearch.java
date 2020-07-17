package control;

import java.util.*;
import java.util.Map.Entry;
import genotype.*;
import gui.*;
import phenotype.*;

public class StrategySearch {
	
	public enum EvolutionStrategy {
		KOMMA,
		PLUS
	}
	
	
	private CircleCanvas circleCanvas;
	private Statistics statistics;
	private EvolutionCanvas graph;
	private Controls controls;

	private ArrayList<Float> radius;
	private ArrayList<Entry<Individual, StrategyGenome>> population;

	private Random random;

	public StrategySearch(ArrayList<Float> radius, CircleCanvas circleCanvas, Statistics statistics, EvolutionCanvas graph, Controls controls) {
		this.circleCanvas = circleCanvas;
		this.statistics = statistics;
		this.graph = graph;
		this.controls = controls;
		this.radius = radius;
		this.random = new Random();
	}

	public void start(EvolutionStrategy strategy, int childsPerParent, double sigma, BinaryDecisionSource permutationMutation, BinaryDecisionSource angleMutation) {
		System.out.println("\nStarting self-adaptive Algorithm:");

		// init
		final int populationSize = this.controls.getPopulation();
		final int generations = this.controls.getGenerations();
		final int childsCount = populationSize * childsPerParent;
		this.population = new ArrayList<Entry<Individual, StrategyGenome>>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			StrategyGenome newGenome = new StrategyGenome(this.radius, sigma);
			Decoder decoder = new Decoder(newGenome);
			this.population.add(new MapEntry<Individual, StrategyGenome>(decoder.decode(), newGenome));
		}
		final int bestIndex = getBestIndexFromPopulation();
		Individual bestPhenotype = this.population.get(bestIndex).getKey();
		StrategyGenome bestGenotype = this.population.get(bestIndex).getValue();
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

			// creating childs
			ArrayList<Entry<Individual, StrategyGenome>> childs = new ArrayList<Entry<Individual, StrategyGenome>>(childsCount);
			for (int i = 0; i < childsCount; i++) {
				StrategyGenome parent = this.population.get(this.random.nextInt(this.population.size())).getValue();
				StrategyGenome child = parent.getNewChild(permutationMutation, angleMutation);
				childs.add(new MapEntry<Individual, StrategyGenome>(new Decoder(child).decode(), child));
			}

			// selecting best
			if (strategy == EvolutionStrategy.PLUS) {
				childs.addAll(this.population);
			}
			childs.sort((first, second) -> {
				return Float.compare(first.getKey().getScore(), second.getKey().getScore());
			});
			this.population = new ArrayList<Entry<Individual, StrategyGenome>>(populationSize);
			for (int i = 0; i < populationSize; i++) {
				this.population.add(childs.get(i));
			}

			// evaluate and update gui
			int newBestIndex = getBestIndexFromPopulation();
			Individual newBestPhenotype = this.population.get(newBestIndex).getKey();
			StrategyGenome newBestGenotype = this.population.get(newBestIndex).getValue();
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
	
	private StrategyGenome tournamentSelection(int numberOfOpponents) {

		ArrayList<Entry<Float, Integer>> scorePerIndex = new ArrayList<Entry<Float, Integer>>(numberOfOpponents + 1);

		for (int i = 0; i <= numberOfOpponents; i++) {
			int index = this.random.nextInt(this.population.size());
			Individual s = this.population.get(index).getKey();
			scorePerIndex.add(new MapEntry<Float, Integer>(s.getScore(), index));
		}

		scorePerIndex.sort((first, second) -> {
			return Float.compare(first.getKey(), second.getKey());
		});

		return this.population.get(scorePerIndex.get(0).getValue()).getValue();
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
