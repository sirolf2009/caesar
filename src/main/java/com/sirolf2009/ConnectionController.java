package com.sirolf2009;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ConnectionController {

    private SimpleObjectProperty<Supplier<MBeanServerConnection>> connectionSupplier = new SimpleObjectProperty<>();

    @FXML
    TextField txtHost;
    @FXML
    TextField txtPort;

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        txtPort.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    @FXML
    public void testConnection(ActionEvent event) {
        ProgressIndicator indicator = new ProgressIndicator(-1);
        ((Button)event.getSource()).setGraphic(indicator);
        try {
            connect(getUrl());
            indicator.setProgress(100);
        } catch (Exception e) {
            ((Button)event.getSource()).setGraphic(null);
        }
    }

    @FXML
    public void finishConnection() {
        try {
            connectionSupplier.set(getConnectionSupplier(getUrl()));
            txtHost.getScene().getWindow().hide();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public SimpleObjectProperty<Supplier<MBeanServerConnection>> getConnectionSupplier() {
        return connectionSupplier;
    }

    private Supplier<MBeanServerConnection> getConnectionSupplier(JMXServiceURL url) throws MalformedURLException {
        return () -> {
            try {
                return connect(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private MBeanServerConnection connect(JMXServiceURL url) throws IOException {
        JMXConnector connector = JMXConnectorFactory.connect(url);
        return connector.getMBeanServerConnection();
    }

    private JMXServiceURL getUrl() throws MalformedURLException {
        return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+txtHost.getText()+":"+Integer.parseInt(txtPort.getText())+"/jmxrmi");
    }
}