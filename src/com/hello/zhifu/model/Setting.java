package com.hello.zhifu.model;

public class Setting {
	private Integer mkey;
	private Double mvalue;
	
	/** default constructor */
	public Setting() {

	}
	/** full constructor */
	public Setting(Integer mkey, Double mvalue) {
		this.mkey = mkey;
		this.mvalue = mvalue;
	}
	
	public Integer getMkey() {
		return mkey;
	}
	public void setMkey(Integer mkey) {
		this.mkey = mkey;
	}
	public Double getMvalue() {
		return mvalue;
	}
	public void setMvalue(Double mvalue) {
		this.mvalue = mvalue;
	}
	
}
