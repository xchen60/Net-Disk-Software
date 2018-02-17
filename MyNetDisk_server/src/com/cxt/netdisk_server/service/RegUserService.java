package com.cxt.netdisk_server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Random;

import com.cxt.netdisk_server.db.DBManager;
import com.cxt.netdisk_server.util.EmailSendCode;


import net.sf.json.JSONObject;

public class RegUserService extends Thread {
	
	private Socket socket = null;
	
	public RegUserService(Socket socket) {
		this.socket = socket;
	}
	
	private static HashMap<String, String> codes = new HashMap<>();
	
	@Override
	public void run() {
		InputStream input;
		OutputStream output;
		
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
			
			//{"type":"reg", "username":"", "password":"", "code":""}
			//{"type":"yzm", "username":""}
			byte[] bytes = new byte[1024 * 2];
			int len = input.read(bytes);
			String json_str = new String(bytes, 0, len);
			
			JSONObject obj = JSONObject.fromObject(json_str);
			String type = obj.getString("type");
			if (type.trim().equals("reg")) {
				
				String username = obj.getString("username");
				String password = obj.getString("password");
				String code = obj.getString("code");
				
				String yzm = codes.get(username);
				if (yzm == null || yzm.trim().equals("") || code.trim().equals("")) {
					output.write("{\"type\":\"errorYzm\"}".getBytes());
					output.flush();
					return;
				}
				
				codes.remove(username);
				
				if (!obj.getString("code").equals(yzm)) {
					output.write("{\"type\":\"errorCode\"}".getBytes());
					output.flush();
					return;
				}
				
				String email = username;
				String phonenumber = "";
				
				Connection conn = DBManager.getDBManager().getConn();
				
				try {
					PreparedStatement pst = conn.prepareStatement("select * from users where email=?");
					pst.setString(1, email);
					if (pst.executeQuery().next()) {
						output.write("{\"type\":\"errorUsername\"}".getBytes());
						output.flush();
					} else {
						pst = conn.prepareStatement(
								"INSERT INTO USERS(phonenumber,email,password) VALUES(?,?," + "password(?))");
						pst.setString(1, phonenumber);
						pst.setString(2, email);
						pst.setString(3, password);
						if (pst.executeUpdate() >= 1) {
							output.write("{\"type\":\"ok\"}".getBytes());
							output.flush();
						} else {
							output.write("{\"type\":\"errorUserReg\"}".getBytes());
							output.flush();
						}
					}
					
				} finally {
					conn.close();
				}
				
				
			} else if (type.trim().equals("yzm")) {
				
				String username = obj.getString("username");
				String code = "";
				Random rd = new Random();
				for (int i = 0; i < 6; i++) {
					code += rd.nextInt(10) + "";
				}
				
				codes.put(username, code);
				
				if (username.indexOf("@") >= 0) {
					EmailSendCode.sendEmail(username, code);
				} 
				
				output.write("{\"type\":\"ok\"}".getBytes());
				output.flush();
				
				//没冲钱，所以就不用电话注册了
//				else if (username.length() == 11) {
//					PhoneSendCode.sendPhone(username, code);
//				}
			}
			
		} catch (Exception e) {
			
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public static void openServer() throws IOException {
		ServerSocket server = new ServerSocket(5656);
		while (true) {
			Socket socket = server.accept();
			new RegUserService(socket).start();
		}	
	}
	
	public static void main(String[] args) throws IOException {
		openServer();
	}
	
}












