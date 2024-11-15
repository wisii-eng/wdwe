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
 *//* $Id: ForeignObject.java,v 1.1 2007/04/12 06:41:18 cvsuser Exp $ */

package com.wisii.fov.area.inline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.io.DOMReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.wisii.fov.area.Area;
import com.wisii.fov.svg.FOVSAXSVGDocumentFactory;

// cacheable object
/**
 * Foreign object inline area.
 * This inline area represents an instream-foreign object.
 * This holds an xml document and the associated namespace.
 */
public class ForeignObject extends Area {

	transient private Document doc;
	private String namespace;

	/**
	 * Create a new foreign object with the given dom and namespace.
	 * 
	 * @param d
	 *            the xml document
	 * @param ns
	 *            the namespace of the document
	 */
	public ForeignObject(Document d, String ns) {
		doc = d;
		namespace = ns;
	}

	/**
	 * Create a new empty foreign object for which the DOM Document will be set
	 * later.
	 * 
	 * @param ns
	 *            the namespace of the document
	 */
	public ForeignObject(String ns) {
		namespace = ns;
	}

	/**
	 * Sets the DOM document for this foreign object.
	 * 
	 * @param document
	 *            the DOM document
	 */
	public void setDocument(Document document) {
		this.doc = document;
	}

	/**
	 * Get the document for this foreign object.
	 * 
	 * @return the xml document
	 */
	public Document getDocument() {
		return doc;
	}

	/**
	 * Get the namespace of this foreign object.
	 * 
	 * @return the namespace of this document
	 */
	public String getNameSpace() {
		return namespace;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		
		//序列化svg文档。【svg-document→xml】
		//把svg文档转换为xml字符串数据，压缩对象流中。
		DOMReader reader = new DOMReader();
		org.dom4j.Document document = reader.read(doc);
		StringWriter outs = new StringWriter();
		document.write(outs);
		String s = outs.toString();
		out.writeObject(s);
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException, SAXException, ParserConfigurationException {
		in.defaultReadObject();
		
		//反序列化【xml→svg-document】
		//从流中读取xml形式的svg-document数据，并把xml数据转换为svg-document对象。
		String s = (String) in.readObject();
		ByteArrayInputStream ins = new ByteArrayInputStream(s.getBytes());//"UTF-8"
		FOVSAXSVGDocumentFactory svgf = new FOVSAXSVGDocumentFactory(SAXParserFactory.newInstance().newSAXParser().getXMLReader().getClass().getName());
		doc = svgf.createDocument(null, ins);
	}
}

