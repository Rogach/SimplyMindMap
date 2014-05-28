package freemind.main;

import freemind.controller.Controller;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapMode;
import freemind.view.mindmapview.MapView;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.JFrame;

public class StandaloneTree {
  public static void main(String[] args) {
    Properties defaultPreferences = readDefaultPreferences();
    
    MindMapMode mindMapMode = new MindMapMode();
    mindMapMode.init();
    
    FreeMindCommon common = new FreeMindCommon(defaultPreferences);
    ModeController mc = new MindMapController(mindMapMode);
    MindMap m = new MindMapMapModel(common, mc);
    
    MapView mapView = new MapView(m, defaultPreferences);
    
    JFrame frame = new JFrame();
    // need scrollpane to wrap mind map
    frame.setContentPane(mapView);
    frame.setSize(500, 500);
    frame.setVisible(true);
  }
  
  public static Properties readDefaultPreferences() {
    String propsLoc = "freemind.properties";
    URL defaultPropsURL =
        StandaloneTree.class.getClassLoader().getResource(propsLoc);
    Properties props = new Properties();
    try {
      InputStream in = defaultPropsURL.openStream();
      props.load(in);
      in.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("Panic! Error while loading default properties.");
    }
    return props;
  }
}
