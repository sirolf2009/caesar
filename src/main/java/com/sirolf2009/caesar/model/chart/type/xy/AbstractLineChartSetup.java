package com.sirolf2009.caesar.model.chart.type.xy;

import com.dooapp.fxform.FXForm;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.chart.type.AbstractComparisonChartSetup;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLineChartSetup<X, Y> extends AbstractComparisonChartSetup<X, Y, XYSeries<X, Y>> {

	public AbstractLineChartSetup(Chart chart, ObservableList<XYSeries<X, Y>> series) {
		super(chart, series);
	}

	@Override public Node createChart() {
		LineChart<X, Y> lineChart = new LineChart<X, Y>(createXAxis(), createYAxis());
		lineChart.setData(EasyBind.map(getSeries(), serie -> serie.getSeries()));
		return lineChart;
	}
	protected abstract Axis<X> createXAxis();
	protected abstract Axis<Y> createYAxis();

	@Override public Node createConfiguration() {
		VBox container = new VBox();
		JavaFxObservable.additionsOf(getSeries()).subscribe(e -> container.getChildren().add(new FXForm<XYSeries>(e)));
		JavaFxObservable.removalsOf(getSeries()).subscribe(e -> {
			List<Node> toBeRemoved = container.getChildren().stream().filter(node -> node instanceof FXForm).filter(node -> ((FXForm)node).getSource() == e).collect(Collectors.toList());
			container.getChildren().removeAll(toBeRemoved);
		});
		EasyBind.listBind(container.getChildren(), EasyBind.map(getSeries(), serie -> new FXForm<XYSeries>(serie)));
		return container;
	}

	@Override protected XYSeries<X, Y> createContainer(ISeries<X> x, ISeries<Y> y) {
		return new XYSeries<>(x, y, new SimpleStringProperty(x.getName()+"/"+y.getName()), new SimpleObjectProperty<>(null), new SimpleBooleanProperty(false));
	}
}
