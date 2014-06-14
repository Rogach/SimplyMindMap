package org.rogach.simplymindmap.controller;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class MindMapPopupMenu extends JPopupMenu {
  public MindMapPopupMenu(MindMapController controller) {
    super();
    
    this.add(controller.edit);
    this.add(controller.editLong);
    this.add(controller.deleteChild);
    this.add(new JSeparator());
    this.add(controller.cut);
    this.add(controller.copy);
    this.add(controller.paste);
    this.add(new JSeparator());
    this.add(controller.newChild);
    this.add(controller.newSibling);
    this.add(controller.newPreviousSibling);
    this.add(new JSeparator());
    this.add(controller.toggleFolded);
    this.add(controller.toggleChildrenFolded);
    this.add(new JSeparator());
    
    JMenu iconMenu = new JMenu(controller.getResources().getText("icon_menu"));
    iconMenu.add(controller.iconSelectionAction);
    iconMenu.add(controller.removeLastIconAction);
    iconMenu.add(controller.removeAllIconsAction);
    this.add(iconMenu);
    
    JMenu formatMenu = new JMenu(controller.getResources().getText("menu_format"));
    formatMenu.add(controller.increaseNodeFont);
    formatMenu.add(controller.decreaseNodeFont);
    formatMenu.add(controller.italic);
    formatMenu.add(controller.bold);
    formatMenu.add(controller.nodeColor);
    formatMenu.add(controller.nodeBackgroundColor);
    this.add(formatMenu);
  }
}
