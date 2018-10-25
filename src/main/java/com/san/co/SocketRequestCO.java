package com.san.co;

public class SocketRequestCO {

	private String method;
	private String action;
	private String requestBody;
	private String other;
	private int requestId;
	private boolean broadcastResponse = false;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public boolean isBroadcastResponse() {
		return broadcastResponse;
	}

	public void setBroadcastResponse(boolean broadcastResponse) {
		this.broadcastResponse = broadcastResponse;
	}
}
