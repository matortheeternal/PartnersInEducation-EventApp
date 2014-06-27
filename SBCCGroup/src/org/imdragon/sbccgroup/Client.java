package org.imdragon.sbccgroup;

import java.net.*;
import java.io.*;
import java.util.*;

import android.widget.Toast;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client {

	// for I/O
	private ObjectInputStream sInput; // to read from the socket
	private ObjectOutputStream sOutput; // to write on the socket
	private Socket socket;

	// the server, the port and the username
	private String server, username, token;
	private int port;
	private boolean connected = false;
	private LoginActivity gui;
	
	// client volunteer, events
	Volunteer volunteer;
	ArrayList<Event> events;

	// constructor
	Client(String server, int port, String username, String token, LoginActivity gui) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.token = token;
		this.gui = gui;
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
//			display("Error connecting to server:" + ec);
			return false;
		}

//		String msg = "Connection accepted " + socket.getInetAddress() + ":"
//				+ socket.getPort();
////		display(msg);
//
		/* Creating both Data Stream */
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
//			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server
		new ListenFromServer().start();
		try {
			sOutput.writeObject(new QueryMessage(QueryMessage.LOGIN, token,
					username));
		} catch (IOException eIO) {
//			display("Exception doing login : " + eIO);
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
		gui.toast(msg);
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

	// When something goes wrong Close the Input/Output streams and disconnect not much to do in the catch clause
	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		} 
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		} 
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		} 

		connected = false;
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
						volunteer = (Volunteer) sInput.readObject();
						lfVolunteer = false;
					} else if (lfEvent) {
						Event event = (Event) sInput.readObject();
						addEvent(event);
						lfEvent = false;
					} else {
						String msg = (String) sInput.readObject();
						if (msg.equals("Volunteer:"))
							lfVolunteer = true;
						if (msg.equals("Event:"))
							lfEvent = true;
						//gui.sendMsg(msg + "\n");
					}
				} catch (IOException e) {
//					display("Server has closed the connection: " + e);
					;//gui.connectionClosed();
				}
				// can't happen with a String object but need the catch anyhow
				catch (ClassNotFoundException e2) {
				}
			}
		}
	}

	private void addEvent(Event event) {
		for (int i = 0; i < events.size(); i++) {
			if (event.equals(events.get(i)))
				return;
		}
		events.add(event);
	}

	public Volunteer getVolunteer() {
		return volunteer;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}
	
	public boolean getConnected() {
		return connected;
	}
}