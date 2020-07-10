package gui;

import java.awt.Color;
import java.awt.ComponentOrientation;

import javax.swing.*;

@SuppressWarnings("serial")
public class Statistics extends JPanel {

	private JTextField generation;
	private JTextField score;
	private JTextField density;

	public Statistics() {
		this.setBackground(Color.LIGHT_GRAY);

		this.add(new JLabel("generation:"));
		this.generation = new JTextField("0");
		this.generation.setColumns(7);
		this.generation.setEditable(false);
		this.add(generation);

		this.add(new JLabel(" best score:"));
		this.score = new JTextField("-");
		this.score.setColumns(10);
		this.score.setEditable(false);
		this.add(score);
		
		this.add(new JLabel(" density:"));
		this.density = new JTextField("-");
		this.density.setColumns(10);
		this.density.setEditable(false);
		this.add(density);

		this.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}

	public void reset() {
		this.generation.setText("0");
		this.score.setText("-");
		this.density.setText("-");
	}

	public void setGeneration(int g) {
		this.generation.setText(Integer.toString(g));
	}

	public void setScore(float score) {
		this.score.setText(Float.toString(score));
	}
	
	public void setDensity(float density) {
		this.density.setText(Float.toString(density));
	}
}
