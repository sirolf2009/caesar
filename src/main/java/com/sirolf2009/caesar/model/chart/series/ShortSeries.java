package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(ShortSeries.ShortSeriesSerializer.class)
public class ShortSeries extends SimpleNumberSeries<Short> {

    public ShortSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class ShortSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new ShortSeries(table, attribute);
        }
    }

}
