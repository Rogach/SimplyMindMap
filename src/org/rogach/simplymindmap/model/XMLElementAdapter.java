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

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
import org.rogach.simplymindmap.controller.MindMapController;
import org.rogach.simplymindmap.nanoxml.XMLElement;
import org.rogach.simplymindmap.util.XmlTools;

public abstract class XMLElementAdapter extends XMLElement {

	// Logging:
	protected static java.util.logging.Logger logger;

	private Object userObject = null;
	private MindMapNode mapChild = null;
	private HashMap<String, String> nodeAttributes = new HashMap<>();

	// Font attributes

	private String fontName;
	private int fontStyle = 0;
	private int fontSize = 0;

	// Icon attributes

	private String iconName;

	// arrow link attributes:
	protected Vector mArrowLinkAdapters;
	protected HashMap<String, MindMapNode> mIdToTarget;
	public static final String XML_NODE_TEXT = "TEXT";
	public static final String XML_NODE = "node";
	public static final String XML_NODE_ATTRIBUTE = "attribute";
	public static final String XML_NODE_ATTRIBUTE_LAYOUT = "attribute_layout";
	public static final String XML_NODE_ATTRIBUTE_REGISTRY = "attribute_registry";
	public static final String XML_NODE_REGISTERED_ATTRIBUTE_NAME = "attribute_name";
	public static final String XML_NODE_REGISTERED_ATTRIBUTE_VALUE = "attribute_value";
	// public static final String XML_NODE_CLASS_PREFIX = XML_NODE+"_";
	public static final String XML_NODE_CLASS = "AA_NODE_CLASS";
	public static final String XML_NODE_ADDITIONAL_INFO = "ADDITIONAL_INFO";
	public static final String XML_NODE_ENCRYPTED_CONTENT = "ENCRYPTED_CONTENT";
	public static final String XML_NODE_HISTORY_CREATED_AT = "CREATED";
	public static final String XML_NODE_HISTORY_LAST_MODIFIED_AT = "MODIFIED";

	public static final String XML_NODE_XHTML_TYPE_TAG = "TYPE";
	public static final String XML_NODE_XHTML_TYPE_NODE = "NODE";
	public static final String XML_NODE_XHTML_TYPE_NOTE = "NOTE";

	protected final MindMapController mMindMapController;

	// Overhead methods

	public XMLElementAdapter(MindMapController modeController) {
		this(modeController, new HashMap<String, MindMapNode>());
	}

	protected XMLElementAdapter(MindMapController modeController, HashMap<String, MindMapNode> IDToTarget) {
		this.mMindMapController = modeController;
		this.mIdToTarget = IDToTarget;
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
	}

	/** abstract method to create elements of my type (factory). */
	abstract protected XMLElement createAnotherElement();

	abstract protected MindMapNode createNodeAdapter(String nodeClass);

	abstract protected EdgeAdapter createEdgeAdapter(MindMapNode node);

	public Object getUserObject() {
		return userObject;
	}

	protected void setUserObject(Object obj) {
		userObject = obj;
	}

	public MindMapNode getMapChild() {
		return mapChild;
	}

	// Real parsing methods

	public void setName(String name) {
		super.setName(name);
    // Create user object based on name
    switch (name) {
      case XML_NODE:
        userObject = createNodeAdapter(null);
        nodeAttributes.clear();
        break;
      case "edge":
        userObject = createEdgeAdapter(null);
        break;
      case "font":
        userObject = null;
        break;
      case XML_NODE_ATTRIBUTE:
        userObject = null;
        break;
      case XML_NODE_ATTRIBUTE_LAYOUT:
        userObject = null;
        break;
      case "map":
        userObject = null;
        break;
      case XML_NODE_ATTRIBUTE_REGISTRY:
        userObject = null;
        break;
      case XML_NODE_REGISTERED_ATTRIBUTE_NAME:
        userObject = null;
        break;
      case XML_NODE_REGISTERED_ATTRIBUTE_VALUE:
        userObject = null;
        break;
      case "icon":
        userObject = null;
        break;
      case "hook":
        // we gather the xml element and send it to the hook after
        // completion.
        userObject = new XMLElement();
        break;
      default:
        userObject = new XMLElement(); // for childs of hooks
        break;
    }
	}

	public void addChild(XMLElement child) {
		if (getName().equals("map")) {
			mapChild = (MindMapNode) child.getUserObject();
			return;
		}
		if (userObject instanceof XMLElement) {
			// ((XMLElement) userObject).addChild(child);
			super.addChild(child);
			return;
		}
		if (userObject instanceof MindMapNode) {
			MindMapNode node = (MindMapNode) userObject;
			if (child.getUserObject() instanceof MindMapNode) {
				node.insert((MindMapNode) child.getUserObject(), -1);
			} // to the end without preferable... (PN)
				// node.getRealChildCount()); }
			else if (child.getUserObject() instanceof EdgeAdapter) {
				EdgeAdapter edge = (EdgeAdapter) child.getUserObject();
				edge.setTarget(node);
				node.setEdge(edge);
			} else if (child.getName().equals("font")) {
				node.setFont((Font) child.getUserObject());
			} else if (child.getName().equals("icon")) {
				node.addIcon((MindIcon) child.getUserObject(), MindIcon.LAST);
			} else if (child.getName().equals(XML_NODE_XHTML_CONTENT_TAG)) {
				String xmlText = ((XMLElement) child).getContent();
        node.setText(xmlText);
			}
			return;
		}
	}

