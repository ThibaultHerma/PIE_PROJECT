package utils;

import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class to plot the the fitness values across generations
 * 
 * @author Amelie Falcou
 *
 */
public class Plot extends JFrame {

	/**
	 * Constructor of the class
	 * 
	 * @param fitnessValues:ConcurrentHashMap<Integer,Double> with the fitness values across generation
	 * @param plotName:String title of the graph
	 * @param xAxis:String  legend of the xAxis
	 * @param yAxis:String  legend of the yAxis
	 */
	public Plot(ConcurrentHashMap<Integer, Double> fitnessValues, String plotName, String xAxis, String yAxis) {
		// Convert HashMap to DataSet
		XYDataset dataset = createDataset(fitnessValues);
		// Create chart
		JFreeChart chart = ChartFactory.createXYLineChart(plotName, xAxis, yAxis, dataset, PlotOrientation.VERTICAL,
				true, true, false);
		// Create Panel
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
		setSize(800, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		// To save the picture (doesn't work)
		// ChartUtils.saveChartAsPNG(new File("CostFunction_P_G.png"), chart, 800, 400);
	}

	private XYDataset createDataset(ConcurrentHashMap<Integer, Double> hashmap) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries series = new XYSeries("fitness values");

		for (int key : hashmap.keySet()) {
			series.add(key, hashmap.get(key));
		}

		// Add series to dataset
		dataset.addSeries(series);

		return dataset;
	}

}
