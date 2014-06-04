package org.rogach.simplymindmap.controller;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.util.Collections;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.listeners.NodeKeyListener;
import org.rogach.simplymindmap.view.MapView;

public class KeymapConfigurator {
  public static void configureKeymap(MapView view, MindMapController controller) {
    putAction(view, "new_child_action", "keystroke_add_child", controller.newChild);
    putAction(view, "new_sibling_action", "keystroke_add", controller.newSibling);
    putAction(view, "new_previous_sibling_action", "keystroke_add_sibling_before", controller.newPreviousSibling);
    putAction(view, "copy_action", "keystroke_copy", controller.copy);
    putAction(view, "paste_action", "keystroke_paste", controller.paste);
    putAction(view, "cut_action", "keystroke_cut", controller.cut);
    putAction(view, "edit_action", "keystroke_edit", controller.edit);
    putAction(view, "edit_long_action", "keystroke_edit_long_node", controller.editLong);
    putAction(view, "icon_selection", "keystroke_accessories/plugins/IconSelectionPlugin.properties.properties_key", controller.iconSelectionAction);
    putAction(view, "change_node_level_left", "keystroke_accessories/plugins/ChangeNodeLevelAction_left.properties_key", controller.changeNodeLevelLeft);
    putAction(view, "change_node_level_right", "keystroke_accessories/plugins/ChangeNodeLevelAction_right.properties_key", controller.changeNodeLevelRight);
    putAction(view, "toggle_folded", "keystroke_toggle_folded", controller.toggleFolded);
    putAction(view, "toggle_children_folded", "keystroke_toggle_children_folded", controller.toggleChildrenFolded);
    putAction(view, "node_up", "keystroke_node_up", controller.nodeUp);
    putAction(view, "node_down", "keystroke_node_down", controller.nodeDown);
    putAction(view, "select_all", "keystroke_select_all", controller.selectAllAction);
    putAction(view, "select_branch", "keystroke_select_branch", controller.selectBranchAction);
    putAction(view, "delete_node", "keystroke_delete_child", controller.deleteChild);
    putAction(view, "find", "keystroke_find", controller.find);
    putAction(view, "find_next", "keystroke_find_next", controller.findNext);
    putAction(view, "zoom_in", "keystroke_zoom_in", controller.zoomIn);
    putAction(view, "zoom_out", "keystroke_zoom_out", controller.zoomOut);
    putAction(view, "increase_font_size", "keystroke_node_increase_font_size", controller.increaseNodeFont);
    putAction(view, "decrease_font_size", "keystroke_node_decrease_font_size", controller.decreaseNodeFont);
    putAction(view, "boldface", "keystroke_node_toggle_boldface", controller.bold);
    putAction(view, "italic", "keystroke_node_toggle_italic", controller.italic);
    putAction(view, "node_color", "keystroke_node_color", controller.nodeColor);
    
    view.addKeyListener(new NodeKeyListener(controller));
    
    Set<AWTKeyStroke> noKeyStrokes = Collections.emptySet();
    view.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, noKeyStrokes);
		view.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, noKeyStrokes);
		view.setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, noKeyStrokes);
  }
  
  private static void putAction(MapView view, String actionKey, String strokeProperty, Action action) {
    view.getActionMap().put(actionKey, action);
    view.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(view.getResources().unsafeGetProperty(strokeProperty)), actionKey);
  }
}
