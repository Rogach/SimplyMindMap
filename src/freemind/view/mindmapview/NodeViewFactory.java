/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import java.awt.Container;

class NodeViewFactory {

	private static NodeViewFactory factory;
	private EdgeView bezierEdgeView;

	// Singleton
	private NodeViewFactory() {

	}

	static NodeViewFactory getInstance() {
		if (factory == null) {
			factory = new NodeViewFactory();
		}
		return factory;
	}

	EdgeView getEdge(NodeView newView) {
		if (bezierEdgeView == null) {
			bezierEdgeView = new BezierEdgeView();
		}
		return bezierEdgeView;
	}

  /**
	 * Factory method which creates the right NodeView for the model.
	 */
	NodeView newNodeView(MindMapNode model, int position, MapView map,
			Container parent) {
		NodeView newView = new NodeView(model, position, map, parent);

		if (model.isRoot()) {
			final MainView mainView = new RootMainView();
			newView.setMainView(mainView);
			newView.setLayout(VerticalRootNodeViewLayout.getInstance());

		} else {
			newView.setMainView(newMainView(model));
			if (newView.isLeft()) {
				newView.setLayout(LeftNodeViewLayout.getInstance());
			} else {
				newView.setLayout(RightNodeViewLayout.getInstance());
			}
		}

		model.addViewer(newView);
		newView.update();
		return newView;
	}

	MainView newMainView(MindMapNode model) {
		if (model.isRoot()) {
			return new RootMainView();
		} else {
      return new ForkMainView();
    }
	}
  
}
