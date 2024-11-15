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
 * WDWEUtil.java
 * 北京汇智互联版权所有
 */
package com.wisii.fov.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.avalon.framework.container.ContainerUtil;
import com.wisii.component.createareatree.CreateAreaTreeBean;
import com.wisii.component.setting.PrintRef;
import com.wisii.component.setting.PrintRef.OUTPUTMode;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.font.util.FontConfigUtil;
import com.wisii.fov.apps.FODocURIResolver;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.Fov;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.render.RenderResult;
import com.wisii.fov.render.Renderer;
//import com.wisii.fov.render.afp.AFPRenderer;
//import com.wisii.fov.render.bitmap.PNGRenderer;
//import com.wisii.fov.render.bitmap.TIFFRenderer;
//import com.wisii.fov.render.pcl.PCLRenderer;
//import com.wisii.fov.render.pdf.PDFRenderer;
import com.wisii.fov.render.print.PrintRenderer;
//import com.wisii.fov.render.ps.PSRenderer;
import com.wisii.fov.render.server.ServerRenderer;
//import com.wisii.fov.render.txt.TXTRenderer;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-11
 */
public class WDWEUtil {
	public static RenderResult renderTo(WisiiBean wisiibean) throws FOVException {
		if(wisiibean==null)
		{
			return null;
		}
		Renderer renderer = createRenderByMime(wisiibean.getOutputFormat(),wisiibean.getPrintSetting().getOutputFile());
		FOUserAgent useragent = renderer.getUserAgent();
		if(renderer instanceof PrintRenderer)
		{
			((PrintRenderer)renderer).setWisiibean(wisiibean);
			useragent.setPrintNoBack(true);
			useragent.setIsview(false);
		}
		
		useragent.setWisiibean(wisiibean);
		Set set = wisiibean.getparaLayers(wisiibean.getSelectedLayers());
		useragent.setSelectLayers(set);
		renderTo(renderer, wisiibean);
		return renderer.getResultInfo();
	}

