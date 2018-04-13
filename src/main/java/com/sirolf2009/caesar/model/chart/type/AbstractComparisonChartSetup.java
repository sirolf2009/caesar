package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.ColumnOrRows;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractComparisonChartSetup<X, Y, T extends AbstractComparisonChartSetup.ComparisonSeries<X, Y>> extends AbstractChartSetup {

	private final ObservableList<T> series;

	public AbstractComparisonChartSetup(ColumnOrRows chartSeries, ObservableList<T> series) {
		super(chartSeries);
		this.series = series;
		update();
	}

	@Override
	public void update() {
		List<Pair<ISeries<X>, ISeries<Y>>> requiredSeries = getColumns().map(column -> (ISeries<X>) column.getSeries()).flatMap(column -> {
			return getRows().map(row -> (ISeries<Y>) row.getSeries()).map(row -> {
				return new Pair<ISeries<X>, ISeries<Y>>(column, row);
			});
		}).collect(Collectors.toList());

		List<ComparisonSeries<X, Y>> noLongerRequired = series.stream().filter(serie -> !requiredSeries.stream().filter(required -> required.getKey() == serie.getX() && required.getValue() == serie.getY()).findAny().isPresent()).collect(Collectors.toList());
		series.removeAll(noLongerRequired);

		List<Pair<ISeries<X>, ISeries<Y>>> toBeCreated = requiredSeries.stream().filter(required -> !series.stream().filter(serie -> required.getKey() == serie.getX() && required.getValue() == serie.getY()).findAny().isPresent()).collect(Collectors.toList());
		toBeCreated.stream().map(pair -> {
			return createContainer(pair.getKey(), pair.getValue());
		}).forEach(serie -> series.add(serie));
	}

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof AbstractComparisonChartSetup))
			return false;
		if(!super.equals(o))
			return false;
		AbstractComparisonChartSetup<?, ?, ?> that = (AbstractComparisonChartSetup<?, ?, ?>) o;
		return Objects.equals(getSeries(), that.getSeries());
	}

	@Override public int hashCode() {
		return Objects.hash(super.hashCode(), getSeries());
	}

	protected abstract T createContainer(ISeries<X> x, ISeries<Y> y);

	public ObservableList<T> getSeries() {
		return series;
	}

	public interface ComparisonSeries<X, Y> {

		ISeries<X> getX();
		ISeries<Y> getY();

	}

}
