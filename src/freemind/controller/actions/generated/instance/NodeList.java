package freemind.controller.actions.generated.instance;
/* NodeList...*/
import java.util.ArrayList;
public class NodeList extends XmlAction {
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

} /* NodeList*/