	public void setAttribute(String name, Object value) {
		// We take advantage of precondition that value != null.
		String sValue = value.toString();
		if (ignoreCase) {
			name = name.toUpperCase();
		}
		if (userObject instanceof XMLElement) {
			// ((XMLElement) userObject).setAttribute(name, value);
			super.setAttribute(name, value); // and to myself, as I am also an
												// xml element.
			return;
		}

		if (userObject instanceof MindMapNode) {
			//
			MindMapNode node = (MindMapNode) userObject;
			userObject = setNodeAttribute(name, sValue, node);
			nodeAttributes.put(name, sValue);
			return;
		}

		if (userObject instanceof EdgeAdapter) {
			EdgeAdapter edge = (EdgeAdapter) userObject;
      switch (name) {
        case "COLOR":
          edge.setColor(XmlTools.xmlToColor(sValue));
          break;
        case "WIDTH":
          if (sValue.equals(EdgeAdapter.EDGE_WIDTH_THIN_STRING)) {
            edge.setWidth(EdgeAdapter.WIDTH_THIN);
          } else {
            edge.setWidth(Integer.parseInt(sValue));
          } break;
      }
			return;
		}

		if (getName().equals("font")) {
			if (name.equals("SIZE")) {
				fontSize = Integer.parseInt(sValue);
			} else if (name.equals("NAME")) {
				fontName = sValue;
			}

			// Styling
			else if (sValue.equals("true")) {
        switch (name) {
          case "BOLD":
            fontStyle += Font.BOLD;
            break;
          case "ITALIC":
            fontStyle += Font.ITALIC;
            break;
        }
			}
		}
		/* icons */
		if (getName().equals("icon")) {
			if (name.equals("BUILTIN")) {
				iconName = sValue;
			}
		}
	}

	private MindMapNode setNodeAttribute(String name, String sValue,
			MindMapNode node) {
    switch (name) {
      case XML_NODE_TEXT:
        logger.finest("Setting node text content to:" + sValue);
        node.setUserObject(sValue);
        break;
      case "FOLDED":
        if (sValue.equals("true")) {
          node.setFolded(true);
        } break;
      case "POSITION":
        // fc, 17.12.2003: Remove the left/right bug.
        node.setLeft(sValue.equals("left"));
        break;
      case "COLOR":
        if (sValue.length() == 7) {
          node.setColor(XmlTools.xmlToColor(sValue));
        } break;
      case "BACKGROUND_COLOR":
        if (sValue.length() == 7) {
          node.setBackgroundColor(XmlTools.xmlToColor(sValue));
        } break;
      case "ID":
        // do not set label but annotate in list:
        // System.out.println("(sValue, node) = " + sValue + ", "+ node);
        mIdToTarget.put(sValue, node);
        break;
      case "VSHIFT":
        node.setShiftY(Integer.parseInt(sValue));
        break;
      case "VGAP":
        node.setVGap(Integer.parseInt(sValue));
        break;
      case "HGAP":
        node.setHGap(Integer.parseInt(sValue));
        break;
    }
		return node;
	}

	/**
	 * Sets all attributes that were formely applied to the current userObject
	 * to a given (new) node. Thus, the instance of a node can be changed after
	 * the creation. (At the moment, relevant for encrypted nodes).
	 */
	protected void copyAttributesToNode(MindMapNode node) {
		// reactivate all settings from nodeAttributes:
		for (Iterator i = nodeAttributes.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			// to avoid self reference:
			setNodeAttribute(key, (String) nodeAttributes.get(key), node);
		}
	}

	protected void completeElement() {
		if (getName().equals(XML_NODE)) {
			// unify map child behaviour:
			if (mapChild == null) {
				mapChild = (MindMapNode) userObject;
			}
			return;
		}
		if (getName().equals("font")) {
			userObject =
					new Font(fontName, fontStyle, fontSize);
			return;
		}
		/* icons */
		if (getName().equals("icon")) {
			userObject = MindIcon.factory(iconName, mMindMapController.getResources());
			return;
		}
	}

	/**
	 * Completes the links within the getMap(). They are registered in the
	 * registry.
	 * 
	 * Case I: Source+Destination are pasted (Ia: cut, Ib: copy) Case II: Source
	 * is pasted, Destination remains unchanged in the map (IIa: cut, IIb: copy)
	 * Case III: Destination is pasted, Source remains unchanged in the map
	 * (IIIa: cut, IIIb: copy)
	 */
	public void processUnfinishedLinks(MindMapLinkRegistry registry) {
		// add labels to the nodes:
		for (Iterator i1 = mIdToTarget.keySet().iterator(); i1.hasNext();) {
			String key = (String) i1.next();
			MindMapNode target1 = (MindMapNode) mIdToTarget.get(key);
			/*
			 * key is the proposed name for the target, is changed by the
			 * registry, if already present.
			 */
			registry.registerLinkTarget(target1, key);
		}
	}

	protected AbstractMindMapModel getMapModel() {
		return mMindMapController.getMapModel();
	}

	public HashMap getIDToTarget() {
		return mIdToTarget;
	}

	public void setIDToTarget(HashMap<String, MindMapNode> pToTarget) {
		mIdToTarget = pToTarget;
	}
}
