package tucil_3_stima.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.strategy.AStar;
import tucil_3_stima.strategy.Heuristic;
import tucil_3_stima.strategy.SearchResult;
import tucil_3_stima.strategy.UCS;
import tucil_3_stima.strategy.AbstractSearch;
import tucil_3_stima.strategy.BlockingHeuristic;
import tucil_3_stima.strategy.DistanceHeuristic;
import tucil_3_stima.strategy.GBFS;
import tucil_3_stima.utils.InputHandler;

public class InitController {
    // Buttons
    @FXML private Button backButton;
    @FXML private Button solveButton, saveButton; // Output button
    @FXML private MenuButton algorithmButton, heuristicButton; 
    @FXML private Button uploadButton, exampleButton; // Input button
    @FXML private Button prevButton, nextButton, speedButton; // Animation button
    @FXML private ToggleButton playButton; 
    
    // Panes
    @FXML private StackPane boardPane;
    
    // Labels
    @FXML private Label timeLabel, expNodeLabel, genNodeLabel, stepsLabel, loadingLabel;

    // Indicator
    @FXML private ProgressIndicator loadingIndicator;
    
    // Images
    @FXML private ImageView backgroundImageView;

    // Audioss
    private AudioClip clickSound, hoverSound, backSound;
    private MediaPlayer pageBgm;

    // datas
    private AbstractSearch strategy = null;
    private Heuristic heuristic = null;
    private Board board;
    private State startState;
    private SearchResult searchRes;
    private int curStep = 0;
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

        // Fonts
        Font impactedFont24 = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(), 24);
        if (impactedFont24 != null) {
            backButton.setFont(impactedFont24);
            exampleButton.setFont(impactedFont24);
            uploadButton.setFont(impactedFont24);
            solveButton.setFont(impactedFont24);
            algorithmButton.setFont(impactedFont24);
            heuristicButton.setFont(impactedFont24);
            saveButton.setFont(impactedFont24);
            prevButton.setFont(impactedFont24);
            playButton.setFont(impactedFont24);
            nextButton.setFont(impactedFont24);
            speedButton.setFont(impactedFont24);
        }

        // Effect
        applyHoverEffects(solveButton);
        applyHoverEffects(algorithmButton);
        applyHoverEffects(heuristicButton);
        applyHoverEffects(saveButton);
        applyHoverEffects(exampleButton);
        applyHoverEffects(uploadButton);
        applyHoverEffects(prevButton);
        applyHoverEffects(nextButton);
        applyHoverEffects(speedButton);
        applyHoverEffects(playButton);

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
        applyBackButtonEffects(backButton);

        // Buttons
        uploadButton.setOnAction(e -> { uploadBtnAction(); });
        exampleButton.setOnAction(e -> { exampleBtnAction(); });
        solveButton.setOnAction(e -> { solveBtnAction(); });
        saveButton.setOnAction(e -> { saveBtnAction(); });
        prevButton.setOnAction(e -> { prevBtnAction(); });
        nextButton.setOnAction(e -> { nextBtnAction(); });
        speedButton.setOnAction(e -> { speedBtnAction(); });
        playButton.setOnAction(e -> { playBtnAction(); });

        // Init buttons options (heuristic & algo buttons)
        // Algorithm button
        String[] algorithms = {"A*", "UCS", "GBFS"};
        for (String algo : algorithms) {
            Label label = new Label(algo);

            
            Platform.runLater(() -> {
                label.setMinWidth(algorithmButton.getWidth());
                label.setPrefWidth(algorithmButton.getWidth());
                label.setAlignment(Pos.CENTER);
            });

            label.setOnMouseClicked(e -> {
                algorithmButton.setText(algo);
                AbstractSearch s = null;

                if ("A*".equals(algo)) s = new AStar();
                else if ("UCS".equals(algo)) s = new UCS();
                else if ("GBFS".equals(algo)) s = new GBFS();

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
        String[] heuristics = {"Blocking", "Distance"};
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
                board = parseRes.getKey();
                startState = parseRes.getValue();
                if (board != null && startState != null) {
                    
                    StackPane boardView = BoardViewBuilder.buildBoardView(board, startState);
                    boardPane.getChildren().clear();
                    boardPane.getChildren().add(boardView);
                    StackPane.setAlignment(boardPane, Pos.CENTER);
                    
                    // set 
                    solveButton.setDisable(false);
                    saveButton.setDisable(false);
                    algorithmButton .setDisable(false);
                    heuristicButton.setDisable(false);
                    
                } 
            }   catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load configuration: " + ex.getMessage());
                alert.showAndWait();
                solveButton.setDisable(true);
                saveButton.setDisable(true);
                algorithmButton .setDisable(true);
                heuristicButton.setDisable(true);
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
                solveButton.setDisable(true);
                saveButton.setDisable(true);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"Example Format:\n\nBoard size and block amount: (rows cols amount)\nThen format specifier (DEFAULT or CUSTOM).\nIf custom, then followed by each row.\n\nThen each block\n\nSample:\n6 6\n11\nGBB.L.\nGHI.LM\nGHIPPMK\nCCCZ.M\n..JZDD\nEEJFF.\n");
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
                            curStep = 0;
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

                SequentialTransition seq = new SequentialTransition();
                if (solveTask.getValue()) {
                    playButton.setDisable(false);
                    prevButton.setDisable(false);
                    nextButton.setDisable(false);
                    speedButton.setDisable(false);


                    
                } else {
                    playButton.setDisable(true);
                    prevButton.setDisable(true);
                    nextButton.setDisable(true);
                    speedButton.setDisable(true);

                    Alert alert = new Alert(Alert.AlertType.ERROR, "No Solution Found");
                    alert.showAndWait();   
                }
                seq.play();
            });
            new Thread(solveTask).start();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No algorithm or heuristics chosen");
            alert.showAndWait();    
        }
    }

    private void saveBtnAction() {
        
    }

    private void prevBtnAction() {
        if (steps != null) {
            curStep = curStep - 1 >= 0 ? curStep - 1 : curStep;

            StackPane boardView = BoardViewBuilder.buildBoardView(board, steps.get(curStep));
            boardPane.getChildren().clear();
            boardPane.getChildren().add(boardView);
            StackPane.setAlignment(boardPane, Pos.CENTER);
        }
    }

    private void nextBtnAction() {
        if (steps != null) {
            curStep = curStep + 1 < steps.size() ? curStep + 1 : curStep;

            StackPane boardView = BoardViewBuilder.buildBoardView(board, steps.get(curStep));
            boardPane.getChildren().clear();
            boardPane.getChildren().add(boardView);
            StackPane.setAlignment(boardPane, Pos.CENTER);
        }
    }

    private void speedBtnAction() {

    }

    private void playBtnAction() {

    }

}
