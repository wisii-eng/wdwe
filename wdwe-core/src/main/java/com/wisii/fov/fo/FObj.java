/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.wisii.fov.fo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fo.extensions.ExtensionAttachment;
import com.wisii.fov.fo.flow.Marker;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.properties.PropertyMaker;
import com.wisii.fov.util.QName;

/** Base class for representation of formatting objects and their processing. */
public abstract class FObj extends FONode implements Constants
{

	/** the list of property makers */
	private static PropertyMaker[] propertyListTable = null;

	/** The immediate child nodes of this node. */
	protected List childNodes = null;

	/** The list of extension attachments, null if none */
	private List extensionAttachments = null;

	/** The map of foreign attributes, null if none */
	private Map foreignAttributes = null;

	/** Used to indicate if this FO is either an Out Of Line FO (see rec)
		or a descendant of one.  Used during validateChildNode() FO validation.
	 */
	private boolean isOutOfLineFODescendant = false;

	/** Markers added to this element. */
	protected Map markers = null;

	static
	{
		propertyListTable = new PropertyMaker[Constants.PROPERTY_COUNT + 1];
		PropertyMaker[] list = FOPropertyMapping.getGenericMappings();
		for (int i = 1; i < list.length; i++)
		{
			if (list[i] != null)
				propertyListTable[i] = list[i];
		}
	}

	/**
	 * Create a new formatting object. All formatting object classes extend this class.
	 * @param parent the parent node
	 */
	public FObj(FONode parent)
	{
		super(parent);

		// determine if isOutOfLineFODescendant should be set
		if (parent != null && parent instanceof FObj)
		{
			if (((FObj)parent).getIsOutOfLineFODescendant())
				isOutOfLineFODescendant = true;
			else
			{
				int foID = getNameId();
				if (foID == FO_FLOAT || foID == FO_FOOTNOTE || foID == FO_FOOTNOTE_BODY)
					isOutOfLineFODescendant = true;
			}
		}
	}

	/** @see com.wisii.fov.fo.FONode#clone(FONode, boolean)   */
	public FONode clone(FONode parent, boolean removeChildren) throws FOVException
	{
		FObj fobj = (FObj)super.clone(parent, removeChildren);
		if (removeChildren)
			fobj.childNodes = null;
		return fobj;
	}

	/**
	 * Returns the PropertyMaker for a given property ID.
	 * @param propId the property ID
	 * @return the requested Property Maker
	 */
	public static PropertyMaker getPropertyMakerFor(int propId)
	{
		return propertyListTable[propId];
	}

	/**
	 * @see com.wisii.fov.fo.FONode#processNode
	 */
	public void processNode(String elementName, Locator locator, Attributes attlist, PropertyList pList) throws FOVException
	{
		setLocator(locator);
		//添加：by 李晓光 2010-3-5
		//用于统计所有得层信息。
		if(attlist != null){
			for (int i = 0; i < attlist.getLength(); i++) {
				String value = attlist.getValue(i);
				if(value != null && value.startsWith("rgb(")){
					value = value.charAt(value.length() - 2) + "";
					try{
						int layer = Integer.parseInt(value);
						getUserAgent().addLayer(layer);
					}catch (Exception e) {
						
					}
				}
			}
		}
		//添加：by 李晓光 2010-3-5
		pList.addAttributesToList(attlist);
		if (!inMarker() || "marker".equals(elementName))
		{
			pList.setWritingMode();
			bind(pList);
		}
	}

	/**
	 * Create a default property list for this element.
	 * @see com.wisii.fov.fo.FONode
	 */
	protected PropertyList createPropertyList(PropertyList parent, FOEventHandler foEventHandler) throws FOVException
	{
		return foEventHandler.getPropertyListMaker().make(this, parent);
	}

	/**
	 * Bind property values from the property list to the FO node.
	 * Must be overridden in all FObj subclasses that have properties applying to it.
	 * @param pList the PropertyList where the properties can be found.
	 * @throws FOVException if there is a problem binding the values
	 */
	public void bind(PropertyList pList) throws FOVException
	{
	}

