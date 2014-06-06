package org.rogach.simplymindmap.controller.listeners;

public class UndoableActionSeq implements UndoableAction {

  private final UndoableAction undo1;
  private final UndoableAction undo2;

  public UndoableActionSeq(UndoableAction undo1, UndoableAction undo2) {
    this.undo1 = undo1;
    this.undo2 = undo2;
  }
  
  @Override
  public void redo() {
    undo1.redo();
    undo2.redo();
  }

  @Override
  public void undo() {
    undo2.undo();
    undo1.undo();
  }

}
