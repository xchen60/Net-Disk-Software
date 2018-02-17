package com.cxt.netdisk_client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainJFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	
	//Paths
	public JTextField textField;
	
	//Folder ID
	public String bianhao; 
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainJFrame frame = new MainJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void updataProgressBar() {
		
		try {
			Config.service.getNumber();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		progressBar.setToolTipText("\r\n");
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 18));
		progressBar.setMaximum((int)(Config.SIZE/1024/1024));
		progressBar.setValue((int)(Config.SIZE/1024/1024) - (int)(Config.MAX_NUMBER/1024/1024));
		
		int total = (int)(Config.SIZE/1024/1024);
		int used = (int)(Config.MAX_NUMBER/1024/1024);
		int remain = total - used;
		
		progressBar.setString(remain + "MB/" + total + "MB");
//		System.out.println(total + "   " + remain);
	}
	

	/**
	 * Create the frame.
	 */
	final JProgressBar progressBar = new JProgressBar();
	FileListJPanel fileListJPanel = new FileListJPanel();
	
	
	
	public MainJFrame() {
		setResizable(false);
		updataProgressBar();
		setBounds(new Rectangle(2, 2, 2, 2));
		setTitle("CXT_NetDisk V1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(450, 100, 749, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Username: " + Config.USER_EMAIL);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel.setBounds(28, 18, 375, 30);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Available Space:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(28, 61, 134, 35);
		contentPane.add(lblNewLabel_1);
		
		progressBar.setStringPainted(true);
		progressBar.setBounds(166, 67, 313, 26);
		contentPane.add(progressBar);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(new Rectangle(2, 2, 2, 2));
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tabbedPane.setBounds(28, 127, 691, 598);
		contentPane.add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Show All Files", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel_1.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JLabel lblAddress = new JLabel("  Path:   ");
		lblAddress.setBounds(new Rectangle(3, 3, 3, 3));
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_4.add(lblAddress, BorderLayout.WEST);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_4.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5, BorderLayout.EAST);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("  <<  ");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textField.getText().equals("  root")) {
					String path = textField.getText();
					path = path.substring(0, path.lastIndexOf("/"));
					textField.setText(path);

					
					bianhao = bianhao.substring(0, bianhao.lastIndexOf("/"));
					String id = bianhao.substring(bianhao.lastIndexOf("/") + 1, bianhao.length());
 
					fileListJPanel.update(MainJFrame.this, textField, "", id, "0");
				}		
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_5.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Refresh");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fileListJPanel.updateList();
				MainJFrame.this.updataProgressBar();
				
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_5.add(btnNewButton_1, BorderLayout.EAST);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel.add(scrollPane_2, BorderLayout.CENTER);
		
		scrollPane_2.getViewport().setView(fileListJPanel);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Download", null, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		
		Vector col = new Vector();
		col.add("File Name");
		col.add("Size");
		col.add("Path");
		col.add("Remain");
		col.add("Speed");
		Vector row = new Vector();
		
		table = new JTable(row, col);
		scrollPane.setViewportView(table);
		
		Vector col1 = new Vector();
		col1.add("File Name");
		col1.add("Size");
		col1.add("Path");
		col1.add("Remain");
		col1.add("Speed");
		Vector row1 = new Vector();
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Upload", null, panel_3, null);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1, BorderLayout.CENTER);
		
		table_1 = new JTable(row1, col1);
		scrollPane_1.setViewportView(table_1);
		
		JButton btnExitNetdisk = new JButton("Logout");
		btnExitNetdisk.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnExitNetdisk.setBounds(544, 64, 175, 29);
		contentPane.add(btnExitNetdisk);
		
		JButton btnChangePassword = new JButton("Change Password");
		btnChangePassword.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnChangePassword.setBounds(544, 19, 175, 29);
		contentPane.add(btnChangePassword);
		
		fileListJPanel.update(this, textField, "", "0", "0");
		
		textField.setText("  root");
		bianhao = "0";
	}
}
