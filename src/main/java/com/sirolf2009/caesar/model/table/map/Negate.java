package com.sirolf2009.caesar.model.table.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.SimplePointer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.management.MBeanServerConnection;
import java.util.function.Function;

@DefaultSerializer(Negate.NegateSerializer.class)
public class Negate extends SimplePointer {

	private final IDataPointer pointer;
	private final Function<Object, Object> negateFunction;

	public Negate(IDataPointer pointer) {
		this(new SimpleStringProperty(pointer.getName()+" Negate"), pointer);
	}

	public Negate(StringProperty name, IDataPointer pointer) {
		super(name);
		this.pointer = pointer;
		this.negateFunction = getNegateFunction(pointer);
	}

	@Override
	public void pullData(MBeanServerConnection connection, JMXAttributes attributes) {
		attributes.put(this, negateFunction.apply(attributes.get(pointer)));
	}

	public Function<Object, Object> getNegateFunction(IDataPointer pointer) {
		switch(pointer.getType()) {
		case "int":
			return object -> ((int)object) * -1;
		case "java.lang.Integer":
			return object -> ((Integer)object) * -1;
		case "long":
			return object -> ((long)object) * -1l;
		case "java.lang.Long":
			return object -> ((Long)object) * -1l;
		case "double":
			return object -> ((double)object) * -1d;
		case "java.lang.Double":
			return object -> ((Double)object) * -1d;
		case "float":
			return object -> ((float)object) * -1f;
		case "java.lang.Float":
			return object -> ((Float)object) * -1f;
		case "boolean":
			return object -> !((boolean)object);
		case "java.lang.Boolean":
			return object -> !((Boolean)object);
		}
		throw new IllegalArgumentException("Unknown type: "+pointer.getType());
	}

	@Override public String getType() {
		return pointer.getType();
	}

	public IDataPointer getPointer() {
		return pointer;
	}

	public static class NegateSerializer extends CaesarSerializer<Negate> {

		@Override public void write(Kryo kryo, Output output, Negate object) {
			output.writeString(object.getName());
			kryo.writeClassAndObject(output, object.getPointer());
		}

		@Override public Negate read(Kryo kryo, Input input, Class<Negate> type) {
			return new Negate(readStringProperty(input), (IDataPointer) kryo.readClassAndObject(input));
		}
	}
}
