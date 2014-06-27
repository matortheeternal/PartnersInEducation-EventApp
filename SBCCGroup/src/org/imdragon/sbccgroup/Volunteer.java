package org.imdragon.sbccgroup;

import java.io.Serializable;

public class Volunteer implements Serializable {
	protected static final long serialVersionUID = 7921158663L; //fix this?
	
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	public double totalHours;
	public int totalEvents;
	private String rank;
	public String eventIDs;

	public Volunteer(String username, String password, String firstName, String lastName, double totalHours, int totalEvents, String rank, String eventIDs) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.totalHours = totalHours;
		this.totalEvents = totalEvents;
		this.rank = rank;
		this.eventIDs = eventIDs;
	}

	public String getUserName() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
		
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public double getTotalHours() {
		return totalHours;
	}
	
	public int getTotalEvents() {
		return totalEvents;
	}

	public String getRank() {
		return rank;
	}

	public void createRank() {
		if (totalEvents >= 0 && totalEvents <= 5)
			rank = "Beginning Helper";
		else if (totalEvents > 5 && totalEvents <= 24)
			rank = "Good samaritan";
		else if (totalEvents >= 25)
			rank = "Master Helper";
	}
}