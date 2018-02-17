package com.cxt.netdisk_server.service;

import java.util.HashMap;

public class UserOnline {
	
	private static HashMap<String, Long> hashmap = new HashMap();
	
	
	public static void regUserOnline(String md5Key, long uid) throws Exception {
		
		synchronized (hashmap) {
			if (hashmap.containsValue(uid)) {
				throw new Exception();
			} else {
				hashmap.put(md5Key, uid);
			}
		}
		
	}
	
	
	public static boolean isUserOnline(String md5Key, long uid) {
		if (hashmap.containsValue(uid))
			return true;
		
		if (hashmap.containsKey(md5Key))
			return true;
		
		return true;
	}
	
	public static Long getUserOnlineUID(String md5Key) throws Exception {
		if (hashmap.containsKey(md5Key)) {
			return hashmap.get(md5Key);
		} else {
			throw new Exception();
		}
	}
	
	public static void logout(String md5Key) {
		
		synchronized (hashmap) {
			hashmap.remove(md5Key);
		}
	}
}













