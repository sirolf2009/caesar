package com.sirolf2009;

import com.sirolf2009.controller.ConnectionController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gridkit.lab.jvm.attach.AttachManager;
import org.gridkit.lab.jvm.attach.JavaProcessId;

import javax.management.MBeanServerConnection;

public class MainController {

    @FXML
    TreeView jvmTree;
    @FXML
    TabPane connections;

    @FXML
    public void initialize() {
        populateJVMTree();
    }

    private void populateJVMTree() {
        TreeItem<JavaProcessId> root = new TreeItem<>();
        AttachManager.listJavaProcesses().forEach(id -> {
            TreeItem<JavaProcessId> item = new TreeItem<>(id);
            root.getChildren().add(item);
        });
        jvmTree.setRoot(root);
        jvmTree.setShowRoot(false);
        jvmTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ContextMenu context = new ContextMenu();
        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(evt -> {
            TreeItem<JavaProcessId> item = (TreeItem<JavaProcessId>) jvmTree.getSelectionModel().getSelectedItems().get(0);
            connect(item.getValue());
        });
        context.getItems().add(connect);
        jvmTree.setContextMenu(context);
    }

    private void connect(JavaProcessId id) {
        MBeanServerConnection connection = AttachManager.getJmxConnection(id);
        Tab newConnectionTab = new Tab(id.getDescription(), new ConnectionController(connection));
        connections.getTabs().add(newConnectionTab);
    }
}
