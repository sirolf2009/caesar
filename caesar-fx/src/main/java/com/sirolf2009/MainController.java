package com.sirolf2009;

import com.sirolf2009.caesar.server.JMXServer;
import com.sirolf2009.caesar.server.model.Attribute;
import com.sirolf2009.caesar.server.model.MBean;
import com.sirolf2009.component.Table;
import com.sirolf2009.component.VariablesTreeView;
import com.sirolf2009.controller.ConnectionView;
import com.sirolf2009.controller.VariableView;
import com.sirolf2009.model.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.gridkit.lab.jvm.attach.AttachManager;

import javax.management.MBeanServerConnection;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MainController {

    private final ObservableList<MBean> beans = FXCollections.observableArrayList();
    private final ObservableList<Attribute> attributes = FXCollections.observableArrayList();
    private final ObservableList<Table> tables = FXCollections.observableArrayList();

    @FXML
    TabPane master;
    @FXML
    Tab connectionsTab;
    @FXML
    ListView<Connection> connections;
    Map<Connection, ConnectionView> connectionViews = new HashMap<>();
    @FXML
    Tab variablesTab;
    VariablesTreeView variables;
    Map<Attribute, VariableView> variablesViews = new HashMap<>();
    @FXML
    Tab tablesTab;
    @FXML
    Tab chartsTab;
    @FXML
    Tab actionsTab;
    @FXML
    Tab rulesTab;
    @FXML
    Tab dashboardsTab;
    @FXML
    AnchorPane slave;
    @FXML
    AnchorPane properties;

    @FXML
    public void initialize() {
        variables = new VariablesTreeView(attributes);
        variablesTab.setContent(variables);

        List<SelectionModel> selectionModels = Arrays.asList(connections.getSelectionModel(), variables.getSelectionModel());
        Consumer<SelectionModel> clearOthers = model -> selectionModels.stream().filter(selectionModel -> selectionModel != model).forEach(selectionModel -> selectionModel.clearSelection());
        connections.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Connection>) c -> {
            Connection connection = connections.getSelectionModel().getSelectedItem();
            if(connection != null) {
                clearOthers.accept(connections.getSelectionModel());
                setProperties(getOrCreateConnectionView(connection));
            }
        });
        variables.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<Object>>) c -> {
            TreeItem selectedItem = variables.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
                Object selection = selectedItem.getValue();
                if (selection instanceof Attribute) {
                    clearOthers.accept(variables.getSelectionModel());
                    setProperties(getOrCreateVariableView((Attribute) selection));
                }
            }
        });

        slave.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != slave && event1.getDragboard().hasString()) {
                event1.acceptTransferModes(TransferMode.LINK);
                slave.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
            }
            event1.consume();
        });
        slave.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                slave.setStyle("");
                event.consume();
            }
        });
        slave.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                //FIXME just support one connection. Managing is insane
                JMXServer server = connections.getItems().get(0).getServer();
                server.getAttribute(event.getDragboard().getString()).ifPresent(attr -> {
                    Table table = new Table(server, attr);
                    slave.getChildren().add(table);
                    AnchorPane.setTopAnchor(table, 0d);
                    AnchorPane.setBottomAnchor(table, 0d);
                    AnchorPane.setRightAnchor(table, 0d);
                    AnchorPane.setLeftAnchor(table, 0d);
                    event.consume();
                });
            }
        });
    }

    @FXML
    private void handleNewConnection(ActionEvent event) {
        List<Supplier<MBeanServerConnection>> choices = new ArrayList<>();
        AttachManager.listJavaProcesses().stream().map(pid -> {
           return new Supplier<MBeanServerConnection>() {
               @Override
               public MBeanServerConnection get() {
                   return AttachManager.getJmxConnection(pid);
               }
               @Override
               public String toString() {
                   String fullString = pid.toString();
                   String cutString = fullString.substring(0, Math.min(fullString.length(), 64));
                   return fullString.equals(cutString) ? fullString : cutString+"...";
               }
           };
        }).forEach(supplier -> choices.add(supplier));
        ChoiceDialog<Supplier<MBeanServerConnection>> dialog = new ChoiceDialog<Supplier<MBeanServerConnection>>(choices.get(0), choices);
        dialog.setTitle("Open a connection");
        dialog.setHeaderText("Please select a JVM to connect to");
        dialog.showAndWait().ifPresent(supplier -> {
            Connection connection = new Connection(supplier.toString(), new JMXServer(UUID.randomUUID().toString(), supplier.get()));
            connections.getItems().add(connection);
            connection.getServer().getBeans().forEach(bean -> bean.getAttributes().forEach(attr -> attributes.add(attr)));
        });
    }

    @FXML
    private void addTable(ActionEvent event) {

    }

    public void setProperties(Node node) {
        properties.getChildren().clear();
        properties.getChildren().add(node);
    }

    public ConnectionView getOrCreateConnectionView(Connection connection) {
        if(!connectionViews.containsKey(connection)) {
            ConnectionView view = new ConnectionView(connection);
            connectionViews.put(connection, view);
            return view;
        } else {
            return connectionViews.get(connection);
        }
    }

    public VariableView getOrCreateVariableView(Attribute attribute) {
        if(!variablesViews.containsKey(attribute)) {
            VariableView view = new VariableView(attribute);
            variablesViews.put(attribute, view);
            return view;
        } else {
            return variablesViews.get(attribute);
        }
    }

}
