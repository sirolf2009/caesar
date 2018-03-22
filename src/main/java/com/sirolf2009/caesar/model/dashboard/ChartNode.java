package com.sirolf2009.caesar.model.dashboard;

import com.sirolf2009.caesar.component.ChartTab;
import com.sirolf2009.caesar.model.Chart;
import javafx.scene.Node;

public class ChartNode implements IDataNode {

    private final Chart chart;

    public ChartNode(Chart chart) {
        this.chart = chart;
    }

    @Override
    public Node createNode() {
        return ChartTab.chartTypes.stream().filter(type -> type.getPredicate().test(chart)).findAny().get().getChart(chart);
    }
}
