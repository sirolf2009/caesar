package com.sirolf2009.model;

import com.sirolf2009.caesar.server.JMXServer;
import com.sirolf2009.caesar.server.model.MBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Connection {

    private final String name;
    private final JMXServer server;
    private final ObservableList<MBean> mBeans;

    public Connection(String name, JMXServer server) {
        this.name = name;
        this.server = server;
        mBeans = FXCollections.observableArrayList(server.getBeans());
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public JMXServer getServer() {
        return server;
    }

}
