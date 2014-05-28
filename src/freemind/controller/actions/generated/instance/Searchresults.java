package freemind.controller.actions.generated.instance;
/* Searchresults...*/
import java.util.ArrayList;
public class Searchresults extends XmlAction {
  protected String timestamp;
  protected String attribution;
  protected String querystring;
  protected String polygon;
  protected String excludePlaceIds;
  protected String moreUrl;
  public String getTimestamp(){
    return timestamp;
  }
  public String getAttribution(){
    return attribution;
  }
  public String getQuerystring(){
    return querystring;
  }
  public String getPolygon(){
    return polygon;
  }
  public String getExcludePlaceIds(){
    return excludePlaceIds;
  }
  public String getMoreUrl(){
    return moreUrl;
  }
  public void setTimestamp(String value){
    this.timestamp = value;
  }
  public void setAttribution(String value){
    this.attribution = value;
  }
  public void setQuerystring(String value){
    this.querystring = value;
  }
  public void setPolygon(String value){
    this.polygon = value;
  }
  public void setExcludePlaceIds(String value){
    this.excludePlaceIds = value;
  }
  public void setMoreUrl(String value){
    this.moreUrl = value;
  }
  public void addPlace(Place place) {
    placeList.add(place);
  }

  public void addAtPlace(int position, Place place) {
    placeList.add(position, place);
  }

  public Place getPlace(int index) {
    return (Place)placeList.get( index );
  }

  public void removeFromPlaceElementAt(int index) {
    placeList.remove( index );
  }

  public int sizePlaceList() {
    return placeList.size();
  }

  public void clearPlaceList() {
    placeList.clear();
  }

  public java.util.List getListPlaceList() {
    return java.util.Collections.unmodifiableList(placeList);
  }
    protected ArrayList placeList = new ArrayList();

} /* Searchresults*/
