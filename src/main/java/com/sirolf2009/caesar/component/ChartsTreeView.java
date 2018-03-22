package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.TreeViewHierarchy;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;

public class ChartsTreeView extends TreeViewHierarchy {

    public static DataFormat CHART = new DataFormat("Chart");
    public static Chart chart;

    public ChartsTreeView(ObservableList<Chart> attributes) {
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
                            if(item instanceof Chart) {
                                textProperty().bind(((Chart)item).nameProperty());
                            } else if(item instanceof ColumnOrRow) {
                                textProperty().bind(((ColumnOrRow)item).getSeries().nameProperty());
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
                        if(value instanceof Chart) {
                            Chart chart = (Chart) value;
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            content.put(CHART, "");
                            db.setContent(content);

                            ChartsTreeView.chart = chart;
                            mouseEvent.consume();
                        }
                    }
                });

                return treeCell;
            }
        });
    }

}
