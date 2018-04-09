package com.sirolf2009.caesar.model.chart.type.xy;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.chart.series.INumberArraySeries;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.IChartTypeSetup;
import com.sirolf2009.caesar.model.chart.type.xy.AbstractLineChartSetup;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.fxmisc.easybind.EasyBind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new LineChartTypeSetup(chart.getChildren(), FXCollections.observableArrayList());
	}

	@DefaultSerializer(LineChartTypeSetupSerializer.class)
	public static class LineChartTypeSetup extends AbstractLineChartSetup<Number, Number> {

		public LineChartTypeSetup(ObservableList<ColumnOrRow> chartSeries, ObservableList<XYSeries<Number, Number>> series) {
			super(chartSeries, series);
		}

		@Override protected Axis createXAxis() {
			return new NumberAxis();
		}

		@Override protected Axis createYAxis() {
			return new NumberAxis();
		}

	}

	public static class LineChartTypeSetupSerializer extends CaesarSerializer<LineChartTypeSetup> {

		@Override
		public void write(Kryo kryo, Output output, LineChartTypeSetup object) {
			writeObservableListWithClass(kryo, output, object.getChartSeries());
			writeObservableListWithClass(kryo, output, object.getSeries());
		}

		@Override
		public LineChartTypeSetup read(Kryo kryo, Input input, Class<LineChartTypeSetup> type) {
			ObservableList<ColumnOrRow> chartSeries = readObservableListWithClass(kryo, input, ColumnOrRow.class);
			ObservableList<XYSeries<Number, Number>> series = readObservableListWithClassAndCast(kryo, input, o -> (XYSeries<Number, Number>) o);
			return new LineChartTypeSetup(chartSeries, series);
		}
	}

}
