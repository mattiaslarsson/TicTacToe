package network;

import com.google.gson.Gson;
import logic.Controller;
import models.Message;
import models.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Handles communication between clients.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class CommandHandler implements Runnable {

	private Controller controller;
	private Socket connection;
	private PrintWriter out;
	private boolean disconnect = false;
	private Gson gson;
	/**
	 * Constructor that takes a socket as argument and opens a output writer in order to
	 * send and receive messages from connected client.
	 *
	 * @param connection Socket
	 */
	public CommandHandler(Socket connection, Controller controller) {
		gson = new Gson();
		this.controller = controller;
		this.connection = connection;
		try {
			out = new PrintWriter(connection.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Error setting up connection: "+e.getStackTrace());
		}
	}

	/**
	 * Method to gracefully disconnect. Closes outputstream and socket.
	 *
	 */
	public void disconnect() {
		disconnect = true;
		try {
			out.close();
			connection.close();
		} catch (IOException e) {
			System.out.println("Error closing connection: "+e.getStackTrace());
		}
	}

	/**
	 * Handles the running connection. Uses scanner to wait for input.
	 */
	@Override
	public void run() {
		Scanner sc = null;
		while(!disconnect) {
			try {
				sc = new Scanner(connection.getInputStream());
				while (sc.hasNextLine()) {
					parse(sc.nextLine());
				}
			} catch (IOException e) {
				System.out.println("Error with connection: "+e.getStackTrace());
			} finally {
				if (sc != null) {
					sc.close();
					disconnect();
				}
			}
		}
	}


	/**
	 * Method to send a command + data to client. Translates to json and sends through
	 * the printwriter.
	 *
	 * @param currMessage Message
	 */
	public void sendMessage (Message currMessage) {
		String jsonData = gson.toJson(currMessage);
		out.println(jsonData);
	}

	/**
	 * Handles the incoming json strings and parses it for commands and command data.
	 *
	 * @param input String
	 */
	private void parse(String input) {
		System.out.println("Parsing line: "+input); // TEST
		String currData;
		// Parses json to Message-object
		Message currMessage = gson.fromJson(input, Message.class);
		// Gets data from command
		List<String> cmdData = currMessage.getCommandData();

		switch (currMessage.getCommand()) {
			case "connect":
				Player currPlayer = gson.fromJson(cmdData.get(0),Player.class);
				controller.connectPlayer(currPlayer);
				break;
			case "disconnect":
				controller.remoteDisconnect();
				break;
			case "start":
				boolean startPlayer = gson.fromJson(cmdData.get(0),Boolean.class);
				controller.remoteStartGame();
			case "chat":
				String chatMessage = gson.fromJson(cmdData.get(0), String.class);
				controller.remoteChatMessage(chatMessage);
				break;
			case "move":
				int xMove = gson.fromJson(cmdData.get(0), Integer.class);
				int yMove = gson.fromJson(cmdData.get(1), Integer.class);
				controller.remoteMakeMove(xMove, yMove);
				break;

		}
	}
}
