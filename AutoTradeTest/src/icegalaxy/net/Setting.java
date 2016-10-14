package icegalaxy.net;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Setting extends JFrame {

	private JTextField shortTimeBase;
	private JTextField mediumTimeBase;
	private JTextField longTimeBase;
	private JTextField cutLoss;
	private JTextField greatProfit;
	private JTextField maxContracts;
	private JTextField dateTested;
	private JTextField startTestingDate;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton analyseAll;
	public static JTextArea result;
	private JScrollPane resultPane;
	private JCheckBox ruleMAcheckBox;
	private JCheckBox ruleMA2checkBox;
	private JCheckBox ruleMACDcheckBox;
	private JCheckBox ruleRSIcheckBox;
	private JCheckBox ruleRSI5checkBox;
	private JCheckBox ruleWTcheckBox;
	private JCheckBox ruleSynccheckBox;
	
	private JRadioButton sidewayBtn;
	private JRadioButton trendingBtn;
	private ButtonGroup btnGroup;

	
	//seems like this: 11periods (as below); 30min per period
	public static double quantity[][] = new double[11][30];

	public static int quantityPeriods[] = { 0, 15, 60, 90, 120, 150, 180, 210,
			240, 270, 285 };
	public static double quantities[] = new double[11];
//	public static String index = "FHI";
	public static String index = "HSF";
	public static String dataBase = "AutoTradeTest";
//	public static String dataBase = "Analyse";
	private static int shortTB;
	private static int mediumTB;
	private static int longTB;

	private String fileName = AutoTradeDB.getToday();

	public static SQLite asql;

	public Setting() {
		super("Tactics Analyser with Multiple Contracts");

		setLayout(new FlowLayout());

		result = new JTextArea();
		shortTimeBase = new JTextField("1",35);
		mediumTimeBase = new JTextField("3", 35);
		longTimeBase = new JTextField("5", 35);
		cutLoss = new JTextField("40", 35);
		greatProfit = new JTextField("40", 35);
		maxContracts = new JTextField("1", 35);
		dateTested = new JTextField(fileName, 35);
		startTestingDate = new JTextField("110614", 35);
		startBtn = new JButton("Start");
		stopBtn = new JButton("Stop");

		ruleMAcheckBox = new JCheckBox("Rule MA", false);
		ruleMA2checkBox = new JCheckBox("Rule MA2", false);
		ruleMACDcheckBox = new JCheckBox("Rule MACD", false);
		ruleRSIcheckBox = new JCheckBox("Rule RSI", false);
		ruleRSI5checkBox = new JCheckBox("Rule RSI5", false);
		ruleWTcheckBox = new JCheckBox("Rule Wave", false);
		ruleSynccheckBox = new JCheckBox("Rule Sync", false);

		trendingBtn = new JRadioButton("Trending");
		sidewayBtn = new JRadioButton("Sideway");
		trendingBtn.setSelected(true);
		
		btnGroup = new ButtonGroup();
		btnGroup.add(trendingBtn);
		btnGroup.add(sidewayBtn);
		
		
		
		
		
		analyseAll = new JButton("Anaylse All");

		add(new JLabel("Short Time Base"));
		add(shortTimeBase);

		add(new JLabel("Medium Time Base"));
		add(mediumTimeBase);

		add(new JLabel("Long Time Base"));
		add(longTimeBase);

		add(new JLabel("Cut Loss Point"));
		add(cutLoss);

		add(new JLabel("Great Profit Point"));
		add(greatProfit);

		add(new JLabel("Max Contracts"));
		add(maxContracts);

		add(new JLabel("Date Tested"));
		add(dateTested);

		add(new JLabel("Start Testing Date"));
		add(startTestingDate);

		add(ruleMAcheckBox);
		add(ruleMA2checkBox);
		add(ruleMACDcheckBox);
		add(ruleRSIcheckBox);
		add(ruleRSI5checkBox);
		add(ruleWTcheckBox);
		add(ruleSynccheckBox);
		
		add(trendingBtn);
		add(sidewayBtn);

		add(startBtn);
		add(stopBtn);
		add(analyseAll);
		result = new JTextArea();
		result.setRows(12);
		result.setColumns(50);
		resultPane = new JScrollPane(result);
		add(resultPane);

		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Global.noOfTrades = 0;
				Global.setCutLost(new Float(cutLoss.getText()));
				Global.setGreatProfit(new Float(greatProfit.getText()));
				Global.maxContracts = new Integer(maxContracts.getText());
				shortTB = new Integer(shortTimeBase.getText());
				mediumTB = new Integer(mediumTimeBase.getText());
				longTB = new Integer(longTimeBase.getText());

				Global.analysingAll = false;

				fileName = dateTested.getText();
				
				Global.setTableName(fileName);

				Global.runRuleMA = ruleMAcheckBox.isSelected();
				Global.runRuleMA2 = ruleMA2checkBox.isSelected();
				Global.runRuleMACD = ruleMACDcheckBox.isSelected();
				Global.runRSI = ruleRSIcheckBox.isSelected();
				Global.runRSI5 = ruleRSI5checkBox.isSelected();
				Global.runRuleWaveTheory = ruleWTcheckBox.isSelected();
				Global.ruleSync = ruleSynccheckBox.isSelected();

				runThreads();

			}
		});

		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.setRunning(false);

				System.out.println("Program is stopping in 1 minutes.");
			}
		});

		analyseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.balance = 0;
				Global.noOfTrades = 0;
				Global.totalBalance = 0;
				int totalTrades = 0;
				AnalyseAll aa = new AnalyseAll();
				ArrayList<String> al = aa.getTables();

