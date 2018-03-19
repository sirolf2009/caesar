package com.sirolf2009.model.series;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.serializer.SeriesSerializer;

@DefaultSerializer(SeriesSerializer.BooleanSeriesSerializer.class)
public class BooleanSeries extends SimpleCategorySeries<Boolean> {

    public BooleanSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Boolean getDefault() {
        return false;
    }
}
