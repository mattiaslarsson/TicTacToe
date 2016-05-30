package gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI controller class. Handles window and switches the content depending on what state the application is in.
 * <p>
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
	private VBox chatDisplay;
	private HBox chatBox;

	private TextArea chatDisplayArea;

	// App configuration
	private boolean connected = false;
	private boolean inGame = false;
	private String versionString = "TicTacToe v0.6a";
	private SimpleStringProperty titleProp;

	private double windowHeight = 620;
	private double windowWidth = 600;
	private double chatDisplayWidth = 300;
	private double panelHeight;
	private double panelWidth;

	private List<String> chatMessages;

	// Game configuration
	private boolean growable = false;
	private boolean drawable = false;
	private int rowsToWin = 3;
	private boolean sound = true;

	public AppWindow(Stage stage, Controller controller) {
		this.controller = controller;
		this.stage = stage;

		chatMessages = new ArrayList<String>();

		Font.loadFont(getClass().getResource("/Roboto-Regular.ttf").toExternalForm(), 12);

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

	private void addMessage(String currMessage) {
		chatMessages.add(currMessage);
		String currDisplay = "";
		for (int i = chatMessages.size() - 1; i >= 0; i--) {
			currDisplay += chatMessages.get(i) + "\n";
		}
		System.out.println(currDisplay);
		chatDisplayArea.setText(currDisplay);
	}

	/*******************************************************************************************************************
	 * VIEWS
	 ******************************************************************************************************************/

	/**
	 * Initializes the view when first ran. Checks if its a first time user.
	 */
	private void init() {
		panelHeight = 600;
		panelWidth = windowWidth;
		rootPane = new BorderPane();
		titleProp = new SimpleStringProperty(versionString);
		stage.titleProperty().bind(titleProp);

		if (controller.isFirstRun()){
			firstStart();
		} else {
			initStart();
		}

		scene = new Scene(rootPane, windowWidth, windowHeight);
		scene.getStylesheets().add
				(getClass().getResource("/game.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Initializes the game board for a new game.
	 *
	 * @param myStart boolean
	 */
	public void initGame(boolean myStart) {
		stage.setWidth(windowWidth + chatDisplayWidth);
		gameBoardPanel = new GameBoardPanel(controller, this);
		gameBoardPanel.startGame(myStart);
		rootPane.setCenter(gameBoardPanel);
		inGame = true;
	}

	public void firstStart(){
		FirstRunPane firstRun = new FirstRunPane(controller);
		rootPane.setCenter(firstRun);
	}

	/**
	 * Initializes the start view
	 */
	public void initStart() {
		startPanel = new StartPanel(controller, this);
		chatBox = createChatbox();
		chatBox.setAlignment(Pos.BOTTOM_LEFT);
		chatDisplay = createChatDisplay();
		chatDisplay.setAlignment(Pos.TOP_RIGHT);
		if (!connected) {
			hidePanels();
		} else {
			displayPanels();
		}
	}

	private void displayPanels() {
		stage.setWidth(windowWidth + chatDisplayWidth);
		rootPane.setCenter(startPanel);
		rootPane.setBottom(chatBox);
		rootPane.setRight(chatDisplay);
	}

	private void hidePanels() {
		System.out.println("In hidepanels windowWidth: "+windowWidth);
		stage.setWidth(windowWidth);
		rootPane.setRight(null);
		rootPane.setBottom(null);
		rootPane.setCenter(startPanel);
	}

	/**
	 * Create chat box.
	 *
	 * @return VBox
	 */
	private HBox createChatbox() {
		double boxHeight = 20;

		HBox chatBox = new HBox();
		Button btnChat = new Button("Send");
		TextField fieldChat = new TextField();

		// Sizing
		chatBox.setPrefHeight(boxHeight);
		chatBox.setPrefWidth(windowHeight);
		fieldChat.setPrefSize((windowWidth - 100), boxHeight);
		btnChat.setPrefSize(100, boxHeight);

		fieldChat.setPromptText("Enter chat message");
		btnChat.setOnAction((e) -> {
			if (connected) {
				String chatString = fieldChat.getText();
				controller.chatMessage(chatString);
				addMessage(chatString);
				fieldChat.setText("");
			}
		});

		chatBox.getChildren().addAll(fieldChat, btnChat);
		return chatBox;
	}

	/**
	 * Create chat display area.
	 *
	 * @return HBox
	 */
	private VBox createChatDisplay() {
		VBox contentPane = new VBox();

		chatDisplayArea = new TextArea();
		chatDisplayArea.setPrefSize(chatDisplayWidth,windowHeight/2);
		chatDisplayArea.setEditable(false);

		contentPane.getChildren().addAll(chatDisplayArea);

		return contentPane;
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
					displayPanels();
				}
				titleProp.setValue(versionString + " - Connected");
			});
		} else {
			Platform.runLater(() -> {
				if (inGame) {
					inGame = false;
					initStart();
				}
				startPanel.disconnected();
				initStart();
				titleProp.setValue(versionString + " - Disconnected");
			});
		}

	}

	/**
	 * Display message from remote
	 *
	 * @param message String
	 */
	public void chatMessage(String message) {
		Platform.runLater(() -> {
			addMessage(message);
		});
	}

	/**
	 * Adds a marker at the specified coordinates.
	 *
	 * @param x int
	 * @param y int
	 */
	public void makeMove(int x, int y) {
		Platform.runLater(() -> {
			try {
				gameBoardPanel.makeMove(x, y);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sets game options from remote
	 *
	 * @param rowsToWin int
	 * @param growable  boolean
	 * @param drawable  boolean
	 */
	public void setOptions(int rowsToWin, boolean growable, boolean drawable) {
		Platform.runLater(() -> {
			this.setDrawable(drawable);
			this.setGrowable(growable);
			this.setRowsToWin(rowsToWin);
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

	public boolean getSound() { return sound; }

}
