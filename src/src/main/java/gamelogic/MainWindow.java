package gamelogic;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.Controller;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias Larsson on 2016-05-17.
 */


public class MainWindow {

    private List<Circle> playerMarkers = new ArrayList<>();
    private List<Pair<Circle, Point2D>> circleList = new ArrayList<>();
    private Stage stage;
    private double screenWidth, screenHeight;
    private BooleanProperty player1Turn = new SimpleBooleanProperty(true);
    private GameBoard gameBoard;
    private int numInRow1 = 0, numInRow2 = 0, numInCol1 = 0, numInCol2 = 0;
    private int numInDiagF1 = 0, numInDiagF2 = 0, numInDiagB1 = 0, numInDiagB2 = 0;


    public MainWindow(Stage stage, Controller controller) {
        screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.stage = stage;
        this.stage.setScene(initGameBoard());
        this.stage.setMaximized(true);
        this.stage.show();
    }


    /**
     * Initiates the Gameboard and sets up the eventlistener
     *
     * @return A Scene with a GridPane
     */
    private Scene initGameBoard() {
        gameBoard = new GameBoard(screenWidth, screenHeight);

        ScrollPane gamePane = new ScrollPane();
        gamePane.setContent(gameBoard);

        Scene gameScene = new Scene(gamePane, screenWidth, screenHeight);

        gameBoard.setOnMouseClicked(mouseClick -> {
            // Find out which column and row the user clicked
            int clickCol = (int)Math.round(((mouseClick.getX() -
                    (mouseClick.getX() % gameBoard.getCellSize())) /
                    gameBoard.getCellSize()));
            int clickRow = (int)Math.round(((mouseClick.getY() -
                    (mouseClick.getY() % gameBoard.getCellSize())) /
                    gameBoard.getCellSize()));

            // Check wether the click was outside the grid
            if (clickCol > gameBoard.getRows() -1){clickCol = gameBoard.getRows()-1;}
            if (clickRow > gameBoard.getRows() -1){clickRow = gameBoard.getRows()-1;}

            // If the click was made in an empty cell
            // place a marker in that cell with a color
            // that corresponds to which player is curretly playing
            // and add that marker to a list
            if (!checkDoubles(clickCol, clickRow)) {
                if (player1Turn.getValue()) {
                    Circle marker = new PlayerMarker().placeMarker(1);
                    gameBoard.add(marker, clickCol, clickRow);
                    playerMarkers.add(marker);
                } else {
                    Circle marker = new PlayerMarker().placeMarker(2);
                    gameBoard.add(marker, clickCol, clickRow);
                    playerMarkers.add(marker);

                }
            }

            // Check if the grid is full of markers
            if(playerMarkers.size()==(gameBoard.getRows()*gameBoard.getRows())) {
                // Increase the gameboard's size
                gameBoard.incGameBoard();
                // Move the markers
                playerMarkers.forEach(marker -> {
                    int col = GridPane.getColumnIndex(marker);
                    int row = GridPane.getRowIndex(marker);
                    gameBoard.getChildren().remove(marker);
                    gameBoard.add(marker, col+1, row+1);
                });
            }
            // Bind the marker's radius so that they always fits in the cells
            playerMarkers.forEach(marker -> {
                marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
            });
            checkWinner();
            // Change the player to play
            player1Turn.setValue(!player1Turn.getValue());
        });
        return gameScene;
    }

    /**
     * Check if the marker is placed in an empty cell
     *
     * @param col The column where the player tried to place a marker
     * @param row The row where the player tried to place a marker
     *
     * @return True if there is a double, False otherwise
     */
    private boolean checkDoubles(int col, int row) {
        for (Circle circle : playerMarkers) {
            if (GridPane.getColumnIndex(circle) - col == 0 && GridPane.getRowIndex(circle) - row == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if there is a player with 3 markers in a row
     */
    private void checkWinner() {
        Circle lastMarker = playerMarkers.get(playerMarkers.size()-1);
        int column = GridPane.getColumnIndex(lastMarker);
        int row = GridPane.getRowIndex(lastMarker);
        Paint color = lastMarker.getFill();
        playerMarkers.forEach(marker -> {
            if (marker != lastMarker) {
                circleList.add(new Pair(marker, new Point2D(
                        (int) GridPane.getColumnIndex(marker),
                        (int) GridPane.getRowIndex(marker))));
            }
        });

        // Check row


        // Check column


        // Check diag (forward slash)


        // Check diag (backward slash)

    }
}
