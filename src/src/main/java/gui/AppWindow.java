package gui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import logic.Controller;
import models.GameStats;

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
    private GridPane statsPanel;
    private HBox chatBox;

    private TextArea chatDisplayArea;

    private Label lblOppName, lblPrevWins, lblPrevLoss, lblPrevDraws, lblTotPoints, lblTotPointsLoss, lblTotWins, lblTotDefeats, lblTotDraws, lblAvgPoints, lblAvgPointsLost, lblAvgMoves, lblAvgGrid;
    private Label lblOppNameText, lblPrevWinsText, lblPrevLossText, lblPrevDrawsText, lblTotPointsText, lblTotPointsLossText, lblTotWinsText, lblTotDefeatsText, lblTotDrawsText, lblAvgPointsText, lblAvgPointsLostText, lblAvgMovesText, lblAvgGridText;

    // App configuration
    private boolean connected = false;
    private boolean inGame = false;
    private String versionString = "TicTacToe v0.8a";
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
    private boolean music = true;

    private GameStats gs;

    private MediaPlayer mPlayer;

    public AppWindow(Stage stage, Controller controller) {
        this.controller = controller;
        this.stage = stage;

        chatMessages = new ArrayList<String>();

        Media bossa = new Media(getClass().getResource("/bossa.mp3").toString());
        mPlayer = new MediaPlayer(bossa);
        if (music) {
            playMusic();
        }
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

        if (controller.isFirstRun()) {
            firstStart();
        } else {
            initStart();
        }

        scene = new Scene(rootPane, windowWidth, windowHeight);
        scene.getStylesheets().add
                (getClass().getResource("/game.css").toExternalForm());
        stage.setScene(scene);
		messageViewer("Starting up");
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

    public void firstStart() {
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

	public void initRestart() {
		stage.setWidth(windowWidth + chatDisplayWidth);
		stage.setHeight(windowHeight + (chatBox.getHeight()));
		displayPanels();
	}

    private void displayPanels() {
        stage.setWidth(windowWidth + chatDisplayWidth);
        stage.setHeight(windowHeight + (chatBox.getHeight()));
        startPanel.setMinSize(windowWidth, windowHeight - (chatBox.getHeight()));
        rootPane.setCenter(startPanel);
        rootPane.setBottom(chatBox);
        rootPane.setRight(chatDisplay);
        fillStats();
    }

    private void hidePanels() {
        System.out.println("In hidepanels windowWidth: " + windowWidth);
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
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

        chatBox.setStyle("-fx-background-image: url(\"/textured_paper.png\");-fx-background-size: 600, 600;-fx-background-repeat: no-repeat;");
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
        chatDisplayArea.setId("chatDisplay");
        chatDisplayArea.setPrefSize(chatDisplayWidth, windowHeight / 2);
        chatDisplayArea.setEditable(false);

        statsPanel = createStatsDisplay();
        statsPanel.setPrefSize(chatDisplayWidth, windowHeight / 2);

        contentPane.getChildren().addAll(chatDisplayArea, statsPanel);

        return contentPane;
    }

    private GridPane createStatsDisplay() {
        GridPane statsPane = new GridPane();
        // Opponent
        lblOppName = new Label("Stats against:");
        lblOppNameText = new Label("");
        lblPrevWins = new Label("Wins:");
        lblPrevWinsText = new Label("");
        lblPrevLoss = new Label("Losses:");
        lblPrevLossText = new Label("");
        lblPrevDraws = new Label("Draws:");
        lblPrevDrawsText = new Label("");
        // Totals
        lblTotPoints = new Label("Total points:");
        lblTotPointsText = new Label("");
        lblTotPointsLoss = new Label("Total points lost:");
        lblTotPointsLossText = new Label("");
        lblTotWins = new Label("Total wins:");
        lblTotWinsText = new Label("");
        lblTotDefeats = new Label("Total losses:");
        lblTotDefeatsText = new Label("");
        lblTotDraws = new Label("Total draws:");
        lblTotDrawsText = new Label("");
        // Average
        lblAvgPoints = new Label("Average points:");
        lblAvgPointsText = new Label("");
        lblAvgPointsLost = new Label("Average points lost:");
        lblAvgPointsLostText = new Label("");
        lblAvgMoves = new Label("Average moves:");
        lblAvgMovesText = new Label("");
        lblAvgGrid = new Label("Average gridsize:");
        lblAvgGridText = new Label("");

        statsPane.add(lblOppName, 0, 0);
        statsPane.add(lblOppNameText, 1, 0);
        statsPane.add(lblPrevWins, 0, 1);
        statsPane.add(lblPrevWinsText, 1, 1);
        statsPane.add(lblPrevLoss, 0, 2);
        statsPane.add(lblPrevLossText, 1, 2);
        statsPane.add(lblPrevDraws, 0, 3);
        statsPane.add(lblPrevDrawsText, 1, 3);
        statsPane.add(lblTotWins, 0, 4);
        statsPane.add(lblTotWinsText, 1, 4);
        statsPane.add(lblTotDefeats, 0, 5);
        statsPane.add(lblTotDefeatsText, 1, 5);
        statsPane.add(lblTotDraws, 0, 6);
        statsPane.add(lblTotDrawsText, 1, 6);
        statsPane.add(lblTotPoints, 0, 7);
        statsPane.add(lblTotPointsText, 1, 7);
        statsPane.add(lblTotPointsLoss, 0, 8);
        statsPane.add(lblTotPointsLossText, 1, 8);
        statsPane.add(lblAvgPoints, 0, 9);
        statsPane.add(lblAvgPointsText, 1, 9);
        statsPane.add(lblAvgPointsLost, 0, 10);
        statsPane.add(lblAvgPointsLostText, 1, 10);
        statsPane.add(lblAvgMoves, 0, 11);
        statsPane.add(lblAvgMovesText, 1, 11);
        statsPane.add(lblAvgGrid, 0, 12);
        statsPane.add(lblAvgGridText, 1, 12);

        return statsPane;
    }

    private void fillStats() {
        GameStats gs = controller.getCurrStats();
        System.out.println("In appwindow - fillstats");
        if (gs != null) {
            System.out.println("appwindow should fill stats");
            lblOppNameText.setText(gs.getOppName() + " " + gs.getOppSurname());
            lblPrevWinsText.setText(gs.getPrevWins());
            lblPrevLossText.setText(gs.getPrevDefeats());
            lblPrevDrawsText.setText(gs.getPrevDraws());
            lblTotPointsText.setText(gs.getTotPoints());
            lblTotDefeatsText.setText(gs.getTotDefeats());
            lblTotDrawsText.setText(gs.getTotDraws());
            lblTotPointsLossText.setText(gs.getTotPointsGiven());
            lblTotWinsText.setText(gs.getTotWins());
            lblAvgPointsText.setText(gs.getAvgPoints());
            lblAvgPointsLostText.setText(gs.getAvgPointsGiven());
            lblAvgMovesText.setText(gs.getAvgMoves());
            lblAvgGridText.setText(gs.getAvgGridSize());
        }
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
	 * Plays the cozy music
	 */
	public void playMusic() {
		mPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mPlayer.play();
	}

    /**
     * Stops the cozy music
     */
    public void stopMusic() {
        mPlayer.stop();
    }

	/**
	 * Lowers the volume of the music
	 *
	 * @param lower boolean true for lower, false for normal
     */
	public void lowerMusic(boolean lower) {
		if (lower) {
			mPlayer.setVolume(0.2);
		} else {
			mPlayer.setVolume(1);
		}
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

	public void messageViewer(String message) {
		Text msgText = new Text(message);
		msgText.setFill(Color.WHITE);
		msgText.setId("msgText");
		Rectangle msgRect = new Rectangle();
		msgRect.setId("msgRect");
		msgText.setFont(Font.font(50));
		msgRect.setWidth(msgText.getBoundsInLocal().getWidth()+20);
		msgRect.setHeight(msgText.getBoundsInLocal().getHeight()+10);
		StackPane msgStack = new StackPane();
		msgStack.getChildren().addAll(msgRect, msgText);
		Group msgGroup = new Group();
		msgGroup.getChildren().add(msgStack);
		msgGroup.setTranslateY(rootPane.getHeight()/2 - (msgGroup.getBoundsInLocal().getHeight()/2));
		msgGroup.setTranslateX(rootPane.getWidth());

		TranslateTransition tT = new TranslateTransition();
		tT.setNode(msgGroup);
		tT.setDuration(new Duration(2000));
		tT.setFromX(msgGroup.getTranslateX());
		tT.setToX((rootPane.getWidth()/2) - (msgGroup.getBoundsInLocal().getWidth()/2));
		tT.setCycleCount(1);

		FadeTransition fT = new FadeTransition();
		fT.setDuration(new Duration(1500));
		fT.setFromValue(1);
		fT.setToValue(0);
		fT.setCycleCount(1);
		fT.setNode(msgGroup);
		tT.setOnFinished(event -> {
			fT.play();
		});
		tT.play();
		fT.setOnFinished(fTFinished -> {
			rootPane.getChildren().remove(msgGroup);
		});
		rootPane.getChildren().add(msgGroup);
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

    public boolean getSound() {
        return sound;
    }



}
