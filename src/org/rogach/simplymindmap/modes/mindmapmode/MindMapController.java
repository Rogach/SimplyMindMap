/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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


package org.rogach.simplymindmap.modes.mindmapmode;

import org.rogach.simplymindmap.controller.MindMapNodesSelection;
import org.rogach.simplymindmap.main.ResourceKeys;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.modes.ControllerAdapter;
import org.rogach.simplymindmap.modes.MindIcon;
import org.rogach.simplymindmap.modes.MindMap;
import org.rogach.simplymindmap.modes.MindMapNode;
import org.rogach.simplymindmap.modes.mindmapmode.actions.BoldAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.CompoundActionHandler;
import org.rogach.simplymindmap.modes.mindmapmode.actions.CopyAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.CutAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.DeleteChildAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.EditAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.FindAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.FindAction.FindNextAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.FontFamilyAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.FontSizeAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.IconAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.ItalicAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.MindMapActions;
import org.rogach.simplymindmap.modes.mindmapmode.actions.ModeControllerActionHandler;
import org.rogach.simplymindmap.modes.mindmapmode.actions.MoveNodeAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NewChildAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NewPreviousSiblingAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NewSiblingAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NodeColorAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NodeDownAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NodeGeneralAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.NodeUpAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.PasteAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.RedoAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.RemoveAllIconsAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.RemoveIconAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.SelectAllAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.SelectBranchAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.SingleNodeOperation;
import org.rogach.simplymindmap.modes.mindmapmode.actions.ToggleChildrenFoldedAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.ToggleFoldedAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.UndoAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActionFactory;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.ActionPair;
import org.rogach.simplymindmap.modes.mindmapmode.actions.xml.UndoActionHandler;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MainView;
import org.rogach.simplymindmap.view.NodeView;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.rogach.simplymindmap.modes.mindmapmode.actions.ChangeNodeLevelAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.FitToPageAction;
import org.rogach.simplymindmap.modes.mindmapmode.actions.IconSelectionAction;

