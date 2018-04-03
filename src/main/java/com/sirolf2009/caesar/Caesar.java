package com.sirolf2009.caesar;

import com.sirolf2009.caesar.dialogs.LocalConnectionDialog;
import com.sirolf2009.caesar.util.JMXUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class Caesar extends Application {

    private static final Logger log = LoggerFactory.getLogger(Caesar.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/main.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        MainController controller = loader.getController();
        Map<String, String> params = getParameters().getNamed();
        if(params.containsKey("host") && params.containsKey("port")) {
            controller.setConnection(new Connection(JMXUtil.fromHostAndPort(params.get("host"), Integer.parseInt(params.get("port")))));
        } else {
            controller.setConnection(new Connection(LocalConnectionDialog.getLocalConnectionsDialog().showAndWait().get().get()));
        }
        controller.queryAttributes();

        if(params.containsKey("file")) {
            controller.load(new File(params.get("file")));
        }

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Caesar");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void launch(int port, File file) {
        Application.launch(Caesar.class, "--host=localhost", "--port="+port, "--file="+file);
    }

}
