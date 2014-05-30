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


package freemind.modes;

import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public abstract class MapAdapter extends DefaultTreeModel implements MindMap {

	protected boolean readOnly = true;
	static protected Logger logger;
	protected final ModeController mModeController;

	public MapAdapter(ModeController modeController) {
		super(null);
		this.mModeController = modeController;
		mModeController.setModel(this);
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	public ModeController getModeController() {
		return mModeController;
	}

	public void load(File file) throws FileNotFoundException, IOException {
		try {
			load(Tools.fileToUrl(file));
		} catch (XMLParseException e) {
			freemind.main.Resources.getInstance().logException(e);
		} catch (URISyntaxException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * Attempts to lock the map using semaphore file.
	 * 
	 * @return If the map is locked, return the name of the locking user, return
	 *         null otherwise.
	 * @throws Exception
	 */
	public String tryToLock(File file) throws Exception {
		return null;
	}

	public void destroy() {
		// Do all the necessary destructions in your model,
		// e.g. remove file locks.
		// and remove all hooks:
		removeNodes(getRootNode());
	}


	// (PN)
	// public void close() {
	// }

	/**
	 */
	private void removeNodes(MindMapNode node) {
		mModeController.fireNodePreDeleteEvent(node);
		// and all children:
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			removeNodes(child);
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public MindMapNode getRootNode() {
		return (MindMapNode) getRoot();
	}

	public void setRoot(MindMapNode root) {
		super.setRoot(root);
	}

	/**
	 * @param newRoot
	 *            one of the nodes, that is now root. The others are grouped
	 *            around.
	 */
	public void changeRoot(MindMapNode newRoot) {
		if (newRoot == getRootNode()) {
			return;
		}
		boolean left = newRoot.isLeft();
		MindMapNode node = newRoot;
		// collect parents (as we remove them from their parents...)
		Vector parents = new Vector();
		while (node.getParentNode() != null) {
			MindMapNode parent = node.getParentNode();
			parents.add(0, node);
			node = parent;
		}
		// bind all parents to a new chain:
		for (Iterator it = parents.iterator(); it.hasNext();) {
			node = (MindMapNode) it.next();
			MindMapNode parent = node.getParentNode();
			// remove parent
			node.removeFromParent();
			// special treatment for left/right
			if (node == newRoot) {
				for (Iterator it2 = node.getChildren().iterator(); it2
						.hasNext();) {
					MindMapNode child = (MindMapNode) it2.next();
					child.setLeft(left);
				}
				parent.setLeft(!left);
			}
			// and put it as a child
			node.insert(parent, node.getChildCount());
		}
		// and set root
		setRoot(newRoot);
	}

	protected String getText(String textId) {
		return Resources.getInstance().getResourceString(textId);
	}

	//
	// Node editing
	//

	public String getAsPlainText(List mindMapNodes) {
		return "";
	}

	public String getAsRTF(List mindMapNodes) {
		return "";
	}

	public String getAsHTML(List mindMapNodes) {
		return null;
	}

	public String getRestorable() {
		return null;
	}

	public MindMapLinkRegistry getLinkRegistry() {
		return null;
	}

	/**
	 * This method should not be called directly!
	 */
	public void nodeChanged(TreeNode node) {
		getModeController().nodeChanged((MindMapNode) node);
	}

	public void nodeRefresh(TreeNode node) {
		getModeController().nodeRefresh((MindMapNode) node);
	}

	/**
	 * Invoke this method if you've totally changed the children of node and its
	 * childrens children... This will post a treeStructureChanged event.
	 */
	void nodeChangedInternal(TreeNode node) {
		if (node != null) {
			fireTreeNodesChanged(this, getPathToRoot(node), null, null);
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source
	 *            the node being changed
	 * @param path
	 *            the path to the root node
	 * @param childIndices
	 *            the indices of the changed elements
	 * @param children
	 *            the changed elements
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		e = fireTreeNodesInserted(source, path, childIndices, children,
				listeners, e);
		MindMapNode node = (MindMapNode) path[path.length - 1];
		fireTreeNodesInserted(source, path, childIndices, children, node
				.getListeners().getListenerList(), e);
	}

	private TreeModelEvent fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices, Object[] children, Object[] listeners,
			TreeModelEvent e) {
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
		return e;
	}

	protected void fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		e = fireTreeNodesRemoved(source, path, childIndices, children,
				listeners, e);
		MindMapNode node = (MindMapNode) path[path.length - 1];
		fireTreeNodesRemoved(source, path, childIndices, children, node
				.getListeners().getListenerList(), e);
	}

	private TreeModelEvent fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices, Object[] children, Object[] listeners,
			TreeModelEvent e) {
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
		return e;
	}

	protected void fireTreeStructureChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		e = fireTreeStructureChanged(source, path, childIndices, children,
				listeners, e);
		MindMapNode node = (MindMapNode) path[path.length - 1];
		fireTreeStructureChanged(source, path, childIndices, children, node
				.getListeners().getListenerList(), e);
	}

	private TreeModelEvent fireTreeStructureChanged(Object source,
			Object[] path, int[] childIndices, Object[] children,
			Object[] listeners, TreeModelEvent e) {
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
		return e;
	}

	protected void fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		e = fireTreeNodesChanged(source, path, childIndices, children,
				listeners, e);
		MindMapNode node = (MindMapNode) path[path.length - 1];
		fireTreeNodesChanged(source, path, childIndices, children, node
				.getListeners().getListenerList(), e);
	}

	private TreeModelEvent fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices, Object[] children, Object[] listeners,
			TreeModelEvent e) {
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
		return e;
	}

}
