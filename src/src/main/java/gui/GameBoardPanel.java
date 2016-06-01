package gui;

import gamelogic.GameArray;
import gamelogic.GameBoard;
import gamelogic.PlayerMarker;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import logic.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Game board panel.
 * <p>
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

	private URL soundRes;
	private Media sound;
	private Media myMove;
	private Media yourMove;
	private MediaPlayer mediaPlayer;
	private List<Point2D> winningRow;


	private boolean player1Turn = false;
	private List<ImageView> playerMarkers = new ArrayList<>();
	private boolean draw = false;
	private boolean winner = false;

	public GameBoardPanel(Controller controller, AppWindow viewController) {
		this.controller = controller;
		this.viewController = viewController;
		myMove = new Media(getClass().getResource("/mySound.mp3").toString());
		yourMove = new Media(getClass().getResource("/yourSound.mp3").toString());
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
		if (startPlayer) {
			viewController.messageViewer("You start!");
		} else {
			viewController.messageViewer("Opponent start!");
		}
	}

	/**
	 * Initiates the Gameboard
	 */
	private void initGameBoard() {
		gameBoard = new GameBoard(viewController.getPanelWidth(), viewController.getPanelHeight(), 3);
		gameArray = new GameArray(gameBoard.getRows());
		gameArray.setGrowable(viewController.isGrowable());
		gameArray.setDrawable(viewController.isDrawable());
		this.setCenter(gameBoard);
		gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
		gameBoard.setStyle("-fx-background-image: url(\"/textured_paper.png\");-fx-background-size: 600, 600;-fx-background-repeat: no-repeat;");
		if(player1Turn) { viewController.messageViewer("You start"); }
		else { viewController.messageViewer("Opponent start"); }

	}

	/**
	 * Makes a move
	 *
	 * @param col int
	 * @param row int
	 */
	public void makeMove(int col, int row) throws IOException {
		if (!checkDoubles(col, row)) {
			drawMarker(col, row);
			if (player1Turn) {
				controller.makeMove(col, row);
			}
			player1Turn = !player1Turn;
			if(player1Turn) { viewController.messageViewer("Your turn"); }
			if (isFull() && !viewController.isDrawable()) {
				playerMarkers.forEach(marker -> {
					marker.fitHeightProperty().bind(gameBoard.getCellSizeProperty().divide(2));
					marker.fitWidthProperty().bind(gameBoard.getCellSizeProperty().divide(2));
				});
				gameBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, addMouseListener());
			} else if (isFull() && gameArray.isGameOver()) {
				winner(0,0,numMyMarkers);
			}
		}
	}

	/**
	 * Checks if gameboard is full and expands it if it's the desired behaviour.
	 *
	 * @return boolean
	 */
	private boolean isFull() throws IOException {
		if (gameArray.isGameOver()) {
			winner(0, 0, numMyMarkers);
		}
		if (playerMarkers.size() == (gameBoard.getRows() * gameBoard.getRows())) {
			// Increase the gameboard's size
			gameBoard.incGameBoard();
			gameArray.growBoard(GridPane.getColumnIndex(playerMarkers.get(playerMarkers.size() - 1)),
					GridPane.getRowIndex(playerMarkers.get(playerMarkers.size() - 1)));

			int[][] tempGrid = gameArray.getGameGrid();
			playerMarkers.clear();
			gameBoard = new GameBoard(viewController.getPanelWidth(), viewController.getPanelHeight(), tempGrid[0].length);
			gameBoard.setStyle("-fx-background-image: url(\"/textured_paper.png\");-fx-background-size: 600, 600;-fx-background-repeat: no-repeat;");
			this.setCenter(gameBoard);
			for (int x = 0; x < tempGrid.length; x++) {
				for (int y = 0; y < tempGrid[x].length; y++) {
					if (tempGrid[x][y] == 1) {
						ImageView playerMarker = new ImageView(new PlayerMarker().placeMarker(1));
						gameBoard.add(playerMarker, x, y);
						GridPane.setHalignment(playerMarker, HPos.CENTER);
						playerMarkers.add(playerMarker);
					} else if (tempGrid[x][y] == 2) {
						ImageView playerMarker = new ImageView(new PlayerMarker().placeMarker(2));
						gameBoard.add(playerMarker, x, y);
						GridPane.setHalignment(playerMarker, HPos.CENTER);
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
		for (ImageView image : playerMarkers) {
			if (GridPane.getColumnIndex(image) - col == 0 && GridPane.getRowIndex(image) - row == 0) {
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
	private void drawMarker(int col, int row) throws IOException {
		if (!checkDoubles(col, row)) {
			if (player1Turn) {
				if (viewController.getSound()) {
					mediaPlayer = new MediaPlayer(myMove);
					mediaPlayer.play();
				}
				ImageView marker = new ImageView(new PlayerMarker().placeMarker(1));
				gameBoard.add(marker, col, row);
				playerMarkers.add(marker);
				GridPane.setHalignment(marker, HPos.CENTER);
				numMyMarkers++;
				winner = gameArray.addMarker(1, col, row, viewController.getRowsToWin());
				if (winner) {
					timeEnd = System.currentTimeMillis() / 1000L;
					int points = (int) (10 * gameArray.getGridSize() + viewController.getRowsToWin()) / numMyMarkers;
					winner(points, 0, numMyMarkers);
				}
			} else {
				if (viewController.getSound()) {
					mediaPlayer = new MediaPlayer(yourMove);
					mediaPlayer.play();
				}
				ImageView marker = new ImageView(new PlayerMarker().placeMarker(2));
				gameBoard.add(marker, col, row);
				playerMarkers.add(marker);
				GridPane.setHalignment(marker, HPos.CENTER);
				numOppMarkers++;
				winner = gameArray.addMarker(2, col, row, viewController.getRowsToWin());
				if (winner) {
					timeEnd = System.currentTimeMillis() / 1000L;
					int points = (int) (10 * gameArray.getGridSize() + viewController.getRowsToWin()) / numOppMarkers;
					winner(0, points, numOppMarkers);
				}
			}
			playerMarkers.forEach(marker -> {
				marker.fitHeightProperty().bind(gameBoard.getCellSizeProperty().divide(2));
				marker.fitWidthProperty().bind(gameBoard.getCellSizeProperty().divide(2));
			});
		}
	}

	/**
	 * Winner method. Display fancy message and return to start screen.
	 *
	 * @param myPoints   int
	 * @param oppPoints  int
	 * @param numMarkers int
	 */
	private void winner(int myPoints, int oppPoints, int numMarkers) {
		if (myPoints > oppPoints) {
			playSound("/applause.mp3");
			viewController.messageViewer("You won!");
		} else if (oppPoints > myPoints) {
			playSound("/boo.mp3");
			viewController.messageViewer("You lost!");
		} else {
			playSound("/sigh.mp3");
			viewController.messageViewer("It's a draw!");
		}
		timeEnd = System.currentTimeMillis() / 1000L;
		controller.winning(myPoints, oppPoints, timeStart, timeEnd, gameArray.getGridSize(), numMarkers);
	}

	/**
	 * Plays game sound effects.
	 *
	 * @param url String
	 */
	private void playSound(String url) {
		winningRow = gameArray.getWinningRow();
		if (viewController.getSound()) {
			viewController.lowerMusic(true);
			soundRes = getClass().getResource(url);
			sound = new Media(soundRes.toString());
			mediaPlayer = new MediaPlayer(sound);
			mediaPlayer.play();
			mediaPlayer.setOnEndOfMedia(() -> {
				viewController.lowerMusic(false);
			});
		}
		winningRow.forEach(coord -> {
			playerMarkers.forEach(marker -> {
				if (GridPane.getColumnIndex(marker) == (int) coord.getX()
						&& GridPane.getRowIndex(marker) == (int) coord.getY()) {
					addScale(marker);
				}
			});
		});

	}

	/**
	 * Makes winner animation.
	 *
	 * @param node Node
	 */
	private void addScale(Node node) {
		ScaleTransition sT = new ScaleTransition();
		sT.setDuration(Duration.seconds(0.5));
		sT.setNode(node);
		sT.setFromX(1);
		sT.setToX(1.2);
		sT.setFromY(1);
		sT.setToY(1.2);
		sT.setCycleCount(15);
		sT.setAutoReverse(true);
		sT.play();
		sT.setOnFinished(event -> {
			viewController.initRestart();
		});
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

					try {
						makeMove(clickCol, clickRow);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
	}
}
