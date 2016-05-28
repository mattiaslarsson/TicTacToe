package gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.Controller;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-27.
 */
public class StartPanel extends BorderPane {

	private Controller controller;
	private AppWindow viewController;

	private HBox connectBox;
	private StackPane connectPane;
	private VBox startBox;

	private RadioButton threeRadio;
	private RadioButton fourRadio;
	private RadioButton fiveRadio;

	private CheckBox growable;
	private CheckBox drawAllowed;

	public StartPanel(Controller controller, AppWindow viewController) {
		this.controller = controller;
		this.viewController = viewController;
		initStartScreen();
	}

	private void initStartScreen() {
		// Scene startScene = new Scene(this, screenWidth, screenHeight);
		TextField ip = new TextField();
		ip.setPromptText("Enter IP");
		Button connectButton = new Button("Connect");
		connectBox = new HBox();
		connectBox.getChildren().addAll(ip, connectButton);
		connectBox.setAlignment(Pos.CENTER);
		connectPane = new StackPane();
		connectPane.getChildren().add(connectBox);
		connectPane.setStyle("-fx-background-color: #ff0000");
		this.setCenter(connectPane);
		connectButton.setOnAction(connect -> {
			controller.connect(ip.getText());
		});

		startBox = new VBox();
		Button startButton = new Button("START GAME!");
		startBox.setAlignment(Pos.CENTER);
		startButton.setOnAction(start -> {
			viewController.initGame(controller.startGame());
			/*
			player1Turn.setValue(controller.startGame());
			stage.setScene(initGameBoard());
			timeStart = System.currentTimeMillis() / 1000L;
			System.out.println(player1Turn.getValue());
			*/

		});

		Text playOptionsText = new Text("OPTIONS");
		threeRadio = new RadioButton("3-in-a-row");
		fourRadio = new RadioButton("4-in-a-row");
		fiveRadio = new RadioButton("5-in-a-row");
		ToggleGroup radioGroup = new ToggleGroup();
		radioGroup.getToggles().addAll(threeRadio, fourRadio, fiveRadio);

		CheckBox growable = new CheckBox("Growable Grid");
		CheckBox drawAllowed = new CheckBox("Allow Draw");

		int reqToWin = 3;

		threeRadio.setOnAction(threeAction -> {
			if(threeRadio.isSelected()) {
				growable.setDisable(false);
				growable.setSelected(false);
				drawAllowed.setDisable(false);
				sendOptions(3, growable.isSelected(), drawAllowed.isSelected());
			}
		});
		fourRadio.setOnAction(fourAction -> {
			if (fourRadio.isSelected()) {
				growable.setSelected(true);
				growable.setDisable(true);
				drawAllowed.setSelected(false);
				drawAllowed.setDisable(true);
				sendOptions(4, growable.isSelected(), drawAllowed.isSelected());
			}
		});
		fiveRadio.setOnAction(fiveAction -> {
			if (fiveRadio.isSelected()) {
				growable.setSelected(true);
				growable.setDisable(true);
				drawAllowed.setSelected(false);
				drawAllowed.setDisable(true);
				sendOptions(5, growable.isSelected(), drawAllowed.isSelected());
			}
		});
		drawAllowed.setOnAction(drawAction -> {
			growable.setDisable(drawAllowed.isSelected());
			sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
		});
		growable.setOnAction(growAction -> {
			drawAllowed.setDisable(growable.isSelected());
			sendOptions(reqToWin, growable.isSelected(), drawAllowed.isSelected());
		});

		VBox optionsBox = new VBox();
		optionsBox.getChildren().addAll(playOptionsText, threeRadio, fourRadio, fiveRadio, growable,drawAllowed, startButton);

		startBox.getChildren().addAll(optionsBox, startButton);
		connectPane.getChildren().add(startBox);
		startBox.setVisible(false);
	}

	public void sendOptions(int reqToWin, boolean growable, boolean draw) {
		viewController.setRowsToWin(reqToWin);
		viewController.setGrowable(growable);
		viewController.setDrawable(draw);
		controller.sendOptions(reqToWin, growable, draw);
	}

	public void setOptions(int reqToWin, boolean grow, boolean draw) {
			switch(reqToWin) {
				case 3:
					threeRadio.setSelected(true);
					growable.setDisable(false);
					drawAllowed.setDisable(false);
					break;
				case 4:
					fourRadio.setSelected(true);
					growable.setDisable(true);
					drawAllowed.setDisable(true);
					break;
				case 5:
					fiveRadio.setSelected(true);
					growable.setDisable(true);
					drawAllowed.setDisable(true);
					break;
			}
			drawAllowed.setDisable(grow);
			drawAllowed.setSelected(draw);
			growable.setSelected(grow);
			growable.setDisable(draw);
	}

	public void connected() {
		connectBox.setVisible(false);
		startBox.setVisible(true);
	}

	public void disconnected() {
		connectBox.setVisible(true);
		startBox.setVisible(false);
	}

}
