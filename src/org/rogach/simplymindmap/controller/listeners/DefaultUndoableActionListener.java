package org.rogach.simplymindmap.controller.listeners;

import java.util.Stack;
import javax.swing.Action;

public class DefaultUndoableActionListener implements UndoableActionListener {

  private final Stack<UndoableAction> undoStack;
  private final Stack<UndoableAction> redoStack;
  
  private Action undoAction = null;
  private Action redoAction = null;
  
  public DefaultUndoableActionListener() {
    this.undoStack = new Stack<>();
    this.redoStack = new Stack<>();
  }
  
  @Override
  public void undoableActionPerformed(UndoableAction action) {
    redoStack.clear();
    undoStack.push(action);
    if (undoAction != null) {
      undoAction.setEnabled(true);
    }
    if (redoAction != null) {
      redoAction.setEnabled(false);
    }
  }
  
  public void setUndoAction(Action undoAction) {
    this.undoAction = undoAction;
  }
  
  public void setRedoAction(Action redoAction) {
    this.redoAction = redoAction;
  }
  
  public void undo() {
    assert !undoStack.isEmpty();
    UndoableAction undo = undoStack.pop();
    undo.undo();
    redoStack.push(undo);
    redoAction.setEnabled(true);
    if (undoStack.isEmpty()) {
      undoAction.setEnabled(false);
    }
  }
  
  public void redo() {
    assert !redoStack.isEmpty();
    UndoableAction redo = redoStack.pop();
    redo.redo();
    undoStack.push(redo);
    undoAction.setEnabled(true);
    if (redoStack.isEmpty()) {
      redoAction.setEnabled(false);
    }
  }
  
}
