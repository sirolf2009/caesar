package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.function.Predicate;

public class BarChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsNumbers).and(hasRows.negate());
	}

	@Override public Node getChart(Chart chart) {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
		chart.getColumns().map(column -> (INumberSeries)column.getSeries()).forEach(column -> {
			XYChart.Series series = new XYChart.Series();
			ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
			series.nameProperty().bindBidirectional(column.nameProperty());
			XYChart.Data<String, Number> data = new XYChart.Data<>(column.getName(), columnSeries.isEmpty() ? 0d : columnSeries.get(columnSeries.size()-1));
			columnSeries.addListener((InvalidationListener) event -> {
				data.setYValue(columnSeries.get(columnSeries.size() - 1));
			});
			series.getData().add(data);
			barChart.getData().add(series);
		});
		return barChart;
	}

	@Override public String getName() {
		return "Bar Chart";
	}
}
