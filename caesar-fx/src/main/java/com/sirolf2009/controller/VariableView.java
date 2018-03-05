package com.sirolf2009.controller;

import com.sirolf2009.caesar.server.model.Attribute;
import com.sirolf2009.caesar.server.model.MBean;
import com.sirolf2009.model.Connection;
import com.sirolf2009.util.ControllerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Function;

public class VariableView extends HBox {

    private final Attribute attribute;

    @FXML
    Label lblName;
    @FXML
    Label lblDescription;
    @FXML
    Label lblType;

    public VariableView(Attribute attribute) {
        this.attribute = attribute;
        ControllerUtil.load(this, "/fxml/detail_variable.fxml");
    }

    @FXML
    public void initialize() {
        lblName.setText(attribute.getAttributeInfo().getName());
        lblDescription.setText(attribute.getAttributeInfo().getDescription());
        lblType.setText(attribute.getAttributeInfo().getType());
    }

}
