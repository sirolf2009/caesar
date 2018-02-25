package com.sirolf2009.component;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class HeaderlessTableView<S> extends TableView<S> {

    private static ChangeListener<Skin<?>> removeHeader = (a, b, newSkin) -> {
        TableHeaderRow headerRow = ((TableViewSkinBase) newSkin).getTableHeaderRow();
        headerRow.setMinHeight(0);
        headerRow.setPrefHeight(0);
        headerRow.setMaxHeight(0);
        headerRow.setVisible(false);
    };

    public HeaderlessTableView() {
        super();
        skinProperty().addListener(removeHeader);
    }

    public HeaderlessTableView(ObservableList<S> items) {
        super(items);
        skinProperty().addListener(removeHeader);
    }
}
