package com.sirolf2009.model.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.DoubleSeriesSerializer.class)
public class DoubleSeries extends SimpleNumberSeries<Double> {

    public DoubleSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Double getDefault() {
        return 0d;
    }
}
