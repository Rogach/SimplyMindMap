package org.rogach.simplymindmap.controller.actions;

import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;

public class DecreaseNodeFontAction extends NodeGeneralAction {

  public DecreaseNodeFontAction(final MindMapController controller) {
    super(controller, "increase_node_font_size", null, new SingleNodeOperation() {
				public void apply(AbstractMindMapModel map, MindMapNode node) {
					controller.increaseFontSize(node, -1);
				}
			});
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_node_decrease_font_size")));
  }

}
