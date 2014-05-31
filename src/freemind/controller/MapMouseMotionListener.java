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

package freemind.controller;

import freemind.view.mindmapview.MapView;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

	private MapMouseMotionReceiver mReceiver;
  
  private final MapView view;

	public MapMouseMotionListener(MapView view) {
    this.view = view;
	}

	public void register(MapMouseMotionReceiver receiver) {
		mReceiver = receiver;
	}

	public void deregister() {
		mReceiver = null;
	}

	private void handlePopup(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (mReceiver != null) {
			mReceiver.mouseDragged(e);
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
		if (e.isPopupTrigger()) { // start the move, when the user press the
									// mouse (PN)
			handlePopup(e);
		} else if (mReceiver != null)
			mReceiver.mousePressed(e);
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		if (mReceiver != null) {
			mReceiver.mouseReleased(e);
		}
		handlePopup(e);
		e.consume();
		view.setMoveCursor(false); // release the cursor to default
											// (PN)
	}
}
