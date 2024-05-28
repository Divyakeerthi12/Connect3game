package com.divya.connect3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.lang.Exception;

import java.io.IOException;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader= new FXMLLoader(getClass().getResource("sample.fxml"));
        GridPane rootGridPane=loader.load();
        controller=loader.getController();
        controller.createPlayground();


        MenuBar menubar=createMenu();
        //to use entire width of menubar;
        menubar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane =(Pane)rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menubar);
        Scene scene =new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Three");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    private MenuBar createMenu()
    {
        //File Menu
        Menu filemenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());
        filemenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
        //Help Menu

        Menu helpmenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(event->aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event->aboutMe());
        helpmenu.getItems().addAll(aboutGame,separator,aboutMe);

        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(filemenu,helpmenu);
        return menubar;


    }

    private void aboutMe() {
        Alert alrt= new Alert(Alert.AlertType.INFORMATION);
        alrt.setTitle("About the Developer");
        alrt.setHeaderText("Divyakeerthi");
        alrt.setContentText("I love to play with code and create games");
        alrt.show();
    }

    private void aboutConnect4() {
        Alert alrt= new Alert(Alert.AlertType.INFORMATION);
        alrt.setTitle("About Connect 3");
        alrt.setHeaderText("How to Play?");
        alrt.setContentText("Connect three is a three-player connection game in which the players first" +
                " choose a color and then take turns dropping colored discs from the top " +
                "into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available " +
                "space within the column. The objective of the game is to be the first " +
                "to form a horizontal, vertical, or diagonal line of three of one's own discs." +
                " Connect Three is a solved game. The first player can always win by " +
                "playing the right moves.");
        alrt.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        //TODO
    }

    public static void main(String[] args) {
        launch();
    }
}