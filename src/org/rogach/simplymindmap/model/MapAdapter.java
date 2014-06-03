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


package org.rogach.simplymindmap.model;

import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.MindMapNode;

public abstract class MapAdapter extends DefaultTreeModel implements MindMap {

	static protected Logger logger;
	protected MindMapController mMindMapController;

	public MapAdapter() {
		super(null);
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	public MindMapController getMindMapController() {
		return mMindMapController;
	}
  
  public void setMindMapController(MindMapController controller) {
    this.mMindMapController = controller;
    this.mMindMapController.setModel(this);
  }

	public MindMapNode getRootNode() {
		return (MindMapNode) getRoot();
	}

	public void setRoot(MindMapNode root) {
		super.setRoot(root);
	}

	public MindMapLinkRegistry getLinkRegistry() {
		return null;
	}

	/**
	 * This method should not be called directly!
	 */
	public void nodeChanged(TreeNode node) {
		getMindMapController().nodeChanged((MindMapNode) node);
	}

	public void nodeRefresh(TreeNode node) {
		getMindMapController().nodeRefresh((MindMapNode) node);
	}

	/**
	 * Invoke this method if you've totally changed the children of node and its
	 * childrens children... This will post a treeStructureChanged event.
	 */
	public void nodeChangedInternal(TreeNode node) {
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
