package com.sirolf2009.caesar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.*;
import com.sirolf2009.caesar.model.*;
import com.sirolf2009.caesar.dialogs.LocalConnectionDialog;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.management.MBeanServerConnection;
import java.io.*;
import java.util.Arrays;

public class MainController {

    private CaesarModel model;
    private Connection connection;

    @FXML
    private ToolBar toolbar;
    @FXML
    private AnchorPane variablesAnchor;
    @FXML
    private AnchorPane tablesAnchor;
    @FXML
    private TabPane tabs;
    private VariablesTreeView variables;
    private TablesTreeView tablesTreeView;

    public MainController() {
        this(new CaesarModel());
    }

    public MainController(CaesarModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        try {
            connection = new Connection(LocalConnectionDialog.getLocalConnectionsDialog().showAndWait().get().get());
            ObservableList<JMXObject> objects = FXCollections.observableArrayList();
            connection.getConnection().ifPresent(connection -> {
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
            });
            variables = new VariablesTreeView(objects);
            variablesAnchor.getChildren().add(variables);
            maximize(variables);

            tablesTreeView = new TablesTreeView(model.getTables());
            tablesAnchor.getChildren().add(tablesTreeView);
            maximize(tablesTreeView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(toolbar.getScene().getWindow());
        if (file != null) {
            try {
                Kryo kryo = new Kryo();
                Output out = new Output(new FileOutputStream(file));
                kryo.writeObject(out, model);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(toolbar.getScene().getWindow());
        if (file != null) {
            try {
                Kryo kryo = new Kryo();
                Input input = new Input(new FileInputStream(file));
                CaesarModel newModel = kryo.readObject(input, CaesarModel.class);
                input.close();
                switchTo(newModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void switchTo(CaesarModel newModel) {
        tabs.getTabs().stream().filter(tab -> tab.getContent() instanceof TableTab).forEach(tab -> ((TableTab) tab.getContent()).getPuller().runningProperty().set(false));
        tabs.getTabs().clear();
        this.model = newModel;
        tablesTreeView.setItems(model.getTables());
        model.getTables().forEach(table -> addTable(table));
        model.getCharts().forEach(chart -> addChart(chart));
        tabs.getTabs().stream().filter(tab -> tab.getContent() instanceof TableTab).forEach(tab -> ((TableTab) tab.getContent()).getPuller().runningProperty().set(true));
    }

    public void newTable() {
        Table table = new Table("Untitled " + (tabs.getTabs().size() + 1));
        model.getTables().add(table);
        addTable(table);
    }

    public void addTable(Table table) {
        Tab newTableTab = new RenameableTab(table.nameProperty());
        tabs.getTabs().add(newTableTab);
        tabs.getSelectionModel().select(newTableTab);
        newTableTab.setContent(new TableTab(table, connection));
    }

    public void newChart() {
        Chart chart = new Chart("Untitled " + (tabs.getTabs().size() + 1));
        model.getCharts().add(chart);
        addChart(chart);
    }

    public void addChart(Chart chart) {
        Tab newChartTab = new RenameableTab(chart.nameProperty());
        newChartTab.setContent(new ChartTab(chart, model.getTables()));
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
