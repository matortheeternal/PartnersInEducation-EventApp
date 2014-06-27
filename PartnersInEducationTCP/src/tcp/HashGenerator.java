package tcp;

import javax.swing.*;

import org.apache.commons.codec.binary.Base64;

import tcp.ServerGUI.ServerRunning;

import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = 1L;
	private JButton generateHash;
	private JTextField tfUsername, tfHash;
	private JPasswordField tfPassword;
	
	public HashGenerator() {
		super("Hash Generator");
		JPanel north = new JPanel(new GridLayout(3,2,1,5));
		tfUsername = new JTextField("Username");
		north.add(new JLabel("Username: "));
		north.add(tfUsername);
		tfPassword = new JPasswordField();
		north.add(new JLabel("Password: "));
		north.add(tfPassword);
		tfHash = new JTextField("hash");
		north.add(new JLabel("Hash: "));
		north.add(tfHash);
		// to stop or start the server, we start with "Start"
		add(north, BorderLayout.NORTH);
		
		// the event and chat room
		JPanel center = new JPanel();
		generateHash = new JButton("Generate Hash");
		generateHash.addActionListener(this);
		center.add(generateHash);
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(700, 150);
		setVisible(true);
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent g) {
		String text = tfUsername.getText() + tfPassword.getText();
		System.out.println(text);
		
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
		
		tfHash.setText(new String(Base64.encodeBase64(hash)));
	}
	
	// entry point
	public static void main(String[] arg) {
		new HashGenerator();
	}

	public void windowClosing(WindowEvent e) {
		// dispose the frame
		dispose();
		System.exit(0);
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

}
