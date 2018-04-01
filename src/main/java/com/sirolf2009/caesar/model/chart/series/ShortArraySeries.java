package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(ShortArraySeries.ShortArraySeriesSerializer.class)
public class ShortArraySeries extends ArraySeries<Byte> implements INumberArraySeries<Short> {

	public ShortArraySeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	public static class ShortArraySeriesSerializer extends SeriesSerializer {

		@Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
			return new ShortArraySeries(table, attribute);
		}
	}

}
