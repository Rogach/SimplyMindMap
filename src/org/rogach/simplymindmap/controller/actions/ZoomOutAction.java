package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;

public class ZoomOutAction extends AbstractAction {
  
  private MindMapController controller;
  
  private static final float[] zoomValues = { 25 / 100f, 50 / 100f,
    75 / 100f, 100 / 100f, 150 / 100f, 200 / 100f, 300 / 100f,
    400 / 100f };

  
  public ZoomOutAction(MindMapController controller) {
    super(controller.getResources().getText("zoom_out"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_zoom_out")));
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    float currentZoom = controller.getView().getZoom();
    float lastZoom = zoomValues[0];
    for (int i = 0; i < zoomValues.length; i++) {
      float val = zoomValues[i];
      if (val >= currentZoom) {
        controller.getView().setZoom(lastZoom);
        return;
      }
      lastZoom = val;
    }
    controller.getView().setZoom(lastZoom);
  }

}
