package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import logic.Controller;

/**
 * Runs if it's the first time application runs to create own player.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-29.
 */
public class FirstRunPane extends BorderPane {

	String firstName, surName;

	public FirstRunPane(Controller controller) {
		VBox nameBox = new VBox();

		Label lblFirstName = new Label("Name");
		lblFirstName.setAlignment(Pos.BASELINE_LEFT);
		Label lblSurName = new Label("Surname");
		lblSurName.setAlignment(Pos.BASELINE_LEFT);

		TextField fieldName = new TextField();
		fieldName.setAlignment(Pos.BASELINE_LEFT);
		TextField fieldSurName = new TextField();
		fieldSurName.setAlignment(Pos.BASELINE_LEFT);
		fieldName.setPrefColumnCount(30);
		fieldSurName.setPrefColumnCount(30);

		Button btnPost = new Button("Save");
		btnPost.setAlignment(Pos.BASELINE_RIGHT);
		btnPost.setOnAction((e) ->{
			controller.setOwnPlayer(fieldName.getText(),fieldSurName.getText());
		});

		nameBox.getChildren().addAll(lblFirstName,fieldName,lblSurName,fieldSurName,btnPost);
		nameBox.setAlignment(Pos.CENTER);
		this.setCenter(nameBox);
	}
}
