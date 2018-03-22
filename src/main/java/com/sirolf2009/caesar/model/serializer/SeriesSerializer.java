package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.chart.series.*;

public abstract class SeriesSerializer extends Serializer<SimpleSeries> {

    @Override
    public void write(Kryo kryo, Output output, SimpleSeries object) {
        kryo.writeObject(output, object.getTable());
        kryo.writeClassAndObject(output, object.getAttribute());
    }

    @Override
    public SimpleSeries read(Kryo kryo, Input input, Class<SimpleSeries> type) {
        return getSeries(kryo.readObject(input, Table.class), (IDataPointer) kryo.readClassAndObject(input));
    }

    public abstract SimpleSeries getSeries(Table table, IDataPointer attribute);

    public static class BooleanSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new BooleanSeries(table, attribute);
        }
    }

    public static class DoubleSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new DoubleSeries(table, attribute);
        }
    }

    public static class IntegerSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new IntegerSeries(table, attribute);
        }
    }

    public static class LongSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new LongSeries(table, attribute);
        }
    }

    public static class StringSeriesSerializer extends SeriesSerializer {
        @Override
        public SimpleSeries getSeries(Table table, IDataPointer attribute) {
            return new StringSeries(table, attribute);
        }
    }

}
