import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by Mattias Larsson on 2016-05-19.
 */

public class PlayerMarker {

    public PlayerMarker() {}

    /**
     * Places a marker in the grid
     * @param player integer - 1 if player 1, 2 if player 2
     *
     * @return Red Circle if player 1, blue Circle if player 2
     */
    public Circle placeMarker(int player) {
        Circle marker = new Circle();
        marker.setFill(player == 1 ? Color.RED : Color.BLUE);
        return marker;
    }
}
