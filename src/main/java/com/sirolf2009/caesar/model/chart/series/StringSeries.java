package com.sirolf2009.caesar.model.chart.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.StringSeriesSerializer.class)
public class StringSeries extends SimpleCategorySeries<String> {

    public StringSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

}
