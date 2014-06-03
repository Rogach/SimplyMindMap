package org.rogach.simplymindmap.view;

import java.util.Iterator;
import java.util.Vector;

class Selected {
  private Vector<NodeView> mySelected = new Vector<>();
  private final MapView outer;

  public Selected(final MapView outer) {
    this.outer = outer;
  }

  public void clear() {
    if (size() > 0) {
      removeFocusForHooks(get(0));
    }
    for (Iterator it = mySelected.iterator(); it.hasNext();) {
      NodeView view = (NodeView) it.next();
      changeSelection(view, false);
    }
    mySelected.clear();
  }

  /**
   * @param pNode
   */
  private void changeSelection(NodeView pNode, boolean pIsSelected) {
    if (pNode.getModel() == null) {
      return;
    }
    outer.getModel().getMindMapController().changeSelection(pNode, pIsSelected);
  }

  public int size() {
    return mySelected.size();
  }

  public void remove(NodeView node) {
    if (mySelected.indexOf(node) == 0) {
      removeFocusForHooks(node);
    }
    changeSelection(node, false);
    mySelected.remove(node);
  }

  public void add(NodeView node) {
    if (size() > 0) {
      removeFocusForHooks(get(0));
    }
    mySelected.add(0, node);
    addFocusForHooks(node);
    changeSelection(node, true);
  }

  private void removeFocusForHooks(NodeView node) {
    if (node.getModel() == null) {
      return;
    }
    outer.getModel().getMindMapController().onLostFocusNode(node);
  }

  private void addFocusForHooks(NodeView node) {
    outer.getModel().getMindMapController().onFocusNode(node);
  }

  public NodeView get(int i) {
    return (NodeView) mySelected.get(i);
  }

  public boolean contains(NodeView node) {
    return mySelected.contains(node);
  }

  /**
   */
  public void moveToFirst(NodeView newSelected) {
    if (contains(newSelected)) {
      int pos = mySelected.indexOf(newSelected);
      if (pos > 0) {
        // move
        if (size() > 0) {
          removeFocusForHooks(get(0));
        }
        mySelected.remove(newSelected);
        mySelected.add(0, newSelected);
      }
    } else {
      add(newSelected);
    }
    addFocusForHooks(newSelected);
  }

}
