/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 05.06.2005
 *
 */
package org.rogach.simplymindmap.view;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import org.rogach.simplymindmap.util.Tools;

/**
 * @author dimitri 05.06.2005
 */
public class LeftNodeViewLayout extends NodeViewLayoutAdapter {
	static private LeftNodeViewLayout instance = null;

	protected void layout() {
		final int contentHeight = getChildContentHeight(true);
		int childVerticalShift = getChildVerticalShift(true);
		final int childHorizontalShift = getChildHorizontalShift();

		final int x = Math.max(getSpaceAround(), -childHorizontalShift);
		if (getView().isContentVisible()) {
			getContent().setVisible(true);
			final Dimension contentPreferredSize = getContent()
					.getPreferredSize();
			childVerticalShift += (contentPreferredSize.height - contentHeight) / 2;
			final int y = Math.max(getSpaceAround(), -childVerticalShift);
			getContent().setBounds(x, y, contentPreferredSize.width,
					contentPreferredSize.height);
		} else {
			getContent().setVisible(false);
			final int y = Math.max(getSpaceAround(), -childVerticalShift);
			getContent().setBounds(x, y, 0, contentHeight);
		}

		placeLeftChildren(childVerticalShift);
	}

	static LeftNodeViewLayout getInstance() {
		if (instance == null)
			instance = new LeftNodeViewLayout();
		return instance;
	}

	public void layoutNodeMotionListenerView(NodeMotionListenerView nodeMotionView) {
		NodeView nodeView = nodeMotionView.getMovedView();
		final JComponent content = nodeView.getContent();
		location.x = content.getWidth();
		location.y = 0;
		Tools.convertPointToAncestor(content, location, nodeMotionView.getParent());
		nodeMotionView.setLocation(location);
		nodeMotionView.setSize(LISTENER_VIEW_WIDTH, content.getHeight());
	}

	public Point getMainViewOutPoint(NodeView view, NodeView targetView,
			Point destinationPoint) {
		final MainView mainView = view.getMainView();
		return mainView.getLeftPoint();
	}

	public Point getMainViewInPoint(NodeView view) {
		final MainView mainView = view.getMainView();
		return mainView.getRightPoint();
	}

}
