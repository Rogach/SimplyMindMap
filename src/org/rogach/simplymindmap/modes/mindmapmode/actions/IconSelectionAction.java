package org.rogach.simplymindmap.modes.mindmapmode.actions;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;
import org.rogach.simplymindmap.util.IconSelectionPopupDialog;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeView;

public class IconSelectionAction extends AbstractAction {
  private MindMapController controller;

  public IconSelectionAction(MindMapController controller) {
    super();
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
    Component c = controller.getView();
    while (!(c instanceof Window) && c != null) {
      c = c.getParent();
    }
    if (c == null) {
      throw new RuntimeException("No parent frame found!");
    }
    Window frame = (Window) c;
    IconSelectionPopupDialog selectionDialog = new IconSelectionPopupDialog(frame, actions);
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
      action.actionPerformed(new ActionEvent(action, 0, "icon", selectionDialog.getModifiers()));
    }
  }

}
