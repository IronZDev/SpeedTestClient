package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.Random;

public class Main extends Application {
    private TCPConnectionHandler tcpConnectionHandler;
    private UDPConnectionHandler udpConnectionHandler;
    private TextField serverIPText = new TextField("localhost");
    private TextField serverPortText = new TextField();
    private Button startBtn = new Button("Start");
    private Button stopBtn = new Button("Stop");
    private TextArea textArea = new TextArea();
    private Slider sizeSlider = new Slider(32, 64000, 128);
    private Text sizeText = new Text(Integer.toString((int)Math.round(sizeSlider.getValue())));
    private Integer currentSize = (int)Math.round(sizeSlider.getValue());

    @Override
    public void start(Stage primaryStage) {
        Main self = this;
        serverPortText.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        serverPortText.setText("3301");
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                byte[] data = generateData(currentSize);
                tcpConnectionHandler = new TCPConnectionHandler(self, serverIPText.getText(), serverPortText.getText(), data);
                udpConnectionHandler = new UDPConnectionHandler(self, serverIPText.getText(), serverPortText.getText(), data);
                startBtn.setDisable(true);
                stopBtn.setDisable(false);
            }
        });
        stopBtn.setDisable(true);
        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tcpConnectionHandler.disconnect();
                udpConnectionHandler.disconnect();
                stopBtn.setDisable(true);
                startBtn.setDisable(false);
            }
        });
        stopBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        sizeSlider.setMinorTickCount(100);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(32000);
        sizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                sizeText.setText(Integer.toString(new_val.intValue()));
                currentSize = new_val.intValue();
            }
        });


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.setHalignment(sizeText, HPos.CENTER);

        // Row one
        grid.add(serverIPText, 0, 0);
        grid.add(serverPortText, 1, 0);
        grid.add(startBtn, 2,0);
        grid.add(stopBtn, 3, 0);
        grid.add(sizeSlider, 0, 1, 3, 1);
        grid.add(sizeText, 3, 1);
        grid.add(textArea,0, 2, 4, 5);

        Scene scene = new Scene(grid, 500, 250);

        primaryStage.setTitle("Speed Test Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    private byte[] generateData(Integer size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return b;
    }

    void writeText(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(s+"\n");
            }
        });
    }

    void writeTextOnError(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(s+"\n");
                tcpConnectionHandler.disconnect();
                udpConnectionHandler.disconnect();
                startBtn.setDisable(false);
                stopBtn.setDisable(true);
            }
        });
    }
}