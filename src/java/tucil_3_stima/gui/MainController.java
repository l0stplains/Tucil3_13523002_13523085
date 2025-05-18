package tucil_3_stima.gui;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MainController {
    @FXML
    private Button btnEnter;
    @FXML
    private Button btnAbout;
    @FXML
    private Button btnExit;
    @FXML
    private ImageView backgroundImageView;

    @FXML private Text mainTitle;

    private AudioClip clickSound;
    private AudioClip hoverSound;
    private AudioClip exitSound;

    @FXML
    public void initialize() {
        // Load and configure the background image
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

        // Initialize sound effects
        clickSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/click.wav").toExternalForm());
        clickSound.setVolume(0.05); // so damn loud Fuck u
        hoverSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hover.wav").toExternalForm());
        hoverSound.setVolume(0.03);
        exitSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/exit.mp3").toExternalForm());
        

        // Load the custom font
        Font impactedFont = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(), 24);
        if(impactedFont != null) {
            applyButtonFonts(btnAbout, impactedFont);
            applyButtonFonts(btnExit, impactedFont);
            applyButtonFonts(btnEnter, impactedFont);
        }
        Font impactedFont60 = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(), 60);
        if(impactedFont60 != null) {
            mainTitle.setFont(impactedFont60);
        }


        // Apply the same hover/click effects to all buttons
        applyHoverEffects(btnAbout);
        applyHoverEffects(btnExit);
        applyHoverEffects(btnEnter);

    }

    private void applyButtonFonts(Button button, Font font) {
        String fontFamily = font.getName(); // Check what the font's family name is.
        button.setFont(font);
        button.setStyle("-fx-font-family: '" + fontFamily + "'; -fx-font-size: 24px;");
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

        // On click: play click sound and log button text
        button.setOnAction(e -> {
            if (clickSound != null) {
                clickSound.play();
            }
        });
    }

    @FXML
    private void handleAbout(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tucil_3_stima/gui/about_menu.fxml"));
            Scene aboutScene = new Scene(loader.load(), btnAbout.getScene().getWidth(), btnAbout.getScene().getHeight());
            MainApp mainApp = (MainApp) btnAbout.getScene().getWindow().getUserData();
            mainApp.backgroundPlayer.stop();
            mainApp.switchScene(aboutScene);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEnter(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tucil_3_stima/gui/init_menu.fxml"));
            Scene aboutScene = new Scene(loader.load(), btnEnter.getScene().getWidth(), btnEnter.getScene().getHeight());
            MainApp mainApp = (MainApp) btnEnter.getScene().getWindow().getUserData();
            mainApp.backgroundPlayer.stop();
            mainApp.switchScene(aboutScene);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void handleExit(MouseEvent event) {
        MainApp mainApp = (MainApp) btnExit.getScene().getWindow().getUserData();
        mainApp.backgroundPlayer.stop();
        FadeTransition fade = new FadeTransition(Duration.seconds(3), btnExit.getScene().getRoot());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> System.exit(0));
        if (exitSound != null) {
            exitSound.play();

        }
        fade.play();
    }
}
