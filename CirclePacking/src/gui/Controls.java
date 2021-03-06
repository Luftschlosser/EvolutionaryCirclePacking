package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

@SuppressWarnings("serial")
public class Controls extends JPanel {

	public enum AlgorithmType {
		LocalSearch, Genetic, Strategy
	}
	
	public enum StrategyType {
		Komma, Plus
	}

	private JCheckBox showVectors;
	private ActionListener onShowVectors;
	private ActionListener onHideVectors;

	private JButton initButton;
	private JButton startButton;

	private JFormattedTextField inputN;
	private JFormattedTextField inputDelay;
	private JFormattedTextField inputGenerations;
	private JFormattedTextField inputPopulation;
	private JFormattedTextField inputReproductionRate;

	private JComboBox<AlgorithmType> algorithmChoose;
	private JComboBox<StrategyType> strategyChoose;

	public Controls() {

		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		intFormat.setGroupingUsed(false);
		NumberFormatter int1Formatter = new NumberFormatter(intFormat);
		int1Formatter.setMinimum(1);
		int1Formatter.setMaximum(Integer.MAX_VALUE);
		int1Formatter.setAllowsInvalid(false);
		int1Formatter.setCommitsOnValidEdit(true);
		NumberFormatter int0Formatter = new NumberFormatter(intFormat);
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
				} else {
					if (onHideVectors != null)
						onHideVectors.actionPerformed(null);
				}
			}
		});

		initButton = new JButton("Init");
		this.add(initButton);
		startButton = new JButton("Start");
		this.add(startButton);

		this.add(new JLabel(" n:"));
		inputN = new JFormattedTextField(int1Formatter);
		inputN.setText("20");
		inputN.setColumns(3);
		this.add(inputN);

		this.add(new JLabel(" Generations:"));
		inputGenerations = new JFormattedTextField(int1Formatter);
		inputGenerations.setText("1000");
		inputGenerations.setColumns(8);
		this.add(inputGenerations);

		this.add(new JLabel(" Delay:"));
		inputDelay = new JFormattedTextField(int0Formatter);
		inputDelay.setText("0");
		inputDelay.setColumns(4);
		this.add(inputDelay);

		this.add(new JLabel(" Algorithm:"));
		algorithmChoose = new JComboBox<AlgorithmType>(new AlgorithmType[] { AlgorithmType.LocalSearch, AlgorithmType.Genetic, AlgorithmType.Strategy });
		this.add(algorithmChoose);
		algorithmChoose.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        switch (algorithmChoose.getItemAt(algorithmChoose.getSelectedIndex())) {
		        case LocalSearch:
		        	inputPopulation.setEnabled(false);
		        	strategyChoose.setEnabled(false);
		        	inputReproductionRate.setEnabled(false);
		        	break;
		        case Genetic:
		        	inputPopulation.setEnabled(true);
		        	strategyChoose.setEnabled(false);
		        	inputReproductionRate.setEnabled(false);
		        	break;
		        case Strategy:
		        	inputPopulation.setEnabled(true);
		        	strategyChoose.setEnabled(true);
		        	inputReproductionRate.setEnabled(true);
		        	break;
		        }
		    }
		});

		this.add(new JLabel(" Population:"));
		inputPopulation = new JFormattedTextField(int1Formatter);
		inputPopulation.setText("100");
		inputPopulation.setColumns(4);
		this.add(inputPopulation);
		inputPopulation.setEnabled(false);
		
		this.add(new JLabel(" Strategy:"));
		strategyChoose = new JComboBox<StrategyType>(new StrategyType[] { StrategyType.Plus, StrategyType.Komma });
		this.add(strategyChoose);
		strategyChoose.setEnabled(false);
		
		this.add(new JLabel(" ReproductionRate:"));
		inputReproductionRate = new JFormattedTextField(int1Formatter);
		inputReproductionRate.setText("10");
		inputReproductionRate.setColumns(4);
		this.add(inputReproductionRate);
		inputReproductionRate.setEnabled(false);

		this.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}

	public int getN() {
		return Math.max(Integer.parseInt(inputN.getText()), 3);
	}

	public int getGenerations() {
		return Math.max(Integer.parseInt(inputGenerations.getText()), 10);
	}

	public int getDelay() {
		return Integer.parseInt(inputDelay.getText());
	}

	public AlgorithmType getChosenAlgorithm() {
		return algorithmChoose.getItemAt(algorithmChoose.getSelectedIndex());
	}

	public int getPopulation() {
		return Math.max(Integer.parseInt(inputPopulation.getText()), 3);
	}
	
	public StrategyType getStrategyType() {
		return strategyChoose.getItemAt(strategyChoose.getSelectedIndex());
	}
	
	public int getReproductionRate() {
		return Integer.parseInt(inputReproductionRate.getText());
	}

	public void onInit(ActionListener l) {
		initButton.addActionListener(l);
	}

	public void onStart(ActionListener l) {
		startButton.addActionListener(l);
	}

	public void onShowVectors(ActionListener l) {
		this.onShowVectors = l;
	}

	public void onHideVectors(ActionListener l) {
		this.onHideVectors = l;
	}
}
