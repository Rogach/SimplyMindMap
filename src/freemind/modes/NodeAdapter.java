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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.main.FreeMind;
import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.NodeViewVisitor;
import java.util.logging.Logger;

/**
 * This class represents a single Node of a Tree. It contains direct handles to
 * its parent and children and to its view.
 */
public abstract class NodeAdapter implements MindMapNode {

	final static int SHIFT = -2;// height of the vertical shift between node and
								// its closest child
	public final static int HGAP = 20;// width of the horizontal gap that
										// contains the edges
	public final static int VGAP = 3;// height of the vertical gap between nodes

	public final static int LEFT_POSITION = -1;
	public final static int RIGHT_POSITION = 1;
	public final static int UNKNOWN_POSITION = 0;

	private HashSet activatedHooks;
	private List hooks;
	protected Object userObject = "no text";
	private String xmlText = "no text";
	private String link = null; // Change this to vector in future for full
								// graph support
	private TreeMap toolTip = null; // lazy, fc, 30.6.2005

	// these Attributes have default values, so it can be useful to directly
	// access them in
	// the save() method instead of using getXXX(). This way the stored file is
	// smaller and looks better.
	// (if the default is used, it is not stored) Look at mindmapmode for an
	// example.
	protected String style;
	/** stores the icons associated with this node. */
	protected Vector/* <MindIcon> */icons = null; // lazy, fc, 30.6.2005

	protected TreeMap /* of String to MindIcon s */stateIcons = null; // lazy, fc,
																	// 30.6.2005

	protected Color color;
	protected Color backgroundColor;
	protected boolean folded;
	private int position = UNKNOWN_POSITION;

	private int vGap = VGAP;
	private int hGap = HGAP;
	private int shiftY = 0;

	protected List children;
	private MindMapNode preferredChild;

	protected Font font;
	protected boolean underlined = false;

	private MindMapNode parent;
	/**
	 * the edge which leads to this node, only root has none In future it has to
	 * hold more than one view, maybe with a Vector in which the index specifies
	 * the MapView which contains the NodeViews
	 */
	private MindMapEdge edge;
	private Collection views = null;
	private FreeMindMain frame;
	private static final boolean ALLOWSCHILDREN = true;
	private static final boolean ISLEAF = false; // all nodes may have children

	private HistoryInformation historyInformation = null;
	// Logging:
	static protected java.util.logging.Logger logger;
	private MindMap map = null;
	private String noteText;
	private String xmlNoteText;
	private static boolean sSaveOnlyIntrinsicallyNeededIds = false;

	//
	// Constructors
	//

	protected NodeAdapter(MindMap map) {
		this(null, map);
	}

	protected NodeAdapter(Object userObject, MindMap map) {
		setText((String) userObject);
		hooks = null; // lazy, fc, 30.6.2005.
		activatedHooks = null; // lazy, fc, 30.6.2005
		if (logger == null)
			logger = Logger.getLogger(this.getClass().getName());
		// create creation time:
		setHistoryInformation(new HistoryInformation());
		this.map = map;

	}

