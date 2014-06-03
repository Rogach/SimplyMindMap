/*
 * FreeMind - a program for creating and viewing mindmaps
 * Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * See COPYING for details
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

package org.rogach.simplymindmap.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.rogach.simplymindmap.controller.actions.CompoundAction;
import org.rogach.simplymindmap.controller.actions.XmlAction;
import org.rogach.simplymindmap.main.Resources;
import org.rogach.simplymindmap.view.NodeView;

/**
 * @author foltin
 * 
 */
public class Tools {

	private static java.util.logging.Logger logger = null;
	static {
		logger = Logger.getLogger("Tools");
	}

	private static Set availableFontFamilyNames = null;

	private static String sEnvFonts[] = null;

	public static Random ran = new Random();

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

	/**
	 * Converts a String in the format "value;value;value" to a List with the
	 * values (as strings)
	 */
	public static List stringToList(String string) {
		StringTokenizer tok = new StringTokenizer(string, ";");
		List list = new LinkedList();
		while (tok.hasMoreTokens()) {
			list.add(tok.nextToken());
		}
		return list;
	}

	public static String listToString(List list) {
		ListIterator it = list.listIterator(0);
		String str = new String();
		while (it.hasNext()) {
			str += it.next().toString() + ";";
		}
		return str;
	}

	public static Set getAvailableFontFamilyNames() {
		if (availableFontFamilyNames == null) {
			String[] envFonts = getAvailableFonts();
			availableFontFamilyNames = new HashSet();
			for (int i = 0; i < envFonts.length; i++) {
				availableFontFamilyNames.add(envFonts[i]);
			}
			// Add this one explicitly, Java defaults to it if the font is not
			availableFontFamilyNames.add("dialog");
		}
		return availableFontFamilyNames;
	}

	/**
     */
	private static String[] getAvailableFonts() {
		if (sEnvFonts == null) {
			GraphicsEnvironment gEnv = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			sEnvFonts = gEnv.getAvailableFontFamilyNames();
		}
		return sEnvFonts;
	}

	public static Vector getAvailableFontFamilyNamesAsVector() {
		String[] envFonts = getAvailableFonts();
		Vector availableFontFamilyNames = new Vector();
		for (int i = 0; i < envFonts.length; i++) {
			availableFontFamilyNames.add(envFonts[i]);
		}
		return availableFontFamilyNames;
	}

	public static boolean isAvailableFontFamily(String fontFamilyName) {
		return getAvailableFontFamilyNames().contains(fontFamilyName);
	}

	/**
	 * @param string1
	 *            input (or null)
	 * @param string2
	 *            input (or null)
	 * @return true, if equal (that means: same text or both null)
	 */
	public static boolean safeEquals(String string1, String string2) {
		return (string1 != null && string2 != null && string1.equals(string2))
				|| (string1 == null && string2 == null);
	}

	public static boolean safeEquals(Object obj1, Object obj2) {
		return (obj1 != null && obj2 != null && obj1.equals(obj2))
				|| (obj1 == null && obj2 == null);
	}

	public static boolean safeEqualsIgnoreCase(String string1, String string2) {
		return (string1 != null && string2 != null && string1.toLowerCase()
				.equals(string2.toLowerCase()))
				|| (string1 == null && string2 == null);
	}

	public static boolean safeEquals(Color color1, Color color2) {
		return (color1 != null && color2 != null && color1.equals(color2))
				|| (color1 == null && color2 == null);
	}

	public static String firstLetterCapitalized(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		return text.substring(0, 1).toUpperCase()
				+ text.substring(1, text.length());
	}

  public static Font getDefaultFont() {
    String fontFamily = Resources.getInstance().getProperty("defaultfont");
    int fontStyle = Resources.getInstance().getIntProperty("defaultfontstyle", 0);
    int fontSize = Resources.getInstance().getIntProperty("defaultfontsize", 12);
    return new Font(fontFamily, fontStyle, fontSize);
  }

	public static class IntHolder {
		private int value;

		public IntHolder() {
		}

		public IntHolder(int value) {
			this.value = value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public String toString() {
			return new String("IntHolder(") + value + ")";
		}
	}

	public static class BooleanHolder {
		private boolean value;

		public BooleanHolder() {
		}

		public BooleanHolder(boolean initialValue) {
			value = initialValue;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public boolean getValue() {
			return value;
		}
	}

	public static class ObjectHolder {
		Object object;

		public ObjectHolder() {
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public Object getObject() {
			return object;
		}
	}

	public static class Pair {
		Object first;

		Object second;

		public Pair(Object first, Object second) {
			this.first = first;
			this.second = second;
		}

		public Object getFirst() {
			return first;
		}

