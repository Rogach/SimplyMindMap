/*FreeMindget - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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


package freemind.modes.mindmapmode;

import freemind.main.FreeMindCommon;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import org.rogach.simplymindmap.nanoxml.XMLParseException;
import org.rogach.simplymindmap.util.Tools;

public class MindMapMapModel extends MapAdapter {

	public static final String MAP_INITIAL_START = "<map version=\"";
	public static final String RESTORE_MODE_MIND_MAP = "MindMap:";
	public static final String FREEMIND_VERSION_UPDATER_XSLT = "freemind/modes/mindmapmode/freemind_version_updater.xslt";
	private MindMapLinkRegistry linkRegistry;
	/**
	 * The current version and all other version that don't need XML update for
	 * sure.
	 */
	public static final String EXPECTED_START_STRINGS[] = {
			MAP_INITIAL_START + "1.0.1" + "\"",
			MAP_INITIAL_START + "0.7.1\"" };

	//
	// Constructors
	//

	public MindMapMapModel(FreeMindCommon common, ModeController modeController) {
		this(null, common, modeController);
	}

	public MindMapMapModel(MindMapNodeModel root, FreeMindCommon common,
			ModeController modeController) {
		super(modeController);

		// register new LinkRegistryAdapter
		linkRegistry = new MindMapLinkRegistry();

		if (root == null)
			root = new MindMapNodeModel(common.getResourceString("new_mindmap"),
					 this);
    updateMapReferenceInNodes(root);
		setRoot(root);
	}
  
  private void updateMapReferenceInNodes(MindMapNodeModel node) {
    node.setMap(this);
    for (MindMapNodeModel child : node.getChildren()) {
      updateMapReferenceInNodes(child);
    }
  }

	//

	public MindMapLinkRegistry getLinkRegistry() {
		return linkRegistry;
	}

	public void changeNode(MindMapNode node, String newText) {
    node.setUserObject(newText);
		nodeChanged(node);
	}

	public static class StringReaderCreator implements ReaderCreator {

		private final String mString;

		public StringReaderCreator(String pString) {
			mString = pString;
		}

		public Reader createReader() throws FileNotFoundException {
			return new StringReader(mString);
		}

		public String toString() {
			return mString;
		}
	}

	public interface ReaderCreator {
		Reader createReader() throws FileNotFoundException;
	}

	MindMapNodeModel loadTree(ReaderCreator pReaderCreator)
			throws XMLParseException, IOException {
		return loadTree(pReaderCreator, true);
	}

	public MindMapNodeModel loadTree(ReaderCreator pReaderCreator,
			boolean pAskUserBeforeUpdate) throws XMLParseException, IOException {
		int versionInfoLength;
		versionInfoLength = EXPECTED_START_STRINGS[0].length();
		// reading the start of the file:
		StringBuffer buffer = readFileStart(pReaderCreator.createReader(),
				versionInfoLength);
		// the resulting file is accessed by the reader:
		Reader reader = null;
		for (int i = 0; i < EXPECTED_START_STRINGS.length; i++) {
			versionInfoLength = EXPECTED_START_STRINGS[i].length();
			String mapStart = "";
			if (buffer.length() >= versionInfoLength) {
				mapStart = buffer.substring(0, versionInfoLength);
			}
			if (mapStart.startsWith(EXPECTED_START_STRINGS[i])) {
				// actual version:
				reader = Tools.getActualReader(pReaderCreator.createReader());
				break;
			}
		}
    if (reader == null) {
      reader = Tools.getActualReader(pReaderCreator.createReader());
    }
		try {
			HashMap IDToTarget = new HashMap();
			return (MindMapNodeModel) mModeController.createNodeTreeFromXml(
					reader, IDToTarget);
			// MindMapXMLElement mapElement = new
			// MindMapXMLElement(mModeController);
			// mapElement.parseFromReader(reader);
			// // complete the arrow links:
			// mapElement.processUnfinishedLinks(getLinkRegistry());
			// // we wait with "invokeHooksRecursively" until the map is fully
			// // registered.
			// return (MindMapNodeModel) mapElement.getMapChild();
		} catch (Exception ex) {
			String errorMessage = "Error while parsing file:" + ex;
			System.err.println(errorMessage);
			freemind.main.Resources.getInstance().logException(ex);
			MindMapXMLElement mapElement = new MindMapXMLElement(
					mModeController);
			NodeAdapter result = mapElement.createNodeAdapter(null);
			result.setText(errorMessage);
			return (MindMapNodeModel) result;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Returns pMinimumLength bytes of the files content.
	 * 
	 * @return an empty string buffer, if something fails.
	 */
	private StringBuffer readFileStart(Reader pReader, int pMinimumLength) {
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// get the file start into the memory:
			in = new BufferedReader(pReader);
			String str;
			while ((str = in.readLine()) != null) {
				buffer.append(str);
				if (buffer.length() >= pMinimumLength)
					break;
			}
			in.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return new StringBuffer();
		}
		return buffer;
	}
  
  public String getAsPlainText(List mindMapNodes) {
		// Returns success of the operation.
		try {
			StringWriter stringWriter = new StringWriter();
			BufferedWriter fileout = new BufferedWriter(stringWriter);

			for (ListIterator it = mindMapNodes.listIterator(); it.hasNext();) {
				((MindMapNodeModel) it.next()).saveTXT(fileout,/* depth= */0);
			}

			fileout.close();
			return stringWriter.toString();

		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public boolean saveTXT(MindMapNodeModel rootNodeOfBranch, File file) {
		// Returns success of the operation.
		try {
			BufferedWriter fileout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			rootNodeOfBranch.saveTXT(fileout,/* depth= */0);
			fileout.close();
			return true;

		} catch (Exception e) {
			System.err.println("Error in MindMapMapModel.saveTXT(): ");
			freemind.main.Resources.getInstance().logException(e);
			return false;
		}
	}


}
