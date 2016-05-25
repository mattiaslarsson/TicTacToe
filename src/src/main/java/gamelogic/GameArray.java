package gamelogic;

/**
 * Created by Mattias on 2016-05-24.
 *
 * Logic for checking for a winner and resizing the board
 */

public class GameArray {
    private int[][] gameGrid;

    /* Array with coordinates relative to each cell
     These are used for checking for a marker with the same color
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

    public GameArray(int gS) {
        gridSize = gS;
        gameGrid = new int[gridSize][gridSize];
    }

    /**
     * Returns an array representing the gameboard
     * @return
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
    public void addMarker(int player, int x, int y) {
        gameGrid[x][y] = player;
        if(checkWinner(x, y, player)) {
            System.out.println("VINN");
        };
    }

    /**
     * Checks if the player has 3 markers in a row
     *
     * @param x x-coordinate to start from
     * @param y y-coordinate to start from
     * @param player which player are we checking
     * @return boolean if there is a winner
     */
    private boolean checkWinner(int x, int y, int player) {
        // Loop through the array with relative coordinates
        for (int i = 0; i < checkPattern.length; i++) {
            int currX = x + checkPattern[i].getX();
            int currY = y + checkPattern[i].getY();
            // If the new coordinate is out of bounds, continue with the next relative coordinate
            if (currX < 0 || currY < 0 || currX > gameGrid[0].length-1 || currY > gameGrid[0].length-1) {
                continue;
            }
            // If the new coordinate contains a marker played by the same player
            if (gameGrid[currX][currY] == player) {
                // Continue the check one step further away from the original marker
                int sameDirX = currX + checkPattern[i].getX();
                int sameDirY = currY + checkPattern[i].getY();
                // If the new coordinate is out of bounds, check the opposite direction
                if (sameDirX < 0 || sameDirX > gameGrid[0].length-1 || sameDirY < 0 || sameDirY > gameGrid[0].length-1) {
                    int oppositeX = i > 3 ? x + checkPattern[i-4].getX() : x + checkPattern[i+4].getX();
                    int oppositeY = i > 3 ? y + checkPattern[i-4].getY() : y + checkPattern[i+4].getY();
                    // If the opposite coordinate is out of bounds, continue with the next relative coordinate
                    if (oppositeX < 0 || oppositeX > gameGrid[0].length-1 || oppositeY < 0 || oppositeY > gameGrid[0].length-1) {
                        continue;
                    }
                    // If the opposite marker is also of the same color we have a winner
                    // else continue with the next relative coordinate
                    if (gameGrid[oppositeX][oppositeY] == player) {
                        return true;
                    }
                    continue;
                }
                // If the marker two steps away from the original is of the same color
                // we have a winner
                if (gameGrid[sameDirX][sameDirY] == player) {
                    return true;
                }
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
        boolean right = false, down = false;
        // The direction of the grow depends on where the last marker is played
        if (x >= (gridSize-1)/2) {
            right = true;
        }
        if (y >= (gridSize-1)/2) {
            down = true;
        }
        gridSize += 2;
        // Move all the markers so that their positions remain the same
        int[][] tempGrid = new int[gridSize][gridSize];
        for (int oldX = 0; oldX < gameGrid.length; oldX++) {
            for (int oldY = 0; oldY < gameGrid[oldX].length; oldY++) {
                if(!right && !down) {
                    tempGrid[oldX+2][oldY+2] = gameGrid[oldX][oldY];
                } else if (!right && down) {
                    tempGrid[oldX+2][oldY] = gameGrid[oldX][oldY];
                } else if (right && !down) {
                    tempGrid[oldX][oldY+2] = gameGrid[oldX][oldY];
                } else {
                    tempGrid[oldX][oldY] = gameGrid[oldX][oldY];
                }
            }
        }
        gameGrid = tempGrid;
    }
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