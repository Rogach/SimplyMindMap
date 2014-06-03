/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: MapMouseWheelListener.java,v 1.8.18.1 2006/01/12 23:10:12 christianfoltin Exp $*/

package org.rogach.simplymindmap.controller;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;
import org.rogach.simplymindmap.view.MapView;

/**
 * The MouseListener which belongs to MapView
 */
public class MapMouseWheelListener implements MouseWheelListener {
  
  private static int SCROLL_SKIPS = Resources.getInstance().getIntProperty("wheel_velocity", 8);
	private static final int HORIZONTAL_SCROLL_MASK = InputEvent.SHIFT_MASK
			| InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK
			| InputEvent.BUTTON3_MASK;
	private static final int ZOOM_MASK = InputEvent.CTRL_MASK;

  private MapView view;
  
	public MapMouseWheelListener(MapView view) {
    this.view = view;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
    MindMapController mController = view.getController();
		if (mController.isBlocked()) {
			return; // block the scroll during edit (PN)
		}

		if ((e.getModifiers() & ZOOM_MASK) != 0) {
			// fc, 18.11.2003: when control pressed, then the zoom is changed.
			float newZoomFactor = 1f + Math.abs((float) e.getWheelRotation()) / 10f;
			if (e.getWheelRotation() < 0)
				newZoomFactor = 1 / newZoomFactor;
      MapView view = (MapView) e.getComponent();
			final float oldZoom = view.getZoom();
			float newZoom = oldZoom / newZoomFactor;
			// round the value due to possible rounding problems.
			newZoom = (float) Math.rint(newZoom * 1000f) / 1000f;
			newZoom = Math.max(1f / 32f, newZoom);
			newZoom = Math.min(32f, newZoom);
			if (newZoom != oldZoom) {
        view.setZoom(newZoom);
			}
			// end zoomchange
		} else if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
			((MapView) e.getComponent()).scrollBy(
					SCROLL_SKIPS * e.getWheelRotation(), 0);
		} else {
			((MapView) e.getComponent()).scrollBy(0,
					SCROLL_SKIPS * e.getWheelRotation());
		}
	}

}
