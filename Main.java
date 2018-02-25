package gator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

import org.jdatepicker.impl.JDatePickerImpl;


public class Main {

	public static void main(String ... strings) throws IOException,
			InterruptedException {
		
		JTextArea console = new JTextArea(30, 45);//30 and 45 - console size
		console.setEditable(false);
		Commands cmd = new Commands();//an object of Commands class
		WriteToExcel write = new WriteToExcel();
		WriteApkVersion writeApk = new WriteApkVersion();
		CreateExcel excel = new CreateExcel();
		RedirectConsoleTo rct = new RedirectConsoleTo();//an object of RedirectConsoleTo class
		Timer t = new Timer();
		
		String devID[] = cmd.devices();//Array with devices
	
		Elements element = new Elements();//an object of a class 'Elements'		
		JDatePickerImpl datePicker = element.datePicker();//calls method datePicker() from Elements class
		JCheckBox cbGetReport = element.cbGetReport();
		JCheckBox cbPullLogs = element.cbPullLogs();
		JSpinner timeSpinner = element.timeSpinner();
		JProgressBar pbReport = element.pbReport(devID);
		JProgressBar pbApk = element.pbApk(devID);
		JProgressBar pbPullLogs = element.pbPullLogs(devID);
		JButton btnOpenFolder = element.btnOpenFolder(); 
		JButton btnGetReport = element.btnGetReport(console, btnOpenFolder, pbReport, write, cmd, devID, excel);
		JButton btnGetApkVersion = element.btnGetApkVersion(console, btnOpenFolder, pbApk, writeApk, cmd, devID, excel);
		JButton btnTimer = element.btnTimer(console, btnOpenFolder, pbReport,  pbPullLogs, write, cmd, devID, excel, t, datePicker, cbGetReport, cbPullLogs, timeSpinner);
		JButton refresh = new JButton("Refresh");
		JButton btnRemoveLogs = element.btnRemoveLogs(console, cmd, devID);
		JButton stopACT = element.btnStopACT(console, cmd, devID);
		JButton copyActLogs = element.btnCopyActLogs(console, cmd, devID);
		JPanel panelButtonsReporting = element.panelButtonsReporting(console, cmd, devID, btnGetReport, btnGetApkVersion, btnRemoveLogs, stopACT, copyActLogs, btnOpenFolder, pbReport, pbApk);
		JPanel panelButtonsTimer = element.panelButtonsTimer(devID, btnTimer, datePicker, cbGetReport, cbPullLogs, timeSpinner, pbPullLogs);
		JPanel panelButtons = element.panelButtons(devID, panelButtonsReporting, panelButtonsTimer, refresh);
		JFrame frame = element.frame(console, panelButtons);
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {//if 'Refresh' button is pressed...
						frame.dispose();//...frame will be closed ...
						try {
							main();// ...and restarted again
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}			
			}		
		});

		rct.redirectConsoleTo(console);//redirects text to UI console

		cmd.printDevices();//calls method printDevices() from Commands class - prints devices info to console
	}
}