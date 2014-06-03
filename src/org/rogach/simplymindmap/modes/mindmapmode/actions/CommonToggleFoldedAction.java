/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 09.11.2005
 */

package org.rogach.simplymindmap.modes.mindmapmode.actions;

import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.modes.MindMapNode;
import org.rogach.simplymindmap.util.Tools;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.rogach.simplymindmap.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * 
 */
public class CommonToggleFoldedAction extends AbstractAction {

	private final MindMapController modeController;

	private Logger logger;

	public CommonToggleFoldedAction(MindMapController controller) {
		super(Resources.getInstance().getText("toggle_folded"));
		this.modeController = controller;
		logger = Logger.getLogger(this.getClass().getName());
	}

	public void actionPerformed(ActionEvent e) {
		toggleFolded();
	}

	public void toggleFolded() {
		toggleFolded(modeController.getSelecteds().listIterator());
	}

	public void toggleFolded(ListIterator listIterator) {
		boolean fold = getFoldingState(reset(listIterator));
		for (Iterator i = reset(listIterator); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			modeController.setFolded(node, fold);
		}
	}

	public static ListIterator reset(ListIterator iterator) {
		while (iterator.hasPrevious()) {
			iterator.previous();
		}
		return iterator;
	}

	/**
	 * Determines whether the nodes should be folded or unfolded depending on
	 * their states. If not all nodes have the same folding status, the result
	 * means folding
	 * 
	 * @param iterator
	 *            an iterator of MindMapNodes.
	 * @return true, if the nodes should be folded.
	 */
	public static boolean getFoldingState(ListIterator iterator) {
		/*
		 * Retrieve the information whether or not all nodes have the same
		 * folding state.
		 */
		Tools.BooleanHolder state = null;
		boolean allNodeHaveSameFoldedStatus = true;
		for (ListIterator it = iterator; it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			if (node.getChildCount() == 0) {
				// no folding state change for unfoldable nodes.
				continue;
			}
			if (state == null) {
				state = new Tools.BooleanHolder();
				state.setValue(node.isFolded());
			} else {
				if (node.isFolded() != state.getValue()) {
					allNodeHaveSameFoldedStatus = false;
					break;
				}
			}
		}
		/* if the folding state is ambiguous, the nodes are folded. */
		boolean fold = true;
		if (allNodeHaveSameFoldedStatus && state != null) {
			fold = !state.getValue();
		}
		return fold;
	}

}
