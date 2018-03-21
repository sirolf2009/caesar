package com.sirolf2009.caesar.component;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

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
				getColumn(e).ifPresent(column -> {
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
				});
				e.consume();
			} else if(e.isSecondaryButtonDown() && e.getClickCount() == 1) {
				getColumn(e).ifPresent(column -> {
					ContextMenu contextMenu = new ContextMenu();
					contextMenu.getItems().addAll(column.getContextItems());
					contextMenu.show(this, e.getScreenX(), e.getScreenY());
				});
			}
		});
	}

	private Optional<TableTab.CaesarTableColumn> getColumn(MouseEvent e) {
		EventTarget target = e.getTarget();
		while(target instanceof Node) {
			target = ((Node) target).getParent();
			// beware: package of TableColumnHeader is version specific
			if(target instanceof TableColumnHeader) {
				TableTab.CaesarTableColumn column = (TableTab.CaesarTableColumn) ((TableColumnHeader) target).getTableColumn();
				if(column != null) {
					return Optional.of(column);
				}
			}
		}
		return Optional.empty();
	}



}
