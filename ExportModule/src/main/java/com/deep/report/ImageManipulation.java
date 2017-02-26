package com.deep.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
// 测试用
public class ImageManipulation {
	public static void main(String[] args) {
		manipulate("./temp/2.png", "./temp/2_converted.png");
	}
	public static void manipulate(String path_in, String path_out) {
		// path_in = "./testfiles/SimulatedData.png"
		File file = new File(path_in);

		try {
			// Reading an Image file from file system
			InputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			// Converting Image byte array into Base64 String
			String imageDataString = encodeImage(imageData);
			System.out.println(imageDataString);
			
			// Converting a Base64 String into Image byte array
			byte[] imageByteArray = decodeImage(imageDataString);

			// Write a image byte array into file system
			// path_out = "./testfiles/SimulatedData_converted.png"
			FileOutputStream imageOutFile = new FileOutputStream(path_out);
			imageOutFile.write(imageByteArray);

			imageInFile.close();
			imageOutFile.close();

			System.out.println("Image Successfully Manipulated!");
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException e) {
			System.out.println("Exception while reading the Image " + e);
		}
	}

	public static String encodeImage(byte[] imageByteArray) {
		return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	public static byte[] decodeImage(String imageDataString) {
		return Base64.decodeBase64(imageDataString);
	}

}
