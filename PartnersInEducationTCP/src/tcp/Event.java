package tcp;

import java.io.Serializable;

public class Event implements Serializable {
	protected static final long serialVersionUID = 1589985257L;

	private String date;
	private String time;
	private String location;
	private String GPSLocation;
	private boolean completionStatus;
	private String description;
	private String startTime;
	private String endTime;
	private int totalTime;
	public String eventID;
	public String surveyIDs;
	
	public Event(String date, String time, String location, String GPSLocation,
			boolean completionStatus, String description, String startTime,
			String endTime, int totalTime, String eventID, String surveyIDs) {
		super();
		this.date = date;
		this.time = time;
		this.location = location;
		this.GPSLocation = GPSLocation;
		this.completionStatus = completionStatus;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.totalTime = totalTime;
		this.eventID = eventID;
		this.surveyIDs = surveyIDs;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public String getLocation() {
		return location;
	}

	public String getGPSLocation() {
		return GPSLocation;
	}

	public void setGPSLocation(String GPSLocation) {	// get location from android app data
		this.GPSLocation = GPSLocation;
	}

	public boolean isEventCompleted() {
		return completionStatus;
	}

	public void completed(double hours, Volunteer temp) {	// when user clicks submit/done
		this.completionStatus = true;
		temp.totalEvents++;
		temp.totalHours += hours;
	}

	public String getDescription() {
		return description;
	}

	public String getStartTime() {	// use time as a string
		return startTime;
	}

	public String getEndTime() { // use time as a string
		return endTime;
	}

	public int getTotalTime() {	// cast ^ strings to ints and subtract to get totalTime
		return totalTime;
	}

	public String getEventID() {
		return eventID;
	}
}
