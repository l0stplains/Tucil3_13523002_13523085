package tucil_3_stima.gui;

import java.io.File;
import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    @FXML private Label timeLabel, expNodeLabel, genNodeLabel, stepsLabel;
    
    // Images
    @FXML private ImageView backgroundImageView;

    // Audioss
    private AudioClip clickSound, hoverSound, backSound;
    private MediaPlayer pageBgm;

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

        // BGM
        Media mediaBgm = new Media(getClass().getResource("/tucil_3_stima/gui/assets/slowBgm.mp3").toExternalForm());
        pageBgm = new MediaPlayer(mediaBgm);
        pageBgm.setCycleCount(MediaPlayer.INDEFINITE);
        pageBgm.setVolume(0.15);
        pageBgm.play();

        clickSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/click.wav").toExternalForm());
        clickSound.setVolume(0.05);
        hoverSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hover.wav").toExternalForm());
        hoverSound.setVolume(0.03);
        backSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/back.wav").toExternalForm());
        backSound.setVolume(0.05);
        applyBackButtonEffects(backButton);


        // Upload button
        uploadButton.setOnAction(e -> {
            if(clickSound != null) clickSound.play();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Puzzle Configuration");

            // Set file extension filter to only allow .txt files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
            if (file != null) {
                /*
                try {

                    // TODO (@BoredAngel): parsing n masukin hasil

                }   catch (IOException ex) {
                    heheSound.play();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load configuration: " + ex.getMessage());
                    alert.showAndWait();
                } catch (IllegalArgumentException ex) {
                    bakaSound.play();
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.showAndWait();
                } */
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No file selected.");
                alert.showAndWait();
            }
        });

        // Example Format button
        exampleButton.setOnAction(e -> {
            if(clickSound != null) clickSound.play();
            // heck yeah i type it manually
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Example Format:\n\nBoard size and block amount: (rows cols amount)\nThen format specifier (DEFAULT or CUSTOM).\nIf custom, then followed by each row.\n\nThen each block\n\nSample:\n3 3 2\nDEFAULT\nA\nAA\n BB\n  B\nBBB\n\n5 7 5\nCUSTOM\n...X...\n.XXXXX.\nXXXXXXX\n.XXXXX.\n...X...\nA\nAAA\nBB\nBBB\nCCCC\n C\nD\nEEE\nE"
            );
            alert.setTitle("Example Configuration Format");
            alert.showAndWait();
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
}
