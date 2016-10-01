package icegalaxy.net;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;

public class Chart extends ApplicationFrame {
	public Chart(String titel, ArrayList<Candle> candle) {
		super(titel);

		DefaultHighLowDataset dataset1 = createDataset(candle);
//		 XYDataset dataset2 = new SampleXYDataset();
		JFreeChart chart = createChart(dataset1);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 350));
		setContentPane(chartPanel);
	}

	public DefaultHighLowDataset createDataset(ArrayList<Candle> candle) {
		int size = candle.size();

		Date[] date = new Date[size];
		double[] high = new double[size];
		double[] low = new double[size];
		double[] open = new double[size];
		double[] close = new double[size];
		double[] volume = new double[size];
			
		
		
		for (int i = 0; i < size; ++i) {
						
			
			
			if (candle.get(i).getTime().toString().contains("00:59:00")){
				System.out.println("No: " + i +" "+candle.get(i).getTime());
//				continue;
			}
			
			date[i] = ((Candle) candle.get(i)).getTime();
			low[i] = ((Candle) candle.get(i)).getLow();
			open[i] = ((Candle) candle.get(i)).getOpen();
			close[i] = ((Candle) candle.get(i)).getClose();
			volume[i] = ((Candle) candle.get(i)).getVolume();
			high[i] = ((Candle) candle.get(i)).getHigh();
		}
		
//		System.out.println("Date: " + date[0]);
		
		DefaultHighLowDataset data = new DefaultHighLowDataset("", date, high,
				low, open, close, volume);
		return data;
	}

//	public XYDataset createDataset2(ArrayList<Candle> candle){
//		int size = candle.size();
//		XYSeries series = new XYSeries("Average Size");
//		for (int i = 0; i < size; ++i) {
//		
//		series.add(candle.get(i).getTime(), 10.0);
//		series.add(40.0, 20.0);
//		series.add(70.0, 50.0);
//		}
//	}
	
	private JFreeChart createChart(DefaultHighLowDataset dataset) {
		JFreeChart chart = ChartFactory.createCandlestickChart(
				"Candlestick Demo", "Time", "Price", dataset, false);
		XYPlot x = (XYPlot) chart.getPlot();
//		x.setDataset(dataset)
		NumberAxis numAxis = (NumberAxis) x.getRangeAxis();
		numAxis.setAutoRangeIncludesZero(false);
		return chart;
	}

}