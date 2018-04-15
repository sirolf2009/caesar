package com.sirolf2009.caesar.util;

import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DragDrop {

    public static void initializeDragDrop(Node node, DataFormat[] acceptedFormats, Consumer<DragEvent> onDropped) {
        initializeDragDrop(node, event -> Arrays.stream(acceptedFormats).filter(format -> event.getDragboard().hasContent(format)).findAny().isPresent(), onDropped);
    }

    public static void initializeDragDrop(Node node, Predicate<DragEvent> predicate, Consumer<DragEvent> onDropped) {
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node && predicate.test(event)) {
                event.acceptTransferModes(TransferMode.LINK);
                node.setStyle("-fx-effect: innershadow(gaussian, rgb(114, 137, 218), 10, 1.0, 0, 0);");
            }
            event.consume();
        });
        node.setOnDragExited(event -> {
            node.setStyle("");
            event.consume();
        });
        node.setOnDragDropped(event -> onDropped.accept(event));
    }

}
