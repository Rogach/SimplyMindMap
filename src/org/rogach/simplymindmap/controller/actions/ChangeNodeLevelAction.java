package org.rogach.simplymindmap.controller.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;

public class ChangeNodeLevelAction extends AbstractAction {
  private String direction;
  private MindMapController controller;

  public ChangeNodeLevelAction(String direction, MindMapController controller) {
    super();
    this.direction = direction;
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    MindMapNode selectedNode = controller.getSelected();
    List<MindMapNode> selectedNodes = controller.getSelecteds();
    // bug fix: sort to make independent by user's selection:
    controller.sortNodesByDepth(selectedNodes);
    if (selectedNode.isRoot()) {
      JOptionPane.showMessageDialog(controller.getView(), 
              Resources.getInstance().getResourceString("cannot_add_parent_to_root"), "", JOptionPane.ERROR_MESSAGE);
      return;
    }
    boolean upwards = Tools.safeEquals("left", direction) != selectedNode.isLeft();
    // Make sure the selected nodes all have the same parent
    // (this restriction is to simplify the action, and could
    // possibly be removed in the future, when we have undo)
    // Also make sure that none of the selected nodes are the root node
    MindMapNode selectedParent = selectedNode.getParentNode();
    for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
      MindMapNode node = (MindMapNode) it.next();
      if (node.getParentNode() != selectedParent) {
        JOptionPane.showMessageDialog(controller.getView(), 
              Resources.getInstance().getResourceString("cannot_add_parent_diff_parents"), "", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (node.isRoot()) {
        JOptionPane.showMessageDialog(controller.getView(), 
              Resources.getInstance().getResourceString("cannot_add_parent_to_root"), "", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    // collect node ids:
    String selectedNodeId = selectedNode.getObjectId(controller);
    // WORKAROUND: Make target of local hyperlinks for the case, that ids
    // are not stored persistently.
    controller.getMapModel().getLinkRegistry().registerLocalHyperlinkId(selectedNodeId);
    Vector<String> selectedNodesId = new Vector<>();
    for (Iterator iter = selectedNodes.iterator(); iter.hasNext();) {
      MindMapNode node = (MindMapNode) iter.next();
      String nodeId = node.getObjectId(controller);
      // WORKAROUND: Make target of local hyperlinks for the case, that
      // ids are not stored persistently.
      controller.getMapModel().getLinkRegistry().registerLocalHyperlinkId(nodeId);
      selectedNodesId.add(nodeId);
    }
    if (upwards) {
      if (selectedParent.isRoot()) {
        // change side of the items:
        boolean isLeft = selectedNode.isLeft();
        Transferable copy = controller.cut(selectedNodes);
        controller.paste(copy, selectedParent, false, !isLeft);
        select(selectedNodeId, selectedNodesId);
        return;
      }
      // determine child pos of parent
      MindMapNode grandParent = selectedParent.getParentNode();
      int parentPosition = grandParent.getChildPosition(selectedParent);
      boolean isLeft = selectedParent.isLeft();
      Transferable copy = controller.cut(selectedNodes);
      if (parentPosition == grandParent.getChildCount() - 1) {
        controller.paste(copy, grandParent, false, isLeft);
      } else {
        controller.paste(copy, (MindMapNode) grandParent.getChildAt(parentPosition + 1), true, isLeft);
      }
      select(selectedNodeId, selectedNodesId);
    } else {
      int ownPosition = selectedParent.getChildPosition(selectedNode);
      // find node above the own nodes:
      MindMapNode directSibling = null;
      for (int i = ownPosition - 1; i >= 0; --i) {
        MindMapNode sibling = (MindMapNode) selectedParent.getChildAt(i);
        if ((!selectedNodes.contains(sibling)) && selectedNode.isLeft() == sibling.isLeft()) {
          directSibling = sibling;
          break;
        }
      }
      if (directSibling == null) {
        // start searching for a sibling after the selected block:
        for (int i = ownPosition + 1; i < selectedParent.getChildCount(); ++i) {
          MindMapNode sibling = (MindMapNode) selectedParent.getChildAt(i);
          if ((!selectedNodes.contains(sibling)) && selectedNode.isLeft() == sibling.isLeft()) {
            directSibling = sibling;
            break;
          }
        }
      }
      if (directSibling != null) {
        // sibling on the same side found:
        Transferable copy = controller.cut(selectedNodes);
        controller.paste(copy, directSibling, false, directSibling.isLeft());
        select(selectedNodeId, selectedNodesId);
      }
    }
    controller.obtainFocusForSelected();
  }

  private void select(String selectedNodeId, List<String> selectedNodesIds) {
    // get new nodes by object id:
    MindMapNode newInstanceOfSelectedNode = controller.getNodeFromID(selectedNodeId);
    List<MindMapNode> newSelecteds = new LinkedList<>();
    for (String nodeId : selectedNodesIds) {
      newSelecteds.add(controller.getNodeFromID(nodeId));
    }
    controller.select(newInstanceOfSelectedNode, newSelecteds);
  }

}
