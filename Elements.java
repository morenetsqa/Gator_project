package gator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.Timer;
import javax.swing.JFormattedTextField.AbstractFormatter;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class Elements {
	
	//actions for timer functionality (method JButton btnTimer)
	public void timerAction(JTextArea console, Commands cmd, String[] devID, JProgressBar pbPullLogs, JCheckBox cbPullLogs) {
		
		if(devID.length-1<=0){
			return;
		}

		javax.swing.Timer timer = new javax.swing.Timer(5, new ActionListener() {//timer instead of 'for' loop =)
					private int counter = 0;

					@Override
					public void actionPerformed(ActionEvent e) {
						counter = counter += 2;
						try {
							String model = cmd.model(devID[counter - 1]);//model name
							String ap = cmd.softwareAP(devID[counter - 1]);//AP version
							String date = cmd.date();//Current date
							
							Date dt = new Date(System.currentTimeMillis());
							SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//time format
							String time = sdf.format(dt).toString();//converts time to String

							Process p = Runtime.getRuntime().exec("adb -s " + devID[counter - 1]
											+ " shell \"am broadcast -a com.salab.act.intent.STOP_ACT\"");//runs adb shell command
							p.waitFor();

							console.setText(console.getText() + "\r\n" + time +" === Test finished: " + model + " (Serial No.: "
									+ devID[counter - 1] + ") ===" + "\r\n");

							if(cbPullLogs.isSelected()){//checks whether 'Copy logs' checkbox is checked
							
							cmd.pullACTlogs(devID[counter - 1], model, date, ap);//method pullACTlogs from Commands class

							pbPullLogs.setValue(counter / 2);//variable for JProgressBar pbPullLogs

							console.setText(console.getText() + "\r\n" + "=== Logs for " + model + " (Serial No.: "
									+ devID[counter - 1] + ") were pulled out ===" + "\r\n");
							}
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}
						if (counter == devID.length - 1) {
							if (devID.length - 1 > 0) {
								Date dt = new Date(System.currentTimeMillis());
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = sdf.format(dt).toString();

								console.setText(console.getText() + "\n"
										 + time + " Completed. Please, check 'ACTlogs' folder " + "\n");
							}
							((Timer) e.getSource()).stop();
						}
					}
				});
		timer.start();
	}
	
	/////

	public void getReport(JTextArea console, JButton btnOpenFolder, JProgressBar pbReport, WriteToExcel write, Commands cmd, String[] devID, CreateExcel excel) {
	
		Date dt = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(dt).toString();
		
		if (devID.length - 1 <= 0) {
			console.setText(console.getText() + "\n" + "There are no available devices =(");
			return;
		} 
		
		try {
			excel.createWorkbook(cmd.date(), console);
			console.setText(console.getText() + time + "\n" + " adding data to report ...");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		javax.swing.Timer timer = new javax.swing.Timer(5,
				new ActionListener() {
					private int counter = 0;

					@Override
					public void actionPerformed(ActionEvent e) {
						counter = counter += 2;
						pbReport.setValue(counter / 2);//variable for progress bar pbReport
						try {						
							String model = cmd.model(devID[counter - 1]);
							String serialNum = cmd.serialNum(devID[counter - 1]);
							String hw = cmd.hardwareVer(devID[counter - 1]);
							String sw = cmd.softwareAP(devID[counter - 1])
									+ "/" + cmd.softwareCSC(devID[counter - 1]);
							String adbAndroid = cmd.adbAndroid(devID[counter - 1]);
							String operatorName = cmd.operatorName(devID[counter - 1]);
							String findLogs = cmd.findACTlogs(devID[counter - 1],
									cmd.model(devID[counter - 1]));
							String testName = cmd.testName(devID[counter - 1],
									cmd.model(devID[counter - 1]));
							String date = cmd.date();
							String scriptName = cmd.scriptName(testName);
							String userName = cmd.userName();
							String result = cmd.result(devID[counter - 1]);

							console.setText(console.getText() + "\r\n"
									+ "=== Getting data for " + model + " (Serial No.: " + devID[counter - 1] + ") ===" + "\r\n");

							if (testName == "No Test") {
								console.setText(console.getText() + "\r\n" + "*Cannot find test for " + model
										+ " (" + devID[counter - 1] + ") device =(*");
							}

							console.setText(console.getText() + "\r\n" + "Logs from " + model + ": " + "\r\n" + findLogs + "\r\n");

							String data[] = {testName, model, serialNum, hw,
									adbAndroid, sw, operatorName, findLogs,
									date, scriptName, userName, result};//data for daily report filling

							write.writeResult(System.getProperty("user.dir"),//method from WriteToExcel class
									"DailyReport(AutoTests)_" + date + ".xlsx",
									"Report", data);

							console.setText(console.getText() + "\r\n" + "=== Completed for " + model + " (Serial No.: "
									+ devID[counter - 1] + ") ===" + "\r\n");
						} catch (IOException | InterruptedException e1) {
							e1.printStackTrace();
						}
						if (counter == devID.length - 1) {
							if (devID.length - 1 > 0) {
								Date dt = new Date(System.currentTimeMillis());
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = sdf.format(dt).toString();

								console.setText(console.getText() + "\n" + time + " Report created " + "\n");
								btnOpenFolder.setEnabled(true);//activates 'Open folder' button
							}
							((Timer) e.getSource()).stop();
						}
					}
				});

		timer.start();
	}
	
	//***************************************Frame***********************************************************************//

	JFrame frame(JTextArea console, JPanel panelButtons){
		JFrame frame = new JFrame("GATOR 2.15");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panelConsole(console), BorderLayout.WEST);
		frame.add(panelButtons, BorderLayout.EAST);
		frame.add(new JScrollPane(scrollPane(console)));
		frame.pack();
		frame.setVisible(true);
		
		return frame;
	}
	
	JScrollPane scrollPane(JTextArea console){
		JScrollPane scrollPane = new JScrollPane(console);
		return scrollPane;
	}
		
	//****************************************************DatePicker (org.jdatepicker library)***************************//		
	@SuppressWarnings("serial")
	JDatePickerImpl datePicker(){
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");


		class DateLabelFormatter extends AbstractFormatter {

			private String datePattern = "yyyy-MM-dd";
			private SimpleDateFormat dateFormatter = new SimpleDateFormat(
					datePattern);

			@Override
			public Object stringToValue(String text) throws ParseException {
				return dateFormatter.parseObject(text);
			}

			@Override
			public String valueToString(Object value) throws ParseException {
				if (value != null) {
					Calendar cal = (Calendar) value;
					return dateFormatter.format(cal.getTime());
				}
				return "Select date";	
			}
		}
	
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
				
		return datePicker;
	}
	
	//****************************************TimeSpinner**********************************************************************//
	JSpinner timeSpinner(){
		JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(new Date());
		return timeSpinner;
	}
		
	//*************************************Panels***********************************************************************//
	JPanel panelConsole(JTextArea console){//left panel where placed console
		JPanel panelConsole = new JPanel();
		panelConsole.setLayout(new GridBagLayout());
		panelConsole.add(console, new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));
		
		return panelConsole;
	}
	
	//right panel with buttons and other elements
	JPanel panelButtons(String[]devID, JPanel panelButtonsReporting, JPanel panelButtonsTimer, JButton refresh){
		
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new GridBagLayout());
		panelButtons.add(panelButtonsReporting, new GridBagConstraints(0, 0,
				15, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		panelButtons.add(panelButtonsTimer, new GridBagConstraints(0, 1, 15, 1,
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));
		panelButtons.add(refresh, new GridBagConstraints(0, 2, 15, 1,//'Refresh button'
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));
		panelButtons.add(labelDevices(devID), new GridBagConstraints(3, 3, 1, 1, 1, 1,
				GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 120, 0, 0), 0, 0));
				
		return panelButtons;	
	}
	

	JPanel panelButtonsReporting(JTextArea console, Commands cmd, String[] devID, JButton btnGetReport, JButton btnGetApkVersion, JButton btnRemoveLogs, JButton stopACT, JButton copyActLogs, 
			JButton btnOpenFolder, JProgressBar pbReport, JProgressBar pbApk){
		JPanel panelButtonsReporting = new JPanel();
		panelButtonsReporting.setLayout(new GridBagLayout());
		panelButtonsReporting.setBackground(Color.LIGHT_GRAY);
		
		panelButtonsReporting.add(btnGetReport, new GridBagConstraints(0, 0, 1,
				1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsReporting.add(pbReport, new GridBagConstraints(1, 0, 1, 1,
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsReporting.add(btnGetApkVersion, new GridBagConstraints(0,
				1, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsReporting.add(pbApk, new GridBagConstraints(1, 1, 1, 1, 1,
				1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));
		
		panelButtonsReporting.add(btnRemoveLogs, new GridBagConstraints(0, 2,
				1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsReporting.add(btnOpenFolder, new GridBagConstraints(1, 2,
				1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		panelButtonsReporting.add(stopACT, new GridBagConstraints(0, 3,
				1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		panelButtonsReporting.add(copyActLogs, new GridBagConstraints(1, 3,
				1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		
		return panelButtonsReporting;
	}
	
	
	JPanel panelButtonsTimer(String[] devID, JButton btnTimer, JDatePickerImpl datePicker, JCheckBox cbGetReport, JCheckBox cbPullLogs, JSpinner timeSpinner, JProgressBar pbPullLogs){
		JPanel panelButtonsTimer = new JPanel();
		panelButtonsTimer.setLayout(new GridBagLayout());
		panelButtonsTimer.setBackground(Color.LIGHT_GRAY);
		
		panelButtonsTimer.add(labelSelectDate(), new GridBagConstraints(0, 0, 1,
				1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsTimer.add(labelSelectTime(), new GridBagConstraints(1, 0, 1,
				1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsTimer.add(datePicker, new GridBagConstraints(0, 1, 1, 1, 1,
				1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsTimer.add(timeSpinner, new GridBagConstraints(1, 1, 1, 1,
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsTimer.add(btnTimer, new GridBagConstraints(0, 2, 1, 1, 1,
				1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));

		panelButtonsTimer.add(cbGetReport, new GridBagConstraints(1, 2, 1, 1,//checkbox 'Get report'
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0));
		
		panelButtonsTimer.add(cbPullLogs, new GridBagConstraints(1, 3, 15, 1,//checkbox 'Copy logs'
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		
		panelButtonsTimer.add(pbPullLogs, new GridBagConstraints(0, 3, 1, 1,//progress bar pbPullLogs
				1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		
		
		
		return panelButtonsTimer;	
	}	
	//**********************************************Buttons**************************************************************//	
	JButton btnGetReport(JTextArea console, JButton btnOpenFolder,  JProgressBar pbReport, WriteToExcel write, Commands cmd, String[] devID, CreateExcel excel){
		JButton btnGetReport = new JButton("Get report");
		btnGetReport.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {				
				getReport(console, btnOpenFolder, pbReport, write, cmd, devID, excel);				
			}
		});				
		return btnGetReport;
	}
	
	
	JButton btnOpenFolder(){
		JButton btnOpenFolder = new JButton("Open folder");		
		Desktop desktop = Desktop.getDesktop();
		File file = new File(System.getProperty("user.dir"));
		
		btnOpenFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});		
		btnOpenFolder.setEnabled(false);
		return btnOpenFolder;
	}
	
	
	JButton btnGetApkVersion(JTextArea console, JButton btnOpenFolder, JProgressBar pbApk, WriteApkVersion writeApk, Commands cmd, String[] devID, CreateExcel excel){
		JButton btnGetApkVersion = new JButton("Get apk versions");
		
		btnGetApkVersion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					excel.createApkWorkbook(cmd.date());
					console.setText(console.getText() + "\n" +" getting apk versions ...");
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				javax.swing.Timer timer = new javax.swing.Timer(5, new ActionListener() {
							private int counter;

							@Override
							public void actionPerformed(ActionEvent event) {
								counter = counter += 2;//we need every second element from devices array(e.g., array=[List of devices attached, 'device serial No.', device])

								if (counter > devID.length - 2) {//will stop when data added for all the devices from array
									((Timer) event.getSource()).stop();			
								}

								try {
									String model = cmd.model(devID[counter - 1]);//counter-1 ==> we skip first element from devices array ("List of devices attached")
									String ap = cmd.softwareAP(devID[counter - 1]);//AP version
									String date = cmd.date();//current date
									String[] arrApk = cmd.appNames(devID[counter - 1]);//Array with packages and its versions (depends on device serial No.)

									int sheetCounter = writeApk.createSheet(System.getProperty("user.dir"), "Apps_"
													+ date + ".xlsx", cmd.softwareAP(devID[counter - 1]), console);

									if (sheetCounter != 1) {//if sheet with given name doesn't exist
										
										Date dt = new Date(System.currentTimeMillis());
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										String time = sdf.format(dt).toString();//time at this moment
											
										console.setText(console.getText() + "\r\n"
												+ time + " === Getting packages for " + model + " (Serial No.: " + devID[counter - 1] + ") ===" + "\r\n");//prints text in console	
										((Timer) event.getSource()).stop();//interrupts loop over the models

										javax.swing.Timer timer1 = new javax.swing.Timer(1, new ActionListener() {
													private int k = 0;

													@Override
													public void actionPerformed(
															ActionEvent event1) {//starts loop over the array with packages (e.g., [package, package version])
														k = k += 2;
														pbApk.setMaximum(((arrApk.length) / 2) - 1);//maximum value for JProgressBar pbApk
														pbApk.setValue((k / 2));//variable for JProgressBar pbApk
														console.setText(console.getText() + "\r\n" + k/2 + ". "
																+ arrApk[k - 2].substring(arrApk[k - 2].indexOf("[") + 1,
																				arrApk[k - 2].indexOf("]")) + "\n");
														try {
															String[] data = {arrApk[k - 2].substring(arrApk[k - 2].indexOf("[") + 1, arrApk[k - 2].indexOf("]")),//cuts unnecessary characters from packageName
																	arrApk[k - 2 + 1].substring(arrApk[k - 2 + 1].indexOf("=") + 1),//cuts unnecessary characters from packages versions
																	cmd.getActivity(devID[counter - 1], arrApk[k - 2].substring(arrApk[k - 2].indexOf("[") + 1,//cuts unnecessary characters from activity name
																							arrApk[k - 2].indexOf("]"))) };

															writeApk.writeApk(System.getProperty("user.dir"), "Apps_" + date + ".xlsx", ap, data);//writes data to Excel
														} catch (IOException e1) {
															e1.printStackTrace();
														} catch (InterruptedException e1) {
															e1.printStackTrace();
														}

														if (k / 2 == (arrApk.length / 2)) {//if there are no packages
															((Timer) event1.getSource()).stop();//terminates loop over the packages
															
															Date dt = new Date(System.currentTimeMillis());
															SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
															String time = sdf.format(dt).toString();//time at the current moment
															
															console.setText(console.getText() + "\r\n"
																	+ time + " === Completed for " + model + " (Serial No.: " + devID[counter - 1] + ") ===" + "\r\n");

															if (counter <= devID.length - 2) {
																((Timer) event.getSource()).restart();//resumes loop over the models
															}else{
																Date dt1 = new Date(System.currentTimeMillis());
																SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
																String time1 = sdf1.format(dt1).toString();
																
																console.setText(console.getText() + "\r\n"
																		+ time1 + " === Completed. Apps_" + date + ".xlsx created===" + "\r\n");
																btnOpenFolder.setEnabled(true);//enables 'Open folder' button
															}
														}
													}
												});
										timer1.start();
									}
								} catch (IOException | InterruptedException e1) {
									e1.printStackTrace();
								}
							}
						});
				timer.start();//starts timer
			}
		});		
		return btnGetApkVersion;
	}
	
	//Button 'Remove logs' - deletes 'ACT_LOGS' and 'log' folder from devices internal storage
	JButton btnRemoveLogs(JTextArea console, Commands cmd, String[] devID){
		JButton btnRemoveLogs = new JButton("Remove logs");
		
		btnRemoveLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if (devID.length - 1 <= 0) {
					console.setText(console.getText() + "\n" + "There are no available devices =(");
					return;
				}
								
				Object[] options = {"No!", "Yes"};
				int n = JOptionPane.showOptionDialog(btnRemoveLogs, "Are you sure?", "A Silly Question",//pop-up
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);
				if(n==0){			
					console.setText(console.getText() + "\n" + " Canceled!");
					return;
					}
				
					javax.swing.Timer timer = new javax.swing.Timer(5, new ActionListener() {
								private int counter = 0;

								@Override
								public void actionPerformed(ActionEvent e) {								 								
									counter = counter += 2;
									
									try {
										String model = cmd.model(devID[counter - 1]);
										
										Date dt = new Date(System.currentTimeMillis());
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										String time = sdf.format(dt).toString();

										console.setText(console.getText() + "\r\n" + time + " === Removing 'ACT_LOGS' and 'log' folders from " + model
												+ " (Serial No.: " + devID[counter - 1] + ") ===" + "\r\n");

										Process pACT = Runtime.getRuntime().exec("adb -s " + devID[counter - 1] + " shell rm -r \"sdcard/ACT_LOGS\"");
										pACT.waitFor();
										
										Process pLog = Runtime.getRuntime().exec("adb -s " + devID[counter - 1] + " shell rm -r \"sdcard/log\"");
										pLog.waitFor();

										console.setText(console.getText() + "\r\n" + "=== Removed: " + model + " (Serial No.: " + devID[counter - 1] + ") ===" + "\r\n");
									} catch (IOException | InterruptedException e1) {
										e1.printStackTrace();
									}
									if (counter == devID.length - 1) {
										if (devID.length - 1 > 0) {
											Date dt = new Date(System.currentTimeMillis());
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											String time = sdf.format(dt).toString();

											console.setText(console.getText() + "\n" + time + " 'ACT_LOGS' and 'log' folders completely removed" + "\n");
										}
										((Timer) e.getSource()).stop();
									}
								}
							});
					timer.start();
			}
		});		
		return btnRemoveLogs;		
	}
	
	//Button 'Stop ACT' - ACT test termination
	JButton btnStopACT(JTextArea console, Commands cmd, String[] devID){
		JButton stopACT = new JButton("Stop ACT");
		
		stopACT.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
							
					if (devID.length - 1 <= 0) {
						console.setText(console.getText() + "\n" + "There are no available devices =(");
						return;
					} 

					javax.swing.Timer timer = new javax.swing.Timer(5, new ActionListener() {
								private int counter = 0;

								@Override
								public void actionPerformed(ActionEvent e) {
									counter = counter += 2;
									try {
										String model = cmd.model(devID[counter - 1]);
										
										Date dt = new Date(System.currentTimeMillis());
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										String time = sdf.format(dt).toString();

										Process p = Runtime.getRuntime().exec("adb -s " + devID[counter - 1]
												+ " shell \"am broadcast -a com.salab.act.intent.STOP_ACT\"");
										p.waitFor();

										console.setText(console.getText() + "\r\n" + time + " === Test finished: " + model + " (Serial No.: "
										+ devID[counter - 1] + ") ===" + "\r\n");
									} catch (IOException | InterruptedException e1) {
										e1.printStackTrace();
									}
									
									if (counter == devID.length - 1) {
										if (devID.length - 1 > 0) {
											Date dt = new Date(System.currentTimeMillis());
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											String time = sdf.format(dt).toString();

											console.setText(console.getText() + "\n" + time + " Test finished for all devices! " + "\n");
										}
										((Timer) e.getSource()).stop();
									}
								}
							});
					timer.start();
			}
		});	
		return stopACT;	
	}
	
	//Button 'Copy ACT logs' - activates logs copying from ACT_LOGS folder
	JButton btnCopyActLogs(JTextArea console, Commands cmd, String[] devID){
		JButton btnCopyActLogs = new JButton("Copy ACT logs");
		
		btnCopyActLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {							
					if (devID.length - 1 <= 0) {
						console.setText(console.getText() + "\n" + "There are no available devices =(");
						return;
					} 
					javax.swing.Timer timer = new javax.swing.Timer(5, new ActionListener() {
								private int counter = 0;

								@Override
								public void actionPerformed(ActionEvent e) {
									counter = counter += 2;
									try {
										String ap = cmd.softwareAP(devID[counter - 1]);
										String date = cmd.date();
										String model = cmd.model(devID[counter - 1]);
										
										Date dt = new Date(System.currentTimeMillis());
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										String time = sdf.format(dt).toString();

										cmd.pullACTlogs(devID[counter - 1], model, date, ap);//method of Commands class

										pbPullLogs(devID).setValue(counter / 2);

										console.setText(console.getText() + "\r\n" + time + " === Logs for " + model + " (Serial No.: "
												+ devID[counter - 1] + ") were pulled out ===" + "\r\n");
									} catch (IOException | InterruptedException e1) {
										e1.printStackTrace();
									}
									if (counter == devID.length - 1) {
										if (devID.length - 1 > 0) {
											Date dt = new Date(System.currentTimeMillis());
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											String time = sdf.format(dt).toString();

											console.setText(console.getText() + "\n" + time + " Copied for all devices " + "\n");
										}
										((Timer) e.getSource()).stop();
									}
								}
							});

					timer.start();
			}
		});		
		return btnCopyActLogs;		
	}
	
	//Button 'Start timer' - activates timer for specified date/time and terminates test (copy logs and takes report) at this time
	JButton btnTimer(JTextArea console, JButton btnOpenFolder, JProgressBar pbReport, JProgressBar pbPullLogs, WriteToExcel write, Commands cmd, String[] devID, CreateExcel excel, java.util.Timer t, JDatePickerImpl datePicker, JCheckBox cbGetReport, JCheckBox cbPullLogs, JSpinner timeSpinner){
			
		JButton btnTimer = new JButton("Start timer");
		btnTimer.addActionListener(new ActionListener() {//action listener executes code when button is pressed
			public void actionPerformed(ActionEvent event) {

				Object value = timeSpinner.getValue();//selected time in time spinner
				if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");//time format
					String time = format.format(date);
					
					console.setText(console.getText() + "\r\n" + "Timer ==> Date: " + datePicker.getJFormattedTextField().getText()
							+ "///Time: " + time + "\r\n");
					//*****Timer*****//
					try {					
						t.schedule(new TimerTask() {
							public void run() {
								timerAction(console, cmd, devID, pbPullLogs, cbPullLogs);
								if (cbGetReport.isSelected()) {//if 'Get report' checkbox is selected method getReport() will be executed
								getReport(console, btnOpenFolder, pbReport, write, cmd, devID, excel);
								}
							}
						}, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datePicker.getJFormattedTextField().getText() + " " + time));//timer will be started at day from datePicker and time from time spinner
					} catch (ParseException e) {
						console.setText(console.getText() + "\r\n" + "*Wrong date format. Please, select date and time*" + "\r\n");
					}
				}
			}
		});	
		return btnTimer;
	}
	
	//***************************************************Labels*******************************************************************//	
	JLabel labelDevices(String[] devID){//displays devices quantity (at the bottom)
		JLabel labelDevices = new JLabel();
		
		if ((devID.length - 1) / 2 == 1) {
			labelDevices.setText((devID.length - 1) / 2 + " device");
		} else {
			labelDevices.setText((devID.length - 1) / 2 + " devices");
		}
		return labelDevices;
	}
	
	
	JLabel labelSelectDate(){
		JLabel labelSelectDate = new JLabel("Date:");
		return labelSelectDate;
	}
	
	JLabel labelSelectTime(){
		JLabel labelSelectTime = new JLabel("Time:");
		return labelSelectTime;
	}

	//***********************************************Check boxes**********************************************************************//
	JCheckBox cbGetReport(){//Check box near 'Start timer' button (find in JButton btnTimer())
		JCheckBox cbGetReport = new JCheckBox("Get report");
		cbGetReport.setBackground(Color.LIGHT_GRAY);
		cbGetReport.setSelected(true);
		
		return cbGetReport;
	}
	
	JCheckBox cbPullLogs(){//Check box near 'Start timer' button (find in public void timerAction())
		JCheckBox cbPullLogs = new JCheckBox("Copy logs");
		cbPullLogs.setBackground(Color.LIGHT_GRAY);
		cbPullLogs.setSelected(true);
		
		return cbPullLogs;
	}
	
	//************************************************Progress bars*****************************************************************//
	
	JProgressBar pbReport(String[] devID){//progress bar during creation 'DailyReport(AutoTests).xlsx'
		JProgressBar pbReport = new JProgressBar();//an object of JProgressBar class
		pbReport.setStringPainted(true);
		pbReport.setMinimum(0);//min value = 0
		pbReport.setMaximum((devID.length - 1) / 2);//max value = devices quantity
		return pbReport;
	}
		
	JProgressBar pbApk(String[] devID){//progress bar during creation 'Apps.xlsx'
		JProgressBar pbApk = new JProgressBar();
		pbApk.setStringPainted(true);
		pbApk.setMinimum(0);//max value in JButton btnGetApkVersion()
		return pbApk;
	}
	
	JProgressBar pbPullLogs(String[] devID){//progress bar during copying logs
		JProgressBar pbPullLogs = new JProgressBar();
		pbPullLogs.setStringPainted(true);
		pbPullLogs.setMinimum(0);
		pbPullLogs.setMaximum((devID.length - 1) / 2);
		return pbPullLogs;
	}

}
