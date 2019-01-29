package com.ibm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Utils {
	
	private static final String CONFIG_FILE ="config.properties";
	private static final String PROG_CONFIG_FILE ="program_config.properties";
	
	
	/**
	 * @param appType
	 * @return
	 */
	public static String getValue(String key) {
		String value = null;
		Properties properties = null;
		InputStream in = null;
		try {
			properties = new Properties();
			in = Utils.class.getClassLoader().getResourceAsStream(PROG_CONFIG_FILE);
			properties.load(in);
			value = properties.getProperty(key);
			
		} catch (Exception e) {
			System.out.println("somthing wrong occured while getting values from program config files");
			System.exit(1);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				System.out.println("somthing wrong occured");
				System.exit(1);
			}
		}
		return value;
	}
	
	/**
	 * @param appType
	 * @return
	 */
	public static String getInitialTransaction(String appType) {
		String tran_no = null;
		Properties properties = null;
		InputStream in = null;
		try {
			properties = new Properties();
			in = Utils.class.getClassLoader().getResourceAsStream(appType+"_"+CONFIG_FILE);
			properties.load(in);
			tran_no = properties.getProperty("temp");
		} catch (Exception e) {
			System.out.println("somthing wrong occured while reading configuration for application "+appType);
			System.exit(1);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				System.out.println("somthing wrong occured");
				System.exit(1);
			}
		}
		return tran_no;
	}
	
	/**
	 * @param appType
	 * @param value
	 */
	public static void updateLastTransaction(String appType,String value) {
		
		Properties properties = null;
		OutputStream outputStream = null;
		File file = null;
		try {
			file = new File(Utils.class.getClassLoader().getResource(appType+"_"+CONFIG_FILE).toURI());
			outputStream = new FileOutputStream(file);
			properties = new Properties();
			properties.setProperty("temp", value);
			properties.store(outputStream, null);
		} catch (Exception e) {

			System.out.println("something went wrong while updating last transaction details for application "+appType);
			System.exit(1);
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				System.out.println("somthing wrong occured");
				System.exit(1);
			}
		}
	}
	
	public static void main(String[] args) {
		updateLastTransaction("yp", "YV39LY_2");
	}
}
