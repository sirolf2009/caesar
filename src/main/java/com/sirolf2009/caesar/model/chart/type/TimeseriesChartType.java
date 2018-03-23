package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.DateSeries;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import nl.itopia.corendon.components.DateAxis;
import org.fxmisc.easybind.EasyBind;

import java.util.Date;
import java.util.function.Predicate;

public class TimeseriesChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsDates).and(hasRows).and(areRowsNumbers);
	}

	@Override public Node getChart(Chart chart) {
		DateAxis xAxis = new DateAxis();
		NumberAxis yAxis = new NumberAxis();
		yAxis.setForceZeroInRange(false);
		LineChart<Date, Number> lineChart = new LineChart<Date, Number>(xAxis, yAxis);
		chart.getColumns().map(column -> (DateSeries) column.getSeries()).forEach(column -> {
			ObservableList<Date> columnSeries = (ObservableList<Date>) column.get();
			chart.getRows().map(row -> (INumberSeries) row.getSeries()).forEach(row -> {
				ObservableList<Number> rowSeries = (ObservableList<Number>) row.get();
				XYChart.Series series = new XYChart.Series();
				series.nameProperty().bind(EasyBind.combine(row.nameProperty(), column.nameProperty(), (r, c) -> r + "/" + c));
				series.setData(EasyBind.map(columnSeries, x -> {
					return new XYChart.Data<Date, Number>(x, rowSeries.get(columnSeries.indexOf(x)));
				}));
				lineChart.getData().add(series);
			});
		});
		return lineChart;
	}

	@Override public String getName() {
		return "Timeseries Chart";
	}
}
