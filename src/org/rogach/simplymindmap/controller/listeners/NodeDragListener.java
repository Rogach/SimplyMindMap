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

package org.rogach.simplymindmap.controller.listeners;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.MindMapNodesSelection;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.PropertyKey;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MainView;

/**
 * The NodeDragListener which belongs to every NodeView
 */
public class NodeDragListener implements DragGestureListener {

	private final MindMapController controller;

	public NodeDragListener(MindMapController controller) {
    this.controller = controller;
	}

	public Cursor getCursorByAction(int dragAction) {
		switch (dragAction) {
		case DnDConstants.ACTION_COPY:
			return DragSource.DefaultCopyDrop;
		case DnDConstants.ACTION_LINK:
			return DragSource.DefaultLinkDrop;
		default:
			return DragSource.DefaultMoveDrop;
		}
	}

	public void dragGestureRecognized(DragGestureEvent e) {
		if (!controller.getResources().getBoolProperty(PropertyKey.DRAG_AND_DROP))
			return;

		MindMapNode node = ((MainView) e.getComponent()).getNodeView()
				.getModel();
		if (node.isRoot())
			return;

		// Transferable t; // = new StringSelection("");
		String dragAction = "MOVE";

		Cursor cursor = getCursorByAction(e.getDragAction());

		int modifiersEx = e.getTriggerEvent().getModifiersEx();
		boolean macLinkAction = Tools.isMacOsX()
				&& ((modifiersEx & InputEvent.BUTTON1_DOWN_MASK) != 0)
				&& e.getTriggerEvent().isMetaDown();
		boolean otherOsLinkAction = (modifiersEx & InputEvent.BUTTON3_DOWN_MASK) != 0;
		if (macLinkAction || otherOsLinkAction) {
			// Change drag action
			cursor = DragSource.DefaultLinkDrop;
			dragAction = "LINK";
		}

		if ((modifiersEx & InputEvent.BUTTON2_DOWN_MASK) != 0) {
			// Change drag action
			cursor = DragSource.DefaultCopyDrop;
			dragAction = "COPY";
		}

		Transferable t = controller.copy();
		// new MindMapNodesSelection("Ahoj","Ahoj","Ahoj", dragAction);
		((MindMapNodesSelection) t).setDropAction(dragAction);
		// public void setDropAction(String dropActionContent) {

		// starts the dragging
		// DragSource dragSource = DragSource.getDefaultDragSource();

		e.startDrag(cursor, t, new DragSourceListener() {
			public void dragDropEnd(DragSourceDropEvent dsde) {

			}

			public void dragEnter(DragSourceDragEvent e) {
			}

			public void dragExit(DragSourceEvent dse) {
			}

			public void dragOver(DragSourceDragEvent dsde) {
			}

			public void dropActionChanged(DragSourceDragEvent dsde) {
				dsde.getDragSourceContext().setCursor(
						getCursorByAction(dsde.getUserAction()));
			}
		});
	}
}
