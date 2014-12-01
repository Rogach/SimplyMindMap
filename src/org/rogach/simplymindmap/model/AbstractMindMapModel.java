/*FreeMindget - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.nanoxml.XMLParseException;
import org.rogach.simplymindmap.util.Tools;

public abstract class AbstractMindMapModel extends DefaultTreeModel {

	public static final String MAP_INITIAL_START = "<map version=\"";
	public static final String RESTORE_MODE_MIND_MAP = "MindMap:";
	public static final String FREEMIND_VERSION_UPDATER_XSLT = "freemind/modes/mindmapmode/freemind_version_updater.xslt";
  protected static Logger logger;
	private MindMapLinkRegistry linkRegistry;
	/**
	 * The current version and all other version that don't need XML update for
	 * sure.
	 */
	public static final String EXPECTED_START_STRINGS[] = {
			MAP_INITIAL_START + "1.0.1" + "\"",
			MAP_INITIAL_START + "0.7.1\"" };
  protected MindMapController mMindMapController;

	//
	// Constructors
	//

	public AbstractMindMapModel() {
		this(null);
	}

	public AbstractMindMapModel(MindMapNode root) {
		super(null);

		// register new LinkRegistryAdapter
		linkRegistry = new MindMapLinkRegistry();

		if (root == null) {
			root = newNode("root");
    }
    updateMapReferenceInNodes(root);
		setRoot(root);
	}
  
  private void updateMapReferenceInNodes(MindMapNode node) {
    node.setModel(this);
    for (MindMapNode child : node.getChildren()) {
      updateMapReferenceInNodes(child);
    }
  }

	public MindMapLinkRegistry getLinkRegistry() {
		return linkRegistry;
	}

	public void changeNode(MindMapNode node, String newText) {
    node.setUserObject(newText);
		nodeChanged(node);
	}

  public MindMapController getMindMapController() {
    return mMindMapController;
  }

  public void setMindMapController(MindMapController controller) {
    this.mMindMapController = controller;
    this.mMindMapController.setMapModel(this);
  }

  public MindMapNode getRootNode() {
    return (MindMapNode) getRoot();
  }

  public void setRoot(MindMapNode root) {
    super.setRoot(root);
    updateMapReferenceInNodes(root);
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
  protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    e = fireTreeNodesInserted(source, path, childIndices, children, listeners, e);
    MindMapNode node = (MindMapNode) path[path.length - 1];
    fireTreeNodesInserted(source, path, childIndices, children, node.getListeners().getListenerList(), e);
  }

  private TreeModelEvent fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
      }
    }
    return e;
  }

  protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    e = fireTreeNodesRemoved(source, path, childIndices, children, listeners, e);
    MindMapNode node = (MindMapNode) path[path.length - 1];
    fireTreeNodesRemoved(source, path, childIndices, children, node.getListeners().getListenerList(), e);
  }

  private TreeModelEvent fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
      }
    }
    return e;
  }

  protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    e = fireTreeStructureChanged(source, path, childIndices, children, listeners, e);
    MindMapNode node = (MindMapNode) path[path.length - 1];
    fireTreeStructureChanged(source, path, childIndices, children, node.getListeners().getListenerList(), e);
  }

  private TreeModelEvent fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
      }
    }
    return e;
  }

  protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    e = fireTreeNodesChanged(source, path, childIndices, children, listeners, e);
    MindMapNode node = (MindMapNode) path[path.length - 1];
    fireTreeNodesChanged(source, path, childIndices, children, node.getListeners().getListenerList(), e);
  }

  private TreeModelEvent fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
      }
    }
    return e;
  }

	public static class StringReaderCreator implements ReaderCreator {

		private final String mString;

		public StringReaderCreator(String pString) {
			mString = pString;
		}

		public Reader createReader() throws FileNotFoundException {
			return new StringReader(mString);
		}

		public String toString() {
			return mString;
		}
	}

	public interface ReaderCreator {
		Reader createReader() throws FileNotFoundException;
	}

	MindMapNode loadTree(ReaderCreator pReaderCreator)
			throws XMLParseException, IOException {
		return loadTree(pReaderCreator, true);
	}

	public MindMapNode loadTree(ReaderCreator pReaderCreator,
			boolean pAskUserBeforeUpdate) throws XMLParseException, IOException {
		int versionInfoLength;
		versionInfoLength = EXPECTED_START_STRINGS[0].length();
		// reading the start of the file:
		StringBuffer buffer = readFileStart(pReaderCreator.createReader(),
				versionInfoLength);
		// the resulting file is accessed by the reader:
		Reader reader = null;
		for (int i = 0; i < EXPECTED_START_STRINGS.length; i++) {
			versionInfoLength = EXPECTED_START_STRINGS[i].length();
			String mapStart = "";
			if (buffer.length() >= versionInfoLength) {
				mapStart = buffer.substring(0, versionInfoLength);
			}
			if (mapStart.startsWith(EXPECTED_START_STRINGS[i])) {
				// actual version:
				reader = Tools.getActualReader(pReaderCreator.createReader());
				break;
			}
		}
    if (reader == null) {
      reader = Tools.getActualReader(pReaderCreator.createReader());
    }
		try {
			HashMap<String, MindMapNode> IDToTarget = new HashMap<>();
			return (MindMapNode) mMindMapController.createNodeTreeFromXml(
					reader, IDToTarget);
			// MindMapXMLElement mapElement = new
			// MindMapXMLElement(mMindMapController);
			// mapElement.parseFromReader(reader);
			// // complete the arrow links:
			// mapElement.processUnfinishedLinks(getLinkRegistry());
			// // we wait with "invokeHooksRecursively" until the map is fully
			// // registered.
			// return (MindMapNodeModel) mapElement.getMapChild();
		} catch (XMLParseException | IOException ex) {
			String errorMessage = "Error while parsing file:" + ex;
      Logger.getLogger(AbstractMindMapModel.class.getName()).log(Level.SEVERE, null, ex);
			MindMapXMLElement mapElement = new MindMapXMLElement(
					mMindMapController);
			MindMapNode result = mapElement.createNodeAdapter(null);
			result.setText(errorMessage);
			return (MindMapNode) result;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Returns pMinimumLength bytes of the files content.
	 * 
	 * @return an empty string buffer, if something fails.
	 */
	private StringBuffer readFileStart(Reader pReader, int pMinimumLength) {
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// get the file start into the memory:
			in = new BufferedReader(pReader);
			String str;
			while ((str = in.readLine()) != null) {
				buffer.append(str);
				if (buffer.length() >= pMinimumLength)
					break;
			}
			in.close();
		} catch (Exception e) {
      Logger.getLogger(AbstractMindMapModel.class.getName()).log(Level.SEVERE, null, e);
			return new StringBuffer();
		}
		return buffer;
	}
  
  public String getAsPlainText(List<MindMapNode> mindMapNodes) {
		// Returns success of the operation.
		try {
			StringWriter stringWriter = new StringWriter();
      try (BufferedWriter fileout = new BufferedWriter(stringWriter)) {
        for (MindMapNode node : mindMapNodes) {
          node.saveTXT(fileout, /* depth= */ 0);
        }
      }
			return stringWriter.toString();

		} catch (Exception e) {
      Logger.getLogger(AbstractMindMapModel.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	public boolean saveTXT(MindMapNode rootNodeOfBranch, File file) {
		// Returns success of the operation.
		try {
      try (BufferedWriter fileout = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(file)))) {
        rootNodeOfBranch.saveTXT(fileout,/* depth= */0);
      }
			return true;

		} catch (Exception e) {
      Logger.getLogger(AbstractMindMapModel.class.getName()).log(Level.SEVERE, null, e);
			return false;
		}
	}
  
  public abstract MindMapNode newNode(String userObject);
  
  public abstract XMLElement createXMLElement();

}
