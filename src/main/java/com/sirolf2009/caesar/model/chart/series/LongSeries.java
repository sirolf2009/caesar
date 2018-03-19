package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.LongSeriesSerializer.class)
public class LongSeries extends SimpleNumberSeries<Long> {

    public LongSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Long getDefault() {
        return 0l;
    }
}
