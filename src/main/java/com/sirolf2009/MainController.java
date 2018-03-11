package com.sirolf2009;

import com.sirolf2009.component.ChartTab;
import com.sirolf2009.component.TableTab;
import com.sirolf2009.component.TablesTreeView;
import com.sirolf2009.component.VariablesTreeView;
import com.sirolf2009.dialogs.ConnectionDialog;
import com.sirolf2009.dialogs.TableConfigurationDialog;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXObject;
import com.sirolf2009.model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Arrays;

public class MainController {

    private final ObservableMap<String, JMXPuller> pullers = FXCollections.observableHashMap();
    private final ObservableList<Table> tables = FXCollections.observableArrayList();
    private MBeanServerConnection connection;

    @FXML private ToolBar toolbar;
    @FXML private AnchorPane variablesAnchor;
    @FXML private AnchorPane tablesAnchor;
    @FXML private TabPane tabs;

    @FXML
    public void initialize() {
        connection = ConnectionDialog.getLocalConnectionsDialog().showAndWait().get().get();
        ObservableList<JMXObject> objects = FXCollections.observableArrayList();
        try {
            connection.queryNames(null, null).forEach(objectName -> {
                ObservableList<JMXAttribute> attributes = FXCollections.observableArrayList();
                try {
                    Arrays.stream(connection.getMBeanInfo(objectName).getAttributes()).forEach(attributeInfo -> {
                        attributes.add(new JMXAttribute(objectName, attributeInfo));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                objects.add(new JMXObject(objectName, attributes));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        VariablesTreeView variables = new VariablesTreeView(objects);
        variablesAnchor.getChildren().add(variables);
        maximize(variables);

        TablesTreeView tablesTreeView = new TablesTreeView(tables);
        tablesAnchor.getChildren().add(tablesTreeView);
        maximize(tablesTreeView);
    }

    @FXML
    private void addTable(ActionEvent event) {
        try {
            TableConfigurationDialog.TableConfiguration config = new TableConfigurationDialog(connection).showAndWait().get();
            pullers.put(config.getTableName(), new JMXPuller(connection, FXCollections.observableArrayList(config.getAttributes()), 1000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newTable() {
        Tab newTableTab = new Tab("Untitled "+(tabs.getTabs().size()+1));
        TableTab table = new TableTab(connection);
        newTableTab.setContent(table);
        tabs.getTabs().add(newTableTab);
        tables.add(new Table(newTableTab.textProperty(), table));
    }

    public void newChart() {
        Tab newChart = new Tab("Untitled "+(tabs.getTabs().size()+1));
        newChart.setContent(new ChartTab(tables));
        tabs.getTabs().add(newChart);
    }

    public static void maximize(Node child) {
        AnchorPane.setTopAnchor(child, 0d);
        AnchorPane.setBottomAnchor(child, 0d);
        AnchorPane.setLeftAnchor(child, 0d);
        AnchorPane.setRightAnchor(child, 0d);
    }

}
