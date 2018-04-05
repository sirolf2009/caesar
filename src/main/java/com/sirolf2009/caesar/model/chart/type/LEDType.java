package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.BooleanSeries;
import eu.hansolo.enzo.led.Led;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.function.Predicate;

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
}
