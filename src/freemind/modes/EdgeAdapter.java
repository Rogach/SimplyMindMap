/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2001 Joerg Mueller <joergmueller@bigfoot.com> See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */


package freemind.modes;

import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import java.awt.Color;

public abstract class EdgeAdapter extends LineAdapter implements MindMapEdge {

	public static final String EDGE_WIDTH_THIN_STRING = "thin";
	private static Color standardColor = null;
  
	public static final int WIDTH_PARENT = -1;

	public static final int WIDTH_THIN = 0;

	public EdgeAdapter(MindMapNode target) {
		super(target);
		NORMAL_WIDTH = WIDTH_PARENT;
	}

	//
	// Attributes
	//

	public Color getColor() {
		if (color == null) {
			if (getTarget().isRoot()) {
				return getStandardColor();
			}
			return getSource().getEdge().getColor();
		}
		return color;
	}

	public Color getRealColor() {
		return color;
	}

	public int getWidth() {
		if (width == WIDTH_PARENT) {
			if (getTarget().isRoot()) {
				return WIDTH_THIN;
			}
			return getSource().getEdge().getWidth();
		}
		return width;
	}

	public int getRealWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	// /////////
	// Private Methods
	// ///////

	private MindMapNode getSource() {
		return target.getParentNode();
	}

	public XMLElement save() {
		if (style != null || color != null || width != WIDTH_PARENT) {
			XMLElement edge = new XMLElement();
			edge.setName("edge");

			if (color != null) {
				edge.setAttribute("COLOR", Tools.colorToXml(color));
			}
			if (width != WIDTH_PARENT) {
				if (width == WIDTH_THIN)
					edge.setAttribute("WIDTH", EDGE_WIDTH_THIN_STRING);
				else
					edge.setAttribute("WIDTH", Integer.toString(width));
			}
			return edge;
		}
		return null;
	}

	protected Color getStandardColor() {
		return standardColor;
	}

	protected void setStandardColor(Color standardColor) {
		EdgeAdapter.standardColor = standardColor;
	}
  
	protected String getStandardColorPropertyString() {
		return "standardedgecolor";
	}
  
}
