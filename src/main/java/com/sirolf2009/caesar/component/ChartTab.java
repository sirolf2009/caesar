package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.MainController;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.chart.type.BarChartType;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.LineChartType;
import com.sirolf2009.caesar.model.chart.series.*;
import com.sirolf2009.caesar.util.ControllerUtil;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class ChartTab extends VBox {

    private static List<IChartType> chartTypes = Arrays.asList(new LineChartType(), new BarChartType());

    private final ObservableList<Table> tables;
    private final Chart chart;

    @FXML
    HBox columns;
    @FXML
    HBox rows;
    @FXML
    AnchorPane chartAnchor;

    public ChartTab(Chart chart, ObservableList<Table> tables) {
        this.chart = chart;
        this.tables = tables;
        ControllerUtil.load(this, "/fxml/chart.fxml");
        chart.getChildren().addListener((InvalidationListener) observable -> setupChart());
    }

    @FXML
    public void initialize() {
        chart.getColumns().forEach(series -> addColumn(series));
        chart.getRows().forEach(series -> addRow(series));
        setupChart();

        columns.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != columns && event1.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER)) {
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
                    ColumnOrRow.Column column = new ColumnOrRow.Column(getSeries(TablesTreeView.table, TablesTreeView.pointer));
                    chart.getChildren().add(column);
                    addColumn(column);
                    event.consume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        rows.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != rows && event1.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER)) {
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
                    ColumnOrRow.Row row = new ColumnOrRow.Row(getSeries(TablesTreeView.table, TablesTreeView.pointer));
                    chart.getChildren().add(row);
                    addRow(row);
                    event.consume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addColumn(ColumnOrRow.Column column) {
        ColumnRowLabel label = new ColumnRowLabel(column.getSeries());
        label.getBtnRemove().setOnAction(event -> {
            chart.getChildren().remove(column);
            columns.getChildren().remove(label);
        });
        columns.getChildren().add(label);
    }

    public void addRow(ColumnOrRow.Row row) {
        ColumnRowLabel label = new ColumnRowLabel(row.getSeries());
        label.getBtnRemove().setOnAction(event -> {
            chart.getChildren().remove(row);
            rows.getChildren().remove(label);
        });
        rows.getChildren().add(label);
    }

    private void setupChart() {
        chartAnchor.getChildren().clear();
        chartTypes.stream().filter(type -> type.getPredicate().test(chart)).findAny().ifPresent(chartType -> {
            Node chart = chartType.getChart(this.chart);
            chartAnchor.getChildren().add(chart);
            MainController.maximize(chart);
        });
    }

    public static ISeries getSeries(Table table, IDataPointer attribute) {
        switch (attribute.getType()) {
            case "int":
                return new IntegerSeries(table, attribute);
            case "java.long.Integer":
                return new IntegerSeries(table, attribute);
            case "long":
                return new LongSeries(table, attribute);
            case "java.lang.Long":
                return new LongSeries(table, attribute);
            case "boolean":
                return new BooleanSeries(table, attribute);
            case "java.lang.Boolean":
                return new BooleanSeries(table, attribute);
            case "double":
                return new DoubleSeries(table, attribute);
            case "java.lang.Double":
                return new DoubleSeries(table, attribute);
            case "java.lang.String":
                return new StringSeries(table, attribute);
        }
        throw new IllegalArgumentException("Unknown type: " + attribute.getType());
    }

}
