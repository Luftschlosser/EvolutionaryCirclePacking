package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import genotype.Genome;
import phenotype.Individual;

@SuppressWarnings("serial")
public class CircleCanvas extends JPanel {
		
	private Individual phenotype = null;
	private Genome genotype = null;
	private boolean showVectors = true;
	
	
	public CircleCanvas(Controls controls) {
		controls.onShowVectors(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent evt) {
				showVectors = true;
				repaint();
			}
		});
		controls.onHideVectors(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent evt) {
				showVectors = false;
				repaint();
			}
		});
	}
	
	public void update(Individual i, Genome g) {
		this.phenotype = i;
		this.genotype = g;
		this.repaint();
	}
	
	public void reset() {
		phenotype = null;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		if (this.phenotype != null && this.genotype != null) {
			if (this.showVectors) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(this.getWidth()/2, this.getHeight()/2 + 20, this.getWidth()/2, this.getHeight()/2 - 20);
				g.drawLine(this.getWidth()/2 + 20, this.getHeight()/2, this.getWidth()/2 - 20, this.getHeight()/2);
				genotype.draw(g, this.getWidth()/2, this.getHeight()/2);
			}
			g.setColor(Color.BLACK);
			phenotype.draw(g, this.getWidth()/2, this.getHeight()/2, this.showVectors);
		}
	}

}
