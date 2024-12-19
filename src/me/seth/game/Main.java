package me.seth.game;

import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

/**
 * This is a simple snake game.
 * <p>
 * ICT 002 1ST MINI PERFORMANCE TASK
 * <p>
 *
 */
public class Main extends Application {

    // Array to store the snake's body parts (rectangles)
    private final Rectangle[] rectangle = new Rectangle[100];
    private final Random random = new Random(); // Random generator for food location
    private final Pane root = new Pane(); // Root layout for the game
    private Rectangle food; // Rectangle representing the food
    private boolean isOver = false; // Game-over flag
    private final int[] x = new int[100]; // X-coordinates for each body part
    private final int[] y = new int[100]; // Y-coordinates for each body part
    private int tailLength = 3; // Initial length of the snake's tail
    private int direction = 3; // Snake's movement direction (0 = Down, 1 = Left, 2 = Up, 3 = Right)
    private int[] foodLocation, headLocation; // Track the food and head locations
    private int grow = 0; // Number of segments to grow
    private Text scoreText;
    private Text controlsText;

    // Draws a rectangle with default styling
    private Rectangle initRect() {
        Rectangle res = new Rectangle(45, 45); // Create a square with 45x45 size
        res.setFill(Color.AQUAMARINE); // Set fill color
        res.setStroke(Color.BLACK); // Set border color
        res.setVisible(false); // Initially invisible
        return res;
    }
    @Override
    public void start(Stage stage) {
        resetGame(stage); // Initialize the game

        stage.setTitle("Snake");
        stage.setResizable(false);
        stage.setMaxHeight(543);
        stage.setMaxWidth(514);
        stage.setOnCloseRequest(event -> isOver = true); // Stop the game on close
        stage.show();
    }

