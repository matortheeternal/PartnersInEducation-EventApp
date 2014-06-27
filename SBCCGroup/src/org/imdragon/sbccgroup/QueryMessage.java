package org.imdragon.sbccgroup;

import java.io.Serializable;

public class QueryMessage implements Serializable {
	protected static final long serialVersionUID = 8567182459L; //fix this?
	
	static final int LOGIN = 0, GETVOLUNTEER = 1, GETEVENT = 2, LOGOUT = 3, STARTTIME = 4, ENDTIME = 5;
	private int type;
	private String token;
	private String data;
	
	// constructor
	public QueryMessage(int type, String token) {
		this.type = type;
		this.token = token;
	}
	
	public QueryMessage(int type, String token, String data) {
		this.type = type;
		this.token = token;
		this.data = data;
	}
	
	// getters
	public int getType() {
		return type;
	}
	public String getToken() {
		return token;
	}
	public String getData() {
		return data;
	}
	
}
