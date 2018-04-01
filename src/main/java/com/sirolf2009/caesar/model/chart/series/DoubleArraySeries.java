package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(DoubleArraySeries.DoubleArraySeriesSerializer.class)
public class DoubleArraySeries extends ArraySeries<Double> implements INumberArraySeries<Double> {

    public DoubleArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class DoubleArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new DoubleArraySeries(table, attribute);
        }
    }

}
