package com.sirolf2009.caesar.model.table.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.SimplePointer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.MBeanServerConnection;
import java.util.Date;

@DefaultSerializer(LongToDate.LongToDateSerializer.class)
public class LongToDate extends SimplePointer {

	private final IDataPointer pointer;

	public LongToDate(IDataPointer pointer) {
		this(new SimpleStringProperty(pointer.getName()+" -> Date"), pointer);
	}

	public LongToDate(StringProperty name, IDataPointer pointer) {
		super(name);
		this.pointer = pointer;
	}

	@Override public void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception {
		attributes.put(this, new Date((long)attributes.get(pointer)));
	}

	@Override public String getType() {
		return Date.class.getTypeName();
	}

	public IDataPointer getPointer() {
		return pointer;
	}

	public static class LongToDateSerializer extends Serializer<LongToDate> {

		@Override public void write(Kryo kryo, Output output, LongToDate object) {
			output.writeString(object.getName());
			kryo.writeClassAndObject(output, object.getPointer());
		}

		@Override public LongToDate read(Kryo kryo, Input input, Class<LongToDate> type) {
			return new LongToDate(new SimpleStringProperty(input.readString()), (IDataPointer) kryo.readClassAndObject(input));
		}
	}

}
