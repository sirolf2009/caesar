package com.sirolf2009.caesar.dialogs;

import com.sirolf2009.caesar.ConnectionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;

import javax.management.MBeanServerConnection;
import java.util.function.Supplier;

public class RemoteConnectionDialog extends Dialog<Supplier<MBeanServerConnection>> {

    public RemoteConnectionDialog() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connection.fxml"));
            Parent root = loader.load();
            ConnectionController controller = loader.getController();
            getDialogPane().setContent(root);
            controller.getConnectionSupplier().addListener(observable -> setResult(controller.getConnectionSupplier().getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
