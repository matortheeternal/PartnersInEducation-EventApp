package sqltest;

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

	public void readDataBase() throws Exception {
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			connect = DriverManager.getConnection("jdbc:mysql://localhost/mydb?" + "user=root&password=TSET");

			// statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// resultSet gets the result of the SQL query
			resultSet = statement.executeQuery("select * from mydb.VOLUNTEER");
			writeResultSet(resultSet);

			// preparedStatements can use variables and are more efficient
			preparedStatement = connect.prepareStatement("insert into  mydb.VOLUNTEER values (?, ?, ?, ?, ? , ?, ?, ?)");
			// "myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
			// parameters start with 1
			preparedStatement.setString(1, "Test");
			preparedStatement.setString(2, "TestPassword");
			preparedStatement.setString(3, "TestFirst");
			preparedStatement.setString(4, "TestLast");
			preparedStatement.setDouble(5, 3.4);
			preparedStatement.setInt(6, 5);
			preparedStatement.setString(7, "TestRank");
			preparedStatement.setString(8, "0");
			//preparedStatement.executeUpdate();

			preparedStatement = connect.prepareStatement("SELECT username, password, firstName, lastName, totalHours from mydb.VOLUNTEER");
			resultSet = preparedStatement.executeQuery();
			writeResultSet(resultSet);

			// remove again the insert comment
			preparedStatement = connect.prepareStatement("delete from mydb.VOLUNTEER where username= ? ; ");
			preparedStatement.setString(1, "Test");
			preparedStatement.executeUpdate();

			resultSet = statement.executeQuery("select * from mydb.VOLUNTEER");
			writeMetaData(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		// now get some metadata from the database
		System.out.println("The columns in the table are: ");
		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
			System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// resultSet is initialized before the first data set
		while (resultSet.next()) {
			// it is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g., resultSet.getString(2);
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			String firstName = resultSet.getString("firstName");
			String lastName = resultSet.getString("lastName");
			Double totalHours = resultSet.getDouble("totalHours");
			int totalEvents = resultSet.getInt("totalEvents");
			String rank = resultSet.getString("rank");
			String events = resultSet.getString("events");
			System.out.println("Username: " + username);
			System.out.println("Password: " + password);
			System.out.println("firstName: " + firstName);
			System.out.println("lastName: " + lastName);
			System.out.println("totalHours: " + totalHours);
			System.out.println("totalEvents: " + totalEvents);
			System.out.println("rank: " + rank);
			System.out.println("events: " + events);
			System.out.println("");
		}
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