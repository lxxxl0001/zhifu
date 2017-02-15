package com.hello.zhifu.model;

public class Flowing {
	
	private Long flowid;
	private Integer userid;
	private Long termNum;
	private Integer carNum;
	private Double buyAmount;
	private Double haveAmount;
	private Integer isPay;
	private Integer isOpen;
	private Integer isSend;
	
	public Long getFlowid() {
		return flowid;
	}
	public void setFlowid(Long flowid) {
		this.flowid = flowid;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Long getTermNum() {
		return termNum;
	}
	public void setTermNum(Long termNum) {
		this.termNum = termNum;
	}
	public Integer getCarNum() {
		return carNum;
	}
	public void setCarNum(Integer carNum) {
		this.carNum = carNum;
	}
	public Double getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(Double buyAmount) {
		this.buyAmount = buyAmount;
	}
	public Double getHaveAmount() {
		return haveAmount;
	}
	public void setHaveAmount(Double haveAmount) {
		this.haveAmount = haveAmount;
	}
	public Integer getIsPay() {
		return isPay;
	}
	public void setIsPay(Integer isPay) {
		this.isPay = isPay;
	}
	public Integer getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}
	public Integer getIsSend() {
		return isSend;
	}
	public void setIsSend(Integer isSend) {
		this.isSend = isSend;
	}
}
