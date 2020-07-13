package control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import genotype.*;
import gui.*;
import gui.Controls.AlgorithmType;
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

	private Hillclimb hillclimb;
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

			switch (this.controls.getChosenAlgorithm()) {
			case HILLCLIMB:
				float n = (float)this.controls.getN();
				int generations = this.controls.getGenerations();
				
				System.out.println("Initializing Hillclimb:");
				
				double initialRate = 0.25;
				double dampingBuf = findDampingFactor(generations/2.0f, initialRate, 1/n);
				System.out.println("\nPermutation-Mutationrate:\n"
						+ "\tSimulated Annealing with initial rate of " + initialRate + ",\n"
						+ "\tdampingFactor " + dampingBuf + ",\n"
						+ "\t[target rate of "+ initialRate*Math.pow(dampingBuf, generations/2.0) +" after "+ generations/2 +" generations]");
				BinaryDecisionSource permutationRate = new SimulatedAnnealing(0.25, dampingBuf);
				
				dampingBuf = Math.min(0.5, 3/n);
				System.out.println("ņAngle-Mutationrate:\n\tConstant rate of " + dampingBuf +" (3/n)");
				BinaryDecisionSource angleMutationRate = new ConstantProbability((float) dampingBuf);
				
				initialRate = 360;
				dampingBuf = findDampingFactor(generations, initialRate, 1);
				System.out.println("Angle-Mutationrange:\n"
						+ "\tSimulated Annealing with initial range of " + initialRate + ",\n"
						+ "\tdampingFactor " + dampingBuf + ",\n"
						+ "\t[target range of "+ initialRate*Math.pow(dampingBuf, generations) +" after "+ generations +" generations]");
				GaussianRangeSource angleMutationRange = new SimulatedAnnealing(360, dampingBuf);
				
				hillclimb.start(permutationRate, angleMutationRate, angleMutationRange);
				break;
			case GENETIC:
				// Todo
				break;
			}
		}
	}

	public void initRandom() {
		HillclimbGenome hillclimbGenome = new HillclimbGenome(this.controls.getN(), circleRadiusMin, circleRadiusMax);
		this.hillclimb = new Hillclimb(hillclimbGenome, this.canvas, this.statistics, this.graph, this.controls);
	}
	
	private static double logBaseN(double base, double arg) {
		return Math.log(arg) / Math.log(base);
	}
	
	private static double findDampingFactor(float generations, double initialRate, double targetRate) {
		final double epsilon = 0.0000001;
		final double rate = targetRate/initialRate;
		double dampingMin = 0.9;
		double dampingMax = 1.0;
		double damping = 0.95;
		do {
			double exponent = logBaseN(damping, rate);
			if (Math.abs(exponent - generations) <= 1){
				break;
			}
			else if (exponent < generations) {
				dampingMin = damping;
			}
			else if (exponent > generations) {
				dampingMax = damping;
			}
			damping = (dampingMin + dampingMax) / 2;
		}
		while ((dampingMax - dampingMin) > epsilon);
		return damping;
	}
}