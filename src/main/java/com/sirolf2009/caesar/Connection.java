package com.sirolf2009.caesar;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.rmi.ConnectException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Connection {

	private final AtomicReference<MBeanServerConnection> connection = new AtomicReference<>(null);
	private final JMXServiceURL serviceURL;

	public Connection(JMXServiceURL serviceURL) throws IOException {
		this.serviceURL = serviceURL;
		connect();
		Thread connectorThread = new Thread(() -> {
			while(true) {
				try {
					connection.get().getDefaultDomain();
					try {
						Thread.sleep(1000);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				} catch(ConnectException e) {
					try {
						connection.set(null);
						connect();
					} catch(IOException e1) {
						e1.printStackTrace();
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, "JMX-connector");
		connectorThread.setDaemon(true);
		connectorThread.start();
	}

	private void connect() throws IOException {
		System.out.println("Connecting to: "+serviceURL);
		JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
		connection.set(connector.getMBeanServerConnection());
	}

	public Optional<MBeanServerConnection> getConnection() {
		return Optional.ofNullable(connection.get());
	}

}
