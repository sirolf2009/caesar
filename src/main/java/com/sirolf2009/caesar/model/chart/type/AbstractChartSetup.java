package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.ColumnOrRow;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractChartSetup implements IChartTypeSetup {

	private final ObservableList<ColumnOrRow> chartSeries;

	public AbstractChartSetup(ObservableList<ColumnOrRow> chartSeries) {
		this.chartSeries = chartSeries;
		chartSeries.addListener((InvalidationListener) e -> update());
	}

	public abstract void update();

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof AbstractChartSetup))
			return false;
		AbstractChartSetup that = (AbstractChartSetup) o;
		return Objects.equals(getChartSeries(), that.getChartSeries());
	}

	@Override public int hashCode() {
		return Objects.hash(getChartSeries());
	}

	public ObservableList<ColumnOrRow> getChartSeries() {
		return chartSeries;
	}

	public Stream<ColumnOrRow.Column> getColumns() {
		return chartSeries.stream().filter(series -> series.isColumn()).map(series -> (ColumnOrRow.Column) series);
	}

	public Stream<ColumnOrRow.Row> getRows() {
		return chartSeries.stream().filter(series -> series.isRow()).map(series -> (ColumnOrRow.Row) series);
	}

}
