package com.sirolf2009.caesar.model.table;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.JMXAttributeSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@DefaultSerializer(JMXAttributeSerializer.class)
public class JMXAttribute implements IDataPointer {

    private final StringProperty name;
    private final ObjectName objectName;
    private final MBeanAttributeInfo attributeInfo;

    public JMXAttribute(ObjectName objectName, MBeanAttributeInfo attributeInfo) {
        this(objectName, attributeInfo, new SimpleStringProperty(attributeInfo.getName()));
    }

    public JMXAttribute(ObjectName objectName, MBeanAttributeInfo attributeInfo, StringProperty name) {
        this.objectName = objectName;
        this.attributeInfo = attributeInfo;
        this.name = name;
    }

    @Override public void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception {
        attributes.put(this, connection.getAttribute(objectName, attributeInfo.getName()));
    }

    @Override public String getType() {
        return attributeInfo.getType();
    }

    @Override public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JMXAttribute that = (JMXAttribute) o;
        return Objects.equals(objectName, that.objectName) &&
                Objects.equals(attributeInfo, that.attributeInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, attributeInfo);
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public MBeanAttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

    @Override
    public ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}
