package tucil_3_stima.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

public class BoardViewBuilder {
    private static final Map<Character, Color> VEHICLE_COLOR_MAP = createColorMap();

    private static Map<Character, Color> createColorMap() {
        Map<Character, Color> map = new HashMap<>();
        map.put('A', Color.STEELBLUE);           // Primary piece (P); // Bright Yellow
        map.put('B', Color.ROYALBLUE); // Bright Cyan
        map.put('C', Color.LIMEGREEN); // Sky Blue
        map.put('D', Color.CORAL); // Lime Green
        map.put('E', Color.DARKORANGE); // Lavender Purple
        map.put('F', Color.MEDIUMPURPLE); // Hot Pink
        map.put('G', Color.DEEPSKYBLUE); // Indigo
        map.put('H', Color.HOTPINK); // Light Lime
        map.put('I', Color.FORESTGREEN); // Coral Red
        map.put('J', Color.CHOCOLATE); // Baby Blue
        map.put('K', Color.SLATEBLUE); // Light Yellow
        map.put('L', Color.TOMATO); // Soft Brown
        map.put('M', Color.TEAL); // Light Sky Blue
        map.put('N', Color.INDIANRED); // Light Orange
        map.put('O', Color.MEDIUMSEAGREEN); // Light Lime
        map.put('P', Color.CRIMSON); // Bright Red
        map.put('Q', Color.DARKVIOLET); // Light Purple
        map.put('R', Color.SANDYBROWN); // Bright Mint
        map.put('S', Color.CORAL); // Soft Pink
        map.put('T', Color.DARKORANGE); // Bright Amber
        map.put('U', Color.DARKCYAN); // Bright Sky Blue
        map.put('V', Color.MEDIUMORCHID); // Bright Orange
        map.put('W', Color.LIGHTCORAL); // Soft Aqua
        map.put('X', Color.LIGHTSEAGREEN); // Mint Green
        map.put('Y', Color.LIGHTSLATEGRAY); // Light Lavender
        map.put('Z', Color.LIGHTSTEELBLUE); // Light Red

        return map;
    }

    public static StackPane buildBoardView(Board board, State state) {
        final double BOARD_SIZE = 525;
        int rows = board.getRows();
        int cols = board.getCols();
        Vehicle[] vehicles = board.getVehicles();

        Pair<Integer, Integer> lastMovement = state.getLastMovement();
        int[] pos = state.getPositions();

        // Each cell is sized so that the entire board fits into 600x600
        double cellSize = BOARD_SIZE / Math.max(rows, cols);

        Pane root = new Pane();
        root.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        root.setMinSize(BOARD_SIZE, BOARD_SIZE);
        root.setMaxSize(BOARD_SIZE, BOARD_SIZE);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setLayoutX(c * cellSize);
                cell.setLayoutY(r * cellSize);
                cell.setStroke(Color.GRAY); // grid line
                cell.setFill(Color.rgb(0, 0, 0, 0.2)); // subtle transparent fill
                cell.setStrokeWidth(0.5);
                root.getChildren().add(cell);
            }
        }

        for (int i = 0; i < vehicles.length; i++) {
            int curPos = pos[i];
            int curR = curPos / cols;
            int curC = curPos % cols;

            Vehicle v = vehicles[i];
            int length = v.length();
            boolean isHorizontal = v.isHorizontal();

            double shrink = 0.9;
            double width = isHorizontal ? cellSize * length - (cellSize * (1 - shrink)) : cellSize * shrink;
            double height = isHorizontal ? cellSize * shrink : cellSize * length - (cellSize * (1 - shrink));

            Rectangle rect = new Rectangle(width, height); // Shrink slightly for padding
            rect.setArcWidth(15); // Rounded corners
            rect.setArcHeight(15);
            rect.setFill(VEHICLE_COLOR_MAP.get(v.getSymbol()));
            if (lastMovement != null && i == lastMovement.getKey()) {
                rect.setStroke(Color.GOLD);
                rect.setStrokeWidth(5);
            }

            // Position of the vehicle
            double x = curC * cellSize + (cellSize * (isHorizontal ? length : 1) - width) / 2.0;
            double y = curR * cellSize + (cellSize * (isHorizontal ? 1 : length) - height) / 2.0;

            // Add a label (like 'F', 'Z') on top
            Text label = new Text(String.valueOf(v.getSymbol()));
            if (v.getSymbol() == 'P') {
                label.setText("Player");
            }
            label.setFont(Font.font(20));
            label.setFill(Color.WHITE);

            StackPane stack = new StackPane(rect, label);
            stack.setPrefSize(width, height);

            // Group rectangle and label
            Group vehicleGroup = new Group(stack);

            vehicleGroup.setLayoutX(x);
            vehicleGroup.setLayoutY(y);

            root.getChildren().add(vehicleGroup);
        }

        // draw exit door
        int exitR = board.getExitRow();
        int exitC = board.getExitCol();
        
        Color exitColor = Color.rgb(105, 216, 88, 0.3);

        // left - right
        if (board.getExitHorizontal()) {
            Rectangle exitRect = new Rectangle(cellSize * 0.1, cellSize);
            exitRect.setStroke(exitColor);
            exitRect.setFill(exitColor);
            exitRect.setStrokeWidth(0.5);

            if (exitC == 0) exitRect.setLayoutX(exitC * cellSize);
            else exitRect.setLayoutX((exitC + 1) * cellSize);

            exitRect.setLayoutY(exitR * cellSize);
            root.getChildren().add(exitRect);
        }  
        else {
            Rectangle exitRect = new Rectangle(cellSize, cellSize * 0.1);
            
            exitRect.setStroke(exitColor);
            exitRect.setFill(exitColor);
            exitRect.setStrokeWidth(0.5);

            if (exitR == 0) exitRect.setLayoutY(exitR * cellSize);
            else exitRect.setLayoutY((exitR + 1) * cellSize);
            
            exitRect.setLayoutX(exitC * cellSize);
            root.getChildren().add(exitRect);
        }
        
        StackPane.setAlignment(root, Pos.CENTER);            
        StackPane container = new StackPane(root);
        container.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}