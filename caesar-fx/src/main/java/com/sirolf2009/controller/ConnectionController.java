package com.sirolf2009.controller;

import com.sirolf2009.caesar.server.JMXServer;
import com.sirolf2009.caesar.server.model.Attribute;
import com.sirolf2009.caesar.server.model.MBean;
import com.sirolf2009.component.TableTab;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class ConnectionController extends VBox {

    final MBeanServerConnection connection;
    final JMXServer server;
    @FXML
    TreeView<Object> mBeans;
    @FXML
    TabPane visualizations;

    public ConnectionController(MBeanServerConnection connection) {
        this.connection = connection;
        this.server = new JMXServer("", connection);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/connection.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {
        mBeans.setRoot(new TreeItem<>());
        mBeans.setShowRoot(false);
        new Thread(() -> {
            while (true) {
                Collection<MBean> beans = server.getBeans();
                Platform.runLater(() -> {
                    updateMBeans(beans);
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        mBeans.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> treeView) {
                TreeCell<Object> treeCell = new TreeCell<Object>() {
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            if (item instanceof MBean) {
                                setText(((MBean) item).getName().toString());
                            } else if (item instanceof Attribute) {
                                setText(((Attribute) item).getAttributeInfo().getName());
                            } else {
                                setText(item.toString());
                            }
                        }
                    }
                };

                treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        TreeItem item = mBeans.getSelectionModel().getSelectedItem();
                        if (item.getValue() instanceof Attribute) {
                            Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);

                            ClipboardContent content = new ClipboardContent();
                            content.putString(treeCell.getText());
                            db.setContent(content);

                            mouseEvent.consume();
                        }
                    }
                });

                return treeCell;
            }
        });

        visualizations.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != visualizations && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.LINK);
                }
                event.consume();
            }
        });
        visualizations.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Attribute attribute = (Attribute) mBeans.getSelectionModel().getSelectedItem().getValue();
                    visualizations.getTabs().add(new TableTab(mBeans, server, attribute));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    public void updateMBeans(Collection<MBean> beans) {
        beans.stream().forEach(bean -> {
            TreeItem<Object> treeBean = findTreeChild(mBeans.getRoot(), bean).orElseGet(() -> {
                TreeItem<Object> newTreeBean = new TreeItem<>();
                newTreeBean.setValue(bean);
                mBeans.getRoot().getChildren().add(newTreeBean);
                return newTreeBean;
            });
            bean.getAttributes().stream().forEach(attribute -> {
                findTreeChild(treeBean, attribute).orElseGet(() -> {
                    TreeItem<Object> newTreeAttribute = new TreeItem<>();
                    newTreeAttribute.setValue(attribute);
                    treeBean.getChildren().add(newTreeAttribute);
                    return newTreeAttribute;
                });
            });
        });
    }

    public Optional<TreeItem<Object>> findTreeChild(TreeItem<Object> treeItem, Object value) {
        return treeItem.getChildren().stream().filter(item -> item.getValue().equals(value)).findAny();
    }



}
