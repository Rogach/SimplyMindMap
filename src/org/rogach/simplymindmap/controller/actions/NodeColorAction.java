/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 19.09.2004
 */


package org.rogach.simplymindmap.controller.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ListIterator;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.NodeColorFormatAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.util.XmlTools;

public class NodeColorAction extends FreemindAction implements ActorXml {
	private final MindMapController controller;

	public NodeColorAction(MindMapController controller) {
		super("node_color", (String) null, controller);
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_node_color")));
		this.controller = controller;
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		Color color = Tools.showCommonJColorChooserDialog(controller
				.getView().getSelected(),
				controller.getResources().getText("choose_node_color"), controller.getSelected()
				.getColor());
		if (color == null) {
			return;
		}
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNode selected = (MindMapNode) it.next();
			setNodeColor(selected, color);
		}
	}

	public void setNodeColor(MindMapNode node, Color color) {
		if (Tools.safeEquals(color, node.getColor())) {
			return;
		}
		NodeColorFormatAction doAction = createNodeColorFormatAction(node,
				color);
		NodeColorFormatAction undoAction = createNodeColorFormatAction(node,
				node.getColor());
		controller.doTransaction(this.getClass().getName(),
				new ActionPair(doAction, undoAction));
	}

	public NodeColorFormatAction createNodeColorFormatAction(MindMapNode node,
			Color color) {
		NodeColorFormatAction nodeAction = new NodeColorFormatAction();
		nodeAction.setNode(node.getObjectId(controller));
		nodeAction.setColor(XmlTools.colorToXml(color));
		return nodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof NodeColorFormatAction) {
			NodeColorFormatAction nodeColorAction = (NodeColorFormatAction) action;
			Color color = XmlTools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = controller.getNodeFromID(nodeColorAction
					.getNode());
			Color oldColor = node.getColor();
			if (!Tools.safeEquals(color, oldColor)) {
				node.setColor(color); // null
				controller.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return NodeColorFormatAction.class;
	}

}