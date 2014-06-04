package org.rogach.simplymindmap.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import org.rogach.simplymindmap.model.MindMapNode;
import org.rogach.simplymindmap.util.Tools;
import org.rogach.simplymindmap.view.NodeView;

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
    final ActionListener fontChangeListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.fontFamily.actionPerformed((String) comboBox.getSelectedItem());
        controller.obtainFocusForSelected();
      }
    };
    comboBox.addActionListener(fontChangeListener);
    controller.registerNodeSelectionListener(new MindMapController.NodeSelectionListener() {

      @Override
      public void onUpdateNodeHook(MindMapNode node) {}

      @Override
      public void onFocusNode(NodeView node) {}

      @Override
      public void onLostFocusNode(NodeView node) {}

      @Override
      public void onSaveNode(MindMapNode node) {}

      @Override
      public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
        if (pIsSelected) {
          comboBox.removeActionListener(fontChangeListener);
          comboBox.setSelectedItem(controller.getSelected().getFontFamilyName());
          comboBox.addActionListener(fontChangeListener);
        }
      }
    }, false);
    return comboBox;
  }
  
  public JComboBox<String> getFontSizeSelector(final MindMapController controller) {
    String[] fontSizes = new String[] { "8", "10", "12", "14", "16", "18", "20", "24", "28" };
    final JComboBox<String> comboBox = new JComboBox<>(fontSizes);
    comboBox.setMaximumSize(new Dimension(40, 50));
    comboBox.setSelectedItem(controller.getSelected().getFontSize());
    final ActionListener fontSizeChangeListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.fontSize.actionPerformed((String) comboBox.getSelectedItem());
        controller.obtainFocusForSelected();
      }
    };
    comboBox.addActionListener(fontSizeChangeListener);
    controller.registerNodeSelectionListener(new MindMapController.NodeSelectionListener() {

      @Override
      public void onUpdateNodeHook(MindMapNode node) {}

      @Override
      public void onFocusNode(NodeView node) {}

      @Override
      public void onLostFocusNode(NodeView node) {}

      @Override
      public void onSaveNode(MindMapNode node) {}

      @Override
      public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
        if (pIsSelected) {
          comboBox.removeActionListener(fontSizeChangeListener);
          comboBox.setSelectedItem(controller.getSelected().getFontSize());
          comboBox.addActionListener(fontSizeChangeListener);
        }
      }
    }, false);
    return comboBox;
  }
}
