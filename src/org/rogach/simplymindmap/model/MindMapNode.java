package org.rogach.simplymindmap.model;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.view.NodeView;

public interface MindMapNode extends MutableTreeNode {
  // its closest child
  int HGAP = 20; // width of the horizontal gap that
  int LEFT_POSITION = -1;
  int RIGHT_POSITION = 1;
  int UNKNOWN_POSITION = 0;
  // contains the edges
  int VGAP = 3; // height of the vertical gap between nodes

  void add(MindMapNode child);
  //
  // Interface MutableTreeNode
  //

  void addIcon(MindIcon _icon, int position);

  void addTreeModelListener(TreeModelListener l);

  void addViewer(NodeView viewer);

  int calcShiftY();

  //
  // Interface TreeNode
  //
  /**
   * AFAIK there is no way to get an enumeration out of a linked list. So this
   * exception must be thrown, or we can't implement TreeNode anymore (maybe
   * we shouldn't?)
   */
  Enumeration children();

  ListIterator<MindMapNode> childrenFolded();

  ListIterator<MindMapNode> childrenUnfolded();

  void collectColors(HashSet<Color> colors);

  //
  // font handling
  //
  // Remark to setBold and setItalic implemetation
  //
  // Using deriveFont() is a bad idea, because it does not really choose
  // the appropriate face. For example, instead of choosing face
  // "Arial Bold", it derives the bold face from "Arial".
  // Node holds font only in the case that the font is not default.
  void establishOwnFont();

  boolean getAllowsChildren();

  // fc, 24.2.2004: background color:
  Color getBackgroundColor();

  TreeNode getChildAt(int childIndex);

  int getChildCount();

  int getChildPosition(MindMapNode childNode);

  List<MindMapNode> getChildren();

  /** The Foreground/Font Color */
  Color getColor();

  MindMapEdge getEdge();

  Font getFont();

  String getFontFamilyName();

  String getFontSize();

  int getHGap();

  // fc, 24.9.2003:
  List getIcons();

  int getIndex(TreeNode node);

  EventListenerList getListeners();

  MindMapController getMindMapController();

  AbstractMindMapModel getModel();

  int getNodeLevel();

  /*
   * (non-Javadoc)
   *
   * @see freemind.modes.MindMapNode#getNodeId()
   */
  String getObjectId(MindMapController controller);

  TreeNode getParent();

  MindMapNode getParentNode();

  /** Creates the TreePath recursively */
  TreePath getPath();

  String getPlainTextContent();

  int getShiftY();

  String getText();

  /**
   */
  SortedMap<String, String> getToolTip();

  int getVGap();

  Collection<NodeView> getViewers();

  boolean hasChildren();

  boolean hasExactlyOneVisibleChild();

  /**
   * True iff one of node's <i>strict</i> descendants is folded. A node N is
   * not its strict descendant - the fact that node itself is folded is not
   * sufficient to return true.
   */
  boolean hasFoldedStrictDescendant();

  boolean hasVisibleChilds();

  // do all remove methods have to work recursively to make the
  // Garbage Collection work (Nodes in removed Sub-Trees reference each
  // other)?
  void insert(MutableTreeNode child, int index);

  boolean isBold();

  boolean isDescendantOf(MindMapNode pParentNode);

  boolean isDescendantOfOrEqual(MindMapNode pParentNode);

  boolean isFolded();

  boolean isItalic();

  boolean isLeaf();

  // fc, 16.12.2003 left-right bug:
  boolean isLeft();

  boolean isNewChildLeft();

  boolean isRoot();

  boolean isVisible();

  void remove(int index);

  void remove(MutableTreeNode node);

  void removeFromParent();

  /** @return returns the number of remaining icons. */
  int removeIcon(int position);

  void removeTreeModelListener(TreeModelListener l);

  void removeViewer(NodeView viewer);

  XMLElement save(Writer writer, MindMapLinkRegistry registry, boolean saveInvisible, boolean saveChildren) throws IOException;

  void saveTXT(Writer fileout, int depth) throws IOException;

  void setBackgroundColor(Color color);

  void setBold(boolean bold);

  void setColor(Color color);

  void setEdge(MindMapEdge edge);

  void setFolded(boolean folded);

  void setFont(Font font);

  void setFontSize(int fontSize);

  void setHGap(int gap);

  void setItalic(boolean italic);

  void setLeft(boolean isLeft);

  void setModel(AbstractMindMapModel map);

  void setParent(MutableTreeNode newParent);

  void setParent(MindMapNode newParent);

  /**
   * @param shiftY
   *            The shiftY to set.
   */
  void setShiftY(int shiftY);

  void setText(String text);

  /**
   */
  void setToolTip(String key, String string);

  void setUserObject(Object object);

  void setVGap(int gap);

  /*
   * (non-Javadoc)
   *
   * @see freemind.modes.MindMapNode#sortedChildrenUnfolded()
   */
  ListIterator<MindMapNode> sortedChildrenUnfolded();

  //
  // other
  //
  String toString();

  void toggleBold();

  void toggleItalic();
  
  void addToPathVector(Vector<MindMapNode> pathVector);

  void setPosition(int position);

  void saveChildrenText(Writer fileout, int depth) throws IOException;

}
