package icegalaxy.net;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class UI extends javax.swing.JFrame {
	private JButton analyseAll;
	private JButton stopBtn;
	private JButton startBtn;
	private JTextField profit;
	private JLabel jLabel4;
	private JTextArea result;
	private JScrollPane jScrollPane1;
	private JTextField cutLoss;
	private JTextField jTextField2;
	private JLabel jLabel3;
	private JTextField jTextField1;
	private JPanel jPanel2;
	private JTabbedPane jTabbedPane1;
	private JPanel jPanel1;
	private JLabel jLabel2;
	private JLabel jLabel1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UI inst = new UI();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public UI() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				analyseAll = new JButton();
				analyseAll.setText("Analyse All");
				analyseAll.setSize(50, 30);
				analyseAll.setFont(new java.awt.Font("Arial Black",0,15));
			}
			{
				jScrollPane1 = new JScrollPane();
				{
					result = new JTextArea();
					jScrollPane1.setViewportView(result);
				}
			}
			{
				jTabbedPane1 = new JTabbedPane();
				{
					jPanel1 = new JPanel();
					jTabbedPane1.addTab("Basic", null, jPanel1, null);
					GroupLayout jPanel1Layout = new GroupLayout((JComponent)jPanel1);
					jPanel1.setLayout(jPanel1Layout);
					jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Basic Setting", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
					jPanel1.setPreferredSize(new java.awt.Dimension(677, 149));
					jPanel1.setEnabled(false);
					{
						jLabel1 = new JLabel();
						jLabel1.setText("Profit");
					}
					{
						jLabel2 = new JLabel();
						jLabel2.setText("Cut Loss");
					}
					{
						cutLoss = new JTextField();
						cutLoss.setText("Cut Loss");
					}
					{
						profit = new JTextField();
						profit.setText("40");
					}
					jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
							.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(profit, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(jLabel1, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(cutLoss, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(jLabel2, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
					jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
							.addGroup(jPanel1Layout.createParallelGroup()
									.addGroup(jPanel1Layout.createSequentialGroup()
											.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
											.addGroup(jPanel1Layout.createSequentialGroup()
													.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)))
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(jPanel1Layout.createParallelGroup()
															.addGroup(jPanel1Layout.createSequentialGroup()
																	.addComponent(cutLoss, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
																	.addGroup(jPanel1Layout.createSequentialGroup()
																			.addComponent(profit, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))));
				}
				{
					jPanel2 = new JPanel();
					jTabbedPane1.addTab("MACD", null, jPanel2, null);
					GroupLayout jPanel2Layout = new GroupLayout((JComponent)jPanel2);
					jPanel2.setPreferredSize(new java.awt.Dimension(230, 149));
					jPanel2.setBorder(BorderFactory.createTitledBorder("MACD Setting"));
					jPanel2.setLayout(jPanel2Layout);
					{
						jTextField1 = new JTextField();
						jTextField1.setText("Cut Loss");
					}
					{
						jLabel3 = new JLabel();
						jLabel3.setText("Cut Loss");
					}
					{
						jTextField2 = new JTextField();
						jTextField2.setText("40");
					}
					{
						jLabel4 = new JLabel();
						jLabel4.setText("Profit");
					}
				jPanel2Layout.setVerticalGroup(jPanel2Layout.createSequentialGroup()
						.addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						    .addComponent(jTextField2, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(jLabel4, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						    .addComponent(jTextField1, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(jLabel3, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
				jPanel2Layout.setHorizontalGroup(jPanel2Layout.createSequentialGroup()
						.addGroup(jPanel2Layout.createParallelGroup()
						    .addGroup(jPanel2Layout.createSequentialGroup()
						        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
						    .addGroup(jPanel2Layout.createSequentialGroup()
						        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup()
						    .addGroup(jPanel2Layout.createSequentialGroup()
						        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
						    .addGroup(jPanel2Layout.createSequentialGroup()
						        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))));
				}
			}
			{
				stopBtn = new JButton();
				stopBtn.setText("Stop");
				stopBtn.setFont(new java.awt.Font("Arial Black",0,15));
			}
			{
				startBtn = new JButton();
				startBtn.setText("Start");
				startBtn.setFont(new java.awt.Font("Arial Black",0,15));
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(startBtn, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
				    .addComponent(stopBtn, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
				    .addComponent(analyseAll, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
				.addContainerGap());
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
				        .addGap(75)
				        .addComponent(startBtn, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(stopBtn, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(analyseAll, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
				    .addGroup(thisLayout.createSequentialGroup()
				        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 768, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(12, Short.MAX_VALUE));
			thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {analyseAll, stopBtn, startBtn});
			thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {analyseAll, stopBtn, startBtn});
			pack();
			this.setSize(800, 600);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}

}
