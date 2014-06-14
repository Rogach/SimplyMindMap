package org.rogach.simplymindmap.controller.actions.instance;

public class NodeBackgroundColorFormatAction extends FormatNodeAction {
  protected String color;
  public String getColor(){
    return color;
  }
  public void setColor(String value){
    this.color = value;
  }
}
