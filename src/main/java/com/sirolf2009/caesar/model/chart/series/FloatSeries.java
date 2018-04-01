package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(FloatSeries.FloatSeriesSerializer.class)
public class FloatSeries extends SimpleNumberSeries<Float> {

    public FloatSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class FloatSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new FloatSeries(table, attribute);
        }
    }

}
