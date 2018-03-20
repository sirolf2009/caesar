package com.sirolf2009.caesar;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.rmi.ConnectException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Connection {

	private final AtomicReference<MBeanServerConnection> connection = new AtomicReference<>(null);
	private final JMXServiceURL serviceURL;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public Connection(JMXServiceURL serviceURL) throws IOException {
		this.serviceURL = serviceURL;
		connect();
		System.out.println("Connecting to: "+serviceURL);
		Thread connectorThread = new Thread(() -> {
			while(true) {
				try {
					connection.get().getDefaultDomain();
					try {
						Thread.sleep(1000);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				} catch(NullPointerException | ConnectException e) {
					try {
						if(connection.get() != null) {
							connection.set(null);
							System.out.println("Disconnected. Reconnecting to: "+serviceURL);
						}
						connect();
					} catch(Exception e1) {
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, "JMX-connector");
		connectorThread.setDaemon(true);
		connectorThread.start();
	}

	private void connect() throws IOException {
		try {
			MBeanServerConnection newConnection = executor.submit(() -> JMXConnectorFactory.connect(serviceURL).getMBeanServerConnection()).get(5, TimeUnit.SECONDS);
			connection.set(newConnection);
			System.out.println("Connected to: "+serviceURL);
		} catch (Exception e) {
			throw new IOException("Failed to connect to "+serviceURL, e);
		}
	}

	public Optional<MBeanServerConnection> getConnection() {
		return Optional.ofNullable(connection.get());
	}

}
