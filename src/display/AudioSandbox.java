package display;/**
 * Created by paul on 31/01/17.
 */

import audio.file.WaveFile;
import effects.TurbineEffect;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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
        VisualizerCanvas visualizerCanvas = new VisualizerCanvas(visWidth,visHeight);
        
        root.getChildren().add(visualizerCanvas);
    
        try {
            String filePath = "resources\\8600Hz.wav";
            File file = new File(filePath);
            WaveFile waveFile = new WaveFile(file);
    
            int chunkSize = 32;
            int channel = 1;
            int freqCount = 1;
    
            TurbineEffect effect = new TurbineEffect(waveFile, chunkSize, channel, freqCount);
    
            visualizerCanvas.play(effect);
    
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
