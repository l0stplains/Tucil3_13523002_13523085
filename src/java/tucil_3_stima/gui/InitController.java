package tucil_3_stima.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;
import tucil_3_stima.strategy.AStar;
import tucil_3_stima.strategy.BeamSearch;
import tucil_3_stima.strategy.BlockingHeuristic;
import tucil_3_stima.strategy.DistanceHeuristic;
import tucil_3_stima.strategy.GBFS;
import tucil_3_stima.strategy.Heuristic;
import tucil_3_stima.strategy.RecursiveBlockingHeuristic;
import tucil_3_stima.strategy.SearchResult;
import tucil_3_stima.strategy.SearchStrategy;
import tucil_3_stima.strategy.UCS;
import tucil_3_stima.utils.InputHandler;

public class InitController {
    // Buttons
    @FXML private Button backButton;
    @FXML private Button solveButton, saveButton; // Output button
    @FXML private MenuButton algorithmButton, heuristicButton; 
    @FXML private Button uploadButton, exampleButton; // Input button
    @FXML private Button resetButton, prevButton, nextButton, speedButton; // Animation button
    @FXML private ToggleButton playButton; 
    
    // Panes
    @FXML private StackPane boardPane;
    
    // Labels
    @FXML private Label timeLabel, expNodeLabel, genNodeLabel, stepsLabel, loadingLabel;
    @FXML private Label timeTitleLabel, expNodeTitleLabel, genNodeTitleLabel, stepsTitleLabel;
    @FXML private Label stepLabelLeft, stepLabelRight;

    // Text Field
    @FXML private TextField stepNumberField;

    // Indicator
    @FXML private ProgressIndicator loadingIndicator;
    
    // Images
    @FXML private ImageView backgroundImageView;

    // Audioss
    private AudioClip clickSound, hoverSound, backSound;
    private MediaPlayer pageBgm;
    private ImageView muteImageView, unmuteImageView;
    private boolean muted = false;
    @FXML private Button muteButton;

    // datas
    private SearchStrategy strategy = null;
    private Heuristic heuristic = null;
    private Board board;
    private State startState;
    private SearchResult searchRes;
    AtomicInteger curStep = new AtomicInteger(0);
    AtomicBoolean animationRunning = new AtomicBoolean(false);
    AtomicReference<Double> stepSpeed = new AtomicReference<>(1.0);
    private ArrayList<State> steps = new ArrayList<>();

