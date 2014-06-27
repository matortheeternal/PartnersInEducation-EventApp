package tcp;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI gui;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned off to stop the server
	private boolean listening;
	// arraylist of volunteers
	private ArrayList<Volunteer> alv;
	// arraylist of events
	private ArrayList<Event> ale;
	// sql connection
	private MySQLAccess sql = new MySQLAccess();

	/*
	 * server constructor that receive the port to listen to for connection as parameter in console
	 */
	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerGUI gui) {
		// GUI or not
		this.gui = gui;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		// ArrayList for volunteers, events
		alv = new ArrayList<Volunteer>();
		ale = new ArrayList<Event>();
	}

	public void start() {
		listening = true;
		/* create socket server and wait for connection requests */
		try {
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while (listening) {
				display("Server listening on port: " + port + ".");

				Socket socket = serverSocket.accept(); // accept connection
				if (!listening)
					break;
				ClientThread t = new ClientThread(socket); // make a thread of it
				al.add(t); // save it in the ArrayList
				t.start();
			}
			try {
				serverSocket.close();
				for (int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch (IOException ioE) {
						// do nothing
					}
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	// For the GUI to stop the server
	protected void stop() {
		listening = false;
		// connect to myself as Client to exit statement
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		} catch (Exception e) {
			// nothing
		}
	}

	// Display an event (not a message) to the console or the GUI
	private void display(String msg) {
		String event = sdf.format(new Date()) + " " + msg;
		if (gui == null)
			System.out.println(event);
		else
			gui.appendEvent(event + "\n");
	}

	// to send a volunteer class to a Client
	synchronized Volunteer getVolunteer(String username) {
		Volunteer v = null;
		try {
			v = sql.getVolunteer(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

	// to send an event class to a Client
	synchronized Event getEvent(String eventID) {
		Event e = sql.getEvent(eventID);
		return e;
	}

	// client log out
	synchronized void logout(int id) {
		// find id, then log out
		for (int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if (ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}

	/*
	 * To run as a console application just open a console window and: > java Server > java Server portNumber If the port number is not specified 1500 is used
	 */
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified
		int portNumber = 1500;
		switch (args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
		case 0:
			break;
		default:
			System.out.println("Usage is: > java Server [portNumber]");
			return;

		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for disconnecting)
		int id;
		// the username of the Client
		String username;
		// a query message for some type of data
		QueryMessage qm;
		// the date of connection
		String date;
		// start time, end time
		Long startTime;
		Long endTime;
		// login token
		String loginToken;

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			// creating data streams
			System.out.println("Thread trying to create Object Input/Output Streams");
			try {
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
				// read the username
				qm = (QueryMessage) sInput.readObject();
				display(qm.getData() + " just connected.");
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			} catch (ClassNotFoundException e) {
				// nothing
			}
			date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while (keepGoing) {
				// read a String (which is an object)
				try {
					qm = (QueryMessage) sInput.readObject();
				} catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;
				} catch (ClassNotFoundException e2) {
					break;
				}

				// Switch on the type of message received
				switch (qm.getType()) {
				case QueryMessage.LOGIN:
					Volunteer qv = getVolunteer(qm.getData());
					loginToken = qm.getToken();
					if (loginToken == null) {
						display("Login token was null.");
					} else if (qv == null) {
						display("Login to "+qm.getData()+" failed (invalid username).");
						sendMsg("Login failed (invalid username).");
						logout(id);
						close();
					} else if (loginToken.equals(qv.getPassword())) {
						username = qm.getData();
						display("Login to " + username + " accepted.");
					} else {
						display("Login to "+qm.getData()+" failed (incorrect password).");
						sendMsg("Login failed (incorrect password).");
						logout(id);
						close();
					}
					break;
				case QueryMessage.LOGOUT:
					// logout
					if (qm.getToken() == null) {
						display("qm.getToken() returned null");
					} else if (loginToken == null) {
						display("loginToken was null");
					} else if (loginToken.equals(qm.getToken())) {
						display("Logout: " + username);
						logout(id);
						close();
					} else {
						display("Logout: " + username + " failed.");
					}
					break;
				case QueryMessage.GETEVENT:
					// send event to user
					if (qm.getToken() == null) {
						display("qm.getToken() returned null");
					} else if (loginToken == null) {
						display("loginToken was null");
					} else if (loginToken.equals(qm.getToken())) {
						display(username + ": getEvent(" + qm.getData() + ")");
						Event e = getEvent(qm.getData());
						if (e != null) {
							sendMsg("Event:");
							sendObject(e);
						} else {
							sendMsg("Error: Event not found.");
						}
					} else {
						display(username + ": getEvent failed.");
					}
					break;
				case QueryMessage.GETVOLUNTEER:
					// send volunteer to user
					if (qm.getToken() == null) {
						display("qm.getToken() returned null");
					} else if (loginToken == null) {
						display("loginToken was null");
					} else if (loginToken.equals(qm.getToken())) {
						display(username + ": getVolunteer(" + qm.getData()+")");
						Volunteer v = getVolunteer(qm.getData());
						if (v != null) {
							sendMsg("Volunteer:");
							sendObject(v);
						} else {
							sendMsg("Error: Volunteer not found.");
						}
					} else {
						display(username + ": getVolunteer failed.");
					}
					break;
				case QueryMessage.STARTTIME:
					// start volunteer timer
					if (qm.getToken() == null) {
						display("qm.getToken() returned null");
					} else if (loginToken == null) {
						display("loginToken was null");
					} else if (loginToken.equals(qm.getToken())) {
						startTime = System.currentTimeMillis();
						display(username + ": start time = " + startTime);
						sendMsg("Start time: "+startTime);
					} else {
						display(username + ": start time failed.");
					}
					break;
				case QueryMessage.ENDTIME:
					// end volunteer timer
					if (qm.getToken() == null) {
						display("qm.getToken() returned null");
					} else if (loginToken == null) {
						display("loginToken was null");
					} else if (loginToken.equals(qm.getToken())) {
						endTime = System.currentTimeMillis();
						display(username + ": endtime = " + endTime);
						sendMsg("End time: "+endTime);
						display(username + ": duration = "+ (endTime - startTime));
						sendMsg("Duration: "+ (endTime - startTime));
					} else {
						display(username + ": end time failed.");
					}
					break;
				}
			}
			// if stopped, log out and close the connection
			logout(id);
			close();
		}

		// try to close the connection
		private void close() {
			try {
				if (sOutput != null)
					sOutput.close();
			} catch (Exception e) {
			}
			try {
				if (sInput != null)
					sInput.close();
			} catch (Exception e) {
			}
			;
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}

		// Write a QueryMessage to the Client output stream
		private boolean sendObject(Object o) {
			// if Client is still connected send the message to it
			if (!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(o);
			}
			// if an error occurs, do not abort just inform the user
			catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}

		private boolean sendMsg(String string) {
			// if Client is still connected send the message to it
			if (!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(string);
			}
			// if an error occurs, do not abort just inform the user
			catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}
