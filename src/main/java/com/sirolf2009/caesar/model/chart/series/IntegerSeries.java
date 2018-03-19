package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.IntegerSeriesSerializer.class)
public class IntegerSeries extends SimpleNumberSeries<Integer> {

    public IntegerSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Integer getDefault() {
        return 0;
    }
}
