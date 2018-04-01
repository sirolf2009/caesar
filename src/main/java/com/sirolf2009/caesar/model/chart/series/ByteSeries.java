package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;

@DefaultSerializer(ByteSeries.ByteSeriesSerializer.class)
public class ByteSeries extends SimpleNumberSeries<Byte> {

    public ByteSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    public static class ByteSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new ByteSeries(table, attribute);
        }
    }

}
