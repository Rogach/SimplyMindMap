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

package org.rogach.simplymindmap.modes;

import org.rogach.simplymindmap.controller.MindMapNodesSelection;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.nanoxml.XMLParseException;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeView;
import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Derive from this class to implement the Controller for your mode. Overload
 * the methods you need for your data model, or use the defaults. There are some
 * default Actions you may want to use for easy editing of your model. Take
 * MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

	// Logging:
	private static java.util.logging.Logger logger;

	private Color selectionColor = new Color(200, 220, 200);
	/**
	 * The model, this controller belongs to. It may be null, if it is the
	 * default controller that does not show a map.
	 */
	private MapAdapter mModel;
	private HashSet mNodeSelectionListeners = new HashSet();
	private HashSet mNodeLifetimeListeners = new HashSet();

	/**
	 * Instantiation order: first me and then the model.
	 */
	public ControllerAdapter() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	public void setModel(MapAdapter model) {
		mModel = model;
	}

	//
	// Methods that should be overloaded
	//

	public abstract MindMapNode newNode(Object userObject, MindMap map);

	public abstract XMLElement createXMLElement();

	/**
	 * You _must_ implement this if you use one of the following actions:
	 * OpenAction, NewMapAction.
	 * 
	 * @param modeController
	 *            TODO
	 */
	public MapAdapter newModel(ModeController modeController) {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * Currently, this method is called by the mapAdapter. This is buggy, and is
	 * to be changed.
	 */
	public void nodeChanged(MindMapNode node) {
		nodeRefresh(node, true);
	}

	public void nodeRefresh(MindMapNode node) {
		nodeRefresh(node, false);
	}

	private void nodeRefresh(MindMapNode node, boolean isUpdate) {
		logger.finest("nodeChanged called for node " + node + " parent="
				+ node.getParentNode());
		if (isUpdate) {
			// Tell any node hooks that the node is changed:
			updateNode(node);
		}
		// fc, 10.10.06: Dirty hack in order to keep this method away from being
		// used by everybody.
		((MapAdapter) getMap()).nodeChangedInternal(node);
	}

	public void refreshMap() {
		final MindMapNode root = getMap().getRootNode();
		refreshMapFrom(root);
	}

	public void refreshMapFrom(MindMapNode node) {
		final Iterator iterator = node.getChildren().iterator();
		while (iterator.hasNext()) {
			MindMapNode child = (MindMapNode) iterator.next();
			refreshMapFrom(child);
		}
		((MapAdapter) getMap()).nodeChangedInternal(node);

	}

	/**
	 */
	public void nodeStructureChanged(MindMapNode node) {
		getMap().nodeStructureChanged(node);
	}

	/**
	 * Overwrite this method to perform additional operations to an node update.
	 */
	protected void updateNode(MindMapNode node) {
		for (Iterator iter = mNodeSelectionListeners.iterator(); iter.hasNext();) {
			NodeSelectionListener listener = (NodeSelectionListener) iter
					.next();
			listener.onUpdateNodeHook(node);
		}
	}

	public void onLostFocusNode(NodeView node) {
		try {
			// deselect the old node:
			HashSet copy = new HashSet(mNodeSelectionListeners);
			// we copied the set to be able to remove listeners during a
			// listener method.
			for (Iterator iter = copy.iterator(); iter.hasNext();) {
				NodeSelectionListener listener = (NodeSelectionListener) iter
						.next();
				listener.onLostFocusNode(node);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void onFocusNode(NodeView node) {
		try {
			// select the new node:
			HashSet copy = new HashSet(mNodeSelectionListeners);
			// we copied the set to be able to remove listeners during a
			// listener method.
			for (Iterator iter = copy.iterator(); iter.hasNext();) {
				NodeSelectionListener listener = (NodeSelectionListener) iter
						.next();
				listener.onFocusNode(node);
      }
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void changeSelection(NodeView pNode, boolean pIsSelected) {
		try {
			HashSet copy = new HashSet(mNodeSelectionListeners);
			for (Iterator iter = copy.iterator(); iter.hasNext();) {
				NodeSelectionListener listener = (NodeSelectionListener) iter
						.next();
				listener.onSelectionChange(pNode, pIsSelected);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error in node selection listeners", e);
		}

	}

	public void registerNodeSelectionListener(NodeSelectionListener listener,
			boolean pCallWithCurrentSelection) {
		mNodeSelectionListeners.add(listener);
		if (pCallWithCurrentSelection) {
			try {
				listener.onFocusNode(getSelectedView());
			} catch (Exception e) {
				org.rogach.simplymindmap.main.Resources.getInstance().logException(e);
			}
			for (Iterator it = getView().getSelecteds().iterator(); it
					.hasNext();) {
				NodeView view = (NodeView) it.next();
				try {
					listener.onSelectionChange(view, true);
				} catch (Exception e) {
					org.rogach.simplymindmap.main.Resources.getInstance().logException(e);
				}
			}
		}
	}

	public void deregisterNodeSelectionListener(NodeSelectionListener listener) {
		mNodeSelectionListeners.remove(listener);
	}

	public void registerNodeLifetimeListener(NodeLifetimeListener listener, boolean pFireCreateEvent) {
		mNodeLifetimeListeners.add(listener);
		if (pFireCreateEvent) {
			// call create node for all:
			// TODO: fc, 10.2.08: this event goes to all listeners. It should be for
			// the new listener only?
			fireRecursiveNodeCreateEvent(getRootNode());
		}
	}

	public void deregisterNodeLifetimeListener(NodeLifetimeListener listener) {
		mNodeLifetimeListeners.remove(listener);
	}

	public HashSet getNodeLifetimeListeners() {
		return mNodeLifetimeListeners;
	}

	public void fireNodePreDeleteEvent(MindMapNode node) {
		// call lifetime listeners:
		for (Iterator iter = mNodeLifetimeListeners.iterator(); iter.hasNext();) {
			NodeLifetimeListener listener = (NodeLifetimeListener) iter.next();
			listener.onPreDeleteNode(node);
		}
	}

	public void fireNodePostDeleteEvent(MindMapNode node, MindMapNode parent) {
		// call lifetime listeners:
		for (Iterator iter = mNodeLifetimeListeners.iterator(); iter.hasNext();) {
			NodeLifetimeListener listener = (NodeLifetimeListener) iter.next();
			listener.onPostDeleteNode(node, parent);
		}
	}

	public void fireRecursiveNodeCreateEvent(MindMapNode node) {
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			NodeAdapter child = (NodeAdapter) i.next();
			fireRecursiveNodeCreateEvent(child);
		}
		// call lifetime listeners:
		for (Iterator iter = mNodeLifetimeListeners.iterator(); iter.hasNext();) {
			NodeLifetimeListener listener = (NodeLifetimeListener) iter.next();
			listener.onCreateNodeHook(node);
		}
	}

	public void firePreSaveEvent(MindMapNode node) {
		// copy to prevent concurrent modification.
		HashSet listenerCopy = new HashSet(mNodeSelectionListeners);
		for (Iterator iter = listenerCopy.iterator(); iter.hasNext();) {
			NodeSelectionListener listener = (NodeSelectionListener) iter
					.next();
			listener.onSaveNode(node);
		}
	}

	public MindMapNode createNodeTreeFromXml(Reader pReader, HashMap pIDToTarget)
			throws XMLParseException, IOException {
		XMLElementAdapter element = (XMLElementAdapter) createXMLElement();
		element.setIDToTarget(pIDToTarget);
		element.parseFromReader(pReader);
		element.processUnfinishedLinks(getModel().getLinkRegistry());
		MindMapNode node = element.getMapChild();
		return node;
	}

	/**
	 * fc, 24.1.2004: having two methods getSelecteds with different return
	 * values (linkedlists of models resp. views) is asking for trouble. @see
	 * MapView
	 * 
	 * @return returns a list of MindMapNode s.
	 */
	public List getSelecteds() {
		LinkedList selecteds = new LinkedList();
		ListIterator it = getView().getSelecteds().listIterator();
		if (it != null) {
			while (it.hasNext()) {
				NodeView selected = (NodeView) it.next();
				selecteds.add(selected.getModel());
			}
		}
		return selecteds;
	}

	public void select(NodeView node) {
		if (node == null) {
			logger.warning("Select with null NodeView called!");
			return;
		}
		getView().scrollNodeToVisible(node);
		getView().selectAsTheOnlyOneSelected(node);
		// this level is default
		getView().setSiblingMaxLevel(node.getModel().getNodeLevel());
	}

	public void select(MindMapNode primarySelected, List selecteds) {
		// are they visible?
		for (Iterator i = selecteds.iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) (i.next());
			displayNode(node);
		}
		final NodeView focussedNodeView = getNodeView(primarySelected);
		if (focussedNodeView != null) {
			getView().selectAsTheOnlyOneSelected(focussedNodeView);
			getView().scrollNodeToVisible(focussedNodeView);
			for (Iterator i = selecteds.iterator(); i.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();
				NodeView nodeView = getNodeView(node);
				if (nodeView != null) {
					getView().makeTheSelected(nodeView);
				}
			}
		}
		obtainFocusForSelected();
	}

	public void selectBranch(NodeView selected, boolean extend) {
		displayNode(selected.getModel());
		getView().selectBranch(selected, extend);
	}

  public abstract void obtainFocusForSelected();

	/**
	 * This class sortes nodes by ascending depth of their paths to root. This
	 * is useful to assure that children are cutted <b>before </b> their
	 * fathers!!!.
	 * 
	 * Moreover, it sorts nodes with the same depth according to their position
	 * relative to each other.
	 */
	protected class nodesDepthComparator implements Comparator {
		public nodesDepthComparator() {
		}

		/* the < relation. */
		public int compare(Object p1, Object p2) {
			MindMapNode n1 = ((MindMapNode) p1);
			MindMapNode n2 = ((MindMapNode) p2);
			Object[] path1 = getModel().getPathToRoot(n1);
			Object[] path2 = getModel().getPathToRoot(n2);
			int depth = path1.length - path2.length;
			if (depth > 0)
				return -1;
			if (depth < 0)
				return 1;
			if (n1.isRoot()) // if n1 is root, n2 is root, too ;)
				return 0;
			return n1.getParentNode().getChildPosition(n1)
					- n2.getParentNode().getChildPosition(n2);
		}
	}

	public List getSelectedsByDepth() {
		// return an ArrayList of MindMapNodes.
		List result = getSelecteds();
		sortNodesByDepth(result);
		return result;
	}

	public void sortNodesByDepth(List inPlaceList) {
		Collections.sort(inPlaceList, new nodesDepthComparator());
		logger.finest("Sort result: " + inPlaceList);
	}

	public void handleLoadingException(Exception ex) {
		String exceptionType = ex.getClass().getName();
		if (exceptionType.equals("freemind.main.XMLParseException")) {
			int showDetail = JOptionPane.showConfirmDialog(getView(),
					Resources.getInstance().getText("map_corrupted"), "FreeMind",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if (showDetail == JOptionPane.YES_OPTION) {
			}
		} else if (exceptionType.equals("java.io.FileNotFoundException")) {
		} else {
			org.rogach.simplymindmap.main.Resources.getInstance().logException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		NodeView node = getSelectedView();
		if (visible) {
			onFocusNode(node);
		} else {
			// bug fix, fc 18.5.2004. This should not be here.
			if (node != null) {
				onLostFocusNode(node);
			}
		}
		changeSelection(node, !visible);
	}

	// status, currently: default, blocked (PN)
	// (blocked to protect against particular events e.g. in edit mode)
	private boolean isBlocked = false;

	protected MapView mView;

	public boolean isBlocked() {
		return this.isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public MindMap getMap() {
		return mModel;
	}

	public MindMapNode getRootNode() {
		return (MindMapNode) getMap().getRoot();
	}

	/**
	 * This was inserted by fc, 10.03.04 to enable all actions to refer to its
	 * controller easily.
	 */
	public ControllerAdapter getModeController() {
		return this;
	}

	// fc, 29.2.2004: there is no sense in having this private and the
	// controller public,
	// because the getController().getModel() method is available anyway.
	public MapAdapter getModel() {
		return mModel;
	}

	public MapView getView() {
		return mView;
	}

	public void setView(MapView pView) {
		mView = pView;
	}

	/**
	 * @throws {@link IllegalArgumentException} when node isn't found.
	 */
	public NodeAdapter getNodeFromID(String nodeID) {
		NodeAdapter node = (NodeAdapter) getMap().getLinkRegistry()
				.getTargetForId(nodeID);
		if (node == null) {
			throw new IllegalArgumentException("Node belonging to the node id "
					+ nodeID + " not found in map");
		}
		return node;
	}

	public String getNodeID(MindMapNode selected) {
		return getMap().getLinkRegistry().registerLinkTarget(selected);
	}

	public MindMapNode getSelected() {
		final NodeView selectedView = getSelectedView();
		if (selectedView != null)
			return selectedView.getModel();
		return null;
	}

	public NodeView getSelectedView() {
		if (getView() != null)
			return getView().getSelected();
		return null;
	}

	public Transferable copy(MindMapNode node, boolean saveInvisible) {
		throw new IllegalArgumentException("No copy so far.");
	}

	public Transferable copy() {
		return copy(getView().getSelectedNodesSortedByY(), false);
	}

	public Transferable copy(List selectedNodes, boolean copyInvisible) {
		try {
			String forNodesFlavor = createForNodesFlavor(selectedNodes,
					copyInvisible);
			List createForNodeIdsFlavor = createForNodeIdsFlavor(selectedNodes,
					copyInvisible);

			return new MindMapNodesSelection(forNodesFlavor,
					getMap().getAsPlainText(selectedNodes), null, createForNodeIdsFlavor);
		}

		catch (UnsupportedFlavorException ex) {
			org.rogach.simplymindmap.main.Resources.getInstance().logException(ex);
		} catch (IOException ex) {
			org.rogach.simplymindmap.main.Resources.getInstance().logException(ex);
		}
		return null;
	}

	public String createForNodesFlavor(List selectedNodes, boolean copyInvisible)
			throws UnsupportedFlavorException, IOException {
		String forNodesFlavor = "";
		boolean firstLoop = true;
		for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
			MindMapNode tmpNode = (MindMapNode) it.next();
			if (firstLoop) {
				firstLoop = false;
			} else {
				forNodesFlavor += NODESEPARATOR;
			}

			forNodesFlavor += copy(tmpNode, copyInvisible).getTransferData(
					MindMapNodesSelection.mindMapNodesFlavor);
		}
		return forNodesFlavor;
	}

	public List createForNodeIdsFlavor(List selectedNodes, boolean copyInvisible)
			throws UnsupportedFlavorException, IOException {
		Vector forNodesFlavor = new Vector();
		boolean firstLoop = true;
		for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
			MindMapNode tmpNode = (MindMapNode) it.next();

			forNodesFlavor.add(getNodeID(tmpNode));
		}
		return forNodesFlavor;
	}

	/**
     */
	public Color getSelectionColor() {
		return selectionColor;
	}

	/**
	 * Don't call me directly!!! The basic folding method. Without undo.
	 */
	public void _setFolded(MindMapNode node, boolean folded) {
		if (node == null)
			throw new IllegalArgumentException(
					"setFolded was called with a null node.");
		// no root folding, fc, 16.5.2004
		if (node.isRoot() && folded) {
			return;
		}
		if (node.isFolded() != folded) {
			node.setFolded(folded);
			nodeStructureChanged(node);
		}
	}

	public void displayNode(MindMapNode node) {
		displayNode(node, null);
	}

	/**
	 * Display a node in the display (used by find and the goto action by arrow
	 * link actions).
	 */
	public void displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay) {
		// Unfold the path to the node
		Object[] path = getMap().getPathToRoot(node);
		// Iterate the path with the exception of the last node
		for (int i = 0; i < path.length - 1; i++) {
			MindMapNode nodeOnPath = (MindMapNode) path[i];
			// System.out.println(nodeOnPath);
			if (nodeOnPath.isFolded()) {
				if (nodesUnfoldedByDisplay != null)
					nodesUnfoldedByDisplay.add(nodeOnPath);
				setFolded(nodeOnPath, false);
			}
		}

	}

	/** Select the node and scroll to it. **/
	private void centerNode(NodeView node) {
		getView().centerNode(node);
		getView().selectAsTheOnlyOneSelected(node);
	}

	public void centerNode(MindMapNode node) {
		NodeView view = null;
		if (node != null) {
			view = getView().getNodeView(node);
		} else {
			return;
		}
		if (view == null) {
			displayNode(node);
			view = getView().getNodeView(node);
		}
		centerNode(view);
	}

	public NodeView getNodeView(MindMapNode node) {
		return getView().getNodeView(node);
	}

	public void insertNodeInto(MindMapNode newNode, MindMapNode parent,
			int index) {
		getModel().insertNodeInto(newNode, parent, index);
		// call hooks
		fireRecursiveNodeCreateEvent(newNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.MindMap#insertNodeInto(javax.swing.tree.MutableTreeNode,
	 * javax.swing.tree.MutableTreeNode)
	 */
	public void insertNodeInto(MindMapNode newChild, MindMapNode parent) {
		insertNodeInto(newChild, parent, parent.getChildCount());
	}

	public void setToolTip(MindMapNode node, String key, String value) {
		node.setToolTip(key, value);
		nodeRefresh(node);
	}

}
