package com.sirolf2009.util;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ControllerUtil {

    public static void load(Object me, String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(me.getClass().getResource(fxml));
        fxmlLoader.setRoot(me);
        fxmlLoader.setController(me);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
