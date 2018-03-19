package com.sirolf2009.caesar.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class RenameableTab extends Tab {

	public RenameableTab(StringProperty name) {
		Label label = new Label();
		label.textProperty().bind(name);
		setGraphic(label);

		TextField textField = new TextField();
		label.setOnMouseClicked(event -> {
			if(event.getClickCount() == 2) {
				textField.setText(label.getText());
				setGraphic(textField);
				textField.selectAll();
				textField.requestFocus();
			}
		});

		textField.setOnAction(event -> {
			name.setValue(textField.getText());
			setGraphic(label);
		});

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				name.setValue(textField.getText());
				setGraphic(label);
			}
		});
	}
}
