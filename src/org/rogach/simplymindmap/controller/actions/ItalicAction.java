/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 25.08.2004
 */

package org.rogach.simplymindmap.controller.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.ItalicNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;

public class ItalicAction extends NodeGeneralAction implements NodeActorXml {
	private final MindMapController modeController;

	/**
	 */
	public ItalicAction(MindMapController modeController) {
		super(modeController, "italic", "org/rogach/simplymindmap/images/Italic16.gif");
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_node_toggle_italic")));
		this.modeController = modeController;
		addActor(this);
	}

	public void act(XmlAction action) {
		ItalicNodeAction italicact = (ItalicNodeAction) action;
		MindMapNode node = getNodeFromID(italicact.getNode());
		if (node.isItalic() != italicact.getItalic()) {
			node.setItalic(italicact.getItalic());
			this.modeController.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return ItalicNodeAction.class;
	}

	public ActionPair apply(AbstractMindMapModel model, MindMapNode selected) {
		// every node is set to the inverse of the focussed node.
		boolean italic = modeController.getSelected().isItalic();
		return getActionPair(selected, !italic);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean italic) {
		ItalicNodeAction italicAction = toggleItalic(selected, italic);
		ItalicNodeAction undoItalicAction = toggleItalic(selected,
				selected.isItalic());
		return new ActionPair(italicAction, undoItalicAction);
	}

	private ItalicNodeAction toggleItalic(MindMapNode selected, boolean italic) {
		ItalicNodeAction italicAction = new ItalicNodeAction();
		italicAction.setNode(getNodeID(selected));
		italicAction.setItalic(italic);
		return italicAction;
	}

	public void setItalic(MindMapNode node, boolean italic) {
		execute(getActionPair(node, italic));
	}

	public boolean isSelected(JMenuItem item, Action action) {
		return modeController.getSelected().isItalic();
	}

}
