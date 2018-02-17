package com.cxt.netdisk_server.service;

import java.io.IOException;

public class Start {
	
	public static void main(String[] args) {
		new Thread(){
			public void run() {
				try {
					LoginUserService.openServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread(){
			public void run() {
				try {
					RegUserService.openServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread(){
			public void run() {
				try {
					UploadFileService.openServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread(){
			public void run() {
				try {
					DownloadFileService.openServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}







