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


package org.rogach.simplymindmap.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.nanoxml.XMLElement;

public class MindMapXMLElement extends XMLElementAdapter {

	// Logging:
	private static java.util.logging.Logger logger;

	public MindMapXMLElement(MindMapController pMindMapController) {
		super(pMindMapController);
		init();
	}

	protected MindMapXMLElement(MindMapController pMindMapController,
			Vector ArrowLinkAdapters, HashMap IDToTarget) {
		super(pMindMapController, ArrowLinkAdapters, IDToTarget);
		init();
	}

	/**
     *
     */
	private void init() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	/** abstract method to create elements of my type (factory). */
	protected XMLElement createAnotherElement() {
		// We do not need to initialize the things of XMLElement.
		return new MindMapXMLElement(mMindMapController, mArrowLinkAdapters,
				mIdToTarget);
	}

	protected MindMapNode createNodeAdapter(String nodeClass) {
		if (nodeClass == null) {
			return new MindMapNode(null, getMap());
		}
		// reflection:
		try {
			// construct class loader:
			ClassLoader loader = this.getClass().getClassLoader();
			// constructed.
			Class nodeJavaClass = Class.forName(nodeClass, true, loader);
			Class[] constrArgs = new Class[] { Object.class, MindMapModel.class };
			Object[] constrObjs = new Object[] { null, getMap() };
			Constructor constructor = nodeJavaClass.getConstructor(constrArgs);
			MindMapNode nodeImplementor = (MindMapNode) constructor
					.newInstance(constrObjs);
			return nodeImplementor;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			org.rogach.simplymindmap.main.Resources.getInstance().logException(e,
					"Error occurred loading node implementor: " + nodeClass);
			// the best we can do is to return the normal class:
			MindMapNode node = new MindMapNode(null, getMap());
			return node;
		}
	}

	protected EdgeAdapter createEdgeAdapter(MindMapNode node) {
		return new MindMapEdgeModel(node);
	}

}
