package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;

import java.util.Date;

public class DateSeries extends SimpleSeries<Date> implements ICategorySeries<Date> {

	public DateSeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	@Override public Date getDefault() {
		return new Date(0);
	}
}
