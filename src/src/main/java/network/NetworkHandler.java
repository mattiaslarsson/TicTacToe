package network;

import logic.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Initializes serversocket and it's thread.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class NetworkHandler implements Runnable {

	private Controller controller;
	private Socket connection;
	private ServerSocket listener;
	private boolean appRunning = true;


	/**
	 * Constructor that initializes the serversocket for incomming connections.
	 *
	 * @param controller Controller
	 */
	public NetworkHandler(Controller controller){
		this.controller = controller;
		try {
			listener = new ServerSocket(33000);
		} catch (IOException e) {
			System.out.println("Error starting listening socket: "+e.getStackTrace());
		}
	}

	/**
	 * Runnable method. Listens for incoming connections and sets the status in controller.
	 *
	 */
	public void run() {
		while(appRunning) {
			try {
				connection = listener.accept();
				if (!controller.connected(connection)) {
					controller.sendBusy();
					connection.close();
				}
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
		try {
			listener.close();
		} catch (IOException e) {
			System.out.println("Error closing listening socket: "+e.getStackTrace());
		}
	}
}
