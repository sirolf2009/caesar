package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.INumberArraySeries;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.fxmisc.easybind.EasyBind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class LineChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return (hasColumns.and(areColumnsNumbers).and(hasRows).and(areRowsNumbers)).or(hasColumns.negate().and(hasRows).and(areRowsNumberArrays));
	}

	@Override public Node getChart(Chart chart) {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setForceZeroInRange(false);
		NumberAxis yAxis = new NumberAxis();
		yAxis.setForceZeroInRange(false);
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		if(hasColumns.test(chart)) {
			chart.getColumns().map(column -> (INumberSeries) column.getSeries()).forEach(column -> {
				ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
				chart.getRows().map(row -> (INumberSeries) row.getSeries()).forEach(row -> {
					ObservableList<Number> rowSeries = (ObservableList<Number>) row.get();
					XYChart.Series series = new XYChart.Series();
					series.nameProperty().bind(EasyBind.combine(row.nameProperty(), column.nameProperty(), (r, c) -> r + "/" + c));
					JavaFxObservable.additionsOf(columnSeries).zipWith(JavaFxObservable.additionsOf(rowSeries), (a,b) -> new XYChart.Data<Number, Number>(a, b)).subscribe(item -> series.getData().add(item));
					lineChart.getData().add(series);
				});
			});
		} else {
			chart.getRows().map(row -> (INumberArraySeries) row.getSeries()).forEach(row -> {
				ObservableList<Number[]> rowSeries = (ObservableList<Number[]>) row.get();
				XYChart.Series series = new XYChart.Series();
				series.nameProperty().bind(row.nameProperty());
				row.get().addListener((InvalidationListener) event -> {
					try {
						Number[] values = (Number[]) row.get().get(row.get().size() - 1);
						List<XYChart.Data<Number, Number>> data = new ArrayList<>();
						for(int i = 0; i < values.length; i++) {
							data.add(new XYChart.Data<>(i, values[i]));
						}
						series.getData().setAll(data);
					} catch(Exception e) {
						e.printStackTrace();
					}
				});
				lineChart.getData().add(series);
			});
		}
		return lineChart;
	}

	@Override public String getName() {
		return "Line Chart";
	}
}
