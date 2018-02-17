package com.cxt.netdisk_client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FileListJPanel extends JPanel implements ActionListener, MouseListener{
	
	private JPopupMenu menu = new JPopupMenu();
	JMenuItem jm = new JMenuItem("Create New Folder");
	JMenuItem upload = new JMenuItem("Upload File");
	
	/**
	 * Create the panel.
	 */
	public FileListJPanel() {
		
		jm.addActionListener(this);
		menu.add(jm); 
		upload.addActionListener(this);
		menu.add(upload);
		this.addMouseListener(this);
		this.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT)); 

	}
	
	public void updateList() {
		update(mainJFrame, text_path, path, did, pdid);
	}
	
	JTextField text_path;
	String path;
	String did;
	String pdid;
	MainJFrame mainJFrame;
	
	/**
	 * 更新目录显示
	 * 
	 * @param text_path 路径显示
	 * @param path 目录地址
	 * @param did 目录id
	 * @param pdid 父目录
	 *
	 */
	public void update(MainJFrame mainJFrame, JTextField text_path, String path, String did, String pdid) {
		this.mainJFrame = mainJFrame;
		this.text_path = text_path;
		this.path = path;
		this.did = did;
		this.pdid = pdid;
		String json_str = null;
		
		if (did.trim().equals("0")) { //rootDir
			try {
				json_str = Config.service.getRootList();
	
			} catch (IOException e) {
				e.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(this.getParent(), "Network Error");
			}
			
		} else {
			try {
				json_str = Config.service.getList(did);
	
			} catch (IOException e) {
				e.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(this.getParent(), "Network Error");
			}
		}
		
		JSONObject json = JSONObject.fromObject(json_str);
		
		JSONArray jsonArray_mulu = json.getJSONArray("mulus");
		JSONArray jsonArray_file = json.getJSONArray("files");
		
		this.removeAll();
		
		int count = jsonArray_mulu.size() + jsonArray_file.size();
		
		for (int i = 0; i < jsonArray_mulu.size(); i++) {
			JSONObject mulu = jsonArray_mulu.getJSONObject(i);
			this.add(new FileJPanel(mainJFrame, mulu.getString("rdid"), 
					mulu.getString("did"), "DIR", mulu.getString("dname"), 0, this));
		}
		
		for (int i = 0; i < jsonArray_file.size(); i++) {
			JSONObject file = jsonArray_file.getJSONObject(i);	
			this.add(new FileJPanel(mainJFrame, file.getString("did"), 
					file.getString("fid"), "FILE", file.getString("fname"), 
					file.getLong("filesize"), this));
		}
		
		int height = (count/5 + (count%5 == 0 ? 0 : 1)) * 135;
		this.setPreferredSize(new Dimension(668, height));
		this.updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == upload) {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.showSaveDialog(mainJFrame);
			
			File file = fileChooser.getSelectedFile();
			if (file == null) {
				return;
			}
			new UploadFile(this, mainJFrame, file, did).start();
			
		} else {
			String name = JOptionPane.showInputDialog(this, "Enter New Folder Name: ");
			if (name != null && !name.trim().equals("")) {
				try {
					String jsonStr = Config.service.mkdir(did, name);
					if (!JSONObject.fromObject(jsonStr).getString("type").equalsIgnoreCase("ok")) {
						JOptionPane.showMessageDialog(this, "Action Failed");
					} 
					
					updateList();
				
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
			
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getButton() == 3) {
			menu.show(this, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}













