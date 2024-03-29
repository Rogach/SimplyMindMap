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
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.instance.EditNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.view.EditNodeBase;
import org.rogach.simplymindmap.view.EditNodeDialog;
import org.rogach.simplymindmap.view.EditNodeTextField;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeView;

//
//Node editing
//

public class EditAction extends AbstractAction implements ActorXml {
	private static final Pattern HTML_HEAD = Pattern.compile(
			"\\s*<head>.*</head>", Pattern.DOTALL);
	private final MindMapController controller;
	private EditNodeBase mCurrentEditDialog = null;

	public EditAction(MindMapController modeController) {
		super(modeController.getResources().getText("edit_node"), modeController.getResources().getIcon("Btn_edit.gif"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(modeController.getResources().unsafeGetProperty("keystroke_edit")));
		this.controller = modeController;
		this.controller.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent arg0) {
		MindMapNode selected = this.controller.getSelected();
		this.controller.edit(null, false, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		EditNodeAction editAction = (EditNodeAction) action;
		MindMapNode node = this.controller.getNodeFromID(editAction
				.getNode());
		if (!node.toString().equals(editAction.getText())) {
			node.setUserObject(editAction.getText());
			this.controller.nodeChanged(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EditNodeAction.class;
	}

	// edit begins with home/end or typing (PN 6.2)
	public void edit(KeyEvent e, boolean addNew, boolean editLong) {
		NodeView selectedNodeView = controller.getView().getSelected();
		if (selectedNodeView != null) {
			if (e == null || !addNew) {
				edit(selectedNodeView, selectedNodeView, e, false, false,
						editLong);
			} else if (!controller.isBlocked()) {
				controller.addNew(controller.getSelected(),
						MindMapController.NEW_SIBLING_BEHIND, e);
			}
			if (e != null) {
				e.consume();
			}
		}
	}

	public void edit(final NodeView node, final NodeView prevSelected,
			final KeyEvent firstEvent, final boolean isNewNode,
			final boolean parentFolded, final boolean editLong) {
		if (node == null) {
			return;
		}
		final MapView map = node.getMap();
		map.validate();
		map.invalidate();

		stopEditing();
		// EditNodeBase.closeEdit();
		controller.setBlocked(true); // locally "modal" stated

		String text = node.getModel().toString();

		boolean isLongNode = node.getIsLong();

		if (isLongNode || editLong) {
			EditNodeDialog nodeEditDialog = new EditNodeDialog(node, text,
					firstEvent, controller,
					new EditNodeBase.EditControl() {

						public void cancel() {
							controller.setBlocked(false);
							mCurrentEditDialog = null;
              controller.obtainFocusForSelected();
						}

						public void ok(String newText) {
							setNodeText(node.getModel(), newText);
							cancel();
						}

					});
			mCurrentEditDialog = nodeEditDialog;
			nodeEditDialog.show();
			return;
		}
		// inline editing:
		EditNodeTextField textfield = new EditNodeTextField(node, text,
				firstEvent, controller, new EditNodeBase.EditControl() {

					public void cancel() {
						if (isNewNode) { // delete also the node and set focus
											// to the parent
							controller.getView()
									.selectAsTheOnlyOneSelected(node);
							Vector<MindMapNode> nodeList = new Vector<>();
							nodeList.add(node.getModel());
							controller.cut(nodeList);
							controller.select(prevSelected);
							// include max level for navigation
							if (parentFolded) {
								controller.setFolded(
										prevSelected.getModel(), true);
							}
						}
						endEdit();
					}

					public void ok(String newText) {
						setNodeText(node.getModel(), newText);
						endEdit();
					}

					private void endEdit() {
						controller.obtainFocusForSelected();
						controller.setBlocked(false);
						mCurrentEditDialog = null;
					}

				});
		mCurrentEditDialog = textfield;
		textfield.show();

	}

	public void setNodeText(MindMapNode selected, String newText) {
		String oldText = selected.toString();

		EditNodeAction EditAction = new EditNodeAction();
		EditAction.setNode(controller.getNodeID(selected));
		EditAction.setText(newText);

		EditNodeAction undoEditAction = new EditNodeAction();
		undoEditAction.setNode(controller.getNodeID(selected));
		undoEditAction.setText(oldText);

		controller.doTransaction(
				controller.getResources().getText("edit_node"),
				new ActionPair(EditAction, undoEditAction));
	}

	protected MindMapController getMindMapController() {
		return controller;
	}

	private void setHtmlText(final NodeView node, String newText) {
		final String body = HTML_HEAD.matcher(newText).replaceFirst("");
		setNodeText(node.getModel(), body);
	}

	public void stopEditing() {
		if (mCurrentEditDialog != null) {
			// there was previous editing.
			mCurrentEditDialog.closeEdit();
			mCurrentEditDialog = null;
		}
	}

}
