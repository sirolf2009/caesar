package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.TreeViewFilteredHierarchy;
import com.sirolf2009.caesar.component.hierarchy.TreeViewHierarchy;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXObject;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.function.Predicate;

public class VariablesTreeView extends VBox {

    private final TextField filter;
    private final TreeViewFilteredHierarchy variables;

    public VariablesTreeView() {
        variables = new TreeViewFilteredHierarchy();
        filter = new TextField();
        filter.textProperty().addListener(e -> {
            variables.itemPredicateProperty().set(p -> p.toString().contains(filter.getText()));
        });
        getChildren().addAll(filter, variables);
        VBox.setVgrow(variables, Priority.ALWAYS);
        variables.setRoot(new TreeItem<>());
        variables.setShowRoot(false);
        variables.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
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

    public TextField getFilter() {
        return filter;
    }

    public TreeViewFilteredHierarchy getVariables() {
        return variables;
    }
}
