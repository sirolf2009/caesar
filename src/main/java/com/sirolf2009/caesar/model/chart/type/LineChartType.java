package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.series.INumberSeries;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.fxmisc.easybind.EasyBind;

import java.util.function.Predicate;

public class LineChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsNumbers).and(hasRows).and(areRowsNumbers);
	}

	@Override public Node getChart(Chart chart) {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setForceZeroInRange(false);
		NumberAxis yAxis = new NumberAxis();
		yAxis.setForceZeroInRange(false);
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.getColumns().map(column -> (INumberSeries) column.getSeries()).forEach(column -> {
			ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
			chart.getRows().map(row -> (INumberSeries) row.getSeries()).forEach(row -> {
				ObservableList<Number> rowSeries = (ObservableList<Number>) row.get();
				XYChart.Series series = new XYChart.Series();
				series.nameProperty().bind(EasyBind.combine(row.nameProperty(), column.nameProperty(), (r, c) -> r + "/" + c));
				series.setData(EasyBind.map(columnSeries, x -> {
					return new XYChart.Data<Number, Number>(x, rowSeries.get(columnSeries.indexOf(x)));
				}));
				lineChart.getData().add(series);
			});
		});
		return lineChart;
	}

	@Override public String getName() {
		return "Line Chart";
	}
}
