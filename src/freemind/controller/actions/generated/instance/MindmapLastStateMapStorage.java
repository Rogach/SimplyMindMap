package freemind.controller.actions.generated.instance;
/* MindmapLastStateMapStorage...*/
import java.util.ArrayList;
public class MindmapLastStateMapStorage extends XmlAction {
  protected int lastFocusedTab;
  public int getLastFocusedTab(){
    return lastFocusedTab;
  }
  public void setLastFocusedTab(int value){
    this.lastFocusedTab = value;
  }
  public void addMindmapLastStateStorage(MindmapLastStateStorage mindmapLastStateStorage) {
    mindmapLastStateStorageList.add(mindmapLastStateStorage);
  }

  public void addAtMindmapLastStateStorage(int position, MindmapLastStateStorage mindmapLastStateStorage) {
    mindmapLastStateStorageList.add(position, mindmapLastStateStorage);
  }

  public MindmapLastStateStorage getMindmapLastStateStorage(int index) {
    return (MindmapLastStateStorage)mindmapLastStateStorageList.get( index );
  }

  public void removeFromMindmapLastStateStorageElementAt(int index) {
    mindmapLastStateStorageList.remove( index );
  }

  public int sizeMindmapLastStateStorageList() {
    return mindmapLastStateStorageList.size();
  }

  public void clearMindmapLastStateStorageList() {
    mindmapLastStateStorageList.clear();
  }

  public java.util.List getListMindmapLastStateStorageList() {
    return java.util.Collections.unmodifiableList(mindmapLastStateStorageList);
  }
    protected ArrayList mindmapLastStateStorageList = new ArrayList();

} /* MindmapLastStateMapStorage*/
