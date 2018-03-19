package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.TreeViewHierarchy;
import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
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

public class TablesTreeView extends TreeViewHierarchy {

    public TablesTreeView(ObservableList<Table> attributes) {
        setRoot(new TreeItem<>());
        setItems(attributes);
        getRoot().getChildren().forEach(treeItem -> ((TreeItem)treeItem).setExpanded(false));
        setShowRoot(false);
        setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                TreeCell<Object> treeCell = new TreeCell<Object>() {
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(String.valueOf(item));
                        }
                    }
                };

                treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Object value = treeCell.getTreeItem().getValue();
                        if(value instanceof JMXAttribute) {
                            Table table = (Table) treeCell.getTreeItem().getParent().getValue();
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            JMXAttribute attr = (JMXAttribute)value;
                            content.putString(attr.getAttributeInfo().getName()+"@"+attr.getObjectName().toString()+"@"+table.getName());
                            db.setContent(content);

                            mouseEvent.consume();
                        }
                    }
                });

                return treeCell;
            }
        });
    }

}
