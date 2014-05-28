package freemind.controller.actions.generated.instance;
/* MindmapLastStateStorage...*/
import java.util.ArrayList;
public class MindmapLastStateStorage {
  protected long lastChanged;
  protected int tabIndex;
  protected String restorableName;
  protected float lastZoom;
  protected int x;
  protected int y;
  protected String lastSelected;
  public long getLastChanged(){
    return lastChanged;
  }
  public int getTabIndex(){
    return tabIndex;
  }
  public String getRestorableName(){
    return restorableName;
  }
  public float getLastZoom(){
    return lastZoom;
  }
  public int getX(){
    return x;
  }
  public int getY(){
    return y;
  }
  public String getLastSelected(){
    return lastSelected;
  }
  public void setLastChanged(long value){
    this.lastChanged = value;
  }
  public void setTabIndex(int value){
    this.tabIndex = value;
  }
  public void setRestorableName(String value){
    this.restorableName = value;
  }
  public void setLastZoom(float value){
    this.lastZoom = value;
  }
  public void setX(int value){
    this.x = value;
  }
  public void setY(int value){
    this.y = value;
  }
  public void setLastSelected(String value){
    this.lastSelected = value;
  }
  public void addNodeListMember(NodeListMember nodeListMember) {
    nodeListMemberList.add(nodeListMember);
  }

  public void addAtNodeListMember(int position, NodeListMember nodeListMember) {
    nodeListMemberList.add(position, nodeListMember);
  }

  public NodeListMember getNodeListMember(int index) {
    return (NodeListMember)nodeListMemberList.get( index );
  }

  public void removeFromNodeListMemberElementAt(int index) {
    nodeListMemberList.remove( index );
  }

  public int sizeNodeListMemberList() {
    return nodeListMemberList.size();
  }

  public void clearNodeListMemberList() {
    nodeListMemberList.clear();
  }

  public java.util.List getListNodeListMemberList() {
    return java.util.Collections.unmodifiableList(nodeListMemberList);
  }
    protected ArrayList nodeListMemberList = new ArrayList();

} /* MindmapLastStateStorage*/
