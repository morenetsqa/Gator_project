package gator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteApkVersion {
	
	//Excel file and sheets creating
	@SuppressWarnings("resource")
	Integer createSheet(String filePath, String fileName, String sheetName, JTextArea console)
			throws IOException {

		File file = new File(filePath + "\\" + fileName);//an object of File class

		FileInputStream inputStream = new FileInputStream(file);//FileInputStream is a class used for reading data from files (a heir of class InputStream)

		Workbook myWorkbook = null;

		String fileExtensionName = fileName.substring(fileName.indexOf("."));//part of fileName between '.' and last character

		if (fileExtensionName.equals(".xlsx")) {

			myWorkbook = new XSSFWorkbook(inputStream);//assigns workbook to file with .xlsx extension

		}

		else if (fileExtensionName.equals(".xls")) {

			myWorkbook = new HSSFWorkbook(inputStream);//assigns workbook to file with .xls extension
		}

		//checks whether sheet already exists
		for (int i = 0; i < myWorkbook.getNumberOfSheets(); i++) {

			if (myWorkbook.getSheetName(i).equals(sheetName) == true) {
				console.setText(console.getText() + "\r\n" + "*Sheet " + sheetName + " already exist*" + "\r\n");
				return 1;//counter - sheetCounter in JButton btnGetApkVersion (class Elements)
			}
		}
		//checks whether sheet is null to avoid nullPointerExeption
		if(sheetName==null){
			console.setText(console.getText() + "\r\n" + " Some device is disconnected =(");
			return 1;
		}
		Sheet apkSheet = myWorkbook.createSheet(sheetName);//creates new sheet in 'Apps' workbook

		Row rowApk = null;
		Font font = null;
		CellStyle style = null;

		rowApk = apkSheet.createRow(0);//creates row with 0 index
		style = myWorkbook.createCellStyle();
		
		style.setAlignment(CellStyle.ALIGN_CENTER);//cell alignment
		style.setBorderBottom(CellStyle.BORDER_THIN);//cell border
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());//border color
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		
		//Font and style for header
		font = myWorkbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor((short)42);
		rowApk.createCell(0).setCellValue("packageName");	
		rowApk.createCell(1).setCellValue("Version");
		rowApk.createCell(2).setCellValue("Activity");
		
		//Loop over header cells ('packageName', 'Version', 'Activity') 
		for (int j = 0; j <= 2; j++)
			rowApk.getCell(j).setCellStyle(style);
				
		inputStream.close();//inputStream is closed

		FileOutputStream outputStream = new FileOutputStream(file);//an object of FileOutputStream class for writing data into Excel file

		myWorkbook.write(outputStream);//writing header to excel

		outputStream.close();//outputStream is closed
		return 0;//counter - sheetCounter in JButton btnGetApkVersion (class Elements)
		
	}
	
	
	//Writing data to Excel
	public void writeApk(String filePath, String fileName, String sheetName, String[] dataToWrite)
			throws IOException {
		
		
		File file = new File(filePath + "\\" + fileName);

		FileInputStream inputStream = new FileInputStream(file);

		Workbook myWorkbook = null;

		String fileExtensionName = fileName.substring(fileName.indexOf("."));

		if (fileExtensionName.equals(".xlsx")) {

			myWorkbook = new XSSFWorkbook(inputStream);

		}

		else if (fileExtensionName.equals(".xls")) {

			myWorkbook = new HSSFWorkbook(inputStream);

		}
		
		CellStyle style = null;
		style = myWorkbook.createCellStyle();
		
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		
		Sheet sheet = myWorkbook.getSheet(sheetName);
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

		Row row = sheet.getRow(0);

		Row newRow = sheet.createRow(rowCount + 1);

		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = newRow.createCell(i);
			cell.setCellValue(dataToWrite[i]);
			cell.setCellStyle(style);
		}

		inputStream.close();

		FileOutputStream outputStream = new FileOutputStream(file);

		myWorkbook.write(outputStream);

		outputStream.close();

	}
}