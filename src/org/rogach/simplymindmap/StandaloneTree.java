package org.rogach.simplymindmap;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class StandaloneTree {
  public static void main(String[] args) {

    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      Logger.getLogger(StandaloneTree.class.getName()).log(Level.SEVERE, null, ex);
    }

    MindMap mindMap = new MindMap();

    JFrame frame = new JFrame();
    BorderLayout layout = new BorderLayout();
    frame.getContentPane().setLayout(layout);
    frame.getContentPane().add(mindMap, BorderLayout.CENTER);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    mindMap.getView().centerNode(mindMap.getView().getRoot());

    frame.setSize(800, 500);
    frame.setVisible(true);

    mindMap.getController().obtainFocusForSelected(); // eagerly grab focus for map, else it will require clicking before proper use
  }
}
