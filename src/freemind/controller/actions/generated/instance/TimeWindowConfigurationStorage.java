package freemind.controller.actions.generated.instance;
/* TimeWindowConfigurationStorage...*/
import java.util.ArrayList;
public class TimeWindowConfigurationStorage extends WindowConfigurationStorage {
  protected boolean viewFoldedNodes;
  public boolean getViewFoldedNodes(){
    return viewFoldedNodes;
  }
  public void setViewFoldedNodes(boolean value){
    this.viewFoldedNodes = value;
  }
  public void addTimeWindowColumnSetting(TimeWindowColumnSetting timeWindowColumnSetting) {
    timeWindowColumnSettingList.add(timeWindowColumnSetting);
  }

  public void addAtTimeWindowColumnSetting(int position, TimeWindowColumnSetting timeWindowColumnSetting) {
    timeWindowColumnSettingList.add(position, timeWindowColumnSetting);
  }

  public TimeWindowColumnSetting getTimeWindowColumnSetting(int index) {
    return (TimeWindowColumnSetting)timeWindowColumnSettingList.get( index );
  }

  public void removeFromTimeWindowColumnSettingElementAt(int index) {
    timeWindowColumnSettingList.remove( index );
  }

  public int sizeTimeWindowColumnSettingList() {
    return timeWindowColumnSettingList.size();
  }

  public void clearTimeWindowColumnSettingList() {
    timeWindowColumnSettingList.clear();
  }

  public java.util.List getListTimeWindowColumnSettingList() {
    return java.util.Collections.unmodifiableList(timeWindowColumnSettingList);
  }
    protected ArrayList timeWindowColumnSettingList = new ArrayList();

} /* TimeWindowConfigurationStorage*/
