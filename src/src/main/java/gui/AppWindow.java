package gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Controller;

/**
 * GUI controller class. Handles window and switches the content depending on what state the application is in.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-27.
 */
public class AppWindow {

	// Class variables
	private Controller controller;
	private Stage stage;

	// Window components
	private Scene scene;
	private BorderPane rootPane;

	private StartPanel startPanel;
	private GameBoardPanel gameBoardPanel;

	// App configuration
	private boolean connected = false;
	private boolean inGame = false;
	private String versionString = "TicTacToe v0.6a";
	private SimpleStringProperty titleProp;
	private double windowHeight = 600;
	private double windowWidth = 600;
	private double panelHeight;
	private double panelWidth;

	// Game configuration
	private boolean growable = false;
	private boolean drawable = false;
	private int rowsToWin = 3;

	public AppWindow(Stage stage, Controller controller) {
		this.controller = controller;
		this.stage = stage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				/**
				 * System exit closes all threads and connections.
				 */
				controller.quit();
			}
		});

		init();
	}

	/*******************************************************************************************************************
	 * VIEWS
	 ******************************************************************************************************************/

	/**
	 * Initializes the view when first ran. Checks if its a first time user.
	 */
	private void init() {
		panelHeight = windowHeight;
		panelWidth = windowWidth;
		rootPane = new BorderPane();
		titleProp = new SimpleStringProperty(versionString);
		//TODO CHECK IF FIRST RUN AND DISPLAY FIRST TIME SCREEN IF SO
		stage.titleProperty().bind(titleProp);
		initStart();
		scene = new Scene(rootPane, windowWidth, windowHeight);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Initializes the game board for a new game.
	 *
	 * @param myStart boolean
	 */
	public void initGame(boolean myStart) {
		gameBoardPanel = new GameBoardPanel(controller, this);
		gameBoardPanel.startGame(myStart);
		rootPane.setCenter(gameBoardPanel);
		inGame = true;
	}

	/**
	 * Initializes the start view
	 */
	public void initStart() {
		startPanel = new StartPanel(controller, this);
		rootPane.setCenter(startPanel);
	}

	/*******************************************************************************************************************
	 * METHODS CALLED FROM CONTROLLER
	 ******************************************************************************************************************/

	/**
	 * Called if connection status changes.
	 *
	 * @param conn boolean
	 */
	public void connected(boolean conn) {
		connected = conn;
		if (conn) {
			Platform.runLater(() -> {
				if (!inGame) {
					startPanel.connected();
				}
				titleProp.setValue(versionString+" - Connected");
			});
		} else {
			Platform.runLater(() -> {
				if (inGame) {
					initStart();
					inGame = false;
				}
				startPanel.disconnected();
				titleProp.setValue(versionString+" - Disconnected");
			});
		}

	}

	/**
	 * Adds a marker at the specified coordinates.
	 *
	 * @param x int
	 * @param y int
	 */
	public void makeMove(int x, int y) {
		Platform.runLater(() -> {
			gameBoardPanel.makeMove(x, y);
		});
	}

	/**
	 * Sets game options from remote
	 *
	 * @param rowsToWin int
	 * @param growable boolean
	 * @param drawable boolean
	 */
	public void setOptions(int rowsToWin, boolean growable, boolean drawable) {
		Platform.runLater(() -> {
			startPanel.setOptions(rowsToWin, growable, drawable);
		});
	}

	/**
	 * Initializes a game from remote.
	 *
	 * @param start boolean
	 */
	public void startGame(boolean start) {
		Platform.runLater(() -> {
			initGame(start);
		});
	}


	/*******************************************************************************************************************
	 * GETTERS & SETTERS
	 ******************************************************************************************************************/

	public int getRowsToWin() {
		return rowsToWin;
	}

	public void setRowsToWin(int rowsToWin) {
		this.rowsToWin = rowsToWin;
	}

	public boolean isDrawable() {
		return drawable;
	}

	public void setDrawable(boolean drawable) {
		this.drawable = drawable;

	}

	public boolean isGrowable() {
		return growable;
	}

	public void setGrowable(boolean growable) {
		this.growable = growable;
	}

	public double getPanelHeight() {
		return panelHeight;
	}

	public double getPanelWidth() {
		return panelWidth;
	}

}
