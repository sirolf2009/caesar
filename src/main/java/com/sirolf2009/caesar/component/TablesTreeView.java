package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.TreeViewHierarchy;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;

public class TablesTreeView extends TreeViewHierarchy {

    public static DataFormat TABLE = new DataFormat("Table");
    public static DataFormat TABLE_AND_POINTER = new DataFormat("TableAndPointer");
    public static Table table;
    public static IDataPointer pointer;

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
                            if(item instanceof Table) {
                                textProperty().bind(((Table)item).nameProperty());
                            } else if(item instanceof IDataPointer) {
                                textProperty().bind(((IDataPointer)item).nameProperty());
                            } else {
                                setText(String.valueOf(item));
                            }
                        } else {
                            textProperty().unbind();
                            setText("");
                        }
                    }
                };

                treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Object value = treeCell.getTreeItem().getValue();
                        if(value instanceof Table) {
                            Table table = (Table) value;
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            content.put(TABLE, "");
                            db.setContent(content);

                            TablesTreeView.table = table;
                            mouseEvent.consume();
                        } else if(value instanceof IDataPointer) {
                            Table table = (Table) treeCell.getTreeItem().getParent().getValue();
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            content.put(TABLE_AND_POINTER, "");
                            db.setContent(content);

                            TablesTreeView.table = table;
                            TablesTreeView.pointer = (IDataPointer) value;

                            mouseEvent.consume();
                        }
                    }
                });

                return treeCell;
            }
        });
    }

}
