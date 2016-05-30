package gamelogic;

import javafx.scene.image.Image;

import java.io.*;

/**
 * Created by Mattias Larsson on 2016-05-19.
 */

public class PlayerMarker {


    public PlayerMarker(){}

    /**
     * Places a marker in the grid
     * @param player integer - 1 if player 1, 2 if player 2
     *
     * @return Red Circle if player 1, blue Circle if player 2
     */
    public Image placeMarker(int player) throws IOException{
        if (player == 1) {
            Image cross=new Image(getClass().getResourceAsStream("../res/cross.png"));
            return cross;
        }
        else {
            Image circle=new Image(getClass().getResourceAsStream("../res/circle.png"));
            return circle;
        }
    }
}
