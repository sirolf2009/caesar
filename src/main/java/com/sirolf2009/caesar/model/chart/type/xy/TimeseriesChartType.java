package com.sirolf2009.caesar.model.chart.type.xy;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.IChartTypeSetup;
import com.sirolf2009.caesar.model.chart.type.xy.AbstractLineChartSetup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import nl.itopia.corendon.components.DateAxis;

import java.util.Date;
import java.util.function.Predicate;

public class TimeseriesChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsDates).and(hasRows).and(areRowsNumbers);
	}

	@Override public Node getChart(Chart chart) {
		throw new IllegalArgumentException("no");
	}

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new TimeseriesChartTypeSetup(chart, FXCollections.observableArrayList());
	}

	@Override public String getName() {
		return "Timeseries Chart";
	}

	public static class TimeseriesChartTypeSetup extends AbstractLineChartSetup<Date, Number> {

		public TimeseriesChartTypeSetup(Chart chart, ObservableList<XYSeries<Date, Number>> series) {
			super(chart, series);
		}

		@Override protected Axis createXAxis() {
			return new DateAxis();
		}

		@Override protected Axis createYAxis() {
			NumberAxis axis = new NumberAxis();
			axis.setForceZeroInRange(false);
			return axis;
		}
	}
}
