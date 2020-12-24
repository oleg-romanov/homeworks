package ru.itis.base;

import ru.itis.gameobjects.Tank;
import ru.itis.gameobjects.common.Destroyable;
import ru.itis.sockets.ReceiveMessageTask;
import ru.itis.sockets.SocketClient;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Game {

    private final List<GameObject> gameObjects = new ArrayList<>();

    public static Game instance = new Game();

    private Pane pane;

     SocketClient client;

    private long fps;

    private Game() {}

    public void play() {
        client = new SocketClient("localhost", 7777);
        // запускаем слушателя сообщений
        ReceiveMessageTask receiveMessageTask = new ReceiveMessageTask(client.getFromServer());
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(receiveMessageTask);

        new JavaFxApp().launchApp();
    }

    public void startTimer() {
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        animationTimer.start();
    }

    public void setGamingPane(Pane pane) {
        this.pane = pane;
    }

    public Pane getGamingPane() {
        return pane;
    }

    public void addGameObject(GameObject gameObject) {
        Platform.runLater(() -> {
            gameObjects.add(gameObject);
            pane.getChildren().add(gameObject);
        });
    }

    public void removeGameObject(GameObject gameObject) {
        Platform.runLater(() -> {
            gameObjects.remove(gameObject);
            pane.getChildren().remove(gameObject);
        });
    }

    public List<GameObject> findGameObjectsByName(String key) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getKey().contains(key)) {
                result.add(gameObject);
            }
        }
        return result;
    }

    public List<GameObject> getAllCollision() {
        return gameObjects
                .stream()
                .filter(item -> item.isCollision)
                .collect(Collectors.toList());
    }

    public List<GameObject> getAllDestroyable() {
        return gameObjects
                .stream()
                .filter(item -> item instanceof Destroyable)
                .collect(Collectors.toList());
    }


    public void update() {
        if (System.currentTimeMillis() - fps > 32) {
            for (GameObject gameObject : gameObjects) {
                gameObject.update();
                if (gameObject instanceof Tank) {
                    client.sendTankPosition(gameObject.getLayoutX(), gameObject.getLayoutY());
                }
            }
            fps = System.currentTimeMillis();
        }
    }
}
