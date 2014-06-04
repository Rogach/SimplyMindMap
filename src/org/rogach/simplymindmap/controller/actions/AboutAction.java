package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.rogach.simplymindmap.controller.MindMapController;

public class AboutAction extends AbstractAction {
  
  private final MindMapController controller;
  
  public AboutAction(MindMapController controller) {
    super(controller.getResources().getText("simplymindmap_about"), controller.getResources().getIcon("clone_original.png"));
    this.controller = controller;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JOptionPane.showMessageDialog(
            controller.getView(), 
            controller.getResources().getText("simplymindmap_about_text"), 
            controller.getResources().getText("simplymindmap_about"),
            JOptionPane.INFORMATION_MESSAGE
            );
  }
  
  
}
