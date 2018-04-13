package com.sirolf2009.caesar.model.chart.type.xy;

import com.dooapp.fxform.FXForm;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.ColumnOrRows;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.chart.type.AbstractComparisonChartSetup;
import com.sirolf2009.caesar.util.ChartUtil;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractLineChartSetup<X, Y> extends AbstractComparisonChartSetup<X, Y, XYSeries<X, Y>> {

	public AbstractLineChartSetup(ColumnOrRows chartSeries, ObservableList<XYSeries<X, Y>> series) {
		super(chartSeries, series);
	}

	@Override public Node createChart() {
		LineChart<X, Y> lineChart = new LineChart<X, Y>(createXAxis(), createYAxis());
		Map<XYSeries<X, Y>, XYChart.Series> chartSeries = new HashMap<>();
		getSeries().forEach(serie -> chartSeries.put(serie, buildSeries(serie)));
		chartSeries.values().forEach(series -> lineChart.getData().add(series));
		JavaFxObservable.removalsOf(getSeries()).subscribe(removed -> {
			lineChart.getData().remove(chartSeries.get(removed));
			chartSeries.remove(removed);
		});
		JavaFxObservable.additionsOf(getSeries()).subscribe(added -> {
			chartSeries.put(added, buildSeries(added));
			lineChart.getData().add(chartSeries.get(added));
		});
		return lineChart;
	}

	public XYChart.Series<X, Y> buildSeries(XYSeries<X, Y> serie) {
		XYChart.Series<X, Y> series = new XYChart.Series<>();
		series.nameProperty().bind(serie.nameProperty());
		serie.colorProperty().addListener(e -> serie.getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor)));
		serie.getColor().ifPresent(selectedColor -> ChartUtil.setLineColor(series, selectedColor));
		ChartUtil.showMarkers(series, serie.isShowMarkers());
		serie.showMarkersProperty().addListener(e -> ChartUtil.showMarkers(series, serie.isShowMarkers()));
		IntStream.range(0, Math.min(serie.getX().get().size(), serie.getY().get().size())).forEach(i -> series.getData().add(new XYChart.Data<>(serie.getX().get().get(i), serie.getY().get().get(i))));
		JavaFxObservable.additionsOf(serie.getX().get()).zipWith(JavaFxObservable.additionsOf(serie.getY().get()), (a, b) -> {
			return new XYChart.Data<X, Y>(a, b);
		}).subscribe((item -> series.getData().add(item)));
		return series;
	}

	protected abstract Axis<X> createXAxis();
	protected abstract Axis<Y> createYAxis();

	@Override public Node createConfiguration() {
		update();
		VBox container = new VBox();
//		JavaFxObservable.additionsOf(getSeries()).subscribe(e -> container.getChildren().add(new FXForm<XYSeries>(e)));
//		JavaFxObservable.removalsOf(getSeries()).subscribe(e -> {
//			List<Node> toBeRemoved = container.getChildren().stream().filter(node -> node instanceof FXForm).filter(node -> ((FXForm)node).getSource() == e).collect(Collectors.toList());
//			container.getChildren().removeAll(toBeRemoved);
//		});
		EasyBind.listBind(container.getChildren(), EasyBind.map(getSeries(), serie -> new FXForm<XYSeries>(serie)));
		return container;
	}

	@Override protected XYSeries<X, Y> createContainer(ISeries<X> x, ISeries<Y> y) {
		return new XYSeries<>(x, y, new SimpleStringProperty(x.getName()+"/"+y.getName()), new SimpleObjectProperty<>(null), new SimpleBooleanProperty(false));
	}

}
