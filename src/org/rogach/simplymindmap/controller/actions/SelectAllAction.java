/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
 */


package org.rogach.simplymindmap.controller.actions;

import javax.swing.Action;
import javax.swing.KeyStroke;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.model.AbstractMindMapModel;
import org.rogach.simplymindmap.model.MindMapNode;

/** */
public class SelectAllAction extends NodeGeneralAction {

	/**
     *
     */
	public SelectAllAction(final MindMapController modeController) {
		super(modeController, "select_all", null, new SingleNodeOperation() {

			public void apply(AbstractMindMapModel map, MindMapNode node) {
				modeController.selectBranch(modeController.getView().getRoot(),
						false);
			}
		});
    this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(controller.getResources().unsafeGetProperty("keystroke_select_all")));
	}

}
