package org.rogach.simplymindmap;

import java.awt.BorderLayout;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.MindMapMenuBar;
import org.rogach.simplymindmap.controller.MindMapToolBar;
import org.rogach.simplymindmap.controller.listeners.DefaultUndoableActionListener;
import org.rogach.simplymindmap.controller.listeners.UndoableActionListener;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.impl.DefaultMindMapModel;
import org.rogach.simplymindmap.util.MindMapResources;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.MapViewScrollPane;

public class MindMap extends JPanel {
  
  private final MapView view;
  private final MindMapMenuBar menuBar;
  private final MindMapToolBar toolBar;
  private final MindMapResources resources;
  private final JPanel headerPanel;
  
  public MindMap() {
    this(new DefaultMindMapModel());
  }
  
  public MindMap(AbstractMindMapModel mapModel) {
    this(mapModel, new MindMapResources());
  }
  
  public MindMap(AbstractMindMapModel mapModel, Properties userProperties) {
    this(mapModel, new MindMapResources(userProperties));
  }
  
  public MindMap(AbstractMindMapModel mapModel, MindMapResources resources) {
    super();
    this.resources = resources;
    view = new MapView(mapModel, resources);
    view.selectAsTheOnlyOneSelected(view.getRoot());
    
    menuBar = new MindMapMenuBar(view.getController());
    toolBar = new MindMapToolBar(view.getController());
    
    headerPanel = new JPanel();
    headerPanel.setLayout(new BorderLayout());
    headerPanel.add(menuBar, BorderLayout.NORTH);
    headerPanel.add(toolBar, BorderLayout.CENTER);
    
    MapViewScrollPane scrollPane = new MapViewScrollPane();
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setViewportView(view);
    
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.add(headerPanel, BorderLayout.NORTH);

    this.setLayout(new BorderLayout());
    this.add(contentPanel, BorderLayout.CENTER);
  }
  
  public MapView getView() {
    return view;
  }
  
  public MindMapController getController() {
    return view.getController();
  }
  
  public AbstractMindMapModel getMapModel() {
    return view.getModel();
  }
  
  public MindMapResources getResources() {
    return resources;
  }
  
  public void setHeaderVisible(boolean visible) {
    headerPanel.setVisible(visible);
  }
  
  public void addUndoableActionListener(UndoableActionListener undoListener) {
    this.getController().getActionFactory().addUndoableActionListener(undoListener);
    if (undoListener instanceof DefaultUndoableActionListener) {
      DefaultUndoableActionListener defaultUndoListener = 
              (DefaultUndoableActionListener) undoListener;
      this.getController().undo.setUndoHandler(defaultUndoListener);
      this.getController().redo.setUndoHandler(defaultUndoListener);
      defaultUndoListener.setUndoAction(this.getController().undo);
      defaultUndoListener.setRedoAction(this.getController().redo);
      toolBar.setUndoActionsVisible(true);
    }
  }
  
  public void removeUndoableActionListener(UndoableActionListener undoListener) {
    this.getController().getActionFactory().removeUndoableActionListener(undoListener);
    if (undoListener instanceof DefaultUndoableActionListener) {
      toolBar.setUndoActionsVisible(false);
    }
  }
  
}
