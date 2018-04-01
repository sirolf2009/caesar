package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(StringArraySeries.StringArraySeriesSerializer.class)
public class StringArraySeries extends ArraySeries<String> implements ICategoryArraySeries<String> {

    public StringArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class StringArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new StringArraySeries(table, attribute);
        }
    }

}
