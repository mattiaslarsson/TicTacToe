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

	public Controller() {

	}

	public void registerView(MainWindow view){
		this.view = view;
	}

	public void connectedPlayer(Socket connection) {
		this.connection = connection;
	}

}
