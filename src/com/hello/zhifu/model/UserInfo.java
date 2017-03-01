package com.hello.zhifu.model;

public class UserInfo {

	private Integer userid;
	private String openid;
	private Integer money;
	private Integer agent;
	private Integer parent;
	
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}
	public Integer getAgent() {
		return agent;
	}
	public void setAgent(Integer agent) {
		this.agent = agent;
	}
	public Integer getParent() {
		return parent;
	}
	public void setParent(Integer parent) {
		this.parent = parent;
	}

}
