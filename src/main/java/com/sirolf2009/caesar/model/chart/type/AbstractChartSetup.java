package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.ColumnOrRows;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractChartSetup implements IChartTypeSetup {

	private final ColumnOrRows chartSeries;

	public AbstractChartSetup(ColumnOrRows chartSeries) {
		this.chartSeries = chartSeries;
		chartSeries.getData().addListener((InvalidationListener) e -> {
			update();
		});
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

	public ColumnOrRows getChartSeries() {
		return chartSeries;
	}

	public Stream<ColumnOrRow.Column> getColumns() {
		return chartSeries.getData().stream().filter(series -> series.isColumn()).map(series -> (ColumnOrRow.Column) series);
	}

	public Stream<ColumnOrRow.Row> getRows() {
		return chartSeries.getData().stream().filter(series -> series.isRow()).map(series -> (ColumnOrRow.Row) series);
	}

}
