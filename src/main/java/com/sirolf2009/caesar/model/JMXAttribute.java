package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.serializer.JMXAttributeSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import java.io.Serializable;
import java.util.Objects;

@DefaultSerializer(JMXAttributeSerializer.class)
public class JMXAttribute implements IHierarchicalData, Serializable {

    private final ObjectName objectName;
    private final MBeanAttributeInfo attributeInfo;

    public JMXAttribute(ObjectName objectName, MBeanAttributeInfo attributeInfo) {
        this.objectName = objectName;
        this.attributeInfo = attributeInfo;
    }

    @Override
    public String toString() {
        return attributeInfo.getName();
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
