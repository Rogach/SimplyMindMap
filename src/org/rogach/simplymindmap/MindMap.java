package org.rogach.simplymindmap;

import java.awt.BorderLayout;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.impl.DefaultMindMapModel;
import org.rogach.simplymindmap.util.MindMapResources;
import org.rogach.simplymindmap.view.MapView;
import org.rogach.simplymindmap.view.MapViewScrollPane;

public class MindMap extends JPanel {
  
  private final MapView view;
  private final MindMapResources resources;
  
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
    
    MapViewScrollPane scrollPane = new MapViewScrollPane();
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setViewportView(view);
    
    BorderLayout layout = new BorderLayout();
    this.setLayout(layout);
    this.add(scrollPane, BorderLayout.CENTER);
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
  
}
