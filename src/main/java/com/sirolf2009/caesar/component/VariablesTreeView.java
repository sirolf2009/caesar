package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.TreeViewHierarchy;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXObject;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

public class VariablesTreeView extends TreeViewHierarchy {

    public VariablesTreeView() {
        setRoot(new TreeItem<>());
        setShowRoot(false);
        setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                TreeCell<Object> treeCell = new TreeCell<Object>() {
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(String.valueOf(item));
                        } else {
                            setText("");
                        }
                    }
                };

                treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Object value = treeCell.getTreeItem().getValue();
                        if(value instanceof JMXAttribute) {
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            JMXAttribute attr = (JMXAttribute)value;
                            content.putString(attr.getAttributeInfo().getName()+"@"+attr.getObjectName().toString());
                            db.setContent(content);

                            mouseEvent.consume();
                        }
                    }
                });

                return treeCell;
            }
        });
    }

    @Override public void setItems(ObservableList items) {
        super.setItems(items);
        if(getRoot() != null) {
            getRoot().getChildren().forEach(treeItem -> ((TreeItem) treeItem).setExpanded(false));
        }
    }
}
