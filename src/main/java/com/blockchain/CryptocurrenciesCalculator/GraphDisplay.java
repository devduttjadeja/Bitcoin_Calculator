package com.blockchain.CryptocurrenciesCalculator;

import javax.swing.JFrame;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class GraphDisplay {

	public static void displayGraph(double[] xData , double[] yData) {

		// Create Chart
		XYChart chart = QuickChart.getChart("Sample Chart", "Hash-Rate of miner H/sec ", "Expected assets (BTC)", "A(H)", xData, yData);

		// Show it
		JFrame displayChart = new SwingWrapper(chart).displayChart();
		
	}
	
}
