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


package org.rogach.simplymindmap.controller;

import org.rogach.simplymindmap.model.MindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.model.MindMapXMLElement;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.rogach.simplymindmap.main.ResourceKeys;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.model.MindIcon;
import org.rogach.simplymindmap.model.XMLElementAdapter;
import org.rogach.simplymindmap.controller.actions.BoldAction;
import org.rogach.simplymindmap.controller.actions.ChangeNodeLevelAction;
import org.rogach.simplymindmap.controller.actions.CompoundActionHandler;
import org.rogach.simplymindmap.controller.actions.CopyAction;
import org.rogach.simplymindmap.controller.actions.CutAction;
import org.rogach.simplymindmap.controller.actions.DeleteChildAction;
import org.rogach.simplymindmap.controller.actions.EditAction;
import org.rogach.simplymindmap.controller.actions.FindAction;
import org.rogach.simplymindmap.controller.actions.FindAction.FindNextAction;
import org.rogach.simplymindmap.controller.actions.FitToPageAction;
import org.rogach.simplymindmap.controller.actions.FontFamilyAction;
import org.rogach.simplymindmap.controller.actions.FontSizeAction;
import org.rogach.simplymindmap.controller.actions.IconAction;
import org.rogach.simplymindmap.controller.actions.IconSelectionAction;
import org.rogach.simplymindmap.controller.actions.ItalicAction;
import org.rogach.simplymindmap.controller.actions.MoveNodeAction;
import org.rogach.simplymindmap.controller.actions.NewChildAction;
import org.rogach.simplymindmap.controller.actions.NewPreviousSiblingAction;
import org.rogach.simplymindmap.controller.actions.NewSiblingAction;
import org.rogach.simplymindmap.controller.actions.NodeColorAction;
import org.rogach.simplymindmap.controller.actions.NodeDownAction;
import org.rogach.simplymindmap.controller.actions.NodeGeneralAction;
import org.rogach.simplymindmap.controller.actions.NodeUpAction;
import org.rogach.simplymindmap.controller.actions.PasteAction;
import org.rogach.simplymindmap.controller.actions.RedoAction;
import org.rogach.simplymindmap.controller.actions.RemoveAllIconsAction;
import org.rogach.simplymindmap.controller.actions.RemoveIconAction;
import org.rogach.simplymindmap.controller.actions.SelectAllAction;
import org.rogach.simplymindmap.controller.actions.SelectBranchAction;
import org.rogach.simplymindmap.controller.actions.SingleNodeOperation;
import org.rogach.simplymindmap.controller.actions.ToggleChildrenFoldedAction;
import org.rogach.simplymindmap.controller.actions.ToggleFoldedAction;
import org.rogach.simplymindmap.controller.actions.UndoAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionFactory;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.UndoActionHandler;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.nanoxml.XMLParseException;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.MainView;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.NodeView;

public class MindMapController {
  
