package me.seth.game;

import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

public class Main extends Application {

    private final Rectangle[] rectangle = new Rectangle[100];
    private final Random random = new Random();
    private final Pane root = new Pane();
    private Rectangle food;
    private boolean isOver = false;
    private final int[] x = new int[100];
    private final int[] y = new int[100];
    private int tailLength = 3;
    private int direction = 3;
    private int[] foodLocation, headLocation;
    private int grow = 0;

    private Rectangle initRect() {
        Rectangle res = new Rectangle(45, 45);
        res.setFill(Color.AQUAMARINE);
        res.setStroke(Color.BLACK);
        res.setVisible(false);
        return res;
    }

    public void start(Stage stage) {
        headLocation = new int[]{x[0], y[0]};
        Thread game;
        food = initRect();
        food.setStroke(Color.RED);
        food.setVisible(true);
        food.setTranslateX(random.nextInt(10) * 50);
        food.setTranslateY(random.nextInt(10) * 50);
        root.getChildren().add(food);
        Scene scene = new Scene(root, 514, 543);
        for (int i = 0; i < 3; i++) {
            rectangle[i] = initRect();
            rectangle[i].setTranslateX(50 + 50 * i);
            rectangle[i].setTranslateY(50 + 50);
            rectangle[i].setVisible(true);
            root.getChildren().add(rectangle[i]);
        }
        for (int i = 3; i < 100; i++) {
            rectangle[i] = initRect();
            rectangle[i].setTranslateX(50 + 50 * i);
            rectangle[i].setTranslateY(50 + 50);
            root.getChildren().add(rectangle[i]);
        }
        rectangle[0].setFill(Color.ANTIQUEWHITE);
        game = new Thread(() -> {
            while (!isOver) {
                movement();
                try {
                    sleep(200);
                } catch (Exception _) {
                }
            }
        });
        game.start();
        scene.setOnKeyPressed(event -> {
            KeyCode k = event.getCode();
            switch (k) {
                case D:
                    if (direction != 1 && ((direction == 2 || direction == 0) && headLocation[1] != y[0])) direction = 3;
                    break;
                case A:
                    if (direction != 3 && ((direction == 2 || direction == 0) && headLocation[1] != y[0])) direction = 1;
                    break;
                case S:
                    if (direction != 2 && ((direction == 3 || direction == 1) && headLocation[0] != x[0])) direction = 0;
                    break;
                case W:
                    if (direction != 0 && ((direction == 3 || direction == 1) && headLocation[0] != x[0])) direction = 2;
                    break;
            }
            headLocation = new int[]{x[0], y[0]};
        });
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMaxHeight(543);
        stage.setMaxWidth(514);
        stage.setOnCloseRequest(event -> isOver = true);
        stage.show();

    }

    private void movement() {
        int[][] tmp = {{x[1], y[1]}, {}};
        x[1] = x[0];
        y[1] = y[0];
        for (int i = 2; i < tailLength; i++) {
            tmp[1] = new int[]{x[i], y[i]};
            x[i] = tmp[0][0];
            y[i] = tmp[0][1];
            tmp[0] = tmp[1];
            if (grow > 0 && x[tailLength - 1] == foodLocation[0] && y[tailLength - 1] == foodLocation[1]) {
                rectangle[tailLength - 1].setVisible(true);
                --grow;
            }
        }
        switch (direction) {
            case 0:
                if (y[0] + 50 <= 450) y[0] += 50;
                else y[0] = 0;
                break;
            case 1:
                if (x[0] - 50 >= 0) x[0] -= 50;
                else x[0] = 450;
                break;
            case 2:
                if (y[0] - 50 >= 0) y[0] -= 50;
                else y[0] = 450;
                break;
            case 3:
                if (x[0] + 50 <= 450) x[0] += 50;
                else x[0] = 0;
                break;
        }
        if (intersects(x[0], y[0])) {
            System.out.print("GAME OVER");
            isOver = true;
        } else {
            for (int i = 0; i < tailLength; i++) {
                rectangle[i].setTranslateX(x[i]);
                rectangle[i].setTranslateY(y[i]);
            }
            if (rectangle[0].getBoundsInParent().intersects(food.getBoundsInParent())) {
                boolean isChanged = false;
                foodLocation = new int[]{(int) food.getTranslateX(), (int) food.getTranslateY()};
                while (!isChanged) {
                    food.setTranslateX(random.nextInt(10) * 50);
                    food.setTranslateY(random.nextInt(10) * 50);
                    if (intersects(foodLocation[0], foodLocation[1]) && (int) food.getTranslateX() != foodLocation[0] || (int) food.getTranslateY() != foodLocation[1])
                        isChanged = true;
                }
                x[tailLength] = (int) food.getTranslateX();
                y[tailLength] = (int) food.getTranslateY();
                rectangle[tailLength].setTranslateX(rectangle[tailLength - 1].getX());
                rectangle[tailLength].setTranslateY(rectangle[tailLength - 1].getY());
                ++tailLength;
                ++grow;
                System.out.println(tailLength + "");
            }
        }
    }

    public boolean intersects(int x, int y) {
        int i = 0;
        for (Rectangle part : rectangle) {
            if (part != rectangle[0] && i > 0 && part.isVisible() && x == this.x[i] && y == this.y[i]) {
                System.out.println(i);
                return true;
            }
            i++;
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
