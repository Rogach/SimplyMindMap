package freemind.controller.actions.generated.instance;
/* PluginAction...*/
import java.util.ArrayList;
public class PluginAction {
  protected String label;
  protected String name;
  protected String base;
  protected String className;
  protected String documentation;
  protected String iconPath;
  protected String keyStroke;
  protected String instanciation;
  protected boolean isSelectable;
  public String getLabel(){
    return label;
  }
  public String getName(){
    return name;
  }
  public String getBase(){
    return base;
  }
  public String getClassName(){
    return className;
  }
  public String getDocumentation(){
    return documentation;
  }
  public String getIconPath(){
    return iconPath;
  }
  public String getKeyStroke(){
    return keyStroke;
  }
  public String getInstanciation(){
    return instanciation;
  }
  public boolean getIsSelectable(){
    return isSelectable;
  }
  public void setLabel(String value){
    this.label = value;
  }
  public void setName(String value){
    this.name = value;
  }
  public void setBase(String value){
    this.base = value;
  }
  public void setClassName(String value){
    this.className = value;
  }
  public void setDocumentation(String value){
    this.documentation = value;
  }
  public void setIconPath(String value){
    this.iconPath = value;
  }
  public void setKeyStroke(String value){
    this.keyStroke = value;
  }
  public void setInstanciation(String value){
    this.instanciation = value;
  }
  public void setIsSelectable(boolean value){
    this.isSelectable = value;
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

} /* PluginAction*/
