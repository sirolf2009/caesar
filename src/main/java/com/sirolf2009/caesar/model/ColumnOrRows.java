package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.serializer.ColumnSerializer;
import com.sirolf2009.caesar.model.serializer.RowSerializer;
import javafx.collections.ObservableList;

import java.util.Objects;

@DefaultSerializer(ColumnOrRows.ColumnOrRowsSerializer.class)
public class ColumnOrRows {

    private final ObservableList<ColumnOrRow> data;

    public ColumnOrRows(ObservableList<ColumnOrRow> data) {
        this.data = data;
    }

    public ObservableList<ColumnOrRow> getData() {
        return data;
    }

    @Override public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof ColumnOrRows))
            return false;
        ColumnOrRows that = (ColumnOrRows) o;
        return Objects.equals(getData(), that.getData());
    }

    @Override public int hashCode() {
        return Objects.hash(getData());
    }

    public static class ColumnOrRowsSerializer extends CaesarSerializer<ColumnOrRows> {

        @Override public void write(Kryo kryo, Output output, ColumnOrRows object) {
            writeObservableListWithClass(kryo, output, object.getData());
        }

        @Override public ColumnOrRows read(Kryo kryo, Input input, Class<ColumnOrRows> type) {
            return new ColumnOrRows(readObservableListWithClass(kryo, input, ColumnOrRow.class));
        }
    }
}
