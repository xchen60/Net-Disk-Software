package com.cxt.netdisk_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.sf.json.JSONObject;

public class DownloadFile extends Thread {

	private MainJFrame mainJFrame;
	private String fileid;
	private File filedir;
	private FileListJPanel fileListJPanel;
	private String filename = null;

	public DownloadFile(FileListJPanel fileListJPanel, MainJFrame mainJFrame, String fileid, File filedir,
			String filename) {
		this.fileListJPanel = fileListJPanel;
		this.mainJFrame = mainJFrame;
		this.fileid = fileid;
		this.filedir = filedir;
		this.filename = filename;
	}

	public void run() {
		Socket socket = null;
		try {

			String strMD5 = "{\"fileid\":\"" + fileid + "\",\"codemd5\":\"" + Config.MD5 + "\"}";
			socket = new Socket(Config.SERVERIP, Config.DOWNLOAD_PORT);
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();

			// 向服务器输出md5
			output.write(strMD5.getBytes());
			output.flush();
			FileOutputStream fileOutputStream = new FileOutputStream(new File(filedir, filename));
			byte[] b = new byte[1024 * 8];
			int len = 0;
			while ((len = input.read(b)) != -1) {
				fileOutputStream.write(b, 0, len);
			}
			fileOutputStream.close();
			javax.swing.JOptionPane.showMessageDialog(mainJFrame, "Download Successfully!");
			
			fileListJPanel.updateList();
			mainJFrame.updataProgressBar();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

}
