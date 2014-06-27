package tcp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	private JLabel pwlabel;
	// to hold the Username and later on the messages
	private JTextField tf;
	private JPasswordField pwf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort, tfUser;
	// to Logout and get the list of the users
	private JButton login, logout, getevent, getvolunteer, starttime, endtime;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Partners in Education");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// add the server and port fields to the GUI
		northPanel.add(serverAndPort);
		
		// the username and password fields
		JPanel loginPanel = new JPanel(new GridLayout(1, 5, 1, 3));
		tfUser = new JTextField("Username");
		pwf = new JPasswordField("");

		loginPanel.add(new JLabel("Username:  "));
		loginPanel.add(tfUser);
		loginPanel.add(new JLabel("Password:  "));
		loginPanel.add(pwf);
		loginPanel.add(new JLabel(""));
		// add the username and password fields to the GUI
		northPanel.add(loginPanel);
		
		// message field
		tf = new JTextField("0");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Application\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the buttons, some disabled until you login
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		getevent = new JButton("Get Event");
		getevent.addActionListener(this);
		getevent.setEnabled(false);
		getvolunteer = new JButton("Get Volunteer");
		getvolunteer.addActionListener(this);
		getvolunteer.setEnabled(false);
		starttime = new JButton("Start Time");
		starttime.addActionListener(this);
		starttime.setEnabled(false);
		endtime = new JButton("End Time");
		endtime.addActionListener(this);
		endtime.setEnabled(false);

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(getevent);
		southPanel.add(getvolunteer);
		southPanel.add(starttime);
		southPanel.add(endtime);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();
	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		getevent.setEnabled(false);
		getvolunteer.setEnabled(false);
		starttime.setEnabled(false);
		endtime.setEnabled(false);
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		connected = false;
	}
	
	// Generate hashed token
	public String getHashedToken() {
		String text = tfUser.getText() + pwf.getText();
		
		// get a hash
		MessageDigest digest;
		byte[] hash = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (hash == null)
			return "";
		else
			return Base64.encodeBase64URLSafeString(hash);
	}
		
	// Button or JTextField clicked
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		String token = getHashedToken();
		System.out.println("Hashed token: "+token);
		
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new QueryMessage(QueryMessage.LOGOUT, token));
			return;
		}
		// if it is the getevent button
		if(o == getevent) {
			client.sendMessage(new QueryMessage(QueryMessage.GETEVENT, token, tf.getText()));				
			return;
		}
		// if it is the getvolunteer button
		if(o == getvolunteer) {
			client.sendMessage(new QueryMessage(QueryMessage.GETVOLUNTEER, token, tfUser.getText()));				
			return;
		}
		// if it is the starttime button
		if(o == starttime) {
			client.sendMessage(new QueryMessage(QueryMessage.STARTTIME, token));
			return;
		}
		// if it is the endtime button
		if(o == endtime) {
			client.sendMessage(new QueryMessage(QueryMessage.ENDTIME, token));				
			return;
		}

		if(o == login) {
			// login request
			String username = tfUser.getText().trim();
			// if empty username ignore it
			if (username.length() == 0)
				return;
			// if empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// if empty or invalid port number, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, token, this);
			// test if we can start the Client
			if(!client.start())
				return;
			client.sendMessage(new QueryMessage(QueryMessage.LOGIN, token, username));
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable other buttons
			logout.setEnabled(true);
			getevent.setEnabled(true);
			getvolunteer.setEnabled(true);
			starttime.setEnabled(true);
			endtime.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
		}

	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

}