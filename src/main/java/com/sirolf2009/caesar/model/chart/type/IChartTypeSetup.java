package com.sirolf2009.caesar.model.chart.type;

import javafx.scene.Node;

public interface IChartTypeSetup {

	String getName();
	Node createChart();
	Node createConfiguration();

}
