package gamelogic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * Created by Mattias Larsson on 2016-05-19.
 */


public class GameBoard extends GridPane {
    private double mWidth, mHeight;
    private int numOfRows = 3;
    private IntegerProperty cellSizeProperty = new SimpleIntegerProperty();

    public GameBoard(double width, double height, int numRows) {
        // The maximum width and height of the board
        numOfRows = numRows;
        this.mWidth = width;
        this.mHeight = height;

        // Set the size of each cell in the grid
        for (int i = 0; i < numOfRows; i++) {
            ColumnConstraints column = new ColumnConstraints((int)mHeight/numOfRows);
            this.getColumnConstraints().add(column);
            RowConstraints row = new RowConstraints((int)mHeight/numOfRows);
            this.getRowConstraints().add(row);
        }
        cellSizeProperty.setValue((int)mHeight/numOfRows);
        this.setGridLinesVisible(true);

    }

    /**
     *
     * @return The size of each cell
     */
    public double getCellSize() {
        return mHeight/numOfRows;
    }

    /**
     *
     * @return The size of each cell as a property
     */
    public IntegerProperty getCellSizeProperty() {
        return cellSizeProperty;
    }

    /**
     * Increase the board's size
     *
     */
    public void incGameBoard() {
        // Remove the constraints
        this.getColumnConstraints().remove(0, numOfRows);
        this.getRowConstraints().remove(0, numOfRows);

        numOfRows += 2;

        // Set new constraints
        for (int i = 0; i<numOfRows; i++) {
            this.getColumnConstraints().add(new ColumnConstraints(mHeight/numOfRows));
            this.getRowConstraints().add(new RowConstraints(mHeight/numOfRows));
        }
        cellSizeProperty.setValue((int)mHeight/numOfRows);
    }

    /**
     *
     * @return The number of rows in the grid
     */
    public int getRows() {
        return numOfRows;
    }

}