		public Object getSecond() {
			return second;
		}
	}

	public static boolean safeEquals(BooleanHolder holder, BooleanHolder holder2) {
		return (holder == null && holder2 == null)
				|| (holder != null && holder2 != null && holder.getValue() == holder2
						.getValue());
	}

	public static void setDialogLocationRelativeTo(JDialog dialog, Component c) {
		if (c == null) {
			// perhaps, the component is not yet existing.
			return;
		}
		if (c instanceof NodeView) {
			final NodeView nodeView = (NodeView) c;
			nodeView.getMap().scrollNodeToVisible(nodeView);
			c = nodeView.getMainView();
		}
		final Point compLocation = c.getLocationOnScreen();
		final int cw = c.getWidth();
		final int ch = c.getHeight();

		final Container parent = dialog.getParent();
		final Point parentLocation = parent.getLocationOnScreen();
		final int pw = parent.getWidth();
		final int ph = parent.getHeight();

		final int dw = dialog.getWidth();
		final int dh = dialog.getHeight();

		final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = defaultToolkit.getScreenSize();
		final Insets screenInsets = defaultToolkit.getScreenInsets(dialog
				.getGraphicsConfiguration());

		final int minX = Math.max(parentLocation.x, screenInsets.left);
		final int minY = Math.max(parentLocation.y, screenInsets.top);

		final int maxX = Math.min(parentLocation.x + pw, screenSize.width
				- screenInsets.right);
		final int maxY = Math.min(parentLocation.y + ph, screenSize.height
				- screenInsets.bottom);

		int dx, dy;

		if (compLocation.x + cw < minX) {
			dx = minX;
		} else if (compLocation.x > maxX) {
			dx = maxX - dw;
		} else // component X on screen
		{
			final int leftSpace = compLocation.x - minX;
			final int rightSpace = maxX - (compLocation.x + cw);
			if (leftSpace > rightSpace) {
				if (leftSpace > dw) {
					dx = compLocation.x - dw;
				} else {
					dx = minX;
				}
			} else {
				if (rightSpace > dw) {
					dx = compLocation.x + cw;
				} else {
					dx = maxX - dw;
				}
			}
		}

		if (compLocation.y + ch < minY) {
			dy = minY;
		} else if (compLocation.y > maxY) {
			dy = maxY - dh;
		} else // component Y on screen
		{
			final int topSpace = compLocation.y - minY;
			final int bottomSpace = maxY - (compLocation.y + ch);
			if (topSpace > bottomSpace) {
				if (topSpace > dh) {
					dy = compLocation.y - dh;
				} else {
					dy = minY;
				}
			} else {
				if (bottomSpace > dh) {
					dy = compLocation.y + ch;
				} else {
					dy = maxY - dh;
				}
			}
		}

		dialog.setLocation(dx, dy);
	}

	/**
	 * Creates a default reader that just reads the given file.
	 * 
	 * @throws FileNotFoundException
	 */
	public static Reader getActualReader(Reader pReader)
			throws FileNotFoundException {
		return new BufferedReader(pReader);
	}

	public static void addEscapeActionToDialog(final JDialog dialog) {
		class EscapeAction extends AbstractAction {
			private static final long serialVersionUID = 238333614987438806L;

			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			};
		}
		addEscapeActionToDialog(dialog, new EscapeAction());
	}

	public static void addEscapeActionToDialog(JDialog dialog, Action action) {
		addKeyActionToDialog(dialog, action, "ESCAPE", "end_dialog");
	}

	public static void addKeyActionToDialog(JDialog dialog, Action action,
			String keyStroke, String actionId) {
		action.putValue(Action.NAME, actionId);
		// Register keystroke
		dialog.getRootPane()
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyStroke),
						action.getValue(Action.NAME));

		// Register action
		dialog.getRootPane().getActionMap()
				.put(action.getValue(Action.NAME), action);
	}

	public static Point convertPointToAncestor(Component c, Point p,
			Component destination) {
		int x, y;
		while (c != destination) {
			x = c.getX();
			y = c.getY();

			p.x += x;
			p.y += y;

			c = c.getParent();
		}
		return p;

	}

	public static void convertPointFromAncestor(Component source, Point p,
			Component c) {
		int x, y;
		while (c != source) {
			x = c.getX();
			y = c.getY();

			p.x -= x;
			p.y -= y;

			c = c.getParent();
		}
		;

	}

	public static void convertPointToAncestor(Component source, Point point,
			Class ancestorClass) {
		Component destination = SwingUtilities.getAncestorOfClass(
				ancestorClass, source);
		convertPointToAncestor(source, point, destination);
	}

	interface NameMnemonicHolder {

