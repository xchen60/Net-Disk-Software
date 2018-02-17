package com.cxt.netdisk_server.service;

public class Mulu {
	
	public Mulu(String did, String dname, String rdid, long uid) {
		this.did = did;
		this.dname = dname;
		this.rdid = rdid;
		this.uid = uid;
	}
	
	private String did;
	private String rdid;
	private String dname;
	private long uid;
	
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getRdid() {
		return rdid;
	}
	public void setRdid(String rdid) {
		this.rdid = rdid;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	
}
