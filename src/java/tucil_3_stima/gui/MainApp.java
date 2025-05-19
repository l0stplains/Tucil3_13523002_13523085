package tucil_3_stima.gui;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This GUI app is heavily inspired by Tetr.io and osu!
 * Props to them for making such a recognizable weeb and arcade style
 *
 * i'm not a weeb btw
 */
public class MainApp extends Application {
    private Stage primaryStage;
    private Scene mainScene;
    public MediaPlayer backgroundPlayer;
    private AudioClip startSound;
    private static final String STYLESHEET = "/tucil_3_stima/gui/assets/styles.css";

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/tucil_3_stima/gui/assets/rushhourlogo.png")));
        primaryStage = stage;

        startSound = new AudioClip(getClass().getResource("/tucil_3_stima/gui/assets/start.mp3").toExternalForm());
        startSound.setVolume(200);

        // Load custom font from resources
        Font customFont = Font.loadFont(getClass().getResource("/tucil_3_stima/gui/assets/impacted.ttf").toExternalForm(),48);


        // welcome text
        Text splashText = new Text("Welcome to\n" + "Rush Hour Puzzle Solver");
        splashText.setFont(customFont);
        splashText.getStyleClass().add("my-text");

        splashText.setStyle("-fx-fill: white;");
        splashText.setTextAlignment(TextAlignment.CENTER);
        splashText.setOpacity(0); // Start fully transparent

        // Set up splash layout with a dark background
        StackPane splashLayout = new StackPane(splashText);
        StackPane.setAlignment(splashText, Pos.CENTER); // CENTER TEXT
        splashLayout.setStyle("-fx-background-color: black;");
        Scene splashScene = new Scene(splashLayout, 1200, 675);
        stage.setMinWidth(1200);
        stage.centerOnScreen();
        stage.setScene(splashScene);
        stage.setTitle("Loading...");
        stage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), splashText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), splashText);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2)); // Hold for 2 seconds before fading out

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> loadMainScene(stage));

        if (startSound != null) startSound.play();

        fadeIn.play();
    }

    private void loadMainScene(Stage stage) {
        try {
            // Load main FXML scene
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/tucil_3_stima/gui/main_menu.fxml"));
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            mainScene = new Scene(fxmlLoader.load(), currentWidth, currentHeight);
            mainScene.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
            stage.setTitle("Rush Hour Solver");
            stage.setScene(mainScene);
            stage.getScene().getWindow().setUserData(this); // for scene changing, keep same user data

            // Initialize and play background music
            initAudio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Called by controllers to switch scenes
    public void switchScene(Scene scene) {
        scene.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setUserData(this);
    }

    private void initAudio() {
        try {
            // Load background music file from resources
            String bgMusicPath = getClass().getResource("/tucil_3_stima/gui/assets/bgm.mp3").toExternalForm();
            Media bgMedia = new Media(bgMusicPath);
            backgroundPlayer = new MediaPlayer(bgMedia);
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.setVolume(0.15);
            backgroundPlayer.play();
        } catch (Exception e) {
            System.out.println("Error loading background music: " + e.getMessage());
        }
    }

    public static void show(){
        launch();
    }
}
