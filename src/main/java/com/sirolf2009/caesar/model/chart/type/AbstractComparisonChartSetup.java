package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractComparisonChartSetup<X, Y, T extends AbstractComparisonChartSetup.ComparisonSeries<X, Y>> implements IChartTypeSetup {

	private final Chart chart;
	private final ObservableList<T> series;

	public AbstractComparisonChartSetup(Chart chart, ObservableList<T> series) {
		this.chart = chart;
		this.series = series;
		chart.getChildren().addListener((InvalidationListener) e -> update());
		update();
	}

	private void update() {
		List<Pair<ISeries<X>, ISeries<Y>>> requiredSeries = chart.getColumns().map(column -> (ISeries<X>) column.getSeries()).flatMap(column -> {
			return chart.getRows().map(row -> (ISeries<Y>) row.getSeries()).map(row -> {
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

	protected abstract T createContainer(ISeries<X> x, ISeries<Y> y);

	public Chart getChart() {
		return chart;
	}

	public ObservableList<T> getSeries() {
		return series;
	}

	public interface ComparisonSeries<X, Y> {

		ISeries<X> getX();
		ISeries<Y> getY();

	}

}
