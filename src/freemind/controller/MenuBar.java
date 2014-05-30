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
/*$Id: MenuBar.java,v 1.24.14.17.2.22 2008/11/12 21:44:33 christianfoltin Exp $*/

package freemind.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.modes.ModeController;
import freemind.view.MapModule;
import java.util.logging.Logger;

/**
 * This is the menu bar for FreeMind. Actions are defined in MenuListener.
 * Moreover, the StructuredMenuHolder of all menus are hold here.
 * */
public class MenuBar extends JMenuBar {

	private static java.util.logging.Logger logger;
	public static final String MENU_BAR_PREFIX = "menu_bar/";
	public static final String GENERAL_POPUP_PREFIX = "popup/";

	public static final String POPUP_MENU = GENERAL_POPUP_PREFIX + "popup/";

	public static final String INSERT_MENU = MENU_BAR_PREFIX + "insert/";
	public static final String NAVIGATE_MENU = MENU_BAR_PREFIX + "navigate/";
	public static final String VIEW_MENU = MENU_BAR_PREFIX + "view/";
	public static final String HELP_MENU = MENU_BAR_PREFIX + "help/";
	public static final String MINDMAP_MENU = MENU_BAR_PREFIX + "mindmaps/";
	private static final String MENU_MINDMAP_CATEGORY = MINDMAP_MENU
			+ "mindmaps";
	public static final String MODES_MENU = MINDMAP_MENU;
	// public static final String MODES_MENU = MENU_BAR_PREFIX+"modes/";
	public static final String EDIT_MENU = MENU_BAR_PREFIX + "edit/";
	public static final String FILE_MENU = MENU_BAR_PREFIX + "file/";
	public static final String FORMAT_MENU = MENU_BAR_PREFIX + "format/";
	public static final String EXTRAS_MENU = MENU_BAR_PREFIX + "extras/";

	private StructuredMenuHolder menuHolder;

	JPopupMenu mapsPopupMenu;

	public MenuBar() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		// updateMenus();
	}// Constructor

	/**
	 * This is the only public method. It restores all menus.
	 * 
	 * @param newModeController
	 */
	public void updateMenus(ModeController newModeController) {
		this.removeAll();

		menuHolder = new StructuredMenuHolder();

		menuHolder.addCategory(FILE_MENU + "open");
		menuHolder.addCategory(FILE_MENU + "close");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "export");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "import");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "print");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "last");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "quit");

		menuHolder.addCategory(EDIT_MENU + "undo");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "select");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "paste");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "edit");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "find");

		// view menu
		menuHolder.addMenu(new JMenu(Resources.getInstance().getResourceString("menu_view")),
				VIEW_MENU + ".");

		// insert menu
		menuHolder.addMenu(new JMenu(Resources.getInstance().getResourceString("menu_insert")),
				INSERT_MENU + ".");
		menuHolder.addCategory(INSERT_MENU + "nodes");
		menuHolder.addSeparator(INSERT_MENU);
		menuHolder.addCategory(INSERT_MENU + "icons");
		menuHolder.addSeparator(INSERT_MENU);

		// navigate menu
		menuHolder.addMenu(new JMenu(Resources.getInstance().getResourceString("menu_navigate")),
				NAVIGATE_MENU + ".");

		// extras menu
		menuHolder.addMenu(new JMenu(Resources.getInstance().getResourceString("menu_extras")),
				EXTRAS_MENU + ".");
		menuHolder.addCategory(EXTRAS_MENU + "first");

		// mapsmenu.setMnemonic(KeyEvent.VK_M);
		menuHolder.addCategory(MINDMAP_MENU + "navigate");
		menuHolder.addSeparator(MINDMAP_MENU);
		menuHolder.addCategory(MENU_MINDMAP_CATEGORY);
		menuHolder.addSeparator(MINDMAP_MENU);
		// Modesmenu
		menuHolder.addCategory(MODES_MENU);

		// maps popup menu
		mapsPopupMenu = new FreeMindPopupMenu();
		mapsPopupMenu.setName(Resources.getInstance().getResourceString("mindmaps"));
		menuHolder.addCategory(POPUP_MENU + "navigate");
		// menuHolder.addSeparator(POPUP_MENU);

		addAdditionalPopupActions();
		// the modes:
		newModeController.updateMenus(menuHolder);
		menuHolder.updateMenus(this, MENU_BAR_PREFIX);
		menuHolder.updateMenus(mapsPopupMenu, GENERAL_POPUP_PREFIX);

	}

	private void addAdditionalPopupActions() {
		menuHolder.addSeparator(POPUP_MENU);
	}

	private void addOptionSet(Action action, String[] textIDs, JMenu menu,
			String selectedTextID) {
		ButtonGroup group = new ButtonGroup();
		for (int optionIdx = 0; optionIdx < textIDs.length; optionIdx++) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
			item.setText(Resources.getInstance().getResourceString(textIDs[optionIdx]));
			item.setActionCommand(textIDs[optionIdx]);
			group.add(item);
			menu.add(item);
			if (selectedTextID != null) {
				item.setSelected(selectedTextID.equals(textIDs[optionIdx]));
			}
			// keystroke present?
			String keystroke = Resources.getInstance().common.getAdjustableProperty(
					"keystroke_" + textIDs[optionIdx]);
			if (keystroke != null)
				item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
		}
	}

	JPopupMenu getMapsPopupMenu() { // visible only in controller package
		return mapsPopupMenu;
	}

	/**
	 * This method simpy copy's all elements of the source Menu to the end of
	 * the second menu.
	 */
	private void copyMenuItems(JMenu source, JMenu dest) {
		Component[] items = source.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			dest.add(items[i]);
		}
	}

	/**
     */
	public StructuredMenuHolder getMenuHolder() {
		return menuHolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JMenuBar#processKeyBinding(javax.swing.KeyStroke,
	 * java.awt.event.KeyEvent, int, boolean)
	 */
	public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition,
			boolean pressed) {
		return super.processKeyBinding(ks, e, condition, pressed);
	}

}
