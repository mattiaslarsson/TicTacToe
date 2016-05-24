package gamelogic;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias on 2016-05-24.
 */
public class GameArray {
    private int[][] gameGrid;
    private IntPair[] checkPattern = {
            new IntPair(0,-1),
            new IntPair(1,-1),
            new IntPair(0,1),
            new IntPair(1,1),
            new IntPair(1,0),
            new IntPair(-1,1),
            new IntPair(-1,0),
            new IntPair(-1,-1)
    };




    public GameArray(int gridSize) {
        gameGrid = new int[gridSize][gridSize];
    }

    public int[][] getGameGrid() {
        return gameGrid;
    }

    public void addMarker(int player, int x, int y) {
        gameGrid[x][y] = player;
        System.out.println("-----------------------------");
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        if(checkWinner(x, y, player)) {
            System.out.println("VINN");
        };
    }

    private boolean checkWinner(int x, int y, int player) {
        int iterator = 0;
        List<IntPair> matches = new ArrayList<>();
        matches.add(new IntPair(x, y));
        int matchesCounter = 0;
        while (matches.size() > 0) {
            x = matches.get(matches.size()-1).getX();
            y = matches.get(matches.size()-1).getY();
            matches.remove(matches.size()-1);
            for (int i = 0; i < checkPattern.length; i++) {
                System.out.println("Börjar kontroll nr: "+i);
                int currX = x + checkPattern[i].getX();
                int currY = y + checkPattern[i].getY();
                if (currX < 0 || currY < 0 || currX > gameGrid[0].length-1 || currY > gameGrid[0].length-1) {continue;}
                
                if (gameGrid[currX][currY] == player) {

                    int sameDirX = currX + checkPattern[i].getX();
                    int sameDirY = currY + checkPattern[i].getY();
                    if (sameDirX < 0 || sameDirX > gameGrid[0].length-1 || sameDirY < 0 || sameDirY > gameGrid[0].length-1) {
                        continue;
                    }
                    if (gameGrid[sameDirX][sameDirY] == player) {
                        return true;
                    }
                    
                    int oppositeX = i > 3 ? x + checkPattern[i - 4].getX() : x + checkPattern[i+4].getX();
                    int oppositeY = i > 3 ? y + checkPattern[i - 4].getY() : y + checkPattern[i+4].getY();
                    if (oppositeX < 0 || oppositeX > gameGrid[0].length-1 || oppositeY < 0 || oppositeY > gameGrid[0].length-1) {
                        continue;
                    }
                    if (gameGrid[oppositeX][oppositeY] == player) {
                            return true;
                    } else if (iterator < 1) {
                        matches.add(new IntPair(currX, currY));
                    }
                }
            }
            iterator++;
        }
        return false;
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