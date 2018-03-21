package com.sirolf2009.caesar.model.table;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.serializer.JMXAttributeSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.io.IOException;
import java.util.Objects;

@DefaultSerializer(JMXCompositeAttribute.JMXCompositeAttributeSerializer.class)
public class JMXCompositeAttribute implements IDataPointer {

    private final StringProperty name;
    private final ObjectName objectName;
    private final String attributeName;
    private final String subAttributeName;
    private final String type;

    public JMXCompositeAttribute(ObjectName objectName, OpenMBeanAttributeInfo attributeInfo, String subAttributeName) {
        this(new SimpleStringProperty(attributeInfo.getName() + "/" + subAttributeName), objectName, attributeInfo.getName(), subAttributeName, ((CompositeType) attributeInfo.getOpenType()).getType(subAttributeName).getTypeName());
    }

    public JMXCompositeAttribute(StringProperty name, ObjectName objectName, String attributeName, String subAttributeName, String type) {
        this.name = name;
        this.objectName = objectName;
        this.attributeName = attributeName;
        this.subAttributeName = subAttributeName;
        this.type = type;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception {
        CompositeData data = (CompositeData) connection.getAttribute(objectName, attributeName);
        attributes.put(this, data.get(subAttributeName));
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JMXCompositeAttribute that = (JMXCompositeAttribute) o;
        return Objects.equals(objectName, that.objectName) && Objects.equals(attributeName, that.attributeName) && Objects.equals(subAttributeName, that.subAttributeName) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, attributeName, subAttributeName);
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getSubAttributeName() {
        return subAttributeName;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }

    public static class JMXCompositeAttributeSerializer extends CaesarSerializer<JMXCompositeAttribute> {

        @Override public void write(Kryo kryo, Output output, JMXCompositeAttribute object) {
            output.writeString(object.getName());
            writeObjectName(output, object.getObjectName());
            output.writeString(object.getAttributeName());
            output.writeString(object.getSubAttributeName());
            output.writeString(object.getType());
        }

        @Override public JMXCompositeAttribute read(Kryo kryo, Input input, Class<JMXCompositeAttribute> type) {
            return new JMXCompositeAttribute(readStringProperty(input), readObjectName(input), input.readString(), input.readString(), input.readString());
        }
    }
}
