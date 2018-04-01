package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(BooleanArraySeries.BooleanArraySeriesSerializer.class)
public class BooleanArraySeries extends ArraySeries<Boolean> implements ICategoryArraySeries<Boolean> {

    public BooleanArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class BooleanArraySeriesSerializer extends SeriesSerializer {

        @Override public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new BooleanArraySeries(table, attribute);
        }
    }

}
