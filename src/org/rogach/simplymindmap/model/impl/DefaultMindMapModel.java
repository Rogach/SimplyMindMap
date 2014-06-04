package org.rogach.simplymindmap.model.impl;

import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;

public class DefaultMindMapModel extends AbstractMindMapModel {

  public DefaultMindMapModel() {
    super();
  }
  
  public DefaultMindMapModel(MindMapNode root) {
    super(root);
  }
  
  @Override
  public MindMapNode newNode(String userObject) {
    return new DefaultMindMapNode(userObject, this);
  }
  
}
