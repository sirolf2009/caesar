package com.sirolf2009.caesar.util;

import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class ChartUtil {

	public static void setLineColor(XYChart.Series series, Color color) {
		Node line = series.getNode().lookup(".chart-series-area-line");
		String rgb = String.format("%d, %d, %d",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
		line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
	}

	public static void setAreaColor(XYChart.Series series, Color color) {
		Node fill = series.getNode().lookup(".chart-series-area-fill");
		String rgb = String.format("%d, %d, %d",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
		fill.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
	}

	public static Disposable showMarkers(XYChart.Series series, boolean show) {
		series.getData().forEach(data -> showMarker((XYChart.Data) data, show));
		return JavaFxObservable.additionsOf(series.getData()).subscribe(data -> showMarker((XYChart.Data) data, show));
	}

	public static void showMarker(XYChart.Data data, boolean show) {
		if(data.getNode() != null) {
			data.getNode().setVisible(show);
		}
	}

}
