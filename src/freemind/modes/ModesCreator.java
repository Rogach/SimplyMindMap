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


package freemind.modes;

import freemind.main.Resources;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * This class creates all the modes that are available. To add your own mode,
 * simply import it, and create it in getAllModes() (just do the same whats done
 * with MindMapMode). Thats all!
 */
public class ModesCreator {

	/**
	 * Contains translated mode name => Mode instances
	 */
	private Map mCreatedModes;

	/**
	 * Contains a name translation. Mode name => Class Name
	 */
	private Map modesTranslation;

	private static Logger logger;

	public ModesCreator() {
	}

	public Set getAllModes() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass().getName());
		}
		if (mCreatedModes == null) {
			mCreatedModes = new TreeMap();
			modesTranslation = new HashMap();
			String modestring = Resources.getInstance().getProperty("modes_since_0_8_0");

			StringTokenizer tokens = new StringTokenizer(modestring, ",");

			while (tokens.hasMoreTokens()) {
				String modename = tokens.nextToken();
				String modeAlias = tokens.nextToken();
				mCreatedModes.put(modename, null);
				modesTranslation.put(modeAlias, modename);
			}
			logger.info("Modes:" + mCreatedModes.keySet());
		}
		return modesTranslation.keySet();
	}

	/**
	 * Creates a new ModeController.
	 * 
	 */
	public Mode getMode(String modeAlias) {
		getAllModes();
		if (!modesTranslation.containsKey(modeAlias)) {
			throw new IllegalArgumentException("Unknown mode " + modeAlias);
		}
		String modeName = (String) modesTranslation.get(modeAlias);
		if (mCreatedModes.get(modeName) == null) {
			try {
				Mode mode = null;
				mode = (Mode) Class.forName(modeName).newInstance();
				logger.info("Initializing mode " + modeAlias);
				logger.info("Done: Initializing mode " + modeAlias);
				mCreatedModes.put(modeName, mode);
			} catch (Exception ex) {
				logger.severe("Mode " + modeName + " could not be loaded.");
				freemind.main.Resources.getInstance().logException(ex);
			}
		}
		return (Mode) mCreatedModes.get(modeName);
	}

}
