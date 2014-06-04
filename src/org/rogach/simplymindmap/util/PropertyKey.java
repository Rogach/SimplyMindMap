package org.rogach.simplymindmap.util;

public enum PropertyKey {
  BUTTONS_POSITION("el__buttons_position"),
  DEFAULT_FONT("defaultfont"),
  DEFAULT_FONT_SIZE("defaultfontsize"),
  DEFAULT_FONT_STYLE("defaultfontstyle"),
  DISABLE_CURSOR_MOVE_PAPER("disable_cursor_move_paper"),
  DISABLE_KEY_TYPE("disable_key_type"),
  DISPLAY_FOLDING_BUTTONS("resources_display_folding_buttons"),
  DRAG_AND_DROP("draganddrop"),
  ENABLE_LEAVES_FOLDING("enable_leaves_folding"),
  ENABLE_NODE_MOVEMENT("enable_node_movement"),
  ENTER_CONFIRMS_BY_DEFAULT("el__enter_confirms_by_default"),
  FOLDING_SYMBOL_WIDTH("foldingsymbolwidth"),
  ICONS_LIST("icons.list"),
  KEYSTROKE_MOVE_DOWN("keystroke_move_down"),
  KEYSTROKE_MOVE_LEFT("keystroke_move_left"),
  KEYSTROKE_MOVE_RIGHT("keystroke_move_right"),
  KEYSTROKE_MOVE_UP("keystroke_move_up"),
  KEY_TYPE_ADDS_NEW("key_type_adds_new"),
  LANGUAGE("language"),
  MAX_DEFAULT_WINDOW_HEIGHT("el__max_default_window_height"),
  MAX_DEFAULT_WINDOW_WIDTH("el__max_default_window_width"),
  MAX_NODE_WIDTH("max_node_width"),
  MAX_TOOLTIP_WIDTH("max_tooltip_width"),
  MIN_DEFAULT_WINDOW_HEIGHT("el__min_default_window_height"),
  MIN_DEFAULT_WINDOW_WIDTH("el__min_default_window_width"),
  PLACE_NEW_BRANCHES("placenewbranches"),
  SAVE_FOLDING_STATE("resources_save_folding_state"),
  SELECTION_METHOD("selection_method"),
  STANDARD_BACKGROUND_COLOR("standardbackgroundcolor"),
  STANDARD_DRAW_RECTANGLE_FOR_SELECTION("standarddrawrectangleforselection"),
  STANDARD_EDGE_COLOR("standardedgecolor"),
  STANDARD_NODE_SELECTED_COLOR("standardselectednodecolor"),
  STANDARD_NODE_TEXT_COLOR("standardnodetextcolor"),
  STANDARD_SELECTED_NODE_RECTANGLE_COLOR("standardselectednoderectanglecolor"),
  TIME_FOR_DELAYED_SELECTION("time_for_delayed_selection"),
  UNDO_LEVELS("undo_levels"),
  UNFOLD_ON_PASTE("unfold_on_paste"),
  USE_COMMON_OUT_POINT_FOR_ROOT_NODE("use_common_out_point_for_root_node"),
  WHEEL_VELOCITY("wheel_velocity");

  private String key;
  private PropertyKey(String key) {
    this.key = key;
  }
  public String getKey() {
    return key;
  }
  
  
}
