package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.series.INumberSeries;
import javafx.scene.Node;

import java.util.function.Predicate;

public interface IChartType {

	Predicate<ColumnOrRow> isNumbers = series -> series.getSeries() instanceof INumberSeries;
	Predicate<ColumnOrRow> isCategories = series -> series.getSeries() instanceof INumberSeries;
	Predicate<Chart> areColumnsNumbers = chart -> chart.getColumns().allMatch(isNumbers);
	Predicate<Chart> areRowsNumbers = chart -> chart.getRows().allMatch(isNumbers);
	Predicate<Chart> areColumnsCategories = chart -> chart.getColumns().allMatch(isCategories);
	Predicate<Chart> areRowsCategories = chart -> chart.getRows().allMatch(isCategories);
	Predicate<Chart> hasColumns = chart -> chart.getColumns().count() > 0;
	Predicate<Chart> hasRows = chart -> chart.getRows().count() > 0;

	Predicate<Chart> getPredicate();
	Node getChart(Chart chart);
	String getName();

}
