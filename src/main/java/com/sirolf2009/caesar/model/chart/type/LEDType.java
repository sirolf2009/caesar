package com.sirolf2009.caesar.model.chart.type;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.annotation.NonVisual;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.BooleanSeries;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.util.ChartUtil;
import eu.hansolo.enzo.led.Led;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxmisc.easybind.EasyBind;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LEDType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.negate().and(hasRows).and(IChartType.areAllRows(row -> row.getSeries() instanceof BooleanSeries));
	}

	@Override public Node getChart(Chart chart) {
		HBox container = new HBox();
		container.setSpacing(4);
		chart.getRows().map(row -> {
			Label name = new Label();
			name.textProperty().bind(row.getSeries().nameProperty());
			Led led = new Led();
			if(row.getSeries().nameProperty().get().contains("Warning")) {
				led.setLedColor(Color.YELLOW);
			}
			JavaFxObservable.emitOnChanged((ObservableList<? extends Boolean>) row.getSeries().get()).map(list -> {
				if(list.isEmpty()) {
					return false;
				}
				return list.get(list.size()-1);
			}).subscribe(state -> led.setOn(state));
			return new VBox(name, led);
		}).forEach(led -> container.getChildren().add(led));
		return container;
	}

	@Override public String getName() {
		return "LED";
	}

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new LEDChartSetup(chart, FXCollections.observableArrayList());
	}

	@DefaultSerializer(LEDTypeSetupSerializer.class)
	public static class LEDChartSetup implements IChartTypeSetup {

		private final Chart chart;
		private final ObservableList<LEDSeries> series;

		public LEDChartSetup(Chart chart, ObservableList<LEDSeries> series) {
			this.chart = chart;
			this.series = series;
			chart.getChildren().addListener((InvalidationListener) e -> update());
			update();
		}

		@Override public Node createChart() {
			HBox container = new HBox();
			series.forEach(model -> {
				Label title = new Label();
				title.textProperty().bind(model.name);
				Led led = new Led();
				model.getColor().ifPresent(color -> led.setLedColor(color));
				model.colorProperty().addListener(e -> model.getColor().ifPresent(color -> led.setLedColor(color)));
				led.setLedType(model.getLedType());
				model.ledTypeProperty().addListener(e -> led.setLedType(model.getLedType()));
				container.getChildren().add(new VBox(title, led));
			});
			return container;
		}

		@Override public Node createConfiguration() {
			VBox container = new VBox();
			JavaFxObservable.additionsOf(series).subscribe(e -> container.getChildren().add(new FXForm<LEDSeries>(e)));
			JavaFxObservable.removalsOf(series).subscribe(e -> {
				List<Node> toBeRemoved = container.getChildren().stream().filter(node -> node instanceof FXForm).filter(node -> ((FXForm) node).getSource() == e).collect(Collectors.toList());
				container.getChildren().removeAll(toBeRemoved);
			});
			EasyBind.listBind(container.getChildren(), EasyBind.map(series, led -> new FXForm<LEDSeries>(led)));
			return container;
		}

		private void update() {
			List<ISeries<Boolean>> requiredSeries = chart.getColumns().map(column -> (ISeries<Boolean>) column.getSeries()).collect(Collectors.toList());

			List<LEDSeries> noLongerRequired = series.stream().filter(serie -> !requiredSeries.stream().filter(required -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			series.removeAll(noLongerRequired);

			List<ISeries<Boolean>> toBeCreated = requiredSeries.stream().filter(required -> !series.stream().filter(serie -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			toBeCreated.stream().map(values -> {
				StringProperty name = new SimpleStringProperty(values.nameProperty().get());
				return new LEDSeries(values, name, new SimpleObjectProperty<>(Color.RED), new SimpleObjectProperty<>(Led.LedType.ROUND));
			}).forEach(piece -> series.add(piece));
		}

		public Chart getChart() {
			return chart;
		}

		public ObservableList<LEDSeries> getSeries() {
			return series;
		}
	}

	public static class LEDTypeSetupSerializer extends CaesarSerializer<LEDChartSetup> {

		@Override
		public void write(Kryo kryo, Output output, LEDChartSetup object) {
			kryo.writeObject(output, object.getChart());
			writeObservableList(kryo, output, object.getSeries());
		}

		@Override
		public LEDChartSetup read(Kryo kryo, Input input, Class<LEDChartSetup> type) {
			Chart chart = kryo.readObject(input, Chart.class);
			ObservableList<LEDSeries> pieces = readObservableList(kryo, input, LEDSeries.class);
			return new LEDChartSetup(chart, pieces);
		}
	}

	@DefaultSerializer(LEDSeriesSerializer.class)
	public static class LEDSeries {

		@NonVisual
		private final ISeries<Boolean> values;
		private final StringProperty name;
		private final ObjectProperty<Color> color;
		private final ObjectProperty<Led.LedType> ledType;

		public LEDSeries(ISeries<Boolean> values, StringProperty name, ObjectProperty<Color> color, ObjectProperty<Led.LedType> ledType) {
			this.values = values;
			this.name = name;
			this.color = color;
			this.ledType = ledType;
		}

		public ObjectProperty<Color> colorProperty() {
			return color;
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

		public ISeries<Boolean> getValues() {
			return values;
		}

		public ObjectProperty<Led.LedType> ledTypeProperty() {
			return ledType;
		}

		public Led.LedType getLedType() {
			return ledType.get();
		}
	}

	public static class LEDSeriesSerializer extends CaesarSerializer<LEDSeries> {

		@Override
		public void write(Kryo kryo, Output output, LEDSeries object) {
			kryo.writeClassAndObject(output, object.getValues());
			output.writeString(object.getName());
			output.writeBoolean(object.getColor().isPresent());
			object.getColor().ifPresent(color -> kryo.writeObject(output, color));
			kryo.writeObject(output, object.getLedType());
		}

		@Override
		public LEDSeries read(Kryo kryo, Input input, Class<LEDSeries> type) {
			ISeries<Boolean> values = (ISeries<Boolean>) kryo.readClassAndObject(input);
			StringProperty name = readStringProperty(input);
			ObjectProperty<Color> color = readColor(kryo, input);
			ObjectProperty<Led.LedType> ledType = new SimpleObjectProperty<>(kryo.readObject(input, Led.LedType.class));
			return new LEDSeries(values, name, color, ledType);
		}

		public ObjectProperty<Color> readColor(Kryo kryo, Input input) {
			if(input.readBoolean()) {
				return new SimpleObjectProperty<>(kryo.readObject(input, Color.class));
			}
			return new SimpleObjectProperty<>();
		}

	}

}
