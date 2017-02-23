package com.deep.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteToExcel {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Main Sheet");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("my first cell is created");
		
		sheet.autoSizeColumn(0);
		workbook.write(new FileOutputStream("report.xlsx"));
		workbook.close();
	}
}
