package tcp;

import java.net.*;
import java.io.*;
import java.util.*;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client {

	// for I/O
	private ObjectInputStream sInput; // to read from the socket
	private ObjectOutputStream sOutput; // to write on the socket
	private Socket socket;

	// gui
	private ClientGUI cgui;

	// the server, the port and the username
	private String server, username, token;
	private int port;

	/*
	 * Constructor called by console mode server: the server address port: the port number username: the username
	 */
	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, "", null);
	}

	/*
	 * Constructor call when used from a GUI in console mode the ClienGUI parameter is null
	 */
	Client(String server, int port, String username, String token, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.token = token;
		// save if we are in GUI mode or not
		this.cgui = cg;
	}

	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		}
		// if it failed not much I can so
		catch (Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try {
			sOutput.writeObject(new QueryMessage(QueryMessage.LOGIN, token, username));
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if (cgui == null)
			System.out.println(msg); // println in console mode
		else
			cgui.append(msg + "\n"); // append to the ClientGUI JTextArea (or whatever)
	}

	/*
	 * To send a message to the server
	 */
	void sendMessage(QueryMessage msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		} // not much else I can do
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		} // not much else I can do
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		} // not much else I can do

		// inform the GUI
		if (cgui != null)
			cgui.connectionFailed();

	}

	/*
	 * To start the Client in console mode use one of the following command > java Client > java Client username > java Client username portNumber > java Client username portNumber serverAddress at the console prompt If the portNumber is not specified 1500 is used If the serverAddress is not specified "localHost" is used If the username is not specified "Anonymous" is used > java Client is equivalent to > java Client Anonymous 1500 localhost are equivalent
	 * 
	 * In console mode, if an error occurs the program simply stops when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";
		String token = "t";

		// depending of the number of arguments provided we fall through
		switch (args.length) {
		// > javac Client username portNumber serverAddr
		case 3:
			serverAddress = args[2];
			// > javac Client username portNumber
		case 2:
			try {
				portNumber = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
				return;
			}
			// > javac Client username
		case 1:
			userName = args[0];
			// > java Client
		case 0:
			break;
		// invalid number of arguments
		default:
			System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if (!client.start())
			return;

		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while (true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if (msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new QueryMessage(QueryMessage.LOGOUT, token));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if (msg.equalsIgnoreCase("LOGIN")) {
				client.sendMessage(new QueryMessage(QueryMessage.LOGIN, token, userName));
			} else if (msg.equalsIgnoreCase("GETVOLUNTEER")) {
				client.sendMessage(new QueryMessage(QueryMessage.GETVOLUNTEER, token, "0"));
			} else if (msg.equalsIgnoreCase("GETEVENT")) {
				client.sendMessage(new QueryMessage(QueryMessage.GETEVENT, token, "0"));
			} else if (msg.equalsIgnoreCase("STARTTIME")) {
				client.sendMessage(new QueryMessage(QueryMessage.STARTTIME, token));
			} else if (msg.equalsIgnoreCase("ENDTIME")) {
				client.sendMessage(new QueryMessage(QueryMessage.ENDTIME, token));
			}
		}

		// done disconnect
		client.disconnect();
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {
		private boolean lfVolunteer = false;
		private boolean lfEvent = false;

		public void run() {
			while (true) {
				try {
					if (lfVolunteer) {
						Volunteer v = (Volunteer) sInput.readObject();
						if (cgui == null) {
							System.out.println("First name: "+v.getFirstName());
							System.out.println("Last name: "+v.getLastName());
							System.out.println("Total hours: "+v.getTotalHours());
							System.out.println("Events attended: "+v.getTotalEvents());
							System.out.println("> ");
						} else {
							cgui.append("First name: "+v.getFirstName()+ "\n");
							cgui.append("Last name: "+v.getLastName()+ "\n");
							cgui.append("Total hours: "+v.getTotalHours()+ "\n");
							cgui.append("Events attended: "+v.getTotalEvents()+ "\n");
						}
						lfVolunteer = false;
					} else if (lfEvent) {
						Event e = (Event) sInput.readObject();
						if (cgui == null) {
							System.out.println(e.getLocation());
							System.out.println("> ");
						} else {
							cgui.append("Event ID: "+e.getEventID() + "\n");
							cgui.append("Date: "+e.getDate() + "\n");
							cgui.append("Time: "+e.getTime() + "\n");
							cgui.append("Location: "+e.getLocation() + "\n");
							cgui.append("Description: "+e.getDescription() + "\n");
						}
						lfEvent = false;
					} else {
						String msg = (String) sInput.readObject();
						if (msg.equals("Volunteer:"))
							lfVolunteer = true;
						if (msg.equals("Event:"))
							lfEvent = true;
						// if console mode print the message and add back the prompt
						if (cgui == null) {
							System.out.println(msg);
							System.out.print("> ");
						} else {
							cgui.append(msg + "\n");
						}
					}
				} catch (IOException e) {
					display("The connection has been terminated.");
					if (cgui != null)
						cgui.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch (ClassNotFoundException e2) {
				}
			}
		}
	}
}