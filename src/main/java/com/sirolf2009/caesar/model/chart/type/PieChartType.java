package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieChartType implements IChartType {

    @Override public Predicate<Chart> getPredicate() {
        return hasColumns.and(areColumnsNumbers).and(hasRows.negate());
    }

    @Override public Node getChart(Chart chart) {
        PieChart pieChart = new PieChart();
        pieChart.setData(FXCollections.observableArrayList(chart.getColumns().map(column -> (INumberSeries)column.getSeries()).map(column -> {
            ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
            PieChart.Data data = new PieChart.Data(column.getName(), columnSeries.isEmpty() ? 0d : columnSeries.get(columnSeries.size()-1).doubleValue());
            data.nameProperty().bind(column.nameProperty());
            columnSeries.addListener((InvalidationListener) event -> {
                data.setPieValue(columnSeries.get(columnSeries.size() - 1).doubleValue());
            });
            return data;
        }).collect(Collectors.toList())));
        return pieChart;
    }

    @Override public String getName() {
        return "Pie Chart";
    }
}
