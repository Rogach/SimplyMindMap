package freemind.controller.actions;
/* NewNodeAction...*/
public class NewNodeAction extends NodeAction {
  protected String position;
  protected int index;
  protected String newId;
  public String getPosition(){
    return position;
  }
  public int getIndex(){
    return index;
  }
  public String getNewId(){
    return newId;
  }
  public void setPosition(String value){
    this.position = value;
  }
  public void setIndex(int value){
    this.index = value;
  }
  public void setNewId(String value){
    this.newId = value;
  }
} /* NewNodeAction*/
