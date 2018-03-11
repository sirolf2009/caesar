package com.sirolf2009.model;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import javafx.collections.ObservableList;

import javax.management.ObjectName;
import java.util.Objects;

public class JMXObject implements IHierarchicalData<JMXAttribute> {

    private final ObjectName objectName;
    private final ObservableList<JMXAttribute> attributes;

    public JMXObject(ObjectName objectName, ObservableList<JMXAttribute> attributes) {
        this.objectName = objectName;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return objectName.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JMXObject jmxObject = (JMXObject) o;
        return Objects.equals(objectName, jmxObject.objectName) &&
                Objects.equals(attributes, jmxObject.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, attributes);
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public ObservableList<JMXAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public ObservableList<JMXAttribute> getChildren() {
        return attributes;
    }
}
