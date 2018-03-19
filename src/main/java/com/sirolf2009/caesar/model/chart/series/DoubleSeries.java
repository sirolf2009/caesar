package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.DoubleSeriesSerializer.class)
public class DoubleSeries extends SimpleNumberSeries<Double> {

    public DoubleSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

    @Override
    public Double getDefault() {
        return 0d;
    }
}
