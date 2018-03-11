package com.sirolf2009;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXAttributes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import javax.management.MBeanServerConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static javafx.collections.FXCollections.*;

public class JMXPuller implements Runnable {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ObservableList<JMXAttributes> values = synchronizedObservableList(observableArrayList());
    private final MBeanServerConnection connection;
    private final ObservableList<JMXAttribute> attributes;
    private final long timeout;

    public JMXPuller(MBeanServerConnection connection, ObservableList<JMXAttribute> attributes, long timeout) {
        this.connection = connection;
        this.attributes = attributes;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(timeout);
                values.add(pullAttributes());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JMXAttributes pullAttributes() {
        List<Pair<JMXAttribute, Future<Object>>> calls = new ArrayList<>(attributes.size());
        attributes.forEach(attribute -> calls.add(new Pair<>(attribute, executor.submit(getValue(connection, attribute)))));
        JMXAttributes attributes = new JMXAttributes();
        calls.forEach(attribute -> {
            try {
                attributes.put(attribute.getKey(), attribute.getValue().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return attributes;
    }

    public Callable<Object> getValue(MBeanServerConnection connection, JMXAttribute attribute) {
        return () -> connection.getAttribute(attribute.getObjectName(), attribute.getAttributeInfo().getName());
    }

    public ObservableList<JMXAttribute> getAttributes() {
        return attributes;
    }

    public ObservableList<JMXAttributes> getValues() {
        return values;
    }
}
