package com.example.projectwithgui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class JavaFXGame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameScene gameScene = new GameScene();
        Scene scene = new Scene(gameScene, 800, 600);
        primaryStage.setTitle("Professional JavaFX Platformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        gameScene.startGame();

        // Set focus to detect key events
        scene.setOnKeyPressed(gameScene::handleKeyPress);
    }

    class GameScene extends Pane {
        private Player player;
        private Obstacle obstacle;
        private Coin coin;
        private int score = 0;
        private AnimationTimer gameLoop;
        private Text scoreText;
        private MediaPlayer backgroundMusic;

        public GameScene() {
            setPrefSize(800, 600);

            // Load background image
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/background.png")));
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(800);
            background.setFitHeight(600);
            getChildren().add(background);

            // Create player
            player = new Player(50, 500);
            getChildren().add(player);

            // Create obstacle
            obstacle = new Obstacle(700, 500);
            getChildren().add(obstacle);

            // Create coin
            coin = new Coin(400, 300);
            getChildren().add(coin);

            // Create score display
            scoreText = new Text(10, 30, "Score: 0");
            scoreText.setFont(Font.font("Arial", 24));
            scoreText.setFill(Color.WHITE);
            getChildren().add(scoreText);

            // Load background music
            Media media = new Media(new File("src/main/resources/sounds/background.mp3").toURI().toString());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.play();
        }

        public void startGame() {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    update();
                }
            };
            gameLoop.start();
        }

        private void update() {
            // Move obstacle
            obstacle.move();

            // Check for collisions
            if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                System.out.println("Game Over! Score: " + score);
                gameLoop.stop();
                backgroundMusic.stop();
            }

            if (player.getBoundsInParent().intersects(coin.getBoundsInParent())) {
                score++;
                scoreText.setText("Score: " + score);
                coin.relocate(Math.random() * 700, Math.random() * 500);
            }

            player.update();
        }

        public void handleKeyPress(javafx.scene.input.KeyEvent e) {
            player.handleKeyPress(e.getCode());
        }
    }

    class Player extends ImageView {
        private double velocityY = 0;
        private boolean canJump = true;

        public Player(double x, double y) {
            super(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png"))));
            setX(x);
            setY(y);
        }

        public void update() {
            // Apply gravity
            velocityY += 0.5;
            setY(getY() + velocityY);

            // Ground collision
            if (getY() >= 500) {
                setY(500);
                velocityY = 0;
                canJump = true;
            }
        }

        public void handleKeyPress(KeyCode key) {
            if (key == KeyCode.SPACE && canJump) {
                velocityY = -15; // Jump
                canJump = false;
            }
            if (key == KeyCode.RIGHT) {
                setX(getX() + 5);
            }
            if (key == KeyCode.LEFT) {
                setX(getX() - 5);
            }
        }
    }

    class Obstacle extends ImageView {
        public Obstacle(double x, double y) {
            super(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/obstacle.png"))));
            setX(x);
            setY(y);
        }

        public void move() {
            setX(getX() - 3); // Move left
            if (getX() < -50) {
                setX(800); // Reset to the right side
            }
        }
    }

    class Coin extends ImageView {
        public Coin(double x, double y) {
            super(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/coin.png"))));
            setX(x);
            setY(y);
        }
    }
}