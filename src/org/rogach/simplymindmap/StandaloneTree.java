package org.rogach.simplymindmap;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class StandaloneTree {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    
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
