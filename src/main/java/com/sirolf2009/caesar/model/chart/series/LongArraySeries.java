package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(LongArraySeries.LongArraySeriesSerializer.class)
public class LongArraySeries extends ArraySeries<Long> implements INumberArraySeries<Long> {

	public LongArraySeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	public static class LongArraySeriesSerializer extends SeriesSerializer {

		@Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
			return new LongArraySeries(table, attribute);
		}
	}

}
