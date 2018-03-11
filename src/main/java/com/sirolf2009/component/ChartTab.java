package com.sirolf2009.component;

import com.sirolf2009.MainController;
import com.sirolf2009.model.ColumnOrRow;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXAttributes;
import com.sirolf2009.model.Table;
import com.sirolf2009.util.ControllerUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.Arrays;

public class ChartTab extends VBox {

    private final ObservableList<Table> tables;
    private final ObservableList<ColumnOrRow> columnsList = FXCollections.observableArrayList();
    private final ObservableList<ColumnOrRow> rowsList = FXCollections.observableArrayList();

    @FXML
    HBox columns;
    @FXML
    HBox rows;
    @FXML
    AnchorPane chartAnchor;

    public ChartTab(ObservableList<Table> tables) {
        this.tables = tables;
        ControllerUtil.load(this, "/fxml/chart.fxml");
        columnsList.addListener((InvalidationListener) observable -> setupChart());
        rowsList.addListener((InvalidationListener) observable -> setupChart());
    }

    @FXML
    public void initialize() {
        columns.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != columns && event1.getDragboard().hasString() && event1.getDragboard().getString().split("@").length == 3) {
                event1.acceptTransferModes(TransferMode.LINK);
                columns.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
            }
            event1.consume();
        });
        columns.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                columns.setStyle("");
                event.consume();
            }
        });
        columns.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                try {
                    String newVariable = event.getDragboard().getString();
                    String[] data = newVariable.split("@");
                    tables.stream().filter(table -> table.getName().equals(data[2])).findAny().ifPresent(table -> {
                        table.getChildren().stream().filter(attribute -> attribute.getObjectName().toString().equals(data[1])).filter(attribute -> attribute.getAttributeInfo().getName().equals(data[0])).findAny().ifPresent(attribute -> {
                            columnsList.add(new ColumnOrRow(table, attribute));
                            columns.getChildren().add(new Label(table.getName()+"/"+attribute.getAttributeInfo().getName()));
                            event.consume();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        rows.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != rows && event1.getDragboard().hasString() && event1.getDragboard().getString().split("@").length == 3) {
                event1.acceptTransferModes(TransferMode.LINK);
                rows.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
            }
            event1.consume();
        });
        rows.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                rows.setStyle("");
                event.consume();
            }
        });
        rows.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                try {
                    String newVariable = event.getDragboard().getString();
                    String[] data = newVariable.split("@");
                    System.out.println(Arrays.toString(data));
                    System.out.println(tables.size());
                    tables.forEach(table -> System.out.println(table.getName()+"=="+data[2]+" ? "+table.getName().equals(data[2])));
                    tables.stream().filter(table -> table.getName().equals(data[2])).findAny().ifPresent(table -> {
                        System.out.println(table);
                        table.getChildren().stream().filter(attribute -> attribute.getObjectName().toString().equals(data[1])).filter(attribute -> attribute.getAttributeInfo().getName().equals(data[0])).findAny().ifPresent(attribute -> {
                            System.out.println(attribute);
                            rowsList.add(new ColumnOrRow(table, attribute));
                            rows.getChildren().add(new Label(table.getName()+"/"+attribute.getAttributeInfo().getName()));
                            event.consume();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupChart() {
        System.out.println("setupChart");
        chartAnchor.getChildren().clear();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        columnsList.forEach(column -> {
            rowsList.forEach(row -> {
                XYChart.Series series = new XYChart.Series();
                series.setData(EasyBind.map(column.getNumbers(), x -> {
                    return new XYChart.Data<Number, Number>(x, row.getNumbers().get(column.getNumbers().indexOf(x)));
                }));
                lineChart.getData().add(series);
            });
        });
        chartAnchor.getChildren().add(lineChart);
        MainController.maximize(lineChart);
    }

}
