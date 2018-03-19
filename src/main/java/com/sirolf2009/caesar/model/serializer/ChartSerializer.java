package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class ChartSerializer extends CaesarSerializer<Chart> {

	@Override public void write(Kryo kryo, Output output, Chart object) {
		output.writeString(object.getName());
		writeObservableListWithClass(kryo, output, object.getChildren());
	}

	@Override public Chart read(Kryo kryo, Input input, Class<Chart> type) {
		SimpleStringProperty name = readStringProperty(input);
		ObservableList<ColumnOrRow> series = readObservableListWithClass(kryo, input, ColumnOrRow.class);
		return new Chart(name, series);
	}
}
