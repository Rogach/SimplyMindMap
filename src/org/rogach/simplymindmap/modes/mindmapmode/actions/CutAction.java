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
 * Created on 09.05.2004
 */


package org.rogach.simplymindmap.modes.mindmapmode.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.rogach.simplymindmap.controller.actions.CompoundAction;
import org.rogach.simplymindmap.controller.actions.CutNodeAction;
import org.rogach.simplymindmap.controller.actions.UndoPasteNodeAction;
import org.rogach.simplymindmap.controller.actions.XmlAction;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapNode;
import org.rogach.simplymindmap.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActionPair;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActorXml;

public class CutAction extends AbstractAction implements ActorXml {
	private String text;
	private final MindMapController mMindMapController;
	private static java.util.logging.Logger logger = null;

	public CutAction(MindMapController c) {
		super("", null);
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		this.mMindMapController = c;
		this.text = "";
		this.mMindMapController.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		if (mMindMapController.getView().getRoot().isSelected()) {
      throw new RuntimeException(
					Resources.getInstance().getResourceString(
							"cannot_delete_root"));
		}
		Transferable copy = mMindMapController.cut();
		// and set it.
		mMindMapController.setClipboardContents(copy);
    mMindMapController.obtainFocusForSelected();
	}

	public CutNodeAction getCutNodeAction(MindMapNode node) {
		CutNodeAction cutAction = new CutNodeAction();
		cutAction.setNode(mMindMapController.getNodeID(node));
		return cutAction;
	}

	public Transferable cut(List nodeList) {
		mMindMapController.sortNodesByDepth(nodeList);
		Transferable totalCopy = mMindMapController.copy(nodeList, true);
		// Do-action
		CompoundAction doAction = new CompoundAction();
		// Undo-action
		CompoundAction undo = new CompoundAction();
		// sort selectedNodes list by depth, in order to guarantee that sons are
		// deleted first:
		for (Iterator i = nodeList.iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			if (node.getParentNode() == null)
				continue;
			CutNodeAction cutNodeAction = getCutNodeAction(node);
			doAction.addChoice(cutNodeAction);

			NodeCoordinate coord = new NodeCoordinate(node, node.isLeft());
			Transferable copy = mMindMapController.copy(node, true);
			XmlAction pasteNodeAction = mMindMapController.paste
					.getPasteNodeAction(copy, coord, (UndoPasteNodeAction) null);
			// The paste actions are reversed because of the strange
			// coordinates.
			undo.addAtChoice(0, pasteNodeAction);

		}
		if (doAction.sizeChoiceList() > 0) {
			mMindMapController.doTransaction(text,
					new ActionPair(doAction, undo));
		}
		return totalCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		CutNodeAction cutAction = (CutNodeAction) action;
		MindMapNode selectedNode = mMindMapController.getNodeFromID(cutAction
				.getNode());
		mMindMapController.deleteChild.deleteWithoutUndo(selectedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return CutNodeAction.class;
	}

}