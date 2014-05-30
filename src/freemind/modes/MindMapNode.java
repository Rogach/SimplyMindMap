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

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import freemind.main.XMLElement;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.NodeViewVisitor;

public interface MindMapNode extends MutableTreeNode {

	public static final String STYLE_BUBBLE = "bubble";
	public static final String STYLE_FORK = "fork";
	public static final String STYLE_COMBINED = "combined";
	public static final String STYLE_AS_PARENT = "as_parent";
	public static final String[] NODE_STYLES = new String[] { STYLE_FORK,
			STYLE_BUBBLE, STYLE_AS_PARENT, STYLE_COMBINED };

	/**
	 * @return the text representation of the nodes content. HTML is represented
	 *         as <html>....</html> see getXmlText
	 */
	String getText();

	/**
	 * Sets both text and xmlText.
	 */
	void setText(String text);

	/**
	 * @return the text representation of the nodes content as valid XML. HTML
	 *         is represented as <html>....</html> with proper tags (like \<br/\>
	 *         instead of \<br\>
	 *         and so on).
	 */
	String getXmlText();

	/**
	 * Sets both text and xmlText.
	 */
	void setXmlText(String structuredText);

	/**
	 * @return the text representation of the notes content as valid XML. HTML
	 *         is represented as <html>....</html> with proper tags (like <br/>
	 *         instead of <br>
	 *         and so on).
	 */
	String getXmlNoteText();

	/**
	 * Sets both noteText and xmlNoteText.
	 */
	void setXmlNoteText(String structuredNoteText);

	/**
	 * @return the text representation of the notes content as valid HTML 3.2.
	 */
	String getNoteText();

	/**
	 * Sets both noteText and xmlNoteText.
	 */
	void setNoteText(String noteText);

	/**
	 * @return returns the unique id of the node. It is generated using the
	 *         LinkRegistry.
	 */
	String getObjectId(ModeController controller);

	/**
	 * @return returns a ListIterator of all children of the node if the node is
	 *         unfolded. EMPTY_LIST_ITERATOR otherwise.
	 * */
	ListIterator childrenFolded();

	/**
	 * @return returns a ListIterator of all (and not only the unfolded ones!!)
	 *         children of the node.
	 * */
	ListIterator childrenUnfolded();

	/**
	 * @return returns a ListIterator of all (and not only the unfolded ones!!)
	 *         children of the node sorted in the way they occur (if called from root, this
	 *         has the effect to sort the children first left then right).
	 * */
	ListIterator sortedChildrenUnfolded();
	
	/**
	 * @return a list of (unmodifiable) children (all ones, folded and unfolded)
	 *         of type MindMapNode.
	 */
	List getChildren();

	boolean hasChildren();

	/** @return -1 if the argument childNode is not a child. */
	int getChildPosition(MindMapNode childNode);

	int getNodeLevel();

	/**
	 * returns a short textual description of the text contained in the node.
	 * Html is filtered out.
	 */
	String getShortText(ModeController controller);

	MindMapEdge getEdge();

	Color getColor();

	String getStyle();

	/**
	 * currently the style may be one of MindMapNode.STYLE_BUBBLE or
	 * MindMapNode.STYLE_FORK.
	 */
	void setStyle(String style);

	// returns false if and only if the style is inherited from parent
	boolean hasStyle();

	MindMapNode getParentNode();

	boolean isBold();

	boolean isItalic();

	boolean isUnderlined();

	Font getFont();

	String getFontSize();

	String getFontFamilyName();

	/**
	 * @return a collection of NodeView objects.
	 */
	Collection getViewers();

	void addViewer(NodeView viewer);

	void removeViewer(NodeView viewer);

	String toString();

	String getPlainTextContent();

	TreePath getPath();

	/**
	 * Returns whether the argument is parent or parent of one of the grandpa's
	 * of this node. (transitive)
	 */
	boolean isDescendantOf(MindMapNode node);

	/**
	 * If the test node is identical or in the same family and elder as the
	 * object. node.isChild..(parent) == true means: parent -> .. -> node exists
	 * in the tree.
	 * 
	 * @see isDecendantOf
	 */
	boolean isDescendantOfOrEqual(MindMapNode pParentNode);

	boolean isRoot();

	boolean isFolded();

	boolean isLeft();

	void setLeft(boolean isLeft);

	void setFolded(boolean folded);

	void setFont(Font font);

	void setShiftY(int y);

	int getShiftY();

	int calcShiftY();

	void setVGap(int i);

	int getVGap();

	void setHGap(int i);

	int getHGap();

	void setFontSize(int fontSize);

	void setColor(Color color);

	// fc, 06.10.2003:
	/** Is a vector of MindIcon s */
	List getIcons();

	void addIcon(MindIcon icon, int position);

	/* @return returns the new amount of icons. */
	int removeIcon(int position);

	// end, fc, 24.9.2003

	// end clouds.

	// fc, 24.2.2004: background color:
	Color getBackgroundColor();

	void setBackgroundColor(Color color);

	// tooltips,fc 29.2.2004
	void setToolTip(String key, String tip);

	SortedMap getToolTip();

	// additional info, fc, 15.12.2004

	/**
	 * This method can be used to store non-visual additions to a node.
	 * Currently, it is used for encrypted nodes to store the encrypted content.
	 */
	void setAdditionalInfo(String info);

	/**
	 * Is only used to store encrypted content of an encrypted mind map node.
	 * 
	 * @see MindMapNode.setAdditionalInfo(String)
	 */
	public String getAdditionalInfo();

	/**
	 * @return a flat copy of this node including all extras like notes, etc.
	 *         But the children are not copied!
	 */
	MindMapNode shallowCopy();

	/**
	 * @param saveHidden
	 *            TODO: Seems not to be used. Remove or fill with live.
	 * @param saveChildren
	 *            if true, the save recurses to all of the nodes children.
	 */
	public XMLElement save(Writer writer, MindMapLinkRegistry registry,
			boolean saveHidden, boolean saveChildren) throws IOException;

	// fc, 10.2.2005:
	/**
	 * State icons are icons that are not saved. They indicate that this node is
	 * special.
	 */
	Map getStateIcons();

	/**
	 * @param icon
	 *            use null to remove the state icon. Then it is not required,
	 *            that the key already exists.
	 */
	void setStateIcon(String key, ImageIcon icon);

	// fc, 11.4.2005:
	HistoryInformation getHistoryInformation();

	void setHistoryInformation(HistoryInformation historyInformation);

	boolean isVisible();

	/**
	 * @return true, if there is exactly one visible child.
	 */
	boolean hasExactlyOneVisibleChild();

	/**
	 * @return true, if there is at least one visible child.
	 */
	boolean hasVisibleChilds();
	
	MindMap getMap();

	public void addTreeModelListener(TreeModelListener l);

	public void removeTreeModelListener(TreeModelListener l);

	public void acceptViewVisitor(NodeViewVisitor visitor);

	EventListenerList getListeners();

	boolean isNewChildLeft();

	/**
	 * Some nodes can't get new children or have other changes (encrypted nodes
	 * for example).
	 */
	boolean isWriteable();

	/**
	 * @return true, if one of its parents is folded. If itself is folded, doesn't matter.
	 */
	boolean hasFoldedParents();
}
