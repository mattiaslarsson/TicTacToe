package logic;

import gamelogic.MainWindow;
import models.Player;
import network.CommandHandler;

import java.net.Socket;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class Controller {

	private CommandHandler cmdhandler;
	private Socket connection;
	private MainWindow view;
	private Player player;

	private boolean connected = false;


	public Controller() {

	}

	public void registerView(MainWindow view){
		this.view = view;
	}

	public boolean connected(Socket connection) {
		boolean connectionOk = false;
		if (!connected) {
			this.connection = connection;
			connectionOk = true;
		}
		return connectionOk;
	}



	// METHODS THAT CORRESPONDS TO COMMANDS FROM COMMANDHANDLER

	public void connectPlayer(Player currPlayer) {
		player = currPlayer;
		connected = true;
		//TODO Get player information from database
	}



}
