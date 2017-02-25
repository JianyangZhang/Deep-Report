package com.deep.report;

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
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
	private static final int GAUGE_ROW = 15;
	private static final int GAUGE_COL = 7;
	public static void main(String[] args) throws IOException {
		String jsonStr = readFileToString("./sample.json");
		export(jsonStr);
	}

	public static void export(String jsonStr) throws FileNotFoundException, IOException {
		JSONObject obj = new JSONObject(jsonStr);
		List<JSONObject> panels = new ArrayList<>();
		int startRow = 0, startCol = 0, endRow = 0, endCol = 0;
		int index = 1;
		while (true) {
			try {
				String temp = "panel" + index; 
				JSONObject panel = obj.getJSONObject("rows").getJSONObject("row1").getJSONObject("panels").getJSONObject(temp);
				panels.add(panel);
				index++;
			} catch (JSONException e) {
				break;
			}
		}
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Main Sheet");
		Drawing drawing = sheet.createDrawingPatriarch();
		for (int i = 0; i < panels.size(); i++) {
			byte[] imageByteArray = Base64.decodeBase64(panels.get(i).getString("img"));
			int pictureIndex = workbook.addPicture(imageByteArray, Workbook.PICTURE_TYPE_PNG);		
			CreationHelper helper = workbook.getCreationHelper();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setRow1(startRow);
			anchor.setCol1(startCol);
			
			endRow = LINEGRAPH_ROW;
			endCol = startCol + LINEGRAPH_COL;
			anchor.setRow2(endRow);
			anchor.setCol2(endCol);
			startCol = endCol + 1;
			drawing.createPicture(anchor, pictureIndex);
		}
		workbook.write(new FileOutputStream("report.xlsx"));
		workbook.close();
	}
	
	private static String readFileToString(String path) throws IOException {
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
