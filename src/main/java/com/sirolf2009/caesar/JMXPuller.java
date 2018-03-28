package com.sirolf2009.caesar;

import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXAttributes;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import javax.management.MBeanServerConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class JMXPuller implements Runnable {

	private final ExecutorService sleepUpdater = Executors.newSingleThreadExecutor();
	private final ObservableList<IDataPointer> attributes;
	private final ObservableList<JMXAttributes> items;
	private final SimpleBooleanProperty running = new SimpleBooleanProperty(false);
	private final SimpleLongProperty timeout;
	private final SimpleDoubleProperty sleepProgress;
	private final Connection connection;

	public JMXPuller(Connection connection, ObservableList<IDataPointer> attributes, ObservableList<JMXAttributes> items, long timeout) {
		this.connection = connection;
		this.attributes = attributes;
		this.items = items;
		this.timeout = new SimpleLongProperty(timeout);
		this.sleepProgress = new SimpleDoubleProperty();
	}

	@Override public void run() {
		while(true) {
			try {
				if(running.get()) {
					sleepUpdater.execute(() -> {
						double steps = timeout.get() / 100;
						for(int i = 0; i < steps; i++) {
							try {
								Thread.sleep(100);
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
							sleepProgress.set(Double.valueOf(i) / steps);
						}
					});
				}
				Thread.sleep(timeout.get());
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			if(running.get()) {
				update();
			}
		}
	}

	public void update() {
		try {
			connection.getConnection().ifPresent(connection -> {
				try {
					JMXAttributes attributes = pullAttributes(connection);
					Platform.runLater(() -> items.add(attributes));
				} catch(Exception e) {
					e.printStackTrace();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public JMXAttributes pullAttributes(MBeanServerConnection connection) throws Exception {
		JMXAttributes items = new JMXAttributes();
		attributes.forEach(pointer -> {
			try {
				pointer.pullData(connection, items);
			} catch(Exception e) {
				throw new RuntimeException("Failed to pull " + pointer, e);
			}
		});
		return items;
	}

	public Connection getConnection() {
		return connection;
	}

	public SimpleLongProperty timeoutProperty() {
		return timeout;
	}

	public SimpleBooleanProperty runningProperty() {
		return running;
	}

	public SimpleDoubleProperty sleepProgressProperty() {
		return sleepProgress;
	}

	public ObservableList<IDataPointer> getAttributes() {
		return attributes;
	}
}
