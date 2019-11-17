package com.im.model;

public class ResponseModel {
	public static final int STATUS_SUCCESS = 0, STATUS_FAILED = 1,
			STATUS_EXCEPTION = 2, STATUS_UNKNOW = 3;
	// 请求状态码 指示请求的状态
	private int status = 1;
	// 状态码 指示请求的数据状态
	private int statusCode = 1;
	// messgae：信息,errorMsg:错误信息
	private String messgae, errorMsg;
	// 数据
	private AppendData appendData;

	public ResponseModel(int status, int statuscode, String msg,
			AppendData data, String errormsg) {
		this.status = status;
		this.statusCode = statuscode;
		this.appendData = data;
		this.errorMsg = errormsg;
		this.messgae = msg;
	}

	public AppendData getAppendData() {
		return appendData;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getMessgae() {
		return messgae;
	}

	public int getStatus() {
		return status;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
