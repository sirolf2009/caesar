package com.sirolf2009;

import com.sirolf2009.component.ChartTab;
import com.sirolf2009.component.TableTab;
import com.sirolf2009.component.TablesTreeView;
import com.sirolf2009.component.VariablesTreeView;
import com.sirolf2009.dialogs.LocalConnectionDialog;
import com.sirolf2009.model.Chart;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXObject;
import com.sirolf2009.model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Arrays;

public class MainController {

    private final ObservableList<Table> tables = FXCollections.observableArrayList();
    private final ObservableList<Chart> charts = FXCollections.observableArrayList();
    private MBeanServerConnection connection;

    @FXML private ToolBar toolbar;
    @FXML private AnchorPane variablesAnchor;
    @FXML private AnchorPane tablesAnchor;
    @FXML private TabPane tabs;

    @FXML
    public void initialize() {
        connection = LocalConnectionDialog.getLocalConnectionsDialog().showAndWait().get().get();
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

    public void newTable() {
        Table table = new Table("Untitled "+(tabs.getTabs().size()+1));
        Tab newTableTab = new Tab();
        newTableTab.textProperty().bind(table.nameProperty());
        tables.add(table);
        newTableTab.setContent(new TableTab(table, connection));
        tabs.getTabs().add(newTableTab);
        tabs.getSelectionModel().select(newTableTab);
    }

    public void newChart() {
        Chart chart = new Chart("Untitled "+(tabs.getTabs().size()+1));
        Tab newChartTab = new Tab();
        newChartTab.textProperty().bind(chart.nameProperty());
        newChartTab.setContent(new ChartTab(chart, tables));
        tabs.getTabs().add(newChartTab);
        tabs.getSelectionModel().select(newChartTab);
    }

    public static void maximize(Node child) {
        AnchorPane.setTopAnchor(child, 0d);
        AnchorPane.setBottomAnchor(child, 0d);
        AnchorPane.setLeftAnchor(child, 0d);
        AnchorPane.setRightAnchor(child, 0d);
    }

}