	public static synchronized void renderTo(Renderer renderer, WisiiBean wisiibean)
			throws FOVException {
		FOUserAgent userAgent = renderer.getUserAgent();
		userAgent.setRendererOverride(renderer);
		FovFactory factory = userAgent.getFactory();
		OutputStream out=null;
		try {
			File file=userAgent.getOutputFile();
			if(file!=null)
			{
				out=new BufferedOutputStream(
						new FileOutputStream(file));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		Fov fov = factory.newFov(wisiibean.getOutputFormat(), userAgent,out);
		Result res = new SAXResult(fov.getDefaultHandler());
		CreateAreaTreeBean bean = getAreaTreeBean(wisiibean);
		transformTo(res, bean);
		//如果是ServerRenderer即为多输出的情况下
		if(renderer instanceof ServerRenderer)
		{
			Map printerMap = wisiibean.getPrintSetting().getPrintRefMap();
			OutputStream outt[] = new OutputStream[printerMap.size()];
			for (int i = 0; i < outt.length; i++) {
				outt[i] = new ByteArrayOutputStream();
			}
			devideSequence(wisiibean,printerMap, outt, userAgent,((ServerRenderer)renderer));
		}
	}
	private static boolean devideSequence(WisiiBean wisiibean,Map printerMap, OutputStream out[],
			FOUserAgent foUserAgent,ServerRenderer renderer) {

		// 修改为 pageViewPort对应多个Render
		try {
			ArrayList renderP = new ArrayList();

			List pvList = renderer.getPageViewportList(); // pageViewPort的列表
			for (int i = 0; i < pvList.size(); i++) {
				PageViewport page = null;
				try {
					page = (PageViewport) pvList.get(i);
				} catch (ClassCastException e) {
					continue;
				}
				String mu = page.getMediaUsage();
				int u = 0;
				if (mu != null)
					try {
						u = Integer.parseInt(mu);
					} catch (NumberFormatException e) {
						System.out.print("不是分组模板MediaUsage默认为0");
					}
				System.err.println("=========================mu u" + mu + "uu"
						+ u);
				PrintRef info = (PrintRef) printerMap.get(u + ""); // 打印机页面信息
				// 如果没有
				if (info == null && wisiibean.getPrintSetting().isJustprintref()) {
					continue;
				}
				if (info == null) {
					info = (PrintRef) printerMap.get("0");
				}
				int j;
				for (j = 0; j < renderP.size(); j++) {
					RenderP ro = (RenderP) renderP.get(j);
					// 把同组的pageViewPort用一个render处理
					if (ro.ref == info) {
						ro.render.renderPage(page);
						break;
					}
				}

				if (j == renderP.size()) {
					RenderP r = new RenderP(info);
					r.render = createRenderByMime(getMimeByOUTPUTMode(info.getOutputMode()), info.getOutputFile());
					r.render.setupPrinterInfo(printerMap, info); // PrinterList
					r.render.setUserAgent(renderer.getUserAgent());
					// ：打印配置列表，info.getName()：打印机名称

					renderP.add(r);
					r.render.renderPage(page);
				}

			}

			for (int j = 0; j < renderP.size(); j++) {
				RenderP r = (RenderP) renderP.get(j);
				if (r.render instanceof PrintRenderer)
				{
					((PrintRenderer) r.render)
							.setWisiibean(wisiibean);
				}
				r.render.stopRenderer();
				r.render = null;
				System.err.println("结束end");
			}
		} catch (FOVException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
    private static String getMimeByOUTPUTMode(OUTPUTMode mode)
    {
    	switch (mode) {
		case PCL: {
			return MimeConstants.MIME_PCL;
		}
		case PS: {
			return MimeConstants.MIME_POSTSCRIPT;
		}
		case PNG: {
			return MimeConstants.MIME_PNG;
		}
		case TIFF: {
			return MimeConstants.MIME_TIFF;
		}
		case TXT: {
			return MimeConstants.MIME_PLAIN_TEXT;
		}
		case PDF: {
			return MimeConstants.MIME_PDF;
		}
		case AFP: {
			return MimeConstants.MIME_AFP;
		}
		case FLASH: {
			return MimeConstants.MIME_FLASH;
		}
		default: {
			return MimeConstants.MIME_WISII_PRINT;
		}
		}
    }
	private static Renderer createRenderByMime(String mime,File outfile) {
		Renderer render = null;
		if (MimeConstants.MIME_PCL.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.pcl.PCLRenderer");
		} else if (MimeConstants.MIME_POSTSCRIPT.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.ps.PSRenderer");
		} else if (MimeConstants.MIME_PLAIN_TEXT.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.txt.TXTRenderer");
		} else if (MimeConstants.MIME_AFP.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.afp.AFPRenderer");
		} else if (MimeConstants.MIME_TIFF.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.bitmap.TIFFRenderer");
		} else if (MimeConstants.MIME_PNG.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.bitmap.PNGRenderer");
		} else if (MimeConstants.MIME_PDF.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.pdf.PDFRenderer");

			String classpath = SystemUtil.getClassesPath();
			if (classpath != null) {
				File userConfigFile = new File(classpath + "renderconfig.xml");
				if(!userConfigFile.exists()) {
					boolean configPDFFont = FontConfigUtil.configPDFFont(classpath +"/font", classpath);
					if(!configPDFFont) {
						System.out.println("renderconfig.xml生成失败！");
					}
				}
				try {
					FOUserAgent useragent = render.getUserAgent();
					useragent.getFactory().setUserConfig(userConfigFile);
					ContainerUtil.configure(render, useragent
							.getUserRendererConfig(MimeConstants.MIME_PDF));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		else if (MimeConstants.MIME_FLASH.equals(mime)) {
			render = getRendererByClass("com.wisii.fov.render.flash.FlashRenderer");
		} 
		else if (MimeConstants.MIME_WISII_PRINT.equals(mime)) {
			return new PrintRenderer();
		} else if (MimeConstants.MIME_WISII_PRINTSEQUENCE.equals(mime)) {
			render = new ServerRenderer();
			((ServerRenderer) render).setOutputMode(mime);
		} else {
			render = null;
		}
		if (render != null) {
			FOUserAgent useragent = render.getUserAgent();
			if (outfile != null) {
				useragent
				.setOutputFile(outfile);
			}

		}
		return render;
	}
    private static Renderer getRendererByClass(String classname)
 {
		try {
			Class cls = Class.forName(classname);
			Constructor cons = cls.getConstructor();
			Renderer render;
			render = (Renderer) cons.newInstance();
			return render;
		} catch (Exception e) {
			return null;
		}
	}
	private static CreateAreaTreeBean getAreaTreeBean(WisiiBean wisiibean) {
		CreateAreaTreeBean areatreebean = new CreateAreaTreeBean();
		if (wisiibean.getFoString() != null) {
			areatreebean.setFoString(wisiibean.getFoString());
		} else {

			areatreebean.setStyleString(wisiibean.getXslString());

			areatreebean.setSourceString(wisiibean.getXmlString());
			areatreebean.setXmlSource(wisiibean.getXmlSource());

		}

		areatreebean.setOutputMode(wisiibean.getOutputFormat());
		// 【刘晓注掉20090617】
		areatreebean.setTemplateMap(wisiibean.getTemplatePara());

		if (wisiibean.getBaseurl() != null) {
			areatreebean.setBaseUrl(wisiibean.getBaseurl().getPath());
		} else
			try {
				wisiibean.setBaseurl(new URL(SystemUtil.getBaseURL()));
			} catch (MalformedURLException e1) {

				try {
					wisiibean.setBaseurl(new URL("file:"
							+ SystemUtil.getBaseURL()));
				} catch (MalformedURLException e) {

					e.printStackTrace();
				}
			}
		areatreebean.setBaseUrl(SystemUtil.getBaseURL());
		areatreebean.setBaseUrl(SystemUtil.getBaseURL());
		return areatreebean;

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
	private static void transformTo(Result result, CreateAreaTreeBean bean)
			throws FOVException {
		URL absoluteUrl = null; // add by huangzl
		Source srcf = null;
		String baseUrl = bean.getBaseUrl();
		if (baseUrl != null && !"".equals(baseUrl)) {
			if (!baseUrl.trim().endsWith("/") && !baseUrl.trim().endsWith("\\")) {
				baseUrl = baseUrl + "/";
			}
			baseUrl = baseUrl + SystemUtil.CONFRELATIVEPATH;
		}
		FODocURIResolver foDocURLResolver = new FODocURIResolver();
		foDocURLResolver.setBaseURL(baseUrl);
		try {

			Transformer transformer = null;

			if (bean.styledir == null && bean.stylesheet == null
					&& bean.styleString == null) // FO Input //chg by
			{

				transformer = TransformerFactory.newInstance().newTransformer();
				if (bean.getInstream() != null) {
					InputStream[] s = SystemUtil.getCopyInputStream(bean
							.getInstream());
					srcf = new StreamSource(s[0], SystemUtil.INPUTSTREAM_ID);

				} else if (bean.getFoString() != null) {

					srcf = new StreamSource(new StringReader(bean.foString));
				}

			} else {
				Source src = null;
				if (bean.styleString != null) {

					src = new StreamSource(new StringReader(bean.styleString),
							SystemUtil.STRINGSTREAM_ID);
					foDocURLResolver.setStringSrc(bean.styleString,
							SystemUtil.STRINGSTREAM_ID);
				} else {
					if (bean.stylesheet == null) {
						try {
							absoluteUrl = new URL(bean.styledir);
						} catch (MalformedURLException mue) {
							absoluteUrl = new URL("file:" + bean.styledir);
						}

						if (absoluteUrl != null) {
							bean.stylesheet = absoluteUrl.openStream();
						} 
					}
					InputStream[] s = SystemUtil
							.getCopyInputStream(bean.stylesheet);
					src = new StreamSource(s[0], SystemUtil.INPUTSTREAM_ID);
				}

				transformer = TransformerFactory.newInstance().newTransformer(
						src);

			}
			transformer.setURIResolver(foDocURLResolver);
			// transformer.setErrorListener(this);

			if (bean.styledir != null || bean.stylesheet != null
					|| bean.styleString != null) // FO Input chg by huagnzl
			{
				if (bean.sourceString != null) {
					srcf = new StreamSource(new StringReader(bean.sourceString));
				}
				else if(bean.xmlsource!=null)
				{
					srcf=bean.xmlsource;
				}
				else {
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
			bean.xmlsource=null;
		} catch (Exception e) {
			throw new FOVException(e);
		}

	}
	private static class RenderP {
		Renderer render; // 使用的render

		PrintRef ref;

		public RenderP(PrintRef ref) {
			this.ref = ref;
		}
	}
}
