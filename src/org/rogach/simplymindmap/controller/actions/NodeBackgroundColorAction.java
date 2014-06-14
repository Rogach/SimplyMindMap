package org.rogach.simplymindmap.controller.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.NodeBackgroundColorFormatAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.util.XmlTools;

public class NodeBackgroundColorAction extends FreemindAction implements ActorXml {
  private final MindMapController controller;
  
  public NodeBackgroundColorAction(MindMapController controller) {
    super("node_background_color", (String) null, controller);
    this.controller = controller;
    controller.getActionFactory().registerActor(this, getDoActionClass());
  }
  
  public void actionPerformed(ActionEvent e) {
		Color color = Tools.showCommonJColorChooserDialog(
        controller.getView().getSelected(),
				controller.getResources().getText("choose_node_background_color"), 
        controller.getSelected().getBackgroundColor());
		if (color == null) {
			return;
		}
    for (MindMapNode selected : controller.getSelecteds()) {
			setNodeBackgroundColor(selected, color);
		}
	}
  
  public void setNodeBackgroundColor(MindMapNode node, Color color) {
		if (Tools.safeEquals(color, node.getColor())) {
			return;
		}
		NodeBackgroundColorFormatAction doAction = createNodeBackgroundColorFormatAction(node,
				color);
		NodeBackgroundColorFormatAction undoAction = createNodeBackgroundColorFormatAction(node,
				node.getColor());
		controller.doTransaction(this.getClass().getName(),
				new ActionPair(doAction, undoAction));
	}
  
  public NodeBackgroundColorFormatAction createNodeBackgroundColorFormatAction(MindMapNode node,
			Color color) {
		NodeBackgroundColorFormatAction nodeAction = new NodeBackgroundColorFormatAction();
		nodeAction.setNode(node.getObjectId(controller));
		nodeAction.setColor(XmlTools.colorToXml(color));
		return nodeAction;
	}
  
  public void act(XmlAction action) {
		if (action instanceof NodeBackgroundColorFormatAction) {
			NodeBackgroundColorFormatAction nodeColorAction = (NodeBackgroundColorFormatAction) action;
			Color color = XmlTools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = controller.getNodeFromID(nodeColorAction
					.getNode());
			Color oldColor = node.getColor();
			if (!Tools.safeEquals(color, oldColor)) {
				node.setBackgroundColor(color); // null
				controller.nodeChanged(node);
			}
		}
	}
  
  public Class getDoActionClass() {
		return NodeBackgroundColorFormatAction.class;
	}
}
