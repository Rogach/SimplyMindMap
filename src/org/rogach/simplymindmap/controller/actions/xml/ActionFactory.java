/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 24.04.2004
 */

package org.rogach.simplymindmap.controller.actions.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rogach.simplymindmap.controller.actions.instance.XmlAction;
import org.rogach.simplymindmap.controller.listeners.UndoableActionListener;
import org.rogach.simplymindmap.controller.listeners.UndoableActionPair;

/**
 * @author foltin
 * 
 */
public class ActionFactory {
  
	/** HashMap of Action class -> actor instance. */
	private HashMap<Class, ActorXml> registeredActors;
	private static java.util.logging.Logger logger = null;
  private List<UndoableActionListener> undoListeners = new ArrayList<>();

	public ActionFactory() {
		super();
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		registeredActors = new HashMap<>();
	}

	/**
	 * @return see {@link #executeAction(ActionPair)}
	 */
	public boolean doTransaction(String pName, ActionPair pPair) {
    return this.executeAction(pPair);
	}

	/**
	 * @return the success of the action. If an exception arises, the method
	 *         returns false.
	 */
	private boolean executeAction(ActionPair pair) {
		if (pair == null)
			return false;
    
    for (UndoableActionListener undoListener : undoListeners) {
      undoListener.undoableActionPerformed(new UndoableActionPair(pair, this));
    }

		ActionPair filteredPair = pair;
    try {
      executeXmlAction(filteredPair.getDoAction());
    } catch (Exception e) {
      Logger.getLogger(ActionFactory.class.getName()).log(Level.SEVERE, null, e);
      return false;
    }
    return true;
	}
  
  public void executeXmlAction(XmlAction action) {
    ActorXml actor = getActor(action);
    actor.act(action);
  }

	public void registerActor(ActorXml actor, Class action) {
		registeredActors.put(action, actor);
	}

	public void deregisterActor(Class action) {
		registeredActors.remove(action);
	}

	public ActorXml getActor(XmlAction action) {
		for (Iterator i = registeredActors.keySet().iterator(); i.hasNext();) {
			Class actorClass = (Class) i.next();
			if (actorClass.isInstance(action)) {
				return (ActorXml) registeredActors.get(actorClass);
			}
		}
		throw new IllegalArgumentException("No actor present for xmlaction"
				+ action.getClass());
	}
  
  public void addUndoableActionListener(UndoableActionListener undoListener) {
    undoListeners.add(undoListener);
  }
  
  public void removeUndoableActionListener(UndoableActionListener undoListener) {
    undoListeners.remove(undoListener);
  }
}
