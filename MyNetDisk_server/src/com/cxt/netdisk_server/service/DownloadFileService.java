package com.cxt.netdisk_server.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cxt.netdisk_server.db.DBManager;

import net.sf.json.JSONObject;

public class DownloadFileService extends Thread {

	private Socket socket = null;

	public DownloadFileService(Socket socket) {
		this.socket = socket;
	}

	public void run() {

		// {"fileid":"","codemd5":""}

		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] b = new byte[1024 * 5];
			int len = in.read(b);
			String jsonStr = new String(b, 0, len);
			JSONObject json = JSONObject.fromObject(jsonStr);

			String fileid = json.getString("fileid");
			String codemd5 = json.getString("codemd5");
			UserOnline.getUserOnlineUID(codemd5);

			String filepath = "";

			Connection conn = null;
			try {
				conn = DBManager.getDBManager().getConn();
				PreparedStatement pst = conn.prepareStatement(
						"SELECT filepath FROM files WHERE filemd5=(SELECT filemd5 FROM  FILE WHERE fid=?)");
				pst.setString(1, fileid);
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					filepath = rs.getString(1);
				}

				FileInputStream fileInputStream = new FileInputStream(filepath);
				b = new byte[1024 * 8];
				while ((len = fileInputStream.read(b)) != -1) {
					out.write(b, 0, len);
					out.flush();
				}
				fileInputStream.close();

			} finally {
				conn.close();
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

		ServerSocket server = new ServerSocket(5759);
		while (true) {
			Socket socket = server.accept();
			new DownloadFileService(socket).start();
		}

	}

}
