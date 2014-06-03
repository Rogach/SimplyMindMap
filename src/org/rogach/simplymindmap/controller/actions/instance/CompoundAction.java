package org.rogach.simplymindmap.controller.actions.instance;
/* CompoundAction...*/
import java.util.ArrayList;
public class CompoundAction extends XmlAction {
  public void addChoice(Object choice) {
    choiceList.add(choice);
  }

  public void addAtChoice(int position, Object choice) {
    choiceList.add(position, choice);
  }

  public void setAtChoice(int position, Object choice) {
    choiceList.set(position, choice);
  }
  public Object getChoice(int index) {
    return (Object)choiceList.get( index );
  }

  public int sizeChoiceList() {
    return choiceList.size();
  }

  public void clearChoiceList() {
    choiceList.clear();
  }

  public java.util.List<Object> getListChoiceList() {
    return java.util.Collections.unmodifiableList(choiceList);
  }

  protected ArrayList<Object> choiceList = new ArrayList<>();

} /* CompoundAction*/
