package freemind.controller.actions.generated.instance;
/* Reversegeocode...*/
import java.util.ArrayList;
public class Reversegeocode extends XmlAction {
  protected String attribution;
  protected String querystring;
  protected String timestamp;
  public String getAttribution(){
    return attribution;
  }
  public String getQuerystring(){
    return querystring;
  }
  public String getTimestamp(){
    return timestamp;
  }
  public void setAttribution(String value){
    this.attribution = value;
  }
  public void setQuerystring(String value){
    this.querystring = value;
  }
  public void setTimestamp(String value){
    this.timestamp = value;
  }
  public void addResult(Result result) {
    resultList.add(result);
  }

  public void addAtResult(int position, Result result) {
    resultList.add(position, result);
  }

  public Result getResult(int index) {
    return (Result)resultList.get( index );
  }

  public void removeFromResultElementAt(int index) {
    resultList.remove( index );
  }

  public int sizeResultList() {
    return resultList.size();
  }

  public void clearResultList() {
    resultList.clear();
  }

  public java.util.List getListResultList() {
    return java.util.Collections.unmodifiableList(resultList);
  }
    protected ArrayList resultList = new ArrayList();

} /* Reversegeocode*/
