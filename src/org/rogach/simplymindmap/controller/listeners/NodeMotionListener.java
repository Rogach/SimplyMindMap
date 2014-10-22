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
/*$Id: NodeMotionListener.java,v 1.1.4.3.2.1 2006/04/05 21:26:24 dpolivaev Exp $*/

package org.rogach.simplymindmap.controller.listeners;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeMotionListenerView;
import org.rogach.simplymindmap.view.NodeView;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMotionListener extends MouseAdapter implements
		MouseMotionListener, MouseListener {

  private MindMapController controller;

	public NodeMotionListener(MindMapController controller) {
    this.controller = controller;
	}
  
  private Point dragStartingPoint = null;
	private int originalParentVGap;
	private int originalHGap;
	private int originalShiftY;
  
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1 && e.getClickCount() == 2) {
			if (e.getModifiersEx() == 0) {
				NodeView nodeV = getNodeView(e);
				MindMapNode node = nodeV.getModel();
				controller.moveNodePosition(node, node.getVGap(), MindMapNode.HGAP, 0);
				return;
			}
			if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
				NodeView nodeV = getNodeView(e);
				MindMapNode node = nodeV.getModel();
				controller.moveNodePosition(node, MindMapNode.VGAP, node.getHGap(),
						node.getShiftY());
				return;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == (InputEvent.BUTTON1_DOWN_MASK)) {
			final NodeMotionListenerView motionListenerView = (NodeMotionListenerView) e
					.getSource();
			final NodeView nodeView = getNodeView(e);
			final MapView mapView = nodeView.getMap();
			MindMapNode node = nodeView.getModel();
			Point point = e.getPoint();
			Tools.convertPointToAncestor(motionListenerView, point,
					JScrollPane.class);
			if (!isActive()) {
				setDragStartingPoint(point, node);
			} else {
				Point dragNextPoint = point;
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
					int nodeShiftY = getNodeShiftY(dragNextPoint, node,
							dragStartingPoint);
					int hGap = getHGap(dragNextPoint, node, dragStartingPoint);
					node.setShiftY(nodeShiftY);
					node.setHGap(hGap);
                    dragStartingPoint = dragNextPoint;
				} else {
					MindMapNode parentNode = nodeView.getVisibleParentView()
							.getModel();
					parentNode
							.setVGap(getVGap(dragNextPoint, dragStartingPoint));
					controller.nodeRefresh(parentNode);
				}
				controller.nodeRefresh(node);
			}
			Point mapPoint = e.getPoint();
			Tools.convertPointToAncestor(motionListenerView, mapPoint, mapView);
			boolean isEventPointVisible = mapView.getVisibleRect().contains(
					mapPoint);
			if (!isEventPointVisible) {
				Rectangle r = new Rectangle(mapPoint);
				Rectangle bounds = mapView.getBounds();
				mapView.scrollRectToVisible(r);
				Rectangle bounds2 = mapView.getBounds();
				int diffx = bounds2.x - bounds.x;
				int diffy = bounds2.y - bounds.y;
				try {
					mapPoint.translate(diffx, diffy);
					// here, there are strange cases, when the mouse moves away.
					// Workaround.
					if (mapView.getVisibleRect().contains(mapPoint)) {
						(new Robot()).mouseMove(e.getXOnScreen() + diffx,
								e.getYOnScreen() + diffy);
					}
				} catch (AWTException e1) {
          Logger.getLogger(NodeMotionListener.class.getName()).log(Level.SEVERE, null, e1);
				}
				dragStartingPoint.x += ((node.getHGap() < 0) ? 2 : 1) * diffx;
				dragStartingPoint.y += ((node.getShiftY() < 0) ? 2 : 1) * diffy;
			}
		}
	}
  
  public void mouseEntered(MouseEvent e) {
		if (!JOptionPane.getFrameForComponent(e.getComponent()).isFocused())
			return;
		if (!isActive()) {
			NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
			v.setMouseEntered();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (!isActive()) {
			NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
			v.setMouseExited();
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
		if (!v.contains(e.getX(), e.getY()))
			v.setMouseExited();
		if (!isActive())
			return;
		NodeView nodeV = getNodeView(e);
		Point point = e.getPoint();
		Tools.convertPointToAncestor(nodeV, point, JScrollPane.class);
		// move node to end position.
		MindMapNode node = nodeV.getModel();
		MindMapNode parentNode = nodeV.getModel().getParentNode();
		final int parentVGap = parentNode.getVGap();
		final int hgap = node.getHGap();
		final int shiftY = node.getShiftY();
		resetPositions(node);
		controller.moveNodePosition(node, parentVGap, hgap, shiftY);
		stopDrag();
	}
  
	private int getVGap(Point dragNextPoint, Point dragStartingPoint) {
		int oldVGap = originalParentVGap;
		int vGapChange = 
            (int) ((dragNextPoint.y - dragStartingPoint.y) /
            controller.getView().getZoom());
		oldVGap = Math.max(0, oldVGap - vGapChange);
		return oldVGap;
	}

	private int getHGap(Point dragNextPoint, MindMapNode node,
			Point dragStartingPoint) {
		int oldHGap = node.getHGap();
		int hGapChange = 
            (int) ((dragNextPoint.x - dragStartingPoint.x) / 
            controller.getView().getZoom());
		if (node.isLeft())
			hGapChange = -hGapChange;
		oldHGap += +hGapChange;
		return oldHGap;
	}

	private int getNodeShiftY(Point dragNextPoint, MindMapNode pNode,
			Point dragStartingPoint) {
		int shiftY = pNode.getShiftY();
		int shiftYChange = 
            (int) ((dragNextPoint.y - dragStartingPoint.y) /
            controller.getView().getZoom());
		shiftY += shiftYChange;
		return shiftY;
	}
  
  private NodeView getNodeView(MouseEvent e) {
		return ((NodeMotionListenerView) e.getSource()).getMovedView();
	}
  
 	private void stopDrag() {
		setDragStartingPoint(null, null);
	}

  private void resetPositions(MindMapNode node) {
		node.getParentNode().setVGap(originalParentVGap);
		node.setHGap(originalHGap);
		node.setShiftY(originalShiftY);
	}

	public boolean isActive() {
		return getDragStartingPoint() != null;
	}

	void setDragStartingPoint(Point point, MindMapNode node) {
		dragStartingPoint = point;
		if (point != null) {
			originalParentVGap = node.getParentNode().getVGap();
			originalHGap = node.getHGap();
			originalShiftY = node.getShiftY();
		} else {
			originalParentVGap = originalHGap = originalShiftY = 0;
		}
	}

	Point getDragStartingPoint() {
		return dragStartingPoint;
	}

}
