package com.cxt.netdisk_server.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cxt.netdisk_server.db.DBManager;

import net.sf.json.JSONObject;

public class UploadFileService extends Thread {
	
	private Socket socket = null;
	
	public UploadFileService(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		
		//File name, size, md5, folder(did), uid, 
		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] b = new byte[1024 * 5];
			int len = in.read(b);
			String jsonStr = new String(b, 0, len);
			JSONObject json = JSONObject.fromObject(jsonStr);

			String filesize = json.getString("filesize");
			String filename = json.getString("filename");
			String filemd5 = json.getString("md5");
			String dirid = json.getString("dirid");
			String codemd5 = json.getString("codemd5");
			long uid = UserOnline.getUserOnlineUID(codemd5);
			long fsize = Long.parseLong(filesize);

			// SELECT maxnumber FROM users WHERE uid=?

			// 使用的大小
			long shiyongdaxiao = -1;
			// 最大大小
			long maxnunmber = 0;

			Connection conn = null;
			try {
				conn = DBManager.getDBManager().getConn();
				PreparedStatement pst = conn.prepareStatement("SELECT SUM(filesize) FROM FILE WHERE uid=?");
				pst.setLong(1, uid);
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					shiyongdaxiao = rs.getLong(1);
				}
				pst = conn.prepareStatement("SELECT maxnumber FROM users WHERE uid=?");
				pst.setLong(1, uid);
				rs = pst.executeQuery();
				if (rs.next()) {
					maxnunmber = rs.getLong(1);
				}
			} finally {
				conn.close();
			}
			if (shiyongdaxiao == -1) {
				throw new Exception();
			}

			if (shiyongdaxiao + fsize <= maxnunmber) {

				try {
					conn = DBManager.getDBManager().getConn();
					PreparedStatement pst = conn.prepareStatement("SELECT * FROM files   WHERE filemd5=?");
					pst.setString(1, filemd5);
					ResultSet rs = pst.executeQuery();
					if (rs.next()) {
						out.write("{\"type\":\"mc\"}".getBytes());
						out.flush();
						
						pst = conn.prepareStatement("INSERT INTO FILE VALUES(?,?,SYSDATE(),?,?,?,?)");

						pst.setString(1, System.currentTimeMillis() + "");
						pst.setString(2, filename);
						pst.setLong(3, Long.parseLong(dirid));
						pst.setLong(4, uid);
						pst.setLong(5, fsize);
						pst.setString(6, filemd5);
						pst.executeUpdate();
												
						return;
					} else {
						out.write("{\"type\":\"ok\"}".getBytes());
						out.flush();
					}
				} finally {
					conn.close();
				}
 
				File filepath = new File("C:\\Users\\Thomas\\Desktop\\Project\\CXT_NetDisk\\file", filemd5);
				FileOutputStream fileOutputStream = new FileOutputStream(filepath);
				byte[] b1 = new byte[1024 * 1024];
				long length = 0;
				while ((len = in.read(b1)) != -1) {
					fileOutputStream.write(b1, 0, len);
					length += len;
					if (length >= fsize) {
						break;
					}
				}
				fileOutputStream.flush();
				fileOutputStream.close();

				try {
					conn = DBManager.getDBManager().getConn();
					PreparedStatement pst = conn.prepareStatement("INSERT INTO FILES VALUES(?,?,?,SYSDATE(),?)");

					pst.setString(1, filemd5);
					pst.setLong(2, fsize);
					pst.setString(3, filepath.getPath());
					pst.setLong(4, uid);
					pst.executeUpdate();

					pst = conn.prepareStatement("INSERT INTO FILE VALUES(?,?,SYSDATE(),?,?,?,?)");

					pst.setString(1, System.currentTimeMillis() + "");
					pst.setString(2, filename);
					pst.setLong(3, Long.parseLong(dirid));
					pst.setLong(4, uid);
					pst.setLong(5, fsize);
					pst.setString(6, filemd5);
					pst.executeUpdate();

				} finally {
					conn.close();
				}
				out.write("{\"type\":\"ok\"}".getBytes());
				out.flush();

			} else {
				out.write("{\"type\":\"rongliangbugou\"}".getBytes());
				out.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void openServer() throws Exception {
		ServerSocket server = new ServerSocket(5758);
		while (true) {
			Socket socket = server.accept();
			new UploadFileService(socket).start();
		}
	}


}













