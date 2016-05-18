import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias on 2016-05-17.
 */

package gamelogic;

public class Main extends Application {
    private BooleanProperty player1 = new SimpleBooleanProperty(true);
    private GridPane grid = new GridPane();
    private ObservableList<Node> nodeList = FXCollections.observableArrayList();
    private long colSum = 0;
    private long rowSum = 0;
    private long diagonalSum = 0;
    private List<Pair<Circle, Point2D>> circleList = new ArrayList<Pair<Circle, Point2D>>();
    private Stage stage;
    private boolean winner = false;

    public void start(Stage stage) {
        this.stage = stage;
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(100);
            grid.getColumnConstraints().add(column);
            RowConstraints row = new RowConstraints(100);
            grid.getRowConstraints().add(row);
        }
        grid.setGridLinesVisible(true);
        grid.setOnMouseClicked(mouseClick -> {
            stage.setTitle(String.valueOf(winner));
            rowSum = 0;
            colSum = 0;
            diagonalSum = 0;
            if(!winner) {
                if (checkDoubles((int) ((mouseClick.getSceneX() - (mouseClick.getSceneX() % 100)) / 100),
                        (int) ((mouseClick.getSceneY() - (mouseClick.getSceneY() % 100)) / 100))) {
                    if (player1.getValue() == true) {
                        grid.add(addRed(), (int) ((mouseClick.getSceneX() - (mouseClick.getSceneX() % 100)) / 100),
                                (int) ((mouseClick.getSceneY() - (mouseClick.getSceneY() % 100)) / 100));
                    } else {
                        grid.add(addBlue(), (int) ((mouseClick.getSceneX() - (mouseClick.getSceneX() % 100)) / 100),
                                (int) ((mouseClick.getSceneY() - (mouseClick.getSceneY() % 100)) / 100));
                    }
                    player1.setValue(!player1.getValue());
                    checkWinner();
                }
            }
        });

        Scene scene = new Scene(grid, 300, 300);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean checkDoubles(int col, int row) {
        for (Node node : nodeList) {
            if (GridPane.getColumnIndex(node) - col == 0 && GridPane.getRowIndex(node) - row == 0) {
                return false;
            }
        }
        return true;
    }

    private void checkWinner() {

        Circle lastCircle = (Circle) nodeList.get(nodeList.size() - 1);
        int column = GridPane.getColumnIndex(lastCircle);
        int row = GridPane.getRowIndex(lastCircle);
        Color color = (Color) lastCircle.getFill();
        System.out.println(color.toString());

        nodeList.forEach(node -> {
            if (node != lastCircle) {
                circleList.add(new Pair((Circle) node, new Point2D(
                        (int)GridPane.getColumnIndex(node),
                        (int)GridPane.getRowIndex(node))));
            }
        });

        if (!winner) {
            if(circleList.stream().filter(circle -> circle.getValue().getY() == row)
                    .filter(circle -> circle.getKey().getFill() == color).count() >= 4 ||
                    circleList.stream().filter(circle -> circle.getValue().getX() == column)
                            .filter(circle -> circle.getKey().getFill() == color).count() >= 4) {
                winner = true;
                stage.setTitle(color.toString() + " vinner");
                return;
            }

        }

    }



    private Circle addRed() {
        Circle circle = new Circle(50);
        circle.setFill(Color.RED);
        nodeList.add(circle);
        return circle;
    }

    private Circle addBlue() {
        Circle circle = new Circle(50);
        circle.setFill(Color.BLUE);
        nodeList.add(circle);
        return circle;
    }
}
