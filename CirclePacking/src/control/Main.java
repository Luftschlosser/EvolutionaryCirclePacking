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

	AlgorithmType currentlyInitializedAlgorithmType = null;
	private Hillclimb hillclimb;
	boolean startInitializedExecution = false;

	public static void main(String[] args) {
		main = new Main();
		main.run();
	}

	public Main() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(controls, BorderLayout.NORTH);
		frame.add(statistics, BorderLayout.SOUTH);
		tabpane.add("Phenotype", canvas);
		frame.add(tabpane, BorderLayout.CENTER);

		frame.setSize(canvasWidth, canvasHeight);
		frame.setVisible(true);

		controls.onInit(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AlgorithmType chosen = controls.getChosenAlgorithm();
				switch (chosen) {
				case HILLCLIMB:
					initHillclimb();
					break;
				case GENETIC:
					// Todo
					break;
				}
			}
		});

		controls.onStart(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				startInitializedExecution = true;
			}
		});
	}

	public void run() {
		while (true) {
			while (!this.startInitializedExecution) {
				Thread.yield();
			}
			this.startInitializedExecution = false;

			switch (currentlyInitializedAlgorithmType) {
			case HILLCLIMB:
				hillclimb.start(controls.getGenerations(), controls.getDelay());
				break;
			case GENETIC:
				// Todo
				break;
			}
		}
	}

	public void initHillclimb() {
		HillclimbGenome hillclimbGenome = new HillclimbGenome(this.controls.getN(), circleRadiusMin, circleRadiusMax);
		this.hillclimb = new Hillclimb(hillclimbGenome, this.canvas, this.statistics);
		currentlyInitializedAlgorithmType = AlgorithmType.HILLCLIMB;
	}
}