package testPlots;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot {

	public static void main(String[] args) throws IOException {
		
		final XYSeries snSp = new XYSeries("sensitivitySpecificity");
		File coordFile = new File("/home/guillermo/Desktop/Temporal/coordinates.txt");
		BufferedReader br = new BufferedReader(new FileReader(coordFile));
		String line;
		while ((line = br.readLine()) != null) {
			String[] spline = line.split("\t");
			snSp.add(Double.parseDouble(spline[0]), Double.parseDouble(spline[1]));
			
		}
		br.close();
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(snSp);
		
		JFreeChart xylinechart = ChartFactory.createScatterPlot("Title","1-Specificity", "Sensitivity", dataset, PlotOrientation.VERTICAL, true, false, false);
		xylinechart.setBackgroundPaint(Color.white);
		xylinechart.setTitle("Sensitivity Vs. Specificity");
		
		int width = 1000; /* Width of the image */
	      int height = 1000; /* Height of the image */ 
	      File XYChart = new File( "/home/guillermo/Desktop/Temporal/XYLineChart.jpeg" ); 
	      ChartUtilities.saveChartAsJPEG( XYChart, xylinechart, width, height);
		
		
		
		
		
	}

}
