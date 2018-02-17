package com.cxt.netdisk_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.sf.json.JSONObject;

/*
 * Keep Connecting with service
 * 
 * */
public class Service extends Thread {

	private Socket socket;

	public Service(Socket socket) {
		this.socket = socket;
		// this.server_str = server_str;
		Config.service = this;
	}

	public void run() {

	}

	public static long getNumber() throws IOException {
		// type:getNumber
		String json = "{\"type\":\"getNumber\"}";
		output.write(json.getBytes());
		output.flush();

		// maxnumber
		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);
		long maxnumber = JSONObject.fromObject(json).getLong("maxnumber");
		Config.MAX_NUMBER = maxnumber;
		return maxnumber;

	}

	// Get Root Dir
	public static String getRootList() throws IOException {
		String json = "{\"type\":\"getRootList\"}";
		output.write(json.getBytes());
		output.flush();

		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);
		Config.ROOT_LIST_JSON_STR = json;
		return json;
	}

	// Get Dirs by Parent Dir
	public static String getList(String rdid) throws IOException {
		String json = "{\"type\":\"getList\",\"rdid\":\"" + rdid + "\"}";
		output.write(json.getBytes());
		output.flush();

		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);

		return json;
	}

	// Update name
	public static String updateName(String type, String did, String fid, String newname) throws IOException {
		String json = "{\"type\":\"updateName\",\"cls\":\"" + type + "\"" + ",\"did\":\"" + did + "\",\"fid\":\"" + fid
				+ "\",\"newname\":\"" + newname + "\"}";
		output.write(json.getBytes());
		output.flush();

		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);

		return json;
	}

	// Delete File or Dir
	public static String delete(String type, String did, String fid) throws IOException {
		String json = "{\"type\":\"removeFile\",\"cls\":\"" + type + "\", \"did\":\"" + did + "\", \"fid\":\"" + fid + "\"}";
		output.write(json.getBytes());
		output.flush();

		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);

		return json;
	}
	
	//Create the Folder
	public static String mkdir(String rdid, String dname) throws Exception {
		String json = "{\"type\":\"mkdir\",\"rdid\":\"" + rdid + "\"" + ",\"dname\":\"" + dname + "\"}";
		output.write(json.getBytes());
		output.flush();
		// maxnumber
		byte[] b = new byte[1024];
		int len = input.read(b);
		json = new String(b, 0, len);
		return json;

	}

	private static InputStream input;
	private static OutputStream output;

	public static boolean login(String username, String password) throws Exception {

		Socket socket = new Socket(Config.SERVERIP, Config.LOGIN_PORT);
		input = socket.getInputStream();
		output = socket.getOutputStream();

		String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
		output.write(json.getBytes());
		output.flush();

		byte[] b = new byte[1024];
		int len = input.read(b);

		// String server_str = new String(b, 0, len).trim();

		JSONObject obj = JSONObject.fromObject(new String(b, 0, len).trim());
		if (obj.getString("type").equals("ok")) {

			Config.USER_EMAIL = obj.getString("email");
			Config.SIZE = obj.getInt("maxnumber");
			Config.MD5 = obj.getString("md5");

			// Get surplus size
			getNumber();

			new Service(socket).start();
			return true;
		}
		socket.close();
		return false;

	}
}
