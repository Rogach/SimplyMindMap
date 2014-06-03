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
/*$Id: MapMouseMotionListener.java,v 1.7.16.5.2.1 2008/01/04 22:52:30 christianfoltin Exp $*/

package org.rogach.simplymindmap.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.rogach.simplymindmap.view.MapView;

/**
 * The MouseListener which belongs to MapView
 */
public class MapMouseMotionListener implements MouseMotionListener,
		MouseListener {

	public interface MapMouseMotionReceiver {
		public void mouseDragged(MouseEvent e);

		public void mousePressed(MouseEvent e);

		public void mouseReleased(MouseEvent e);
	}

  private final MapView view;
  
  private int originX = -1;
	private int originY = -1;

	public MapMouseMotionListener(MapView view) {
    this.view = view;
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		// Always try to get mouse to the original position in the Map.
		if (originX >= 0) {
			view.scrollBy(originX - e.getX(), originY - e.getY());
		}
	}

	public void mouseClicked(MouseEvent e) {
		// to loose the focus in edit
		view.selectAsTheOnlyOneSelected(view.getSelected());
    view.requestFocusInWindow();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!e.isPopupTrigger()) {
      if (!view.getController().isBlocked() && e.getButton() == MouseEvent.BUTTON1) {
			view.setMoveCursor(true);
			originX = e.getX();
			originY = e.getY();
		}
    }
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
    originX = -1;
		originY = -1;
		e.consume();
		view.setMoveCursor(false);
	}
}
