package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;

import java.util.Date;

@DefaultSerializer(DateSeries.DateSeriesSerializer.class)
public class DateSeries extends SimpleSeries<Date> implements ICategorySeries<Date> {

	public DateSeries(Table table, IDataPointer attribute) {
		super(table, attribute);
	}

	public static class DateSeriesSerializer extends SeriesSerializer {

		@Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
			return new DateSeries(table, attribute);
		}
	}

}
