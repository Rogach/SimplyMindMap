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
 * Created on 29.09.2004
 */


package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ListIterator;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.AddIconAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.IconInformation;
import org.rogach.simplymindmap.model.MindIcon;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;

public class IconAction extends FreemindAction implements ActorXml,
		IconInformation {
	public MindIcon icon;
	private final MindMapController controller;
	private final RemoveIconAction removeLastIconAction;

	public IconAction(MindMapController controller, MindIcon _icon,
			RemoveIconAction removeLastIconAction) {
		super(_icon.getDescription(), _icon.getIcon(), controller);
		this.controller = controller;
		this.removeLastIconAction = removeLastIconAction;
		putValue(Action.SHORT_DESCRIPTION, _icon.getDescription());
		this.icon = _icon;
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getID() == ActionEvent.ACTION_FIRST
				&& (e.getModifiers() & ActionEvent.SHIFT_MASK
						& ~ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeAllIcons();
			addLastIcon();
			return;
		}
		if (e == null
				|| (e.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK)) == 0) {
			addLastIcon();
			return;
		}
		// e != null
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK
				& ~ActionEvent.CTRL_MASK & ActionEvent.ALT_MASK) != 0) {
			removeIcon(false);
			return;
		}
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK & ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeIcon(true);
			return;
		}
	}

	private void addLastIcon() {
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNode selected = (MindMapNode) it.next();
			addIcon(selected, icon);
		}
	}

	private void removeIcon(boolean removeFirst) {
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNode selected = (MindMapNode) it.next();
			removeIcon(selected, icon, removeFirst);
		}
	}

	private void removeAllIcons() {
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNode selected = (MindMapNode) it.next();
			if (selected.getIcons().size() > 0) {
				controller.removeAllIcons(selected);
			}
		}
	}

	public void addIcon(MindMapNode node, MindIcon icon) {
		controller.doTransaction(
				(String) getValue(NAME), getAddLastIconActionPair(node, icon));
	}

	private void removeIcon(MindMapNode node, MindIcon icon, boolean removeFirst) {
		final ActionPair removeIconActionPair = getRemoveIconActionPair(node,
				icon, removeFirst);
		if (removeIconActionPair == null) {
			return;
		}
		controller.doTransaction(
				(String) getValue(NAME), removeIconActionPair);
	}

	/**
     */
	private ActionPair getAddLastIconActionPair(MindMapNode node, MindIcon icon) {
		int iconIndex = MindIcon.LAST;
		return getAddIconActionPair(node, icon, iconIndex);
	}

	private ActionPair getAddIconActionPair(MindMapNode node, MindIcon icon,
			int iconIndex) {
		AddIconAction doAction = createAddIconAction(node, icon, iconIndex);
		XmlAction undoAction = removeLastIconAction.createRemoveIconXmlAction(
				node, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	/**
	 * @param removeFirst
	 */
	private ActionPair getRemoveIconActionPair(MindMapNode node, MindIcon icon,
			boolean removeFirst) {
		int iconIndex = removeFirst ? iconFirstIndex(
				node, icon.getName()) : iconLastIndex(
				node, icon.getName());
		return iconIndex >= 0 ? getRemoveIconActionPair(node, icon, iconIndex)
				: null;
	}
  
 	static public int iconFirstIndex(MindMapNode node, String iconName) {
		List icons = node.getIcons();
		for (ListIterator i = icons.listIterator(); i.hasNext();) {
			MindIcon nextIcon = (MindIcon) i.next();
			if (iconName.equals(nextIcon.getName()))
				return i.previousIndex();
		}
		return -1;

	}

	static public int iconLastIndex(MindMapNode node, String iconName) {
		List icons = node.getIcons();
		ListIterator i = icons.listIterator(icons.size());
		while (i.hasPrevious()) {
			MindIcon nextIcon = (MindIcon) i.previous();
			if (iconName.equals(nextIcon.getName()))
				return i.nextIndex();
		}
		return -1;

	}


	private ActionPair getRemoveIconActionPair(MindMapNode node, MindIcon icon,
			int iconIndex) {
		XmlAction doAction = removeLastIconAction.createRemoveIconXmlAction(
				node, iconIndex);
		XmlAction undoAction = createAddIconAction(node, icon, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	public void act(XmlAction action) {
		if (action instanceof AddIconAction) {
			AddIconAction iconAction = (AddIconAction) action;
			MindMapNode node = controller.getNodeFromID(iconAction
					.getNode());
			String iconName = iconAction.getIconName();
			int position = iconAction.getIconPosition();
			MindIcon icon = MindIcon.factory(iconName, controller.getResources());
			node.addIcon(icon, position);
			controller.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddIconAction.class;
	}

	public AddIconAction createAddIconAction(MindMapNode node, MindIcon icon,
			int iconPosition) {
		AddIconAction action = new AddIconAction();
		action.setNode(node.getObjectId(controller));
		action.setIconName(icon.getName());
		action.setIconPosition(iconPosition);
		return action;
	}

	public MindIcon getMindIcon() {
		return icon;
	}

	public KeyStroke getKeyStroke() {
		final String keystrokeResourceName = icon.getKeystrokeResourceName();
		final String keyStrokeDescription = controller.getResources().unsafeGetProperty(keystrokeResourceName);
		return Tools.getKeyStroke(keyStrokeDescription);
	}

	public String getDescription() {
		return icon.getDescription();
	}

	public ImageIcon getIcon() {
		return icon.getIcon();
	}

	public String getKeystrokeResourceName() {
		return icon.getKeystrokeResourceName();
	}

}
