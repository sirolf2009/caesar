package com.sirolf2009.caesar.model.table;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.MBeanServerConnection;
import java.util.Date;

@DefaultSerializer(CurrentTime.CurrentTimeSerializer.class)
public class CurrentTime implements IDataPointer {

	private final StringProperty name;

	public CurrentTime() {
		this("Time");
	}

	public CurrentTime(String name) {
		this(new SimpleStringProperty(name));
	}

	public CurrentTime(StringProperty name) {
		this.name = name;
	}

	@Override public void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception {
		attributes.put(this, new Date());
	}

	@Override public String getType() {
		return Date.class.getTypeName();
	}

	@Override public StringProperty nameProperty() {
		return name;
	}

	@Override public String toString() {
		return getName();
	}

	@Override public ObservableList getChildren() {
		return FXCollections.emptyObservableList();
	}

	public static class CurrentTimeSerializer extends CaesarSerializer<CurrentTime> {

		@Override public void write(Kryo kryo, Output output, CurrentTime object) {
			output.writeString(object.getName());
		}

		@Override public CurrentTime read(Kryo kryo, Input input, Class<CurrentTime> type) {
			return new CurrentTime(readStringProperty(input));
		}
	}

}
