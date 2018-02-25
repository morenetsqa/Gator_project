package gator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateExcel {
	
	//Report creation ("DailyReport(AutoTests)" file)
	public void createWorkbook(String date, JTextArea console) throws IOException {
	    try {
	    	File file = new File(System.getProperty("user.dir") + "\\DailyReport(AutoTests)_"+date+".xlsx");
	    	if(!file.exists()){//if file doesn't exist will be created new Excel file
	    	FileOutputStream fos = new FileOutputStream(file);//opens output stream
	    	FileInputStream fis = new FileInputStream(file);//opens intput stream

	        @SuppressWarnings("resource")
			XSSFWorkbook  workbook = new XSSFWorkbook();////assigns workbook to file with .xlsx extension
	     
	        Sheet apkSheet = workbook.createSheet("Report");//creates sheet "Report"

	        Row row = null;
			Font font = null;
			CellStyle style = null;

			row = apkSheet.createRow(0);
			style = workbook.createCellStyle();
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			font = workbook.createFont();
			font.setFontHeightInPoints((short) 10);
			font.setBold(true);
			style.setFont(font);
			style.setAlignment(CellStyle.ALIGN_CENTER);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			style.setFillForegroundColor((short)42);//font color
				
			row.createCell(0).setCellValue("Test");//headers	
			row.createCell(1).setCellValue("Model");
			row.createCell(2).setCellValue("Serial No.");	
			row.createCell(3).setCellValue("HW");	
			row.createCell(4).setCellValue("OS");	
			row.createCell(5).setCellValue("SW(AP/CSC)");	
			row.createCell(6).setCellValue("SIM1,SIM2");	
			row.createCell(7).setCellValue("Incident");	
			row.createCell(8).setCellValue("Date");	
			row.createCell(9).setCellValue("Script");	
			row.createCell(10).setCellValue("Tester");
			row.createCell(11).setCellValue("Iterations");	
			
			//Loop over all header cells
			for (int j = 0; j <= 11; j++)
				row.getCell(j).setCellStyle(style);	
			
			fis.close(); //input stream is closed
	        workbook.write(fos);
	        fos.flush();
	        fos.close();
	    	}else{
	    		console.setText(console.getText() + "*Report already exists*" + "\r\n");//prints text in console if file exists  		
	    	}
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	
	//Apps document creation (Apps.xlsx)
	public void createApkWorkbook(String date) throws IOException {
	    try {
	    	File fileName = new File(System.getProperty("user.dir") + "\\Apps_"+date+".xlsx");
	        FileOutputStream fos = new FileOutputStream(fileName);
	        @SuppressWarnings("resource")
			XSSFWorkbook  workbook = new XSSFWorkbook();            
	        workbook.write(fos);
	        fos.flush();
	        fos.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}

}