		/**
		 */
		String getText();

		/**
		 */
		void setText(String replaceAll);

		/**
		 */
		void setMnemonic(char charAfterMnemoSign);

		/**
		 */
		void setDisplayedMnemonicIndex(int mnemoSignIndex);

	}

	private static class ButtonHolder implements NameMnemonicHolder {
		private AbstractButton btn;

		public ButtonHolder(AbstractButton btn) {
			super();
			this.btn = btn;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#getText()
		 */
		public String getText() {
			return btn.getText();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.main.Tools.IAbstractButton#setDisplayedMnemonicIndex(int)
		 */
		public void setDisplayedMnemonicIndex(int mnemoSignIndex) {
			btn.setDisplayedMnemonicIndex(mnemoSignIndex);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#setMnemonic(char)
		 */
		public void setMnemonic(char charAfterMnemoSign) {
			btn.setMnemonic(charAfterMnemoSign);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#setText(java.lang.String)
		 */
		public void setText(String text) {
			btn.setText(text);
		}

	}

	private static class ActionHolder implements NameMnemonicHolder {
		private Action action;

		public ActionHolder(Action action) {
			super();
			this.action = action;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#getText()
		 */
		public String getText() {
			return action.getValue(Action.NAME).toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.main.Tools.IAbstractButton#setDisplayedMnemonicIndex(int)
		 */
		public void setDisplayedMnemonicIndex(int mnemoSignIndex) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#setMnemonic(char)
		 */
		public void setMnemonic(char charAfterMnemoSign) {
			int vk = (int) charAfterMnemoSign;
			if (vk >= 'a' && vk <= 'z')
				vk -= ('a' - 'A');
			action.putValue(Action.MNEMONIC_KEY, new Integer(vk));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.main.Tools.IAbstractButton#setText(java.lang.String)
		 */
		public void setText(String text) {
			action.putValue(Action.NAME, text);
		}

	}

	/**
	 * Ampersand indicates that the character after it is a mnemo, unless the
	 * character is a space. In "Find & Replace", ampersand does not label
	 * mnemo, while in "&About", mnemo is "Alt + A".
	 */
	public static void setLabelAndMnemonic(AbstractButton btn, String inLabel) {
		setLabelAndMnemonic(new ButtonHolder(btn), inLabel);
	}

	/**
	 * Ampersand indicates that the character after it is a mnemo, unless the
	 * character is a space. In "Find & Replace", ampersand does not label
	 * mnemo, while in "&About", mnemo is "Alt + A".
	 */
	public static void setLabelAndMnemonic(Action action, String inLabel) {
		setLabelAndMnemonic(new ActionHolder(action), inLabel);
	}

	private static void setLabelAndMnemonic(NameMnemonicHolder item,
			String inLabel) {
		String rawLabel = inLabel;
		if (rawLabel == null)
			rawLabel = item.getText();
		if (rawLabel == null)
			return;
		item.setText(removeMnemonic(rawLabel));
		int mnemoSignIndex = rawLabel.indexOf("&");
		if (mnemoSignIndex >= 0 && mnemoSignIndex + 1 < rawLabel.length()) {
			char charAfterMnemoSign = rawLabel.charAt(mnemoSignIndex + 1);
			if (charAfterMnemoSign != ' ') {
				// no mnemonics under Mac OS:
				if (!isMacOsX()) {
					item.setMnemonic(charAfterMnemoSign);
					// sets the underline to exactly this character.
					item.setDisplayedMnemonicIndex(mnemoSignIndex);
				}
			}
		}
	}

	public static boolean isMacOsX() {
		boolean underMac = false;
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Mac OS")) {
			underMac = true;
		}
		return underMac;
	}

	public static boolean isLinux() {
		boolean underLinux = false;
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Linux")) {
			underLinux = true;
		}
		return underLinux;
	}

	public static String removeMnemonic(String rawLabel) {
		return rawLabel.replaceFirst("&([^ ])", "$1");
	}

	public static KeyStroke getKeyStroke(final String keyStrokeDescription) {
		if (keyStrokeDescription == null) {
			return null;
		}
		final KeyStroke keyStroke = KeyStroke
				.getKeyStroke(keyStrokeDescription);
		if (keyStroke != null)
			return keyStroke;
		return KeyStroke.getKeyStroke("typed " + keyStrokeDescription);
	}

	public static void restoreAntialiasing(Graphics2D g, Object renderingHint) {
		if (RenderingHints.KEY_ANTIALIASING.isCompatibleValue(renderingHint)) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
		}
	}

	public static void waitForEventQueue() {
		try {
			// wait until AWT thread starts
			// final Exception e = new IllegalArgumentException("HERE");
			if (!EventQueue.isDispatchThread()) {
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						// logger.info("Waited for event queue.");
						// e.printStackTrace();
					};
				});
			} else {
				logger.warning("Can't wait for event queue, if I'm inside this queue!");
			}
		} catch (Exception e) {
			org.rogach.simplymindmap.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * Adapts the font size inside of a component to the zoom
	 * 
	 * @param c
	 *            component
	 * @param zoom
	 *            zoom factor
	 * @param normalFontSize
	 *            "unzoomed" normal font size.
	 * @return a copy of the input font (if the size was effectively changed)
	 *         with the correct scale.
	 */
	public static Font updateFontSize(Font font, float zoom, int normalFontSize) {
		if (font != null) {
			float oldFontSize = font.getSize2D();
			float newFontSize = normalFontSize * zoom;
			if (oldFontSize != newFontSize) {
				font = font.deriveFont(newFontSize);
			}
		}
		return font;
	}

	public static Vector getVectorWithSingleElement(Object obj) {
		Vector nodes = new Vector();
		nodes.add(obj);
		return nodes;
	}

	public static boolean isUnix() {
		return (File.separatorChar == '/') || isMacOsX();
	}

	/**
     */
	public static Clipboard getClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * @param pString
	 * @param pSearchString
	 * @return the amount of occurrences of pSearchString in pString.
	 */
	public static int countOccurrences(String pString, String pSearchString) {
		int amount = 0;
		while (true) {
			final int index = pString.indexOf(pSearchString);
			if (index < 0) {
				break;
			}
			amount++;
			pString = pString.substring(index + pSearchString.length());
		}
		return amount;
	}

	public static String printXmlAction(XmlAction pAction) {
		final String classString = pAction.getClass().getName()
				.replaceAll(".*\\.", "");
		if (pAction instanceof CompoundAction) {
			CompoundAction compound = (CompoundAction) pAction;
			StringBuffer buf = new StringBuffer("[");
			for (Iterator it = compound.getListChoiceList().iterator(); it
					.hasNext();) {
				if (buf.length() > 1) {
					buf.append(',');
				}
				XmlAction subAction = (XmlAction) it.next();
				buf.append(printXmlAction(subAction));
			}
			buf.append(']');
			return classString + " " + buf.toString();
		}
		return classString;
	}

	public static String generateID(String proposedID, HashMap hashMap,
			String prefix) {
		String myProposedID = new String((proposedID != null) ? proposedID : "");
		String returnValue;
		do {
			if (!myProposedID.isEmpty()) {
				// there is a proposal:
				returnValue = myProposedID;
				// this string is tried only once:
				myProposedID = "";
			} else {
				/*
				 * The prefix is to enable the id to be an ID in the sense of
				 * XML/DTD.
				 */
				returnValue = prefix
						+ Integer.toString(Tools.ran.nextInt(2000000000));
			}
		} while (hashMap.containsKey(returnValue));
		return returnValue;
	}

	/**
	 * Call this method, if you don't know, if you are in the event thread or
	 * not. It checks this and calls the invokeandwait or the runnable directly.
	 * 
	 * @param pRunnable
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void invokeAndWait(Runnable pRunnable)
			throws InvocationTargetException, InterruptedException {
		if (EventQueue.isDispatchThread()) {
			pRunnable.run();
		} else {
			EventQueue.invokeAndWait(pRunnable);
		}
	}
  
  public static Color showCommonJColorChooserDialog(Component component,
			String title, Color initialColor) throws HeadlessException {

		final JColorChooser pane = new JColorChooser();
		pane.setColor(initialColor);

		ColorTracker ok = new ColorTracker(pane);
		JDialog dialog = JColorChooser.createDialog(component, title, true,
				pane, ok, null);
		dialog.addWindowListener(new Closer());
		dialog.addComponentListener(new DisposeOnClose());

		dialog.show(); // blocks until user brings dialog down...

		return ok.getColor();
	}
    
  private static class ColorTracker implements ActionListener, Serializable {
		JColorChooser chooser;
		Color color;

		public ColorTracker(JColorChooser c) {
			chooser = c;
		}

		public void actionPerformed(ActionEvent e) {
			color = chooser.getColor();
		}

		public Color getColor() {
			return color;
		}
	}
      
  static class Closer extends WindowAdapter implements Serializable {
		public void windowClosing(WindowEvent e) {
			Window w = e.getWindow();
			w.setVisible(false);
		}
	}

	static class DisposeOnClose extends ComponentAdapter implements
			Serializable {
		public void componentHidden(ComponentEvent e) {
			Window w = (Window) e.getComponent();
			w.dispose();
		}
	}

}
