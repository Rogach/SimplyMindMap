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
/*$Id: MindMapNodeModel.java,v 1.21.14.4.4.11 2008/05/26 19:25:09 christianfoltin Exp $*/

package org.rogach.simplymindmap.model;

import org.rogach.simplymindmap.model.MindMapEdgeModel;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.util.XmlTools;
import org.rogach.simplymindmap.view.NodeView;

/**
 * This class represents a single Node of a Tree. It contains direct handles to
 * its parent and children and to its view.
 */
public class MindMapNode implements MutableTreeNode {
  static final int SHIFT = -2; // height of the vertical shift between node and
  // its closest child
  public static final int HGAP = 20; // width of the horizontal gap that
  // contains the edges
  public static final int VGAP = 3; // height of the vertical gap between nodes
  public static final int LEFT_POSITION = -1;
  public static final int RIGHT_POSITION = 1;
  public static final int UNKNOWN_POSITION = 0;
  private static boolean sSaveOnlyIntrinsicallyNeededIds = false;
  protected Object userObject = "no text";
  private TreeMap<String, String> toolTip = null;
  /** stores the icons associated with this node. */
  protected Vector<MindIcon> icons = null;
  protected Color color;
  protected Color backgroundColor;
  protected boolean folded;
  private int position = UNKNOWN_POSITION;
  private int vGap = VGAP;
  private int hGap = HGAP;
  private int shiftY = 0;
  protected List<MindMapNode> children;
  private MindMapNode preferredChild;
  protected Font font;
  private MindMapNode parent;
  /**
   * the edge which leads to this node, only root has none In future it has to
   * hold more than one view, maybe with a Vector in which the index specifies
   * the MapView which contains the NodeViews
   */
  private MindMapEdge edge;
  private Collection<NodeView> views = null;
  private MindMapModel model = null;
  EventListenerList listenerList = new EventListenerList();

	public MindMapNode(String userObject) {
		this(userObject, null);
	}

	public MindMapNode(String userObject, MindMapModel map) {
    setText(userObject);
    this.model = map;
		children = new LinkedList();
		setEdge(new MindMapEdgeModel(this));
	}

	public void collectColors(HashSet colors) {
		if (color != null) {
			colors.add(getColor());
		}
		for (ListIterator e = childrenUnfolded(); e.hasNext();) {
			((MindMapNode) e.next()).collectColors(colors);
		}
	}
  
	public String getPlainTextContent() {
		return getText();
	}

	public void saveTXT(Writer fileout, int depth) throws IOException {
		String plainTextContent = getPlainTextContent();
		for (int i = 0; i < depth; ++i) {
			fileout.write("    ");
		}
		if (plainTextContent.matches(" *")) {
			fileout.write("o");
		} else {
      fileout.write(plainTextContent);
		}

		fileout.write("\n");
		// fileout.write(System.getProperty("line.separator"));
		// fileout.newLine();

		// ^ One would rather expect here one of the above commands
		// commented out. However, it does not work as expected on
		// Windows. My unchecked hypothesis is, that the String Java stores
		// in Clipboard carries information that it actually is \n
		// separated string. The current coding works fine with pasting on
		// Windows (and I expect, that on Unix too, because \n is a Unix
		// separator). This method is actually used only for pasting
		// purposes, it is never used for writing to file. As a result, the
		// writing to file is not tested.

		// Another hypothesis is, that something goes astray when creating
		// StringWriter.

		saveChildrenText(fileout, depth);
	}

	private void saveChildrenText(Writer fileout, int depth) throws IOException {
		for (ListIterator e = sortedChildrenUnfolded(); e.hasNext();) {
			final MindMapNode child = (MindMapNode) e.next();
			if (child.isVisible()) {
				child.saveTXT(fileout, depth + 1);
			} else {
				child.saveChildrenText(fileout, depth);
			}
		}
	}

  public void setMap(MindMapModel map) {
    this.model = map;
  }

  public String getText() {
    String string = "";
    if (userObject != null) {
      string = userObject.toString();
    }
    return string;
  }

  public final void setText(String text) {
    if (text == null) {
      userObject = null;
      return;
    }
    userObject = text;
  }

