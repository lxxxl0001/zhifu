package com.hello.zhifu.model;

import java.sql.Timestamp;

public class Award {

	private Long termNum;
	private Timestamp awardDate;
	private Long awardTime;
	private Long nextTime;
	private String awardNumbers;
	
	public Long getTermNum() {
		return termNum;
	}
	public void setTermNum(Long termNum) {
		this.termNum = termNum;
	}
	public Timestamp getAwardDate() {
		return awardDate;
	}
	public void setAwardDate(Timestamp awardDate) {
		this.awardDate = awardDate;
	}
	public Long getAwardTime() {
		return awardTime;
	}
	public void setAwardTime(Long awardTime) {
		this.awardTime = awardTime;
	}
	public Long getNextTime() {
		return nextTime;
	}
	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}
	public String getAwardNumbers() {
		return awardNumbers;
	}
	public void setAwardNumbers(String awardNumbers) {
		this.awardNumbers = awardNumbers;
	}

}
