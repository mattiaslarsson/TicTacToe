package logic;

import dao.DatabaseConnector;
import gamelogic.MainWindow;
import models.Message;
import models.Player;
import network.CommandHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class Controller {

    private CommandHandler cmdhandler;
    private Socket connection;
    private MainWindow view;
    private Player player, remotePlayer;
    private Message currMessage;
    private DatabaseConnector dbconn;

    private boolean connected = false;


    public Controller() {
        dbconn = new DatabaseConnector();
        if(dbconn.firstRun()){
            //TODO FANCY FIRST RUN ROUTINES
            System.out.println("Första starten");
        }
        setOwnPlayer();
    }

    /**
     * Registers the view with the controller.
     *
     * @param view MainWindow
     */
    public void registerView(MainWindow view) {
        this.view = view;
    }

    /**
     * Called when a connection to the server socket is accepted. Checks if
     * there are no ongoing connections and if not starts a commandhandler to
     * handle the new connection.
     *
     * @param connection Socket
     * @return
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
    private void setOwnPlayer() {
        //TODO fix load of info
        player = new Player("TestName", "TestSurName", 22);
    }

    /**
     * Method to handle a program exit. Sends disconnect and then gracefully exits closing all threads.
     */
    public void quit() {
        disconnect();
        dbconn.closeConnection();
        System.exit(0);
    }


    // METHODS THAT CORRESPONDS TO REMOTE COMMANDS

    /**
     * Sets the current remote player and gets data for previous games
     * from database.
     *
     * @param currPlayer Player
     */
    public void connectPlayer(Player currPlayer) {
        remotePlayer = currPlayer;
        connected = true;
        //TODO Get player information from database
        currMessage = new Message("connected", player);
        cmdhandler.sendMessage(currMessage);
    }

    public void connectedPlayer(Player currPlayer) {
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
        //TODO Send chatMessage to GUI
    }

    /**
     * Gets a move from remote and if it's the remote players turn sends it to view/gamelogic.
     *
     * @param x int
     * @param y int
     */
    public void remoteMakeMove(int x, int y) {
        view.makeMove(x,y);
    }

    /**
     * Disconnects a remote gracefully
     */
    public void remoteDisconnect() {
        cmdhandler.disconnect();
        view.connected(false);
        connected = false;
    }

    public void remoteStartGame(boolean startPlayer) {
        view.startGame(startPlayer);
    }

    // METHODS FOR SENDING COMMANDS

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
     * Sends disconnection message.
     */
    public void disconnect() {
        if (connected) {
            currMessage = new Message("disconnect");
            cmdhandler.sendMessage(currMessage);
            cmdhandler.disconnect();
            view.connected(false);
        }
    }

    // CHECK FOR FIRST USE

    private boolean firstUse(){
        return false;
    }

    // GETTERS & SETTERS

    public boolean getConnected() {
        return connected;
    }
}
