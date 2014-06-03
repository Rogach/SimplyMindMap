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

package org.rogach.simplymindmap.modes.mindmapmode;

import org.rogach.simplymindmap.modes.MindMap;
import org.rogach.simplymindmap.modes.NodeAdapter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class represents a single Node of a Tree. It contains direct handles to
 * its parent and children and to its view.
 */
public class MindMapNodeModel extends NodeAdapter {

	public MindMapNodeModel(Object userObject) {
		this(userObject, null);
	}

	public MindMapNodeModel(Object userObject, MindMap map) {
		super(userObject, map);
		children = new LinkedList();
		setEdge(new MindMapEdgeModel(this));
	}

	public void collectColors(HashSet colors) {
		if (color != null) {
			colors.add(getColor());
		}
		for (ListIterator e = childrenUnfolded(); e.hasNext();) {
			((MindMapNodeModel) e.next()).collectColors(colors);
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
			final MindMapNodeModel child = (MindMapNodeModel) e.next();
			if (child.isVisible()) {
				child.saveTXT(fileout, depth + 1);
			} else {
				child.saveChildrenText(fileout, depth);
			}
		}
	}

}