	private static Logger logger;
  public static final int NEW_CHILD_WITHOUT_FOCUS = 1; // old model of
  // insertion
  public static final int NEW_CHILD = 2;
  public static final int NEW_SIBLING_BEHIND = 3;
  public static final int NEW_SIBLING_BEFORE = 4;
  public static final String NODESEPARATOR = "<nodeseparator>";
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
				public void apply(MindMapModel map, MindMapNode node) {
					increaseFontSize(node, 1);
				}
			});
	public Action decreaseNodeFont = new NodeGeneralAction(this,
			"decrease_node_font_size", null, new SingleNodeOperation() {
				public void apply(MindMapModel map, MindMapNode node) {
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
  private Color selectionColor = new Color(200, 220, 200);
  /**
   * The model, this controller belongs to. It may be null, if it is the
   * default controller that does not show a map.
   */
  private MindMapModel mModel;
  private HashSet mNodeSelectionListeners = new HashSet();
  private HashSet mNodeLifetimeListeners = new HashSet();
  // status, currently: default, blocked (PN)
  // (blocked to protect against particular events e.g. in edit mode)
  private boolean isBlocked = false;
  protected MapView mView;
  
	public MindMapController(MapView view) {
		super();
    this.mView = view;
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		// create action factory:
		actionFactory = new ActionFactory();
    new CompoundActionHandler(this); // eagerly initialize compound action handler
    logger.info("createIconActions");
    createStandardActions();
    createIconActions();
	}

	private void createStandardActions() {
		// prepare undo:
		undo = new UndoAction(this);
		redo = new RedoAction(this);
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

	// fc, 14.12.2004: changes, such that different models can be used:
	private NewNodeCreator myNewNodeCreator = null;

  public void setModel(MindMapModel model) {
    mModel = model;
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
    logger.finest("nodeChanged called for node " + node + " parent=" + node.getParentNode());
    if (isUpdate) {
      // Tell any node hooks that the node is changed:
      updateNode(node);
    }
    ((MindMapModel) getMap()).nodeChangedInternal(node);
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
    ((MindMapModel) getMap()).nodeChangedInternal(node);
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
      NodeSelectionListener listener = (NodeSelectionListener) iter.next();
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
        NodeSelectionListener listener = (NodeSelectionListener) iter.next();
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
        NodeSelectionListener listener = (NodeSelectionListener) iter.next();
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
        NodeSelectionListener listener = (NodeSelectionListener) iter.next();
        listener.onSelectionChange(pNode, pIsSelected);
      }
    } catch (RuntimeException e) {
      logger.log(Level.SEVERE, "Error in node selection listeners", e);
    }
  }

  public void registerNodeSelectionListener(NodeSelectionListener listener, boolean pCallWithCurrentSelection) {
    mNodeSelectionListeners.add(listener);
    if (pCallWithCurrentSelection) {
      try {
        listener.onFocusNode(getSelectedView());
      } catch (Exception e) {
        Resources.getInstance().logException(e);
      }
      for (Iterator it = getView().getSelecteds().iterator(); it.hasNext();) {
        NodeView view = (NodeView) it.next();
        try {
          listener.onSelectionChange(view, true);
        } catch (Exception e) {
          Resources.getInstance().logException(e);
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
      fireRecursiveNodeCreateEvent((MindMapNode) getMap().getRoot());
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
      MindMapNode child = (MindMapNode) i.next();
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
      NodeSelectionListener listener = (NodeSelectionListener) iter.next();
      listener.onSaveNode(node);
    }
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

  public List getSelectedsByDepth() {
    // return an ArrayList of MindMapNodes.
    List result = getSelecteds();
    sortNodesByDepth(result);
    return result;
  }

  public void sortNodesByDepth(List inPlaceList) {
    Collections.sort(inPlaceList, new MindMapController.nodesDepthComparator());
    logger.finest("Sort result: " + inPlaceList);
  }

  /*
   * (non-Javadoc)
   *
   * @see freemind.modes.MindMapController#setVisible(boolean)
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

  public boolean isBlocked() {
    return this.isBlocked;
  }

  public void setBlocked(boolean isBlocked) {
    this.isBlocked = isBlocked;
  }

  public MindMapModel getMap() {
    return mModel;
  }

  // fc, 29.2.2004: there is no sense in having this private and the
  // controller public,
  // because the getController().getModel() method is available anyway.
  public MindMapModel getModel() {
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
  public MindMapNode getNodeFromID(String nodeID) {
    MindMapNode node = (MindMapNode) getMap().getLinkRegistry().getTargetForId(nodeID);
    if (node == null) {
      throw new IllegalArgumentException("Node belonging to the node id " + nodeID + " not found in map");
    }
    return node;
  }

  public String getNodeID(MindMapNode selected) {
    return getMap().getLinkRegistry().registerLinkTarget(selected);
  }

  public MindMapNode getSelected() {
    final NodeView selectedView = getSelectedView();
    if (selectedView != null) {
      return selectedView.getModel();
    }
    return null;
  }

  public NodeView getSelectedView() {
    if (getView() != null) {
      return getView().getSelected();
    }
    return null;
  }

  public String createForNodesFlavor(List selectedNodes, boolean copyInvisible) throws UnsupportedFlavorException, IOException {
    String forNodesFlavor = "";
    boolean firstLoop = true;
    for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
      MindMapNode tmpNode = (MindMapNode) it.next();
      if (firstLoop) {
        firstLoop = false;
      } else {
        forNodesFlavor += NODESEPARATOR;
      }
      forNodesFlavor += copy(tmpNode, copyInvisible).getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
    }
    return forNodesFlavor;
  }

  public List createForNodeIdsFlavor(List selectedNodes, boolean copyInvisible) throws UnsupportedFlavorException, IOException {
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
    if (node == null) {
      throw new IllegalArgumentException("setFolded was called with a null node.");
    }
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
        if (nodesUnfoldedByDisplay != null) {
          nodesUnfoldedByDisplay.add(nodeOnPath);
        }
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

  public void insertNodeInto(MindMapNode newNode, MindMapNode parent, int index) {
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

  public static interface MouseWheelEventHandler {

    /**
     * @return true if the event was sucessfully processed and false if the
     *         event did not apply.
     */
    boolean handleMouseWheelEvent(MouseWheelEvent e);
  }

  /** */
  public static interface NodeSelectionListener {

    /**
     * Sent, if a node is changed
     * */
    void onUpdateNodeHook(MindMapNode node);

    /**
     * Is sent when a node is focused (this means, that it is *the* selected node,
     * there may only be one!).
     */
    void onFocusNode(NodeView node);

    /**
     * Is sent when a node has lost its focus (see {@link onSelectHook()}).
     */
    void onLostFocusNode(NodeView node);

    /**
     * Is issued before a node is saved (eg. to save its notes, too, even if
     * the notes is currently edited).
     */
    void onSaveNode(MindMapNode node);

    /**
     * Informs whether or not the node belongs to the group of selected
     * nodes (in contrast to the focused node above).
     * @param pNode
     * @param pIsSelected true, if the node is selected now.
     */
    void onSelectionChange(NodeView pNode, boolean pIsSelected);
  }

  /** */
  public static interface NodeLifetimeListener {

    /**
     * Sent, if a node is created (on map startup or during operations).
     * */
    void onCreateNodeHook(MindMapNode node);

    /**
     * Is sent before a node is deleted (on map shutdown, too).
     */
    void onPreDeleteNode(MindMapNode node);

    /**
     * Is sent before after a node is deleted (on map shutdown, this event
     * is *not* send).
     */
    void onPostDeleteNode(MindMapNode node, MindMapNode parent);
  }
  
	public interface NewNodeCreator {
		MindMapNode createNode(String userObject, MindMapModel map);
	}

	public class DefaultMindMapNodeCreator implements NewNodeCreator {

		public MindMapNode createNode(String userObject, MindMapModel map) {
			return new MindMapNode(userObject, map);
		}

	}

	public void setNewNodeCreator(NewNodeCreator creator) {
		myNewNodeCreator = creator;
	}

	public MindMapNode newNode(String userObject, MindMapModel map) {
		// singleton default:
		if (myNewNodeCreator == null) {
			myNewNodeCreator = new DefaultMindMapNodeCreator();
		}
		return myNewNodeCreator.createNode(userObject, map);
	}

	// convenience methods
	public MindMapModel getMindMapMapModel() {
		return (MindMapModel) getMap();
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
			((MindMapNode) node).save(stringWriter, getMap()
					.getLinkRegistry(), saveInvisible, true);
		} catch (IOException e) {
		}
		Vector nodeList = Tools.getVectorWithSingleElement(getNodeID(node));
		return new MindMapNodesSelection(stringWriter.toString(),
				null, null, nodeList);
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

	public XMLElement createXMLElement() {
		return new MindMapXMLElement(this);
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
      super();
    }

    /* the < relation. */
    public int compare(Object p1, Object p2) {
      MindMapNode n1 = (MindMapNode) p1;
      MindMapNode n2 = (MindMapNode) p2;
      Object[] path1 = getModel().getPathToRoot(n1);
      Object[] path2 = getModel().getPathToRoot(n2);
      int depth = path1.length - path2.length;
      if (depth > 0) {
        return -1;
      }
      if (depth < 0) {
        return 1;
      }
      if (n1.isRoot()) {
        return 0;
      }
      return n1.getParentNode().getChildPosition(n1) - n2.getParentNode().getChildPosition(n2);
    }
  }

}
