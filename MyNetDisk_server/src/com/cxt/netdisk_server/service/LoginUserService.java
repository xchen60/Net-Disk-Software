package com.cxt.netdisk_server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.cxt.netdisk_server.db.DBManager;
import com.cxt.netdisk_server.util.MD5FileUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * Login
 * 
 * @author CXT
 * 
 * 
 * */

public class LoginUserService extends Thread {

	private Socket socket;

	public LoginUserService(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		String md5Key = "";
		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			// {"username":"","password":""}
			byte[] b = new byte[1024];
			int len = in.read(b);

			JSONObject json = JSONObject.fromObject(new String(b, 0, len));
			String username = json.getString("username");
			String password = json.getString("password");

			Connection conn = null;
			long uid = 0;
			try {
				conn = DBManager.getDBManager().getConn();
				PreparedStatement pst = conn
						.prepareStatement("SELECT * FROM users WHERE email=? AND PASSWORD=password(?)");
				pst.setString(1, username);
				pst.setString(2, password);
				ResultSet rs = pst.executeQuery();

				if (rs.next()) {
					uid = rs.getLong("uid");
					md5Key = MD5FileUtil.getMD5String(System.currentTimeMillis() + "" + uid);

					out.write(("{\"type\":\"ok\",\"email\":\"" + rs.getString("email") + "\",\"maxnumber\":"
							+ rs.getString("maxnumber") + ",\"md5\":\""+md5Key+"\"}").getBytes());
					out.flush();

				} else {
					out.write("{\"type\":\"errorLogin\"}".getBytes());
					out.flush();
					throw new Exception();
				}

			} finally {
				conn.close();
			}

			// Check Information After Login Successfully
			
			
			UserOnline.regUserOnline(md5Key, uid);
			
			while (true) {
				b = new byte[1024];
				len = in.read(b);
				json = JSONObject.fromObject(new String(b, 0, len));

				if (json.getString("type").equals("getNumber")) {	//**********getDiskSize

					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();
						PreparedStatement pst = conn.prepareStatement("SELECT SUM(filesize) FROM FILE WHERE uid=?");
						pst.setLong(1, uid);
						ResultSet rs = pst.executeQuery();
						if (rs.next()) {
							out.write(("{\"maxnumber\":" + rs.getLong(1) + "}").getBytes());
							out.flush();
						}

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("getUserInfo")) {	//**********getUserInfo

					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();

						PreparedStatement pst = conn.prepareStatement("SELECT * FROM FILE WHERE uid=?");
						pst.setLong(1, uid);
						ResultSet rs = pst.executeQuery();
						if (rs.next()) {
							out.write(("{\"type\":\"ok\",\"uid\":" + uid + "\",\"email\":\"" + rs.getString("email")
									+ "\",\"maxnumber\":" + rs.getString("maxnumber") + "}").getBytes());
							out.flush();
						}

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("getRootList")) {		//**********getRootList

					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();

						Hashtable<String, Object> hashtable = new Hashtable<>();

						// Query Diractory
						PreparedStatement pst = conn.prepareStatement("SELECT * FROM mulu WHERE uid=? AND rdid=0");
						pst.setLong(1, uid);
						ResultSet rs = pst.executeQuery();
						Vector<Mulu> vector = new Vector<>();
						while (rs.next()) {
							Mulu mulu = new Mulu(rs.getString(1), rs.getString(2), rs.getString(3), rs.getLong(4));
							vector.add(mulu);
						}
						hashtable.put("mulus", vector);

						// Query Files under the Dir
						pst = conn.prepareStatement("SELECT * FROM FILE WHERE uid=? AND did=0");
						pst.setLong(1, uid);
						rs = pst.executeQuery();
						Vector<Fileinfo> vector1 = new Vector<>();
						while (rs.next()) {
							Fileinfo info = new Fileinfo();
							info.setFid(rs.getString("fid"));
							info.setFname(rs.getString("fname"));
							info.setFilemd5(rs.getString("filemd5"));
							info.setFilesize(rs.getLong("filesize"));
							info.setDid(rs.getString("did"));
							info.setUid(rs.getLong("did"));

							vector1.add(info);
						}
						hashtable.put("files", vector1);

						JSONObject jsonArray = JSONObject.fromObject(hashtable);

						out.write(jsonArray.toString().getBytes());
						out.flush();

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("getList")) {		//**********getList
					
					String rdid = json.getString("rdid");

					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();

						Hashtable<String, Object> hashtable = new Hashtable<String, Object>();

						// 查询目录
						PreparedStatement pst = conn.prepareStatement("SELECT * FROM mulu WHERE uid=? AND rdid=?");
						pst.setLong(1, uid);
						pst.setString(2, rdid);
						ResultSet rs = pst.executeQuery();
						Vector<Mulu> vector = new Vector<Mulu>();
						while (rs.next()) {
							Mulu mulu = new Mulu(rs.getString(1), rs.getString(2), rs.getString(3), rs.getLong(4));
							vector.add(mulu);
						}
						hashtable.put("mulus", vector);

						// 根目录下的文件
						pst = conn.prepareStatement("SELECT * FROM FILE WHERE uid=? AND did=?");
						pst.setLong(1, uid);
						pst.setString(2, rdid);
						rs = pst.executeQuery();
						Vector<Fileinfo> vector1 = new Vector<Fileinfo>();
						while (rs.next()) {
							Fileinfo info = new Fileinfo();
							info.setFid(rs.getString("fid"));
							info.setFname(rs.getString("fname"));
							info.setFilemd5(rs.getString("filemd5"));
							info.setFilesize(rs.getLong("filesize"));
							info.setDid(rs.getString("did"));
							info.setUid(rs.getLong("uid"));
							vector1.add(info);
						}
						hashtable.put("files", vector1);

						JSONObject jsonArray = JSONObject.fromObject(hashtable);

						out.write((jsonArray.toString()).getBytes());
						out.flush();

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("mkdir")) {	//**********mkdir
					String rdid = json.getString("rdid");
					String dname = json.getString("dname");
					
					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();

						PreparedStatement pst = conn
								.prepareStatement("SELECT * FROM mulu WHERE uid=? AND rdid=? AND dname=?");
						pst.setLong(1, uid);
						pst.setString(2, rdid);
						pst.setString(3, dname);
						if (pst.executeQuery().next()) {
							out.write("{\"type\":\"error\"}".getBytes());
							out.flush();
						} else {
							// 创建目录
							pst = conn.prepareStatement("INSERT INTO mulu(did,dname,rdid,uid) " + "VALUES(?,?,?,?)");
							pst.setString(1, System.currentTimeMillis() + "");
							pst.setString(2, dname);
							pst.setString(3, rdid);
							pst.setLong(4, uid);
							int row = pst.executeUpdate();
							if (row > 0) {
								out.write("{\"type\":\"ok\"}".getBytes());
								out.flush();
							} else {
								out.write("{\"type\":\"error\"}".getBytes());
								out.flush();
							}
						}

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("removeFile")) {		//**********removeFile
					
					String fid = json.getString("fid");
					String type = json.getString("cls");
					String did = json.getString("did");

					if (type.equals("DIR")) {	// Delete Dir

						conn = null;
						try {
							conn = DBManager.getDBManager().getConn();

							PreparedStatement pst = conn.prepareStatement("DELETE FROM mulu WHERE UID=? AND DID=?");
							pst.setLong(1, uid);
							pst.setString(2, did);
							int row = pst.executeUpdate();
							if (row >= 1) {
								out.write(("{\"type\":\"OK\"}").getBytes());
								out.flush();
							} else {
								out.write(("{\"type\":\"ERROR\"}").getBytes());
								out.flush();
							}

						} finally {
							conn.close();
						}

					} else if (type.equals("FILE")) {	// Delete Files
						// DELETE FROM FILE WHERE UID=? AND FID=?

						conn = null;
						try {
							conn = DBManager.getDBManager().getConn();

							PreparedStatement pst = conn.prepareStatement("DELETE FROM FILE WHERE UID=? AND FID=?");
							pst.setLong(1, uid);
							pst.setString(2, fid);
							int row = pst.executeUpdate();
							if (row >= 1) {
								out.write(("{\"type\":\"OK\"}").getBytes());
								out.flush();
							} else {
								out.write(("{\"type\":\"ERROR\"}").getBytes());
								out.flush();
							}

						} finally {
							conn.close();
						}
					}
					
					
				} else if (json.getString("type").equals("updateName")) { //------------change the file name

					String type = json.getString("cls");
					String did = json.getString("did");
					String fid = json.getString("fid");
					String newname = json.getString("newname");

					conn = null;

					if (type.equals("DIR")) {// 目录的名字
						try {
							conn = DBManager.getDBManager().getConn();
							PreparedStatement pst = conn.prepareStatement(
									"SELECT * FROM mulu WHERE rdid=(SELECT rdid FROM mulu WHERE did=?) AND uid=?  AND dname=?");
							pst.setString(1, did);
							pst.setLong(2, uid);
							pst.setString(3, newname);
							if (pst.executeQuery().next()) {
								out.write(("{\"type\":\"ERROR_CHONGMING\"}").getBytes());
								out.flush();
								continue;
							}
							pst = conn.prepareStatement(
									"SELECT * FROM FILE WHERE did=(SELECT rdid FROM mulu WHERE did=?) AND fname=? AND uid=?");
							pst.setString(1, did);
							pst.setString(2, newname);
							pst.setLong(3, uid);
							if (pst.executeQuery().next()) {
								out.write(("{\"type\":\"ERROR_CHONGMING\"}").getBytes());
								out.flush();
								continue;
							}
						} finally {
							conn.close();
						}

						try {
							conn = DBManager.getDBManager().getConn();
							PreparedStatement pst = conn.prepareStatement("UPDATE MULU SET DNAME=? WHERE DID=?");
							pst.setString(1, newname);
							pst.setString(2, did);
							if (pst.executeUpdate() > 0) {
								out.write(("{\"type\":\"OK\"}").getBytes());
								out.flush();

							} else {
								out.write(("{\"type\":\"ERROR\"}").getBytes());
								out.flush();

							}
						} finally {
							conn.close();
						}

					} else if (type.equals("FILE")) {// 文件名字
						try {
							conn = DBManager.getDBManager().getConn();
							PreparedStatement pst = conn
									.prepareStatement("SELECT * FROM mulu WHERE rdid=? AND uid=?  AND dname=?");
							pst.setString(1, did);
							pst.setLong(2, uid);
							pst.setString(3, newname);
							if (pst.executeQuery().next()) {
								out.write(("{\"type\":\"ERROR_CHONGMING\"}").getBytes());
								out.flush();
								continue;
							}
							pst = conn.prepareStatement("SELECT * FROM FILE WHERE did=? AND fname=? AND uid=?");
							pst.setString(1, did);
							pst.setString(2, newname);
							pst.setLong(3, uid);
							if (pst.executeQuery().next()) {
								out.write(("{\"type\":\"ERROR_CHONGMING\"}").getBytes());
								out.flush();
								continue;
							}
						} finally {
							conn.close();
						}

						try {
							conn = DBManager.getDBManager().getConn();
							PreparedStatement pst = conn.prepareStatement("UPDATE FILE SET FNAME=? WHERE FID=?");
							pst.setString(1, newname);
							pst.setString(2, fid);
							if (pst.executeUpdate() > 0) {
								out.write(("{\"type\":\"OK\"}").getBytes());
								out.flush();

							} else {
								out.write(("{\"type\":\"ERROR\"}").getBytes());
								out.flush();

							}
						} finally {
							conn.close();
						}
					}
					
				} else if (json.getString("type").equals("updatePassword")) {

					String newpassword = json.getString("newpassword");
					String lastpassword = json.getString("lastpassword");

					conn = null;
					try {
						conn = DBManager.getDBManager().getConn();

						PreparedStatement pst = conn.prepareStatement(
								"update users set password=password(?) WHERE uid=? and password=password(?)");
						pst.setString(1, newpassword);
						pst.setLong(2, uid);
						pst.setString(3, lastpassword);
						int row = pst.executeUpdate();
						if (row >= 1) {
							out.write(("{\"type\":\"OK\"}").getBytes());
							out.flush();
						}

					} finally {
						conn.close();
					}

				} else if (json.getString("type").equals("moveFile")) {

				} else if (json.getString("type").equals("logout")) {

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			UserOnline.logout(md5Key);
		} finally {
			try {
				socket.close();
			} catch (Exception e2) {
			}
		}

	}
	


	public static void openServer() throws IOException {
		ServerSocket server = new ServerSocket(5757);
		while (true) {
			new LoginUserService(server.accept()).start();
		}
	}

}
















