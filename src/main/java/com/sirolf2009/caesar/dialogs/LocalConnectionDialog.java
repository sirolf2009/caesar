package com.sirolf2009.caesar.dialogs;

import javafx.scene.control.ChoiceDialog;
import org.gridkit.lab.jvm.attach.AttachManager;
import org.gridkit.lab.jvm.attach.JavaProcessId;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class LocalConnectionDialog extends ChoiceDialog<Supplier<MBeanServerConnection>> {

    public LocalConnectionDialog(Supplier<MBeanServerConnection> defaultChoice, Collection<Supplier<MBeanServerConnection>> choices) {
        super(defaultChoice, choices);
        setTitle("Open a connection");
        setHeaderText("Please select a JVM to connect to");
    }

    public static LocalConnectionDialog getLocalConnectionsDialog() {
        List<Supplier<MBeanServerConnection>> choices = new ArrayList<>();
        choices.add(new Supplier<MBeanServerConnection>() {
            @Override
            public MBeanServerConnection get() {
                return new RemoteConnectionDialog().showAndWait().get().get();
            }

            @Override
            public String toString() {
                return "Remote connection";
            }
        });
        List<JavaProcessId> localJVMs = AttachManager.listJavaProcesses();
        localJVMs.stream().filter(pid -> !pid.getDescription().equals("com.sirolf2009.caesar.MainApp")).map(pid -> {
            return new Supplier<MBeanServerConnection>() {
                @Override
                public MBeanServerConnection get() {
                    try {
                        System.out.println(JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:attach:///["+pid.getDescription()+"]")).getMBeanServerConnection().getDefaultDomain());
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    return AttachManager.getJmxConnection(pid);
                }
                @Override
                public String toString() {
                    String fullString = pid.toString();
                    String cutString = fullString.substring(0, Math.min(fullString.length(), 64));
                    return fullString.equals(cutString) ? fullString : cutString+"...";
                }
            };
        }).forEach(supplier -> choices.add(supplier));
        return new LocalConnectionDialog(choices.get(0), choices);
    }
}
