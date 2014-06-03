package org.rogach.simplymindmap.util;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import static org.rogach.simplymindmap.util.Tools.listToString;
import static org.rogach.simplymindmap.util.Tools.stringToList;

public class XmlTools {

	public static String colorToXml(Color col) {
		// if (col == null) throw new IllegalArgumentException("Color was
		// null");
		if (col == null)
			return null;
		String red = Integer.toHexString(col.getRed());
		if (col.getRed() < 16)
			red = "0" + red;
		String green = Integer.toHexString(col.getGreen());
		if (col.getGreen() < 16)
			green = "0" + green;
		String blue = Integer.toHexString(col.getBlue());
		if (col.getBlue() < 16)
			blue = "0" + blue;
		return "#" + red + green + blue;
	}

	public static Color xmlToColor(String string) {
		if (string == null)
			return null;
		string = string.trim();
		if (string.length() == 7) {

			int red = Integer.parseInt(string.substring(1, 3), 16);
			int green = Integer.parseInt(string.substring(3, 5), 16);
			int blue = Integer.parseInt(string.substring(5, 7), 16);
			return new Color(red, green, blue);
		} else {
			throw new IllegalArgumentException("No xml color given by '"
					+ string + "'.");
		}
	}

	public static String PointToXml(Point col) {
		if (col == null)
			return null; // throw new IllegalArgumentException("Point was
		// null");
		Vector l = new Vector();
		l.add(Integer.toString(col.x));
		l.add(Integer.toString(col.y));
		return listToString((List) l);
	}

	public static Point xmlToPoint(String string) {
		if (string == null)
			return null;
		// fc, 3.11.2004: bug fix for alpha release of FM
		if (string.startsWith("java.awt.Point")) {
			string = string.replaceAll(
					"java\\.awt\\.Point\\[x=(-*[0-9]*),y=(-*[0-9]*)\\]",
					"$1;$2");
		}
		List l = stringToList(string);
		ListIterator it = l.listIterator(0);
		if (l.size() != 2)
			throw new IllegalArgumentException(
					"A point must consist of two numbers (and not: '" + string
							+ "').");
		int x = Integer.parseInt((String) it.next());
		int y = Integer.parseInt((String) it.next());
		return new Point(x, y);
	}

	public static String BooleanToXml(boolean col) {
		return (col) ? "true" : "false";
	}

	public static boolean xmlToBoolean(String string) {
		if (string == null)
			return false;
		if (string.equals("true"))
			return true;
		return false;
	}

}