	/**
	 * Setup the id for this formatting object.
	 * Most formatting objects can have an id that can be referenced.
	 * This methods checks that the id isn't already used by another fo and sets the id attribute of this object.
	 * @param id ID to check
	 * @throws ValidationException if the ID is already defined elsewhere
	 */
	protected void checkId(String id) throws ValidationException
	{
		if (!inMarker() && !id.equals(""))
		{
			Set idrefs = getFOEventHandler().getIDReferences();
			if (!idrefs.contains(id))
				idrefs.add(id);
			else
			{
				throw new ValidationException("属性标识 \"" + id
											  + "\" 使用前; 标识值必须是惟一的"
											  + " 在文件中.", locator);
			}
		}
	}

	/**
	 * Returns Out Of Line FO Descendant indicator.
	 * @return true if Out of Line FO or Out Of Line descendant, false otherwise
	 */
	public boolean getIsOutOfLineFODescendant()
	{
		return isOutOfLineFODescendant;
	}

	/** @see com.wisii.fov.fo.FONode#addChildNode(FONode)  */
	protected void addChildNode(FONode child) throws FOVException
	{
		
		if (canHaveMarkers() && child.getNameId() == FO_MARKER)
			addMarker((Marker)child);
		else
		{
			ExtensionAttachment attachment = child.getExtensionAttachment();
			if (attachment != null)
			{
				// This removes the element from the normal children, so no layout manager is being created for them
				// as they are only additional information.
				addExtensionAttachment(attachment);
			}
			else
			{
				if (childNodes == null)
					childNodes = new java.util.ArrayList();
				childNodes.add(child);
			}
		}
	}

	protected static void addChildTo(FONode child, FObj parent) throws FOVException
	{
		parent.addChildNode(child);
	}

	/** @see com.wisii.fov.fo.FONode#removeChild(com.wisii.fov.fo.FONode) */
	public void removeChild(FONode child)
	{
		if (childNodes != null)
			childNodes.remove(child);
	}

	/**
	 * Find the nearest parent, grandparent, etc. FONode that is also an FObj
	 * @return FObj the nearest ancestor FONode that is an FObj
	 */
	public FObj findNearestAncestorFObj()
	{
		FONode par = parent;
		while (par != null && !(par instanceof FObj))
			par = par.parent;
		return (FObj)par;
	}

	/**
	 * Check if this formatting object generates reference areas.
	 * @return true if generates reference areas
	 * @todo see if needed
	 */
	public boolean generatesReferenceAreas()
	{
		return false;
	}

	/** @see com.wisii.fov.fo.FONode#getChildNodes()     */
	public ListIterator getChildNodes()
	{
		
		if (childNodes != null)
			return childNodes.listIterator();
		return null;
	}

	/**
	 * Return an iterator over the object's childNodes starting at the passed-in node.
	 * @param childNode First node in the iterator
	 * @return A ListIterator or null if childNode isn't a child of this FObj.
	 */
	public ListIterator getChildNodes(FONode childNode)
	{
		if (childNodes != null)
		{
			int i = childNodes.indexOf(childNode);
			if (i >= 0)
				return childNodes.listIterator(i);
		}
		return null;
	}

	/**
	 * Return a FONode based on the index in the list of childNodes.
	 * @param nodeIndex index of the node to return
	 * @return the node or null if the index is invalid
	 */
	public FONode getChildNodeAt(int nodeIndex)
	{
		if (childNodes != null)
		{
			if (nodeIndex >= 0 && nodeIndex < childNodes.size())
				return (FONode)childNodes.get(nodeIndex);
		}
		return null;
	}

	/**
	 * Notifies a FObj that one of it's children is removed.
	 * This method is subclassed by Block to clear the firstInlineChild variable.
	 * @param node the node that was removed
	 */
	protected void notifyChildRemoval(FONode node)
	{
		//nop
	}

	/**
	 * Add the marker to this formatting object.
	 * If this object can contain markers it checks that the marker has a unique class-name for this object and that it is the first child.
	 * @param marker Marker to add.
	 */
	protected void addMarker(Marker marker)
	{
		String mcname = marker.getMarkerClassName();
		if (childNodes != null)
		{
			// check for empty childNodes
			for (Iterator iter = childNodes.iterator(); iter.hasNext(); )
			{
				FONode node = (FONode)iter.next();
				if (node instanceof FOText)
				{
					FOText text = (FOText)node;
					if (text.willCreateArea())
					{
						getLogger().error("fo:marker must be an initial child: " + mcname);
						return;
					}
					else
					{
						iter.remove();
						notifyChildRemoval(node);
					}
				}
				else
				{
					getLogger().error("fo:marker must be an initial child: " + mcname);
					return;
				}
			}
		}
		if (markers == null)
			markers = new java.util.HashMap();
		if (!markers.containsKey(mcname))
			markers.put(mcname, marker);
		else
			getLogger().error("fo:marker 'marker-class-name' " + "must be unique for same parent: " + mcname);
	}

