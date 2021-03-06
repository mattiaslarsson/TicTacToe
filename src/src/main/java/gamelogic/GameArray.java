package gamelogic;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias on 2016-05-24.
 *
 * Logic for checking for a winner and resizing the board
 */

public class GameArray {
    private int[][] gameGrid;

    /*
      Array with coordinates relative to each cell
      These are used for checking for a marker of the same kind
      as the one last played
    */
    private IntPair[] checkPattern = {
            new IntPair(0,-1),
            new IntPair(1,-1),
            new IntPair(1,0),
            new IntPair(1,1),
            new IntPair(0,1),
            new IntPair(-1,1),
            new IntPair(-1,0),
            new IntPair(-1,-1)
    };

    private int gridSize;
    private boolean growable;
    private boolean drawable;
    private boolean gameOver = false;
    private boolean draw = false;
    private List<Point2D> winningRow;

    public GameArray(int gS) {
        gridSize = gS;
        gameGrid = new int[gridSize][gridSize];
        winningRow = new ArrayList<>();
    }

    public void setGrowable(boolean growable) { this.growable = growable; }
    public void setDrawable(boolean drawable) { this.drawable = drawable; }
    public boolean isGameOver() { return gameOver; }

    /**
     * Returns an array representing the gameboard
     * @return int[][]
     */
    public int[][] getGameGrid() {
        return gameGrid;
    }

    /**
     * Adds a marker to the gameGrid
     *
     * @param player The player who made the move
     * @param x x-coordinate of the move
     * @param y y-coordinate of the move
     */
    public boolean addMarker(int player, int x, int y, int reqToWin) {
        gameGrid[x][y] = player;
        if(checkWinner(x, y, player, reqToWin)) {
            return true;
        };
        return false;
    }

    /**
     * Checks if the player has required markers in a row
     *
     * @param x x-coordinate to start from
     * @param y y-coordinate to start from
     * @param player which player are we checking
     * @return boolean if there is a winner
     */
    private boolean checkWinner(int x, int y, int player, int reqToWin) {
        winningRow.clear();
        // Add the last marker played to a list
        winningRow.add(new Point2D(x, y));
        int numInRow = 0;
        // Loop through the array with relative coordinates
        outerLoop:
        for (int i = 0; i < checkPattern.length; i++) {
            int currX = x + checkPattern[i].getX();
            int currY = y + checkPattern[i].getY();
            // Is the coordinate out of bounds?
            if (currX < 0 || currY < 0 || currX > gameGrid[0].length-1 || currY > gameGrid[0].length-1 || gameGrid[currX][currY] != player) {
                continue outerLoop;

            }
            // Is the marker checked of the same kind as the one last played
            if (gameGrid[currX][currY] == player) {
                numInRow++;
                winningRow.add(new Point2D(currX, currY));
                if(numInRow >= reqToWin-1) {
                    gameOver = true;
                    return true;
                }
                // Check the next marker in the same direction
                for (int j = 0; j<reqToWin-1; j++) {
                    currX += checkPattern[i].getX();
                    currY += checkPattern[i].getY();
                    // If the coordinate is out of bounds, check the opposite direction
                    if (currX < 0 || currY < 0 || currX > gameGrid[0].length-1 || currY > gameGrid[0].length-1 || gameGrid[currX][currY] != player) {
                        int dirX = i > 3 ? checkPattern[i-4].getX() : checkPattern[i+4].getX();
                        int dirY = i > 3 ? checkPattern[i-4].getY() : checkPattern[i+4].getY();
                        int oppositeX = x+dirX;
                        int oppositeY = y+dirY;
                        // If the opposite coord is out of bounds, we have no winner in that direction
                        if (oppositeX < 0 || oppositeY < 0 || oppositeX > gameGrid[0].length-1 || oppositeY > gameGrid[0].length-1 || gameGrid[oppositeX][oppositeY] != player) {
                            numInRow = 0;
                            winningRow.clear();
                            // Continue on next relative coord
                            continue outerLoop;
                        }
                        for (int k = 0; k < reqToWin-1; k++) {
                            if (gameGrid[oppositeX][oppositeY] == player) {
                                // We have a match in the opposite direction
                                numInRow++;
                                winningRow.add(new Point2D(oppositeX, oppositeY));
                                if(numInRow >= reqToWin-1) {
                                    gameOver = true;
                                    return true;
                                }
                                // Continue the check in the current direction
                                oppositeX += dirX;
                                oppositeY += dirY;

                                // If the coord is out of bounds we have no winner, check the next relative coord
                                if (oppositeX < 0 || oppositeY < 0 || oppositeX > gameGrid[0].length-1 || oppositeY > gameGrid[0].length-1 || gameGrid[oppositeX][oppositeY] != player) {
                                    numInRow = 0;
                                    winningRow.clear();
                                    continue outerLoop;
                                }
                            }
                        }
                    } else if(gameGrid[currX][currY] == player) {
                        // The coord is in the grid and a match
                        numInRow++;
                        winningRow.add(new Point2D(currX, currY));
                        if(numInRow >= reqToWin-1) {
                            gameOver = true;
                            return true;
                        }
                    }
                }
                numInRow = 0;
                winningRow.clear();
            }
        }
        return false;
    }

    /**
     * Increase the array of markers played
     *
     * @param x x-coordinate of the last marker played
     * @param y y-coordinate of the last marker played
     */
    public void growBoard(int x, int y) {
        if(growable) {
            boolean right = false, down = false;
            // The direction of the grow depends on where the last marker is played
            if (x >= (gridSize - 1) / 2) {
                right = true;
            }
            if (y >= (gridSize - 1) / 2) {
                down = true;
            }
            gridSize += 2;
            // Move all the markers so that their positions remain the same
            int[][] tempGrid = new int[gridSize][gridSize];
            for (int oldX = 0; oldX < gameGrid.length; oldX++) {
                for (int oldY = 0; oldY < gameGrid[oldX].length; oldY++) {
                    if (!right && !down) {
                        tempGrid[oldX + 2][oldY + 2] = gameGrid[oldX][oldY];
                    } else if (!right && down) {
                        tempGrid[oldX + 2][oldY] = gameGrid[oldX][oldY];
                    } else if (right && !down) {
                        tempGrid[oldX][oldY + 2] = gameGrid[oldX][oldY];
                    } else {
                        tempGrid[oldX][oldY] = gameGrid[oldX][oldY];
                    }
                }
            }
            gameGrid = tempGrid;
        } else {
            draw = true;
        }

    }

    public int getGridSize() {
        return gridSize;
    }

    public List<Point2D> getWinningRow() { return winningRow; }

    public boolean isDraw() { return draw; }
}

/**
 * Helper class
 */
class IntPair {
    private int x, y;

    IntPair (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}