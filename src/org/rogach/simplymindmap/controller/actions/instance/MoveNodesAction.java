package org.rogach.simplymindmap.controller.actions.instance;
/* MoveNodesAction...*/
import java.util.ArrayList;
public class MoveNodesAction extends NodeAction {
  protected int direction;
  public int getDirection(){
    return direction;
  }
  public void setDirection(int value){
    this.direction = value;
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

} /* MoveNodesAction*/
