package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;

public class LongArraySeries extends ArraySeries<Long> implements INumberArraySeries<Long> {

	public LongArraySeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	@Override public Long[] getDefault() {
		return new Long[0];
	}
}
