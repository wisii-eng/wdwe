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
 */package com.wisii.component.createareatree;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.wisii.component.mainFramework.WisiiComponent;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FODocURIResolver;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.Fov;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.render.server.ServerRenderer;

public class CreateAreaTree extends WisiiComponent implements ErrorListener {
	private CreateAreaTreeBean bean = null;

	private List pageViewPortList = null;


	/** XSLT转换的参数集合 */
	private Vector xsltParams = null;

	public CreateAreaTree(CreateAreaTreeBean bean) {
		this.bean = bean;

	}



	public Object execute(Object para) {
		FOUserAgent foUserAgent = FovFactory.newInstance().newFOUserAgent();
		foUserAgent.setBaseURL(bean.getBaseUrl());
	
		ServerRenderer _renderer = new ServerRenderer();

		
		_renderer.setUserAgent(foUserAgent);
		_renderer.setCommunicateProxy(communproxy);
		_renderer.setWriter(bean.getOut());

		_renderer.setOutputMode(bean.getOutputMode());

		OutputStream outt[] = new OutputStream[1];// areaTree流

		try {
			addXSLTParameter();
			outt[0] = new ByteArrayOutputStream();

			/** Apache FOV's area tree XML */
			String MIME= bean.getOutputMode();
			if(MIME.equals(MimeConstants.MIME_XSL_FO)||MIME.equals(MimeConstants.MIME_RTF))
			renderTo(foUserAgent, MIME,(OutputStream)bean.getOut());
			else
				renderTo(foUserAgent, MIME,outt[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// pageViewPortList =_renderer._pageViewportList;
		return outt;
	}
	/**
	 * checks whether all necessary information has been given in a consistent
	 * way
	 */
	private void checkSettings() throws FOVException, IOException {
		if (bean.getInstream() == null) {
			System.out.println("fo文件为null");

			if (bean.getSourcefile() == null) {
				throw new FOVException("必须指定XML文件");
			}
			if (bean.getStylesheet() == null) {
				throw new FOVException("必须指定XSLT文件");
			}
		}
	}

	/**
	 * Generate a document, given an initialized Fov object
	 * 
	 * @param userAgent
	 *            the user agent
	 * @param outputFormat
	 *            the output format to generate (MIME type, see MimeConstants)
	 * @param out
	 *            the output stream to write the generated output to (may be
	 *            null if not applicable)
	 * @throws FOVException
	 *             in case of an error during processing
	 */
	public void renderTo(FOUserAgent userAgent, String outputFormat,
			OutputStream out) throws FOVException {
		FovFactory factory = userAgent.getFactory();
		Fov fov;
		if (out != null)
			fov = factory.newFov(outputFormat, userAgent, out);
		else
			fov = factory.newFov(outputFormat, userAgent);

		// Resulting SAX events (the generated FO) must be piped through to FOV
		Result res =null;
		if(outputFormat.equals(MimeConstants.MIME_XSL_FO))
			res = new StreamResult(out);
			else
			res = new SAXResult(fov.getDefaultHandler());
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
	protected void transformTo(Result result) throws FOVException {
		URL absoluteUrl = null; // add by huangzl
		Source srcf = null;
		
		String baseUrl = bean.getBaseUrl();
		
		if (baseUrl != null && !baseUrl.isEmpty()) {
			if (!baseUrl.trim().endsWith("/")
					&& !baseUrl.trim().endsWith("\\")) {
				baseUrl = baseUrl + "/";
			}
			baseUrl = baseUrl + SystemUtil.CONFRELATIVEPATH;
		}
		
//		 used to resolve URIs used in document().
		FODocURIResolver foDocURLResolver = new FODocURIResolver();
		foDocURLResolver.setBaseURL(baseUrl);
		
		try {

			// Setup XSLT
			
			Transformer transformer;

			if (bean.styledir == null && bean.stylesheet == null && bean.styleString == null) // FO Input //chg by
			{// huagnzl
				transformer = TransformerFactory.newInstance().newTransformer();

				// Create a SAXSource from the input Source file

				if (bean.getInstream() != null) {
					//change by liuxiao 20090206 用于解决从文件中读取配置文件的需求
					InputStream [] s=SystemUtil.getCopyInputStream(bean.getInstream());
					srcf = new StreamSource(s[0],
							SystemUtil.INPUTSTREAM_ID);
					
				}
				else if(bean.getFoString()!=null)
				{
					
					srcf = new StreamSource(new StringReader (bean.foString));
				}

			} else { // XML/XSLT input
				Source src = null;
				if (bean.styleString != null) 
				{
					src = new StreamSource(new StringReader(bean.styleString),SystemUtil.STRINGSTREAM_ID);
//					change by liuxiao 20090206 用于解决从文件中读取配置文件的需求
					foDocURLResolver.setStringSrc(bean.styleString, SystemUtil.STRINGSTREAM_ID);
				} 
				else 
				{
					if (bean.stylesheet == null) 
					{
						try {
							absoluteUrl = new URL(bean.styledir);
						} catch (MalformedURLException mue) {
							absoluteUrl = new URL("file:" + bean.styledir);
						}

						if (absoluteUrl != null) {
							bean.stylesheet = absoluteUrl.openStream();
						} else {
							return;
						}
					}
//					change by liuxiao 20090206 用于解决从文件中读取配置文件的需求
					InputStream [] s=SystemUtil.getCopyInputStream(bean.stylesheet);
					src = new StreamSource(s[0],SystemUtil.INPUTSTREAM_ID);
				}
				/**
				 * liuxiao add tip
				 * ms这个地方已经完成了对xslt的解析
				 */
				
				transformer = TransformerFactory.newInstance().newTransformer(src);
				// Set the value of parameters, if any, defined for stylesheet
				// Set the value of parameters, if any, defined for stylesheet
				if (xsltParams != null) {
					for (int i = 0; i < xsltParams.size(); i += 2) {
						transformer.setParameter((String) xsltParams
								.elementAt(i), (String) xsltParams
								.elementAt(i + 1));
					}
				}

//				// 把背景设置为透明色（隐藏背景）
//				if (!bean.isBackground()) {
//					transformer.setParameter("backcolor", "transparent");
//				}

			}


			transformer.setURIResolver(foDocURLResolver);
			transformer.setErrorListener(this);

			if (bean.styledir != null || bean.stylesheet != null || bean.styleString != null) // FO Input chg by huagnzl
			{
				// Create a SAXSource from the input Source file

				if (bean.sourceString != null) {
					srcf = new StreamSource(new StringReader(
							bean.sourceString));
				} else {
					if (bean.sourcefile == null) {
						try {
							absoluteUrl = new URL(bean.sourcedir);
						} catch (MalformedURLException mue) {
							absoluteUrl = new URL("file:" + bean.sourcedir);
						}

						if (absoluteUrl != null) {
							bean.sourcefile = absoluteUrl.openStream();
						} else {
							return;
						}
					}
					srcf = new StreamSource(bean.sourcefile);
				}
			}
			// Start XSLT transformation and FOV processing
			
			transformer.transform(srcf, result);

			// close the inputStream
			if (bean.stylesheet != null) {
				bean.stylesheet.close();
				bean.stylesheet = null;
			}
			if (bean.sourcefile != null) {
				bean.sourcefile.close();
				bean.sourcefile = null;
			}

			bean.sourceString = null;
			bean.styleString = null;
		} catch (Exception e) {
			throw new FOVException(e);
		}

	}

	// --- Implementation of the ErrorListener interface ---

	/**
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exc) {
		exc.printStackTrace();
	}

	/**
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exc) {
		exc.printStackTrace();
	}

	/**
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exc)
			throws TransformerException {
		throw exc;
	}

	public List getPageViewPortList() {
		return pageViewPortList;
	}

	// 添加XSLT的转换参数
	private void addXSLTParameter() {
		addXSLTParameter("fov-output-format", MimeConstants.MIME_XSL_FO);
		Map map = bean.getTemplateMap();
		if (map != null) {
			Set ss = map.keySet();
			Iterator it = ss.iterator();
			while (it.hasNext()) {
				Object one=it.next();
				addXSLTParameter(one.toString(), map.get(one).toString());
			}
		}

//		if (!bean.isBackground()) {
//			addXSLTParameter("backcolor", "transparent");
//		}
		//【刘晓注掉20090617】
		/*if (bean.isEditable()) {
			addXSLTParameter("XmlEditable", "yes");
		}
		else 
			addXSLTParameter("XmlEditable", "no");
			*/
	}

	private void addXSLTParameter(String name, String value) {
		if (xsltParams == null) {
			xsltParams = new Vector();
		}
		xsltParams.addElement(name);
		xsltParams.addElement(value);
	}

	// 以下为test使用
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileInputStream inpustream = null;

		try {
			inpustream = new FileInputStream(
					"C:/eclipse/workspace/Wdems/CreateFOTest.fo");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 建立areaTree
		CreateAreaTreeBean bean = new CreateAreaTreeBean();
		bean.setInstream(inpustream);
//		bean.setConfigMap(ReadConfig.configres);
		bean.setOut(new Object());

		CreateAreaTree careaTree = new CreateAreaTree(bean);
		OutputStream[] out = (OutputStream[]) (careaTree.execute(null));

		List list = careaTree.getPageViewPortList();

		int pagecount = list.size();// pageViewPort的总数

		System.err.println("输出 ：" + pagecount + "个 pageViewPort");

		careaTree.writeFile((ByteArrayOutputStream) out[0], new File(
				"MyTestCreatAreaTree.xml"));
	}

	public String readFile(String filename) {
		File f = new File(filename);
		String line = null;
		StringBuffer sb = new StringBuffer();

		InputStreamReader read;
		try {
			read = new InputStreamReader(new FileInputStream(f), "UTF-8");

			BufferedReader reader = new BufferedReader(read);

			while ((line = reader.readLine()) != null) {

				sb.append(line);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static void writeFile(ByteArrayOutputStream baos, File file) {
		byte cbuf[] = baos.toByteArray();
		if (cbuf != null && cbuf.length > 0) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(cbuf);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}





	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}





	



}
