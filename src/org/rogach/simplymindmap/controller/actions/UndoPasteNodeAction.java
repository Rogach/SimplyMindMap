package org.rogach.simplymindmap.controller.actions;
/* UndoPasteNodeAction...*/
public class UndoPasteNodeAction extends NodeAction {
  protected boolean isLeft;
  protected boolean asSibling;
  protected int nodeAmount;
  public boolean getIsLeft(){
    return isLeft;
  }
  public boolean getAsSibling(){
    return asSibling;
  }
  public int getNodeAmount(){
    return nodeAmount;
  }
  public void setIsLeft(boolean value){
    this.isLeft = value;
  }
  public void setAsSibling(boolean value){
    this.asSibling = value;
  }
  public void setNodeAmount(int value){
    this.nodeAmount = value;
  }
} /* UndoPasteNodeAction*/
