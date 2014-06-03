/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.rogach.simplymindmap.controller.actions.instance.CompoundAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.MindMapMapModel;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.controller.actions.xml.AbstractXmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.util.Tools;

public class NodeGeneralAction extends AbstractXmlAction {
	protected final MindMapController modeController;

	private org.rogach.simplymindmap.controller.actions.NodeActorXml actor;

	SingleNodeOperation singleNodeOperation;

	protected static Logger logger;
  
	/**
	 * null if you cannot provide a title that is present in the resources. Use
	 * the setName method to set your not translateble title after that. give a
	 * resource name for the icon.
	 */
	protected NodeGeneralAction(MindMapController modeController,
			final String textID, String iconPath) {
		super(null, iconPath == null ? null : new ImageIcon(Resources.getInstance().getResource(iconPath)), modeController);
		this.modeController = modeController;
		if (textID != null) {
			setName("");
		}

		this.singleNodeOperation = null;
		this.actor = null;
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	protected void setName(String name) {
		if (name != null) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, Tools.removeMnemonic(name));
		}

	}

	public NodeGeneralAction(MindMapController modeController, String textID,
			String iconPath, SingleNodeOperation singleNodeOperation) {
		this(modeController, textID, iconPath);
		this.singleNodeOperation = singleNodeOperation;
	}

	public NodeGeneralAction(MindMapController modeController, String textID,
			String iconPath,
			org.rogach.simplymindmap.controller.actions.NodeActorXml actor) {
		this(modeController, textID, iconPath);
		addActor(actor);
	}

	public void addActor(NodeActorXml actor) {
		this.actor = actor;
		if (actor != null) {
			super.addActor(actor);
		}
	}

	/**
	 * The singleNodeOperation to set.
	 */
	public void setSingleNodeOperation(SingleNodeOperation singleNodeOperation) {
		this.singleNodeOperation = singleNodeOperation;
	}

	public void xmlActionPerformed(ActionEvent e) {
		if (singleNodeOperation != null) {
			for (ListIterator it = modeController.getSelecteds().listIterator(); it
					.hasNext();) {
				MindMapNode selected = (MindMapNode) it.next();
				singleNodeOperation.apply(
						(MindMapMapModel) this.modeController.getMap(),
						selected);
			}
		} else {
			// xml action:
			// Do-action
			CompoundAction doAction = new CompoundAction();
			// Undo-action
			CompoundAction undo = new CompoundAction();
			// sort selectedNodes list by depth, in order to guarantee that
			// sons are deleted first:
			for (ListIterator it = modeController.getSelecteds().listIterator(); it
					.hasNext();) {
				MindMapNode selected = (MindMapNode) it.next();
				ActionPair pair = actor.apply(this.modeController.getMap(),
						selected);
				if (pair != null) {
					doAction.addChoice(pair.getDoAction());
					undo.addAtChoice(0, pair.getUndoAction());
				}
			}
			if (doAction.sizeChoiceList() == 0)
				return;
			modeController.doTransaction((String) getValue(NAME),
					new ActionPair(doAction, undo));
		}

	}

	protected void execute(ActionPair pair) {
		modeController.doTransaction(getShortDescription(), pair);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.FreeMindAction#act(freemind.controller.actions
	 * .generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
	}

	/**
     */
	protected MindMapNode getNodeFromID(String string) {
		return modeController.getNodeFromID(string);
	}

	/**
     */
	protected String getNodeID(MindMapNode selected) {
		// TODO Auto-generated method stub
		return modeController.getNodeID(selected);
	}

}
