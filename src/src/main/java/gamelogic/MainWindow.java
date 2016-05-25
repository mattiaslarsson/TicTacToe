package gamelogic;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias Larsson on 2016-05-17.
 */


public class MainWindow {

    private List<Circle> playerMarkers = new ArrayList<>();
    private Stage stage;
    private double screenWidth, screenHeight;
    private BooleanProperty player1Turn = new SimpleBooleanProperty(true);
    private GameBoard gameBoard;
    private Controller controller;
    private GameArray gameArray;
    private ScrollPane gamePane;
    private Button chatButton;
    private BorderPane rootPane;
    private HBox startBox;
    private StackPane connectPane;
    private HBox connectBox;

    public MainWindow(Stage stage, Controller controller) {
        this.controller = controller;
        screenWidth = 600;
        screenHeight = 600;
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
        this.stage.setScene(initStartScreen());
//        this.stage.setScene(initGameBoard());
        this.stage.show();
    }

    private Scene initStartScreen() {
        rootPane = new BorderPane();
        Scene startScene = new Scene(rootPane, screenWidth, screenHeight);
        TextField ip = new TextField();
        ip.setPromptText("Enter IP");
        Button connectButton = new Button("Connect");
        connectBox = new HBox();
        connectBox.getChildren().addAll(ip, connectButton);
        connectBox.setAlignment(Pos.CENTER);
        connectPane = new StackPane();
        connectPane.getChildren().add(connectBox);
        connectPane.setPrefSize(screenWidth, screenHeight - (screenHeight*0.1));
        connectPane.setStyle("-fx-background-color: #ff0000");
        rootPane.setCenter(connectPane);
        rootPane.setBottom(chatBox());
        connectButton.setOnAction(connect -> {
            controller.connect(ip.getText());
        });

        startBox = new HBox();
        Button startButton = new Button("START GAME!");
        startBox.setAlignment(Pos.CENTER);
        startButton.setOnAction(start -> {
            player1Turn.setValue(controller.startGame());

            System.out.println(player1Turn.getValue());

        });
        startBox.getChildren().add(startButton);
        connectPane.getChildren().add(startBox);
        startBox.setVisible(false);
        return startScene;
    }

    public HBox chatBox() {
        TextField chatMsg = new TextField();
        chatMsg.setPromptText("chat");
        chatMsg.setMinWidth(screenWidth - (screenWidth*0.2));
        chatButton = new Button("Send msg");
        chatButton.setDisable(true);
        chatButton.setMinWidth(screenWidth - (screenWidth*0.8));
        HBox chatBox = new HBox();
        chatBox.getChildren().addAll(chatMsg, chatButton);
        chatButton.setOnAction(chat -> {
            controller.chatMessage(chatMsg.getText());
            chatMsg.setText("");
        });

        return chatBox;
    }
    public void connected(boolean conn) {
        if (conn) {
            Platform.runLater(() -> {
                chatButton.setDisable(false);
                connectBox.setVisible(false);
                startBox.setVisible(true);
            });
        } else {
            Platform.runLater(() -> {
                chatButton.setDisable(true);
                connectBox.setVisible(true);
                startBox.setVisible(false);
            });

        }

    }

    public void startGame(boolean start) {
        Platform.runLater(() -> {
            player1Turn.setValue(start);
            stage.setScene(initGameBoard());
        });


    }

    /**
     * Initiates the Gameboard
     *
     * @return A Scene with a GridPane
     */
    private Scene initGameBoard() {
        BorderPane gamePane = new BorderPane();
        gameBoard = new GameBoard(screenWidth, screenHeight, 3);
        gameArray = new GameArray(gameBoard.getRows());
        gamePane.setCenter(gameBoard);
        gamePane.setBottom(chatBox());
        gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
        Scene gameScene = new Scene(gamePane, screenWidth, screenHeight);
        
        return gameScene;
    }

    private void viewController(int col, int row) {
        if(!checkDoubles(col, row)) {
            controller.makeMove(col, row);
            drawMarker(col, row);
            player1Turn.setValue(!player1Turn.getValue());
        }
        if(isFull()) {
            playerMarkers.forEach(marker -> {
                marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
            });
            gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
        }
    }

    private boolean isFull() {
        if(playerMarkers.size()==(gameBoard.getRows()*gameBoard.getRows())) {
                // Increase the gameboard's size
                gameBoard.incGameBoard();
                gameArray.growBoard(GridPane.getColumnIndex(playerMarkers.get(playerMarkers.size()-1)),
                        GridPane.getRowIndex(playerMarkers.get(playerMarkers.size()-1)));

                int[][] tempGrid = gameArray.getGameGrid();
                playerMarkers.clear();
                gameBoard = new GameBoard(screenWidth, screenHeight, tempGrid[0].length);
                gamePane.setContent(gameBoard);
                for (int x = 0; x<tempGrid.length; x++) {
                    for(int y = 0; y<tempGrid[x].length; y++) {
                        if (tempGrid[x][y] == 1) {
                            Circle playerMarker = new PlayerMarker().placeMarker(1);
                            gameBoard.add(playerMarker, x, y);
                            playerMarkers.add(playerMarker);
                        } else if(tempGrid[x][y] == 2) {
                            Circle playerMarker = new PlayerMarker().placeMarker(2);
                            gameBoard.add(playerMarker, x, y);
                            playerMarkers.add(playerMarker);
                        }
                    }
                }

            return true;
        }
        return false;
    }

    private boolean checkDoubles(int col, int row) {
        for (Circle circle : playerMarkers) {
            if (GridPane.getColumnIndex(circle) - col == 0 && GridPane.getRowIndex(circle) - row == 0) {
                return true;
            }
        }
        return false;
    }

    public void makeMove(int x, int y) {
        viewController(x, y);
    }

    private void drawMarker(int col, int row) {
        if (!checkDoubles(col, row)) {
            if (player1Turn.getValue()) {
                Circle marker = new PlayerMarker().placeMarker(1);
                gameBoard.add(marker, col, row);
                gameBoard.addMarker(marker, col, row);
                playerMarkers.add(marker);
                gameArray.addMarker(1, col, row);
            } else {
                Circle marker = new PlayerMarker().placeMarker(2);
                gameBoard.add(marker, col, row);
                gameBoard.addMarker(marker, col, row);
                playerMarkers.add(marker);
                gameArray.addMarker(2, col, row);
            }
            playerMarkers.forEach(marker -> {
                marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
            });
        }
    }


    private EventHandler<MouseEvent> addMouseListener() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getEventType() == MouseEvent.MOUSE_CLICKED && player1Turn.get()) {
                    int clickCol = (int) Math.round(((e.getX() -
                            (e.getX() % gameBoard.getCellSize())) /
                            gameBoard.getCellSize()));
                    int clickRow = (int) Math.round(((e.getY() -
                            (e.getY() % gameBoard.getCellSize())) /
                            gameBoard.getCellSize()));
                    
                    viewController(clickCol, clickRow);
                }
            }
        };
    }

}

