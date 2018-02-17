package com.cxt.netdisk_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.sf.json.JSONObject;

public class UploadFile extends Thread{
	
	private MainJFrame mainJFrame;
	private File file;
	private String dirid;
    private FileListJPanel fileListJPanel;
	public UploadFile(FileListJPanel fileListJPanel, MainJFrame mainJFrame, File file, String dirid) {
		this.fileListJPanel=fileListJPanel;
		this.mainJFrame = mainJFrame;
		this.file = file;
		this.dirid = dirid;
	}
 

	public void run() {
		Socket socket = null;
		try {
			String md5 = MD5FileUtil.getFileMD5String(file);

			String strMD5 = "{\"filesize\":\"" + file.length() + "\",\"filename\":\"" + file.getName() + "\",\"md5\":\""
					+ md5 + "\",\"dirid\":\"" + dirid + "\",\"codemd5\":\"" + Config.MD5 + "\"}";
			socket = new Socket(Config.SERVERIP, Config.UPLOAD_PORT);
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();

			// ����������md5
			output.write(strMD5.getBytes());
			output.flush();

			// �ȴ��������ػ�
			byte[] b = new byte[1024];
			int len = input.read(b);
			String serverStr = new String(b, 0, len);
			// rongliangbugou
			String typeStr = JSONObject.fromObject(serverStr).getString("type");
			if (typeStr.equals("rongliangbugou")) {// ����˻���������
				javax.swing.JOptionPane.showMessageDialog(mainJFrame, "Not Enough Free Space!");
			} else if (typeStr.equals("mc")) {// �봫��
				javax.swing.JOptionPane.showMessageDialog(mainJFrame, "Upload Successfully =!");
			} else if (typeStr.equals("ok")) {// ���Դ���
				FileInputStream fileInputStream = new FileInputStream(file);
				b = new byte[1024 * 8];
				while ((len = fileInputStream.read(b)) != -1) {
					output.write(b, 0, len);
					output.flush();
				}
				fileInputStream.close();

				len = input.read(b);
				serverStr = new String(b, 0, len);
				typeStr = JSONObject.fromObject(serverStr).getString("type");
				if (typeStr.equals("ok")) {
					javax.swing.JOptionPane.showMessageDialog(mainJFrame, "Upload Successfully!");
				}
			} 
//			System.out.println("====ok====");
			fileListJPanel.updateList();//����Ŀ¼
			mainJFrame.updataProgressBar();//���½�����

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
