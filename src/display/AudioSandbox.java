package display;/**
 * Created by paul on 31/01/17.
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class AudioSandbox extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Audio Sandbox");

        Group root = new Group();
        int visWidth = 500;
        int visHeight = 500;
        Canvas visualizerCanvas = new VisualizerCanvas(visWidth,visHeight);


        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
