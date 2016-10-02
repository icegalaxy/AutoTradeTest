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

	private ArrayList<Double> volume;

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
		
		volume = new ArrayList<Double>();
		


		try {

			Scanner sc = new Scanner(file);
					
			sc.useDelimiter(",");

			// ignore the title

			for (int i = 0; i < 5; i++)
				System.out.print(sc.next() + " ");
			
			sc.useDelimiter("\r");
			System.out.println(sc.next());

			while (sc.hasNext()) {

				// System.out.println(sc.next());
				sc.useDelimiter(",");
				try {

					sc.useDelimiter(",");
					
					time.add(sc.next());

					open.add(sc.nextDouble());

					high.add(sc.nextDouble());

					low.add(sc.nextDouble());

					close.add(sc.nextDouble());
					
//					System.out.println("Close: " + close.get(close.size()-1));

					sc.useDelimiter("\r");
					volume.add(Double.parseDouble(sc.next().replace(",", "")));


				} catch (Exception e) {

					e.printStackTrace();

					break;

				}

			}
			
			sc.close();

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

	public ArrayList<Double> getVolume() {
		return volume;
	}

	

}
