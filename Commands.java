package gator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands {

	public Commands() {
		super();
	}

	public void printDevices() {//for displaying list of devices in console

		try {
			Process p = Runtime.getRuntime().exec("adb devices -l");//sends adb command
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {//data reading using BufferedReader
			String	line1=line.replace("device product:", "").replace("model:", "").replace("  ", ">");
				System.out.println(line1);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}

	// //////

	String[] devices() throws InterruptedException, IOException {//devices array

		Process p = Runtime.getRuntime().exec("adb devices");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;

		StringBuilder sb = new StringBuilder();//makes changing in strings
		while ((line = reader.readLine()) != null) {
			if(!line.contains("*")&&!line.contains("adb server is out of date.  killing...")){//if there are no forbidden text
			sb.append(line.replace("\t", "/")).append("/");//String Builder append current line to the previous line
			}
		}
		return sb.toString().split("/");
	}

	// ////
	String model(String id) throws IOException, InterruptedException {//returns model name
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ro.product.model");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ////
	String adbAndroid(String id) throws IOException, InterruptedException {//returns Android version
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ro.build.version.release");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ////
	String operatorName(String id) throws IOException, InterruptedException {//returns operator name
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop gsm.sim.operator.alpha");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ////
	String serialNum(String id) throws IOException, InterruptedException {//returns device serial number (e.g., R32HB002YDV)
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ril.serialnumber");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ///
	String softwareAP(String id) throws IOException, InterruptedException {//returns AP version
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ro.build.PDA");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ///
	String softwareCSC(String id) throws IOException, InterruptedException {//returns CSC version
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ril.official_cscver");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ///
	String hardwareVer(String id) throws IOException, InterruptedException {//returns HW revision
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell getprop ril.hw_ver");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			return line;
		}
		return line;
	}

	// ///
	String findACTlogs(String id, String model) throws IOException,////returns list of logs in folders 'ACT_LOGS' and 'ACT_LOGS/OLD_LOGS'
			InterruptedException {

		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell ls  /sdcard");
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = input.readLine()) != null) {
			if ("".equals(line))
				break;
			sb.append(line).append('\n');
		}

		if (Arrays.toString(sb.toString().split("\n")).contains("ACT")) {//will check whether 'ACT' folder exists
			Process p1 = Runtime.getRuntime().exec("adb -s " + id + " shell ls  /sdcard/ACT_LOGS/");
			p1.waitFor();
			try (BufferedReader input1 = new BufferedReader(
					new InputStreamReader(p1.getInputStream()))) {
				StringBuilder sb1 = new StringBuilder();
				String line1;
				while ((line1 = input1.readLine()) != null) {
					if ("".equals(line1))
						break;
					sb1.append(line1).append('\n');//all the files in 'ACT_LOGS' folder
				}

				StringBuilder sb0 = new StringBuilder();
				if (Arrays.toString(sb1.toString().split("\n")).contains("OLD_LOGS")) {//will check whether 'OLD_LOGS' folder exists
					Process p0 = Runtime.getRuntime().exec("adb -s " + id + " shell ls  /sdcard/ACT_LOGS/OLD_LOGS/");
					p0.waitFor();
					try (BufferedReader input0 = new BufferedReader(
							new InputStreamReader(p0.getInputStream()))) {

						String line0;
						while ((line0 = input0.readLine()) != null) {
							if ("".equals(line0))
								break;
							sb0.append(line0).append('\n');//all the files in 'OLD_LOGS' folder
						}
					}
				}

				StringBuilder sbAll = new StringBuilder();
				sbAll = sb1.append(sb0);
				
				
				String[] array = sbAll.toString().split("\n");//array with list of files from folders 'ACT_LOGS' and 'ACT_LOGS/OLD_LOGS'
				
				StringBuilder sb2 = new StringBuilder();
				for (int i = 0; i < array.length; i++) {
					if (array[i].contains("CRASH") || array[i].contains("ANR")) {//if array contains CRASH or ANR
						
						String unnecessary = "ACT_AUTO_SCRIPT.txt "; 
						
						if(array[i].contains(unnecessary)){//if unnecessary text appears in array this text will be removed
							array[i]=array[i].replace(unnecessary, "");
							sb2.append(array[i]).append("/");
						}else{
							sb2.append(array[i]).append("/");
						}
					}
				}
				String[] array3 = sb2.toString().split("/");

				for (int j = 0; j < array3.length; j++) {
					if (array3[j].contains(".zip")) {
						array3[j] = array3[j].substring(0,
								array3[j].indexOf(".zip"));//removes tail ".zip" from all elements of array3
					}
				}

				StringBuilder sb3 = new StringBuilder();
				HashSet<String> mSet = new HashSet<String>();//some magic for removing duplicates 
				Collections.addAll(mSet, array3);//duplicates appears if we have logs folder and zip with same names

				for (String word : mSet) {
					sb3.append(word).append(",").append(" ").append("\r\n");
				}
				return sb3.toString().substring(0, sb3.length() - 2);//here is string with ACT logs (without duplicates)
			}
		} else {
			return " ";//returns " " if folder 'ACT' dosen't exist
		}
	}

	// ///
	
	public void pullACTlogs(String id, String model, String date, String ap)
			throws IOException, InterruptedException {
		
		///Folders
		
		String folderACTName = "ACTlogs_" + date;//new folder name 
		File folderACT = new File(System.getProperty("user.dir") + "\\" + folderACTName);//'System.getProperty("user.dir") + "\\" + folderACTName' - path to new folder
		folderACT.mkdirs();//creates new directory 'ACTlogs' (if such directory doesn't exist)
		
		String folderModelName = folderACTName + "\\" + "[" + model + "]"; 
		File folderModel = new File(System.getProperty("user.dir") + "\\" + folderModelName);
		folderModel.mkdirs();//new folder with model name (inside previous folder)
		
		String folderLogsName = folderModelName + "\\" + "[" + ap + "]";
		File folderLogs = new File(System.getProperty("user.dir") + "\\" + folderLogsName);
		folderLogs.mkdirs();//new folder with AP version name (inside previous folder)
		
		

		String to = System.getProperty("user.dir") + "\\" + "ACTlogs_" + date
				+ "\\" + "[" + model + "]" + "\\" + "[" + ap + "]";//path to directory with logs
		

		Process p = Runtime.getRuntime().exec("adb -s " + id + " shell ls  /sdcard");//list of files in device internal storage
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = input.readLine()) != null) {
			if ("".equals(line))
				break;
			sb.append(line).append('\n');
		}

		if (Arrays.toString(sb.toString().split("\n")).contains("ACT")) {//will check whether 'ACT_LOGS' folder exists or not
			
			Process pLog = Runtime.getRuntime().exec("adb -s " + id + " pull /sdcard/ACT_LOGS/" + " " + "\"" + to + "\"");//logs copying to specified folder
			
			pLog.waitFor();
			
		}
	}
	

	// ///

	String testName(String id, String model) throws IOException,
			InterruptedException {
		String line;
		String name = null;
		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell ls  /sdcard");
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		StringBuilder sb = new StringBuilder();

		while ((line = input.readLine()) != null) {
			if ("".equals(line))
				break;
			sb.append(line).append('\n');
		}
		if (Arrays.toString(sb.toString().split("\n")).contains("ACT")) {
			name = "ACT";
		} else if (Arrays.toString(sb.toString().split("\n")).contains(
				"StpSetting.ini")) {
			name = "TA2L";
		} else {
			name = "No Test";
		}
		return name;
	}

	// ////

	String userName() {
		return System.getProperty("user.name");
	}

	// /////

	String date() {
		Date dt = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(dt);
	}

	String scriptName(String testName) {
		String script = null;
		if (testName == "ACT") {
			script = "Exploratory";
		} else if (testName == "TA2L") {
			script = "Aging";
		} else {
			script = " ";
		}
		return script;
	}

	String imei(String id) throws IOException, InterruptedException {

		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell service call iphonesubinfo 1");
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = input.readLine()) != null) {
			if ("".equals(line))
				break;
			sb.append(line).append('\n');
		}
		String[] array = sb.toString().split("\n");
		String[] array2 = Arrays.toString(array).split(" ");
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < array2.length; i++) {

			if (array2[i].contains(".")) {
				sb2.append(array2[i]).append("");
			}
		}
		StringBuilder sb3 = new StringBuilder();
		Pattern pat = Pattern.compile("\\d+");
		Matcher matcher = pat.matcher(sb2.toString());
		while (matcher.find()) {
			sb3.append(matcher.group());
		}
		return sb3.toString();
	}

	// //////
	String result(String id) throws IOException, InterruptedException {//returns number of iterations for ACT test

		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell ls  /sdcard");
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = input.readLine()) != null) {
			if ("".equals(line))
				break;
			sb.append(line).append('\n');
		}

		if (Arrays.toString(sb.toString().split("\n")).contains("ACT")) {
			Process p1 = Runtime.getRuntime().exec(
					"adb -s " + id + " shell ls  /sdcard/ACT_LOGS/");
			p1.waitFor();
			try (BufferedReader input1 = new BufferedReader(
					new InputStreamReader(p1.getInputStream()))) {
				StringBuilder sb1 = new StringBuilder();
				String line1;
				while ((line1 = input1.readLine()) != null) {
					if ("".equals(line1))
						break;
					sb1.append(line1).append('\n');
				}

				StringBuilder sbRes = new StringBuilder();
				if (Arrays.toString(sb1.toString().split("\n")).contains("PASS")) {//if we have PASS fail...
					Process pRes = Runtime.getRuntime()
							.exec("adb -s " + id
									+ " shell cat /sdcard/ACT_LOGS/PASS");
					pRes.waitFor();
					try (BufferedReader inputRes = new BufferedReader(
							new InputStreamReader(pRes.getInputStream()))) {

						String lineRes;
						while ((lineRes = inputRes.readLine()) != null) {
							if ("".equals(lineRes))
								break;
							sbRes.append(lineRes).append("/");
						}
					}
					String results[] = sbRes.toString().split("/");
					return results[3];
				} else if (Arrays.toString(sb1.toString().split("\n")).contains("FAIL")) {//...or FAIL...
					Process p0 = Runtime.getRuntime()
							.exec("adb -s " + id
									+ " shell cat /sdcard/ACT_LOGS/FAIL");
					p0.waitFor();
					try (BufferedReader input0 = new BufferedReader(
							new InputStreamReader(p0.getInputStream()))) {

						String line0;
						while ((line0 = input0.readLine()) != null) {
							if ("".equals(line0))
								break;
							sbRes.append(line0).append("/");
						}
					}
					String results[] = sbRes.toString().split("/");
					return results[3];
				} else if (Arrays.toString(sb1.toString().split("\n")).contains("PROGRESS")) {//...or PROGRESS...
					Process pRes = Runtime.getRuntime().exec(
							"adb -s " + id
									+ " shell cat /sdcard/ACT_LOGS/PROGRESS");
					pRes.waitFor();
					try (BufferedReader inputRes = new BufferedReader(
							new InputStreamReader(pRes.getInputStream()))) {

						String lineRes;
						while ((lineRes = inputRes.readLine()) != null) {
							if ("".equals(lineRes))
								break;
							sbRes.append(lineRes).append("/");
						}
					}
					String results[] = sbRes.toString().split("/");//...we take text from these files...
					return results[3];//...and return only quantity of iterations
				} else {
					return " ";//if there are no PASS, FAIL or PROGRESS files - returns empty string
				}
			}
		} else {
			return " ";//if there is no ACT_LOGS folder - returns empty string
		}
	}


	String getActivity(String id, String app) throws IOException, InterruptedException {//returns activity name for specified app

		Process p = Runtime.getRuntime().exec(
				"adb -s " + id + " shell \"cmd package resolve-activity --brief " + app + " | tail -n 1\"");//package activity name
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		if ((line = reader.readLine()) != null) {
			return line;
		} else {
			return "null";
		}
	}

	String[] appNames(String id) throws InterruptedException, IOException {//array with packages names and it versions [packageName, version, packageName, version, ...] 

		Process p = Runtime.getRuntime().exec("adb -s " + id + " shell \"dumpsys package packages | grep -E 'Package | versionName'\"");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;

		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("/");
		}
		return sb.toString().split("/");
	}

}
