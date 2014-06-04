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
/*$Id: NodeMouseMotionListener.java,v 1.15.14.3 2006/01/12 23:10:12 christianfoltin Exp $*/

package org.rogach.simplymindmap.controller.listeners;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.util.PropertyKey;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MainView;
import org.rogach.simplymindmap.view.NodeView;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMouseMotionListener implements MouseMotionListener,
		MouseListener {
  
  private MindMapController controller;

  	/** time in ms, overwritten by property time_for_delayed_selection */
	private static Tools.IntHolder timeForDelayedSelection;

	/** overwritten by property delayed_selection_enabled */
	private static Tools.BooleanHolder delayedSelectionEnabled;

  private Timer timerForDelayedSelection;
  
  /**
	 * The mouse has to stay in this region to enable the selection after a
	 * given time.
	 */
	private Rectangle controlRegionForDelayedSelection;

	private MouseEvent mMousePressedEvent;
  
	public NodeMouseMotionListener(MindMapController controller) {
    this.controller = controller;
    if (delayedSelectionEnabled == null)
			updateSelectionMethod();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		// first stop the timer and select the node:
		stopTimerForDelayedSelection();
		NodeView nodeV = ((MainView) e.getComponent()).getNodeView();

		// if dragged for the first time, select the node:
		if (!controller.getView().isSelected(nodeV))
			controller.extendSelection(e);
	}

	public void mouseEntered(MouseEvent e) {
		if (!JOptionPane.getFrameForComponent(e.getComponent()).isFocused())
			return;
		createTimer(e);
	}

	public void mouseExited(MouseEvent e) {
		stopTimerForDelayedSelection();
	}

	public void mouseMoved(MouseEvent e) {
		// Invoked when the mouse button has been moved on a component (with no
		// buttons down).
		MainView node = ((MainView) e.getComponent());
		// test if still in selection region:
		if (controlRegionForDelayedSelection != null
				&& delayedSelectionEnabled.getValue()) {
			if (!controlRegionForDelayedSelection.contains(e.getPoint())) {
				// point is not in the region. start timer again and adjust
				// region to the current point:
				createTimer(e);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		// for Linux/Mac
		mMousePressedEvent = e;
	}

	public void mouseReleased(MouseEvent e) {
		// handling click in mouseReleased rather than in mouseClicked
		// provides better interaction. If mouse was slightly moved
		// between pressed and released events, the event clicked
		// is not triggered.
		// The behavior is not tested on Linux.
		
		MouseEvent ev = e;
		/* 
		 * For Mac see 
		 * https://developer.apple.com/library/mac/#documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html
		 * */
		if(Tools.isLinux() || Tools.isMacOsX()) {
			ev = mMousePressedEvent;
		} 
		handlePopupMenu(ev);
		
		if (ev.isConsumed()) {
			return;
		}

		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			// FIXME Dimitry: Double Click comes after Plain Click combining
			// (un)folding with editing
			// if (e.getClickCount() % 2 == 0) {
			// c.doubleClick(e);
			// } else {
			controller.plainClick(e);
			// }
			e.consume();
		}
	}
  
  /**
	 * And a static method to reread this holder. This is used when the
	 * selection method is changed via the option menu.
	 */
	public void updateSelectionMethod() {
		if (timeForDelayedSelection == null) {
			timeForDelayedSelection = new Tools.IntHolder();
		}
		delayedSelectionEnabled = new Tools.BooleanHolder();
		delayedSelectionEnabled.setValue(
            !controller.getResources().getProperty(PropertyKey.SELECTION_METHOD)
            .equals("selection_method_direct"));
		/*
		 * set time for delay to infinity, if selection_method equals
		 * selection_method_by_click.
		 */
		if (controller.getResources().getProperty(PropertyKey.SELECTION_METHOD)
				.equals("selection_method_by_click")) {
			timeForDelayedSelection.setValue(Integer.MAX_VALUE);
		} else {
			timeForDelayedSelection.setValue(controller.getResources().getIntProperty(PropertyKey.TIME_FOR_DELAYED_SELECTION, 100));
		}
	}
  
  
	protected void handlePopupMenu(MouseEvent e) {
		// first stop the timer and select the node:
		stopTimerForDelayedSelection();
		controller.extendSelection(e);
		// Right mouse <i>press</i> is <i>not</i> a popup trigger for Windows.
		// Only Right mouse release is a popup trigger!
		// OK, but Right mouse <i>press</i> <i>is</i> a popup trigger on Linux.
    
		//c.showPopupMenu(e);
    //popupmenu.show(e.getComponent(), e.getX(), e.getY());
    //e.consume();
	}

	protected Rectangle getControlRegion(Point2D p) {
		// Create a small square around the given point.
		int side = 8;
		return new Rectangle((int) (p.getX() - side / 2),
				(int) (p.getY() - side / 2), side, side);
	}

	public void createTimer(MouseEvent e) {
		// stop old timer if present.*/
		stopTimerForDelayedSelection();
		/* Region to check for in the sequel. */
		controlRegionForDelayedSelection = getControlRegion(e.getPoint());
		timerForDelayedSelection = new Timer();
		timerForDelayedSelection.schedule(
				new timeDelayedSelection(controller, e),
				/*
				 * if the new selection method is not enabled we put 0 to get
				 * direct selection.
				 */
				(delayedSelectionEnabled.getValue()) ? timeForDelayedSelection
						.getValue() : 0);
	}

	protected void stopTimerForDelayedSelection() {
		// stop timer.
		if (timerForDelayedSelection != null)
			timerForDelayedSelection.cancel();
		timerForDelayedSelection = null;
		controlRegionForDelayedSelection = null;
	}

	protected class timeDelayedSelection extends TimerTask {
		private final MindMapController c;

		private final MouseEvent e;

		timeDelayedSelection(MindMapController c, MouseEvent e) {
			this.c = c;
			this.e = e;
		}

		/** TimerTask method to enable the selection after a given time. */
		public void run() {
			/*
			 * formerly in ControllerAdapter. To guarantee, that point-to-select
			 * does not change selection if any meta key is pressed.
			 */
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (e.getModifiers() == 0 && !c.isBlocked()
							&& c.getView().getSelecteds().size() <= 1) {
						c.extendSelection(e);
					}
				}
			});
		}
	}

}
