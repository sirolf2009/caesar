package com.sirolf2009.caesar.model.chart.type;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.annotation.NonVisual;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.util.ChartUtil;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.fxmisc.easybind.EasyBind;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BarChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsNumbers).and(hasRows.negate());
	}

	@Override public Node getChart(Chart chart) {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
		chart.getColumns().map(column -> (INumberSeries)column.getSeries()).forEach(column -> {
			XYChart.Series series = new XYChart.Series();
			ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
			series.nameProperty().bindBidirectional(column.nameProperty());
			XYChart.Data<String, Number> data = new XYChart.Data<>(column.getName(), columnSeries.isEmpty() ? 0d : columnSeries.get(columnSeries.size()-1));
			columnSeries.addListener((InvalidationListener) event -> {
				data.setYValue(columnSeries.get(columnSeries.size() - 1));
			});
			series.getData().add(data);
			barChart.getData().add(series);
		});
		return barChart;
	}

	@Override public String getName() {
		return "Bar Chart";
	}

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new BarChartSetup(chart, FXCollections.observableArrayList());
	}

	public static class BarChartSetup implements IChartTypeSetup {

		private final Chart chart;
		private final ObservableList<Bar> bars;

		public BarChartSetup(Chart chart, ObservableList<Bar> bars) {
			this.chart = chart;
			this.bars = bars;
			chart.getChildren().addListener((InvalidationListener) e -> update());
			update();
		}

		@Override public Node createChart() {
			CategoryAxis xAxis = new CategoryAxis();
			NumberAxis yAxis = new NumberAxis();
			BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
			barChart.setData(EasyBind.map(bars, serie -> serie.series));
			return barChart;
		}

		@Override public Node createConfiguration() {
			VBox container = new VBox();
			JavaFxObservable.additionsOf(bars).subscribe(e -> container.getChildren().add(new FXForm<Bar>(e)));
			JavaFxObservable.removalsOf(bars).subscribe(e -> {
				List<Node> toBeRemoved = container.getChildren().stream().filter(node -> node instanceof FXForm).filter(node -> ((FXForm) node).getSource() == e).collect(Collectors.toList());
				container.getChildren().removeAll(toBeRemoved);
			});
			EasyBind.listBind(container.getChildren(), EasyBind.map(bars, bar -> new FXForm<Bar>(bar)));
			return container;
		}

		private void update() {
			List<INumberSeries> requiredSeries = chart.getColumns().map(column -> (INumberSeries) column.getSeries()).collect(Collectors.toList());

			List<Bar> noLongerRequired = bars.stream().filter(serie -> !requiredSeries.stream().filter(required -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			bars.removeAll(noLongerRequired);

			List<INumberSeries> toBeCreated = requiredSeries.stream().filter(required -> !bars.stream().filter(serie -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			toBeCreated.stream().map(values -> {
				StringProperty name = new SimpleStringProperty(values.nameProperty().get());
				return new Bar(values, name);
			}).forEach(bar -> bars.add(bar));
		}

		public Chart getChart() {
			return chart;
		}

		public ObservableList<Bar> getBars() {
			return bars;
		}
	}

	public static class GaugeChartTypeSetupSerializer extends CaesarSerializer<BarChartSetup> {

		@Override
		public void write(Kryo kryo, Output output, BarChartSetup object) {
			kryo.writeObject(output, object.getChart());
			writeObservableList(kryo, output, object.getBars());
		}

		@Override
		public BarChartSetup read(Kryo kryo, Input input, Class<BarChartSetup> type) {
			Chart chart = kryo.readObject(input, Chart.class);
			ObservableList<Bar> bars = readObservableList(kryo, input, Bar.class);
			return new BarChartSetup(chart, bars);
		}
	}

	public static class Bar {

		@NonVisual
		private final ISeries<Number> values;
		@NonVisual
		private final XYChart.Series<String, Number> series;
		private final StringProperty name;
//		private final ObjectProperty<Color> color;

		public Bar(ISeries<Number> values, StringProperty name) {
			this.values = values;
			this.name = name;
//			this.color = color;

			series = new XYChart.Series<>();
			series.nameProperty().bind(name);
			XYChart.Data<String, Number> data = new XYChart.Data();
			data.XValueProperty().bind(name);
			if(values.get().isEmpty()) {
				data.YValueProperty().set(0);
			} else {
				data.YValueProperty().set(values.get().get(values.get().size()-1));
			}
			JavaFxObservable.additionsOf(values.get()).subscribe(newValue -> data.YValueProperty().set(newValue));
			series.getData().add(data);
//			color.addListener(e -> getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor)));
//			getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor));
		}

//		public Optional<Color> getColor() {
//			return Optional.ofNullable(color.get());
//		}

		public ISeries<Number> getValues() {
			return values;
		}

		public StringProperty nameProperty() {
			return name;
		}

		public String getName() {
			return name.get();
		}
	}

	public static class BarSerializer extends CaesarSerializer<Bar> {

		@Override
		public void write(Kryo kryo, Output output, Bar object) {
			kryo.writeClassAndObject(output, object.getValues());
			output.writeString(object.getName());
		}

		@Override
		public Bar read(Kryo kryo, Input input, Class<Bar> type) {
			ISeries<Number> values = (ISeries<Number>) kryo.readClassAndObject(input);
			StringProperty name = readStringProperty(input);
			return new Bar(values, name);
		}
	}

}
