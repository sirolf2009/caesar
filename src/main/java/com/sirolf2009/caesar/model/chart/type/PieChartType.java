package com.sirolf2009.caesar.model.chart.type;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.annotation.NonVisual;
import com.esotericsoftware.kryo.DefaultSerializer;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.fxmisc.easybind.EasyBind;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieChartType implements IChartType {

	@Override public Predicate<Chart> getPredicate() {
		return hasColumns.and(areColumnsNumbers).and(hasRows.negate());
	}

	@Override public Node getChart(Chart chart) {
		PieChart pieChart = new PieChart();
		pieChart.setData(FXCollections.observableArrayList(chart.getColumns().map(column -> (INumberSeries) column.getSeries()).map(column -> {
			ObservableList<Number> columnSeries = (ObservableList<Number>) column.get();
			PieChart.Data data = new PieChart.Data(column.getName(), columnSeries.isEmpty() ? 0d : columnSeries.get(columnSeries.size() - 1).doubleValue());
			data.nameProperty().bind(column.nameProperty());
			columnSeries.addListener((InvalidationListener) event -> {
				data.setPieValue(columnSeries.get(columnSeries.size() - 1).doubleValue());
			});
			return data;
		}).collect(Collectors.toList())));
		return pieChart;
	}

	@Override public String getName() {
		return "Pie Chart";
	}

	@Override public IChartTypeSetup getSetup(Chart chart) {
		return new PieChartSetup(chart, FXCollections.observableArrayList());
	}

	@DefaultSerializer(GaugeChartTypeSetupSerializer.class)
	public static class PieChartSetup implements IChartTypeSetup {

		private final Chart chart;
		private final ObservableList<Piece> pieces;

		public PieChartSetup(Chart chart, ObservableList<Piece> pieces) {
			this.chart = chart;
			this.pieces = pieces;
			chart.getChildren().addListener((InvalidationListener) e -> update());
			update();
		}

		@Override public Node createChart() {
			PieChart pieChart = new PieChart();
			pieChart.setData(EasyBind.map(pieces, piece -> piece.data));
			return pieChart;
		}

		@Override public Node createConfiguration() {
			VBox container = new VBox();
			JavaFxObservable.additionsOf(pieces).subscribe(e -> container.getChildren().add(new FXForm<Piece>(e)));
			JavaFxObservable.removalsOf(pieces).subscribe(e -> {
				List<Node> toBeRemoved = container.getChildren().stream().filter(node -> node instanceof FXForm).filter(node -> ((FXForm) node).getSource() == e).collect(Collectors.toList());
				container.getChildren().removeAll(toBeRemoved);
			});
			EasyBind.listBind(container.getChildren(), EasyBind.map(pieces, piece -> new FXForm<Piece>(piece)));
			return container;
		}

		private void update() {
			List<INumberSeries> requiredSeries = chart.getColumns().map(column -> (INumberSeries) column.getSeries()).collect(Collectors.toList());

			List<Piece> noLongerRequired = pieces.stream().filter(serie -> !requiredSeries.stream().filter(required -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			pieces.removeAll(noLongerRequired);

			List<INumberSeries> toBeCreated = requiredSeries.stream().filter(required -> !pieces.stream().filter(serie -> required == serie.values).findAny().isPresent()).collect(Collectors.toList());
			toBeCreated.stream().map(values -> {
				StringProperty name = new SimpleStringProperty(values.nameProperty().get());
				return new Piece(values, name, new SimpleObjectProperty<>(Color.RED));
			}).forEach(piece -> pieces.add(piece));
		}

		public Chart getChart() {
			return chart;
		}

		public ObservableList<Piece> getPieces() {
			return pieces;
		}
	}

	public static class GaugeChartTypeSetupSerializer extends CaesarSerializer<PieChartSetup> {

		@Override
		public void write(Kryo kryo, Output output, PieChartSetup object) {
			kryo.writeObject(output, object.getChart());
			writeObservableList(kryo, output, object.getPieces());
		}

		@Override
		public PieChartSetup read(Kryo kryo, Input input, Class<PieChartSetup> type) {
			Chart chart = kryo.readObject(input, Chart.class);
			ObservableList<Piece> pieces = readObservableList(kryo, input, Piece.class);
			return new PieChartSetup(chart, pieces);
		}
	}

	@DefaultSerializer(PieceSerializer.class)
	public static class Piece {

		@NonVisual private final ISeries<Number> values;
		@NonVisual private final PieChart.Data data;
		private final StringProperty name;
		private final ObjectProperty<Color> color;

		public Piece(ISeries<Number> values, StringProperty name, ObjectProperty<Color> color) {
			this.values = values;
			this.name = name;
			this.color = color;

			data = new PieChart.Data(name.get(), 0);
			data.nameProperty().bind(name);
			if(values.get().isEmpty()) {
				data.pieValueProperty().set(0);
			} else {
				data.pieValueProperty().set((values.get().get(values.get().size() - 1)).doubleValue());
			}
			JavaFxObservable.additionsOf(values.get()).subscribe(newValue -> data.pieValueProperty().set(newValue.doubleValue()));
			color.addListener(e -> getColor().ifPresent(selectedColor -> ChartUtil.setPiePieceColor(data, selectedColor)));
			getColor().ifPresent(selectedColor -> ChartUtil.setPiePieceColor(data, selectedColor));
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

		public ISeries<Number> getValues() {
			return values;
		}
	}

	public static class PieceSerializer extends CaesarSerializer<Piece> {

		@Override
		public void write(Kryo kryo, Output output, Piece object) {
			kryo.writeClassAndObject(output, object.getValues());
			output.writeString(object.getName());
			output.writeBoolean(object.getColor().isPresent());
			object.getColor().ifPresent(color -> kryo.writeObject(output, color));
		}

		@Override
		public Piece read(Kryo kryo, Input input, Class<Piece> type) {
			ISeries<Number> values = (ISeries<Number>) kryo.readClassAndObject(input);
			StringProperty name = readStringProperty(input);
			ObjectProperty<Color> color = readColor(kryo, input);
			return new Piece(values, name, color);
		}

		public ObjectProperty<Color> readColor(Kryo kryo, Input input) {
			if(input.readBoolean()) {
				return new SimpleObjectProperty<>(kryo.readObject(input, Color.class));
			}
			return new SimpleObjectProperty<>();
		}

	}

}
