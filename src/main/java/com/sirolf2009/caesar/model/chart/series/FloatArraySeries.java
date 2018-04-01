package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(FloatArraySeries.FloatArraySeriesSerializer.class)
public class FloatArraySeries extends ArraySeries<Float> implements INumberArraySeries<Float> {

    public FloatArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class FloatArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new FloatArraySeries(table, attribute);
        }
    }

}
