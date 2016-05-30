package gui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.Controller;

import java.util.Random;

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
		TextField ip = new TextField();
		ip.setPromptText("Enter IP");
		Button connectButton = new Button("Connect");

		AnchorPane logoPane = initLogoPane();

		connectBox = new HBox();
		connectBox.getChildren().addAll(ip, connectButton);
		connectBox.setAlignment(Pos.CENTER);
		connectPane = new StackPane();
		connectPane.getChildren().add(connectBox);
		//connectPane.setStyle("-fx-background-color: #ff0000");
		this.setCenter(connectPane);
		this.setTop(logoPane);
		connectButton.setOnAction(connect -> {
			controller.connect(ip.getText());
		});

		startBox = new VBox();
		Button startButton = new Button("START GAME!");
		startBox.setAlignment(Pos.CENTER);
		startButton.setOnAction(start -> {
			viewController.initGame(controller.startGame());
		});

		Text playOptionsText = new Text("OPTIONS");
		threeRadio = new RadioButton("3-in-a-row");
		fourRadio = new RadioButton("4-in-a-row");
		fiveRadio = new RadioButton("5-in-a-row");
		ToggleGroup radioGroup = new ToggleGroup();
		radioGroup.getToggles().addAll(threeRadio, fourRadio, fiveRadio);

		growable = new CheckBox("Growable Grid");
		drawAllowed = new CheckBox("Allow Draw");

		int reqToWin = 3;

		threeRadio.setOnAction(threeAction -> {
			if (threeRadio.isSelected()) {
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
		optionsBox.getChildren().addAll(playOptionsText, threeRadio, fourRadio, fiveRadio, growable, drawAllowed, startButton);

		startBox.getChildren().addAll(optionsBox, startButton);
		connectPane.getChildren().add(startBox);
		startBox.setVisible(false);
	}

	public AnchorPane initLogoPane() {
		AnchorPane anchPane = new AnchorPane();
		Random random = new Random();

		Image ticImg = new Image(getClass().getResourceAsStream("/tic.png"));
		Image tacImg = new Image(getClass().getResourceAsStream("/tac.png"));
		Image toeImg = new Image(getClass().getResourceAsStream("/toe.png"));

		ImageView tic = new ImageView(ticImg);
		ImageView tac = new ImageView(tacImg);
		ImageView toe = new ImageView(toeImg);

		ImageView logoArray[] = { tic, tac, toe };

		tic.setFitWidth(180);
		tac.setFitWidth(180);
		toe.setFitWidth(180);
		tic.setFitHeight(100);
		tac.setFitHeight(100);
		toe.setFitHeight(100);

		tic.setX(10);
		tic.setY(10);

		tac.setX(200);
		tac.setY(60);

		toe.setX(390);
		toe.setY(30);

		anchPane.getChildren().addAll(tic, tac, toe);

		SequentialTransition seqFirst = new SequentialTransition(zoomAnimFirst(tic), zoomAnimFirst(tac), zoomAnimFirst(toe));
		seqFirst.play();

		Timeline repeatTl = new Timeline(new KeyFrame(Duration.millis(4000),ae -> {
			int num = random.nextInt(2-0+1) + 0;
			ScaleTransition currAnim = zoomAnim(logoArray[num]);
			currAnim.play();
		}));
		repeatTl.setCycleCount(Animation.INDEFINITE);
		repeatTl.play();

		return anchPane;
	}


	private ScaleTransition zoomAnimFirst(ImageView currImage) {
		ScaleTransition st = new ScaleTransition(Duration.millis(500), currImage);
		st.setFromX(0.01f);
		st.setFromY(0.01f);
		st.setToX(1.0f);
		st.setToY(1.0f);
		st.setAutoReverse(true);
		return st;
	}

	private ScaleTransition zoomAnim(ImageView currImage) {
		ScaleTransition st = new ScaleTransition(Duration.millis(800), currImage);
		st.setFromX(1.0f);
		st.setFromY(1.0f);
		st.setToX(1.5f);
		st.setToY(1.5f);
		st.setCycleCount(2);
		st.setAutoReverse(true);
		return st;
	}


	public void sendOptions(int reqToWin, boolean growable, boolean draw) {
		viewController.setRowsToWin(reqToWin);
		viewController.setGrowable(growable);
		viewController.setDrawable(draw);
		controller.sendOptions(reqToWin, growable, draw);
	}

	public void setOptions(int reqToWin, boolean grow, boolean draw) {
		switch (reqToWin) {
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
