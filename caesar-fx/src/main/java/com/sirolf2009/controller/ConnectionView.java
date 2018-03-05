package com.sirolf2009.controller;

import com.sirolf2009.caesar.server.model.MBean;
import com.sirolf2009.model.Connection;
import com.sirolf2009.util.ControllerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Function;

public class ConnectionView extends HBox {

    private final Connection connection;

    @FXML
    Label lblName;
    @FXML
    Label lblUptime;
    @FXML
    Label lblVersion;
    @FXML
    TreeView<Object> beanTree;

    public ConnectionView(Connection connection) {
        this.connection = connection;
        ControllerUtil.load(this, "/fxml/detail_connection.fxml");
    }

    @FXML
    public void initialize() {
        lblName.setText(connection.getName());

        Collection<MBean> beans = connection.getServer().getBeans();
        TreeItem<Object> root = new TreeItem<>();
        beans.stream().map(bean -> bean.getName().getDomain()).distinct().forEach(domain -> {
            root.getChildren().add(new TreeItem<>(domain));
        });
        beans.forEach(bean -> root.getChildren().stream().filter(item -> item.getValue().toString().equals(bean.getName().getDomain())).findAny().ifPresent(parent -> {
            parent.getChildren().add(new TreeItem<>(bean.getName()));
        }));
        beanTree.setShowRoot(false);
        beanTree.setRoot(root);

        setProperties(beans);
    }

    public void setProperties(Collection<MBean> beans) {
        setProperty(beans, "java.lang:type=Runtime", "Uptime", value -> Duration.ofMillis((Long)value).toString(), lblUptime);
        setProperty(beans, "java.lang:type=Runtime", "SpecVersion", lblVersion);
    }

    public void setProperty(Collection<MBean> beans, String beanName, String property, Label label) {
        setProperty(beans, beanName, property, value -> String.valueOf(value), label);
    }

    public void setProperty(Collection<MBean> beans, String beanName, String property, Function<Object, String> toString, Label label) {
        beans.stream().filter(bean -> bean.getName().toString().equals(beanName)).findAny().ifPresent(bean -> {
            try {
                label.setText(toString.apply(connection.getServer().getConnection().getAttribute(bean.getName(), property)));
            } catch (Exception e) {
                label.setText(e.getClass()+": "+e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }

}
