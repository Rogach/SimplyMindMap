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


package freemind.modes.mindmapmode;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternEdgeColor;
import freemind.controller.actions.generated.instance.PatternEdgeStyle;
import freemind.controller.actions.generated.instance.PatternEdgeWidth;
import freemind.controller.actions.generated.instance.PatternIcon;
import freemind.controller.actions.generated.instance.PatternNodeBackgroundColor;
import freemind.controller.actions.generated.instance.PatternNodeColor;
import freemind.controller.actions.generated.instance.PatternNodeFontBold;
import freemind.controller.actions.generated.instance.PatternNodeFontItalic;
import freemind.controller.actions.generated.instance.PatternNodeFontName;
import freemind.controller.actions.generated.instance.PatternNodeFontSize;
import freemind.controller.actions.generated.instance.PatternNodeStyle;
import freemind.controller.actions.generated.instance.PatternNodeText;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FixedHTMLWriter;
import freemind.main.HtmlTools;
import freemind.main.ResourceKeys;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.BoldAction;
import freemind.modes.mindmapmode.actions.CopyAction;
import freemind.modes.mindmapmode.actions.CutAction;
import freemind.modes.mindmapmode.actions.DeleteChildAction;
import freemind.modes.mindmapmode.actions.EditAction;
import freemind.modes.mindmapmode.actions.FindAction;
import freemind.modes.mindmapmode.actions.FindAction.FindNextAction;
import freemind.modes.mindmapmode.actions.FontFamilyAction;
import freemind.modes.mindmapmode.actions.FontSizeAction;
import freemind.modes.mindmapmode.actions.IconAction;
import freemind.modes.mindmapmode.actions.ItalicAction;
import freemind.modes.mindmapmode.actions.MindMapActions;
import freemind.modes.mindmapmode.actions.ModeControllerActionHandler;
import freemind.modes.mindmapmode.actions.MoveNodeAction;
import freemind.modes.mindmapmode.actions.NewChildAction;
import freemind.modes.mindmapmode.actions.NewPreviousSiblingAction;
import freemind.modes.mindmapmode.actions.NewSiblingAction;
import freemind.modes.mindmapmode.actions.NodeColorAction;
import freemind.modes.mindmapmode.actions.NodeDownAction;
import freemind.modes.mindmapmode.actions.NodeGeneralAction;
import freemind.modes.mindmapmode.actions.NodeUpAction;
import freemind.modes.mindmapmode.actions.PasteAction;
import freemind.modes.mindmapmode.actions.RedoAction;
import freemind.modes.mindmapmode.actions.RemoveAllIconsAction;
import freemind.modes.mindmapmode.actions.RemoveIconAction;
import freemind.modes.mindmapmode.actions.SelectAllAction;
import freemind.modes.mindmapmode.actions.SelectBranchAction;
import freemind.modes.mindmapmode.actions.SingleNodeOperation;
import freemind.modes.mindmapmode.actions.ToggleChildrenFoldedAction;
import freemind.modes.mindmapmode.actions.ToggleFoldedAction;
import freemind.modes.mindmapmode.actions.UndoAction;
import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.UndoActionHandler;
import freemind.view.mindmapview.MainView;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

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
  
	public IconAction unknownIconAction = null;
	public RemoveIconAction removeLastIconAction = null;
	public RemoveAllIconsAction removeAllIconsAction = null;
	public MoveNodeAction moveNodeAction = null;
	
	public FindAction find = null;
	public FindNextAction findNext = null;
	public SelectBranchAction selectBranchAction = null;
	public SelectAllAction selectAllAction = null;

	// Extension Actions
	public Vector iconActions = new Vector(); // fc
  
	public MindMapController() {
		super();
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		// create action factory:
		actionFactory = new ActionFactory();
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

	}

	public boolean isUndoAction() {
		return undo.isUndoAction() || redo.isUndoAction();
	}

	private void createIconActions() {
		Vector iconNames = MindIcon.getAllIconNames();
		File iconDir = new File(Resources.getInstance().getFreemindDirectory(),
				"icons");
		if (iconDir.exists()) {
			String[] userIconArray = iconDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(".*\\.png");
				}
			});
			if (userIconArray != null)
				for (int i = 0; i < userIconArray.length; ++i) {
					String iconName = userIconArray[i];
					iconName = iconName.substring(0, iconName.length() - 4);
					if (iconName.equals("")) {
						continue;
					}
					iconNames.add(iconName);
				}
		}
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

	/**
	 * Enabled/Disabled all actions that are dependent on whether there is a map
	 * open or not.
	 */
	protected void setAllActions(boolean enabled) {
		logger.fine("setAllActions:" + enabled);
		super.setAllActions(enabled);
		// own actions
		increaseNodeFont.setEnabled(enabled);
		decreaseNodeFont.setEnabled(enabled);
		editLong.setEnabled(enabled);
		newSibling.setEnabled(enabled);
		newPreviousSibling.setEnabled(enabled);
		for (int i = 0; i < iconActions.size(); ++i) {
			((Action) iconActions.get(i)).setEnabled(enabled);
		}
		cut.setEnabled(enabled);
		copy.setEnabled(enabled);
		copySingle.setEnabled(enabled);
		paste.setEnabled(enabled);
		undo.setEnabled(enabled);
		redo.setEnabled(enabled);
		edit.setEnabled(enabled);
		newChild.setEnabled(enabled);
		toggleFolded.setEnabled(enabled);
		toggleChildrenFolded.setEnabled(enabled);
		italic.setEnabled(enabled);
		bold.setEnabled(enabled);
		find.setEnabled(enabled);
		findNext.setEnabled(enabled);
		nodeUp.setEnabled(enabled);
		nodeDown.setEnabled(enabled);
		deleteChild.setEnabled(enabled);
		nodeColor.setEnabled(enabled);
		removeLastIconAction.setEnabled(enabled);
		removeAllIconsAction.setEnabled(enabled);
		selectAllAction.setEnabled(enabled);
		selectBranchAction.setEnabled(enabled);
		moveNodeAction.setEnabled(enabled);
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

	public void blendNodeColor(MindMapNode node) {
		Color mapColor = getView().getBackground();
		Color nodeColor = node.getColor();
		if (nodeColor == null) {
			nodeColor = MapView.standardNodeTextColor;
		}
		setNodeColor(node,
				new Color((3 * mapColor.getRed() + nodeColor.getRed()) / 4,
						(3 * mapColor.getGreen() + nodeColor.getGreen()) / 4,
						(3 * mapColor.getBlue() + nodeColor.getBlue()) / 4));
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
				null, null, null, nodeList);
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

	static public void saveHTML(List mindMapNodes, Writer fileout)
			throws IOException {
		MindMapHTMLWriter htmlWriter = new MindMapHTMLWriter(fileout);
		htmlWriter.saveHTML(mindMapNodes);
	}

	/**
     */
	public void splitNode(MindMapNode node, int caretPosition, String newText) {
		if (node.isRoot()) {
			return;
		}
		// If there are children, they go to the node below
		String futureText = newText != null ? newText : node.toString();

		String[] strings = getContent(futureText, caretPosition);
		if (strings == null) { // do nothing
			return;
		}
		String newUpperContent = strings[0];
		String newLowerContent = strings[1];
		setNodeText(node, newUpperContent);

		MindMapNode parent = node.getParentNode();
		MindMapNode lowerNode = addNewNode(parent,
				parent.getChildPosition(node) + 1, node.isLeft());
		lowerNode.setColor(node.getColor());
		lowerNode.setFont(node.getFont());
		setNodeText(lowerNode, newLowerContent);

	}

	private String[] getContent(String text, int pos) {
		if (pos <= 0) {
			return null;
		}
		String[] strings = new String[2];
		if (text.startsWith("<html>")) {
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = new HTMLDocument();
			StringReader buf = new StringReader(text);
			try {
				kit.read(buf, doc, 0);
				final char[] firstText = doc.getText(0, pos).toCharArray();
				int firstStart = 0;
				int firstLen = pos;
				while ((firstStart < firstLen)
						&& (firstText[firstStart] <= ' ')) {
					firstStart++;
				}
				while ((firstStart < firstLen)
						&& (firstText[firstLen - 1] <= ' ')) {
					firstLen--;
				}
				int secondStart = 0;
				int secondLen = doc.getLength() - pos;
				final char[] secondText = doc.getText(pos, secondLen)
						.toCharArray();
				while ((secondStart < secondLen)
						&& (secondText[secondStart] <= ' ')) {
					secondStart++;
				}
				while ((secondStart < secondLen)
						&& (secondText[secondLen - 1] <= ' ')) {
					secondLen--;
				}
				if (firstStart == firstLen || secondStart == secondLen) {
					return null;
				}
				StringWriter out = new StringWriter();
				new FixedHTMLWriter(out, doc, firstStart, firstLen - firstStart)
						.write();
				strings[0] = out.toString();
				out = new StringWriter();
				new FixedHTMLWriter(out, doc, pos + secondStart, secondLen
						- secondStart).write();
				strings[1] = out.toString();
				return strings;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				freemind.main.Resources.getInstance().logException(e);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				freemind.main.Resources.getInstance().logException(e);
			}
		} else {
			if (pos >= text.length()) {
				return null;
			}
			strings[0] = text.substring(0, pos);
			strings[1] = text.substring(pos);
		}
		return strings;
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

	public String marshall(XmlAction action) {
		return Tools.marshall(action);
	}

	public XmlAction unMarshall(String inputString) {
		return Tools.unMarshall(inputString);
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

	public void clearNodeContents(MindMapNode pNode) {
		Pattern erasePattern = new Pattern();
		erasePattern.setPatternEdgeColor(new PatternEdgeColor());
		erasePattern.setPatternEdgeStyle(new PatternEdgeStyle());
		erasePattern.setPatternEdgeWidth(new PatternEdgeWidth());
		erasePattern.setPatternIcon(new PatternIcon());
		erasePattern
				.setPatternNodeBackgroundColor(new PatternNodeBackgroundColor());
		erasePattern.setPatternNodeColor(new PatternNodeColor());
		erasePattern.setPatternNodeFontBold(new PatternNodeFontBold());
		erasePattern.setPatternNodeFontItalic(new PatternNodeFontItalic());
		erasePattern.setPatternNodeFontName(new PatternNodeFontName());
		erasePattern.setPatternNodeFontSize(new PatternNodeFontSize());
		erasePattern.setPatternNodeStyle(new PatternNodeStyle());
		erasePattern.setPatternNodeText(new PatternNodeText());
	}

	public EditNoteToNodeAction createEditNoteToNodeAction(MindMapNode node,
			String text) {
		EditNoteToNodeAction nodeAction = new EditNoteToNodeAction();
		nodeAction.setNode(node.getObjectId(this));
		if (text != null
				&& (HtmlTools.htmlToPlain(text).length() != 0 || text
						.indexOf("<img") >= 0)) {
			nodeAction.setText(text);
		} else {
			nodeAction.setText(null);
		}
		return nodeAction;
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
