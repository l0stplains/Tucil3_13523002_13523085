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
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

public class BoardViewBuilder {
    private static final Map<Character, Color> VEHICLE_COLOR_MAP = createColorMap();

    private static Map<Character, Color> createColorMap() {
        Map<Character, Color> map = new HashMap<>();
        map.put('A', Color.web("#FFB74D")); // Orange
        map.put('B', Color.web("#4DB6AC")); // Teal
        map.put('C', Color.web("#64B5F6")); // Light Blue
        map.put('D', Color.web("#81C784")); // Green
        map.put('E', Color.web("#BA68C8")); // Purple
        map.put('F', Color.web("#F06292")); // Pink
        map.put('G', Color.web("#7986CB")); // Indigo
        map.put('H', Color.web("#AED581")); // Light Green
        map.put('I', Color.web("#E57373")); // Red
        map.put('J', Color.web("#4DD0E1")); // Cyan
        map.put('K', Color.web("#FFD54F")); // Yellow
        map.put('L', Color.web("#A1887F")); // Brown
        map.put('M', Color.web("#90A4AE")); // Blue Gray
        map.put('N', Color.web("#FF8A65"));
        map.put('O', Color.web("#D4E157"));
        map.put('P', Color.web("#F48FB1"));
        map.put('Q', Color.web("#7986CB"));
        map.put('R', Color.web("#A5D6A7"));
        map.put('S', Color.web("#CE93D8"));
        map.put('T', Color.web("#FFF176"));
        map.put('U', Color.web("#81D4FA"));
        map.put('V', Color.web("#FFAB91"));
        map.put('W', Color.web("#B0BEC5"));
        map.put('X', Color.web("#DCEDC8"));
        map.put('Y', Color.web("#B39DDB"));
        map.put('Z', Color.web("#FFCCBC"));
        return map;
    }

    public static StackPane buildBoardView(Board board, State state) {
        final double BOARD_SIZE = 525;
        int rows = board.getRows();
        int cols = board.getCols();
        Vehicle[] vehicles = board.getVehicles();
        int[] pos = state.getPositions();

        // Each cell is sized so that the entire board fits into 600x600
        double cellSize = BOARD_SIZE / Math.max(rows, cols);

        Pane root = new Pane();
        root.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        root.setMinSize(BOARD_SIZE, BOARD_SIZE);
        root.setMaxSize(BOARD_SIZE, BOARD_SIZE);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Coordinates for center of this cell
                // double centerX = c * cellSize + cellSize / 2.0;
                // double centerY = r * cellSize + cellSize / 2.0;
                
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

            // Position of the vehicle
            double x = curC * cellSize + (cellSize * (isHorizontal ? length : 1) - width) / 2.0;
            double y = curR * cellSize + (cellSize * (isHorizontal ? 1 : length) - height) / 2.0;

            // Add a label (like 'F', 'Z') on top
            Text label = new Text(String.valueOf(v.getSymbol()));
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
        
        StackPane.setAlignment(root, Pos.CENTER);            
        StackPane container = new StackPane(root);
        container.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}