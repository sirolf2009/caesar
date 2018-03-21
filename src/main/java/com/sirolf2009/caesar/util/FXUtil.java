package com.sirolf2009.caesar.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class FXUtil {

	public static void maximize(Node child) {
		AnchorPane.setTopAnchor(child, 0d);
		AnchorPane.setBottomAnchor(child, 0d);
		AnchorPane.setLeftAnchor(child, 0d);
		AnchorPane.setRightAnchor(child, 0d);
	}

}
