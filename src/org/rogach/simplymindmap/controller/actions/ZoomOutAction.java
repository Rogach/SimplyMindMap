package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;

public class ZoomOutAction extends AbstractAction {
  
  private MindMapController controller;
  
  public ZoomOutAction(MindMapController controller) {
    super(controller.getResources().getText("zoom_out"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_zoom_out")));
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    float currentZoom = controller.getView().getZoom();
    float lastZoom = MindMapController.zoomValues[0];
    for (int i = 0; i < MindMapController.zoomValues.length; i++) {
      float val = MindMapController.zoomValues[i];
      if (val >= currentZoom) {
        controller.getView().setZoom(lastZoom);
        return;
      }
      lastZoom = val;
    }
    controller.getView().setZoom(lastZoom);
  }

}
