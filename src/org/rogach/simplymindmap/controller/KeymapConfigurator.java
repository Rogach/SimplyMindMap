package org.rogach.simplymindmap.controller;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.view.MapView;

public class KeymapConfigurator {
  public static void configureKeymap(final MapView view, final MindMapController controller) {
    putAction(view, "new_child_action", "INSERT", controller.newChild);
    putAction(view, "new_sibling_action", "ENTER", controller.newSibling);
    putAction(view, "new_previous_sibling_action", "shift ENTER", controller.newPreviousSibling);
    putAction(view, "copy_action", "control C", controller.copy);
    putAction(view, "paste_action", "control V", controller.paste);
    putAction(view, "cut_action", "control X", controller.cut);
    putAction(view, "edit_action", "F2", controller.edit);
    putAction(view, "icon_selection", "alt I", controller.iconSelectionAction);
    putAction(view, "change_node_level_left", "control LEFT", controller.changeNodeLevelLeft);
    putAction(view, "change_node_level_right", "control RIGHT", controller.changeNodeLevelRight);
    putAction(view, "fit_to_page", "alt EQUALS", controller.fitToPage);
    putAction(view, "toggle_folded", "SPACE", controller.toggleFolded);
    putAction(view, "toggle_children_folded", "control SPACE", controller.toggleChildrenFolded);
    putAction(view, "node_up", "control UP", controller.nodeUp);
    putAction(view, "node_down", "control DOWN", controller.nodeDown);
  }
  
  private static void putAction(MapView view, String actionKey, String keyStroke, Action action) {
    view.getActionMap().put(actionKey, action);
    view.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyStroke), actionKey);
  }
}
