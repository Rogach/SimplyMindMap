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
/*$Id: MindMapLinkRegistry.java,v 1.6.18.1.16.4 2008/12/09 21:09:43 christianfoltin Exp $*/

package org.rogach.simplymindmap.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Vector;
import org.rogach.simplymindmap.util.Tools;

/**
 * Interface for the registry, which manages the ids of nodes and the existing
 * links in a map. Thus, this interface is bound to a map model, because other
 * maps have a different registry.
 */
public class MindMapLinkRegistry {

	/** source -> vector of links with same source */
	protected HashMap mSourceToLinks = new HashMap();

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Attributes /////
	// //////////////////////////////////////////////////////////////////////////////////////

	/** MindMapNode = Target -> ID. */
	protected HashMap<MindMapNode, String> mTargetToId;
	/** MindMapNode-> ID. */
	protected HashMap<String, MindMapNode> mIdToTarget;
	/** id -> vector of links whose TargetToID.get(target) == id. */
	protected HashMap<String, Vector<Object>> mIdToLinks;
	/** id -> link */
	protected HashMap mIdToLink;
	protected HashSet<String> mLocallyLinkedIds;

	public MindMapLinkRegistry() {
		mTargetToId = new HashMap<>();
		mIdToTarget = new HashMap<>();
		mIdToLinks = new HashMap<>();
		mIdToLink = new HashMap<>();
		mLocallyLinkedIds = new HashSet<>();
	}

	/**
	 * This can be used, if the id has to be known, before a node can be
	 * labeled.
	 */
	public String generateUniqueID(String proposedID) {
		return Tools.generateID(proposedID, mIdToLinks, "ID_");
	}

	/**
	 * This can be used, if the id has to be known, before a link can be labled.
	 */
	public String generateUniqueLinkId(String proposedID) {
		return Tools.generateID(proposedID, mIdToLink, "Arrow_ID_");
	}

	public String registerLinkTarget(MindMapNode pTarget) {
		return _registerLinkTarget(pTarget);

	}

	/**
	 * The second variant of the main method. The difference is that here an ID
	 * is proposed, but has not to be taken, though.
	 */
	public String registerLinkTarget(MindMapNode pTarget, String pProposedID) {
		return _registerLinkTarget(pTarget, pProposedID);
	}

	/**
	 * The main method. Registeres a node with a new (or an existing) node-id.
	 */
	public String _registerLinkTarget(MindMapNode target) {
		return _registerLinkTarget(target, null);
	}

	public String _registerLinkTarget(MindMapNode target, String proposedID) {
		// id already exists?
		if (mTargetToId.containsKey(target)) {
			String id = (String) mTargetToId.get(target);
			if (id != null)
				return id;
			// blank state.
			// is equal to no state.
		}
		// generate new id:
		String newId = generateUniqueID(proposedID);
		mTargetToId.put(target, newId);
		mIdToTarget.put(newId, target);

		// logger.fine("Register target node:"+target+", with ID="+newID);
		/*
		 * This is to allocate the link target in the IDToLinks map!.
		 */
		getAssignedLinksVector(newId);
		return newId;
	}

	/**
	 * @param node
	 * @return null, if not registered.
	 */
	public String getState(MindMapNode node) {
		if (mTargetToId.containsKey(node))
			return mTargetToId.get(node);
		return null;
	}

	/**
	 * Reverses the getLabel method: searches for a node with the id given as
	 * the argument.
	 */
	public MindMapNode getTargetForId(String ID) {
		return mIdToTarget.get(ID);
	}

	/** @return a Vector of {@link MindMapLink}s */
	private Vector getAssignedLinksVector(String newId) {
		String id = newId;
		// look, if target is already present:
		Vector<Object> vec;
		if (mIdToLinks.containsKey(id)) {
			vec = mIdToLinks.get(id);
		} else {
			vec = new Vector<>();
			mIdToLinks.put(id, vec);
		}

		// Dimitry : logger is a performance killer here
		// //logger.fine("getAssignedLinksVector "+vec);
		return vec;
	}

	public String getLabel(MindMapNode target) {
		return getState(target);
	}

	public void registerLocalHyperlinkId(String pTargetId) {
		mLocallyLinkedIds.add(pTargetId);
	}

	public boolean isTargetOfLocalHyperlinks(String pTargetId) {
		return mLocallyLinkedIds.contains(pTargetId);
	}
  
  public void deregisterLinkTarget(MindMapNode target)
			throws java.lang.IllegalArgumentException {
		// and process my sons:
		for (ListIterator e = target.childrenUnfolded(); e.hasNext();) {
			MindMapNode child = (MindMapNode) e.next();
			deregisterLinkTarget(child);
		}
		String id = getState(target);
		if (id != null) {
			// logger.fine("Deregister target node:"+target);
			mTargetToId.remove(target);
			mIdToTarget.remove(id);
			mIdToLinks.remove(id);
		}
	}
  
  public HashMap<String, MindMapNode> getIdToTarget() {
      return mIdToTarget;
  }

}
