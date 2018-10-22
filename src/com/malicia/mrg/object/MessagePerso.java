package com.malicia.mrg.object;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class MessagePerso
{
    private static StackPane root;
    private static Text text;
    private static Stage messageStage;
    private static Scene scene;

    public static void addText(String messageMsg)
    {
        text.setText(text.getText() + "\\r\\n" + messageMsg  );
    }
    public static void makeText(Stage ownerStage, String messageMsg) {
        messageStage = new Stage();
        messageStage.initOwner(ownerStage);
        messageStage.setResizable(false);
        messageStage.initStyle(StageStyle.TRANSPARENT);

        text = new Text(messageMsg);
        text.setFont(Font.font("Verdana", 12));
        text.setFill(Color.BLACK);

        root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 50px;");
        root.setOpacity(0);

        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        messageStage.setScene(scene);
        messageStage.show();

    }
    public static void killText() {
//        messageStage.close();
    }

}