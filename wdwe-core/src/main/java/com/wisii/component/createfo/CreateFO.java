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
 */package com.wisii.component.createfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.wisii.component.mainFramework.WisiiComponent;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FODocURIResolver;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;

public class CreateFO extends WisiiComponent implements ErrorListener
{
	private CreateFOBean bean = null;

	/** XSLT转换的参数集合 */
	private Vector xsltParams = null;

	

	/** display the background */
	public static final String CHECK_YES = "yes";

	/** hide the background */
	public static final String CHECK_NO = "no";

	// 构造方法
	public CreateFO(CreateFOBean bean)
	{
		this.bean = bean;
	}

	// 初始化
	public void init()
	{

	}

	public Object execute(Object para)
	{
		// String xslc = bean.getXslc();//xsl模板字符串

		// String xmlc = bean.getXmlc();//xml数据字符串

		// String background = bean.getBackground();//是否显示有背景的数据 yes:显示,no:不显示

		// String editable = bean.getEditable();//数据是否可以编辑 yes:可以,no:不可以

		if (bean.getBaseURL() == null)
		{
			bean.setBaseURL(SystemUtil.getBaseURL());
		}// xsl中图片和document()方法引用外部xml文件所对应baseURL

		OutputStream[] out = new OutputStream[1];// Fo流

		try
		{
			checkSettings();

			addXSLTParameter();

			out[0] = new ByteArrayOutputStream();
			transformTo(out[0]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return out;
	}

	// 开始转换生成FO流
	public void transformTo(OutputStream out) throws FOVException
	{
		Result res = new StreamResult(out);
		transformTo(res);
	}

	/**
	 * Transforms the input document to the input format expected by FOV using
	 * XSLT.
	 * 
	 * @param result
	 *            the Result object where the result of the XSL transformation
	 *            is sent to
	 * @throws FOVException
	 *             in case of an error during processing
	 */
	protected void transformTo(Result result) throws FOVException
	{

		// Setup XSLT
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer;

		Source srcXsl = null;
		InputStream ss = bean.getXslc();
		try
		{
			if (ss != null)
			{

				srcXsl = new StreamSource(ss);
				// srcXsl = new StreamSource(new StringReader(bean.getXslc()));
			}
			transformer = factory.newTransformer(srcXsl);

			// Set the value of parameters, if any, defined for stylesheet
			if (xsltParams != null)
			{
				for (int i = 0; i < xsltParams.size(); i += 2)
				{
					transformer.setParameter((String) xsltParams.elementAt(i),
							(String) xsltParams.elementAt(i + 1));
				}
			}

			// 把背景设置为透明色（隐藏背景）
			if (!bean.getBackground())
			{
				transformer.setParameter("backcolor", "transparent");
			}

			// used to resolve URIs used in document().
			FODocURIResolver foDocURLResolver = new FODocURIResolver();

			String baseUrl = bean.getBaseURL();
			if (baseUrl != null && !"".equals(baseUrl))
			{
				if (!baseUrl.trim().endsWith("/")
						&& !baseUrl.trim().endsWith("\\"))
				{
					baseUrl = baseUrl + "/";
				}
				baseUrl = baseUrl + SystemUtil.CONFRELATIVEPATH;
			}

			foDocURLResolver.setBaseURL(baseUrl);
			transformer.setURIResolver(foDocURLResolver);
			transformer.setErrorListener(this);

			// Create a SAXSource from the input Source file
			Source srcXml = null;
			if (bean.getXmlc() != null)
			{
				srcXml = new StreamSource(bean.getXmlc());
				// srcXml = new StreamSource(new StringReader(bean.getXmlc()));
			}

			transformer.transform(srcXml, result);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * checks whether all necessary information has been given in a consistent
	 * way
	 */
	private void checkSettings() throws FOVException
	{
		if (bean.getXslc() == null)
		{
			throw new FOVException("必须指定XML文件");
		}
		if (bean.getXmlc() == null)
		{
			throw new FOVException("必须指定XSLT文件");
		}
	}

	// 添加XSLT的转换参数
	private void addXSLTParameter()
	{
		addXSLTParameter("fov-output-format", MimeConstants.MIME_XSL_FO);

		if (!bean.getBackground())
		{
			addXSLTParameter("backcolor", "transparent");
		}
		if (bean.getEditable())
		{
			addXSLTParameter("XmlEditable", "yes");
		}
	}

	private void addXSLTParameter(String name, String value)
	{
		if (xsltParams == null)
		{
			xsltParams = new Vector();
		}
		xsltParams.addElement(name);
		xsltParams.addElement(value);
	}

	/**
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exc)
	{
		System.err.println(exc.toString());
	}

	/**
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exc)
	{
		System.err.println(exc.toString());
	}

	/**
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exc)
			throws TransformerException
	{
		throw exc;
	}

	public static void main(String[] args)
	{
		CreateFO fo = new CreateFO(null);

		String xmlSrc = fo.readFile(args[0]);

		String xslSrc = fo.readFile(args[1]);

		CreateFOBean cBean = new CreateFOBean();

		cBean.setBackground(args[2]);
		cBean.setBaseURL(args[3]);
		cBean.setEditable(args[4]);

		CreateFO cfo = new CreateFO(cBean);

		OutputStream[] out = (OutputStream[]) (cfo.execute(null));

		fo.writeFile((ByteArrayOutputStream) out[0],
				new File("CreateFOTest.fo"));
	}

	public String readFile(String filename)
	{
		File f = new File(filename);
		String line = null;
		StringBuffer sb = new StringBuffer();

		InputStreamReader read;
		try
		{
			read = new InputStreamReader(new FileInputStream(f), "UTF-8");

			BufferedReader reader = new BufferedReader(read);

			while ((line = reader.readLine()) != null)
			{

				sb.append(line);

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sb.toString();
	}

	public void writeFile(ByteArrayOutputStream baos, File file)
	{
		byte cbuf[] = baos.toByteArray();

		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file);
			fos.write(cbuf);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
