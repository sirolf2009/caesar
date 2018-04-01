package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(ByteArraySeries.ByteArraySeriesSerializer.class)
public class ByteArraySeries extends ArraySeries<Byte> implements INumberArraySeries<Byte> {

	public ByteArraySeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	public static class ByteArraySeriesSerializer extends SeriesSerializer {

		@Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
			return new ByteArraySeries(table, attribute);
		}
	}

}