	/** @return true if there are any Markers attached to this object     */
	public boolean hasMarkers()
	{
		return markers != null && !markers.isEmpty();
	}

	/** @return th collection of Markers attached to this object     */
	public Map getMarkers()
	{
		return markers;
	}

	/*
	 * Return a string representation of the fo element.
	 * Deactivated in order to see precise ID of each fo element created (helpful for debugging)
	 */
	/*    public String toString() {
			return getName() + " at line " + line + ":" + column;
		}
	 */

	/** @see com.wisii.fov.fo.FONode#gatherContextInfo() */
	protected String gatherContextInfo()
	{
		if (getLocator() != null)
			return super.gatherContextInfo();
		else
		{
			ListIterator iter = getChildNodes();
			if (iter == null)
				return null;
			StringBuffer sb = new StringBuffer();
			while (iter.hasNext())
			{
				FONode node = (FONode)iter.next();
				String s = node.gatherContextInfo();
				if (s != null)
				{
					if (sb.length() > 0)
						sb.append(", ");
					sb.append(s);
				}
			}
			return (sb.length() > 0 ? sb.toString() : null);
		}
	}

	/**
	 * Convenience method for validity checking.  Checks if the incoming node is a member of the "%block;" parameter entity
	 * as defined in Sect. 6.2 of the XSL 1.0 & 1.1 Recommendations
	 * @param nsURI namespace URI of incoming node
	 * @param lName local name (i.e., no prefix) of incoming node
	 * @return true if a member, false if not
	 */
	protected boolean isBlockItem(String nsURI, String lName)
	{
		return (FO_URI.equals(nsURI)
				&& (lName.equals("block")
					|| lName.equals("table")
					|| lName.equals("table-and-caption")
					|| lName.equals("block-container")
					|| lName.equals("list-block")
					|| lName.equals("float")
					|| isNeutralItem(nsURI, lName)));
	}

	/**
	 * Convenience method for validity checking.  Checks if the incoming node is a member of the "%inline;" parameter
	 * entity as defined in Sect. 6.2 of the XSL 1.0 & 1.1 Recommendations
	 * @param nsURI namespace URI of incoming node
	 * @param lName local name (i.e., no prefix) of incoming node
	 * @return true if a member, false if not
	 */
	protected boolean isInlineItem(String nsURI, String lName)
	{
		return (FO_URI.equals(nsURI)
				&& (lName.equals("bidi-override")
					|| lName.equals("character")
					|| lName.equals("external-graphic")
					|| lName.equals("instream-foreign-object")
					|| lName.equals("inline")
					|| lName.equals("inline-container")
					|| lName.equals("leader")
					|| lName.equals("page-number")
					|| lName.equals("page-number-citation")
					|| lName.equals("page-number-citation-last")
					|| lName.equals("basic-link")
					|| (lName.equals("multi-toggle")
						&& (getNameId() == FO_MULTI_CASE
							|| findAncestor(FO_MULTI_CASE) > 0))
					|| (lName.equals("footnote") && !isOutOfLineFODescendant)
					|| isNeutralItem(nsURI, lName)));
	}

	/**
	 * Convenience method for validity checking.  Checks if the
	 * incoming node is a member of the "%block;" parameter entity or "%inline;" parameter entity
	 * @param nsURI namespace URI of incoming node
	 * @param lName local name (i.e., no prefix) of incoming node
	 * @return true if a member, false if not
	 */
	protected boolean isBlockOrInlineItem(String nsURI, String lName)
	{
		return (isBlockItem(nsURI, lName) || isInlineItem(nsURI, lName));
	}

