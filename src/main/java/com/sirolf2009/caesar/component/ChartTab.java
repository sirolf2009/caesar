package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.chart.series.*;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.IChartTypeSetup;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.util.ControllerUtil;
import com.sirolf2009.caesar.util.FXUtil;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.sirolf2009.caesar.util.TypeUtil.*;

public class ChartTab extends VBox {

    private final ObservableList<Table> tables;
    private final Chart chart;

    @FXML
    HBox columns;
    @FXML
    HBox rows;
    @FXML
    AnchorPane chartAnchor;
    @FXML
    AnchorPane configAnchor;
    @FXML
    ChoiceBox<IChartType> chartTypeSelector;

    public ChartTab(Chart chart, ObservableList<Table> tables) {
        this.chart = chart;
        this.tables = tables;
        ControllerUtil.load(this, "/fxml/chart.fxml");
        chart.getChildren().addListener((InvalidationListener) observable -> setupChartTypes());
    }

    @FXML
    public void initialize() {
        chart.getColumns().forEach(series -> addColumn(series));
        chart.getRows().forEach(series -> addRow(series));
        setupChartTypes();
        if(chart.getChartTypeSetup() != null) {
            setupChart();
        }

        chartTypeSelector.setConverter(new StringConverter<IChartType>() {
            @Override
            public String toString(IChartType object) {
                return object.getName();
            }

            @Override
            public IChartType fromString(String string) {
                return chart.getPossibleChartTypes().filter(type -> type.getName().equals(string)).findAny().orElse(null);
            }
        });
        chartTypeSelector.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
            chart.chartTypeSetupProperty().set(chartTypeSelector.getSelectionModel().getSelectedItem().getSetup(chart));
            setupChart();
        });

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

    private void setupChartTypes() {
        IChartType selected = chartTypeSelector.getSelectionModel().getSelectedItem();
        chartTypeSelector.getItems().clear();
        chart.getPossibleChartTypes().forEach(type -> {
            chartTypeSelector.getItems().add(type);
        });
        if (selected != null && selected.getPredicate().test(chart)) {
            chartTypeSelector.getSelectionModel().select(selected);
        }
    }

    private void setupChart() {
        configAnchor.getChildren().clear();
        IChartTypeSetup type = this.chart.getChartTypeSetup();
        Node config = type.createConfiguration();
        configAnchor.getChildren().add(config);
        FXUtil.maximize(config);
        chartAnchor.getChildren().clear();
        Node chart = type.createChart();
        chartAnchor.getChildren().add(chart);
        FXUtil.maximize(chart);
    }

    protected static Map<Predicate<IDataPointer>, Function<Pair<Table, IDataPointer>, ISeries>> seriesMapper = new HashMap<>();
    static {
        seriesMapper.put(type -> isBoolean(type), pair -> new BooleanSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isByteArray(type), pair -> new BooleanArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isByte(type), pair -> new ByteSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isByteArray(type), pair -> new ByteArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isShort(type), pair -> new ShortSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isShortArray(type), pair -> new ShortArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isInt(type), pair -> new IntegerSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isIntArray(type), pair -> new IntegerArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isLong(type), pair -> new LongSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isLongArray(type), pair -> new LongArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isFloat(type), pair -> new FloatSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isFloatArray(type), pair -> new FloatArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isDouble(type), pair -> new DoubleSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isDoubleArray(type), pair -> new DoubleArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isString(type), pair -> new StringSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isStringArray(type), pair -> new StringArraySeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isDate(type), pair -> new DateSeries(pair.getKey(), pair.getValue()));
        seriesMapper.put(type -> isDateArray(type), pair -> new DateArraySeries(pair.getKey(), pair.getValue()));
    }

    public static ISeries getSeries(Table table, IDataPointer attribute) {
        Function<Pair<Table, IDataPointer>, ISeries> converter = seriesMapper.get(seriesMapper.keySet().stream().filter(predicate -> predicate.test(attribute)).findAny().orElseThrow(() -> new IllegalArgumentException("Unknown type: " + attribute.getType())));
        return converter.apply(new Pair(table, attribute));
    }

}
