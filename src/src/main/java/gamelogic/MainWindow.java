package gamelogic;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
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
    private Button chatButton;
    private BorderPane rootPane;
    private VBox startBox;
    private StackPane connectPane;
    private HBox connectBox;
    private BorderPane gamePane;
    private int reqToWin;
    private RadioButton threeRadio, fourRadio, fiveRadio;
    private CheckBox growable, drawAllowed;



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

        startBox = new VBox();
        Button startButton = new Button("START GAME!");
        startBox.setAlignment(Pos.CENTER);
        startButton.setOnAction(start -> {
            player1Turn.setValue(controller.startGame());
            stage.setScene(initGameBoard());
            System.out.println(player1Turn.getValue());

        });

        Text playOptionsText = new Text("OPTIONS");
        threeRadio = new RadioButton("3-in-a-row");
        fourRadio = new RadioButton("4-in-a-row");
        fiveRadio = new RadioButton("5-in-a-row");
        ToggleGroup radioGroup = new ToggleGroup();
        radioGroup.getToggles().addAll(threeRadio, fourRadio, fiveRadio);

        growable = new CheckBox("Growable Grid");
        drawAllowed = new CheckBox("Allow Draw");

        threeRadio.setOnAction(threeAction -> {
            if(threeRadio.isSelected()) {
                reqToWin = 3;
                growable.setDisable(false);
                growable.setSelected(false);
                drawAllowed.setDisable(false);
                sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
            }
        });
        fourRadio.setOnAction(fourAction -> {
            if (fourRadio.isSelected()) {
                reqToWin = 4;
                growable.setSelected(true);
                growable.setDisable(true);
                drawAllowed.setSelected(false);
                drawAllowed.setDisable(true);
                sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
            }
        });
        fiveRadio.setOnAction(fiveAction -> {
            if (fiveRadio.isSelected()) {
                reqToWin = 5;
                growable.setSelected(true);
                growable.setDisable(true);
                drawAllowed.setSelected(false);
                drawAllowed.setDisable(true);
                sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
            }
        });
        drawAllowed.setOnAction(drawAction -> {
            growable.setDisable(drawAllowed.isSelected());
            sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
        });
        growable.setOnAction(growAction -> {
            drawAllowed.setDisable(growable.isSelected());
            sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
        });

        VBox optionsBox = new VBox();
        optionsBox.getChildren().addAll(playOptionsText, threeRadio, fourRadio, fiveRadio, growable,drawAllowed, startButton);

        startBox.getChildren().addAll(optionsBox, startButton);
        connectPane.getChildren().add(startBox);
        startBox.setVisible(false);
        return startScene;
    }

    public void sendOptions(int reqToWin, boolean growable, boolean draw) {
        controller.sendOptions(reqToWin, growable, draw);
    }

    public void getOptions(int reqToWin, boolean grow, boolean draw) {
        this.reqToWin = reqToWin;
        Platform.runLater(() -> {
            switch(reqToWin) {
                case 3:
                    threeRadio.setSelected(true);
                    growable.setDisable(false);
                    drawAllowed.setDisable(false);
                    break;
                case 4:
                    fourRadio.setSelected(true);
                    growable.setDisable(true);
                    drawAllowed.setDisable(true);
                    break;
                case 5:
                    fiveRadio.setSelected(true);
                    growable.setDisable(true);
                    drawAllowed.setDisable(true);
                    break;
            }
            drawAllowed.setDisable(grow);
            drawAllowed.setSelected(draw);
            growable.setSelected(grow);
            growable.setDisable(draw);
        });
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
                stage.setScene(initStartScreen());
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
        gamePane = new BorderPane();
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
            drawMarker(col, row);
            if (player1Turn.getValue()) {
                controller.makeMove(col, row);
            }
            player1Turn.setValue(!player1Turn.getValue());
            if(isFull()) {
                playerMarkers.forEach(marker -> {
                    marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
                });
                gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
            }
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
                gamePane.setCenter(gameBoard);
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
        Platform.runLater(() -> {
            viewController(x, y);
        });

    }

    private void drawMarker(int col, int row) {
        if (!checkDoubles(col, row)) {
            if (player1Turn.getValue()) {
                Circle marker = new PlayerMarker().placeMarker(1);
                gameBoard.add(marker, col, row);
                gameBoard.addMarker(marker, col, row);
                playerMarkers.add(marker);
                gameArray.addMarker(1, col, row, reqToWin);
            } else {
                Circle marker = new PlayerMarker().placeMarker(2);
                gameBoard.add(marker, col, row);
                gameBoard.addMarker(marker, col, row);
                playerMarkers.add(marker);
                gameArray.addMarker(2, col, row, reqToWin);
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

