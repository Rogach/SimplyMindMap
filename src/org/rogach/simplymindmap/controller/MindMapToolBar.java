package org.rogach.simplymindmap.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
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
    this.add(getFontSizeSelector(controller));
  }
  
  public JComboBox<String> getFontFamilySelector(final MindMapController controller) {
    final JComboBox<String> comboBox = new JComboBox<>(new Vector<String>(Tools.getAvailableFontFamilyNames()));
    comboBox.setMaximumSize(new Dimension(90, 50));
    comboBox.setSelectedItem(controller.getSelected().getFontFamilyName());
    comboBox.setFont(comboBox.getFont().deriveFont(11f));
    comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.fontFamily.actionPerformed((String) comboBox.getSelectedItem());
      }
    });
    return comboBox;
  }
  
  public JComboBox<String> getFontSizeSelector(final MindMapController controller) {
    String[] fontSizes = new String[] { "8", "10", "12", "14", "16", "18", "20", "24", "28" };
    final JComboBox<String> comboBox = new JComboBox<>(fontSizes);
    comboBox.setMaximumSize(new Dimension(40, 50));
    comboBox.setSelectedItem(controller.getSelected().getFontSize());
    comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.fontSize.actionPerformed((String) comboBox.getSelectedItem());
      }
    });
    return comboBox;
  }
}
