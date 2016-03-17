package me.daniel.draw;

import java.net.DatagramSocket;

public class Server implements Runnable
{
	@SuppressWarnings("unused")
	private DatagramSocket socket;

	public Server()
	{
		new Thread(this);
	}
	
	public void run() {
		try {
			socket = new DatagramSocket(43115);
		} catch(Exception e) {
			System.err.println("lol error at Server.java:run() catch 1");
		}
 	}

/**
 * while(true) {
			String portIn = JOptionPane.showInputDialog(null, "The port of the server", "Server", JOptionPane.QUESTION_MESSAGE);
			int port = 0;
			try {
				port = Integer.parseInt(portIn.trim());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String host = JOptionPane.showInputDialog(null, "The address (hostnames allowed)", "Server", JOptionPane.QUESTION_MESSAGE);
			InetAddress inet;
			try {
				inet = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, "Invalid address.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				socket = new DatagramSocket(port, inet);
			} catch (SocketException e) {
				JOptionPane.showMessageDialog(null, "Could not make a server.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
 */
}