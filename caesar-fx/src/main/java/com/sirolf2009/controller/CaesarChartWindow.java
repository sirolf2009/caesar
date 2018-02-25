package com.sirolf2009.controller;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import jfxtras.scene.control.window.Window;

public class CaesarChartWindow extends Window {

    public CaesarChartWindow() {
        super("linechart");

        VBox root = new VBox();

        HBox yAndChart = new HBox();
        AnchorPane yAxisAnchor = new AnchorPane();
        yAxisAnchor.setMaxWidth(32);
        yAxisAnchor.setMinWidth(32);
        yAxisAnchor.setStyle("-fx-border-color: black");
        yAndChart.getChildren().add(yAxisAnchor);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        yAndChart.getChildren().add(new LineChart(xAxis, yAxis));
        root.getChildren().add(yAndChart);

        HBox spacerAndX = new HBox();
        HBox spacer = new HBox();
        spacer.setMaxWidth(32);
        spacer.setMinWidth(32);
        spacer.setMaxHeight(32);
        spacer.setMinHeight(32);
        spacerAndX.getChildren().add(spacer);
        AnchorPane xAxisAnchor = new AnchorPane();
        xAxisAnchor.setMaxHeight(32);
        xAxisAnchor.setMinHeight(32);
        xAxisAnchor.setStyle("-fx-border-color: black");
        HBox.setHgrow(xAxisAnchor, Priority.ALWAYS);
        spacerAndX.getChildren().add(xAxisAnchor);
        root.getChildren().add(spacerAndX);

        setContentPane(root);

        getContentPane().setPadding(new Insets(8));
    }

}
