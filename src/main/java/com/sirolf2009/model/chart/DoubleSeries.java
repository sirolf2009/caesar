package com.sirolf2009.model.chart;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.serializer.ChartSerializer;
import com.sirolf2009.model.serializer.SeriesSerializer;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

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