package tucil_3_stima.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;

public class AboutController {
    @FXML private Text aboutText;
    @FXML private Text aboutTitle;
    @FXML private Button backButton;
    @FXML private ImageView backgroundImageView;

    private AudioClip clickSound, hoverSound, backSound, miaw;
    private MediaPlayer pageBgm;

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
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), aboutText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        FadeTransition fadeInTitle = new FadeTransition(Duration.seconds(2), aboutTitle);
        fadeInTitle.setFromValue(0);
        fadeInTitle.setToValue(1);
        fadeInTitle.play();

        // Fonts
        Font impactedFont48 = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(), 48);
        Font impactedFont24 = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(), 24);
        backButton.setFont(impactedFont24);
        aboutText.setFont(impactedFont24);
        aboutTitle.setFont(impactedFont48);


        Media mediaBgm = new Media(getClass().getResource("/tucil_3_stima/gui/assets/slowBgm.mp3").toExternalForm());
        pageBgm = new MediaPlayer(mediaBgm);
        pageBgm.setCycleCount(MediaPlayer.INDEFINITE);
        pageBgm.setVolume(0.15);
        pageBgm.play();

        clickSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/click.wav").toExternalForm());
        backSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/back.wav").toExternalForm());
        hoverSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/hover.wav").toExternalForm());
        miaw = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/miaw.wav").toExternalForm());
        backButton.setOnAction(e -> {
            if(clickSound != null) clickSound.play();
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
        applyBackButtonEffects(backButton);
        aboutTitle.setOnMouseClicked(e -> miaw.play());
        aboutText.setWrappingWidth(800);
        aboutText.setText("YO ini aplikasi ini dibikin oleh \nRefki Alfarizi 13523002 dan Muhammad Jibril Ibrahim 13523085\n\nKite kite ngerjain app ini buat menuhin spesifikasi tucil 3 stima jirr lah");


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
}
