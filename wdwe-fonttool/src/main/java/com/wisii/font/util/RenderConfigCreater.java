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
 */
/**
 * @RenderConfigCreater.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.font.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wisii.font.model.FontPathInfo;
import com.wisii.font.model.PathItemInfo;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-10-25
 */
public final class RenderConfigCreater
{
	static boolean createRenderConfig(List<FontPathInfo> fpinfos, String path)
	{
		if (fpinfos != null && !fpinfos.isEmpty() && path != null
				&& path.length() > 0)
		{
			path = path.trim();
			File dir = new File(path);
			if(!dir.exists())
			{
				if(!dir.mkdirs()){
				return false;
				}
			}
			File file = new File(dir.getAbsolutePath()+File.separator+"renderconfig.xml");
			if (!file.exists())
			{

				try
				{
					if (!file.createNewFile())
					{
						return false;
					}
				} catch (IOException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			Document doc;
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				doc = factory.newDocumentBuilder().newDocument();
			} catch (javax.xml.parsers.ParserConfigurationException e)
			{
				e.printStackTrace();
				return false;
			}
			Element root = doc.createElement("wdwe");
			root.setAttribute("version", "2.5");
			doc.appendChild(root);
			Element Renders = doc.createElement("renderers");
			root.appendChild(Renders);
			Element Render = doc.createElement("renderer");
			Render.setAttribute("mime", "application/pdf");
			Renders.appendChild(Render);
			Element filterList = doc.createElement("filterList");
			Render.appendChild(filterList);
			Element value = doc.createElement("value");
			value.setTextContent("flate");
			filterList.appendChild(value);
			Element fonts = doc.createElement("fonts");
			Render.appendChild(fonts);
			for (FontPathInfo fpinfo : fpinfos)
			{
				Element normalelement = null;
				Element boldelement = null;
				Element itaticelement = null;
				Element bolditaticelement = null;
				PathItemInfo item = fpinfo.getNormalfontpath();
				if (item != null)
				{
					normalelement = doc.createElement("font");
					normalelement.setAttribute("metrics-url", item
							.getMetrics_url());
					normalelement
							.setAttribute("embed-url", item.getEmbed_url());
					normalelement.setAttribute("kerning", "yes");
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "normal", "normal");
					normalelement.appendChild(fonttriplet);
					fonts.appendChild(normalelement);
				}
				item = fpinfo.getBlodfontpath();
				if (item != null)
				{
					boldelement = doc.createElement("font");
					boldelement.setAttribute("metrics-url", item
							.getMetrics_url());
					boldelement.setAttribute("embed-url", item.getEmbed_url());
					boldelement.setAttribute("kerning", "yes");
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "normal", "bold");
					boldelement.appendChild(fonttriplet);
					fonts.appendChild(boldelement);
				}
				item = fpinfo.getItalicfontpath();
				if (item != null)
				{
					itaticelement = doc.createElement("font");
					itaticelement.setAttribute("metrics-url", item
							.getMetrics_url());
					itaticelement
							.setAttribute("embed-url", item.getEmbed_url());
					itaticelement.setAttribute("kerning", "yes");
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "italic", "normal");
					itaticelement.appendChild(fonttriplet);
					fonts.appendChild(itaticelement);
				}
				item = fpinfo.getItalicboldfontpath();
				if (item != null)
				{
					bolditaticelement = doc.createElement("font");
					bolditaticelement.setAttribute("metrics-url", item
							.getMetrics_url());
					bolditaticelement.setAttribute("embed-url", item
							.getEmbed_url());
					bolditaticelement.setAttribute("kerning", "yes");
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "italic", "bold");
					bolditaticelement.appendChild(fonttriplet);
					fonts.appendChild(bolditaticelement);
				}
				if (bolditaticelement == null)
				{
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "italic", "bold");
					if (boldelement != null)
					{
						boldelement.appendChild(fonttriplet);
					} else if (itaticelement != null)
					{
						itaticelement.appendChild(fonttriplet);
					} else if (normalelement != null)
					{
						normalelement.appendChild(fonttriplet);
					}
				}
				if (itaticelement == null)
				{
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "italic", "normal");
					if (bolditaticelement != null)
					{
						bolditaticelement.appendChild(fonttriplet);
					} else if (normalelement != null)
					{
						normalelement.appendChild(fonttriplet);
					}
				}
				if (boldelement == null)
				{
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "normal", "bold");
					if (bolditaticelement != null)
					{
						bolditaticelement.appendChild(fonttriplet);
					} else if (normalelement != null)
					{
						normalelement.appendChild(fonttriplet);
					}
				}
				if (normalelement == null)
				{
					Element fonttriplet = createFonttriplet(doc, fpinfo
							.getName(), "normal", "normal");
					if (itaticelement != null)
					{
						itaticelement.appendChild(fonttriplet);
					} else if (boldelement != null)
					{
						boldelement.appendChild(fonttriplet);
					} else if (bolditaticelement != null)
					{
						bolditaticelement.appendChild(fonttriplet);
					}
				}
			}
			return outputFile(file, doc);
		}
		return false;
	}

	private static boolean outputFile(File target, Document doc)
	{
		try
		{
			OutputStream out = new java.io.FileOutputStream(target);
			out = new java.io.BufferedOutputStream(out);
			try
			{
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				transformer.transform(
						new javax.xml.transform.dom.DOMSource(doc),
						new javax.xml.transform.stream.StreamResult(out));
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
				return false;
			} finally
			{
				out.close();
			}
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
	}

	private static Element createFonttriplet(Document doc, String name,
			String style, String weight)
	{
		Element fonttriplet = doc.createElement("font-triplet");
		fonttriplet.setAttribute("name", name);
		fonttriplet.setAttribute("style", style);
		fonttriplet.setAttribute("weight", weight);
		return fonttriplet;
	}
}
