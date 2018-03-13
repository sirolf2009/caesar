package com.sirolf2009.component;

import com.sirolf2009.MainController;
import com.sirolf2009.model.ColumnOrRow;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXAttributes;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.chart.*;
import com.sirolf2009.util.ControllerUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
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
import java.util.function.Function;
import java.util.function.Predicate;

public class ChartTab extends VBox {

    private static final Predicate<ISeries> isNumbers = serie -> serie instanceof INumberSeries;
    private static final Function<ISeries, INumberSeries> asNumbers = serie -> (INumberSeries) serie;
    private static final Predicate<ISeries> isCategories = serie -> serie instanceof ICategorySeries;
    private static final Function<ISeries, ICategorySeries> asCategories = serie -> (ICategorySeries) serie;

    private final ObservableList<Table> tables;
    private final ObservableList<ISeries> columnsList = FXCollections.observableArrayList();
    private final ObservableList<ISeries> rowsList = FXCollections.observableArrayList();

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
                            columnsList.add(getSeries(table, attribute));
                            columns.getChildren().add(new Label(table.getName() + "/" + attribute.getAttributeInfo().getName()));
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
                    tables.stream().filter(table -> table.getName().equals(data[2])).findAny().ifPresent(table -> {
                        table.getChildren().stream().filter(attribute -> attribute.getObjectName().toString().equals(data[1])).filter(attribute -> attribute.getAttributeInfo().getName().equals(data[0])).findAny().ifPresent(attribute -> {
                            rowsList.add(getSeries(table, attribute));
                            rows.getChildren().add(new Label(table.getName() + "/" + attribute.getAttributeInfo().getName()));
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
        chartAnchor.getChildren().clear();
        if (rowsList.size() == 0 && columnsList.stream().filter(isCategories).count() == 0) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
            columnsList.stream().map(asNumbers).forEach(column -> {
                XYChart.Series series = new XYChart.Series();
                ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
                series.nameProperty().bindBidirectional(column.getName());
                XYChart.Data<String, Number> data = new XYChart.Data<>(column.getName().get(), columnSeries.get(columnSeries.size()-1));
                columnSeries.addListener((InvalidationListener) event -> data.setYValue(columnSeries.get(columnSeries.size()-1)));
                series.getData().add(data);
                barChart.getData().add(series);
            });
            chartAnchor.getChildren().add(barChart);
            MainController.maximize(barChart);
        } else if (rowsList.stream().filter(isCategories).count() == 0 && columnsList.stream().filter(isCategories).count() == 0) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setForceZeroInRange(false);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setForceZeroInRange(false);
            LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
            columnsList.stream().map(asNumbers).forEach(column -> {
                ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
                rowsList.stream().map(asNumbers).forEach(row -> {
                    ObservableList<Number> rowSeries = (ObservableList<Number>) row.get();
                    XYChart.Series series = new XYChart.Series();
                    series.nameProperty().bind(EasyBind.combine(row.getName(), column.getName(), (r,c) -> r+"/"+c));
                    series.setData(EasyBind.map(columnSeries, x -> {
                        return new XYChart.Data<Number, Number>(x, rowSeries.get(columnSeries.indexOf(x)));
                    }));
                    lineChart.getData().add(series);
                });
            });
            chartAnchor.getChildren().add(lineChart);
            MainController.maximize(lineChart);
        }
    }

    public static ISeries getSeries(Table table, JMXAttribute attribute) {
        switch (attribute.getAttributeInfo().getType()) {
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
        throw new IllegalArgumentException("Unknown type: " + attribute.getAttributeInfo().getType());
    }

}
