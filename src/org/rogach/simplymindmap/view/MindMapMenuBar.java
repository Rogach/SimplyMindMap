package org.rogach.simplymindmap.view;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.AboutAction;

public class MindMapMenuBar extends JMenuBar {
  public MindMapMenuBar(MindMapController controller) {
    super();
    
    JMenu editMenu = new JMenu(controller.getResources().getText("edit"));
    editMenu.add(controller.selectAllAction);
    editMenu.add(controller.selectBranchAction);
    editMenu.add(new JSeparator());
    editMenu.add(controller.cut);
    editMenu.add(controller.copy);
    editMenu.add(controller.paste);
    editMenu.add(new JSeparator());
    editMenu.add(controller.edit);
    editMenu.add(controller.editLong);
    editMenu.add(controller.deleteChild);
    editMenu.add(new JSeparator());
    editMenu.add(controller.find);
    editMenu.add(controller.findNext);
    this.add(editMenu);
    
    JMenu viewMenu = new JMenu(controller.getResources().getText("menu_view"));
    viewMenu.add(controller.zoomIn);
    viewMenu.add(controller.zoomOut);
    viewMenu.add(controller.fitToPage);
    this.add(viewMenu);
    
    JMenu insertMenu = new JMenu(controller.getResources().getText("menu_insert"));
    insertMenu.add(controller.newChild);
    insertMenu.add(controller.newSibling);
    insertMenu.add(controller.newPreviousSibling);
    insertMenu.add(new JSeparator());
    
    JMenu iconMenu = new JMenu(controller.getResources().getText("icon_menu"));
    iconMenu.add(controller.iconSelectionAction);
    iconMenu.add(controller.removeLastIconAction);
    iconMenu.add(controller.removeAllIconsAction);
    iconMenu.add(new JSeparator());
    for (Action iconAction : controller.iconActions) {
      iconMenu.add(iconAction);
    }
    insertMenu.add(iconMenu);
    this.add(insertMenu);
    
    JMenu formatMenu = new JMenu(controller.getResources().getText("menu_format"));
    formatMenu.add(controller.increaseNodeFont);
    formatMenu.add(controller.decreaseNodeFont);
    formatMenu.add(controller.fontSize);
    formatMenu.add(controller.fontFamily);
    formatMenu.add(controller.italic);
    formatMenu.add(controller.bold);
    formatMenu.add(controller.nodeColor);
    this.add(formatMenu);
    
    JMenu navigateMenu = new JMenu(controller.getResources().getText("menu_navigate"));
    navigateMenu.add(controller.nodeUp);
    navigateMenu.add(controller.nodeDown);
    navigateMenu.add(controller.changeNodeLevelLeft);
    navigateMenu.add(controller.changeNodeLevelRight);
    navigateMenu.add(new JSeparator());
    navigateMenu.add(controller.toggleFolded);
    navigateMenu.add(controller.toggleChildrenFolded);
    this.add(navigateMenu);
    
    JMenu helpMenu = new JMenu(controller.getResources().getText("help"));
    helpMenu.add(new AboutAction(controller));
    this.add(helpMenu);
  }
}
