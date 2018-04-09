package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.chart.series.DateSeries;
import com.sirolf2009.caesar.model.chart.series.INumberArraySeries;
import com.sirolf2009.caesar.model.chart.series.INumberSeries;
import javafx.scene.Node;

import java.util.Date;
import java.util.function.Predicate;

public interface IChartType {

	Predicate<ColumnOrRow> isNumbers = series -> series.getSeries() instanceof INumberSeries;
	Predicate<Chart> areColumnsNumbers = areAllColumns(isNumbers);
	Predicate<Chart> areRowsNumbers = areAllRows(isNumbers);
	Predicate<ColumnOrRow> isNumberArrays = series -> series.getSeries() instanceof INumberArraySeries;
	Predicate<Chart> areColumnsNumberArrays = areAllColumns(isNumberArrays);
	Predicate<Chart> areRowsNumberArrays = areAllRows(isNumberArrays);
	Predicate<ColumnOrRow> isCategories = series -> series.getSeries() instanceof INumberSeries;
	Predicate<Chart> areColumnsCategories = areAllColumns(isCategories);
	Predicate<Chart> areRowsCategories = areAllRows(isCategories);
	Predicate<ColumnOrRow> isDates = series -> series.getSeries() instanceof DateSeries;
	Predicate<Chart> areColumnsDates = areAllColumns(isDates);
	Predicate<Chart> areRowsDates = areAllRows(isDates);
	Predicate<Chart> hasColumns = chart -> chart.getColumns().count() > 0;
	Predicate<Chart> hasRows = chart -> chart.getRows().count() > 0;

	Predicate<Chart> getPredicate();
	IChartTypeSetup getSetup(Chart chart);
	String getName();

	static Predicate<Chart> areAllColumns(Predicate<ColumnOrRow> predicate) {
		return chart -> chart.getColumns().allMatch(predicate);
	}

	static Predicate<Chart> areAllRows(Predicate<ColumnOrRow> predicate) {
		return chart -> chart.getRows().allMatch(predicate);
	}

}
