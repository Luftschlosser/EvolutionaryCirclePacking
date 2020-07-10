package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

@SuppressWarnings("serial")
public class Controls extends JPanel{
	
	public enum AlgorithmType {
		HILLCLIMB,
		GENETIC
	}
	
	
	private JCheckBox showVectors;
	private ActionListener onShowVectors;
	private ActionListener onHideVectors;
	
	private JButton initButton;
	private JButton startButton;
	private JButton stopButton;
	
	private JFormattedTextField inputN;
	private JFormattedTextField inputDelay;
	
	private JComboBox<AlgorithmType> algorithmChoose;

	
	public Controls() {
		
		NumberFormat intFormat = NumberFormat.getInstance();
	    NumberFormatter int1Formatter = new NumberFormatter(intFormat);
	    int1Formatter.setValueClass(Integer.class);
	    int1Formatter.setMinimum(1);
	    int1Formatter.setMaximum(Integer.MAX_VALUE);
	    int1Formatter.setAllowsInvalid(false);
	    int1Formatter.setCommitsOnValidEdit(true);
	    NumberFormatter int0Formatter = new NumberFormatter(intFormat);
	    int0Formatter.setValueClass(Integer.class);
	    int0Formatter.setMinimum(0);
	    int0Formatter.setMaximum(Integer.MAX_VALUE);
	    int0Formatter.setAllowsInvalid(false);
	    int0Formatter.setCommitsOnValidEdit(true);
	    
		this.setBackground(Color.LIGHT_GRAY);
		
		this.showVectors = new JCheckBox("show vectors: ", true);
		this.add(showVectors);
		this.showVectors.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent evt) {
		    	if (showVectors.isSelected()) {
		    		if (onShowVectors != null)
		    			onShowVectors.actionPerformed(null);
		    	}
		    	else {
		    		if (onHideVectors != null)
		    			onHideVectors.actionPerformed(null);
		    	}
		    }
		});
		
		initButton = new JButton("Init");
		this.add(initButton);
		startButton = new JButton("Start");
		this.add(startButton);
		stopButton = new JButton("Stop");
		this.add(stopButton);
		
		this.add(new JLabel(" n:"));
		inputN = new JFormattedTextField(int1Formatter);
		inputN.setText("5");
		inputN.setColumns(3);
		this.add(inputN);
		
		this.add(new JLabel(" delay:"));
		inputDelay = new JFormattedTextField(int0Formatter);
		inputDelay.setText("0");
		inputDelay.setColumns(5);
		this.add(inputDelay);
		
		this.add(new JLabel(" algorithm:"));
		algorithmChoose = new JComboBox<AlgorithmType>(new AlgorithmType[] {AlgorithmType.HILLCLIMB, AlgorithmType.GENETIC});
		this.add(algorithmChoose);
		
		this.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}
	
	public int getN() {
		return Integer.parseInt(inputN.getText());
	}
	
	public int getDelay() {
		return Integer.parseInt(inputDelay.getText());
	}
	
	public AlgorithmType getChosenAlgorithm() {
		return algorithmChoose.getItemAt(algorithmChoose.getSelectedIndex());
	}
	
	public void onInit(ActionListener l) {
		initButton.addActionListener(l);
	}
	
	public void onStart(ActionListener l) {
		startButton.addActionListener(l);
	}
	
	public void onStop(ActionListener l) {
		stopButton.addActionListener(l);
	}
	
	public void onShowVectors(ActionListener l) {
		this.onShowVectors = l;
	}
	
	public void onHideVectors(ActionListener l) {
		this.onHideVectors = l;
	}
}
