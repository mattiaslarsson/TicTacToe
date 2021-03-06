package gui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.Controller;

import java.util.Random;

/**
 * Intro screen and game configuration screen.
 *
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

    private MediaPlayer mediaPlayer;

    private int logoCounter;
    private AnimationTimer logoTimer;

    public StartPanel(Controller controller, AppWindow viewController) {
        this.controller = controller;
        this.viewController = viewController;
        this.setStyle("-fx-background-image: url(\"/textured_paper.png\");-fx-background-size: 600, 600;-fx-background-repeat: no-repeat;");
        initStartScreen();
    }

	/**
	 * Initializes the start screen.
	 */
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

        threeRadio = new RadioButton("3-in-a-row");
        fourRadio = new RadioButton("4-in-a-row");
        fiveRadio = new RadioButton("5-in-a-row");
        ToggleGroup radioGroup = new ToggleGroup();
        radioGroup.getToggles().addAll(threeRadio, fourRadio, fiveRadio);

        growable = new CheckBox("Growable Grid");
        drawAllowed = new CheckBox("Allow Draw");

        int reqToWin = 3;

        threeRadio.setSelected(true);
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

        HBox optionsBox = new HBox();
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setSpacing(20);

        VBox numberOptionsBox = new VBox();
        numberOptionsBox.setSpacing(10);
        numberOptionsBox.getChildren().addAll(threeRadio, fourRadio, fiveRadio);

        VBox growOptionsBox = new VBox();
        growOptionsBox.setSpacing(10);
        growOptionsBox.getChildren().addAll(growable, drawAllowed);

        optionsBox.getChildren().addAll(numberOptionsBox, growOptionsBox);

        startBox.setSpacing(30);
        startBox.getChildren().addAll(optionsBox, startButton);
        connectPane.getChildren().add(startBox);
        startBox.setVisible(false);
    }

	/**
	 * Initializes the pane containing the logo (with animation)
	 *
	 * @return AnchorPane
	 */
    public AnchorPane initLogoPane() {
        AnchorPane anchPane = new AnchorPane();
        Random random = new Random();

        Image ticImg = new Image(getClass().getResourceAsStream("/tic.png"));
        Image tacImg = new Image(getClass().getResourceAsStream("/tac.png"));
        Image toeImg = new Image(getClass().getResourceAsStream("/toe.png"));

        ImageView tic = new ImageView(ticImg);
        ImageView tac = new ImageView(tacImg);
        ImageView toe = new ImageView(toeImg);

        ImageView logoArray[] = {tic, tac, toe};

        tic.setFitWidth(180);
        tac.setFitWidth(180);
        toe.setFitWidth(180);
        tic.setFitHeight(100);
        tac.setFitHeight(100);
        toe.setFitHeight(100);

        tic.setX(10);
        tic.setY(140);

        tac.setX(200);
        tac.setY(120);

        toe.setX(390);
        toe.setY(144);

        tic.setVisible(false);
        tac.setVisible(false);
        toe.setVisible(false);

        anchPane.getChildren().addAll(tic, tac, toe);

        Media ticSound = new Media(getClass().getResource("/tic.mp3").toString());
        Media tacSound = new Media(getClass().getResource("/tac.mp3").toString());
        Media toeSound = new Media(getClass().getResource("/toe.mp3").toString());


        logoCounter = 0;
        AnimationTimer logoTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (logoCounter == 60) {
                    logoAnimation(tic, ticSound);
                } else if (logoCounter == 120) {
                    logoAnimation(tac, tacSound);
                } else if (logoCounter == 180) {
                    logoAnimation(toe, toeSound);
                }

                logoCounter++;
            }
        };

        logoTimer.start();

        Timeline repeatTl = new Timeline(new KeyFrame(Duration.millis(4000), ae -> {
            int num = random.nextInt(2 - 0 + 1) + 0;
            logoArray[num].toFront();
            ScaleTransition currAnim = zoomAnim(logoArray[num]);
            currAnim.play();
        }));
        repeatTl.setCycleCount(Animation.INDEFINITE);
        repeatTl.play();

        return anchPane;
    }

	/**
	 * Method to animate the different elements of the logo.
	 *
	 * @param image ImageView
	 * @param sound Media
	 */
    private void logoAnimation(ImageView image, Media sound) {
        ScaleTransition st = zoomAnimFirst(image, sound);

        image.setScaleX(0.01f);
        image.setScaleY(0.01f);
        image.setVisible(true);
        st.play();
    }

	/**
	 * The logo animation routine.
	 *
	 * @param currImage ImageView
	 * @param sound Media
	 * @return ScaleTransition
	 */
    private ScaleTransition zoomAnimFirst(ImageView currImage, Media sound) {
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        ScaleTransition st = new ScaleTransition(Duration.millis(400), currImage);
        st.setFromX(0.01f);
        st.setFromY(0.01f);
        st.setToX(1.0f);
        st.setToY(1.0f);
        st.setAutoReverse(true);
        return st;
    }

	/**
	 * The running logo animation.
	 *
	 * @param currImage
	 * @return ScaleTransition
	 */
    private ScaleTransition zoomAnim(ImageView currImage) {
        ScaleTransition st = new ScaleTransition(Duration.millis(600), currImage);
        st.setFromX(1.0f);
        st.setFromY(1.0f);
        st.setToX(1.5f);
        st.setToY(1.5f);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        return st;
    }

	/**
	 * Sends the game options to the remote client.
	 *
	 * @param reqToWin int
	 * @param growable boolean
	 * @param draw boolean
	 */
    public void sendOptions(int reqToWin, boolean growable, boolean draw) {
        viewController.setRowsToWin(reqToWin);
        viewController.setGrowable(growable);
        viewController.setDrawable(draw);
        controller.sendOptions(reqToWin, growable, draw);
    }

	/**
	 * Sets the game options from remote.
	 *
	 * @param reqToWin int
	 * @param grow boolean
	 * @param draw boolean
	 */
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
                growable.setSelected(true);
                break;
            case 5:
                fiveRadio.setSelected(true);
                growable.setDisable(true);
                growable.setSelected(true);
                drawAllowed.setDisable(true);
                break;
        }
    }

	/**
	 * Switches the view to connected state.
	 */
    public void connected() {
        connectBox.setVisible(false);
        startBox.setVisible(true);
    }

	/**
	 * Switches the view to disconnected state.
	 */
    public void disconnected() {
        connectBox.setVisible(true);
        startBox.setVisible(false);
    }
}
