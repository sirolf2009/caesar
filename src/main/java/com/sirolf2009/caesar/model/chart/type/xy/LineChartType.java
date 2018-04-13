package com.sirolf2009.caesar.model.chart.type.xy;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.ColumnOrRows;
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

	public static final String name = "Line Chart";

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsNumbers).and(hasRows).and(areRowsNumbers);
	}

	@Override public String getName() {
		return name;
	}

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new LineChartTypeSetup(chart.getSeriesList(), FXCollections.observableArrayList());
	}

	@DefaultSerializer(LineChartTypeSetupSerializer.class)
	public static class LineChartTypeSetup extends AbstractLineChartSetup<Number, Number> {

		public LineChartTypeSetup(ColumnOrRows chartSeries, ObservableList<XYSeries<Number, Number>> series) {
			super(chartSeries, series);
		}

		@Override protected Axis createXAxis() {
			return new NumberAxis();
		}

		@Override protected Axis createYAxis() {
			return new NumberAxis();
		}

		@Override public String getName() {
			return name;
		}

	}

	public static class LineChartTypeSetupSerializer extends CaesarSerializer<LineChartTypeSetup> {

		@Override
		public void write(Kryo kryo, Output output, LineChartTypeSetup object) {
			kryo.writeObject(output, object.getChartSeries());
			writeObservableListWithClass(kryo, output, object.getSeries());
		}

		@Override
		public LineChartTypeSetup read(Kryo kryo, Input input, Class<LineChartTypeSetup> type) {
			ColumnOrRows chartSeries = kryo.readObject(input, ColumnOrRows.class);
			ObservableList<XYSeries<Number, Number>> series = readObservableListWithClassAndCast(kryo, input, o -> (XYSeries<Number, Number>) o);
			return new LineChartTypeSetup(chartSeries, series);
		}
	}

}
