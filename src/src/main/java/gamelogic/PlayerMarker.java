package gamelogic;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
//        Image crossImage = new Image("res/cross.png");
//        Image circleImage = new Image("res/circle.png");
        if (player == 1) {
            Image cross=new Image(getClass().getResourceAsStream("/res/cross.png"));
            //BufferedImage crossTemp = ImageIO.read(new File("/res/cross.png"));
            //Image cross = SwingFXUtils.toFXImage(crossTemp,null);
            return cross;
        }
        else {
            System.out.println("returnerar circle");
            Image circle=new Image(getClass().getResourceAsStream("/res/circle.png"));
            //BufferedImage circleTemp = ImageIO.read(new File("/res/circle.png"));
            //Image circle = SwingFXUtils.toFXImage(circleTemp, null);
            return circle;
        }
    }
}
