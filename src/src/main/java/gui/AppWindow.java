package gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Controller;

/**
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
	private String title = "";
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

	private void init() {
		panelHeight = windowHeight;
		panelWidth = windowWidth;
		rootPane = new BorderPane();
		//TODO CHECK IF FIRST RUN AND DISPLAY FIRST TIME SCREEN IF SO
		initStart();
		scene = new Scene(rootPane, windowWidth, windowHeight);
		stage.setScene(scene);
		stage.show();
	}

	public void initGame(boolean myStart) {
		gameBoardPanel = new GameBoardPanel(controller, this);
		gameBoardPanel.startGame(myStart);
		rootPane.setCenter(gameBoardPanel);
		inGame = true;
	}

	public void initStart() {
		startPanel = new StartPanel(controller, this);
		rootPane.setCenter(startPanel);
	}

	/*******************************************************************************************************************
	 * METHODS CALLED FROM CONTROLLER
	 ******************************************************************************************************************/

	public void connected(boolean conn) {
		connected = conn;

		if (conn) {
			Platform.runLater(() -> {
				if (!inGame) {
					startPanel.connected();
				}
			});
		} else {
			Platform.runLater(() -> {
				if (inGame) {
					initStart();
					inGame = false;
				}
				startPanel.disconnected();
			});

		}

	}

	public void makeMove(int x, int y) {
		Platform.runLater(() -> {
			gameBoardPanel.makeMove(x, y);
		});
	}

	public void setOptions(int rowsToWin, boolean growable, boolean drawable) {
		Platform.runLater(() -> {
			startPanel.setOptions(rowsToWin, growable, drawable);
		});
	}


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
