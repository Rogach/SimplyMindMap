package org.rogach.simplymindmap.controller;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.rogach.simplymindmap.controller.actions.AboutAction;

public class MindMapMenuBar extends JMenuBar {
  public MindMapMenuBar(MindMapController controller) {
    super();
    
    JMenu editMenu = new JMenu(controller.getResources().getText("edit"));
    muteAccelerator(editMenu.add(controller.selectAllAction));
    muteAccelerator(editMenu.add(controller.selectBranchAction));
    editMenu.add(new JSeparator());
    muteAccelerator(editMenu.add(controller.cut));
    muteAccelerator(editMenu.add(controller.copy));
    muteAccelerator(editMenu.add(controller.paste));
    editMenu.add(new JSeparator());
    muteAccelerator(editMenu.add(controller.edit));
    muteAccelerator(editMenu.add(controller.editLong));
    muteAccelerator(editMenu.add(controller.deleteChild));
    editMenu.add(new JSeparator());
    muteAccelerator(editMenu.add(controller.find));
    muteAccelerator(editMenu.add(controller.findNext));
    this.add(editMenu);
    
    JMenu viewMenu = new JMenu(controller.getResources().getText("menu_view"));
    muteAccelerator(viewMenu.add(controller.zoomIn));
    muteAccelerator(viewMenu.add(controller.zoomOut));
    muteAccelerator(viewMenu.add(controller.fitToPage));
    this.add(viewMenu);
    
    JMenu insertMenu = new JMenu(controller.getResources().getText("menu_insert"));
    muteAccelerator(insertMenu.add(controller.newChild));
    muteAccelerator(insertMenu.add(controller.newSibling));
    muteAccelerator(insertMenu.add(controller.newPreviousSibling));
    insertMenu.add(new JSeparator());
    
    JMenu iconMenu = new JMenu(controller.getResources().getText("icon_menu"));
    muteAccelerator(iconMenu.add(controller.iconSelectionAction));
    muteAccelerator(iconMenu.add(controller.removeLastIconAction));
    muteAccelerator(iconMenu.add(controller.removeAllIconsAction));
    insertMenu.add(iconMenu);
    this.add(insertMenu);
    
    JMenu formatMenu = new JMenu(controller.getResources().getText("menu_format"));
    muteAccelerator(formatMenu.add(controller.increaseNodeFont));
    muteAccelerator(formatMenu.add(controller.decreaseNodeFont));
    muteAccelerator(formatMenu.add(controller.italic));
    muteAccelerator(formatMenu.add(controller.bold));
    muteAccelerator(formatMenu.add(controller.nodeColor));
    this.add(formatMenu);
    
    JMenu navigateMenu = new JMenu(controller.getResources().getText("menu_navigate"));
    muteAccelerator(navigateMenu.add(controller.nodeUp));
    muteAccelerator(navigateMenu.add(controller.nodeDown));
    muteAccelerator(navigateMenu.add(controller.changeNodeLevelLeft));
    muteAccelerator(navigateMenu.add(controller.changeNodeLevelRight));
    navigateMenu.add(new JSeparator());
    muteAccelerator(navigateMenu.add(controller.toggleFolded));
    muteAccelerator(navigateMenu.add(controller.toggleChildrenFolded));
    this.add(navigateMenu);
    
    JMenu helpMenu = new JMenu(controller.getResources().getText("help"));
    helpMenu.add(new AboutAction(controller));
    this.add(helpMenu);
  }
  
  /**
   * Removes accelerator bindings from menu item (button still displays hint
   * about accelerator).
   * Motivation: since keybindings should only work on our mindmap, and shouldn't
   * spill out to other components, we need to remove them from menubar - but
   * we still need to display keybindings to user (so he doesn't need to read
   * the manual, which he is not going to read anyway).
   */
  private void muteAccelerator(JMenuItem menuItem) {
    InputMap inputMap = SwingUtilities.getUIInputMap(menuItem, JComponent.WHEN_IN_FOCUSED_WINDOW);
    if (inputMap != null) {
      for (KeyStroke ks : SwingUtilities.getUIInputMap(menuItem, JComponent.WHEN_IN_FOCUSED_WINDOW).keys()) {
        SwingUtilities.getUIInputMap(menuItem, JComponent.WHEN_IN_FOCUSED_WINDOW).remove(ks);
      }
    }
  }
}
