package freemind.view.mindmapview;

import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.common.dialogs.IconSelectionPopupDialog;
import freemind.modes.mindmapmode.MindMapController;
import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeymapConfigurator {
  public static void configureKeymap(final MapView view, final MindMapController controller) {
    putAction(view, "new_child_action", "INSERT", controller.newChild);
    putAction(view, "new_sibling_action", "ENTER", controller.newSibling);
    putAction(view, "new_previous_sibling_action", "shift ENTER", controller.newPreviousSibling);
    putAction(view, "copy_action", "control C", controller.copy);
    putAction(view, "paste_action", "control V", controller.paste);
    putAction(view, "cut_action", "control X", controller.cut);
    putAction(view, "edit_action", "F2", controller.edit);
    putAction(view, "icon_selection", "alt I", new IconSelectionAction(view, controller));
    putAction(view, "change_node_level_left", "control LEFT", new ChangeNodeLevelAction("left", controller));
    putAction(view, "change_node_level_right", "control RIGHT", new ChangeNodeLevelAction("right", controller));
  }
  
  private static void putAction(MapView view, String actionKey, String keyStroke, Action action) {
    view.getActionMap().put(actionKey, action);
    view.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyStroke), actionKey);
  }
  
  public static class IconSelectionAction extends AbstractAction {
    private MapView view;
    private MindMapController controller;
    
    public IconSelectionAction(MapView view, MindMapController controller) {
      super();
      this.view = view;
      this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NodeView focussed = controller.getSelectedView();
        Vector actions = new Vector();
        Vector iconActions = controller.iconActions;
        actions.addAll(iconActions);
        actions.add(controller.removeLastIconAction);
        actions.add(controller.removeAllIconsAction);

        Component c = view;
        while (!(c instanceof Window) && c != null) {
          c = c.getParent();
        }
        if (c == null) {
          throw new RuntimeException("No parent frame found!");
        }
        Window frame = (Window) c;
        
        IconSelectionPopupDialog selectionDialog = new IconSelectionPopupDialog(
            frame, actions);

        final MapView mapView = controller.getView();
        mapView.scrollNodeToVisible(focussed, 0);
        selectionDialog.pack();
        Tools.setDialogLocationRelativeTo(selectionDialog, focussed);
        selectionDialog.setModal(true);
        selectionDialog.setVisible(true);
        // process result:
        int result = selectionDialog.getResult();
        if (result >= 0) {
          Action action = (Action) actions.get(result);
          action.actionPerformed(new ActionEvent(action, 0, "icon",
					selectionDialog.getModifiers()));
        }
    }
    
  }
  
  public static class ChangeNodeLevelAction extends AbstractAction {
    private String direction;
    private MindMapController controller;
    public ChangeNodeLevelAction(String direction, MindMapController controller) {
      super();
      this.direction = direction;
      this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      MindMapNode selectedNode;
      List selectedNodes;
      {
        MindMapNode focussed = controller.getSelected();
        List selecteds = controller.getSelecteds();
        selectedNode = focussed;
        selectedNodes = selecteds;
      }

      // bug fix: sort to make independent by user's selection:
      controller.sortNodesByDepth(selectedNodes);

      if (selectedNode.isRoot()) {
        throw new RuntimeException(Resources.getInstance().getResourceString("cannot_add_parent_to_root"));
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
          throw new RuntimeException(Resources.getInstance().getResourceString("cannot_add_parent_diff_parents"));
        }
        if (node.isRoot()) {
          throw new RuntimeException(Resources.getInstance().getResourceString("cannot_add_parent_to_root"));
        }
      }

      // collect node ids:
      String selectedNodeId = selectedNode.getObjectId(controller);
      // WORKAROUND: Make target of local hyperlinks for the case, that ids
      // are not stored persistently.
      controller.getMap().getLinkRegistry().registerLocalHyperlinkId(selectedNodeId);
      Vector selectedNodesId = new Vector();
      for (Iterator iter = selectedNodes.iterator(); iter.hasNext();) {
        MindMapNode node = (MindMapNode) iter.next();
        String nodeId = node.getObjectId(controller);
        // WORKAROUND: Make target of local hyperlinks for the case, that
        // ids are not stored persistently.
        controller.getMap().getLinkRegistry().registerLocalHyperlinkId(nodeId);
        selectedNodesId.add(nodeId);
      }

      if (upwards) {
        if (selectedParent.isRoot()) {
          // change side of the items:
          boolean isLeft = selectedNode.isLeft();
          Transferable copy = controller.cut(selectedNodes);
          controller.paste(copy, selectedParent, false,
              !isLeft);
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
          controller.paste(
              copy,
              (MindMapNode) grandParent
                  .getChildAt(parentPosition + 1), true, isLeft);
        }
        select(selectedNodeId, selectedNodesId);

      } else {
        int ownPosition = selectedParent.getChildPosition(selectedNode);
        // find node above the own nodes:
        MindMapNode directSibling = null;
        for (int i = ownPosition - 1; i >= 0; --i) {
          MindMapNode sibling = (MindMapNode) selectedParent
              .getChildAt(i);
          if ((!selectedNodes.contains(sibling))
              && selectedNode.isLeft() == sibling.isLeft()) {
            directSibling = sibling;
            break;
          }
        }
        if (directSibling == null) {
          // start searching for a sibling after the selected block:
          for (int i = ownPosition + 1; i < selectedParent
              .getChildCount(); ++i) {
            MindMapNode sibling = (MindMapNode) selectedParent
                .getChildAt(i);
            if ((!selectedNodes.contains(sibling))
                && selectedNode.isLeft() == sibling.isLeft()) {
              directSibling = sibling;
              break;
            }
          }
        }
        if (directSibling != null && directSibling.isWriteable()) {
          // sibling on the same side found:
          Transferable copy = controller.cut(selectedNodes);
          controller.paste(copy, directSibling, false,
              directSibling.isLeft());
          select(selectedNodeId, selectedNodesId);
        }
      }
      controller.obtainFocusForSelected();
    }
    
    private void select(String selectedNodeId, List selectedNodesIds) {
      // get new nodes by object id:
      MindMapNode newInstanceOfSelectedNode = controller
          .getNodeFromID(selectedNodeId);
      List newSelecteds = new LinkedList();
      for (Iterator iter = selectedNodesIds.iterator(); iter.hasNext();) {
        String nodeId = (String) iter.next();
        newSelecteds.add(controller.getNodeFromID(nodeId));
      }
      controller.select(newInstanceOfSelectedNode, newSelecteds);
    }
  }
}