//				int startDate = new Integer(startTestingDate.getText());
//
//				for (int i = 0; i < al.size(); i++) {
//					if (new Integer(al.get(i).substring(0, 6)) < startDate) {
////						System.out.println("Remove " + al.get(i));
////						try {
////							Thread.sleep(100);
////						} catch (InterruptedException e1) {
////							// TODO Auto-generated catch block
////							e1.printStackTrace();
////						}
//						al.remove(i);
//						i--;
//					}
//				}

				String s = "";

//				asql = new SQLite("AutoTrade");
				asql = new SQLite(Setting.dataBase);

				for (int i = 0; i < al.size(); i++) {
				
					Global.setCutLost(new Float(cutLoss.getText()));
					Global.setGreatProfit(new Float(greatProfit.getText()));
					Global.maxContracts = new Integer(maxContracts.getText());
					shortTB = new Integer(shortTimeBase.getText());
					mediumTB = new Integer(mediumTimeBase.getText());
					longTB = new Integer(longTimeBase.getText());

					Global.analysingAll = true;

					Global.runRuleMA = ruleMAcheckBox.isSelected();
					Global.runRuleMA2 = ruleMA2checkBox.isSelected();
					Global.runRuleMACD = ruleMACDcheckBox.isSelected();
					Global.runRSI = ruleRSIcheckBox.isSelected();
					Global.runRSI5 = ruleRSI5checkBox.isSelected();
					Global.runRuleWaveTheory = ruleWTcheckBox.isSelected();
					Global.ruleSync = ruleSynccheckBox.isSelected();


					fileName = al.get(i).substring(3, 9);

					
					Global.setTableName(fileName);
					
					
					runThreads();

					// wait for the day to end
					while (Global.isRunning()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {

							e1.printStackTrace();
						}
					}

					s = s + al.get(i) + ": " + Global.balance + " trades: "
							+ Global.noOfTrades + "\n";
					


					Global.totalBalance += Global.balance;
					Global.balance = 0;
					totalTrades += Global.noOfTrades;
					Global.noOfTrades = 0;
				}
				result.setText(s + "\n" + "Total Balance: "
						+ Global.totalBalance + " Total trades: " + totalTrades);
				asql.close();

				try {
					DB.playSound("applaus.wav");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

	}

	private synchronized void runThreads() {
		AutoTradeDB.setTime("00000000000000000");
		Global.setRunning(true);
		Global.setDayHigh(0);
		Global.setDayLow(99999);
		Global.isTrendingMrt = trendingBtn.isSelected();
		Global.isSidewayMrt = sidewayBtn.isSelected();

		int noOfThreads = 8;

		WaitAndNotify[] wan = new WaitAndNotify[noOfThreads];

		for (int i = 0; i < noOfThreads; i++) {
			wan[i] = new WaitAndNotify();
		}

		LoginThread login = new LoginThread(wan[wan.length - 2],
				wan[wan.length - 1]);

		StockDataController sdc = new StockDataController(fileName, wan[0]);
		TimePeriodDecider tpd = new TimePeriodDecider(wan[0], wan[1]);
//		RuleMA ruleMA_0 = new RuleMA(wan[1], wan[2], Global.runRuleMA);
//		ruleMA_0.setBufferPt(0);
//		RuleMA ruleMA_5 = new RuleMA(wan[12], wan[13], Global.runRuleMA);
//		ruleMA_5.setBufferPt(5);
//		RuleMA ruleMA_10 = new RuleMA(wan[13], wan[14], Global.runRuleMA);
//		ruleMA_10.setBufferPt(10);

		// RuleMA5 rulema5_0 = new RuleMA5(wan[21], wan[22], false);
		// rulema5_0.setBufferPt(0);
		// RuleMA5 rulema5_5 = new RuleMA5(wan[22], wan[23], false);
		// rulema5_5.setBufferPt(5);
		// RuleMA5 rulema5_10 = new RuleMA5(wan[23], wan[24], false);
		// rulema5_10.setBufferPt(10);

//		RuleEMA ruleEma_0 = new RuleEMA(wan[6], wan[7], false);
//		ruleEma_0.setBufferPt(0);

//		RuleMABackup backup = new RuleMABackup(wan[3], wan[4], false);

		// RuleMAReverse ruleMA_0 = new RuleMAReverse(wan[1], wan[2],
		// Global.runRuleMA);
		// ruleMA_0.setBufferPt(0);
		// RuleMAReverse ruleMA_5 = new RuleMAReverse(wan[12], wan[13],
		// Global.runRuleMA);
		// ruleMA_5.setBufferPt(5);
		// RuleMAReverse ruleMA_10 = new RuleMAReverse(wan[13], wan[14],
		// Global.runRuleMA);
		// ruleMA_10.setBufferPt(10);
//		RuleMA ruleMA_15 = new RuleMA(wan[15], wan[16], false);
//		ruleMA_15.setBufferPt(15);
//		ruleMACD macd = new ruleMACD(wan[2], wan[3], Global.runRuleMACD);
//		RuleMACD2 macd2 = new RuleMACD2(wan[20], wan[21], false);
		// RuleSync ruleSync = new RuleSync(wan[3], wan[4], Global.ruleSync);
		// RuleMA2 ruleMA2_5 = new RuleMA2(wan[4], wan[5], Global.runRuleMA2);
		// ruleMA2_5.setBufferPt(5);
//		RuleMA2 ruleMA2_0 = new RuleMA2(wan[11], wan[12], Global.runRuleMA2);
//		ruleMA2_0.setBufferPt(0);
		// RuleMA2 ruleMA2_10 = new RuleMA2(wan[14], wan[15],
		// Global.runRuleMA2);
		// ruleMA2_10.setBufferPt(10);
//		RuleMA2 ruleMA2_15 = new RuleMA2(wan[16], wan[17], false);
//		ruleMA2_15.setBufferPt(15);
		RuleRSI ruleRSI = new RuleRSI(wan[1], wan[2], Global.runRSI);

//		RuleMAKissGoogBye kiss = new RuleMAKissGoogBye(wan[17], wan[18], false); //bad

//		RuleRail rail = new RuleRail(wan[18], wan[19], false);

//		RuleBouncing bouncing = new RuleBouncing(wan[10], wan[11], false); //bad

//		RuleSameTrack track = new RuleSameTrack(wan[9], wan[10], false);

		// RuleMACupcake cupCakeShort = new RuleMACupcake(wan[6], wan[7], false,
		// StockDataController.getShortTB());
		// RuleMACupcake cupCakeMedium = new RuleMACupcake(wan[7], wan[8],
		// false,
		// StockDataController.getMediumTB());
		// RuleMACupcake cupCakeLong = new RuleMACupcake(wan[8], wan[9], false,
		// StockDataController.getLongTB());
		//
		//
		//
		// cupCakeShort.setName("Cupcake Short");
		// cupCakeMedium.setName("Cupcake Medium");
		// cupCakeLong.setName("Cupcake Long");

//		RuleSuddenBreakThrough sudden = new RuleSuddenBreakThrough(wan[13],
//				wan[12], false); // open contract�����D

//		RuleSuddenBreakThrough2 sudden2 = new RuleSuddenBreakThrough2(wan[14],
//				wan[13], false); // even
 
//		RuleDayOpen open = new RuleDayOpen(wan[14], wan[15], false);

//		RuleSupportAndResist sar = new RuleSupportAndResist(wan[4], wan[5],
//				false); // bad

		
		RuleEMA56 ema56 = new RuleEMA56(wan[3], wan[4], true); 
		RuleTest2 test2 = new RuleTest2(wan[2], wan[3], true); 
		RulePHigh pH = new RulePHigh(wan[4], wan[5], false); 
		RuleDanny danny = new RuleDanny(wan[5], wan[6], true);
//		RuleTest3 test3 = new RuleTest3(wan[22], wan[23], false); //Good
//		RuleBouncing2 bouncing2 = new RuleBouncing2(wan[8], wan[9], false); //���M
//		RuleBouncing3 bouncing3 = new RuleBouncing3(wan[19], wan[20], false);
		// RuleRateOfChange roc = new RuleRateOfChange(wan[25], wan[26], false);
		// RuleMADonut donut = new RuleMADonut(wan[10], wan[11], false);

//		RuleSeconds sec = new RuleSeconds(wan[23], wan[24], false);
		
		Runnable[] r = {sdc, tpd,  login, ruleRSI, ema56, test2, pH, danny};
		
		Thread[] t = new Thread[noOfThreads];

		for (int i = 0; i < noOfThreads; i++) {
			t[i] = new Thread(r[i]);
			t[i].start();
		}

	}

	public static int getShortTB() {
		return shortTB;
	}

	public static int getMediumTB() {
		return mediumTB;
	}

	public static int getLongTB() {
		return longTB;
	}

}
