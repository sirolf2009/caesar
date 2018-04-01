package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

import java.util.Date;

@DefaultSerializer(DateArraySeries.DateArraySeriesSerializer.class)
public class DateArraySeries extends ArraySeries<Date> implements ICategoryArraySeries<Date> {

    public DateArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class DateArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new DateArraySeries(table, attribute);
        }
    }

}
