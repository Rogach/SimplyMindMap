/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 05.05.2004
 */


package org.rogach.simplymindmap.modes.mindmapmode.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.rogach.simplymindmap.controller.actions.DeleteNodeAction;
import org.rogach.simplymindmap.controller.actions.PasteNodeAction;
import org.rogach.simplymindmap.controller.actions.UndoPasteNodeAction;
import org.rogach.simplymindmap.controller.actions.XmlAction;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapNode;
import org.rogach.simplymindmap.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActionPair;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActorXml;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeView;

public class DeleteChildAction extends AbstractAction implements ActorXml {
	private final MindMapController controller;
	private String text;

	public DeleteChildAction(MindMapController modeController) {
		super("", null);
		text = "";
		this.controller = modeController;
		this.controller.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		// ask user if not root is selected:
		for (Iterator iterator = controller.getSelecteds().iterator(); iterator
				.hasNext();) {
			MindMapNode node = (MindMapNode) iterator.next();
			if (node.isRoot()) {
        JOptionPane.showMessageDialog(controller.getView(), 
              Resources.getInstance().getResourceString("cannot_delete_root"), "", JOptionPane.ERROR_MESSAGE);
        return;
			}
		}
		// because of multiple selection, cut is better.
		controller.cut();
		// this.c.deleteNode(c.getSelected());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		DeleteNodeAction deleteNodeAction = (DeleteNodeAction) action;
		MindMapNode selectedNode = this.controller
				.getNodeFromID(deleteNodeAction.getNode());
		deleteWithoutUndo(selectedNode);
	}

	/**
     */
	public void deleteWithoutUndo(MindMapNode selectedNode) {
		MindMapNode parent = selectedNode.getParentNode();
		controller.fireNodePreDeleteEvent(selectedNode);

    // deregister node:
		controller.getModel().getLinkRegistry()
				.deregisterLinkTarget(selectedNode);

		// deselect
		MapView view = controller.getView();
		NodeView nodeView = view.getNodeView(selectedNode);
		view.deselect(nodeView);
		if(view.getSelecteds().size() == 0) {
			NodeView newSelectedView;
			int childIndex = parent.getChildPosition(selectedNode);
			if(parent.getChildCount() > childIndex+1) {
				// the next node
				newSelectedView = view.getNodeView((MindMapNode) parent.getChildAt(childIndex+1));
			} else if(childIndex > 0) {
				// the node before:
				newSelectedView = view.getNodeView((MindMapNode) parent.getChildAt(childIndex-1));
			} else {
				// no other node on same level. take the parent.
				newSelectedView = view.getNodeView(parent);
			}
			view.selectAsTheOnlyOneSelected(newSelectedView);
		}
		controller.removeNodeFromParent(selectedNode);
		// post event
		controller.fireNodePostDeleteEvent(selectedNode, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return DeleteNodeAction.class;
	}

	public void deleteNode(MindMapNode selectedNode) {
		String newId = controller.getNodeID(selectedNode);

		Transferable copy = controller.copy(selectedNode, true);
		NodeCoordinate coord = new NodeCoordinate(selectedNode,
				selectedNode.isLeft());
		// Undo-action
		PasteNodeAction pasteNodeAction = null;
		pasteNodeAction = controller.paste.getPasteNodeAction(copy,
				coord, (UndoPasteNodeAction) null);

		DeleteNodeAction deleteAction = getDeleteNodeAction(newId);
		controller.doTransaction(text,
				new ActionPair(deleteAction, pasteNodeAction));
	}

	public DeleteNodeAction getDeleteNodeAction(String newId) {
		DeleteNodeAction deleteAction = new DeleteNodeAction();
		deleteAction.setNode(newId);
		return deleteAction;
	}

}
