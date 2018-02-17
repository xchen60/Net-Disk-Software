package com.cxt.netdisk_client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sf.json.JSONObject;
import java.awt.event.FocusAdapter;

public class LoginFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtEmail;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
					frame.textField_3.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		setResizable(false);
		setTitle("Login CXT_NetDisk");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(800, 300, 440, 324);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtEmail = new JTextField("  Email Account");
		txtEmail.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (txtEmail.getText().trim().equals("")) {
					txtEmail.setText("  Email Account");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				if (txtEmail.getText().equals("  Email Account")) {
					txtEmail.setText("");
				}
				
			}
		});
		
//		txtPhoneNumber.addFocusListener(new FocusListener() {
//			
//			public void focusLost(FocusEvent e) {
//				if (txtPhoneNumber.getText().trim().equals("")) {
//					txtPhoneNumber.setText("  Phone Number / Email Account / id");
//				}
//			}
//
//			public void focusGained(FocusEvent e) {
//				if (txtPhoneNumber.getText().equals("  Phone Number / Email Account / id")) {
//					txtPhoneNumber.setText("");
//				}
//			}
//			
//		});
		
		txtEmail.setBounds(37, 35, 344, 48);
		contentPane.add(txtEmail); 
		txtEmail.setColumns(10);
		
		textField_1 = new JTextField("  Password");
		textField_1.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (textField_1.getText().trim().equals("")) {
					textField_1.setText("  Password");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				if (textField_1.getText().equals("  Password")) {
					textField_1.setText("");
				}
				
			}
			
		});
		textField_1.setColumns(10);
		textField_1.setBounds(37, 107, 344, 48);
		contentPane.add(textField_1);
		
		JButton btnNewButton = new JButton("Sign Up");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (LoginFrame.this.getHeight() == 324) {
					LoginFrame.this.setSize(440, 650); 
				} else {
					LoginFrame.this.setSize(440, 324); 
				}
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.setBounds(37, 209, 174, 40);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Sign In");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String username = txtEmail.getText();
				String password = textField_1.getText();
				
				try {
					if (Service.login(username, password)) {
						MainJFrame.main(null);
						LoginFrame.this.setVisible(false);
						LoginFrame.this.dispose();
						
					} else {
						
						javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Username or Password Incorrect ");
						textField_1.setText("");
						txtEmail.setText("");
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton_1.setBounds(222, 209, 159, 40);
		contentPane.add(btnNewButton_1);
		
		JLabel lblForgetPassword = new JLabel("Forget Password?");
		lblForgetPassword.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
			}
		});
		lblForgetPassword.setBounds(265, 171, 125, 20);
		contentPane.add(lblForgetPassword);
		
		JPanel panel = new JPanel();
		panel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sign Up", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(37, 282, 344, 316);
		contentPane.add(panel);
		panel.setLayout(null);
		
		textField_2 = new JTextField("  Email Account");
		
		textField_2.addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				if (textField_2.getText().trim().equals("")) {
					textField_2.setText("  Email Account");
				}
			}

			public void focusGained(FocusEvent e) {
				if (textField_2.getText().equals("  Email Account")) {
					textField_2.setText("");
				}

			}
			
		});
		
		textField_2.setColumns(10);
		textField_2.setBounds(25, 26, 292, 48);
		panel.add(textField_2);
		
		textField_3 = new JTextField("");
		textField_3.setColumns(10);
		textField_3.setBounds(25, 153, 292, 41);
		panel.add(textField_3);
		
		textField_4 = new JTextField();
		textField_4.setBounds(25, 90, 138, 41);
		panel.add(textField_4);
		textField_4.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("Verifcation Code");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					Socket socket = new Socket(Config.SERVERIP, Config.REG_PORT);
					InputStream input = socket.getInputStream();
					OutputStream output = socket.getOutputStream();
					
					output.write(("{\"type\":\"yzm\",\"username\":\"" + textField_2.getText() + "\"}").getBytes());
					output.flush();
					
					byte[] b = new byte[1024 * 2];
					int len = input.read(b);
					
					String serverJson = new String(b, 0, len);
					JSONObject obj = JSONObject.fromObject(serverJson);
					if (obj.getString("type").equals("ok")) {
						javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Send Verifcation Code Successfully, Please Check Your Email Box");
					} else {
						javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "ERROR: Failed to Send Verifcation");
					}
					
					output.close();
					input.close();
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}
		});
		btnNewButton_2.setBounds(179, 90, 138, 41);
		panel.add(btnNewButton_2);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(25, 134, 69, 20);
		panel.add(lblPassword);
		
		textField_5 = new JTextField("");
		textField_5.setColumns(10);
		textField_5.setBounds(25, 210, 292, 41);
		panel.add(textField_5);
		
		JLabel lblRepeatPassword = new JLabel("Repeat Password");
		lblRepeatPassword.setBounds(25, 191, 138, 20);
		panel.add(lblRepeatPassword);
		
		JButton btnNewButton_3 = new JButton("COMPLETE");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String username = textField_2.getText();
				String password1 = textField_3.getText();
				String password2 = textField_5.getText();
				String code = textField_4.getText();
				if (username.trim().equals("") ||
						password1.trim().equals("") ||
						password2.trim().equals("") ||
						code.trim().equals("")) {
					
					javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "ALL Information MUST be Filled");
					
				} else {
					if (!password1.trim().equals(password2)) {
						javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Two Passwords are Different");
						textField_3.setText("");
						textField_5.setText("");
						textField_3.requestFocus();
					} else {
						//{"type":"reg", "username":"", "password":"", "code":""}
						String json = "{\"type\":\"reg\",\"username\":\"" + 
								username + "\",\"password\":\"" + 
								password1 + "\",\"code\":\"" + code + "\"}";
						
						Socket socket = null;
						try {
							socket = new Socket(Config.SERVERIP, Config.REG_PORT);
							InputStream input = socket.getInputStream();
							OutputStream output = socket.getOutputStream();
							
							output.write(json.getBytes());
							output.flush();
							
							byte[] b = new byte[1024];
							int len = input.read(b);
							String msg = new String(b, 0, len);
							
							JSONObject jsonobj = JSONObject.fromObject(msg);
							if (jsonobj.getString("type").equals("errorYzm")) {
								javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Invaild Verifcation Code");
								textField_3.setText("");
								textField_5.setText("");
//								textField_2.setText("");
								textField_4.setText("");
								
							} else if (jsonobj.getString("type").equals("errorCode")) {
								javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Wrong Verifcation Code");
								textField_3.setText("");
								textField_5.setText("");
//								textField_2.setText("");
								textField_4.setText("");
								
							} else if (jsonobj.getString("type").equals("errorUsername")) {
								javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "This Email Account Has been Used");
								textField_3.setText("");
								textField_5.setText("");
								textField_2.setText("");
								textField_4.setText("");
								
							} else if (jsonobj.getString("type").equals("")) {
								
							} else if (jsonobj.getString("type").equals("ok")) {
								javax.swing.JOptionPane.showMessageDialog(LoginFrame.this, "Successfully Sign Up!");
								textField_3.setText("");
								textField_5.setText("");
								textField_2.setText("");
								textField_4.setText("");
							}
							
							
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							try {
								socket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						
					}
					
				}
				
			}
		});
		btnNewButton_3.setBounds(25, 267, 292, 33);
		panel.add(btnNewButton_3);
	}
}