    @FXML
    public void initialize() {
        // Setup background image with parallax
        Image bgImage = new Image(getClass().getResource("/tucil_3_stima/gui/assets/background.jpg").toExternalForm());
        backgroundImageView.setImage(bgImage);
        backgroundImageView.setScaleX(1.05);
        backgroundImageView.setScaleY(1.05);
        backgroundImageView.setOpacity(0.2);
        backgroundImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
                    double maxMove = 20;
                    double moveX = ((e.getSceneX() / newScene.getWidth()) - 0.5) * maxMove;
                    double moveY = ((e.getSceneY() / newScene.getHeight()) - 0.5) * maxMove;
                    backgroundImageView.setTranslateX(moveX);
                    backgroundImageView.setTranslateY(moveY);
                });
            }
        });

        Image muteImage = new Image(getClass().getResource("/tucil_3_stima/gui/assets/music-icon.png").toExternalForm());
        muteImageView = new ImageView(muteImage);
        muteImageView.setFitWidth(30);
        muteImageView.setFitHeight(30);

        Image unmuteImage = new Image(getClass().getResource("/tucil_3_stima/gui/assets/music-off-icon.png").toExternalForm());
        unmuteImageView = new ImageView(unmuteImage);
        unmuteImageView.setFitWidth(30);
        unmuteImageView.setFitHeight(30);
        muteButton.setGraphic(muteImageView);

        // Effect
        applyHoverEffects(solveButton);
        applyHoverEffects(algorithmButton);
        applyHoverEffects(heuristicButton);
        applyHoverEffects(saveButton);
        applyHoverEffects(exampleButton);
        applyHoverEffects(uploadButton);
        applyHoverEffects(resetButton);
        applyHoverEffects(prevButton);
        applyHoverEffects(nextButton);
        applyHoverEffects(speedButton);
        applyHoverEffects(playButton);
        applyHoverEffects(muteButton);

        // BGM & Audio
        Media mediaBgm = new Media(getClass().getResource("/tucil_3_stima/gui/assets/slowBgm.mp3").toExternalForm());
        pageBgm = new MediaPlayer(mediaBgm);
        pageBgm.setCycleCount(MediaPlayer.INDEFINITE);
        pageBgm.setVolume(0.15);
        pageBgm.play();

        clickSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/click.wav").toExternalForm());
        clickSound.setVolume(0.05);
        hoverSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hover.wav").toExternalForm());
        hoverSound.setVolume(0.01);
        backSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/back.wav").toExternalForm());
        backSound.setVolume(0.05);
        
        // Buttons
        applyBackButtonEffects(backButton);
        uploadButton.setOnAction(e -> { uploadBtnAction(); });
        exampleButton.setOnAction(e -> { exampleBtnAction(); });
        solveButton.setOnAction(e -> { solveBtnAction(); });
        saveButton.setOnAction(e -> { saveBtnAction(); });
        resetButton.setOnAction(e -> { resetBtnAction(); });
        prevButton.setOnAction(e -> { prevBtnAction(); });
        nextButton.setOnAction(e -> { nextBtnAction(); });
        speedButton.setOnAction(e -> { speedBtnAction(); });
        playButton.setOnAction(e -> { playBtnAction(); });
        muteButton.setOnAction(e -> { muteButtonEffect(); });

        // Init buttons options (heuristic & algo buttons)
        // Algorithm button
        String[] algorithms = {"A*", "UCS", "GBFS", "Beam"};
        for (String algo : algorithms) {
            Label label = new Label(algo);

            
            Platform.runLater(() -> {
                label.setMinWidth(algorithmButton.getWidth());
                label.setPrefWidth(algorithmButton.getWidth());
                label.setAlignment(Pos.CENTER);
            });

            label.setOnMouseClicked(e -> {
                algorithmButton.setText(algo);
                SearchStrategy s = null;

                if ("A*".equals(algo)) s = new AStar();
                else if ("UCS".equals(algo)) s = new UCS();
                else if ("GBFS".equals(algo)) s = new GBFS();
                else if ("Beam".equals(algo)) s = new BeamSearch(100);

                if (s != null) {
                    strategy = s;
                }
            });

            applyHoverEffects(label);
            
            CustomMenuItem item = new CustomMenuItem(label, false);
            item.setHideOnClick(true);
            algorithmButton.getItems().add(item);
        }

        // Heuristic button
        String[] heuristics = {"Blocking", "Recursive Blocking", "Distance"};
        for (String heur : heuristics) {
            Label label = new Label(heur);

            Platform.runLater(() -> {
                label.setMinWidth(heuristicButton.getWidth());
                label.setPrefWidth(heuristicButton.getWidth());
                label.setAlignment(Pos.CENTER);
            });

            label.setOnMouseClicked(e -> {
                heuristicButton.setText(heur);
                Heuristic s = null;

                if ("Blocking".equals(heur)) s = new BlockingHeuristic();
                else if ("Recursive Blocking".equals(heur)) s = new RecursiveBlockingHeuristic();
                else if ("Distance".equals(heur)) s = new DistanceHeuristic();

                if (s != null) {
                    heuristic = s;
                }
            });

            applyHoverEffects(label);
            
            CustomMenuItem item = new CustomMenuItem(label, false);
            item.setHideOnClick(true);
            heuristicButton.getItems().add(item);
        }
    
        stepNumberField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    newValue = newValue.replaceAll("[^\\d]", "");
                }
            
                try {
                    int n = Integer.parseInt(newValue);
                    if (n < 0) {
                        stepNumberField.setText("0");
                        curStep.set(0);
                    }
                    else if (n >= steps.size()) {
                        stepNumberField.setText("" + (steps.size() - 1));
                        curStep.set(steps.size() - 1);
                    }
                    else {
                        stepNumberField.setText("" + n);
                        curStep.set(n);
                    }
                }
                catch (Exception e) {
                    stepNumberField.setText("0");
                    curStep.set(0);
                }

                drawBoard();
            }
        });
        
    }

    private void applyBackButtonEffects(Button button) {
        button.setOnAction(e -> {
            if(clickSound != null) clickSound.play();
            if (pageBgm != null) pageBgm.stop();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tucil_3_stima/gui/main_menu.fxml"));
                Scene scene = new Scene(loader.load(), button.getScene().getWidth(), button.getScene().getHeight());
                MainApp mainApp = (MainApp) button.getScene().getWindow().getUserData();
                mainApp.backgroundPlayer.play();
                mainApp.switchScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        button.setOnMouseEntered(e -> {
            if (hoverSound != null) {
                hoverSound.play();
            }
            TranslateTransition translate = new TranslateTransition(Duration.millis(100), button);
            translate.setToX(10); // move 10px right

            FadeTransition fade = new FadeTransition(Duration.millis(100), button);
            fade.setToValue(1.0); // fully opaque

            ParallelTransition pt = new ParallelTransition(translate, fade);
            pt.play();
        });

        // Mouse Exit: reset translate, scale, and opacity
        button.setOnMouseExited(e -> {
            TranslateTransition translate = new TranslateTransition(Duration.millis(200), button);
            translate.setToX(0);

            FadeTransition fade = new FadeTransition(Duration.millis(200), button);
            fade.setToValue(0.85); // back to default

            ParallelTransition pt = new ParallelTransition(translate, fade);
            pt.play();
        });

        button.setOnAction(e -> {
            if (backSound != null) {
                backSound.play();
            }
            if(pageBgm != null) pageBgm.stop();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tucil_3_stima/gui/main_menu.fxml"));
                Scene scene = new Scene(loader.load(), backButton.getScene().getWidth(), backButton.getScene().getHeight());
                MainApp mainApp = (MainApp) backButton.getScene().getWindow().getUserData();
                mainApp.backgroundPlayer.play();
                mainApp.switchScene(scene);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void applyHoverEffects(Node node) {
        // Set the default opacity
        node.setOpacity(0.85);

        // Mouse Enter: shift left, scale up, and fade to full opacity
        node.setOnMouseEntered(e -> {
            if (hoverSound != null) {
                hoverSound.play();
            }

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.05);
            scale.setToY(1.05);

            FadeTransition fade = new FadeTransition(Duration.millis(100), node);
            fade.setToValue(1.0); // fully opaque

            ParallelTransition pt = new ParallelTransition(scale, fade);
            pt.play();
        });

        // Mouse Exit: reset translate, scale, and opacity
        node.setOnMouseExited(e -> {

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.0);
            scale.setToY(1.0);

            FadeTransition fade = new FadeTransition(Duration.millis(200), node);
            fade.setToValue(0.85); // back to default

            ParallelTransition pt = new ParallelTransition(scale, fade);
            pt.play();
        });

        if (node instanceof ButtonBase button) {
            button.setOnAction(e -> {
                if (clickSound != null) {
                    clickSound.play();
                }
            });
            
        }
    }

    private void uploadBtnAction() {
        if(clickSound != null) clickSound.play();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Puzzle Configuration");

        // Set file extension filter to only allow .txt files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                Pair<Board, State> parseRes = InputHandler.inputTestCaseFromFile(file.getPath());
                if (parseRes.getKey() != null && parseRes.getValue() != null) {
                    board = parseRes.getKey();
                    startState = parseRes.getValue();
                    
                    StackPane boardView = BoardViewBuilder.buildBoardView(board, startState);
                    boardPane.getChildren().clear();
                    boardPane.getChildren().add(boardView);
                    StackPane.setAlignment(boardPane, Pos.CENTER);
                    
                    // set 
                    solveButton.setDisable(false);
                    algorithmButton .setDisable(false);
                    heuristicButton.setDisable(false);
                    stepLabelLeft.setVisible(false);
                    stepLabelRight.setVisible(false);
                    stepNumberField.setVisible(false);
                    saveButton.setDisable(true);
                    
                } 
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load configuration: " + ex.getMessage());
                alert.showAndWait();
                solveButton.setDisable(true);
                algorithmButton .setDisable(true);
                heuristicButton.setDisable(true);
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
                solveButton.setDisable(true);
                algorithmButton .setDisable(true);
                heuristicButton.setDisable(true);
            } 
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No file selected.");
            alert.showAndWait();
        }
    }

    private void exampleBtnAction() {
        if (clickSound != null) clickSound.play();
        // heck yeah i type it manually
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"Example Format:\n\nBoard size: (rows cols)\nThen number of vehicle (not counting the player's).\n\nThen each block\n\nSample:\n6 6\n11\nGBB.L.\nGHI.LM\nGHIPPMK\nCCCZ.M\n..JZDD\nEEJFF.\n");
        alert.setTitle("Example Configuration Format");
        alert.showAndWait();
    }

    private void solveBtnAction() {
        if (strategy != null && heuristic != null) {
            loadingIndicator.setVisible(true);
            loadingLabel.setVisible(true);
            loadingLabel.setText("Please Wait");
            
            Task<Boolean> solveTask = new Task<>() {
                @Override
                protected Boolean call() {
                    searchRes = strategy.solve(board, startState, heuristic);
                    boolean solved = searchRes.getSolution() != null;

                    // make solution into steps;
                    if (solved) {
                        steps.clear();
                        tucil_3_stima.model.State curState = searchRes.getSolution();
                        while (curState.getParent() != null) {
                            steps.add(0, curState);
                            curState = curState.getParent();
                            curStep.set(0);
                        }
                        steps.add(0, curState);
                    }

                    return solved;
                }
            };
            
            solveTask.setOnSucceeded(e -> {
                timeLabel.setText(String.valueOf(searchRes.getDurationMillis()));
                expNodeLabel.setText(String.valueOf(searchRes.getNodesExpanded()));
                genNodeLabel.setText(String.valueOf(searchRes.getNodesGenerated()));
                stepsLabel.setText(String.valueOf(searchRes.getSolutionDepth()));
                loadingIndicator.setVisible(false);
                loadingLabel.setVisible(false);

                // succ seed
                if (solveTask.getValue()) {
                    playButton.setDisable(false);
                    resetButton.setDisable(false);
                    prevButton.setDisable(false);
                    nextButton.setDisable(false);
                    speedButton.setDisable(false);
                    saveButton.setDisable(false);
                    stepLabelLeft.setVisible(true);
                    stepLabelRight.setVisible(true);
                    stepNumberField.setVisible(true);
                    stepNumberField.setText("" + curStep.get());
                    stepLabelRight.setText("of " + (steps.size() - 1));

                    // reset board to the start state
                    StackPane boardView = BoardViewBuilder.buildBoardView(board, startState);
                    boardPane.getChildren().clear();
                    boardPane.getChildren().add(boardView);
                    StackPane.setAlignment(boardPane, Pos.CENTER);

                    
                } else {
                    playButton.setDisable(true);
                    resetButton.setDisable(true);
                    prevButton.setDisable(true);
                    nextButton.setDisable(true);
                    speedButton.setDisable(true);
                    stepLabelLeft.setVisible(false);
                    stepLabelRight.setVisible(false);
                    stepNumberField.setVisible(false);
                    saveButton.setDisable(true);

                    Alert alert = new Alert(Alert.AlertType.ERROR, "No Solution Found");
                    alert.showAndWait();   
                }
            });
            new Thread(solveTask).start();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No algorithm or heuristics chosen");
            alert.showAndWait();    
        }
    }

    private void saveBtnAction() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Solution as txt");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Txt Files", "*.txt"));
            
            if (steps.isEmpty()|| board == null || startState == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No solution to save into txt");
                alert.showAndWait();
                return;
            }

            File file = fileChooser.showSaveDialog(boardPane.getScene().getWindow());
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Solution Stats\n");
                    writer.write("Time           : " + searchRes.getDurationMillis() + "\n");
                    writer.write("Expanded Node  : " + searchRes.getNodesExpanded() + "\n");
                    writer.write("Generated Node : " + searchRes.getNodesGenerated() + "\n");                    
                    writer.write("Solution Depth : " + searchRes.getSolutionDepth() + "\n\n");                    

                    writer.write("Initial Board\n");
                    writer.write(drawBoardTxt(board, startState.getPositions()));
                    writer.write("\n");
                    
                    for (int i = 1; i < steps.size(); i++) {
                        State s = steps.get(i);
                        Pair<Integer, Integer> lastMovement = s.getLastMovement();
                        if (lastMovement != null) {
                            Vehicle v = board.getVehicle(lastMovement.getKey());
                            String gerakan;
    
                            if (v.isHorizontal()) {
                                if (lastMovement.getValue() > 0) gerakan = "right";
                                else gerakan = "left";
                            }
                            else {
                                if (lastMovement.getValue() > 0) gerakan = "down";
                                else gerakan = "up";
                            }
    
                            writer.write("Step " + i + ": " + v.getSymbol() + "-" + gerakan + '\n');
                        }

                        writer.write(drawBoardTxt(board, s.getPositions()));
                        writer.write("\n");
                    }

                    
                }
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save txt: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private String drawBoardTxt(Board board, int[] pos) {
        int row = board.getRows();
        int col = board.getCols();
        char[][] grid = new char[row][col];

        // init grid
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                grid[i][j] = '.';
            }
        }
        
        // fill with vehicles
        Vehicle[] vehicles = board.getVehicles();
        for (int i = 0; i < vehicles.length; i++) {
            Vehicle v = vehicles[i];
            int vRow = pos[i] / col;
            int vCol = pos[i] % col;

            for (int j = 0; j < v.length(); j++) {
                if (v.isHorizontal()) {
                    grid[vRow][vCol + j] = v.getSymbol();
                }
                else {
                    grid[vRow + j][vCol] = v.getSymbol();
                }
            }
        }

        // char grid to string
        String res = "";
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                res += grid[i][j];
            }
            res += "\n";
        }

        return res;
    }

    private void resetBtnAction() {
        if (steps != null) {
            curStep.set(0);
            
            stepNumberField.setText("" + curStep.get());
            stepLabelRight.setText("of " + (steps.size() - 1));
        }
    }

    private void prevBtnAction() {
        if (steps != null) {
            int newStep = curStep.get() - 1 >= 0 ? curStep.get() - 1 : curStep.get();
            curStep.set(newStep);

            stepNumberField.setText("" + curStep.get());
            stepLabelRight.setText("of " + (steps.size() - 1));
        }
    }

    private void nextBtnAction() {
        if (steps != null) {
            int newStep = curStep.get() + 1 < steps.size() ? curStep.get() + 1 : curStep.get();
            curStep.set(newStep);

            stepNumberField.setText("" + curStep.get());
            stepLabelRight.setText("of " + (steps.size() - 1));
        }
    }

    private void speedBtnAction() {
        if (stepSpeed.get() == 1.0) stepSpeed.set(2.0);
        else if (stepSpeed.get() == 2.0) stepSpeed.set(4.0);
        else if (stepSpeed.get() == 4.0) stepSpeed.set(.25);
        else if (stepSpeed.get() == .25) stepSpeed.set(0.5);
        else if (stepSpeed.get() == 0.5) stepSpeed.set(1.0);

        speedButton.setText(String.valueOf(stepSpeed.get()) + "x");
    }

    private void playBtnAction() {
        if (animationRunning.get()) {
            animationRunning.set(false);
            resetButton.setDisable(false);
            prevButton.setDisable(false);
            nextButton.setDisable(false);
            solveButton.setDisable(false);
            uploadButton.setDisable(false);
            stepNumberField.setDisable(false);

            return;
        }

        animationRunning.set(true);
        resetButton.setDisable(true);
        prevButton.setDisable(true);
        nextButton.setDisable(true);
        solveButton.setDisable(true);
        uploadButton.setDisable(true);
        stepNumberField.setDisable(true);
        

        int baseTime = 1000;
        Task<Boolean> solveTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    for (int i = curStep.get(); i < steps.size(); i++) {
                        if (!animationRunning.get() || isCancelled()) break;

                        tucil_3_stima.model.State state = steps.get(i); 

                        Platform.runLater(() -> {
                            stepNumberField.setText("" + curStep.get());
                            stepLabelRight.setText("of " + (steps.size() - 1));
                        });

                        curStep.set(i);
                        Thread.sleep((long) (baseTime / stepSpeed.get()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }

                return true;
            }
        };

        solveTask.setOnSucceeded(e -> {
            animationRunning.set(false);
            resetButton.setDisable(false);
            prevButton.setDisable(false);
            nextButton.setDisable(false);
            solveButton.setDisable(false);
            uploadButton.setDisable(false);
            stepNumberField.setDisable(false);
        });

        new Thread(solveTask).start();
    }

    private void muteButtonEffect() {
        if (muted) {
            muteButton.setGraphic(muteImageView);
            muted = false;
            pageBgm.play();
        }
        else {
            muteButton.setGraphic(unmuteImageView);
            muted = true;
            pageBgm.stop();
        }
    }

    private void drawBoard() {
            StackPane boardView = BoardViewBuilder.buildBoardView(board, steps.get(curStep.get()));
            boardPane.getChildren().clear();
            boardPane.getChildren().add(boardView);
            StackPane.setAlignment(boardPane, Pos.CENTER);
    }
}
