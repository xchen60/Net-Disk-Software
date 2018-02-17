package com.cxt.netdisk_server.db;

import java.sql.SQLException;
import java.sql.Connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/*
 * Manage MySql Database
 * 
 * @author CXT
 * 
 * */

public class DBManager {
	
	ComboPooledDataSource combo = new ComboPooledDataSource();

	private DBManager() {
		try {
			combo.setDriverClass("com.mysql.jdbc.Driver");
			combo.setPassword("123");
			combo.setUser("root");
			combo.setJdbcUrl("jdbc:mysql:///my_netdisk");
			combo.setMaxPoolSize(10);
			combo.setMinPoolSize(3);
			combo.setCheckoutTimeout(2000);
			
			combo.getConnection().close();

		} catch (Exception e) {
			System.out.println("DB Connection Failed!");
			e.printStackTrace();
		}

	}

	private static DBManager dbManager = new DBManager();

	public static DBManager getDBManager() {
		return dbManager;
	}

	public Connection getConn() throws SQLException {
		return combo.getConnection();
	}
	
//	public static void main(String[] args) throws SQLException {
//		System.out.println(DBManager.getDBManager().getConn());
//	}
}











