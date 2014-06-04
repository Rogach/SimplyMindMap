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


package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.DeleteNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.NewNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.PropertyKey;
import org.rogach.simplymindmap.view.NodeView;

public class NewChildAction extends AbstractAction implements ActorXml {
	private final MindMapController controller;
	private static Logger logger = null;

	public NewChildAction(MindMapController modeController) {
		super(modeController.getResources().getText("new_child"), modeController.getResources().getIcon("idea.png"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(modeController.getResources().unsafeGetProperty("keystroke_add_child")));
		this.controller = modeController;
		this.controller.getActionFactory().registerActor(this, getDoActionClass());
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}

	}

	public void actionPerformed(ActionEvent e) {
		this.controller.addNew(controller.getSelected(), MindMapController.NEW_CHILD, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		NewNodeAction addNodeAction = (NewNodeAction) action;
		MindMapNode parent = this.controller.getNodeFromID(addNodeAction.getNode());
		int index = addNodeAction.getIndex();
		MindMapNode newNode = controller.newNode("");
		newNode.setLeft(addNodeAction.getPosition().equals("left"));
		String newId = addNodeAction.getNewId();
		String givenId = controller.getMapModel().getLinkRegistry()
				.registerLinkTarget(newNode, newId);
		if (!givenId.equals(newId)) {
			throw new IllegalArgumentException("Designated id '" + newId
					+ "' was not given to the node. It received '" + givenId
					+ "'.");
		}
		controller.insertNodeInto(newNode, parent, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return NewNodeAction.class;
	}

	public MindMapNode addNew(final MindMapNode target, int newNodeMode,
			final KeyEvent e) {
		final MindMapNode targetNode = target;
		MindMapNode newNode = null;

		switch (newNodeMode) {
		case MindMapController.NEW_SIBLING_BEFORE:
		case MindMapController.NEW_SIBLING_BEHIND: {
			if (!targetNode.isRoot()) {
				MindMapNode parent = targetNode.getParentNode();
				int childPosition = parent.getChildPosition(targetNode);
				if (newNodeMode == MindMapController.NEW_SIBLING_BEHIND) {
					childPosition++;
				}
				newNode = addNewNode(parent, childPosition, targetNode.isLeft());
				final NodeView nodeView = controller.getNodeView(newNode);
				controller.select(nodeView);
				controller.edit.edit(nodeView, controller.getNodeView(target), e, true, false,
						false);
				break;
			} else {
				// fc, 21.8.07: we don't do anything here and get a new child
				// instead.
				newNodeMode = MindMapController.NEW_CHILD;
				// @fallthrough
			}
		}

		case MindMapController.NEW_CHILD:
		case MindMapController.NEW_CHILD_WITHOUT_FOCUS: {
			final boolean parentFolded = targetNode.isFolded();
			if (parentFolded) {
				controller.setFolded(targetNode, false);
			}
			int position = controller.getResources().getProperty(PropertyKey.PLACE_NEW_BRANCHES).equals("last") ? targetNode.getChildCount() : 0;
			newNode = addNewNode(targetNode, position);
			final NodeView nodeView = controller.getNodeView(newNode);
			if (newNodeMode == MindMapController.NEW_CHILD) {
				controller.select(nodeView);
			}
			controller.edit.edit(nodeView, controller.getNodeView(target), e, true, parentFolded,
					false);
			break;
		}
		}
		return newNode;
	}

	public MindMapNode addNewNode(MindMapNode parent, int index) {
		return addNewNode(parent, index, parent.isNewChildLeft());
	}

	public MindMapNode addNewNode(MindMapNode parent, int index,
			boolean newNodeIsLeft) {
		if (index == -1) {
			index = parent.getChildCount();
		}
		// bug fix from Dimitri.
		controller.getMapModel().getLinkRegistry().registerLinkTarget(parent);
		String newId = controller.getMapModel().getLinkRegistry().generateUniqueID(null);
		NewNodeAction newNodeAction = getAddNodeAction(parent, index, newId,
				newNodeIsLeft);
		// Undo-action
		DeleteNodeAction deleteAction = controller.deleteChild
				.getDeleteNodeAction(newId);
		controller.doTransaction(controller.getResources().getText("new_child"),
				new ActionPair(newNodeAction, deleteAction));
		return (MindMapNode) parent.getChildAt(index);
	}

	public NewNodeAction getAddNodeAction(MindMapNode parent, int index,
			String newId, boolean newNodeIsLeft) {
		String pos = newNodeIsLeft ? "left" : "right";
		NewNodeAction newNodeAction = new NewNodeAction();
		newNodeAction.setNode(controller.getNodeID(parent));
		newNodeAction.setPosition(pos);
		newNodeAction.setIndex(index);
		newNodeAction.setNewId(newId);
		return newNodeAction;
	}

}