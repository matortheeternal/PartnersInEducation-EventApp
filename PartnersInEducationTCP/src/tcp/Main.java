package tcp;

public class Main {
	private static MySQLAccess sql = new MySQLAccess();
	
	public static void main(String[] args) {
		Volunteer v = null;
		try {
			v = sql.getVolunteer("mward");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (v != null)
			System.out.println(v.getFirstName());
		else
			System.out.println("volunteer was null");
		//Event e = sql.getEvent("0");
	}

}
