package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;

public class ZoomInAction extends AbstractAction {
  
  MindMapController controller;

  private static final float[] zoomValues = { 25 / 100f, 50 / 100f,
    75 / 100f, 100 / 100f, 150 / 100f, 200 / 100f, 300 / 100f,
    400 / 100f };
  
  public ZoomInAction(MindMapController controller) {
    super(controller.getResources().getText("zoom_in"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_zoom_in")));
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // logger.info("ZoomInAction actionPerformed");
    float currentZoom = controller.getView().getZoom();
    for (int i = 0; i < zoomValues.length; i++) {
      float val = zoomValues[i];
      if (val > currentZoom) {
        controller.getView().setZoom(val);
        return;
      }
    }
    controller.getView().setZoom(zoomValues[zoomValues.length - 1]);
  }

}
