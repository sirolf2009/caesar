package com.sirolf2009.caesar.component;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class EditableTableView<S> extends TableView<S> {

	public EditableTableView() {
		addHeaderHandler();
	}

	public EditableTableView(ObservableList<S> items) {
		super(items);
		addHeaderHandler();
	}

	public void addHeaderHandler() {
		addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if(e.isPrimaryButtonDown() && e.getClickCount() > 1) {
				EventTarget target = e.getTarget();
				TableColumnBase<?, ?> column = null;
				while(target instanceof Node) {
					target = ((Node) target).getParent();
					// beware: package of TableColumnHeader is version specific
					if(target instanceof TableColumnHeader) {
						column = ((TableColumnHeader) target).getTableColumn();
						if(column != null)
							break;
					}
				}
				if(column != null) {
					TableColumnBase<?, ?> tableColumn = column;
					TextField textField = new TextField(column.getText());
					textField.setMaxWidth(column.getWidth());
					textField.setOnAction(a -> {
						tableColumn.setText(textField.getText());
						tableColumn.setGraphic(null);
					});
					textField.focusedProperty().addListener((src, ov, nv) -> {
						if(!nv) {
							tableColumn.setText(textField.getText());
							tableColumn.setGraphic(null);
						}
					});
					column.setGraphic(textField);
					textField.requestFocus();
				}
				e.consume();
			}
		});
	}

}
