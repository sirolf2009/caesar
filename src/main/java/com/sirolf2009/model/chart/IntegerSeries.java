package com.sirolf2009.model.chart;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.serializer.SeriesSerializer;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

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
