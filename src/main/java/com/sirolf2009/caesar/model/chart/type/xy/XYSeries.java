package com.sirolf2009.caesar.model.chart.type.xy;

import com.dooapp.fxform.annotation.NonVisual;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.util.ChartUtil;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.stream.IntStream;

public class XYSeries<X, Y> implements AbstractComparisonChartSetup.ComparisonSeries<X, Y> {

	@NonVisual
	private final ISeries<X> x;
	@NonVisual
	private final ISeries<Y> y;
	@NonVisual
	private final XYChart.Series<X, Y> series;
	private final StringProperty name;
	private final ObjectProperty<Color> color;
	private final BooleanProperty showMarkers;

	public XYSeries(ISeries<X> x, ISeries<Y> y, StringProperty name, ObjectProperty<Color> color, BooleanProperty showMarkers) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.color = color;
		this.showMarkers = showMarkers;

		series = new XYChart.Series<>();
		series.nameProperty().bind(name);
		color.addListener(e -> getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor)));
		getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor));
		ChartUtil.showMarkers(series, showMarkers.get());
		showMarkers.addListener(e -> ChartUtil.showMarkers(series, showMarkers.get()));
		IntStream.range(0, Math.min(x.get().size(), y.get().size())).forEach(i -> series.getData().add(new XYChart.Data<>(x.get().get(i), y.get().get(i))));
		JavaFxObservable.additionsOf(x.get()).zipWith(JavaFxObservable.additionsOf(y.get()), (a, b) -> new XYChart.Data<X, Y>(a, b)).subscribe((item -> series.getData().add(item)));
	}

	@Override public ISeries<X> getX() {
		return x;
	}

	@Override public ISeries<Y> getY() {
		return y;
	}

	public XYChart.Series<X, Y> getSeries() {
		return series;
	}

	public Optional<Color> getColor() {
		return Optional.ofNullable(color.get());
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}
}
