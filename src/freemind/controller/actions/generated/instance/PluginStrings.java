package freemind.controller.actions.generated.instance;
/* PluginStrings...*/
import java.util.ArrayList;
public class PluginStrings {
  protected String language;
  public String getLanguage(){
    return language;
  }
  public void setLanguage(String value){
    this.language = value;
  }
  public void addPluginString(PluginString pluginString) {
    pluginStringList.add(pluginString);
  }

  public void addAtPluginString(int position, PluginString pluginString) {
    pluginStringList.add(position, pluginString);
  }

  public PluginString getPluginString(int index) {
    return (PluginString)pluginStringList.get( index );
  }

  public void removeFromPluginStringElementAt(int index) {
    pluginStringList.remove( index );
  }

  public int sizePluginStringList() {
    return pluginStringList.size();
  }

  public void clearPluginStringList() {
    pluginStringList.clear();
  }

  public java.util.List getListPluginStringList() {
    return java.util.Collections.unmodifiableList(pluginStringList);
  }
    protected ArrayList pluginStringList = new ArrayList();

} /* PluginStrings*/
