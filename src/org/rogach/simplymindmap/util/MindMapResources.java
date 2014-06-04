package org.rogach.simplymindmap.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class MindMapResources {
  
  private final String DEFAULT_LANGUAGE = "en";
  
  private final Properties properties;
  private final Properties languageResources;
  private final Properties defaultLanguageResources;
  
  public MindMapResources() {
    this(new Properties());
  }
  
  public MindMapResources(Properties userProperties) {
    properties = readDefaultProperties();
    properties.putAll(userProperties);
    languageResources = readLanguageResources();
    defaultLanguageResources = readDefaultLanguageResources();
  }
  
  private Properties readDefaultProperties() {
    Properties defaultProperties = new Properties();
    try {
      URL defaultPropertiesURL = this.getClass().getResource("/org/rogach/simplymindmap/resources/simplymindmap.properties");
      try (InputStream in = defaultPropertiesURL.openStream()) {
        defaultProperties.load(in);
      }
    } catch (IOException ex) {
      Logger.getLogger(MindMapResources.class.getName()).log(Level.SEVERE, null, ex);
    }
    return defaultProperties;
  }
  
  private Properties readLanguageResources() {
    String lang = getProperty(PropertyKey.LANGUAGE);
    if (lang == null || lang.equals("automatic")) {
      lang = Locale.getDefault().getLanguage() + "_"
          + Locale.getDefault().getCountry();
      if (getLanguageResources(lang) == null) {
        lang = Locale.getDefault().getLanguage();
        if (getLanguageResources(lang) == null) {
          // default is english.
          lang = DEFAULT_LANGUAGE;
        }
      }
    }
    if ("no".equals(lang)) {
      // Bugs item #1935818
      lang = "nb";
    }
    Properties languageResources = getLanguageResources(lang);
    return (languageResources == null) ? new Properties() : languageResources;
  }
  
  private Properties readDefaultLanguageResources() {
    Properties languageResources = getLanguageResources(DEFAULT_LANGUAGE);
    return (languageResources == null) ? new Properties() : languageResources;
  }
  
  private Properties getLanguageResources(String lang) {
    URL resourcesURL = this.getClass().getClassLoader().getResource(
            "org/rogach/simplymindmap/resources/Resources_" + lang + ".properties");
    if (resourcesURL == null) {
      return null;
    }
    try (InputStream in = resourcesURL.openStream()) {
      if (in == null) {
        return null;
      } else {
        Properties languageResources = new Properties();
        languageResources.load(in);
        return languageResources;
      }
    } catch (IOException ex) {
      Logger.getLogger(MindMapResources.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }
  
  // ==== RESOURCE GETTERS ====
    
  public URL getResource(String resource) {
		return this.getClass().getClassLoader().getResource(resource);
	}
  
  public String getText(String resourceKey) {
    String value = languageResources.getProperty(resourceKey);
    if (value == null) {
      value = defaultLanguageResources.getProperty(resourceKey);
    }
    if (value == null) {
      value = resourceKey;
    }
    // remove mnemonic hints (we don't use mnemonics)
    value = value.replace("&", "");
    return value;
  }
  
	public String format(String resourceKey, Object[] messageArguments) {
		MessageFormat formatter =
			new MessageFormat(getText(resourceKey));
		String stringResult = formatter.format(messageArguments);
		return stringResult;
	}
  
  // ==== PROPERTY GETTERS ====

  public String getProperty(PropertyKey key) {
    return properties.getProperty(key.getKey());
  }
  
  public String unsafeGetProperty(String keyName) {
    return properties.getProperty(keyName);
  }
  
  public boolean getBoolProperty(PropertyKey key) {
		String boolProperty = getProperty(key);
		return Tools.safeEquals("true", boolProperty);
  }
  
  public int getIntProperty(PropertyKey key, int defaultValue) {
    try {
			return Integer.parseInt(getProperty(key));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}
	
	public long getLongProperty(PropertyKey key, long defaultValue) {
		try {
			return Long.parseLong(getProperty(key));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}
  
  public ImageIcon getIcon(String imageName) {
    URL iconURL = getResource("org/rogach/simplymindmap/images/" + imageName);
    if (iconURL != null) {
      return new ImageIcon(iconURL);
    } else {
      return new ImageIcon("org/rogach/simplymindmap/images/IconNotFound.png");
    }
  }
}
