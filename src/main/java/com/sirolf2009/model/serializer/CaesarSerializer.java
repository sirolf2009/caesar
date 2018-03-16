package com.sirolf2009.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CaesarSerializer<T> extends Serializer<T> {

	public void writeObservableList(Kryo kryo, Output output, ObservableList list) {
		output.writeInt(list.size());
		list.forEach(object -> kryo.writeObject(output, object));
	}

	public <T> ObservableList<T> readObservableList(Kryo kryo, Input input, Class<T> type) {
		return FXCollections.observableArrayList(IntStream.range(0, input.readInt()).mapToObj(index -> kryo.readObject(input, type)).collect(Collectors.toList()));
	}

	public SimpleStringProperty readStringProperty(Input input) {
		return new SimpleStringProperty(input.readString());
	}


}
