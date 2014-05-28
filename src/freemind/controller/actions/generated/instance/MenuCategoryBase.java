package freemind.controller.actions.generated.instance;
/* MenuCategoryBase...*/
import java.util.ArrayList;
public class MenuCategoryBase {
  protected String name;
  public String getName(){
    return name;
  }
  public void setName(String value){
    this.name = value;
  }
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

  public java.util.List getListChoiceList() {
    return java.util.Collections.unmodifiableList(choiceList);
  }

  protected ArrayList choiceList = new ArrayList();

} /* MenuCategoryBase*/
