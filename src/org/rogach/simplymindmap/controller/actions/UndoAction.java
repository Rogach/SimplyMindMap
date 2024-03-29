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
 * Created on 20.09.2004
 */
/*$Id: UndoAction.java,v 1.1.2.2.2.4 2006/11/26 10:20:44 dpolivaev Exp $*/

package org.rogach.simplymindmap.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.controller.listeners.DefaultUndoableActionListener;

public class UndoAction extends AbstractAction {

  private DefaultUndoableActionListener undoHandler;

	public UndoAction(MindMapController controller) {
    super(controller.getResources().getText("cancel"), controller.getResources().getIcon("undo.png"));
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_undo")));
		this.setEnabled(false);
	}

  @Override
  public void actionPerformed(ActionEvent e) {
    assert undoHandler != null;
    undoHandler.undo();
  }
  
  public void setUndoHandler(DefaultUndoableActionListener undoHandler) {
    this.undoHandler = undoHandler;
  }
}
