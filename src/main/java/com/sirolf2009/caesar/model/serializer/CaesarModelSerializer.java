package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.CaesarModel;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.Chart;
import javafx.collections.ObservableList;

public class CaesarModelSerializer extends CaesarSerializer<CaesarModel> {

	@Override public void write(Kryo kryo, Output output, CaesarModel object) {
		writeObservableList(kryo, output, object.getTables());
		writeObservableList(kryo, output, object.getCharts());
	}

	@Override public CaesarModel read(Kryo kryo, Input input, Class<CaesarModel> type) {
		ObservableList<Table> tables = readObservableList(kryo, input, Table.class);
		ObservableList<Chart> charts = readObservableList(kryo, input, Chart.class);
		return new CaesarModel(tables, charts);
	}
}
