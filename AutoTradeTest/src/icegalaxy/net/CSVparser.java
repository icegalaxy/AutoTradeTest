package icegalaxy.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVparser {

	private String fileName;
	private ArrayList<String> time;
	private ArrayList<Double> open;
	private ArrayList<Double> high;
	private ArrayList<Double> low;
	private ArrayList<Double> close;

	public CSVparser(String fileName) {

		this.fileName = fileName;
	}

	public void parseOHLC() {
		File file = new File(fileName);

		time = new ArrayList<String>();
		open = new ArrayList<Double>();
		high = new ArrayList<Double>();
		low = new ArrayList<Double>();
		close = new ArrayList<Double>();

		try {
			Scanner sc = new Scanner(file);
			sc.useDelimiter(",");

			// ignore the title
			for (int i = 0; i < 5; i++)
				sc.next();

		
			while (sc.hasNext()) {

				// System.out.println(sc.next());


				try {
					time.add(sc.next());
					open.add(sc.nextDouble());
					high.add(sc.nextDouble());
					low.add(sc.nextDouble());
					close.add(sc.nextDouble());
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> getTime() {
		return time;
	}

	public ArrayList<Double> getOpen() {
		return open;
	}

	public ArrayList<Double> getHigh() {
		return high;
	}

	public ArrayList<Double> getLow() {
		return low;
	}

	public ArrayList<Double> getClose() {
		return close;
	}

}
