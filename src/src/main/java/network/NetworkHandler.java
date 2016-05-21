package network;

import logic.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class NetworkHandler implements Runnable {

	private Controller controller;
	private Socket connection;
	private ServerSocket listener;
	private boolean connected;
	private boolean appRunning = true;


	/**
	 * Constructor that initializes the serversocket for incomming connections.
	 *
	 * @param controller
	 */
	public NetworkHandler(Controller controller){
		connected = false;
		this.controller = controller;
		try {
			listener = new ServerSocket(33000);
		} catch (IOException e) {
			System.out.println("Error starting listening socket: "+e.getStackTrace());
		}
	}

	/**
	 * Runnable method. Listens for incomming connections and sets the status in controller.
	 *
	 */
	public void run() {
		while(appRunning) {
			try {
				connection = listener.accept();
				controller.connectedPlayer(connection);
			} catch (IOException e) {
				System.out.println("Error setting a socket for incomming connection: "+e.getStackTrace());
			}
		}
	}

	/**
	 * Disconnect method to handle graceful exits.
	 */
	public void disconnect() {
		appRunning = false;
	}
}