public class MindMapController extends ControllerAdapter implements
		MindMapActions {
  
	private static Logger logger;
	// for MouseEventHandlers
	private HashSet mRegisteredMouseWheelEventHandler = new HashSet();

	private ActionFactory actionFactory;
	private Clipboard clipboard = null;
	private Clipboard selection = null;

	public Action editLong = new EditLongAction();
	public Action newSibling = new NewSiblingAction(this);
	public Action newPreviousSibling = new NewPreviousSiblingAction(this);

	public Action showAttributeManagerAction = null;
	public Action propertyAction = null;

	public Action increaseNodeFont = new NodeGeneralAction(this,
			"increase_node_font_size", null, new SingleNodeOperation() {
				public void apply(MindMapMapModel map, MindMapNodeModel node) {
					increaseFontSize(node, 1);
				}
			});
	public Action decreaseNodeFont = new NodeGeneralAction(this,
			"decrease_node_font_size", null, new SingleNodeOperation() {
				public void apply(MindMapMapModel map, MindMapNodeModel node) {
					increaseFontSize(node, -1);
				}
			});

	public UndoAction undo = null;
	public RedoAction redo = null;
	public CopyAction copy = null;
	public Action copySingle = null;
	public CutAction cut = null;
	public PasteAction paste = null;
	public BoldAction bold = null;
	public ItalicAction italic = null;
	public FontSizeAction fontSize = null;
	public FontFamilyAction fontFamily = null;
	public NodeColorAction nodeColor = null;
	public EditAction edit = null;
	public NewChildAction newChild = null;
	public DeleteChildAction deleteChild = null;
	public ToggleFoldedAction toggleFolded = null;
	public ToggleChildrenFoldedAction toggleChildrenFolded = null;
	public NodeUpAction nodeUp = null;
	public NodeDownAction nodeDown = null;
  
  public IconSelectionAction iconSelectionAction = null;
	public IconAction unknownIconAction = null;
	public RemoveIconAction removeLastIconAction = null;
	public RemoveAllIconsAction removeAllIconsAction = null;
	public MoveNodeAction moveNodeAction = null;
	
	public FindAction find = null;
	public FindNextAction findNext = null;
	public SelectBranchAction selectBranchAction = null;
	public SelectAllAction selectAllAction = null;
  
  public ChangeNodeLevelAction changeNodeLevelRight = null;
  public ChangeNodeLevelAction changeNodeLevelLeft = null;
  
  public FitToPageAction fitToPage = null;

	// Extension Actions
	public Vector iconActions = new Vector(); // fc
  
	public MindMapController() {
		super();
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		// create action factory:
		actionFactory = new ActionFactory();
    new CompoundActionHandler(this); // eagerly initialize compound action handler
    logger.info("createIconActions");
    createStandardActions();
    createIconActions();
    logger.info("createNodeHookActions");
	}

	private void createStandardActions() {
		// prepare undo:
		undo = new UndoAction(this);
		redo = new RedoAction(this);
		// register default action handler:
		// the executor must be the first here, because it is executed last
		// then.
		getActionFactory().registerHandler(
				new ModeControllerActionHandler(getActionFactory()));
		getActionFactory().registerUndoHandler(
				new UndoActionHandler(this, undo, redo));
		cut = new CutAction(this);
		paste = new PasteAction(this);
		copy = new CopyAction(this);
		bold = new BoldAction(this);
		italic = new ItalicAction(this);
		fontSize = new FontSizeAction(this);
		fontFamily = new FontFamilyAction(this);
		edit = new EditAction(this);
		newChild = new NewChildAction(this);
		deleteChild = new DeleteChildAction(this);
		toggleFolded = new ToggleFoldedAction(this);
		toggleChildrenFolded = new ToggleChildrenFoldedAction(this);
		nodeUp = new NodeUpAction(this);
		nodeDown = new NodeDownAction(this);
		nodeColor = new NodeColorAction(this);
    
    iconSelectionAction = new IconSelectionAction(this);
		// this is an unknown icon and thus corrected by mindicon:
		removeLastIconAction = new RemoveIconAction(this);
		// this action handles the xml stuff: (undo etc.)
		unknownIconAction = new IconAction(this,
				MindIcon.factory((String) MindIcon.getAllIconNames().get(0)),
				removeLastIconAction);
		removeLastIconAction.setIconAction(unknownIconAction);
		removeAllIconsAction = new RemoveAllIconsAction(this, unknownIconAction);
		moveNodeAction = new MoveNodeAction(this);
		find = new FindAction(this);
		findNext = new FindNextAction(this, find);
		selectBranchAction = new SelectBranchAction(this);
		selectAllAction = new SelectAllAction(this);

    changeNodeLevelLeft = new ChangeNodeLevelAction("left", this);
    changeNodeLevelRight = new ChangeNodeLevelAction("right", this);
    
    fitToPage = new FitToPageAction(this);
	}

	public boolean isUndoAction() {
		return undo.isUndoAction() || redo.isUndoAction();
	}

	private void createIconActions() {
		Vector iconNames = MindIcon.getAllIconNames();
		for (int i = 0; i < iconNames.size(); ++i) {
			String iconName = ((String) iconNames.get(i));
			MindIcon myIcon = MindIcon.factory(iconName);
			IconAction myAction = new IconAction(this, myIcon,
					removeLastIconAction);
			iconActions.add(myAction);
		}
	}

	public void nodeChanged(MindMapNode n) {
		super.nodeChanged(n);
	}

	// fc, 14.12.2004: changes, such that different models can be used:
	private NewNodeCreator myNewNodeCreator = null;
  
	public interface NewNodeCreator {
		MindMapNode createNode(Object userObject, MindMap map);
	}

	public class DefaultMindMapNodeCreator implements NewNodeCreator {

		public MindMapNode createNode(Object userObject, MindMap map) {
			return new MindMapNodeModel(userObject, map);
		}

	}

	public void setNewNodeCreator(NewNodeCreator creator) {
		myNewNodeCreator = creator;
	}

	public MindMapNode newNode(Object userObject, MindMap map) {
		// singleton default:
		if (myNewNodeCreator == null) {
			myNewNodeCreator = new DefaultMindMapNodeCreator();
		}
		return myNewNodeCreator.createNode(userObject, map);
	}

	// convenience methods
	public MindMapMapModel getMindMapMapModel() {
		return (MindMapMapModel) getMap();
	}

	public void setBold(MindMapNode node, boolean bolded) {
		bold.setBold(node, bolded);
	}

	public void setItalic(MindMapNode node, boolean isItalic) {
		italic.setItalic(node, isItalic);
	}

	// Node editing
	public void setFontSize(MindMapNode node, String fontSizeValue) {
		fontSize.setFontSize(node, fontSizeValue);
	}

	/**
     *
     */

	public void increaseFontSize(MindMapNode node, int increment) {
		int newSize = Integer.valueOf(node.getFontSize()).intValue()
				+ increment;

		if (newSize > 0) {
			setFontSize(node, Integer.toString(newSize));
		}
	}

	public void setFontFamily(MindMapNode node, String fontFamilyValue) {
		fontFamily.setFontFamily(node, fontFamilyValue);
	}

	public void setNodeColor(MindMapNode node, Color color) {
		nodeColor.setNodeColor(node, color);
	}

	public void addIcon(MindMapNode node, MindIcon icon) {
		unknownIconAction.addIcon(node, icon);
	}

	public void removeAllIcons(MindMapNode node) {
		removeAllIconsAction.removeAllIcons(node);
	}

	public int removeLastIcon(MindMapNode node) {
		return removeLastIconAction.removeLastIcon(node);
	}

	// edit begins with home/end or typing (PN 6.2)
	public void edit(KeyEvent e, boolean addNew, boolean editLong) {
		edit.edit(e, addNew, editLong);
	}

	public void setNodeText(MindMapNode selected, String newText) {
		edit.setNodeText(selected, newText);
	}

	public Transferable copy(MindMapNode node, boolean saveInvisible) {
		StringWriter stringWriter = new StringWriter();
		try {
			((MindMapNodeModel) node).save(stringWriter, getMap()
					.getLinkRegistry(), saveInvisible, true);
		} catch (IOException e) {
		}
		Vector nodeList = Tools.getVectorWithSingleElement(getNodeID(node));
		return new MindMapNodesSelection(stringWriter.toString(),
				null, null, nodeList);
	}

	public Transferable cut() {
		return cut(getView().getSelectedNodesSortedByY());
	}

	public Transferable cut(List nodeList) {
		return cut.cut(nodeList);
	}

	public void paste(Transferable t, MindMapNode parent) {
		paste(t, /* target= */parent, /* asSibling= */false,
				parent.isNewChildLeft());
	}

	/* (non-Javadoc)
	 * @see freemind.modes.mindmapmode.actions.MindMapActions#paste(java.awt.datatransfer.Transferable, freemind.modes.MindMapNode, boolean, boolean)
	 */
	public boolean paste(Transferable t, MindMapNode target, boolean asSibling,
			boolean isLeft) {
		if (!asSibling
				&& target.isFolded()
				&& Resources.getInstance().getBoolProperty(
						ResourceKeys.RESOURCE_UNFOLD_ON_PASTE)) {
			setFolded(target, false);
		}
		return paste.paste(t, target, asSibling, isLeft);
	}

	public void paste(MindMapNode node, MindMapNode parent) {
		paste.paste(node, parent);
	}

	public MindMapNode addNew(final MindMapNode target, final int newNodeMode,
			final KeyEvent e) {
		edit.stopEditing();
		return newChild.addNew(target, newNodeMode, e);
	}

	public MindMapNode addNewNode(MindMapNode parent, int index,
			boolean newNodeIsLeft) {
		return newChild.addNewNode(parent, index, newNodeIsLeft);
	}

	public void deleteNode(MindMapNode selectedNode) {
		deleteChild.deleteNode(selectedNode);
	}

	public void toggleFolded() {
		toggleFolded.toggleFolded();
	}

	public void setFolded(MindMapNode node, boolean folded) {
		toggleFolded.setFolded(node, folded);
	}

	public void moveNodes(MindMapNode selected, List selecteds, int direction) {
		nodeUp.moveNodes(selected, selecteds, direction);
	}

	public void moveNodePosition(MindMapNode node, int parentVGap, int hGap,
			int shiftY) {
		moveNodeAction.moveNodeTo(node, parentVGap, hGap, shiftY);
	}

	// ///////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////

	public void plainClick(MouseEvent e) {
		/* perform action only if one selected node. */
		if (getSelecteds().size() != 1)
			return;
		final MainView component = (MainView) e.getComponent();
    MindMapNode node = (component).getNodeView().getModel();
    if (!node.hasChildren()) {
      // then emulate the plain click.
      doubleClick(e);
      return;
    }
    toggleFolded();
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	protected class EditLongAction extends AbstractAction {
		public EditLongAction() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			edit(null, false, true);
		}
	}

	public void doubleClick(MouseEvent e) {
		/* perform action only if one selected node. */
		if (getSelecteds().size() != 1)
			return;
		// edit the node only if the node is a leaf (fc 0.7.1), or the root node
		// (fc 0.9.0)
		if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown()
				&& !e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {
			edit(null, false, false);
		}
	}

	public boolean extendSelection(MouseEvent e) {
		NodeView newlySelectedNodeView = ((MainView) e.getComponent())
				.getNodeView();
		// MindMapNode newlySelectedNode = newlySelectedNodeView.getModel();
		boolean extend = e.isControlDown();
		// Fixes Cannot select multiple single nodes *
		// https://sourceforge.net/tracker/?func=detail&atid=107118&aid=1675829&group_id=7118
		if (Tools.isMacOsX()) {
			extend |= e.isMetaDown();
		}
		boolean range = e.isShiftDown();
		boolean branch = e.isAltGraphDown() || e.isAltDown(); /*
															 * windows alt,
															 * linux altgraph
															 * ....
															 */
		boolean retValue = false;

		if (extend || range || branch
				|| !getView().isSelected(newlySelectedNodeView)) {
			if (!range) {
				if (extend)
					getView().toggleSelected(newlySelectedNodeView);
				else
					select(newlySelectedNodeView);
				retValue = true;
			} else {
				retValue = getView().selectContinuous(newlySelectedNodeView);
				// /* fc, 25.1.2004: replace getView by controller methods.*/
				// if (newlySelectedNodeView != getView().getSelected() &&
				// newlySelectedNodeView.isSiblingOf(getView().getSelected())) {
				// getView().selectContinuous(newlySelectedNodeView);
				// retValue = true;
				// } else {
				// /* if shift was down, but no range can be selected, then the
				// new node is simply selected: */
				// if(!getView().isSelected(newlySelectedNodeView)) {
				// getView().toggleSelected(newlySelectedNodeView);
				// retValue = true;
				// }
			}
			if (branch) {
				getView().selectBranch(newlySelectedNodeView, extend);
				retValue = true;
			}
		}

		if (retValue) {
			e.consume();
		}
		logger.fine("MouseEvent: extend:" + extend + ", range:" + range
				+ ", branch:" + branch + ", event:" + e + ", retValue:"
				+ retValue);
    getView().requestFocusInWindow();
		return retValue;
	}

	public void registerMouseWheelEventHandler(MouseWheelEventHandler handler) {
		logger.fine("Registered   MouseWheelEventHandler " + handler);
		mRegisteredMouseWheelEventHandler.add(handler);
	}

	public void deRegisterMouseWheelEventHandler(MouseWheelEventHandler handler) {
		logger.fine("Deregistered MouseWheelEventHandler " + handler);
		mRegisteredMouseWheelEventHandler.remove(handler);
	}

	public Set getRegisteredMouseWheelEventHandler() {
		return Collections.unmodifiableSet(mRegisteredMouseWheelEventHandler);

	}

	public XMLElement createXMLElement() {
		return new MindMapXMLElement(this);
	}

	public void insertNodeInto(MindMapNode newNode, MindMapNode parent,
			int index) {
		super.insertNodeInto(newNode, parent, index);
	}

	public void removeNodeFromParent(MindMapNode selectedNode) {
		// first deselect, and then remove. 
		NodeView nodeView = getView().getNodeView(selectedNode);
		getView().deselect(nodeView);
		getModel().removeNodeFromParent(selectedNode);
	}

	public void repaintMap() {
		getView().repaint();
	}

	public Transferable getClipboardContents() {
		getClipboard();
		return clipboard.getContents(this);
	}

	protected void getClipboard() {
		if (clipboard == null) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			selection = toolkit.getSystemSelection();
			clipboard = toolkit.getSystemClipboard();

		}
	}

	/**
	 */
	public void setClipboardContents(Transferable t) {
		getClipboard();
		clipboard.setContents(t, null);
		if (selection != null) {
			selection.setContents(t, null);
		}
	}

	/**
	 * Delegate method to Controller. Must be called after cut.s
	 */
	public void obtainFocusForSelected() {
    getView().requestFocusInWindow();
	}

	public boolean doTransaction(String pName, ActionPair pPair) {
		return actionFactory.doTransaction(pName, pPair);
	}

}
