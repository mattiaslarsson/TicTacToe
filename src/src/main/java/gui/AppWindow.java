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
	}

	private void init() {
		panelHeight = windowHeight;
		panelWidth = windowWidth;
		rootPane = new BorderPane();
		//TODO CHECK IF FIRST RUN AND DISPLAY FIRST TIME SCREEN IF SO
		startPanel = new StartPanel(controller, this);
		rootPane.setCenter(startPanel);
		scene = new Scene(rootPane,windowWidth,windowHeight);
		stage.setScene(scene);
		stage.show();
	}

	public void startGame(boolean myStart) {
		System.out.println("GAME SKALL STARTAS");
	}

	/*******************************************************************************************************************
	 * METHODS CALLED FROM CONTROLLER
	 ******************************************************************************************************************/

	public void connected(boolean conn) {
		connected = conn;

		if (conn) {
			Platform.runLater(() -> {
				startPanel.connected();
			});
		} else {
			Platform.runLater(() -> {
				startPanel.disconnected();
			});

		}

	}

	public void makeMove(int x, int y) {
		Platform.runLater(() -> {
			gameBoardPanel.makeMove(x, y);
		});
	}

	public void setOptions(int rowsToWin, boolean growable, boolean drawable){
		Platform.runLater(() -> {
			startPanel.setOptions(rowsToWin,growable,drawable);
		});
	}

	/*
	public void startGame(boolean start) {
		Platform.runLater(() -> {
			player1Turn.setValue(start);
			stage.setScene(initGameBoard());
		});
*/


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
