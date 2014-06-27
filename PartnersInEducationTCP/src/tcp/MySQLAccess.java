package tcp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	public Volunteer getVolunteer(String username) throws Exception {
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			connect = DriverManager.getConnection("jdbc:mysql://localhost/mydb?user=root&password=TSET");

			// statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// resultSet gets the result of the SQL query
			preparedStatement = connect.prepareStatement("SELECT username, password, firstName, lastName, totalHours, totalEvents, rank, events from mydb.VOLUNTEER where username = ?");
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			
			// write to a volunteer object
			while (resultSet.next()) {
				String user = resultSet.getString("username");
				String password = resultSet.getString("password");
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				Double totalHours = resultSet.getDouble("totalHours");
				int totalEvents = resultSet.getInt("totalEvents");
				String rank = resultSet.getString("rank");
				String events = resultSet.getString("events");
				return new Volunteer(user, password, firstName, lastName, totalHours, totalEvents, rank, events);
			}
		}  catch (Exception e) {
			throw e;
		} finally {
			close();
		}
		return null;
	}
	
	public Event getEvent(String id) {
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			connect = DriverManager.getConnection("jdbc:mysql://localhost/mydb?user=root&password=TSET");

			// statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// resultSet gets the result of the SQL query
			preparedStatement = connect.prepareStatement("SELECT * from mydb.EVENT where eventID = ?");
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();
			
			// write to an event object
			while (resultSet.next()) {
				String date = resultSet.getString("date");
				String time = resultSet.getString("time");
				String location = resultSet.getString("location");
				String GPSLocation = resultSet.getString("GPSLocation");
				boolean completionStatus = (resultSet.getInt("completionStatus") == 1);
				String description = resultSet.getString("description");
				String startTime = resultSet.getString("startTime");
				String endTime = resultSet.getString("endTime");
				int totalTime = resultSet.getInt("totalTime");
				String eventID = resultSet.getString("eventID");
				String surveyIDs = resultSet.getString("survey");
				return new Event(date, time, location, GPSLocation, completionStatus, description, startTime, endTime, totalTime, eventID, surveyIDs);
			}
		} catch (Exception e) {
			// exception
		} finally {
			close();
		}
		return null;
	}

	// you need to close all three to make sure
	private void close() {
		try {
			resultSet.close();
			statement.close();
			connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}