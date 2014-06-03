/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2005  Christian Foltin <christianfoltin@users.sourceforge.net>
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


package org.rogach.simplymindmap.modes.mindmapmode.listeners;

import java.awt.event.MouseEvent;
import org.rogach.simplymindmap.controller.MapMouseMotionListener.MapMouseMotionReceiver;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;
import org.rogach.simplymindmap.view.MapView;

/** */
public class MindMapMouseMotionManager implements MapMouseMotionReceiver {

	int originX = -1;
	int originY = -1;

	private final MindMapController mController;

	// |= oldX >=0 iff we are in the drag

	/**
     *
     */
	public MindMapMouseMotionManager(MindMapController controller) {
		super();
		this.mController = controller;

	}

	public void mouseDragged(MouseEvent e) {
		MapView mapView = (MapView) e.getComponent();
		// Always try to get mouse to the original position in the Map.
		if (originX >= 0) {
			mapView.scrollBy(originX - e.getX(), originY - e.getY());
		}
	}

	public void mousePressed(MouseEvent e) {
		if (!mController.isBlocked() && e.getButton() == MouseEvent.BUTTON1) {
			mController.getView().setMoveCursor(true);
			originX = e.getX();
			originY = e.getY();
		}
	}

	public void mouseReleased(MouseEvent e) {
		originX = -1;
		originY = -1;
	}

}
