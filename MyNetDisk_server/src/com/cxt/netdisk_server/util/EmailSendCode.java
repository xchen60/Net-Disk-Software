package com.cxt.netdisk_server.util;

import org.apache.commons.mail.HtmlEmail;

public class EmailSendCode {
	
	public static void sendEmail(String emailAddress, String code) {
		
		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName("smtp.163.com");
			email.setCharset("UTF-8");
			email.addTo(emailAddress); 

			email.setFrom("xchen60@163.com", "CXT_NetDisk Verification Code");

			email.setAuthentication("xchen60@163.com", "c931210");

			email.setSubject("CXT_NetDisk Verification Code");
			email.setMsg("Verification Code is: " + code);

			email.send();
		
//			System.out.println("Success Sending");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
//	public static void main(String[] args) {
//		sendEmail("2128771615@qq.com", "123456");
//	}

}
