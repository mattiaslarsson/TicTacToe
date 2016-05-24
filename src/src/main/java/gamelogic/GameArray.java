package gamelogic;

/**
 * Created by Mattias on 2016-05-24.
 */
public class GameArray {
    private int[][] gameGrid;
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
        this.gridSize = gS;
        gameGrid = new int[gridSize][gridSize];
    }

    public int[][] getGameGrid() {
        return gameGrid;
    }

    public void addMarker(int player, int x, int y) {
        gameGrid[x][y] = player;
        if(checkWinner(x, y, player)) {
            System.out.println("VINN");
        };
    }

    private boolean checkWinner(int x, int y, int player) {
        for (int i = 0; i < checkPattern.length; i++) {
            int currX = x + checkPattern[i].getX();
            int currY = y + checkPattern[i].getY();
            if (currX < 0 || currY < 0 || currX > gameGrid[0].length-1 || currY > gameGrid[0].length-1) {
                continue;
            }
            if (gameGrid[currX][currY] == player) {
                System.out.println("träff i " + currX + ", " + currY);
                int sameDirX = currX + checkPattern[i].getX();
                int sameDirY = currY + checkPattern[i].getY();
                if (sameDirX < 0 || sameDirX > gameGrid[0].length-1 || sameDirY < 0 || sameDirY > gameGrid[0].length-1) {
                    int oppositeX = i > 3 ? x + checkPattern[i-4].getX() : x + checkPattern[i+4].getX();
                    int oppositeY = i > 3 ? y + checkPattern[i-4].getY() : y + checkPattern[i+4].getY();
                    if (oppositeX < 0 || oppositeX > gameGrid[0].length-1 || oppositeY < 0 || oppositeY > gameGrid[0].length-1) {
                        continue;
                    }
                    if (gameGrid[oppositeX][oppositeY] == player) {
                        System.out.println("OppDir --- x: " + x + " y: " + y + " currX: " + currX + " currY: " + currY + " oppX: " + oppositeX + " oppY: " + oppositeY);
                        return true;
                    }
                    continue;
                }
                if (gameGrid[sameDirX][sameDirY] == player) {
                    System.out.println("sameDir --- x: " + x + " y: " + y + " currX: " + currX + " currY: " + currY + " sameX: " + sameDirX + " sameY: " + sameDirY);
                    return true;
                }
            }
        }
        return false;
    }

    public void growBoard(int x, int y) {
        boolean right = false, down = false;
        if (x >= (gridSize-1)/2) {
            right = true;
        }
        if (y >= (gridSize-1)/2) {
            down = true;
        }
        gridSize += 2;
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