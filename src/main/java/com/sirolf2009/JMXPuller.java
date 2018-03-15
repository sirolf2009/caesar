package com.sirolf2009;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXAttributes;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import javax.management.MBeanServerConnection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static javafx.collections.FXCollections.*;

public class JMXPuller implements Runnable {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ObservableList<JMXAttribute> attributes;
    private final ObservableList<JMXAttributes> items;
    private final SimpleBooleanProperty running = new SimpleBooleanProperty(false);
    private final SimpleLongProperty timeout;
    private MBeanServerConnection connection;

    public JMXPuller(ObservableList<JMXAttribute> attributes, ObservableList<JMXAttributes> items, long timeout) {
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
        //FIXME: handle disconnect
        /*
java.util.concurrent.ExecutionException: java.rmi.ConnectException: Connection refused to host: 127.0.1.1; nested exception is:
	java.net.ConnectException: Connection refused (Connection refused)
	at java.util.concurrent.FutureTask.report(FutureTask.java:122)
	at java.util.concurrent.FutureTask.get(FutureTask.java:192)
	at com.sirolf2009.JMXPuller.lambda$pullAttributes$2(JMXPuller.java:55)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at com.sirolf2009.JMXPuller.pullAttributes(JMXPuller.java:53)
	at com.sirolf2009.JMXPuller.run(JMXPuller.java:40)
	at java.lang.Thread.run(Thread.java:748)
Caused by: java.rmi.ConnectException: Connection refused to host: 127.0.1.1; nested exception is:
	java.net.ConnectException: Connection refused (Connection refused)
	at sun.rmi.transport.tcp.TCPEndpoint.newSocket(TCPEndpoint.java:619)
	at sun.rmi.transport.tcp.TCPChannel.createConnection(TCPChannel.java:216)
	at sun.rmi.transport.tcp.TCPChannel.newConnection(TCPChannel.java:202)
	at sun.rmi.server.UnicastRef.invoke(UnicastRef.java:129)
	at com.sun.jmx.remote.internal.PRef.invoke(Unknown Source)
	at javax.management.remote.rmi.RMIConnectionImpl_Stub.getAttribute(Unknown Source)
	at javax.management.remote.rmi.RMIConnector$RemoteMBeanServerConnection.getAttribute(RMIConnector.java:903)
	at com.sirolf2009.JMXPuller.lambda$getValue$3(JMXPuller.java:66)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	... 1 more
Caused by: java.net.ConnectException: Connection refused (Connection refused)
	at java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
	at java.net.Socket.connect(Socket.java:589)
	at java.net.Socket.connect(Socket.java:538)
	at java.net.Socket.<init>(Socket.java:434)
	at java.net.Socket.<init>(Socket.java:211)
	at sun.rmi.transport.proxy.RMIDirectSocketFactory.createSocket(RMIDirectSocketFactory.java:40)
	at sun.rmi.transport.proxy.RMIMasterSocketFactory.createSocket(RMIMasterSocketFactory.java:148)
	at sun.rmi.transport.tcp.TCPEndpoint.newSocket(TCPEndpoint.java:613)
	... 11 more
         */
        return () -> connection.getAttribute(attribute.getObjectName(), attribute.getAttributeInfo().getName());
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

    public ObservableList<JMXAttribute> getAttributes() {
        return attributes;
    }
}
