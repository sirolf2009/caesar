package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.LongSeriesSerializer.class)
public class LongSeries extends SimpleNumberSeries<Long> {

    public LongSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

}
