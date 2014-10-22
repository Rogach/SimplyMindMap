package org.rogach.simplymindmap.view;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class MapViewScrollPane extends JScrollPane {

  public MapViewScrollPane() {
      this.getVerticalScrollBar().setUnitIncrement(32);
  }

  protected boolean processKeyBinding(KeyStroke pKs, KeyEvent pE, int pCondition, boolean pPressed) {
    /*
     * the scroll pane eats control page up and down. Moreover, the page
     * up and down itself is not very useful, as the map hops away too
     * far.
     */
    if (pE.getKeyCode() == KeyEvent.VK_PAGE_DOWN || pE.getKeyCode() == KeyEvent.VK_PAGE_UP) {
      return false;
    }
    return super.processKeyBinding(pKs, pE, pCondition, pPressed);
  }

  protected void validateTree() {
    final Component view = getViewport().getView();
    if (view != null) {
      view.validate();
    }
    super.validateTree();
  }

}
