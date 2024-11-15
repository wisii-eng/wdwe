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
 */package com.wisii.fov.area;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wisii.fov.util.QName;

/** Abstract base class for all area tree objects. */
public abstract class AreaTreeObject
{
	/** Foreign attributes */
	protected Map foreignAttributes = null;

	/**
	 * Sets a foreign attribute.
	 * @param name the qualified name of the attribute
	 * @param value the attribute value
	 */
	public void setForeignAttribute(QName name, String value)
	{
		if (this.foreignAttributes == null) this.foreignAttributes = new java.util.HashMap();
		this.foreignAttributes.put(name, value);
	}

	/**
	 * Set foreign attributes from a Map.
	 * @param atts a Map with attributes (keys: QName, values: String)
	 */
	public void setForeignAttributes(Map atts)
	{
		if (atts.size() == 0) return;
		Iterator iter = atts.keySet().iterator();
		while (iter.hasNext())
		{
			QName qName = (QName)iter.next();
			String value = (String)atts.get(qName);
			//The casting is only to ensure type safety (too bad we can't use generics, yet)
			setForeignAttribute(qName, value);
		}
	}

	/**
	 * Returns the value of a foreign attribute on the area.
	 * @param name the qualified name of the attribute
	 * @return the attribute value or null if it isn't set
	 */
	public String getForeignAttributeValue(QName name)
	{
		if (this.foreignAttributes != null)
			return (String)this.foreignAttributes.get(name);
		else
			return null;
	}

	/** @return the foreign attributes associated with this area */
	public Map getForeignAttributes()
	{
		if (this.foreignAttributes != null)
			return Collections.unmodifiableMap(this.foreignAttributes);
		else
			return Collections.EMPTY_MAP;
	}
	/* 【添加：START】 by 李晓光 2009-6-8 */
	/* 把AreaTree建立为可双向遍历的Tree */
	public List getChildAreas() {
		return Collections.EMPTY_LIST;
	}
	protected AreaTreeObject parentArea = null;
	public AreaTreeObject getParentArea() {
		return parentArea;
	}

	public void setParentArea(final AreaTreeObject parent) {
		this.parentArea = parent;
	}
	/* 【添加：END】 by 李晓光 2009-6-8 */
}
