import gui.AppWindow;
import javafx.application.Application;
import javafx.stage.Stage;
import logic.Controller;
import network.NetworkHandler;

/**
 *
 * Application entry class. Initializes controller, view, network and database.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-20.
 */
public class TicApp extends Application{

	private AppWindow view;

	public static void main (String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Init controller
		Controller controller = new Controller();
		// Init view
		view = new AppWindow(primaryStage, controller);
		// Register view with controller
		controller.registerView(view);

		// Init network and start network thread
		NetworkHandler networkHandler = new NetworkHandler(controller);
		Thread network = new Thread(networkHandler);
		network.start();
	}
}