    // Method to reset and start the game
    private void resetGame(Stage stage) {
        Pane newRoot = new Pane(); // Create a new root pane
        isOver = false; // Reset game-over flag
        tailLength = 3; // Reset snake length
        direction = 3; // Reset direction
        grow = 0; // Reset growth
        for (int i = 0; i < 100; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        food = initRect(); // Initialize the food
        food.setStroke(Color.RED);
        food.setVisible(true);
        food.setTranslateX(random.nextInt(10) * 50);
        food.setTranslateY(random.nextInt(10) * 50);
        newRoot.getChildren().add(food);

        // Display text for keyboard controls
        controlsText = new Text("Controls: W (Up), A (Left), S (Down), D (Right), Q (Restart)");
        controlsText.setFill(Color.BLACK);
        controlsText.setStyle("-fx-font-size: 14px;");
        controlsText.setTranslateX(10);
        controlsText.setTranslateY(20);
        newRoot.getChildren().add(controlsText);

        // Display text for score
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.BLACK);
        scoreText.setStyle("-fx-font-size: 14px;");
        scoreText.setTranslateX(10);
        scoreText.setTranslateY(40);
        newRoot.getChildren().add(scoreText);

        // Initialize the snake
        for (int i = 0; i < 3; i++) {
            rectangle[i] = initRect();
            rectangle[i].setTranslateX(50 + 50 * i);
            rectangle[i].setTranslateY(50 + 50);
            rectangle[i].setVisible(true);
            newRoot.getChildren().add(rectangle[i]);
        }

        for (int i = 3; i < 100; i++) {
            rectangle[i] = initRect();
            rectangle[i].setTranslateX(50 + 50 * i);
            rectangle[i].setTranslateY(50 + 50);
            newRoot.getChildren().add(rectangle[i]);
        }

        rectangle[0].setFill(Color.ANTIQUEWHITE);

        Scene newScene = new Scene(newRoot, 514, 543);
        stage.setScene(newScene); // Set the new scene to the stage

        Thread game = new Thread(() -> {
            while (!isOver) {
                movement();
                try {
                    sleep(200);
                } catch (Exception ignored) {
                }
            }
            displayGameOver(newRoot); // Show Game Over screen when game ends
        });
        game.start();

        // Handle keyboard input
        newScene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case D:
                    if (direction != 1) direction = 3;
                    break;
                case A:
                    if (direction != 3) direction = 1;
                    break;
                case S:
                    if (direction != 2) direction = 0;
                    break;
                case W:
                    if (direction != 0) direction = 2;
                    break;
                case Q: // Restart game
                    resetGame(stage);
                    break;
            }
            headLocation = new int[]{x[0], y[0]};
        });
    }

    // Method to display the Game Over screen
    private void displayGameOver(Pane newRoot) {
        Platform.runLater(() -> {
            newRoot.getChildren().clear(); // Clear all elements
            Text gameOverText = new Text("GAME OVER");
            gameOverText.setFill(Color.RED);
            gameOverText.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
            gameOverText.setTranslateX(150);
            gameOverText.setTranslateY(200);

            Text finalScoreText = new Text("Your Score: " + (tailLength - 3));
            finalScoreText.setFill(Color.BLACK);
            finalScoreText.setStyle("-fx-font-size: 24px;");
            finalScoreText.setTranslateX(170);
            finalScoreText.setTranslateY(250);

            Text restartText = new Text("Press Q to Restart");
            restartText.setFill(Color.BLACK);
            restartText.setStyle("-fx-font-size: 18px;");
            restartText.setTranslateX(180);
            restartText.setTranslateY(300);

            newRoot.getChildren().addAll(gameOverText, finalScoreText, restartText);
        });
    }


    // Handles snake movement and game logic
    private void movement() {
        int[][] tailPosition = {{x[1], y[1]}, {}}; // Temporary storage for tail positions
        x[1] = x[0]; // Move first segment to the head's position
        y[1] = y[0];

        // Move other tail segments
        for (int i = 2; i < tailLength; i++) {
            tailPosition[1] = new int[]{x[i], y[i]};
            x[i] = tailPosition[0][0];
            y[i] = tailPosition[0][1];
            tailPosition[0] = tailPosition[1];
            if (grow > 0 && x[tailLength - 1] == foodLocation[0] && y[tailLength - 1] == foodLocation[1]) {
                rectangle[tailLength - 1].setVisible(true); // Grow tail
                --grow;
            }
        }

        // Update head position based on direction
        switch (direction) {
            case 0 -> y[0] = (y[0] + 50 <= 450) ? y[0] + 50 : 0; // Move down, wrap if needed
            case 1 -> x[0] = (x[0] - 50 >= 0) ? x[0] - 50 : 450; // Move left, wrap if needed
            case 2 -> y[0] = (y[0] - 50 >= 0) ? y[0] - 50 : 450; // Move up, wrap if needed
            case 3 -> x[0] = (x[0] + 50 <= 450) ? x[0] + 50 : 0; // Move right, wrap if needed
        }

        // Check for collisions with the tail
        if (selfCollision(x[0], y[0])) {
            System.out.print("GAME OVER");
            isOver = true;
        } else {
            // Update position of all visible rectangles
            for (int i = 0; i < tailLength; i++) {
                rectangle[i].setTranslateX(x[i]);
                rectangle[i].setTranslateY(y[i]);
            }

            // Check if food is eaten
            if (rectangle[0].getBoundsInParent().intersects(food.getBoundsInParent())) {
                boolean isChanged = false;
                foodLocation = new int[]{(int) food.getTranslateX(), (int) food.getTranslateY()};
                while (!isChanged) {
                    food.setTranslateX(random.nextInt(10) * 50); // New random location
                    food.setTranslateY(random.nextInt(10) * 50);
                    if (!selfCollision((int) food.getTranslateX(), (int) food.getTranslateY())) {
                        isChanged = true;
                    }
                }
                // Update the snake to grow
                x[tailLength] = (int) food.getTranslateX();
                y[tailLength] = (int) food.getTranslateY();
                ++tailLength;
                ++grow;
                System.out.println(tailLength + ""); // Log current tail length
                scoreText.setText("Score: " + (tailLength - 3));  // Update the score when food is eaten

            }
        }
    }

    // Check if a coordinate intersects with any snake segment
    public boolean selfCollision(int x, int y) {
        for (int i = 1; i < tailLength; i++) {
            if (rectangle[i].isVisible() && this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args); // Launch the Game
    }
}
