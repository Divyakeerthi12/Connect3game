package com.divya.connect3;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

//import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.geometry.Point2D;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;

    private static final String disc_color_one = "#F6BE00";
    private static final String disc_color_two = "#154734";
    private static final String disc_color_three = "#FF0000";
    private static String PLAYER_ONE = "Player one";
    private static String PLAYER_TWO = "Player two";
    private static String PLAYER_THREE = "Player three";

    private boolean isPlayerOneTurn = true;
    private int currentPlayerIndex = 0;

    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];//For structural changes.

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane InsertedDiscsPane;

    @FXML
    public Label PlayerNameLabel = new Label("Default Text");

    @FXML
    public TextField playerOneTextField;

    public TextField playerTwoTextField;

    public TextField playerThreeTextField;

    @FXML
    public Button setNamesButton = new Button();

    private boolean isAllowedToInsert = true;

    public void createPlayground() {
        // Rectangle rectangleWithHoles = new Rectangle(COLUMNS*CIRCLE_DIAMETER,ROWS*CIRCLE_DIAMETER); // Example of initialization

        Platform.runLater(() -> setNamesButton.requestFocus());

        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);
        List<Rectangle> rectangleList = createClickableColumns();
        for (Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }
        setNamesButton.setOnAction(event -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
            PLAYER_THREE = playerThreeTextField.getText();
            PlayerNameLabel.setText(getPlayerName(currentPlayerIndex));
        });
    }

    private Shape createGameStructuralGrid() {
        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);
                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumns() {
        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if (isAllowedToInsert) {
                    isAllowedToInsert = false;

                    insertDisc(new Disc(currentPlayerIndex), column);
                }
            });
            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {
        int row = ROWS - 1;
        while (row >= 0) {
            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }
        if (row < 0) {
            return;
        }
        insertedDiscsArray[row][column] = disc;//structural changes for the developers
        InsertedDiscsPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;

            if (gameEnded(currentRow, column)) {
                gameOver();
                return;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % 3;
            PlayerNameLabel.setText(getPlayerName(currentPlayerIndex));
        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(r, column))
                .collect(Collectors.toList());
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        // Corrected diagonal points generation
        List<Point2D> diagonal1Points = new ArrayList<>();
        List<Point2D> diagonal2Points = new ArrayList<>();
        for (int i = -3; i <= 3; i++) {
            diagonal1Points.add(new Point2D(row + i, column + i));
            diagonal2Points.add(new Point2D(row + i, column - i));
        }

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;
        for (Point2D point : points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();
            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
            if (disc != null && disc.isPlayerOneMove == (currentPlayerIndex == 0)) {
                chain++;
                if (chain == 3) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;
    }


    private Disc getDiscIfPresent(int row, int column) {
        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0) {
            return null;
        }
        return insertedDiscsArray[row][column];
    }

    private String getPlayerName(int index) {
        switch (index) {
            case 0:
                return PLAYER_ONE;
            case 1:
                return PLAYER_TWO;
            case 2:
                return PLAYER_THREE;
            default:
                return "";
        }
    }

    private void gameOver() {
        String winner = getPlayerName(currentPlayerIndex);
        System.out.println("Winner is " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Three");
        alert.setHeaderText("Congratulations !! " + winner + " is the winner");
        alert.setContentText("Do you want to play again?");
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
                resetGame();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        InsertedDiscsPane.getChildren().clear();
        for (int row = 0; row < insertedDiscsArray.length; row++) {
            for (int col = 0; col < insertedDiscsArray[row].length; col++) {
                insertedDiscsArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true;
        currentPlayerIndex = 0;
        PlayerNameLabel.setText(getPlayerName(currentPlayerIndex));
        createPlayground();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private static class Disc extends Circle {
        private final boolean isPlayerOneMove;

        public Disc(int playerIndex) {
            this.isPlayerOneMove = playerIndex == 0;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(disc_color_one) : (playerIndex == 1 ? Color.valueOf(disc_color_two) : Color.valueOf(disc_color_three)));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }
}
