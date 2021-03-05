package JumpGame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class Main extends Application {
    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();//

    private ArrayList<Node> obstacles = new ArrayList<Node>();
    private ArrayList<Node> platforms = new ArrayList<Node>();//

    private Pane appRoot = new Pane();//
    private Pane uiRoot = new Pane();//
    private Pane gameRoot = new Pane();//

    private Node player;//make a player class if wanna implement better graphics
    private  Point2D playerVelocity = new Point2D(0, 0);//
    private boolean canJump = true;//

    private int levelWidth;//


    private Image image= new Image("JumpGame/gameOver.jpg", 1280, 720, true, true);
    private ImageView onDeath= new ImageView(image);

    private Image image1= new Image("JumpGame/wonGame.jpeg", 1280, 720, true, true);
    private ImageView onWin= new ImageView(image1);

    private Image image2= new Image("JumpGame/help.jpg", 1280, 720, true, true);
    private ImageView onStart= new ImageView(image2);

    private void end(ImageView iv){
        iv.setVisible(true);
        new TimedExit();

    }
    private void initContent() throws IOException{

        Image image1 = new Image("JumpGame/forest.jpg", 1280, 720, true, true);
        ImageView imageView = new ImageView(image1);


        levelWidth = LevelData.LEVEL1[0].length() * 60;

        for(int i = 0; i < LevelData.LEVEL1.length; i++){
            String line = LevelData.LEVEL1[i];

            for(int j = 0; j < line.length(); j++){
                switch (line.charAt(j)){
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j*60, i*60, 60, 60, Color.rgb(14, 23, 46) );
                        platforms.add(platform);
                        break;
                    case '2':
                        Node obstacle=createEntity(j * 60, i * 60, 60, 60, Color.rgb(232, 31, 0));
                        obstacles.add(obstacle);
                        break;
                }
            }
        }
        player = createEntity(0, 600, 40, 40, Color.rgb(16, 135, 76));

        player.translateXProperty().addListener((obs, old, newValue)->{
            int offset = newValue.intValue();

            if(offset > 640 && offset < levelWidth - 640){
                gameRoot.setLayoutX(-(offset-640));
            }
        });
        onDeath.setVisible(false);
        onWin.setVisible(false);
        appRoot.getChildren().addAll(imageView, gameRoot, uiRoot, onDeath, onWin, onStart);

        TimerTask exitHelp = new TimerTask() {
            public void run() {
                onStart.setVisible(false);
            }
        };
        new Timer().schedule(exitHelp, new Date(System.currentTimeMillis()+4*1000));

    }
    private void update(){
        if (player.getTranslateX() >=3540){
            end(onWin);
        }

        if(player.getTranslateY() > 720){
            end(onDeath);
            //How to implement code for being dead and restarting??
        }
        if (isPressed(KeyCode.SPACE) && player.getTranslateY() >= 5)
            jump();
        if (isPressed(KeyCode.LEFT) && player.getTranslateX() >= 5)
            moveForward(-5);
        if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 5)
            moveForward(5);

        if (playerVelocity.getY() < 10)
            playerVelocity = playerVelocity.add(0, 1);

        moveUp((int)playerVelocity.getY());
    }

    private void checkObstacles(){
        for (Node obstacle : obstacles){
            if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent()))
                end(onDeath);
        }
    }

    private void moveForward(int value){
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++){
            checkObstacles();
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX())
                            return;
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60)
                            return;
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
            //System.out.println(player.getTranslateY());
        }

    }
    private void moveUp(int value){
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++){
            checkObstacles();
            for (Node platform : platforms){
                if(player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60){
                            return;
                        }
                    }

                }

            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }
    private void jump(){
        if(canJump){
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;//no double jump
        }
    }
    private Node createEntity(int x, int y, int w, int h, Color color){
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.setStroke(Color.STEELBLUE);
        entity.setStrokeType(StrokeType.INSIDE);

        gameRoot.getChildren().add(entity);
        return entity;
    }
    private boolean isPressed(KeyCode key){
        return keys.getOrDefault(key, false);
    }
    public void start(Stage primaryStage) throws IOException {
        initContent();//
        Scene scene = new Scene(appRoot);//
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));//
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));//
        primaryStage.setTitle("Jump Game");//
        primaryStage.setScene(scene);//
        primaryStage.show();//

        AnimationTimer timer = new AnimationTimer() {//
            @Override
            public void handle(long now){
                    update();
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/*
* package JumpGame;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application {
    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();//

    private ArrayList<Node> platforms = new ArrayList<Node>();//

    private Pane appRoot = new Pane();//
    private Pane uiRoot = new Pane();//
    private Pane gameRoot = new Pane();//

    private Node player;//make a player class if wanna implement better graphics
    private  Point2D playerVelocity = new Point2D(0, 0);//
    private boolean canJump = true;//

    private int levelWidth;//

    private boolean dailogEvent = false, running = true;

    private void initContent() throws IOException{

        //Rectangle bg = new Rectangle(1280, 720);

        FileInputStream input = new FileInputStream("C:\\Users\\Mahesh\\IdeaProjects\\JadeJump\\src\\JumpGame\\forest.jpg");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);

        levelWidth = LevelData.LEVEL1[0].length() * 60;

        for(int i = 0; i < LevelData.LEVEL1.length; i++){
            String line = LevelData.LEVEL1[i];

            for(int j = 0; j < line.length(); j++){
                switch (line.charAt(j)){
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j*60, i*60, 60, 60, Color.BLUE );
                        platforms.add(platform);
                        break;
                }
            }
        }
        player = createEntity(0, 600, 40, 40, Color.RED);

        player.translateXProperty().addListener((obs, old, newValue)->{
            int offset = newValue.intValue();

            if(offset > 640 && offset < levelWidth - 640){
                gameRoot.setLayoutX(-(offset-640));
            }
        });

        appRoot.getChildren().addAll(imageView, gameRoot, uiRoot);
    }
    private void update(){
        if (isPressed(KeyCode.SPACE) && player.getTranslateY() >= 5)
            jump();
//        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5)
//            moveForward(-5);
        if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 5)
            moveForward(5);

        if (playerVelocity.getY() < 10)
            playerVelocity = playerVelocity.add(0, 1);

        moveUp((int)playerVelocity.getY());

        for (Node obstacle : obstacles){
            if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())){
                obstacle.getProperties().put("alive", false);
                dailogEvent  = true;
                running = false;
            }
        }
        for (Iterator<Node> it = obstacles.iterator(); it.hasNext();){
            Node obstacle = it.next();
            if (!(Boolean)obstacle.getProperties().get("alive")){
                it.remove();
                gameRoot.getChildren().remove(obstacle);
            }
        }
    }
    private void moveForward(int value){
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++){
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX())
                            return;
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60)
                            return;
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }
    private void moveUp(int value){
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++){
            for (Node platform : platforms){
                if(player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60){
                            return;
                        }
                    }

                }

            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }
    private void jump(){
        if(canJump){
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;//no double jump
        }
    }
    private Node createEntity(int x, int y, int w, int h, Color color){
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);

        gameRoot.getChildren().add(entity);
        return entity;
    }
    private boolean isPressed(KeyCode key){
        return keys.getOrDefault(key, false);
    }
    public void start(Stage primaryStage) throws IOException {
        initContent();//
        Scene scene = new Scene(appRoot);//
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));//
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));//
        primaryStage.setTitle("Jump Game");//
        primaryStage.setScene(scene);//
        primaryStage.show();//

        AnimationTimer timer = new AnimationTimer() {//
            @Override
            public void handle(long now){
                    update();
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/