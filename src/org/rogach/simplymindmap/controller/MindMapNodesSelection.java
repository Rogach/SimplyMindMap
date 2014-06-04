/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on ???
 */
/*$Id: MindMapNodesSelection.java,v 1.2.18.2.12.3 2007/02/04 22:02:02 dpolivaev Exp $*/
package org.rogach.simplymindmap.controller;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MindMapNodesSelection implements Transferable, ClipboardOwner {

	private final String nodesContent;
	private final String stringContent;
	private String dropActionContent;
	private final List nodeIdsContent;
	public static DataFlavor mindMapNodesFlavor = null;
	/**
	 * fc, 7.8.2004: This is a quite interesting flavor, but how does it
	 * works???
	 */
	public static DataFlavor dropActionFlavor = null;
	/**
	 * This flavor contains the node ids only. Thus, it works only on the same
	 * map.
	 */
	public static DataFlavor copyNodeIdsFlavor = null;

	static {
		try {
			mindMapNodesFlavor = new DataFlavor(
					"text/freemind-nodes; class=java.lang.String");
			dropActionFlavor = new DataFlavor(
					"text/drop-action; class=java.lang.String");
			copyNodeIdsFlavor = new DataFlavor(
					"application/freemind-node-ids; class=java.util.List");
		} catch (Exception e) {
      Logger.getLogger(MindMapNodesSelection.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public MindMapNodesSelection(String nodesContent,
			String stringContent,
			String dropActionContent, List nodeIdsContent) {
		this.nodesContent = nodesContent;
		this.stringContent = stringContent;
		this.dropActionContent = dropActionContent;
		this.nodeIdsContent = nodeIdsContent;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException {
		if (flavor.equals(DataFlavor.stringFlavor)) {
			return stringContent;
		}
		if (flavor.equals(mindMapNodesFlavor)) {
			return nodesContent;
		}
		if (flavor.equals(dropActionFlavor)) {
			return dropActionContent;
		}
		if (flavor.equals(copyNodeIdsFlavor)) {
			return nodeIdsContent;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {
				DataFlavor.stringFlavor, mindMapNodesFlavor,
				dropActionFlavor, copyNodeIdsFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(DataFlavor.stringFlavor) && stringContent != null) {
			return true;
		}
		if (flavor.equals(mindMapNodesFlavor) && nodesContent != null) {
			return true;
		}
		if (flavor.equals(dropActionFlavor) && dropActionContent != null) {
			return true;
		}
		if (flavor.equals(copyNodeIdsFlavor) && nodeIdsContent != null) {
			return true;
		}
		return false;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	public void setDropAction(String dropActionContent) {
		this.dropActionContent = dropActionContent;
	}
}
