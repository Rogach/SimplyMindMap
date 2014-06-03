package org.rogach.simplymindmap.model;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public interface IconInformation {
	String getDescription();

	ImageIcon getIcon();

	String getKeystrokeResourceName();

	KeyStroke getKeyStroke();
}
