package com.sirolf2009.caesar.model.chart.type.xy;

import com.dooapp.fxform.annotation.NonVisual;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.chart.type.AbstractComparisonChartSetup;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.util.ChartUtil;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@DefaultSerializer(XYSeries.XYSeriesSerializer.class)
public class XYSeries<X, Y> implements AbstractComparisonChartSetup.ComparisonSeries<X, Y> {

	@NonVisual
	private final ISeries<X> x;
	@NonVisual
	private final ISeries<Y> y;
	@NonVisual
	private XYChart.Series<X, Y> series;
	private final StringProperty name;
	private final ObjectProperty<Color> color;
	private final BooleanProperty showMarkers;

	public XYSeries(ISeries<X> x, ISeries<Y> y, StringProperty name, ObjectProperty<Color> color, BooleanProperty showMarkers) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.color = color;
		this.showMarkers = showMarkers;
	}

	@Override public ISeries<X> getX() {
		return x;
	}

	@Override public ISeries<Y> getY() {
		return y;
	}

	public XYChart.Series<X, Y> getSeries() {
		if(series == null) {
			series = new XYChart.Series<>();
			series.nameProperty().bind(name);
			color.addListener(e -> getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor)));
			getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor));
			ChartUtil.showMarkers(series, showMarkers.get());
			showMarkers.addListener(e -> ChartUtil.showMarkers(series, showMarkers.get()));
			IntStream.range(0, Math.min(x.get().size(), y.get().size())).forEach(i -> series.getData().add(new XYChart.Data<>(x.get().get(i), y.get().get(i))));
			JavaFxObservable.additionsOf(x.get()).zipWith(JavaFxObservable.additionsOf(y.get()), (a, b) -> {
				return new XYChart.Data<X, Y>(a, b);
			}).subscribe((item -> series.getData().add(item)));
		}
		return series;
	}

	@Override public String toString() {
		return "XYSeries{" + "x=" + x + ", y=" + y + ", series=" + series + ", name=" + name + ", color=" + color + ", showMarkers=" + showMarkers + '}';
	}

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof XYSeries))
			return false;
		XYSeries<?, ?> xySeries = (XYSeries<?, ?>) o;
		return Objects.equals(getX(), xySeries.getX()) && Objects.equals(getY(), xySeries.getY()) && Objects.equals(getName(), xySeries.getName()) && Objects.equals(getColor(), xySeries.getColor()) && Objects.equals(isShowMarkers(), xySeries.isShowMarkers());
	}

	@Override public int hashCode() {
		return Objects.hash(getX(), getY(), getName(), getColor(), isShowMarkers());
	}

	public Optional<Color> getColor() {
		return Optional.ofNullable(color.get());
	}

	public ObjectProperty<Color> colorProperty() {
		return color;
	}

	public boolean isShowMarkers() {
		return showMarkers.get();
	}

	public BooleanProperty showMarkersProperty() {
		return showMarkers;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public static class XYSeriesSerializer extends CaesarSerializer<XYSeries> {

		@Override
		public void write(Kryo kryo, Output output, XYSeries object) {
			kryo.writeClassAndObject(output, object.getX());
			kryo.writeClassAndObject(output, object.getY());
			output.writeString(object.getName());
			output.writeBoolean(object.getColor().isPresent());
			object.getColor().ifPresent(c -> {
				kryo.writeObject(output, c);
			});
			output.writeBoolean(object.isShowMarkers());
		}

		@Override
		public XYSeries read(Kryo kryo, Input input, Class<XYSeries> type) {
			ISeries x = (ISeries) kryo.readClassAndObject(input);
			ISeries y = (ISeries) kryo.readClassAndObject(input);
			StringProperty name = readStringProperty(input);
			ObjectProperty<Color> color = readColor(kryo, input);
			BooleanProperty showMarkers = readBooleanProperty(input);
			return new XYSeries(x, y, name, color, showMarkers);
		}

		public ObjectProperty<Color> readColor(Kryo kryo, Input input) {
			if(input.readBoolean()) {
				return new SimpleObjectProperty<>(kryo.readObject(input, Color.class));
			}
			return new SimpleObjectProperty<>();
		}
	}
}