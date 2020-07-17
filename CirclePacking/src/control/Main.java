package control;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import genotype.*;
import gui.*;
import phenotype.*;

public class Main implements Runnable {

	private final static int canvasWidth = 1000;
	private final static int canvasHeight = 800;

	private final static float circleRadiusMin = 2.5f;
	private final static float circleRadiusMax = 60f;

	private static Main main;

	private JFrame frame = new JFrame("CirclePacking");
	private JTabbedPane tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	private Controls controls = new Controls();
	private Statistics statistics = new Statistics();
	private CircleCanvas canvas = new CircleCanvas(this.controls);
	private EvolutionCanvas graph = new EvolutionCanvas();

	private LocalSearch hillclimbAlgorithm;
	private GeneticSearch geneticAlgorithm;
	boolean startExecution = false;

	public static void main(String[] args) {
		main = new Main();
		main.run();
	}

	public Main() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(controls, BorderLayout.NORTH);
		frame.add(statistics, BorderLayout.SOUTH);
		tabpane.add("Phenotype", canvas);
		tabpane.add("Evolution Graph", graph);
		frame.add(tabpane, BorderLayout.CENTER);

		frame.setSize(canvasWidth, canvasHeight);
		frame.setVisible(true);

		controls.onInit(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				initRandom();
			}
		});

		controls.onStart(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				startExecution = true;
			}
		});
	}

	public void run() {
		while (true) {
			while (!this.startExecution) {
				Thread.yield();
			}
			this.startExecution = false;

			final float n = (float) this.controls.getN();
			final int generations = this.controls.getGenerations();
			double initialRate, dampingBuf;

			BinaryDecisionSource permutationMutationRate;
			BinaryDecisionSource angleMutationRate;
			GaussianRangeSource angleMutationRange;

			switch (this.controls.getChosenAlgorithm()) {
			case LocalSearch:
				System.out.println("Initializing local Search:");

				initialRate = 0.25;
				dampingBuf = findDampingFactor(generations / 2.0f, initialRate, 1 / n);
				System.out.println("\nPermutation-Mutationrate:\n" + "\tConverging probability with initial rate of " + initialRate + ",\n" + "\tdampingFactor " + dampingBuf + ",\n" + "\t[target rate of "
						+ initialRate * Math.pow(dampingBuf, generations / 2.0) + " after " + generations / 2 + " generations]");
				permutationMutationRate = new ConvergingProbability(initialRate, dampingBuf);

				dampingBuf = Math.min(0.5, 3 / n);
				System.out.println("ņAngle-Mutationrate:\n\tConstant rate of " + dampingBuf + " (3/n)");
				angleMutationRate = new ConstantProbability((float) dampingBuf);

				initialRate = 360;
				dampingBuf = findDampingFactor(generations, initialRate, 1);
				System.out.println("Angle-Mutationrange:\n" + "\tConverging with initial range of " + initialRate + ",\n" + "\tdampingFactor " + dampingBuf + ",\n" + "\t[target range of " + initialRate * Math.pow(dampingBuf, generations) + " after "
						+ generations + " generations]");
				angleMutationRange = new ConvergingProbability(initialRate, dampingBuf);

				this.hillclimbAlgorithm.start(permutationMutationRate, angleMutationRate, angleMutationRange);
				break;

			case Genetic:
				System.out.println("Initializing genetic Algorithm:");

				initialRate = 2 / n;
				dampingBuf = findDampingFactor(generations / 2.0f, initialRate, 1 / n);
				System.out.println("\nPermutation-Mutationrate:\n" + "\tConverging probability with initial rate of " + initialRate + ",\n" + "\tdampingFactor " + dampingBuf + ",\n" + "\t[target rate of "
						+ initialRate * Math.pow(dampingBuf, generations / 2.0) + " after " + generations / 2.0f + " generations]");
				permutationMutationRate = new ConvergingProbability(initialRate, dampingBuf);

				float probability = (float) Math.min(0.5, 1.0 / 2);
				System.out.println("Angle-Mutationrate:\n\tConstant rate of " + probability + " (1/5)");
				angleMutationRate = new ConstantProbability((float) probability);

				initialRate = 240;
				dampingBuf = findDampingFactor(generations, initialRate, 2);
				System.out.println("Angle-Mutationrange:\n" + "\tConverging with initial range of " + initialRate + ",\n" + "\tdampingFactor " + dampingBuf + ",\n" + "\t[target range of " + initialRate * Math.pow(dampingBuf, generations) + " after "
						+ generations + " generations]");
				angleMutationRange = new ConvergingProbability(initialRate, dampingBuf);

				this.geneticAlgorithm.start(permutationMutationRate, angleMutationRate, angleMutationRange);
				break;
			}
		}
	}

	public void initRandom() {
		int n = this.controls.getN();
		ArrayList<Float> radius = generateRandomRadius(n, circleRadiusMin, circleRadiusMax);

		HillclimbGenome hillclimbGenome = new HillclimbGenome(radius);
		this.hillclimbAlgorithm = new LocalSearch(hillclimbGenome, this.canvas, this.statistics, this.graph, this.controls);
		this.geneticAlgorithm = new GeneticSearch(radius, this.canvas, this.statistics, this.graph, this.controls);

		// preview
		Individual phenotype = new Decoder(hillclimbGenome).decode();
		this.graph.reset(this.controls.getGenerations(), phenotype.getTotalArea(), phenotype.getScore());

		this.canvas.reset();
		this.canvas.update(phenotype, hillclimbGenome);

		this.statistics.reset();
		this.statistics.setScore(phenotype.getScore());
		this.statistics.setDensity(phenotype.getDensity());
	}

	private static ArrayList<Float> generateRandomRadius(int n, float rmin, float rmax) {
		ArrayList<Float> radius = new ArrayList<Float>(n);

		for (int i = 0; i < n; i++) {
			radius.add((float) ((rmax - rmin) * Math.random() + rmin));
		}

		return radius;
	}

	private static double logBaseN(double base, double arg) {
		return Math.log(arg) / Math.log(base);
	}

	private static double findDampingFactor(float generations, double initialRate, double targetRate) {
		final double epsilon = 0.0000001;
		final double rate = targetRate / initialRate;
		double dampingMin = 0.9;
		double dampingMax = 1.0;
		double damping = 0.95;
		do {
			double exponent = logBaseN(damping, rate);
			if (Math.abs(exponent - generations) <= 1) {
				break;
			} else if (exponent < generations) {
				dampingMin = damping;
			} else if (exponent > generations) {
				dampingMax = damping;
			}
			damping = (dampingMin + dampingMax) / 2;
		} while ((dampingMax - dampingMin) >= epsilon);
		return damping;
	}
}