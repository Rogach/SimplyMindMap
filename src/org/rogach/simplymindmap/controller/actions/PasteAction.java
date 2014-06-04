/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 09.05.2004
 */

package org.rogach.simplymindmap.controller.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.MindMapNodesSelection;
import org.rogach.simplymindmap.controller.actions.instance.PasteNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.TransferableContent;
import org.rogach.simplymindmap.controller.actions.instance.UndoPasteNodeAction;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;
import org.rogach.simplymindmap.controller.actions.xml.ActorXml;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.nanoxml.XMLParseException;
import org.rogach.simplymindmap.util.Tools;

public class PasteAction extends AbstractAction implements ActorXml {

	private static java.util.logging.Logger logger;
	private final MindMapController controller;
	private UndoPasteHandler mUndoPasteHandler;

	public PasteAction(MindMapController controller) {
		super(controller.getResources().getText("paste"), controller.getResources().getIcon("editpaste.png"));
		this.controller = controller;
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}

		this.controller.getActionFactory().registerActor(this,
				getDoActionClass());

		// special undo handler for paste.
		mUndoPasteHandler = new UndoPasteHandler(controller);
		this.controller.getActionFactory().registerActor(
				mUndoPasteHandler, UndoPasteNodeAction.class);

	}

	public void actionPerformed(ActionEvent e) {
		Transferable clipboardContents = this.controller
				.getClipboardContents();
		MindMapNode selectedNode = this.controller.getSelected();
		this.controller.paste(clipboardContents, selectedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		PasteNodeAction pasteAction = (PasteNodeAction) action;
		_paste(getTransferable(pasteAction.getTransferableContent()),
				controller.getNodeFromID(pasteAction.getNode()),
				pasteAction.getAsSibling(), pasteAction.getIsLeft());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return PasteNodeAction.class;
	}

	/**
	 * @param t
	 * @param coord
	 * @param pUndoAction
	 *            is filled automatically when not null.
	 * @return a new PasteNodeAction.
	 */
	public PasteNodeAction getPasteNodeAction(Transferable t,
			NodeCoordinate coord, UndoPasteNodeAction pUndoAction) {
		PasteNodeAction pasteAction = new PasteNodeAction();
		final String targetId = controller.getNodeID(coord.target);
		pasteAction.setNode(targetId);
		pasteAction.setTransferableContent(getTransferableContent(t,
				pUndoAction));
		pasteAction.setAsSibling(coord.asSibling);
		pasteAction.setIsLeft(coord.isLeft);
		if (pUndoAction != null) {
			pUndoAction.setNode(targetId);
			pUndoAction.setAsSibling(coord.asSibling);
			pUndoAction.setIsLeft(coord.isLeft);
		}
		return pasteAction;
	}

	/** URGENT: Change this method. */
	public void paste(MindMapNode node, MindMapNode parent) {
		if (node != null) {
			insertNodeInto(node, parent);
			controller.nodeStructureChanged(parent);
		}
	}

	/**
	 * @param t
	 *            the content
	 * @param target
	 *            where to add the content
	 * @param asSibling
	 *            if true, the content is added beside the target, otherwise as
	 *            new children
	 * @param isLeft
	 *            if something is pasted as a sibling to root, it must be
	 *            decided on which side of root
	 * @return true, if successfully executed.
	 */
	public boolean paste(Transferable t, MindMapNode target, boolean asSibling,
			boolean isLeft) {
		UndoPasteNodeAction undoAction = new UndoPasteNodeAction();
		PasteNodeAction pasteAction;
		pasteAction = getPasteNodeAction(t, new NodeCoordinate(target,
				asSibling, isLeft), undoAction);
		// Undo-action
		/*
		 * how to construct the undo action for a complex paste? a) Paste pastes
		 * a number of new nodes that are adjacent. This number should be
		 * determined.
		 * 
		 * 
		 * d) But, as there are many possibilities which data flavor is pasted,
		 * it has to be determined before, which one will be taken.
		 */
		return controller.doTransaction("paste",
				new ActionPair(pasteAction, undoAction));
	}

	public static class NodeCoordinate {

		public MindMapNode target;
		public boolean asSibling;
		public boolean isLeft;

		public NodeCoordinate(MindMapNode target, boolean asSibling,
				boolean isLeft) {
			this.target = target;
			this.asSibling = asSibling;
			this.isLeft = isLeft;
		}

		public MindMapNode getNode() {
			if (asSibling) {
				MindMapNode parentNode = target.getParentNode();
				return (MindMapNode) parentNode.getChildAt(parentNode
						.getChildPosition(target) - 1);
			} else {
				logger.finest("getChildCount = " + target.getChildCount()
						+ ", target = " + target);
				return (MindMapNode) target
						.getChildAt(target.getChildCount() - 1);
			}
		}

		public NodeCoordinate(MindMapNode node, boolean isLeft) {
			this.isLeft = isLeft;
			MindMapNode parentNode = node.getParentNode();
			int childPosition = parentNode.getChildPosition(node);
			if (childPosition == parentNode.getChildCount() - 1) {
				target = parentNode;
				asSibling = false;
			} else {
				target = (MindMapNode) parentNode.getChildAt(childPosition + 1);
				asSibling = true;
			}
		}
	}

	private interface DataFlavorHandler {

		void paste(Object TransferData, MindMapNode target, boolean asSibling,
				boolean isLeft, Transferable t)
				throws UnsupportedFlavorException, IOException;

		DataFlavor getDataFlavor();
	}

	private class MindMapNodesFlavorHandler implements DataFlavorHandler {

		public void paste(Object TransferData, MindMapNode target,
				boolean asSibling, boolean isLeft, Transferable t) {
			String textFromClipboard = (String) TransferData;
			if (textFromClipboard != null) {
				String[] textLines = textFromClipboard
						.split(MindMapController.NODESEPARATOR);
				// and now? paste it:
				String mapContent = AbstractMindMapModel.MAP_INITIAL_START
						+ "1.0.1" + "\"><node TEXT=\"DUMMY\">";
				for (int j = 0; j < textLines.length; j++) {
					mapContent += textLines[j];
				}
				mapContent += "</node></map>";
				// logger.info("Pasting " + mapContent);
				try {
					MindMapNode node = controller.getMapModel()
							.loadTree(
									new AbstractMindMapModel.StringReaderCreator(
											mapContent), false);
					for (ListIterator i = node.childrenUnfolded(); i.hasNext();) {
						MindMapNode importNode = (MindMapNode) i
								.next();
						insertNodeInto(importNode, target, asSibling, isLeft,
								true);
						// addUndoAction(importNode);
					}
				} catch (XMLParseException | IOException e) {
          Logger.getLogger(PasteAction.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}

		public DataFlavor getDataFlavor() {
			return MindMapNodesSelection.mindMapNodesFlavor;
		}
	}

	private class StringFlavorHandler implements DataFlavorHandler {

		public void paste(Object TransferData, MindMapNode target,
				boolean asSibling, boolean isLeft, Transferable t)
				throws UnsupportedFlavorException, IOException {
			// System.err.println("stringFlavor");
			pasteStringWithoutRedisplay(t, target, asSibling, isLeft);
		}

		public DataFlavor getDataFlavor() {
			return DataFlavor.stringFlavor;
		}
	}

  private void _paste(Transferable t, MindMapNode target, boolean asSibling,
			boolean isLeft) {
		if (t == null) {
			return;
		}
		// Uncomment to print obtained data flavors

		/*
		 * DataFlavor[] fl = t.getTransferDataFlavors(); for (int i = 0; i <
		 * fl.length; i++) { System.out.println(fl[i]); }
		 */
		DataFlavorHandler[] dataFlavorHandlerList = getFlavorHandlers();
		for (int i = 0; i < dataFlavorHandlerList.length; i++) {
			DataFlavorHandler handler = dataFlavorHandlerList[i];
			DataFlavor flavor = handler.getDataFlavor();
			if (t.isDataFlavorSupported(flavor)) {
				try {
					handler.paste(t.getTransferData(flavor), target, asSibling,
							isLeft, t);
					break;
				} catch (UnsupportedFlavorException | IOException e) {
					Logger.getLogger(PasteAction.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}
	}

	/**
     */
	private DataFlavorHandler[] getFlavorHandlers() {
		DataFlavorHandler[] dataFlavorHandlerList = new DataFlavorHandler[] {
				new MindMapNodesFlavorHandler(),
				new StringFlavorHandler()  };
		// %%% Make dependent on an option?: new HtmlFlavorHandler(),
		return dataFlavorHandlerList;
	}

	public MindMapNode pasteXMLWithoutRedisplay(String pasted,
			MindMapNode target, boolean asSibling, boolean changeSide,
			boolean isLeft, HashMap<String, MindMapNode> pIDToTarget) throws XMLParseException {
		// Call nodeStructureChanged(target) after this function.
		logger.fine("Pasting " + pasted + " to " + target);
		try {
			MindMapNode node = (MindMapNode) controller
					.createNodeTreeFromXml(new StringReader(pasted),
							pIDToTarget);
			insertNodeInto(node, target, asSibling, isLeft, changeSide);
			return node;
		} catch (IOException ee) {
			Logger.getLogger(PasteAction.class.getName()).log(Level.SEVERE, null, ee);
			return null;
		}
	}

	private void insertNodeInto(MindMapNode node, MindMapNode target,
			boolean asSibling, boolean isLeft, boolean changeSide) {
		MindMapNode parent;
		if (asSibling) {
			parent = target.getParentNode();
		} else {
			parent = target;
		}
		if (changeSide) {
			node.setParent(parent);
			node.setLeft(isLeft);
		}
		// now, the import is finished. We can inform others about the new
		// nodes:
		if (asSibling) {
			insertNodeInto(node, parent, parent.getChildPosition(target));
		} else {
			insertNodeInto(node, target);
		}
	}

	static final Pattern nonLinkCharacter = Pattern.compile("[ \n()'\",;]");

	/**
	 * Paste String (as opposed to other flavours)
	 * 
	 * Split the text into lines; determine the new tree structure by the number
	 * of leading spaces in lines. In case that trimmed line starts with
	 * protocol (http:, https:, ftp:), create a link with the same content.
	 * 
	 * If there was only one line to be pasted, return the pasted node, null
	 * otherwise.
	 * 
	 * @param isLeft
	 *            TODO
	 */
	private MindMapNode pasteStringWithoutRedisplay(Transferable t,
			MindMapNode parent, boolean asSibling, boolean isLeft)
			throws UnsupportedFlavorException, IOException {

		String textFromClipboard = (String) t
				.getTransferData(DataFlavor.stringFlavor);
		Pattern mailPattern = Pattern.compile("([^@ <>\\*']+@[^@ <>\\*']+)");

		String[] textLines = textFromClipboard.split("\n");

		MindMapNode realParent = null;
		if (asSibling) {
			// When pasting as sibling, we use virtual node as parent. When the
			// pasting to
			// virtual node is completed, we insert the children of that virtual
			// node to
			// the parent of real parent.
			realParent = parent;
      parent = controller.getMapModel().newNode(null);
		}

		ArrayList<MindMapNode> parentNodes = new ArrayList<>();
		ArrayList<Integer> parentNodesDepths = new ArrayList<>();

		parentNodes.add(parent);
		parentNodesDepths.add(new Integer(-1));

		String[] linkPrefixes = { "http://", "ftp://", "https://" };

		MindMapNode pastedNode = null;

		for (int i = 0; i < textLines.length; ++i) {
			String text = textLines[i];
			text = text.replaceAll("\t", "        ");
			if (text.matches(" *")) {
				continue;
			}

			int depth = 0;
			while (depth < text.length() && text.charAt(depth) == ' ') {
				++depth;
			}
			String visibleText = text.trim();

			// If the text is a recognizable link (e.g.
			// http://www.google.com/index.html),
			// make it more readable by look nicer by cutting off obvious prefix
			// and other
			// transforamtions.

			if (visibleText.matches("^http://(www\\.)?[^ ]*$")) {
				visibleText = visibleText.replaceAll("^http://(www\\.)?", "")
						.replaceAll("(/|\\.[^\\./\\?]*)$", "")
						.replaceAll("((\\.[^\\./]*\\?)|\\?)[^/]*$", " ? ...")
						.replaceAll("_|%20", " ");
				String[] textParts = visibleText.split("/");
				visibleText = "";
				for (int textPartIdx = 0; textPartIdx < textParts.length; textPartIdx++) {
					if (textPartIdx > 0) {
						visibleText += " > ";
					}
					visibleText += textPartIdx == 0 ? textParts[textPartIdx]
							: Tools.firstLetterCapitalized(textParts[textPartIdx]
									.replaceAll("^~*", ""));
				}
			}

			MindMapNode node = controller.newNode(visibleText);
			if (textLines.length == 1) {
				pastedNode = node;
			}

			// Determine parent among candidate parents
			// Change the array of candidate parents accordingly

			for (int j = parentNodes.size() - 1; j >= 0; --j) {
				if (depth > ((Integer) parentNodesDepths.get(j)).intValue()) {
					for (int k = j + 1; k < parentNodes.size(); ++k) {
						MindMapNode n = (MindMapNode) parentNodes.get(k);
						if (n.getParentNode() == parent) {
							// addUndoAction(n);
						}
						parentNodes.remove(k);
						parentNodesDepths.remove(k);
					}
					MindMapNode target = (MindMapNode) parentNodes.get(j);
					node.setLeft(isLeft);
					insertNodeInto(node, target);
					parentNodes.add(node);
					parentNodesDepths.add(new Integer(depth));
					break;
				}
			}
		}

		for (int k = 0; k < parentNodes.size(); ++k) {
			MindMapNode n = (MindMapNode) parentNodes.get(k);
			if (n.getParentNode() == parent) {
				// addUndoAction(n);
			}
		}
		return pastedNode;
	}

	/**
     */
	private void insertNodeInto(MindMapNode node, MindMapNode parent, int i) {
		controller.insertNodeInto(node, parent, i);
	}

	private void insertNodeInto(MindMapNode node, MindMapNode parent) {
		controller.insertNodeInto(node, parent);
	}

	private TransferableContent getTransferableContent(Transferable t,
			UndoPasteNodeAction pUndoAction) {
		boolean amountAlreadySet = false;
		try {
			TransferableContent trans = new TransferableContent();
			if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
				String textFromClipboard;
				textFromClipboard = (String) t
						.getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
				trans.setTransferable(textFromClipboard.replaceAll("\u0000", "").replaceAll("&#0;", ""));
				if (pUndoAction != null && !amountAlreadySet) {
					pUndoAction
							.setNodeAmount(Tools.countOccurrences(
									textFromClipboard,
									MindMapController.NODESEPARATOR) + 1);
					amountAlreadySet = true;
				}
			}
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String textFromClipboard;
				textFromClipboard = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				trans.setTransferableAsPlainText(textFromClipboard.replaceAll("\u0000", "")
						.replaceAll("&#0;", ""));
				if (pUndoAction != null && !amountAlreadySet) {
					// determine amount of new nodes using the algorithm:
					final int childCount = determineAmountOfNewNodes(t);
					pUndoAction.setNodeAmount(childCount);
					amountAlreadySet = true;
				}
			}
			return trans;
		} catch (UnsupportedFlavorException | IOException e) {
			Logger.getLogger(PasteAction.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/*
	 * TODO: This is a bit dirty here. Better would be to separate the algorithm
	 * from the node creation and use the pure algo.
	 */
	protected int determineAmountOfNewNodes(Transferable t)
			throws UnsupportedFlavorException, IOException {
		// create a new node for testing purposes.
		MindMapNode parent = controller.getMapModel().newNode(null);
		pasteStringWithoutRedisplay(t, parent, false, false);
		final int childCount = parent.getChildCount();
		return childCount;
	}

	private Transferable getTransferable(TransferableContent trans) {
		Transferable copy = new MindMapNodesSelection(trans.getTransferable(),
				trans.getTransferableAsPlainText(),
				trans.getTransferableAsDrop(), null);
		return copy;
	}

}
