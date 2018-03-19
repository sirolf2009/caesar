package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.table.JMXAttribute;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JMXAttributeSerializer extends Serializer<JMXAttribute> {

	@Override public void write(Kryo kryo, Output output, JMXAttribute object) {
		output.writeString(object.getObjectName().toString());
		output.writeString(object.getAttributeInfo().getName());
		output.writeString(object.getAttributeInfo().getType());
		output.writeString(object.getAttributeInfo().getDescription());
		output.writeBoolean(object.getAttributeInfo().isReadable());
		output.writeBoolean(object.getAttributeInfo().isWritable());
		output.writeBoolean(object.getAttributeInfo().isIs());

	}

	@Override public JMXAttribute read(Kryo kryo, Input input, Class<JMXAttribute> type) {
		String objectNameSpec = input.readString();
		try {
			ObjectName objectName = new ObjectName(objectNameSpec);
			MBeanAttributeInfo info = new MBeanAttributeInfo(input.readString(), input.readString(), input.readString(), input.readBoolean(), input.readBoolean(), input.readBoolean());
			return new JMXAttribute(objectName, info);
		} catch(MalformedObjectNameException e) {
			throw new RuntimeException(objectNameSpec + " is not a valid objectName", e);
		}
	}
}
