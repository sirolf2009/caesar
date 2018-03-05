package com.sirolf2009.component;

import com.sirolf2009.caesar.server.model.Attribute;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
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
import org.w3c.dom.Attr;

import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class VariablesTreeView extends TreeView<Object> {

    public VariablesTreeView(ObservableList<Attribute> attributes) {
        setRoot(new TreeItem<>());
        setShowRoot(false);

        Map<String, TreeItem> domains = new HashMap<>();
        Map<ObjectName, TreeItem> beans = new HashMap<>();
        Map<Attribute, TreeItem> attributeItems = new HashMap<>();
        Function<String, TreeItem> getOrCreateDomain = domain -> {
            if(!domains.containsKey(domain)) {
                TreeItem domainItem = new TreeItem(domain);
                getRoot().getChildren().add(domainItem);
                domains.put(domain, domainItem);
                return domainItem;
            }
            return domains.get(domain);
        };
        Function<ObjectName, TreeItem> getOrCreateBean = bean -> {
            if(!beans.containsKey(bean)) {
                TreeItem beanItem = new TreeItem(bean);
                getOrCreateDomain.apply(bean.getDomain()).getChildren().add(beanItem);
                beans.put(bean, beanItem);
                return beanItem;
            }
            return beans.get(bean);
        };
        attributes.forEach(attr -> {
            TreeItem parent = getOrCreateBean.apply(attr.getName());
            TreeItem attribute = new TreeItem(attr);
            attributeItems.put(attr, attribute);
            parent.getChildren().add(attribute);
        });

        attributes.addListener((ListChangeListener<Attribute>) c -> {
            attributes.stream().forEach(attr -> {
                if(!attributeItems.containsKey(attr)) {
                    TreeItem parent = getOrCreateBean.apply(attr.getName());
                    TreeItem attribute = new TreeItem(attr);
                    attributeItems.put(attr, attribute);
                    parent.getChildren().add(attribute);
                }
            });
        });

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
                        if(value instanceof Attribute) {
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.LINK);

                            ClipboardContent content = new ClipboardContent();
                            Attribute attr = (Attribute)value;
                            content.putString(attr.getAttributeInfo().getName()+"@"+attr.getName());
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
