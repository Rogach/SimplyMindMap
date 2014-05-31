/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 10.01.2006
 */
/*$Id: FreeMindCommon.java,v 1.1.2.2.2.39 2009/05/18 19:47:57 christianfoltin Exp $*/
package freemind.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author foltin
 * 
 */
public class FreeMindCommon {
  public static final String POSTFIX_TRANSLATE_ME = "[translate me]";

	private class FreeMindResourceBundle extends ResourceBundle {
		private PropertyResourceBundle languageResources;
		private PropertyResourceBundle defaultResources;

		FreeMindResourceBundle() {
			try {
				String lang = getProperty(ResourceKeys.RESOURCE_LANGUAGE);
				if (lang == null || lang.equals("automatic")) {
					lang = Locale.getDefault().getLanguage() + "_"
							+ Locale.getDefault().getCountry();
					if (getLanguageResources(lang) == null) {
						lang = Locale.getDefault().getLanguage();
						if (getLanguageResources(lang) == null) {
							// default is english.
							lang = ResourceKeys.DEFAULT_LANGUAGE;
						}
					}
				}
				if ("no".equals(lang)) {
					// Bugs item #1935818
					lang = "nb";
				}
				languageResources = getLanguageResources(lang);
				/*
				 * fc, 26.4.2008. the following line is a bug, as the
				 * defaultResources are used, even, when a single string is
				 * missing inside a bundle and not only, when the complete
				 * bundle is missing.
				 */
				// if(languageResources == null)
				defaultResources = getLanguageResources(ResourceKeys.DEFAULT_LANGUAGE);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				logger.severe("Error loading Resources");
			}
			// printResourceTable();
		}

		/**
		 * @throws IOException
		 */
		private PropertyResourceBundle getLanguageResources(String lang)
				throws IOException {
			URL systemResource = this.getClass().getClassLoader().getResource("Resources_" + lang
					+ ".properties");
			if (systemResource == null) {
				return null;
			}
			InputStream in = systemResource.openStream();
			if (in == null) {
				return null;
			}
			PropertyResourceBundle bundle = new PropertyResourceBundle(in);
			in.close();
			return bundle;
		}

		protected Object handleGetObject(String key) {
			try {
				return languageResources.getString(key);
			} catch (Exception ex) {
				if(key != null && key.startsWith("__")) {
					// private string, only translate on demand
					return key;
				} else {
					//logger.severe("Warning - resource string not found:\n" + key);
					return defaultResources.getString(key) + FreeMindCommon.POSTFIX_TRANSLATE_ME;
				}
			}
		}

		public Enumeration getKeys() {
			return defaultResources.getKeys();
		}

		String getResourceString(String key) {
			try {
				return getString(key);
			} catch (Exception ex) {
				return key;
			}
		}

		String getResourceString(String key, String pDefault) {
			try {
				try {
					return languageResources.getString(key);
				} catch (Exception ex) {
					return defaultResources.getString(key)
							+ FreeMindCommon.POSTFIX_TRANSLATE_ME;
				}
			} catch (Exception e) {
				// logger.info(key+" not found.");
				return pDefault;
			}
		}
	}
  
  private final Properties properties;

	private FreeMindResourceBundle resources;

	private static Logger logger = null;

	/**
	 * 
	 */
	public FreeMindCommon(Properties properties) {
		super();
    this.properties = properties;
		if (logger == null)
			logger = Logger.getLogger(this.getClass().getName());
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	private void setDefaultProperty(String key, String value) {
		
	}

	/** Returns the ResourceBundle with the current language */
	public ResourceBundle getResources() {
		if (resources == null) {
			resources = new FreeMindResourceBundle();
		}
		return resources;
	}

	public String getResourceString(String key) {
		return ((FreeMindResourceBundle) getResources()).getResourceString(key);
	}

	public String getResourceString(String key, String pDefault) {
		return ((FreeMindResourceBundle) getResources()).getResourceString(key,
				pDefault);
	}

	public void clearLanguageResources() {
		resources = null;
	}

	public String getAdjustableProperty(final String label) {
		String value = getProperty(label);
		if (value == null) {
			return value;
		}
		if (value.startsWith("?") && !value.equals("?")) {
			// try to look in the language specific properties
			String localValue = ((FreeMindResourceBundle) getResources())
					.getResourceString(ResourceKeys.LOCAL_PROPERTIES + label, null);
			value = localValue == null ? value.substring(1).trim() : localValue;
			setDefaultProperty(label, value);
		}
		return value;
	}

	public void loadUIProperties(Properties props) {
		// props.put(FreeMind.RESOURCES_BACKGROUND_COLOR,
		// Tools.colorToXml(UIManager.getColor("text")));
		// props.put(FreeMind.RESOURCES_NODE_TEXT_COLOR,
		// Tools.colorToXml(UIManager.getColor("textText")));
		// props.put(FreeMind.RESOURCES_SELECTED_NODE_COLOR,
		// Tools.colorToXml(UIManager.getColor("textHighlight")));
		// props.put(FreeMind.RESOURCES_SELECTED_NODE_TEXT_COLOR,
		// Tools.colorToXml(UIManager.getColor("textHighlightText")));
	}

}
