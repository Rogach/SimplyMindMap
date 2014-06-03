package org.rogach.simplymindmap.controller;

import org.rogach.simplymindmap.controller.listeners.MapMouseMotionListener;
import org.rogach.simplymindmap.controller.listeners.MapMouseWheelListener;
import org.rogach.simplymindmap.view.MapView;

public class MouseConfigurator {
  public static void configureMouseListeners(MapView view) {
    MapMouseMotionListener mapMouseMotionListener = new MapMouseMotionListener(view);
		view.addMouseListener(mapMouseMotionListener);
		view.addMouseMotionListener(mapMouseMotionListener);
    
    MapMouseWheelListener mapMouseWheelListener = new MapMouseWheelListener(view);
		view.addMouseWheelListener(mapMouseWheelListener);
  }
}
