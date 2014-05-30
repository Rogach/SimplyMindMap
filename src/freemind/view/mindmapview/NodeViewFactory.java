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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JComponent;

class NodeViewFactory {

	private static class ContentPane extends JComponent {
		static private LayoutManager layoutManager = new ContentPaneLayout();

		ContentPane() {
			setLayout(layoutManager);
		}
	}

	private static class ContentPaneLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void layoutContainer(Container parent) {
			final int componentCount = parent.getComponentCount();
			final int width = parent.getWidth();
			int y = 0;
			for (int i = 0; i < componentCount; i++) {
				final Component component = parent.getComponent(i);
				if (component.isVisible()) {
					final Dimension preferredCompSize = component
							.getPreferredSize();
					if (component instanceof MainView) {
						component.setBounds(0, y, width,
								preferredCompSize.height);
					} else {
						int x = (int) (component.getAlignmentX() * (width - preferredCompSize.width));
						component.setBounds(x, y, preferredCompSize.width,
								preferredCompSize.height);
					}
					y += preferredCompSize.height;
				}
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		public Dimension preferredLayoutSize(Container parent) {
			final Dimension prefSize = new Dimension(0, 0);
			final int componentCount = parent.getComponentCount();
			for (int i = 0; i < componentCount; i++) {
				final Component component = parent.getComponent(i);
				if (component.isVisible()) {
					final Dimension preferredCompSize = component
							.getPreferredSize();
					prefSize.height += preferredCompSize.height;
					prefSize.width = Math.max(prefSize.width,
							preferredCompSize.width);
				}
			}
			return prefSize;
		}

		public void removeLayoutComponent(Component comp) {
		}

	}

	private static NodeViewFactory factory;
	private EdgeView sharpBezierEdgeView;
	private EdgeView sharpLinearEdgeView;
	private EdgeView bezierEdgeView;
	private EdgeView linearEdgeView;

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
    return getBezierEdgeView();
	}

	private EdgeView getBezierEdgeView() {
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
		fireNodeViewCreated(newView);
		return newView;
	}

	MainView newMainView(MindMapNode model) {
		if (model.isRoot()) {
			return new RootMainView();
		} else {
      return new ForkMainView();
    }
	}

	private void fireNodeViewCreated(NodeView newView) {
	}

	JComponent newContentPane(NodeView view) {
		return new ContentPane();
	}
}
