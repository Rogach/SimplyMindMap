package org.rogach.simplymindmap.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.rogach.simplymindmap.util.Tools;

public class MindMapToolBar extends JToolBar {
  public MindMapToolBar(MindMapController controller) {
    super();
    
    this.setFloatable(false);
    
    this.add(controller.cut);
    this.add(controller.copy);
    this.add(controller.paste);
    this.addSeparator();
    this.add(controller.newChild);
    this.add(controller.italic);
    this.add(controller.bold);
    this.addSeparator();
    this.add(getFontFamilySelector(controller));
  }
  
  public JComboBox<String> getFontFamilySelector(final MindMapController controller) {
    final JComboBox<String> comboBox = new JComboBox<>(new Vector<String>(Tools.getAvailableFontFamilyNames()));
    comboBox.setMaximumSize(new Dimension(90, 50));
    comboBox.setSelectedItem(Tools.getDefaultFont(controller.getResources()).getFamily());
    comboBox.setFont(comboBox.getFont().deriveFont(11f));
    comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.fontFamily.actionPerformed((String) comboBox.getSelectedItem());
      }
    });
    return comboBox;
  }
}
