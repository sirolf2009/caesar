package com.sirolf2009.component;

import com.sirolf2009.caesar.server.model.Attribute;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
    }

}
