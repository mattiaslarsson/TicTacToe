package gui;

import gamelogic.GameArray;
import gamelogic.GameBoard;
import gamelogic.PlayerMarker;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import logic.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Game board panel.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-27.
 */
public class GameBoardPanel extends BorderPane {

	private Controller controller;
	private AppWindow viewController;

	private GameBoard gameBoard;

	private GameArray gameArray;

	private int numMyMarkers;
	private int numOppMarkers;
	private long timeStart;
	private long timeEnd;

	private boolean player1Turn = false;
	private List<Circle> playerMarkers = new ArrayList<>();

	public GameBoardPanel (Controller controller, AppWindow viewController) {
		this.controller = controller;
		this.viewController = viewController;
	}

	/**
	 * Starts a game.
	 *
	 * @param startPlayer boolean
	 */
	public void startGame(boolean startPlayer) {
		player1Turn = startPlayer;
		numMyMarkers = 0;
		numOppMarkers = 0;
		initGameBoard();
	}

	/**
	 * Initiates the Gameboard
	 */
	private void initGameBoard() {
		gameBoard = new GameBoard(viewController.getPanelWidth(), viewController.getPanelWidth(), 3);
		gameArray = new GameArray(gameBoard.getRows());
		gameArray.setGrowable(viewController.isGrowable());
		gameArray.setDrawable(viewController.isDrawable());
		this.setCenter(gameBoard);
		gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
	}

	/**
	 * Makes a move
	 *
	 * @param col int
	 * @param row int
	 */
	public void makeMove(int col, int row) {
		if (!checkDoubles(col, row)) {
			drawMarker(col, row);
			if (player1Turn) {
				controller.makeMove(col, row);
			}
			player1Turn = !player1Turn;
			if (isFull() && !viewController.isDrawable()) {
				playerMarkers.forEach(marker -> {
					marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
				});
				gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
			}
		}
	}

	/**
	 * Checks if gameboard is full and expands it if it's the desired behaviour.
	 *
	 * @return boolean
	 */
	private boolean isFull() {
		if (gameArray.isGameOver()) {
			winner(0,0,numMyMarkers);
		}
		if (playerMarkers.size() == (gameBoard.getRows() * gameBoard.getRows())) {
			// Increase the gameboard's size
			gameBoard.incGameBoard();
			gameArray.growBoard(GridPane.getColumnIndex(playerMarkers.get(playerMarkers.size() - 1)),
					GridPane.getRowIndex(playerMarkers.get(playerMarkers.size() - 1)));

			int[][] tempGrid = gameArray.getGameGrid();
			playerMarkers.clear();
			gameBoard = new GameBoard(viewController.getPanelWidth(), viewController.getPanelHeight(), tempGrid[0].length);
			this.setCenter(gameBoard);
			for (int x = 0; x < tempGrid.length; x++) {
				for (int y = 0; y < tempGrid[x].length; y++) {
					if (tempGrid[x][y] == 1) {
						Circle playerMarker = new PlayerMarker().placeMarker(1);
						gameBoard.add(playerMarker, x, y);
						playerMarkers.add(playerMarker);
					} else if (tempGrid[x][y] == 2) {
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

	/**
	 * Checks if there is a marker in the desired spot allready
	 *
	 * @param col int
	 * @param row int
	 * @return boolean
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
	 * Draws the marker and checks if there's a winner.
	 *
	 * @param col int
	 * @param row int
	 */
	private void drawMarker(int col, int row) {
		boolean winner;
		if (!checkDoubles(col, row)) {
			if (player1Turn) {
				Circle marker = new PlayerMarker().placeMarker(1);
				gameBoard.add(marker, col, row);
				gameBoard.addMarker(marker, col, row);
				playerMarkers.add(marker);
				numMyMarkers++;
				winner = gameArray.addMarker(1, col, row, viewController.getRowsToWin());
				if (winner) {
					timeEnd = System.currentTimeMillis() / 1000L;
					int points = (int) (10 * gameArray.getGridSize() + viewController.getRowsToWin()) / numMyMarkers;
					winner(points, 0, numMyMarkers);
					//controller.winning(points, 0, timeStart, timeEnd, gameArray.getGridSize(), numMyMarkers);
				}
			} else {
				Circle marker = new PlayerMarker().placeMarker(2);
				gameBoard.add(marker, col, row);
				gameBoard.addMarker(marker, col, row);
				playerMarkers.add(marker);
				numOppMarkers++;
				winner = gameArray.addMarker(2, col, row, viewController.getRowsToWin());
				if (winner) {
					timeEnd = System.currentTimeMillis() / 1000L;
					int points = (int) (10 * gameArray.getGridSize() + viewController.getRowsToWin()) / numOppMarkers;
					winner(0, points, numOppMarkers);
					//controller.winning(0, points, timeStart, timeEnd, gameArray.getGridSize(), numOppMarkers);
				}
			}
			playerMarkers.forEach(marker -> {
				marker.radiusProperty().bind(gameBoard.getCellSizeProperty().divide(2));
			});
		}
	}

	/**
	 * Winner method. Display fancy message and return to start screen.
	 *
	 * @param myPoints int
	 * @param oppPoints int
	 * @param numMarkers int
	 */
	private void winner(int myPoints, int oppPoints, int numMarkers) {
		System.out.println("myPoints: " + myPoints + ", oppPoints: " + oppPoints);
		//TODO DISPLAY WINNER IN FANCY STYLE
		if (myPoints > oppPoints) {
			System.out.println("You're the WINNER!");
		} else if (oppPoints > myPoints){
			System.out.println("Opponent won! Boooh!");
		} else {
			System.out.println("It's a draw!");
		}
		timeEnd = System.currentTimeMillis() / 1000L;
		controller.winning(myPoints, oppPoints, timeStart, timeEnd, gameArray.getGridSize(), numMarkers);
		viewController.initStart();
	}

	/**
	 * Mouseclick handler
	 *
	 * @return EventHandler<MouseEvent>
	 */
	private EventHandler<MouseEvent> addMouseListener() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getEventType() == MouseEvent.MOUSE_CLICKED && player1Turn) {
					int clickCol = (int) Math.round(((e.getX() -
							(e.getX() % gameBoard.getCellSize())) /
							gameBoard.getCellSize()));
					int clickRow = (int) Math.round(((e.getY() -
							(e.getY() % gameBoard.getCellSize())) /
							gameBoard.getCellSize()));

					makeMove(clickCol, clickRow);
				}
			}
		};
	}

}