	/**
     */
	public void setMap(MindMap map) {
		this.map = map;
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
			xmlText = null;
			return;
		}
		userObject = HtmlTools.makeValidXml(text);
		xmlText = HtmlTools.getInstance().toXhtml((String) userObject);
	}

	public final String getXmlText() {
		return xmlText;
	}

	public final void setXmlText(String pXmlText) {
		this.xmlText = HtmlTools.makeValidXml(pXmlText);
		userObject = HtmlTools.getInstance().toHtml(xmlText);
	}

	/* ************************************************************
	 * ******** Notes *******
	 * ************************************************************
	 */

	public final String getXmlNoteText() {
		return xmlNoteText;
	}

	public final String getNoteText() {
		// logger.info("Note html: " + noteText);
		return noteText;
	}

	public final void setXmlNoteText(String pXmlNoteText) {
		if (pXmlNoteText == null) {
			xmlNoteText = null;
			noteText = null;
			return;
		}
		this.xmlNoteText = HtmlTools.makeValidXml(pXmlNoteText);
		noteText = HtmlTools.getInstance().toHtml(xmlNoteText);
	}

	public final void setNoteText(String pNoteText) {
		if (pNoteText == null) {
			xmlNoteText = null;
			noteText = null;
			return;
		}
		this.noteText = HtmlTools.makeValidXml(pNoteText);
		this.xmlNoteText = HtmlTools.getInstance().toXhtml(noteText);
	}

	public String getPlainTextContent() {
		// Redefined in MindMapNodeModel.
		return toString();
	}

	public String getLink() {
		return link;
	}

	public String getShortText(ModeController controller) {
		String adaptedText = getPlainTextContent();
		// adaptedText = adaptedText.replaceAll("<html>", "");
		if (adaptedText.length() > 40)
			adaptedText = adaptedText.substring(0, 40) + " ...";
		return adaptedText;
	}

	public void setLink(String link) {
		if (link != null && link.startsWith("#")) {
			getMap().getLinkRegistry().registerLocalHyperlinkId(
					link.substring(1));
		}
		this.link = link;
	}

	public FreeMindMain getFrame() {
		return null;
	}

	//
	// Interface MindMapNode
	//

	//
	// get/set methods
	//

	public Collection getViewers() {
		if (views == null) {
			views = new LinkedList();
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

	/** A Node-Style like MindMapNode.STYLE_FORK or MindMapNode.STYLE_BUBBLE */
	public String getStyle() {
		String returnedString = style; /* Style string returned */
		if (style == null) {
			if (this.isRoot()) {
				returnedString = Resources.getInstance().getProperty(
						FreeMind.RESOURCES_ROOT_NODE_STYLE);
			} else {
				String stdstyle = Resources.getInstance().getProperty(
						FreeMind.RESOURCES_NODE_STYLE);
				if (stdstyle.equals(MindMapNode.STYLE_AS_PARENT)) {
					returnedString = getParentNode().getStyle();
				} else {
					returnedString = stdstyle;
				}
			}
		} else if (this.isRoot() && style.equals(MindMapNode.STYLE_AS_PARENT)) {
			returnedString = Resources.getInstance().getProperty(
					FreeMind.RESOURCES_ROOT_NODE_STYLE);
		} else if (style.equals(MindMapNode.STYLE_AS_PARENT)) {
			returnedString = getParentNode().getStyle();
		}

		// Handle the combined node style
		if (returnedString.equals(MindMapNode.STYLE_COMBINED)) {
			if (this.isFolded()) {
				return MindMapNode.STYLE_BUBBLE;
			} else {
				return MindMapNode.STYLE_FORK;
			}
		}
		return returnedString;
	}

	public boolean hasStyle() {
		return style != null;
	}

	/** The Foreground/Font Color */
	public Color getColor() {
		return color;
	}

	// ////
	// The set methods. I'm not sure if they should be here or in the
	// implementing class.
	// ///

	public void setStyle(String style) {
		this.style = style;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	// fc, 24.2.2004: background color:
	public Color getBackgroundColor() {
		return backgroundColor;
	};

	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	};

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
		font = (font != null) ? font : NodeView
				.getDefaultFont();
	}

	public void setBold(boolean bold) {
		if (bold != isBold()) {
			toggleBold();
		}
	}

	public void toggleBold() {
		establishOwnFont();
		setFont(
				new Font(font.getFamily(), font.getStyle() ^ Font.BOLD, font
						.getSize()));
	}

	public void setItalic(boolean italic) {
		if (italic != isItalic()) {
			toggleItalic();
		}
	}

	public void toggleItalic() {
		establishOwnFont();
		setFont(
				new Font(font.getFamily(), font.getStyle() ^ Font.ITALIC, font
						.getSize()));
	}

	public void setUnderlined(boolean underlined) {
		this.underlined = underlined;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public MindMapNode getParentNode() {
		return parent;
	}

	public void setFontSize(int fontSize) {
		establishOwnFont();
		setFont(
				new Font(font.getFamily(), font.getStyle(), fontSize));
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

	public boolean isUnderlined() { // not implemented
		return underlined;
	}

	public boolean isFolded() {
		return folded;
	}

	// fc, 24.9.2003:
	public List getIcons() {
		if (icons == null)
			return Collections.EMPTY_LIST;
		return icons;
	}

	public MindMap getMap() {
		return map;
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
	};

	// end, fc, 24.9.2003

	// public String getLabel() { return mLabel; }

	// public void setLabel(String newLabel) { mLabel = newLabel; /* bad hack:
	// registry fragen.*/ };

	// public Vector/* of NodeLinkStruct*/ getReferences() { return
	// mNodeLinkVector; };

	// public void removeReferenceAt(int i) {
	// if(mNodeLinkVector.size() > i) {
	// mNodeLinkVector.removeElementAt(i);
	// } else {
	// /* exception. */
	// }
	// }

	// public void addReference(MindMapLink mindMapLink) {
	// mNodeLinkVector.add(mindMapLink); };

	/**
	 * True iff one of node's <i>strict</i> descendants is folded. A node N is
	 * not its strict descendant - the fact that node itself is folded is not
	 * sufficient to return true.
	 */
	public boolean hasFoldedStrictDescendant() {

		for (ListIterator e = childrenUnfolded(); e.hasNext();) {
			NodeAdapter child = (NodeAdapter) e.next();
			if (child.isFolded() || child.hasFoldedStrictDescendant()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true, if one of its parents is folded. If itself is folded,
	 *         doesn't matter.
	 */
	public boolean hasFoldedParents() {
		if (isRoot())
			return false;
		if (getParentNode().isFolded()) {
			return true;
		}
		return getParentNode().hasFoldedParents();
	}

	public void setFolded(boolean folded) {
		this.folded = folded;
	}

	public MindMapNode shallowCopy() {
		try {
			// get XML from me.
			StringWriter writer = new StringWriter();
			this.save(writer, this.getMap().getLinkRegistry(), true, false);
			String result = writer.toString();
			HashMap IDToTarget = new HashMap();
			MindMapNode copy = this.getModeController().createNodeTreeFromXml(
					new StringReader(result), IDToTarget);
			copy.setFolded(false);
			return copy;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	//
	// other
	//

	public String toString() {
		return getText();
	}

	public boolean isDescendantOf(MindMapNode pParentNode) {
		if (this.isRoot())
			return false;
		else if (pParentNode == getParentNode())
			return true;
		else
			return getParentNode().isDescendantOf(pParentNode);
	}

	public boolean isRoot() {
		return (parent == null);
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

	public ListIterator childrenUnfolded() {
		return children != null ? children.listIterator()
				: Collections.EMPTY_LIST.listIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMapNode#sortedChildrenUnfolded()
	 */
	public ListIterator sortedChildrenUnfolded() {
		if (children == null)
			return null;
		LinkedList sorted = new LinkedList(children);
		/*
		 * Using this stable sort, we assure that the left nodes came in front
		 * of the right ones.
		 */
		Collections.sort(sorted, new Comparator() {

			public int compare(Object pO1, Object pO2) {
				return comp(((MindMapNode) pO2).isLeft(),
						((MindMapNode) pO1).isLeft());
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

	public ListIterator childrenFolded() {
		if (isFolded()) {
			return Collections.EMPTY_LIST.listIterator();
		}
		return childrenUnfolded();
	}

	public List getChildren() {
		return Collections.unmodifiableList((children != null) ? children
				: Collections.EMPTY_LIST);
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
		throw new UnsupportedOperationException(
				"Use childrenFolded or childrenUnfolded instead");
	}

	public boolean getAllowsChildren() {
		return ALLOWSCHILDREN;
	}

	public TreeNode getChildAt(int childIndex) {
		// fc, 11.12.2004: This is not understandable, that a child does not
		// exist if the parent is folded.
		// if (isFolded()) {
		// return null;
		// }
		return (TreeNode) children.get(childIndex);
	}

	public int getChildCount() {
		return children == null ? 0 : children.size();
	}

	// (PN)
	// public int getChildCount() {
	// if (isFolded()) {
	// return 0;
	// }
	// return children.size();
	// }
	// // Daniel: ^ The name of this method is confusing. It does nto convey
	// // the meaning, at least not to me.

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
				final NodeAdapter child = (NodeAdapter) getChildAt(i);
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
			if (!((MindMapNode) getChildAt(i)).isLeft())
				rightChildrenCount++;
			if (rightChildrenCount > getChildCount() / 2) {
				return true;
			}
		}
		return false;
	}

	//
	// Interface MutableTreeNode
	//

	// do all remove methods have to work recursively to make the
	// Garbage Collection work (Nodes in removed Sub-Trees reference each
	// other)?

	public void insert(MutableTreeNode child, int index) {
		logger.finest("Insert at " + index + " the node " + child);
		final MindMapNode childNode = (MindMapNode) child;
		if (index < 0) { // add to the end (used in xml load) (PN)
			index = getChildCount();
			children.add(index, child);
		} else { // mind preferred child :-)
			children.add(index, child);
			preferredChild = childNode;
		}
		child.setParent(this);
		recursiveCallAddChildren(this, childNode);
	}

	public void remove(int index) {
		MutableTreeNode node = (MutableTreeNode) children.get(index);
		remove(node);
	}

	public void remove(MutableTreeNode node) {
		if (node == this.preferredChild) { // mind preferred child :-) (PN)
			int index = children.indexOf(node);
			if (children.size() > index + 1) {
				this.preferredChild = (MindMapNode) (children.get(index + 1));
			} else {
				this.preferredChild = (index > 0) ? (MindMapNode) (children
						.get(index - 1)) : null;
			}
		}
		node.setParent(null);
		children.remove(node);
		// call remove child hook after removal.
		recursiveCallRemoveChildren(this, (MindMapNode) node, this);
	}

	private void recursiveCallAddChildren(MindMapNode node,
			MindMapNode addedChild) {
		if (!node.isRoot() && node.getParentNode() != null)
			recursiveCallAddChildren(node.getParentNode(), addedChild);
	}

	/**
	 * @param oldDad
	 *            the last dad node had.
	 */
	private void recursiveCallRemoveChildren(MindMapNode node,
			MindMapNode removedChild, MindMapNode oldDad) {
		if (!node.isRoot() && node.getParentNode() != null)
			recursiveCallRemoveChildren(node.getParentNode(), removedChild,
					oldDad);
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
			((NodeAdapter) parent).addToPathVector(pathVector);
		}
	}

	public int getNodeLevel() { // for cursor navigation within a level (PN)
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

	private void createStateIcons() {
		if (stateIcons == null) {
			stateIcons = new TreeMap();
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
		if (toolTip == null)
			return new TreeMap();
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
			if (toolTip.size() == 0)
				toolTip = null;
		} else {
			toolTip.put(key, string);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMapNode#getNodeId()
	 */
	public String getObjectId(ModeController controller) {
		return controller.getNodeID(this);
	}

	public XMLElement save(Writer writer, MindMapLinkRegistry registry,
			boolean saveInvisible, boolean saveChildren) throws IOException {
		// pre save event to save all contents of the node:
		getModeController().firePreSaveEvent(this);
		XMLElement node = new XMLElement();

		// if (!isNodeClassToBeSaved()) {
		node.setName(XMLElementAdapter.XML_NODE);
		// } else {
		// node.setName(XMLElementAdapter.XML_NODE_CLASS_PREFIX
		// + this.getClass().getName());
		// }

		/** fc, 12.6.2005: XML must not contain any zero characters. */
		String text = this.toString().replace('\0', ' ');
		if (!HtmlTools.isHtmlNode(text)) {
			node.setAttribute(XMLElementAdapter.XML_NODE_TEXT, text);
		} else {
			// save <content> tag:
			XMLElement htmlElement = new XMLElement();
			htmlElement.setName(XMLElementAdapter.XML_NODE_XHTML_CONTENT_TAG);
			htmlElement.setAttribute(XMLElementAdapter.XML_NODE_XHTML_TYPE_TAG,
					XMLElementAdapter.XML_NODE_XHTML_TYPE_NODE);
			htmlElement
					.setEncodedContent(convertToEncodedContent(getXmlText()));
			node.addChild(htmlElement);
		}
		if (getXmlNoteText() != null) {
			XMLElement htmlElement = new XMLElement();
			htmlElement.setName(XMLElementAdapter.XML_NODE_XHTML_CONTENT_TAG);
			htmlElement.setAttribute(XMLElementAdapter.XML_NODE_XHTML_TYPE_TAG,
					XMLElementAdapter.XML_NODE_XHTML_TYPE_NOTE);
			htmlElement
					.setEncodedContent(convertToEncodedContent(getXmlNoteText()));
			node.addChild(htmlElement);

		}
		// save additional info:
		if (getAdditionalInfo() != null) {
			node.setAttribute(XMLElementAdapter.XML_NODE_ENCRYPTED_CONTENT,
					getAdditionalInfo());
		}
		// ((MindMapEdgeModel)getEdge()).save(doc,node);

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
			node.setAttribute("COLOR", Tools.colorToXml(getColor()));
		}

		// new background color.
		if (getBackgroundColor() != null) {
			node.setAttribute("BACKGROUND_COLOR",
					Tools.colorToXml(getBackgroundColor()));
		}

		if (style != null) {
			node.setAttribute("STYLE", this.getStyle());
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
		// link
		if (getLink() != null) {
			node.setAttribute("LINK", getLink());
		}

		// history information, fc, 11.4.2005
		if (historyInformation != null) {
			node.setAttribute(XMLElementAdapter.XML_NODE_HISTORY_CREATED_AT,
					Tools.dateToString(getHistoryInformation().getCreatedAt()));
			node.setAttribute(
					XMLElementAdapter.XML_NODE_HISTORY_LAST_MODIFIED_AT, Tools
							.dateToString(getHistoryInformation()
									.getLastModifiedAt()));
		}
		// font
		if (font != null) {
			XMLElement fontElement = new XMLElement();
			fontElement.setName("font");

			if (font != null) {
				fontElement.setAttribute("NAME", font.getFamily());
			}
			if (font.getSize() != 0) {
				fontElement.setAttribute("SIZE",
						Integer.toString(font.getSize()));
			}
			if (isBold()) {
				fontElement.setAttribute("BOLD", "true");
			}
			if (isItalic()) {
				fontElement.setAttribute("ITALIC", "true");
			}
			if (isUnderlined()) {
				fontElement.setAttribute("UNDERLINE", "true");
			}
			node.addChild(fontElement);
		}
		for (int i = 0; i < getIcons().size(); ++i) {
			XMLElement iconElement = new XMLElement();
			iconElement.setName("icon");
			iconElement.setAttribute("BUILTIN",
					((MindIcon) getIcons().get(i)).getName());
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

	public static String convertToEncodedContent(String xmlText2) {
		String replace = HtmlTools.makeValidXml(xmlText2);
		return HtmlTools.unicodeToHTMLUnicodeEntity(replace, true);
	}

	public ModeController getModeController() {
		return map.getModeController();
	}

	private void saveChildren(Writer writer, MindMapLinkRegistry registry,
			NodeAdapter node, boolean saveHidden) throws IOException {
		for (ListIterator e = node.childrenUnfolded(); e.hasNext();) {
			NodeAdapter child = (NodeAdapter) e.next();
			if (saveHidden || child.isVisible())
				child.save(writer, registry, saveHidden, true);
			else
				saveChildren(writer, registry, child, saveHidden);
		}
	}

	public int getShiftY() {
		return shiftY;
	}

	public boolean hasExactlyOneVisibleChild() {
		int count = 0;
		for (ListIterator i = childrenUnfolded(); i.hasNext();) {
			if (((MindMapNode) i.next()).isVisible())
				count++;
			if (count == 2)
				return false;
		}
		return count == 1;
	}

	public boolean hasVisibleChilds() {
		for (ListIterator i = childrenUnfolded(); i.hasNext();) {
			if (((MindMapNode) i.next()).isVisible())
				return true;
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

	/**
     *
     */

	public void setAdditionalInfo(String info) {
	}

	public String getAdditionalInfo() {
		return null;
	}

	/** This method must be synchronized as the TreeMap isn't. */
	public synchronized void setStateIcon(String key, ImageIcon icon) {
		// logger.info("Set state of key:"+key+", icon "+icon);
		createStateIcons();
		if (icon != null) {
			stateIcons.put(key, icon);
		} else if (stateIcons.containsKey(key)) {
			stateIcons.remove(key);
		}
		if (stateIcons.size() == 0)
			stateIcons = null;
	}

	public Map getStateIcons() {
		if (stateIcons == null)
			return Collections.EMPTY_MAP;
		return Collections.unmodifiableSortedMap(stateIcons);
	}

	public HistoryInformation getHistoryInformation() {
		return historyInformation;
	}

	public void setHistoryInformation(HistoryInformation historyInformation) {
		this.historyInformation = historyInformation;
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

	EventListenerList listenerList = new EventListenerList();

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	public EventListenerList getListeners() {
		return listenerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.MindMapNode#acceptViewVisitor(freemind.view.mindmapview
	 * .NodeViewVisitor)
	 */
	public void acceptViewVisitor(NodeViewVisitor visitor) {
		final Iterator iterator = views.iterator();
		while (iterator.hasNext()) {
			visitor.visit((NodeView) iterator.next());
		}

	}
	//
	// Events
	//
}
