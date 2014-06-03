package org.rogach.simplymindmap.controller.actions;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.view.MapView;

public class FitToPageAction extends AbstractAction {
  private MindMapController controller;

  public FitToPageAction(MindMapController controller) {
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    zoom();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        scroll();
      }
    });
  }

  private int shift(int coord1, int size1, int coord2, int size2) {
    return coord1 - coord2 + (size1 - size2) / 2;
  }

  private void scroll() {
    MapView view = controller.getView();
    Rectangle rect = view.getInnerBounds();
    Rectangle viewer = view.getVisibleRect();
    view.scrollBy(shift(rect.x, rect.width, viewer.x, viewer.width), shift(rect.y, rect.height, viewer.y, viewer.height));
  }

  private void zoom() {
    MapView view = controller.getView();
    Rectangle rect = view.getInnerBounds();
    // calculate the zoom:
    double oldZoom = view.getZoom();
    JViewport viewPort = (JViewport) view.getParent();
    JScrollPane pane = (JScrollPane) viewPort.getParent();
    Dimension viewer = viewPort.getExtentSize();
    double newZoom = viewer.width * oldZoom / (rect.width + 0.0);
    double heightZoom = viewer.height * oldZoom / (rect.height + 0.0);
    if (heightZoom < newZoom) {
      newZoom = heightZoom;
    }
    view.setZoom((float) (newZoom));
  }

}
