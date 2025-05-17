package tucil_3_stima.gui;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class InitController {
    @FXML private Button uploadButton;
    @FXML private Button backButton;
    @FXML private Button exampleButton;
    @FXML private ScrollPane boardScrollPane;
    @FXML private GridPane boardGrid;
    @FXML private ScrollPane blockScrollPane;
    @FXML private VBox blockListVBox;
    @FXML private ImageView backgroundImageView;
    @FXML private ListView<String> puzzleListView;  // puzzle file list on the left

    private AudioClip clickSound, hoverSound, backSound, bakaSound, heheSound;
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
        backButton.setFont(impactedFont24);
        exampleButton.setFont(impactedFont24);
        uploadButton.setFont(impactedFont24);

        // Effect
        applyHoverEffects(exampleButton);
        applyHoverEffects(uploadButton);

        // BGM
        Media mediaBgm = new Media(getClass().getResource("/tucil_3_stima/gui/assets/slowBgm.mp3").toExternalForm());
        pageBgm = new MediaPlayer(mediaBgm);
        pageBgm.setCycleCount(MediaPlayer.INDEFINITE);
        pageBgm.setVolume(0.15);
        pageBgm.play();

        clickSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/click.wav").toExternalForm());
        hoverSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hover.wav").toExternalForm());
        backSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/back.wav").toExternalForm());
        bakaSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/baka.wav").toExternalForm());
        heheSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hehe.wav").toExternalForm());
        applyBackButtonEffects(backButton);


        // On puzzleListView item click -> load that puzzle
        puzzleListView.setOnMouseClicked(e -> {
            String selected = puzzleListView.getSelectionModel().getSelectedItem();
            if(selected != null) {
            }
        });

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

    private void applyHoverEffects(Button button) {
        // Set the default opacity
        button.setOpacity(0.85);

        // Mouse Enter: shift left, scale up, and fade to full opacity
        button.setOnMouseEntered(e -> {
            if (hoverSound != null) {
                hoverSound.play();
            }

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.05);
            scale.setToY(1.05);

            FadeTransition fade = new FadeTransition(Duration.millis(100), button);
            fade.setToValue(1.0); // fully opaque

            ParallelTransition pt = new ParallelTransition(scale, fade);
            pt.play();
        });

        // Mouse Exit: reset translate, scale, and opacity
        button.setOnMouseExited(e -> {

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.0);
            scale.setToY(1.0);

            FadeTransition fade = new FadeTransition(Duration.millis(200), button);
            fade.setToValue(0.85); // back to default

            ParallelTransition pt = new ParallelTransition(scale, fade);
            pt.play();
        });

        button.setOnAction(e -> {
            if (clickSound != null) {
                clickSound.play();
            }
        });
    }
}
