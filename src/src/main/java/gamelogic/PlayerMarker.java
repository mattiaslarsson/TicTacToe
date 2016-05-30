package gamelogic;

import javafx.scene.image.Image;

import java.io.IOException;

/**
 * Created by Mattias Larsson on 2016-05-19.
 */

public class PlayerMarker {


	Image cross, circle;

	public PlayerMarker() {
		cross = new Image(getClass().getResourceAsStream("/xL.png"));
		circle = new Image(getClass().getResourceAsStream("/oL.png"));
	}

	/**
	 * Places a marker in the grid
	 *
	 * @param player integer - 1 if player 1, 2 if player 2
	 * @return Red Circle if player 1, blue Circle if player 2
	 */
	public Image placeMarker(int player) throws IOException {
		if (player == 1) {
			return cross;
		} else {
			return circle;
		}
	}
}
