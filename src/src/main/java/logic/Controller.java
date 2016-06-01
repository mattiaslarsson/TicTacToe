package logic;

import dao.DatabaseConnector;
import gui.AppWindow;
import models.GameStats;
import models.Message;
import models.Player;
import network.CommandHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Controller - handles logic between view (that contains gamelogic), database and network.
 * <p>
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class Controller {

	private CommandHandler cmdhandler = null;
	private Socket connection;
	private AppWindow view;
	private Player player, remotePlayer;
	private Message currMessage;
	private DatabaseConnector dbconn;

	private boolean connected = false;


	/**
	 * Initializes database connection
	 */
	public Controller() {
		dbconn = new DatabaseConnector();
	}

	/**
	 * Registers the view with the controller.
	 *
	 * @param view MainWindow
	 */
	public void registerView(AppWindow view) {
		this.view = view;
	}

	public boolean isFirstRun(){
		boolean firstRun = false;
		if (dbconn.firstRun()) {
			firstRun = true;
			connected = true;
		} else {
			player = dbconn.getOwnPlayer();
		}
		return firstRun;
	}

	/**
	 * Called when a connection to the server socket is accepted. Checks if
	 * there are no ongoing connections and if not starts a commandhandler to
	 * handle the new connection.
	 *
	 * @param connection Socket
	 * @return boolean
	 */
	public boolean connected(Socket connection) {
		boolean connectionOk = false;
		if (!connected) {
			this.connection = connection;
			cmdhandler = new CommandHandler(connection, this);
			Thread connectionThread = new Thread(cmdhandler);
			connectionThread.start();
			connectionOk = true;
			view.connected(true);
		}
		return connectionOk;
	}

	/**
	 * Method to load userdata for current session.
	 */
	public void setOwnPlayer(String firstName, String surName) {
		player = new Player(firstName, surName, 0);
		dbconn.createOwnPlayer(player.getFirstName(), player.getSurName());
		connected = false;
		view.initStart();
	}

	/**
	 * Method to handle a program exit. Sends disconnect and then gracefully exits closing all threads.
	 */
	public void quit() {
		disconnect();
		dbconn.closeConnection();
		System.exit(0);
	}

	/**
	 * Connects to a remote ip and sets up commandhandler for the connection. Sends own information.
	 *
	 * @param ip String
	 */
	public void connect(String ip) {
		try {
			Socket newConnection = new Socket(ip, 33000);
			if (connected(newConnection)) {
				currMessage = new Message("connect", player);
				cmdhandler.sendMessage(currMessage);
				connected = true;
			}
		} catch (IOException e) {
			System.out.println("Error connecting to remote: " + e.getStackTrace());
		}
	}

	/**
	 * Runs every time a game ends with a winner or draw. Updates data in database.
	 * player.
	 *
	 * @param points int
	 * @param opppoints int
	 * @param startTime long
	 * @param endTime long
	 * @param gridSize int
	 * @param numMoves int
	 */
	public void winning(int points, int opppoints, long startTime, long endTime, int gridSize, int numMoves){
		dbconn.addMatch(remotePlayer,points,opppoints,startTime,endTime,gridSize,numMoves);
	}

	/**
	 * Gets stats from database.
	 *
	 * @return GameStats
	 */
	public GameStats getCurrStats() {
		GameStats gs = null;
		if (connected && remotePlayer != null) {
			gs = dbconn.getStats(remotePlayer.getId());
			gs.setOppName(remotePlayer.getFirstName());
			gs.setOppSurname(remotePlayer.getSurName());
		}
		return gs;
	}

	/**
	 * Displays a message in view.
	 *
	 * @param message
	 */
	public void displayMessage(String message) {
		view.messageViewer(message);
	}

/***********************************************************************************************************************
 METHODS THAT CORRESPONDS TO REMOTE COMMANDS
 ***********************************************************************************************************************/

	/**
	 * Sends own information to remote when connected to.
	 *
	 * @param currPlayer Player
	 */
	public void connectPlayer(Player currPlayer) {
		currMessage = new Message("connected", player);
		cmdhandler.sendMessage(currMessage);
		connectedPlayer(currPlayer);
	}

	/**
	 * Gets information from remote, updates db and sets connected in view.
	 *
	 * @param currPlayer Player
	 */
	public void connectedPlayer(Player currPlayer) {
		dbconn.updatePlayer(currPlayer);
		remotePlayer = currPlayer;
		connected = true;
		view.connected(true);
	}

	/**
	 * Receives a remote chatmessage and sends it to view.
	 *
	 * @param chatMessage String
	 */
	public void remoteChatMessage(String chatMessage) {
		view.chatMessage(remotePlayer.getFirstName()+" "+remotePlayer.getSurName()+": "+chatMessage);
	}

	/**
	 * Gets a move from remote and if it's the remote players turn sends it to view/gamelogic.
	 *
	 * @param x int
	 * @param y int
	 */
	public void remoteMakeMove(int x, int y) {
		view.makeMove(x, y);
	}

	/**
	 * Disconnects a remote gracefully
	 */
	public void remoteDisconnect() {
		cmdhandler.disconnect();
		view.connected(false);
		connected = false;
		cmdhandler = null;
	}

	/**
	 * Sets the gameoptions from remote
	 *
	 * @param rowsToWin int
	 * @param growable  boolean
	 * @param drawable  boolean
	 */
	public void setGameOptions(int rowsToWin, boolean growable, boolean drawable) {
		view.setOptions(rowsToWin, growable, drawable);
	}

	/**
	 * Sets the starting player
	 *
	 * @param startPlayer boolean
	 */
	public void remoteStartGame(boolean startPlayer) {
		view.startGame(startPlayer);
	}

/***********************************************************************************************************************
 METHODS THAT SENDS COMMANDS VIA THE NETWORK
 ***********************************************************************************************************************/

	/**
	 * Randomizes the starting player and sends the result to the connected clients.
	 *
	 * @return boolean
	 */
	public boolean startGame() {
		boolean startPlayer = false;
		double start = Math.random();
		if (start > 0.5) {
			startPlayer = true;
		}
		currMessage = new Message("start", !startPlayer);
		cmdhandler.sendMessage(currMessage);
		return startPlayer;
	}

	/**
	 * Sends a chatstring to remote.
	 *
	 * @param chatString String
	 */
	public void chatMessage(String chatString) {
		currMessage = new Message("chat", chatString);
		cmdhandler.sendMessage(currMessage);
	}

	/**
	 * Makes a move and sends it to remote.
	 *
	 * @param x int
	 * @param y int
	 */
	public void makeMove(int x, int y) {
		currMessage = new Message("move");
		currMessage.addCommandData(x);
		currMessage.addCommandData(y);
		cmdhandler.sendMessage(currMessage);
	}

	/**
	 * Sends the gameoptions to remote.
	 *
	 * @param rowsToWin int
	 * @param growable  boolean
	 * @param drawable  boolean
	 */
	public void sendOptions(int rowsToWin, boolean growable, boolean drawable) {
		currMessage = new Message("gameoptions");
		currMessage.addCommandData(rowsToWin);
		currMessage.addCommandData(growable);
		currMessage.addCommandData(drawable);
		cmdhandler.sendMessage(currMessage);
	}

	/**
	 * Sends busy message when already connected.
	 */
	public void sendBusy(){
		currMessage = new Message("busy");
		if (cmdhandler != null) {
			cmdhandler.sendMessage(currMessage);
		}
	}

	/**
	 * Sends disconnection message.
	 */
	public void disconnect() {
		if (connected) {
			currMessage = new Message("disconnect");
			cmdhandler.sendMessage(currMessage);
			cmdhandler.disconnect();
			view.connected(false);
			cmdhandler = null;
		}
	}


	/***********************************************************************************************************************
	 * GETTERS AND SETTERS
	 ***********************************************************************************************************************/

	public boolean getConnected() {
		return connected;
	}
}
