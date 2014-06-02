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

import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HtmlTools {

	public static final String NBSP = "\u00A0";

	private static Logger logger;

	private static final Pattern HTML_PATTERN = Pattern
			.compile("(?s)^\\s*<\\s*html.*?>.*");

	public static final String SP = "&#160;";

	private HtmlTools() {
		super();
		logger = Resources.getInstance().getLogger(HtmlTools.class.getName());
	}

	public static boolean isHtmlNode(String text) {
		for (int i = 0; i < text.length(); i++) {
			final char ch = text.charAt(i);
			if (ch == '<') {
				break;
			}
			if (!Character.isWhitespace(ch) || i == text.length()) {
				return false;
			}
		}
		return HTML_PATTERN.matcher(text.toLowerCase(Locale.ENGLISH)).matches();
	}

	public static String plainToHTML(String text) {
		char myChar;
		String textTabsExpanded = text.replaceAll("\t", "         "); // Use
																		// eight
																		// spaces
																		// as
																		// tab
																		// width.
		StringBuffer result = new StringBuffer(textTabsExpanded.length()); // Heuristic
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

	/**
	 * Determines whether the character is valid in XML. Invalid characters
	 * include most of the range x00-x1F, and more.
	 * 
	 * @see http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char.
	 */
	public static boolean isXMLValidCharacter(char character) {
		// Order the tests in such a sequence that the most probable
		// conditions are tested first.
		return character >= 0x20 && character <= 0xD7FF || character == 0x9
				|| character == 0xA || character == 0xD || character >= 0xE000
				&& character <= 0xFFFD || character >= 0x10000
				&& character <= 0x10FFFF;
	}

}
