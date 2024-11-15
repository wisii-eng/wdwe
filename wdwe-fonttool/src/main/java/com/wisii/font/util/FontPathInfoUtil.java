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
 * @FontPathInfoUtil.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.font.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.IOUtils;
import com.wisii.font.CustomFont;
import com.wisii.font.EncodingMode;
import com.wisii.font.Font;
import com.wisii.font.FontLoader;
import com.wisii.font.app.TTFReader;
import com.wisii.font.model.FontPathInfo;
import com.wisii.font.model.PathItemInfo;
import com.wisii.font.truetype.FontFileReader;
import com.wisii.font.truetype.TTFFile;
import com.wisii.font.truetype.TTFFontLoader;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2011-10-19
 */
public class FontPathInfoUtil
{
	public static List<FontPathInfo> getFontPathInfos(List fonturls,String outputpath)
	{
		if (fonturls != null && !fonturls.isEmpty())
		{
			List<FontPathInfo> paths = new ArrayList<FontPathInfo>();
			File outputfile = new File(outputpath);
			outputfile.mkdirs();
			for (Object fonturl : fonturls)
			{
				if (fonturl instanceof URL)
				{
					URL url = (URL) fonturl;
					String fontFileURI = url.toExternalForm().trim();
					System.out.println("解析字体:"+url.getPath()+"开始");
					if (fontFileURI.toLowerCase().endsWith(".ttc"))
					{
						List ttcNames = null; // List<String>
						InputStream in = null;
						try
						{
							in = FontLoader.openFontUri(null, fontFileURI);
							TTFFile ttf = new TTFFile();
							FontFileReader reader = new FontFileReader(in);
							ttcNames = ttf.getTTCnames(reader);
						} catch (Exception e)
						{
							return null;
						} finally
						{
							IOUtils.closeQuietly(in);
						}
						Iterator ttcNamesIterator = ttcNames.iterator();
						while (ttcNamesIterator.hasNext())
						{
							String fontName = (String) ttcNamesIterator.next();
							try
							{
								TTFFontLoader ttfLoader = new TTFFontLoader(
										fontFileURI, fontName, true,
										EncodingMode.AUTO, true, null);
								CustomFont customFont = ttfLoader.getFont();
								getPathInfoOfFont(customFont, paths, url,outputfile,fontName);
							} catch (Exception e)
							{

								continue;
							}

						}
					} else
					{
						try
						{
							CustomFont customFont = FontLoader.loadFont(url,
									null, true, EncodingMode.AUTO, null);
							getPathInfoOfFont(customFont, paths, url,outputfile,null);
						} catch (Exception e)
						{
							System.out.println(url.getPath()+"字体不支持");
							e.printStackTrace();
						}
					}
					System.out.println("解析字体:"+url.getPath()+"结束");
				}
			}
			return paths;
		}
		return null;
	}

	private static void getPathInfoOfFont(CustomFont font,
			List<FontPathInfo> fpinfos, URL url,File outputfile,String ttcname)
	{
		String fullName = stripQuotes(font.getFullName());
		String searchName = fullName.toLowerCase();
		String style = guessStyle(font, searchName);
		int guessedWeight = FontUtil.guessWeight(searchName);
		boolean isitallic = Font.STYLE_ITALIC.equals(style);
		boolean isbold = (guessedWeight == Font.WEIGHT_BOLD)
				|| (guessedWeight == Font.WEIGHT_EXTRA_BOLD);
		boolean isitallicandbold = isitallic && isbold;
		Iterator namesit = font.getFamilyNames().iterator();
		boolean needcreate=false;
		while (namesit.hasNext())
		{
			String name = stripQuotes((String) namesit.next());
			FontPathInfo info = getFontPathInfo(fpinfos, name);
			if (isitallicandbold)
			{
				if (info.getItalicboldfontpath() == null)
				{
					needcreate=true;
					PathItemInfo item= new PathItemInfo(url.getPath(),ttcname,outputfile);
					info.setItalicboldfontpath(item);
				}
			} else if (isbold)
			{
				if (info.getBlodfontpath() == null)
				{
					needcreate=true;
					PathItemInfo item= new PathItemInfo(url.getPath(),ttcname,outputfile);
					info.setBlodfontpath(item);
				}
			} else if (isitallic)
			{
				if (info.getItalicfontpath() == null)
				{
					needcreate=true;
					PathItemInfo item= new PathItemInfo(url.getPath(),ttcname,outputfile);
					info.setItalicfontpath(item);
				}
			} else
			{
				if (info.getNormalfontpath() == null)
				{
					needcreate=true;
					PathItemInfo item= new PathItemInfo(url.getPath(),ttcname,outputfile);
					info.setNormalfontpath(item);
				}
			}
		}
		if(needcreate)
		{
			CreateFontXml(url,outputfile,ttcname);
		}
 
	}
    private static void CreateFontXml(URL url,File rootpath,String ttcname)
	{
		TTFReader app = new TTFReader();
		TTFFile ttf = null;
		try
		{
			ttf = app.loadTTF(url.openStream(), ttcname);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ttf != null)
		{
			org.w3c.dom.Document doc = app.constructFontXML(ttf, null, null,
					null, null, true, ttcname);

			try
			{
				String filepath = url.getPath();
				int index = filepath
				.lastIndexOf(File.separator);
				if(index==-1)
				{
					index = filepath.lastIndexOf('/');
				}
				String filename = filepath.substring( index+ 1);
				index = filename.lastIndexOf('.');
				if(index!=-1)
				{
					filename = filename.substring(0, index);
				}
				if (ttcname != null)
				{
					filename = filename + "_" + ttcname;
				}
				String outfilepath = rootpath.getAbsolutePath()
						+ File.separator + filename + ".xml";
				app.writeFontXML(doc, outfilepath);
			} catch (TransformerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	} 
	private static FontPathInfo getFontPathInfo(List<FontPathInfo> fpinfos,
			String name)
	{
		int size = fpinfos.size();
		// 从后往前找理论上命中率会大些
		for (int i = size - 1; i >= 0; i--)
		{
			FontPathInfo fpinfo = fpinfos.get(i);
			if (fpinfo.getName().equals(name))
			{
				return fpinfo;
			}
		}
		// 如果没找到，则新生成一个
		FontPathInfo fpinfo = new FontPathInfo();
		fpinfo.setName(name);
		fpinfos.add(fpinfo);
		return fpinfo;
	}

	private static String guessStyle(CustomFont customFont, String fontName)
	{
		// style
		String style = Font.STYLE_NORMAL;
		if (customFont.getItalicAngle() > 0)
		{
			style = Font.STYLE_ITALIC;
		} else
		{
			style = FontUtil.guessStyle(fontName);
		}
		return style;
	}

	private static final Pattern quotePattern = Pattern.compile("'");

	private static String stripQuotes(String name)
	{
		return quotePattern.matcher(name).replaceAll("");
	}

}
