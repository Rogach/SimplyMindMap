package freemind.main;

import freemind.modes.MindMap;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.view.MapView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class StandaloneTree {
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    
    Properties defaultPreferences = readDefaultPreferences();
    
    FreeMindCommon common = new FreeMindCommon(defaultPreferences);
    MindMapController mc = new MindMapController();
    
    // populate data
    MindMapNodeModel root = new MindMapNodeModel("root", null);
    root.add(new MindMapNodeModel("child0", null));
    root.add(new MindMapNodeModel("child1", null));
    
    MindMap m = new MindMapMapModel(root, common, mc);
    
    MapView mapView = new MapView(m, defaultPreferences, mc);
    mapView.selectAsTheOnlyOneSelected(mapView.getRoot());
    mc.setView(mapView);
    
    JFrame frame = new JFrame();
    
    MapView.ScrollPane scrollPane = new MapView.ScrollPane();
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setViewportView(mapView);
    
    
    // need scrollpane to wrap mind map
    BorderLayout layout = new BorderLayout();
    frame.getContentPane().setLayout(layout);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    mapView.centerNode(mapView.getRoot());
    
    frame.setSize(500, 500);
    frame.setVisible(true);
    
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    mc.obtainFocusForSelected(); // eagerly grab focus for map, else it will require clicking before proper use
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