  public Collection getViewers() {
    if (views == null) {
      views = new LinkedList<>();
    }
    return views;
  }

  public void addViewer(NodeView viewer) {
    getViewers().add(viewer);
    addTreeModelListener(viewer);
  }

  public void removeViewer(NodeView viewer) {
    getViewers().remove(viewer);
    removeTreeModelListener(viewer);
  }

  /** Creates the TreePath recursively */
  public TreePath getPath() {
    Vector pathVector = new Vector();
    TreePath treePath;
    this.addToPathVector(pathVector);
    treePath = new TreePath(pathVector.toArray());
    return treePath;
  }

  public MindMapEdge getEdge() {
    return edge;
  }

  public void setEdge(MindMapEdge edge) {
    this.edge = edge;
  }

  /** The Foreground/Font Color */
  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  // fc, 24.2.2004: background color:
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color color) {
    this.backgroundColor = color;
  }

  //
  // font handling
  //
  // Remark to setBold and setItalic implemetation
  //
  // Using deriveFont() is a bad idea, because it does not really choose
  // the appropriate face. For example, instead of choosing face
  // "Arial Bold", it derives the bold face from "Arial".
  // Node holds font only in the case that the font is not default.
  public void establishOwnFont() {
    font = (font != null) ? font : Tools.getDefaultFont();
  }

  public void setBold(boolean bold) {
    if (bold != isBold()) {
      toggleBold();
    }
  }

  public void toggleBold() {
    establishOwnFont();
    setFont(new Font(font.getFamily(), font.getStyle() ^ Font.BOLD, font.getSize()));
  }

  public void setItalic(boolean italic) {
    if (italic != isItalic()) {
      toggleItalic();
    }
  }

  public void toggleItalic() {
    establishOwnFont();
    setFont(new Font(font.getFamily(), font.getStyle() ^ Font.ITALIC, font.getSize()));
  }

  public void setFont(Font font) {
    this.font = font;
  }

  public MindMapNode getParentNode() {
    return parent;
  }

  public void setFontSize(int fontSize) {
    establishOwnFont();
    setFont(new Font(font.getFamily(), font.getStyle(), fontSize));
  }

  public Font getFont() {
    return font;
  }

  public String getFontSize() {
    if (getFont() != null) {
      return new Integer(getFont().getSize()).toString();
    } else {
      return Resources.getInstance().getProperty("defaultfontsize");
    }
  }

  public String getFontFamilyName() {
    if (getFont() != null) {
      return getFont().getFamily();
    } else {
      return Resources.getInstance().getProperty("defaultfontsize");
    }
  }

  public boolean isBold() {
    return font != null ? font.isBold() : false;
  }

  public boolean isItalic() {
    return font != null ? font.isItalic() : false;
  }

  public boolean isFolded() {
    return folded;
  }

  // fc, 24.9.2003:
  public List getIcons() {
    if (icons == null) {
      return Collections.EMPTY_LIST;
    }
    return icons;
  }

  public MindMapModel getModel() {
    return model;
  }

  public void addIcon(MindIcon _icon, int position) {
    createIcons();
    if (position == MindIcon.LAST) {
      icons.add(_icon);
    } else {
      icons.add(position, _icon);
    }
  }

  /** @return returns the number of remaining icons. */
  public int removeIcon(int position) {
    createIcons();
    if (position == MindIcon.LAST) {
      position = icons.size() - 1;
    }
    icons.remove(position);
    int returnSize = icons.size();
    if (returnSize == 0) {
      icons = null;
    }
    return returnSize;
  }

  /**
   * True iff one of node's <i>strict</i> descendants is folded. A node N is
   * not its strict descendant - the fact that node itself is folded is not
   * sufficient to return true.
   */
  public boolean hasFoldedStrictDescendant() {
    for (ListIterator e = childrenUnfolded(); e.hasNext();) {
      MindMapNode child = (MindMapNode) e.next();
      if (child.isFolded() || child.hasFoldedStrictDescendant()) {
        return true;
      }
    }
    return false;
  }

  public void setFolded(boolean folded) {
    this.folded = folded;
  }

  //
  // other
  //
  public String toString() {
    return getText();
  }

  public boolean isDescendantOf(MindMapNode pParentNode) {
    if (this.isRoot()) {
      return false;
    } else if (pParentNode == getParentNode()) {
      return true;
    } else {
      return getParentNode().isDescendantOf(pParentNode);
    }
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isDescendantOfOrEqual(MindMapNode pParentNode) {
    if (this == pParentNode) {
      return true;
    }
    return isDescendantOf(pParentNode);
  }

  public boolean hasChildren() {
    return children != null && !children.isEmpty();
  }

  public int getChildPosition(MindMapNode childNode) {
    int position = 0;
    for (ListIterator i = children.listIterator(); i.hasNext(); ++position) {
      if (((MindMapNode) i.next()) == childNode) {
        return position;
      }
    }
    return -1;
  }

  public ListIterator<MindMapNode> childrenUnfolded() {
    return children != null ? children.listIterator() : Collections.EMPTY_LIST.listIterator();
  }

  /*
   * (non-Javadoc)
   *
   * @see freemind.modes.MindMapNode#sortedChildrenUnfolded()
   */
  public ListIterator<MindMapNode> sortedChildrenUnfolded() {
    if (children == null) {
      return null;
    }
    LinkedList<MindMapNode> sorted = new LinkedList<MindMapNode>(children);
    /*
     * Using this stable sort, we assure that the left nodes came in front
     * of the right ones.
     */
    Collections.sort(sorted, new Comparator<MindMapNode>() {
      public int compare(MindMapNode pO1, MindMapNode pO2) {
        return comp(((MindMapNode) pO2).isLeft(), ((MindMapNode) pO1).isLeft());
      }

      private int comp(boolean pLeft, boolean pLeft2) {
        if (pLeft == pLeft2) {
          return 0;
        }
        if (pLeft) {
          return 1;
        }
        return -1;
      }
    });
    return sorted.listIterator();
  }

  public ListIterator<MindMapNode> childrenFolded() {
    if (isFolded()) {
      return Collections.EMPTY_LIST.listIterator();
    }
    return childrenUnfolded();
  }

  public List<MindMapNode> getChildren() {
    return Collections.unmodifiableList((children != null) ? children : Collections.EMPTY_LIST);
  }

  //
  // Interface TreeNode
  //
  /**
   * AFAIK there is no way to get an enumeration out of a linked list. So this
   * exception must be thrown, or we can't implement TreeNode anymore (maybe
   * we shouldn't?)
   */
  public Enumeration children() {
    throw new UnsupportedOperationException("Use childrenFolded or childrenUnfolded instead");
  }

  public boolean getAllowsChildren() {
    return true;
  }

  public TreeNode getChildAt(int childIndex) {
    return (TreeNode) children.get(childIndex);
  }

  public int getChildCount() {
    return children == null ? 0 : children.size();
  }

  public int getIndex(TreeNode node) {
    return children.indexOf((MindMapNode) node); // uses equals()
  }

  public TreeNode getParent() {
    return parent;
  }

  public boolean isLeaf() {
    return getChildCount() == 0;
  }

  // fc, 16.12.2003 left-right bug:
  public boolean isLeft() {
    if (getParent() != null && !getParentNode().isRoot()) {
      return getParentNode().isLeft();
    }
    if (position == UNKNOWN_POSITION && !isRoot()) {
      setLeft(getParentNode().isLeft());
    }
    return position == LEFT_POSITION;
  }

  public void setLeft(boolean isLeft) {
    position = isLeft ? LEFT_POSITION : RIGHT_POSITION;
    if (!isRoot()) {
      for (int i = 0; i < getChildCount(); i++) {
        final MindMapNode child = (MindMapNode) getChildAt(i);
        child.position = position;
      }
    }
  }

  public boolean isNewChildLeft() {
    if (!isRoot()) {
      return isLeft();
    }
    int rightChildrenCount = 0;
    for (int i = 0; i < getChildCount(); i++) {
      if (!((MindMapNode) getChildAt(i)).isLeft()) {
        rightChildrenCount++;
      }
      if (rightChildrenCount > getChildCount() / 2) {
        return true;
      }
    }
    return false;
  }

  public void add(MindMapNode child) {
    insert(child, this.getChildCount());
  }
  //
  // Interface MutableTreeNode
  //

  // do all remove methods have to work recursively to make the
  // Garbage Collection work (Nodes in removed Sub-Trees reference each
  // other)?
  public void insert(MutableTreeNode child, int index) {
    final MindMapNode childNode = (MindMapNode) child;
    if (index < 0) {
      // add to the end (used in xml load) (PN)
      index = getChildCount();
      children.add(index, childNode);
    } else {
      // mind preferred child :-)
      children.add(index, childNode);
      preferredChild = childNode;
    }
    child.setParent(this);
  }

  public void remove(int index) {
    MutableTreeNode node = (MutableTreeNode) children.get(index);
    remove(node);
  }

  public void remove(MutableTreeNode node) {
    if (node == this.preferredChild) {
      // mind preferred child :-) (PN)
      int index = children.indexOf(node);
      if (children.size() > index + 1) {
        this.preferredChild = (MindMapNode) (children.get(index + 1));
      } else {
        this.preferredChild = (index > 0) ? (MindMapNode) (children.get(index - 1)) : null;
      }
    }
    node.setParent(null);
    children.remove(node);
  }

  public void removeFromParent() {
    parent.remove(this);
  }

  public void setParent(MutableTreeNode newParent) {
    parent = (MindMapNode) newParent;
  }

  public void setParent(MindMapNode newParent) {
    parent = newParent;
  }

  public void setUserObject(Object object) {
    setText((String) object);
  }

  // //////////////
  // Private methods. Internal Implementation
  // ////////////
  /** Recursive Method for getPath() */
  private void addToPathVector(Vector pathVector) {
    pathVector.add(0, this); // Add myself to beginning of Vector
    if (parent != null) {
      ((MindMapNode) parent).addToPathVector(pathVector);
    }
  }

  public int getNodeLevel() {
    // for cursor navigation within a level (PN)
    int level = 0;
    MindMapNode parent;
    for (parent = this; !parent.isRoot(); parent = parent.getParentNode()) {
      if (parent.isVisible()) {
        level++;
      }
    }
    return level;
  }

  private void createToolTip() {
    if (toolTip == null) {
      toolTip = new TreeMap();
    }
  }

  private void createIcons() {
    if (icons == null) {
      icons = new Vector();
    }
  }

  /**
   */
  public SortedMap getToolTip() {
    if (toolTip == null) {
      return new TreeMap();
    }
    ;
    return Collections.unmodifiableSortedMap(toolTip);
  }

  /**
   */
  public void setToolTip(String key, String string) {
    createToolTip();
    if (string == null) {
      if (toolTip.containsKey(key)) {
        toolTip.remove(key);
      }
      if (toolTip.size() == 0) {
        toolTip = null;
      }
    } else {
      toolTip.put(key, string);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see freemind.modes.MindMapNode#getNodeId()
   */
  public String getObjectId(MindMapController controller) {
    return controller.getNodeID(this);
  }

  public XMLElement save(Writer writer, MindMapLinkRegistry registry, boolean saveInvisible, boolean saveChildren) throws IOException {
    // pre save event to save all contents of the node:
    getMindMapController().firePreSaveEvent(this);
    XMLElement node = new XMLElement();
    node.setName(XMLElementAdapter.XML_NODE);
    /** fc, 12.6.2005: XML must not contain any zero characters. */
    String text = this.toString().replace('\u0000', ' ');
    node.setAttribute(XMLElementAdapter.XML_NODE_TEXT, text);
    XMLElement edge = (getEdge()).save();
    if (edge != null) {
      node.addChild(edge);
    }
    if (isFolded()) {
      node.setAttribute("FOLDED", "true");
    }
    // fc, 17.12.2003: Remove the left/right bug.
    // VVV save if and only if parent is root.
    if (!(isRoot()) && (getParentNode().isRoot())) {
      node.setAttribute("POSITION", isLeft() ? "left" : "right");
    }
    // the id is used, if there is a local hyperlink pointing to me or a
    // real link.
    String label = registry.getLabel(this);
    if (!sSaveOnlyIntrinsicallyNeededIds) {
      if (label != null) {
        node.setAttribute("ID", label);
      }
    }
    if (color != null) {
      node.setAttribute("COLOR", XmlTools.colorToXml(getColor()));
    }
    // new background color.
    if (getBackgroundColor() != null) {
      node.setAttribute("BACKGROUND_COLOR", XmlTools.colorToXml(getBackgroundColor()));
    }
    // ^ Here cannot be just getStyle() without super. This is because
    // getStyle's style depends on folded / unfolded. For example, when
    // real style is fork and node is folded, getStyle returns
    // MindMapNode.STYLE_BUBBLE, which is not what we want to save.
    // layout
    if (vGap != VGAP) {
      node.setAttribute("VGAP", Integer.toString(vGap));
    }
    if (hGap != HGAP) {
      node.setAttribute("HGAP", Integer.toString(hGap));
    }
    if (shiftY != 0) {
      node.setAttribute("VSHIFT", Integer.toString(shiftY));
    }
    // font
    if (font != null) {
      XMLElement fontElement = new XMLElement();
      fontElement.setName("font");
      if (font != null) {
        fontElement.setAttribute("NAME", font.getFamily());
      }
      if (font.getSize() != 0) {
        fontElement.setAttribute("SIZE", Integer.toString(font.getSize()));
      }
      if (isBold()) {
        fontElement.setAttribute("BOLD", "true");
      }
      if (isItalic()) {
        fontElement.setAttribute("ITALIC", "true");
      }
      node.addChild(fontElement);
    }
    for (int i = 0; i < getIcons().size(); ++i) {
      XMLElement iconElement = new XMLElement();
      iconElement.setName("icon");
      iconElement.setAttribute("BUILTIN", ((MindIcon) getIcons().get(i)).getName());
      node.addChild(iconElement);
    }
    if (saveChildren && childrenUnfolded().hasNext()) {
      node.writeWithoutClosingTag(writer);
      // recursive
      saveChildren(writer, registry, this, saveInvisible);
      node.writeClosingTag(writer);
    } else {
      node.write(writer);
    }
    return node;
  }

  public MindMapController getMindMapController() {
    return model.getMindMapController();
  }

  private void saveChildren(Writer writer, MindMapLinkRegistry registry, MindMapNode node, boolean saveHidden) throws IOException {
    for (ListIterator e = node.childrenUnfolded(); e.hasNext();) {
      MindMapNode child = (MindMapNode) e.next();
      if (saveHidden || child.isVisible()) {
        child.save(writer, registry, saveHidden, true);
      } else {
        saveChildren(writer, registry, child, saveHidden);
      }
    }
  }

  public int getShiftY() {
    return shiftY;
  }

  public boolean hasExactlyOneVisibleChild() {
    int count = 0;
    for (ListIterator i = childrenUnfolded(); i.hasNext();) {
      if (((MindMapNode) i.next()).isVisible()) {
        count++;
      }
      if (count == 2) {
        return false;
      }
    }
    return count == 1;
  }

  public boolean hasVisibleChilds() {
    for (ListIterator i = childrenUnfolded(); i.hasNext();) {
      if (((MindMapNode) i.next()).isVisible()) {
        return true;
      }
    }
    return false;
  }

  public int calcShiftY() {
    try {
      // return 0;
      return shiftY + (parent.hasExactlyOneVisibleChild() ? SHIFT : 0);
    } catch (NullPointerException e) {
      return 0;
    }
  }

  /**
   * @param shiftY
   *            The shiftY to set.
   */
  public void setShiftY(int shiftY) {
    this.shiftY = shiftY;
  }

  public int getHGap() {
    return hGap;
  }

  public void setHGap(int gap) {
    // hGap = Math.max(HGAP, gap);
    hGap = gap;
  }

  public int getVGap() {
    return vGap;
  }

  public void setVGap(int gap) {
    vGap = Math.max(gap, 0);
  }

  public boolean isVisible() {
    return true;
  }

  public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(TreeModelListener.class, l);
  }

  public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(TreeModelListener.class, l);
  }

  public EventListenerList getListeners() {
    return listenerList;
  }

}
