package org.rogach.simplymindmap.controller.listeners;

import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.actions.xml.ActionFactory;
import org.rogach.simplymindmap.controller.actions.xml.ActionPair;

public class UndoableActionPair implements UndoableAction {

  private ActionPair actionPair;
  private ActionFactory actionFactory;
  
  public UndoableActionPair(ActionPair actionPair, ActionFactory actionFactory) {
    this.actionPair = actionPair;
    this.actionFactory = actionFactory;
  }
  
  @Override
  public void redo() {
    actionFactory.executeXmlAction(actionPair.getDoAction());
  }

  @Override
  public void undo() {
    actionFactory.executeXmlAction(actionPair.getUndoAction());
  }

}
