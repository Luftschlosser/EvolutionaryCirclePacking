package control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import genotype.*;
import gui.*;
import gui.Controls.AlgorithmType;
import phenotype.*;

public class Main {
	
	private final static int canvasWidth = 1000;
	private final static int canvasHeight = 800;
	private final static float circleRadiusMin = 1.5f;
	private final static float circleRadiusMax = 60f;
	
	private static Main main;
	
	
	private JFrame frame = new JFrame("CirclePacking");
	private Controls controls = new Controls();
	private Statistics statistics = new Statistics();
	private CircleCanvas canvas = new CircleCanvas(this.controls);
	
	
	AlgorithmType currentlyInitializedAlgorithmType = null;
	private Genome initialGenome[];

	
	public static void main(String[] args) {
		main = new Main();		
	}
	
	public Main() {
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(controls, BorderLayout.NORTH);
		frame.add(statistics, BorderLayout.SOUTH);
		frame.add(canvas, BorderLayout.CENTER);
		
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
		        	//Todo
		        	break;
		        }
		    }
		});
	}
	
	public void initHillclimb() {
		this.initialGenome = new Genome[] { new Genome(this.controls.getN(),circleRadiusMin,circleRadiusMax) };
		currentlyInitializedAlgorithmType = AlgorithmType.HILLCLIMB;
		this.statistics.reset();
		Individual initialPhenotype = new Decoder(this.initialGenome[0]).decode();
		this.canvas.update(initialPhenotype, this.initialGenome[0]);
		this.statistics.setScore(initialPhenotype.getScore());
	}
}