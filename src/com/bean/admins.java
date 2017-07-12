package com.bean;
/*
 *  管理员信息封装类，有id、用户名、密码信息
 */
public class admins {
	private int id;
	private String loginid;
	private String passwords;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getPasswords() {
		return passwords;
	}
	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}

	

	
}
