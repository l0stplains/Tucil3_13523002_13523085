package tucil_3_stima.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

public class BoardViewBuilder {
    public static StackPane buildBoardView(Board board, State state) {
        final double BOARD_SIZE = 550;
        int rows = board.getRows();
        int cols = board.getCols();
        Vehicle[] vehicles = board.getVehicles();
        int[] pos = state.getPositions();

        // Each cell is sized so that the entire board fits into 600x600
        double cellSize = BOARD_SIZE / Math.max(rows, cols);

        Pane root = new Pane();
        root.setPrefSize(BOARD_SIZE, BOARD_SIZE);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Coordinates for center of this cell
                double centerX = c * cellSize + cellSize / 2.0;
                double centerY = r * cellSize + cellSize / 2.0;
                
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setX(centerX);
                cell.setY(centerY);
                cell.setStrokeWidth(1.5);
                root.getChildren().add(cell);
            }
        }

        for (int i = 0; i < vehicles.length; i++) {
            int curPos = pos[i];
            int curR = curPos / cols;
            int curC = curPos % cols;
        }

        StackPane container = new StackPane(root);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        return container;
    }
}