package control;

import java.util.*;
import java.util.Map.Entry;
import genotype.*;
import gui.*;
import phenotype.*;

public class Genetic {
	
	private CircleCanvas circleCanvas;
	private Statistics statistics;
	private EvolutionCanvas graph;
	private Controls controls;
	
	private ArrayList<Float> radius;
	private ArrayList<GeneticGenome> populationGenotypes;
	private ArrayList<Individual> populationPhenotypes;
	
	private Random random;
	

	public Genetic(ArrayList<Float> radius, CircleCanvas circleCanvas, Statistics statistics, EvolutionCanvas graph, Controls controls) {
		this.circleCanvas = circleCanvas;
		this.statistics = statistics;
		this.graph = graph;
		this.controls = controls;
		this.radius = radius;
		this.random = new Random();
	}
	
	public void start(BinaryDecisionSource permutationMutationRate, BinaryDecisionSource angleMutationRate, GaussianRangeSource angleMutationRange) {
		System.out.println("\nStarting genetic Algorithm:");
		
		//init
		final int populationSize = this.controls.getPopulation();
		final int generations = this.controls.getGenerations();
		this.populationGenotypes = new ArrayList<GeneticGenome>(populationSize);
		this.populationPhenotypes = new ArrayList<Individual>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			GeneticGenome newGenome = new GeneticGenome(this.radius);
			Decoder decoder = new Decoder(newGenome);
			this.populationGenotypes.add(newGenome);
			this.populationPhenotypes.add(decoder.decode());
		}
		final int bestIndex = getBestIndexFromPopulation();
		Individual bestPhenotype = this.populationPhenotypes.get(bestIndex);
		GeneticGenome bestGenotype = this.populationGenotypes.get(bestIndex);
		float bestScore = bestPhenotype.getScore();
		this.statistics.reset();
		this.statistics.setScore(bestScore);
		this.statistics.setDensity(bestPhenotype.getDensity());
		this.graph.reset(generations, bestPhenotype.getTotalArea(), bestScore);
		this.circleCanvas.reset();
		this.circleCanvas.update(bestPhenotype, bestGenotype);
		
		//run
		int gen = 0;
		while (++gen <= generations) {
			
			//making love
			ArrayList<GeneticGenome> childs = new ArrayList<GeneticGenome>(populationSize);
			for (int i = 0; i < populationSize; i++) {
				ArrayList<Integer> parents = tournamentSelection(2, (populationSize/10)+1);
				GeneticGenome parent1 = this.populationGenotypes.get(parents.get(0));
				GeneticGenome parent2 = this.populationGenotypes.get(parents.get(1));
				childs.add(parent1.recombine(parent2));
				i++;
				if (i < populationSize) {
					childs.add(parent2.recombine(parent1));
				}
			}
			
			//mutate
			for (GeneticGenome child : childs) {
				if (permutationMutationRate.nextDecision()) {
					child.mutatePermutation();
				}
				if (angleMutationRate.nextDecision()) {
					child.mutateAngle(angleMutationRange);
				}
			}
			permutationMutationRate.incrementGeneration();
			angleMutationRate.incrementGeneration();
			angleMutationRange.incrementGeneration();
			
			//evaluate
			this.populationGenotypes = childs;
			this.populationPhenotypes = new ArrayList<Individual>(populationSize);
			for (GeneticGenome child : childs) {
				Decoder decoder = new Decoder(child);
				this.populationPhenotypes.add(decoder.decode());
			}
			int newBestIndex = getBestIndexFromPopulation();
			Individual newBestPhenotype = this.populationPhenotypes.get(newBestIndex);
			GeneticGenome newBestGenotype = this.populationGenotypes.get(newBestIndex);
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
			
			//pause
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
		
		//after
		System.out.println("Best Individual from " + --gen + " Generations: Area of " + bestScore + ", Density of " + bestPhenotype.getDensity()*100 + "%.\n");
	}
	
	private int getBestIndexFromPopulation() {
		float bestScore = Float.MAX_VALUE;
		int bestIndex = 0;
		
		for(int i = 0; i < this.populationPhenotypes.size(); i++){
			float score = this.populationPhenotypes.get(i).getScore();
			if (score < bestScore) {
				bestScore = score;
				bestIndex = i;
			}
		}
		
		return bestIndex;
	}
	
	private ArrayList<Integer> tournamentSelection(int numberOfWinners, int numberOfOpponents){
		ArrayList<Integer> winners = new ArrayList<Integer>(numberOfWinners);
		ArrayList<Entry<Integer, Integer>> winsPerIndividual = new ArrayList< Entry<Integer, Integer>>(this.populationPhenotypes.size()); //Maps Index -> Wins
		
		for (int i = 0; i < this.populationPhenotypes.size(); i++) {
			int wins = 0;
			Individual testling = this.populationPhenotypes.get(i);
			for (int o = 0; o < numberOfOpponents; o++) {
				Individual opponent = this.populationPhenotypes.get(Math.abs(this.random.nextInt() % this.populationPhenotypes.size()));
				if (testling.getScore() < opponent.getScore()) {
					wins++;
				}
			}
			winsPerIndividual.add(new MapEntry<Integer, Integer>(i, wins));
		}
		
		winsPerIndividual.sort((first, second) -> {
	        return Integer.compare(second.getValue(), first.getValue());
	    });
		
		for (int i = 0; i < numberOfWinners; i++) {
			winners.add(winsPerIndividual.get(0).getKey());
			winsPerIndividual.remove(0);
		}
		
		return winners;
	}
	
	private class MapEntry<K,V> implements Entry<K,V> {
		
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
