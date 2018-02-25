package gator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteToExcel {
	
	public void writeResult(String filePath, String fileName, String sheetName, String[] dataToWrite)
			throws IOException {
		
		
		File file = new File(filePath + "\\" + fileName);//an object of File class

		FileInputStream inputStream = new FileInputStream(file);//FileInputStream is a class used for reading data from files (a heir of class InputStream)

		Workbook myWorkbook = null;
		CellStyle style = null;
		

		String fileExtensionName = fileName.substring(fileName.indexOf("."));//part of fileName between '.' and last character

		if (fileExtensionName.equals(".xlsx")) {
			myWorkbook = new XSSFWorkbook(inputStream);//assigns workbook to file with .xlsx extension
		}else if(fileExtensionName.equals(".xls")){
			myWorkbook = new HSSFWorkbook(inputStream);//assigns workbook to file with .xls extension
		}
		
		Sheet sheet = myWorkbook.getSheet(sheetName);////Reads sheet inside the workbook by its name
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
		
		int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();//number of rows in excel file

		Row row = sheet.getRow(0);//Returns the row at the given index

		Row newRow = sheet.createRow(rowCount + 1);

		//loop over all the rows of excel file
		for (int i = 0; i < row.getLastCellNum(); i++) {

			Cell cell = newRow.createCell(i);//cells creating
			cell.setCellStyle(style);
			cell.setCellValue(dataToWrite[i]);//puts data into new cell
		}
		
		for (int i = 0; i < row.getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);//auto alignment for all cells
		}
		
		inputStream.close(); //input stream is closed

		FileOutputStream outputStream = new FileOutputStream(file);//an object of FileOutputStream class for writing data into Excel file

		myWorkbook.write(outputStream);//writing data into Excel file

		outputStream.close();//output stream is closed

	}
}