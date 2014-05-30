package freemind.controller.actions.generated.instance;
/* MapWindowConfigurationStorage...*/
import java.util.ArrayList;
public class MapWindowConfigurationStorage extends WindowConfigurationStorage {
  protected double mapCenterLongitude;
  protected double mapCenterLatitude;
  protected double cursorLongitude;
  protected double cursorLatitude;
  protected int zoom;
  protected int lastDividerPosition;
  protected String tileSource;
  protected boolean showMapMarker;
  protected boolean tileGridVisible;
  protected boolean zoomControlsVisible;
  protected boolean searchControlVisible;
  protected boolean hideFoldedNodes;
  protected boolean limitSearchToVisibleArea;
  protected int mapLocationStorageIndex;
  public double getMapCenterLongitude(){
    return mapCenterLongitude;
  }
  public double getMapCenterLatitude(){
    return mapCenterLatitude;
  }
  public double getCursorLongitude(){
    return cursorLongitude;
  }
  public double getCursorLatitude(){
    return cursorLatitude;
  }
  public int getZoom(){
    return zoom;
  }
  public int getLastDividerPosition(){
    return lastDividerPosition;
  }
  public String getTileSource(){
    return tileSource;
  }
  public boolean getShowMapMarker(){
    return showMapMarker;
  }
  public boolean getTileGridVisible(){
    return tileGridVisible;
  }
  public boolean getZoomControlsVisible(){
    return zoomControlsVisible;
  }
  public boolean getSearchControlVisible(){
    return searchControlVisible;
  }
  public boolean getHideFoldedNodes(){
    return hideFoldedNodes;
  }
  public boolean getLimitSearchToVisibleArea(){
    return limitSearchToVisibleArea;
  }
  public int getMapLocationStorageIndex(){
    return mapLocationStorageIndex;
  }
  public void setMapCenterLongitude(double value){
    this.mapCenterLongitude = value;
  }
  public void setMapCenterLatitude(double value){
    this.mapCenterLatitude = value;
  }
  public void setCursorLongitude(double value){
    this.cursorLongitude = value;
  }
  public void setCursorLatitude(double value){
    this.cursorLatitude = value;
  }
  public void setZoom(int value){
    this.zoom = value;
  }
  public void setLastDividerPosition(int value){
    this.lastDividerPosition = value;
  }
  public void setTileSource(String value){
    this.tileSource = value;
  }
  public void setShowMapMarker(boolean value){
    this.showMapMarker = value;
  }
  public void setTileGridVisible(boolean value){
    this.tileGridVisible = value;
  }
  public void setZoomControlsVisible(boolean value){
    this.zoomControlsVisible = value;
  }
  public void setSearchControlVisible(boolean value){
    this.searchControlVisible = value;
  }
  public void setHideFoldedNodes(boolean value){
    this.hideFoldedNodes = value;
  }
  public void setLimitSearchToVisibleArea(boolean value){
    this.limitSearchToVisibleArea = value;
  }
  public void setMapLocationStorageIndex(int value){
    this.mapLocationStorageIndex = value;
  }
  public void addTableColumnSetting(TableColumnSetting tableColumnSetting) {
    tableColumnSettingList.add(tableColumnSetting);
  }

  public void addAtTableColumnSetting(int position, TableColumnSetting tableColumnSetting) {
    tableColumnSettingList.add(position, tableColumnSetting);
  }

  public TableColumnSetting getTableColumnSetting(int index) {
    return (TableColumnSetting)tableColumnSettingList.get( index );
  }

  public void removeFromTableColumnSettingElementAt(int index) {
    tableColumnSettingList.remove( index );
  }

  public int sizeTableColumnSettingList() {
    return tableColumnSettingList.size();
  }

  public void clearTableColumnSettingList() {
    tableColumnSettingList.clear();
  }

  public java.util.List getListTableColumnSettingList() {
    return java.util.Collections.unmodifiableList(tableColumnSettingList);
  }
    protected ArrayList tableColumnSettingList = new ArrayList();

  public void addMapLocationStorage(MapLocationStorage mapLocationStorage) {
    mapLocationStorageList.add(mapLocationStorage);
  }

  public void addAtMapLocationStorage(int position, MapLocationStorage mapLocationStorage) {
    mapLocationStorageList.add(position, mapLocationStorage);
  }

  public MapLocationStorage getMapLocationStorage(int index) {
    return (MapLocationStorage)mapLocationStorageList.get( index );
  }

  public void removeFromMapLocationStorageElementAt(int index) {
    mapLocationStorageList.remove( index );
  }

  public int sizeMapLocationStorageList() {
    return mapLocationStorageList.size();
  }

  public void clearMapLocationStorageList() {
    mapLocationStorageList.clear();
  }

  public java.util.List getListMapLocationStorageList() {
    return java.util.Collections.unmodifiableList(mapLocationStorageList);
  }
    protected ArrayList mapLocationStorageList = new ArrayList();

} /* MapWindowConfigurationStorage*/
