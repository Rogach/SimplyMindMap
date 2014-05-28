package freemind.controller.actions.generated.instance;
/* PluginRegistration...*/
import java.util.ArrayList;
public class PluginRegistration {
  protected String className;
  protected boolean isPluginBase;
  public String getClassName(){
    return className;
  }
  public boolean getIsPluginBase(){
    return isPluginBase;
  }
  public void setClassName(String value){
    this.className = value;
  }
  public void setIsPluginBase(boolean value){
    this.isPluginBase = value;
  }
  public void addPluginMode(PluginMode pluginMode) {
    pluginModeList.add(pluginMode);
  }

  public void addAtPluginMode(int position, PluginMode pluginMode) {
    pluginModeList.add(position, pluginMode);
  }

  public PluginMode getPluginMode(int index) {
    return (PluginMode)pluginModeList.get( index );
  }

  public void removeFromPluginModeElementAt(int index) {
    pluginModeList.remove( index );
  }

  public int sizePluginModeList() {
    return pluginModeList.size();
  }

  public void clearPluginModeList() {
    pluginModeList.clear();
  }

  public java.util.List getListPluginModeList() {
    return java.util.Collections.unmodifiableList(pluginModeList);
  }
    protected ArrayList pluginModeList = new ArrayList();

} /* PluginRegistration*/
