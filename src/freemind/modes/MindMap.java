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

import java.util.List;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public interface MindMap extends TreeModel {

	MindMapNode getRootNode();

	ModeController getModeController();

	void nodeChanged(TreeNode node);

	void nodeRefresh(TreeNode node);

	String getAsHTML(List mindMapNodes);

	TreeNode[] getPathToRoot(TreeNode node);

	/**
	 * @return returns the link registry associated with this mode, or null, if
	 *         no registry is present.
	 */
	MindMapLinkRegistry getLinkRegistry();

	void nodeStructureChanged(TreeNode node);

}
