package com.cxt.netdisk_client;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sf.json.JSONObject;

import java.awt.Dimension;
import javax.swing.JMenuItem;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class FileJPanel extends JPanel implements MouseListener, ActionListener {

	JLabel label = new JLabel();
	JLabel label_1 = new JLabel();
	
	
	private MainJFrame mainJFrame = null;
	private String rdid;
	private String id;
	private String filename;
	private String type;
	private long size;
	private FileListJPanel fileListJPanel = null;

	JPopupMenu jPopupMenu = new JPopupMenu();
	JMenuItem jMenuItem_del = new JMenuItem("Delete File");
	JMenuItem jMenuItem_updata = new JMenuItem("Change Name");
	JMenuItem jMenuItem_d = new JMenuItem("Download File");

	/**
	 * Create the panel.
	 */
	public FileJPanel(MainJFrame mainJFrame, String rdid, String id, String type, String filename, long size, FileListJPanel fileListJPanel) {
		this.mainJFrame = mainJFrame;
		this.rdid = rdid;
		this.id = id;
		this.type = type;
		this.filename = filename;
		this.size = size;
		this.fileListJPanel = fileListJPanel;

		setPreferredSize(new Dimension(108, 132));
		setLayout(null);

		jPopupMenu.add(jMenuItem_updata);
		jMenuItem_updata.addActionListener(this);
		jPopupMenu.add(jMenuItem_del);
		jMenuItem_del.addActionListener(this);
		jMenuItem_del.setText("Delete " + filename);

		this.addMouseListener(this);

		if (type.equals("DIR")) {
			label.setIcon(new ImageIcon("image/icon_list_folder.png"));
			
		} else {
			
			jMenuItem_d.addActionListener(this);
			jPopupMenu.add(jMenuItem_d);

			this.setToolTipText("Size: " + Config.changeUnit(size));

			if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".bmp")
					|| filename.toLowerCase().endsWith(".jepg") || filename.toLowerCase().endsWith(".gif")
					|| filename.toLowerCase().endsWith(".png")) {
				label.setIcon(new ImageIcon("image/icon_list_image.png"));

			} else if (filename.toLowerCase().endsWith(".doc") || filename.toLowerCase().endsWith(".docx")) {

				label.setIcon(new ImageIcon("image/icon_list_doc.png"));

			} else if (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls")) {
				label.setIcon(new ImageIcon("image/icon_list_excel.png"));

			} else if (filename.toLowerCase().endsWith(".html") || filename.toLowerCase().endsWith(".htm")) {
				label.setIcon(new ImageIcon("image/icon_list_html.png"));

			} else if (filename.toLowerCase().endsWith(".pdf")) {
				label.setIcon(new ImageIcon("image/icon_list_pdf.png"));

			} else if (filename.toLowerCase().endsWith(".vsd")) {
				label.setIcon(new ImageIcon("image/icon_list_visio.png"));

			} else if (filename.toLowerCase().endsWith(".vcf")) {
				label.setIcon(new ImageIcon("image/icon_list_vcard.png"));

			} else if (filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pptx")) {
				label.setIcon(new ImageIcon("image/icon_list_ppt.png"));

			} else if (filename.toLowerCase().endsWith(".mp3") || filename.toLowerCase().endsWith(".wma")) {
				label.setIcon(new ImageIcon("image/icon_list_audiofile.png"));

			} else if (filename.toLowerCase().endsWith(".mp4") || filename.toLowerCase().endsWith(".rm")
					|| filename.toLowerCase().endsWith(".rmvb") || filename.toLowerCase().endsWith(".avi")) {
				label.setIcon(new ImageIcon("image/icon_list_videofile.png"));

			} else if (filename.toLowerCase().endsWith(".rar") || filename.toLowerCase().endsWith(".jar")
					|| filename.toLowerCase().endsWith(".zip") || filename.toLowerCase().endsWith(".7z")) {
				label.setIcon(new ImageIcon("image/icon_list_compressfile.png"));

			} else if (filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".log")
					|| filename.toLowerCase().endsWith(".ini")) {
				label.setIcon(new ImageIcon("image/icon_list_txtfile.png"));

			} else {
				label.setIcon(new ImageIcon("image/icon_list_unknown.png"));
			}
		}

		label.setBounds(10, 10, 88, 88);
		add(label);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 18));

		label_1.setText(filename);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(0, 104, 108, 21);
		add(label_1);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && type.equals("DIR")) { 
			fileListJPanel.update(mainJFrame, null, "", id, "");
			mainJFrame.bianhao += "/" + id;
			mainJFrame.textField.setText(mainJFrame.textField.getText()+"/"+filename);
			System.out.println(id);
		} else {
			if (e.getButton() == 3) {
				jPopupMenu.show(this, e.getX(), e.getY());
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
//		if (e.getButton() == 1 && type.equals("DIR")) {
//			fileListJPanel.update(mainJFrame, null, "", id, "");
//			mainJFrame.bianhao += "/" + id;
//			mainJFrame.textField.setText(mainJFrame.textField.getText()+"/"+filename);
//			System.out.println("c");
//		
//		} else {
//			if (e.getButton() == 3) {
//				jPopupMenu.show(this, e.getX(), e.getY());
//			}
//		}

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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == jMenuItem_updata) {
			String input = javax.swing.JOptionPane.showInputDialog(this, "Enter a New Name");
			if (input != null) {

				if (type.equalsIgnoreCase("dir")) {
					try {
						String json = Config.service.updateName("DIR", id, "", input);
						String type = JSONObject.fromObject(json).getString("type");
						if (type.equalsIgnoreCase("ERROR_CHONGMING")) {
							javax.swing.JOptionPane.showMessageDialog(this, "Repetitive Name");
						} else if (type.equalsIgnoreCase("ok")) {
							javax.swing.JOptionPane.showMessageDialog(this, "Changing Successful");
							fileListJPanel.updateList();
						} else {
							javax.swing.JOptionPane.showMessageDialog(this, "Unknow Error");
						}

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						javax.swing.JOptionPane.showMessageDialog(this, "Network Error");
					}
				} else {
					try {
						String json = Config.service.updateName("FILE", rdid, id, input);
						String type = JSONObject.fromObject(json).getString("type");

						if (type.equalsIgnoreCase("ERROR_CHONGMING")) {
							javax.swing.JOptionPane.showMessageDialog(this, "Repetitive Name");
						} else if (type.equalsIgnoreCase("ok")) {
							javax.swing.JOptionPane.showMessageDialog(this, "Changing Successful");
							fileListJPanel.updateList();
						} else {
							javax.swing.JOptionPane.showMessageDialog(this, "Unknow Error");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						javax.swing.JOptionPane.showMessageDialog(this, "Network Error");
					}
				}
			}
		} else if (e.getSource() == jMenuItem_del) {

			int n = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure to DELETE?");
			if (n == 0) {
				
				try {
					String json = null;
					if (type.equalsIgnoreCase("dir")) {
						json = Config.service.delete("DIR", id, "");
					} else {
						json = Config.service.delete("FILE", "", id);
					}
					
					String type = JSONObject.fromObject(json).getString("type");
					if (type.equalsIgnoreCase("ERROR")) {
						javax.swing.JOptionPane.showMessageDialog(this, "Delete Failed");
					} else {
						fileListJPanel.updateList();
						mainJFrame.updataProgressBar();
					}
					
				} catch (Exception e2) {
					// TODO: handle exception
					
				}	
			}

		} else if (e.getSource() == jMenuItem_d) {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showOpenDialog(mainJFrame);
			File file = fileChooser.getSelectedFile();
			if(file == null){
				return;
			}
			
			new DownloadFile(fileListJPanel,mainJFrame,id,file,filename).start();
		}
	}

}






