package com.zhongzhou.common.utils;

public class HttpClientResult {
	
	/** 请求结果*/
	private String result;
	/** 请求状态码*/
	private int responseCode;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}
