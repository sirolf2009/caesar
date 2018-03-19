package com.sirolf2009.caesar;

import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXAttributes;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import javax.management.MBeanServerConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class JMXPuller implements Runnable {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ObservableList<IDataPointer> attributes;
    private final ObservableList<JMXAttributes> items;
    private final SimpleBooleanProperty running = new SimpleBooleanProperty(false);
    private final SimpleLongProperty timeout;
    private MBeanServerConnection connection;

    public JMXPuller(ObservableList<IDataPointer> attributes, ObservableList<JMXAttributes> items, long timeout) {
        this.connection = connection;
        this.attributes = attributes;
        this.items = items;
        this.timeout = new SimpleLongProperty(timeout);
    }

    @Override
    public void run() {
        while (true) {
            while (running.get()) {
                try {
                    Thread.sleep(timeout.get());
                    JMXAttributes attributes = pullAttributes();
                    Platform.runLater(() -> items.add(attributes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JMXAttributes pullAttributes() {
        JMXAttributes items = new JMXAttributes();
        attributes.forEach(pointer -> pointer.pullData(connection, items));
        return items;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public void setConnection(MBeanServerConnection connection) {
        this.connection = connection;
    }

    public SimpleLongProperty timeoutProperty() {
        return timeout;
    }

    public SimpleBooleanProperty runningProperty() {
        return running;
    }

    public ObservableList<IDataPointer> getAttributes() {
        return attributes;
    }
}
