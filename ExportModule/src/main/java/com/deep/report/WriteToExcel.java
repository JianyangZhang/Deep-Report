package com.deep.report;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class WriteToExcel {
	private static final int LINEGRAPH_ROW = 15;
	private static final int LINEGRAPH_COL = 7;
	private static final int MINI_LINEGRAPH_ROW = 6;
	private static final int MINI_LINEGRAPH_COL = 5;
	private static final int GAUGE_ROW = 11;
	private static final int GAUGE_COL = 4;
	private static final float MARGIN_ROW = 2;
	private static final int MARGIN_COL = 665;
	
	public static void main(String[] args) throws IOException {
		String jsonStr = buildStringFromFile("./temp/test.json");
		export(jsonStr);
	}

	public static void export(String jsonStr) throws FileNotFoundException, IOException {
		JSONObject jsonObj = new JSONObject(jsonStr);
		List<JSONObject> rows = getRows(jsonObj);
		int startRow = 0, startCol = 0, endRow = 0, endCol = 0;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Main Sheet");
//		sheet.setDefaultColumnWidth(1);
//		sheet.setDefaultRowHeight((short) 200);
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		for (int i = 0; i < rows.size(); i++) {
			String type = rows.get(i).getJSONObject("panels").getString("type");
			List<JSONObject> panels = getPanels(rows.get(i));
			if (startRow >= 1) {
				sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, startCol, endCol - 1));
				XSSFRow row = sheet.createRow(startRow - 1);
				row.setHeightInPoints(MARGIN_ROW);				
			}
			for (int j = 0; j < panels.size(); j++) {
				byte[] imageByteArray = Base64.decodeBase64(panels.get(j).getString("img"));
				int pictureIndex = workbook.addPicture(imageByteArray, XSSFWorkbook.PICTURE_TYPE_PNG);		
				XSSFCreationHelper helper = workbook.getCreationHelper();
				XSSFClientAnchor anchor = helper.createClientAnchor();
//				if (startCol >= 1) {
//					sheet.addMergedRegion(new CellRangeAddress(startRow, endRow - 1, startCol - 1, startCol - 1));
//					sheet.setColumnWidth(startCol - 1, MARGIN);		
//				}			
				anchor.setRow1(startRow);
				anchor.setCol1(startCol);
				switch (type) {
					case "a":
						endRow = startRow + MINI_LINEGRAPH_ROW;
						endCol = startCol + MINI_LINEGRAPH_COL;
						break;
					case "b":
						endRow = startRow + GAUGE_ROW;
						endCol = startCol + GAUGE_COL;
						break;
					default:
						endRow = startRow + MINI_LINEGRAPH_ROW;
						endCol = startCol + MINI_LINEGRAPH_COL;
						break;
				}
				anchor.setRow2(endRow);
				anchor.setCol2(endCol);
				sheet.addMergedRegion(new CellRangeAddress(startRow, endRow - 1, startCol, endCol - 1));
//				startCol = endCol + 1;
				startCol = endCol;
				drawing.createPicture(anchor, pictureIndex);			
			}
			startRow = endRow + 1;
			startCol = 0;
		}
		workbook.write(new FileOutputStream("report.xlsx"));
		workbook.close();
	}
	
	private static List<JSONObject> getPanels(JSONObject row) {
		List<JSONObject> panels = new ArrayList<>();
		int index = 1;
		while (true) {
			try {
				String temp = "panel" + index; 
				JSONObject panel = row.getJSONObject("panels").getJSONObject(temp);
				panels.add(panel);
				index++;
			} catch (JSONException e) {
				break;
			}
		}
		return panels;
	}
	
	private static List<JSONObject> getRows(JSONObject jsonObj) {
		List<JSONObject> rows = new ArrayList<>();
		int index = 1;
		while (true) {
			try {
				String temp = "row" + index; 
				JSONObject row = jsonObj.getJSONObject("rows").getJSONObject(temp);
				rows.add(row);
				index++;
			} catch (JSONException e) {
				break;
			}
		}
		return rows;
	}
	
	private static String buildStringFromFile(String path) throws IOException {
		InputStream is = new FileInputStream(path);
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();		
		while (line != null) {
			sb.append(line);
			line = buf.readLine();
		}
		return sb.toString();
	}
	/*
	private static void saveImgFromStr(String imgStr, String path_out) throws IOException {
		byte[] imageByteArray = Base64.decodeBase64(imgStr);
		FileOutputStream imageOutFile = new FileOutputStream(path_out);
		imageOutFile.write(imageByteArray);
		imageOutFile.close();
	}
	*/
}