	/**
	 * Convenience method for validity checking.  Checks if the
	 * incoming node is a member of the neutral item list as defined in Sect. 6.2 of the XSL 1.0 & 1.1 Recommendations
	 * @param nsURI namespace URI of incoming node
	 * @param lName local name (i.e., no prefix) of incoming node
	 * @return true if a member, false if not
	 */
	protected boolean isNeutralItem(String nsURI, String lName)
	{
		return (FO_URI.equals(nsURI)
				&& (lName.equals("multi-switch")
					|| lName.equals("multi-properties")
					|| lName.equals("wrapper")
					|| (!isOutOfLineFODescendant && lName.equals("float"))
					|| lName.equals("retrieve-marker")));
	}

	/**
	 * Convenience method for validity checking.  Checks if the current node has an ancestor of a given name.
	 * @param ancestorID -- Constants ID of node name to check for (e.g., FO_ROOT)
	 * @return number of levels above FO where ancestor exists, -1 if not found
	 */
	protected int findAncestor(int ancestorID)
	{
		int found = 1;
		FONode temp = getParent();
		while (temp != null)
		{
			if (temp.getNameId() == ancestorID)
				return found;
			found += 1;
			temp = temp.getParent();
		}
		return -1;
	}


	/** @see com.wisii.fov.fo.FONode#getNamespaceURI() */
	public String getNamespaceURI()
	{
		return FOElementMapping.URI;
	}

	/** @see com.wisii.fov.fo.FONode#getNormalNamespacePrefix() */
	public String getNormalNamespacePrefix()
	{
		return "fo";
	}

	/**
	 * Add a new extension attachment to this FObj. See com.wisii.fov.fo.FONode for details.
	 * @param attachment the attachment to add.
	 */
	public void addExtensionAttachment(ExtensionAttachment attachment)
	{
		if (attachment == null)
			throw new NullPointerException("参数不能为空");
		if (extensionAttachments == null)
			extensionAttachments = new java.util.ArrayList();
		if (log.isDebugEnabled())
		{
			getLogger().debug("ExtensionAttachment of category " + attachment.getCategory()
							  + " added to " + getName() + ": " + attachment);
		}
		extensionAttachments.add(attachment);
	}

	/** @return the extension attachments of this FObj. */
	public List getExtensionAttachments()
	{
		if (extensionAttachments == null)
			return Collections.EMPTY_LIST;
		else
			return extensionAttachments;
	}

	/**
	 * Adds a foreign attribute to this FObj.
	 * @param uri the namespace URI
	 * @param qName the fully qualified name
	 * @param value the attribute value
	 * @todo Handle this over FOV's property mechanism so we can use inheritance.
	 */
	public void addForeignAttribute(String uri, String qName, String value)
	{
		if (qName == null)
			throw new NullPointerException("参数名不能为空");
		if (foreignAttributes == null)
			foreignAttributes = new java.util.HashMap();
		String localName = qName;
		String prefix = null;
		int p = localName.indexOf(':');
		if (p > 0)
		{
			prefix = localName.substring(0, p);
			localName = localName.substring(p + 1);
		}
		foreignAttributes.put(new QName(uri, prefix, localName), value);
	}

	/** @return the map of foreign attributes */
	public Map getForeignAttributes()
	{
		if (foreignAttributes == null)
			return Collections.EMPTY_MAP;
		else
			return foreignAttributes;
	}
	protected void endOfNode() throws FOVException
	{
		if(childNodes==null)return;
		  for(int i=childNodes.size()-1;i>=0;i--)
//	         while(li.hasNext())
	        {
	        	FONode child=(FONode)childNodes.get(i);
	        	if(child instanceof Table &&child.getChildNodes()==null) childNodes.remove(i);
	        }
	     
		/*20101202 liuxiao start 
		 * 判断child是tablefobj对象并且子节点是null或者该节点是table，其子节点是tableBody的时候执行，其他情况返回
		 */
//		if(childNodes==null) return;
//		ListIterator lsk=childNodes.listIterator();
//		for(int i=(childNodes.size()-1);i==0;i--)
//		{
//			FONode child=(FONode)childNodes.get(i);
//		if(child instanceof TableFObj)
//		{
//			ListIterator li=child.getChildNodes();
//			if(li==null) childNodes.remove(i);
//			else 
//				if(child instanceof Table)
//			{
//				boolean as=false;
//			 while(li.hasNext())
//			 {
//			
//				 Object o=li.next();
//				if(o instanceof TableBody) 
//				{
//					as=true;
//					break;
//				}
//				
//				
//			 }
//			 if(!as) childNodes.remove(i);;
//			}
//		}
//		}
		/*end*/
	}
}

