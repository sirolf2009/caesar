package com.sirolf2009.caesar.model.table;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.JMXAttributeSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.io.IOException;
import java.util.Objects;

@DefaultSerializer(JMXAttributeSerializer.class)
public class JMXCompositeAttribute implements IDataPointer {

    private final StringProperty name;
    private final ObjectName objectName;
    private final OpenMBeanAttributeInfoSupport attributeInfo;
    private final String subAttributeName;

    public JMXCompositeAttribute(ObjectName objectName, OpenMBeanAttributeInfoSupport attributeInfo, String subAttributeName) {
        this(objectName, attributeInfo, subAttributeName, new SimpleStringProperty(attributeInfo.getName() + "/" + subAttributeName));
    }

    public JMXCompositeAttribute(ObjectName objectName, OpenMBeanAttributeInfoSupport attributeInfo, String subAttributeName, StringProperty name) {
        this.objectName = objectName;
        this.attributeInfo = attributeInfo;
        this.subAttributeName = subAttributeName;
        this.name = name;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception {
        CompositeData data = (CompositeData) connection.getAttribute(objectName, attributeInfo.getName());
        attributes.put(this, data.get(subAttributeName));
    }

    @Override
    public String getType() {
        return getCompositeType().getType(subAttributeName).getTypeName();
    }

    public CompositeType getCompositeType() {
        return (CompositeType) attributeInfo.getOpenType();
    }

    @Override
    public String toString() {
        return attributeInfo.getName() + "/" + subAttributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JMXCompositeAttribute that = (JMXCompositeAttribute) o;
        return Objects.equals(objectName, that.objectName) && Objects.equals(attributeInfo, that.attributeInfo) && Objects.equals(subAttributeName, that.subAttributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, attributeInfo, subAttributeName);
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public OpenMBeanAttributeInfoSupport getAttributeInfo() {
        return attributeInfo;
    }

    public String getSubAttributeName() {
        return subAttributeName;
    }

    @Override
    public ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}
