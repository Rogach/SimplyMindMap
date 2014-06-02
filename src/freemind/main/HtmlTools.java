/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: HtmlTools.java,v 1.1.2.28 2010/12/04 21:07:23 christianfoltin Exp $*/

package freemind.main;

public class HtmlTools {

	public static final String NBSP = "\u00A0";

	public static final String SP = "&#160;";

	private HtmlTools() {
		super();
	}

	public static String plainToHTML(String text) {
		char myChar;
		String textTabsExpanded = text.replaceAll("\t", "         ");
		StringBuilder result = new StringBuilder(textTabsExpanded.length()); 
		int lengthMinus1 = textTabsExpanded.length() - 1;
		result.append("<html><body><p>");
		for (int i = 0; i < textTabsExpanded.length(); ++i) {
			myChar = textTabsExpanded.charAt(i);
			switch (myChar) {
			case '&':
				result.append("&amp;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case ' ':
				if (i > 0 && i < lengthMinus1
						&& (int) textTabsExpanded.charAt(i - 1) > 32
						&& (int) textTabsExpanded.charAt(i + 1) > 32) {
					result.append(' ');
				} else {
					result.append("&nbsp;");
				}
				break;
			case '\n':
				result.append("<br>");
				break;
			default:
				result.append(myChar);
			}
		}
		return result.toString();
	}

}
