package com.filemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/mainScene.fxml")));
        primaryStage.setTitle("File manager");
        Image logo = new Image("icons/program-logo.png");
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(new Scene(root, 700, 450));
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}