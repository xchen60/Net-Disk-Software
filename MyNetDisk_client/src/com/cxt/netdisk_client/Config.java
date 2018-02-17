package com.cxt.netdisk_client;

public class Config {

	public static String SERVERIP = "127.0.0.1";
	
	public static int REG_PORT = 5656;
	
	public static int LOGIN_PORT = 5757;
	
	public static int DOWNLOAD_PORT = 5759;

	public static int UPLOAD_PORT = 5758;
	
	public static String USER_EMAIL = "";
	public static int SIZE = 0;
	
	//Surplus size
	public static long MAX_NUMBER = 0;
	
	//Root Dir Info
	public static String ROOT_LIST_JSON_STR = "";
	
	public static Service service;

	public static String MD5;
	
	public static String changeUnit(long size) {
		if (size / 1024 == 0) {
			return size + "B";
		} else if (size / 1024 / 1024 == 0) {
			return size/1024 + "KB";
		} else if (size / 1024 / 1024 /1024 == 0) {
			return size/1024/1024 + "MB";
		} else {
			return size/1024/1024/1024 + "GB";
		}
	}



}













