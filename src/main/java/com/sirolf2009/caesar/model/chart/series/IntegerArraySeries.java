package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(IntegerArraySeries.IntegerArraySeriesSerializer.class)
public class IntegerArraySeries extends ArraySeries<Integer> implements INumberArraySeries<Integer> {

    public IntegerArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class IntegerArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new IntegerArraySeries(table, attribute);
        }
    }

}
