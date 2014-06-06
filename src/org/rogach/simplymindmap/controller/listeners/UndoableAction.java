package org.rogach.simplymindmap.controller.listeners;

public interface UndoableAction {
  public void redo();
  public void undo();
}
