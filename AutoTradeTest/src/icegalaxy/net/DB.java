package icegalaxy.net;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import sun.audio.*;

public class DB {
	static Statement stmt;
	static Connection conn;

	public static String quote(String point) {
		return ("'" + point + "'");
	}

	public static void connect(String DBName) {

		try {
			 SQLiteConfig config = new SQLiteConfig();
		        // config.setReadOnly(true);   
		        config.setSharedCache(true);
		        config.enableRecursiveTriggers(true);
		    
		            
		        SQLiteDataSource ds = new SQLiteDataSource(config); 
		        ds.setUrl("jdbc:sqlite:Analyse.sqlite");
		        conn = ds.getConnection();
		        //ds.setServerName("sample.db");

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public static void playSound(String fileNameWithExt) throws IOException{
		InputStream in = new FileInputStream(fileNameWithExt);

		// Create an AudioStream object from the input stream.
		AudioStream as = new AudioStream(in);         

		// Use the static class member "player" from class AudioPlayer to play
		// clip.
		AudioPlayer.player.start(as); 
	}

	public static void close() {
		try {
			stmt.close();
			conn.close();
			System.out.println("Closed");
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static void executeUpdate(String s) {

		try {

			stmt.executeUpdate(s);

		} catch (SQLException e) {

			e.printStackTrace();

		}
	}

	// �H�U�Otxt file to String,�n�ΡI
	public static String fileToString(String filePath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sb = new StringBuffer();

		Reader reader = new InputStreamReader(new java.io.BufferedInputStream(
				fis));

		// read the stream
		int c = 0;
		try {
			while ((c = reader.read()) != -1)
				sb.append((char) c);
		} catch (IOException e) {

			e.printStackTrace();
		}

		String output = sb.toString();

		return output;

	}

	public static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	public static void stringtoFile(String folderName, String inputString,
			String outputFileNameWithExtension) {

		try {
			// Create file
			FileWriter fstream = new FileWriter(folderName + "\\"
					+ outputFileNameWithExtension);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(inputString);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	// �L���wfolder name, means current folder
	public static void stringtoFile(String inputString,
			String outputFileNameWithExtension) {

		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFileNameWithExtension);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(inputString);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	// ArrayList serialization, save ArrayList �� .dat file
	public static void arrayListToFile(String path,
			ArrayList<String> inputArrayList, String fileName) {

		/* serialize arraylist */
		try {
			System.out.println("serializing list");
			FileOutputStream fout = new FileOutputStream(path + "\\" + fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(inputArrayList);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> fileToArrayList(String fileName) {

		ArrayList<String> outputArrayList = new ArrayList<String>();

		/* unserialize arraylist */

		System.out.println("unserializing list");
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fin);
			outputArrayList = (ArrayList) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputArrayList;

	}

	// copied from Example Depot
	// http://www.exampledepot.com/egs/java.awt.datatransfer/ToClip.html
	// This method writes a string to the system clipboard. // otherwise it
	// returns null. public static void setClipboard(String str) {
	// StringSelection ss = new StringSelection(str);
	// Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); }
	public static String getClipboard() {
		Transferable t = null;
		try {
		t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);
		}catch (IllegalStateException e){ //�O��exception�ڥ[���A�]���չL�X�{
			return null;
		}
		
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
		}
		return "";
	}

	
	// copied from Example Depot
	// http://www.exampledepot.com/egs/java.awt.datatransfer/ToClip.html
	// This method writes a string to the system clipboard. // otherwise it
	// returns null.
	public static void setClipboard(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}
