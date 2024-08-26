package com.example.jobtracker;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class InputManager {
    public static InputManager instance;
    public SimpleBooleanProperty control = new SimpleBooleanProperty();

    public InputManager(Scene scene) {
        instance = this;

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown())
                    control.setValue(true);
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!event.isControlDown())

                    control.setValue(false);
            }
        });
    }
}
