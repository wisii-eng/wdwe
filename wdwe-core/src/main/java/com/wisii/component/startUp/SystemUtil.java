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
 */package com.wisii.component.startUp;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import javax.xml.transform.stream.StreamSource;
import com.wisii.component.mainFramework.commun.CommincateFactory;
/**
 * 
 * @author zhangqiang
 *
 */
public class SystemUtil {
	// charset
	public static final String SYS_CHARSET = "UTF-8";
	public static final String FILE_CHARSET = "UTF-8";

	// path of page
	public static final String CLASSPATH = "com.wisii.fov.command.";

	// key of requtest parameter
	public static final String PARA_EDITTYPE = "editType";

	public static final String PARA_CURRENTPAGE = "currentPage";

	public static final String PARA_BACKGROUND = "background";

	public static final String PARA_SERVERTYPE = "serverType";

	// value of servertype
	public static final String SER_SCHEMAOBJ = "CMDSchemaObj";

	public static final String SER_AREATREEOBJ = "CMDAreaTreeObj";

	public static final String SER_RTRNPAGE2OTH = "CMDRtrnPage2Oth";

	public static final String SER_SUBMITXML = "CMDSubmitXml";

	public static final String SER_UPDTXML = "CMDUpdtXml";

	public static final String SER_GETHARDINFO = "CMDGetHardWareInfo";

	public static final String SER_XMLDATA = "CMDXmlData";

	public static final String SER_CLEARSESSION = "CMDClearSession";

	public static final String SER_DeliverConfig = "CMDDeliverConfig";

	public static final String SER_DeliverPic = "CMDDeliverPic";

	public static final String SER_BACKGROUNDPRINT = "BackGroundPrint";
	// add by px
	public static final String SER_AUTHORITY = "CMDAuthority";
	// key of session

	// 【add by 刘晓 用于下拉列表文件型是的读取】
	public static final String SER_SELECTDATA = "CMDSelectData";

	public static final String   SES_wisselectdatainterface= "wisselectdatainterface";
	public static final String SES_FOC = "foc";

	public static final String SES_XML_IMANGE = "wisii_widePicDataDef";

	public static final String SES_XMLC = "xmlc";

	public static final String SES_XSLTC = "xsltc";

	public static final String SES_STRUCTDEFC = "structdefc";

	public static final String SES_DOCID = "docId";

	public static final String SES_RETURNSERVERURL = "returnServerURL";
	// 【add by 刘晓 用于下拉列表文件型是的读取,这个是传递到服务端的Map中的关键字】
	public static final String SES_TRANSLATEURL = "translateUrl";

	public static final String SES_EXCEPTIONURL = "exceptionURL";

	public static final String SES_OUTPUTMODE = "out";

	public static final String SES_EDITABLE = "isEditable";

	/** configuration file's relative path */
	public static final String CONFRELATIVEPATH = "conf/";

	/** graphics file's relative path */
	public static final String GRAPHICSRELATIVEPATH = "graphics/";

	/** validate file's relative path */
	public static final String VALIDATEPATH = "validate/";

	/** xml file's relative path */
	public static final String XMLPATH = "xml/";

	/** template file's relative path */
	public static final String TEMPLATEPATH = "template/";

	/** fo file's relative path */
	public static final String FORELATIVEPATH = "fo/";

	/** scheme component: http:// */
	public static final String HTTPSCHEME = "http://";

	/** scheme component: file: */
	public static final String FILESCHEME = "file:";

	public static final String FINAL_BUNDLE_NAME = "wddeconfig";

	/** Defines the default target resolution (72dpi) for editor */
	public static final float DEFAULT_TARGET_RESOLUTION = 72.0f; // dpi

	/** Defines the default source resolution (25.4dpi) for editor */
	public static final float DEFAULT_SOURCE_RESOLUTION = 25.4f; // dpi

	/** print time of executed a certain function */
	public static final boolean PRINT_RUN_TIME = false;

	/** 打印最大偏移量 */
	public static final float MAX_excursion = 500.0f;

	public static final float MIN_excursion = -500.0f;

	/** *英寸 */
	public static final int INCH = 72000;

	/** *点 */
	public static final int PT = 25400;

	public static final int BORDER_SPACING = 10; // 为边沿预留的空间。

	public static final int SPACE_HEIGHT = 73; // 整页显示时，为上边沿预留的空间。

	public static final int SPACE_WIDTH = 19; // 整页显示时，为左右边沿预留的空间。

	public static final int SPACE_LEFT_RIGHT = 86; // 页宽显示时 ，为左右边沿预留的空间 。

	public static final float MIN_PERCENT = 20.0f; // 最小显示比例 。

	public static final float MAX_PERCENT = 500.0f; // 最大显示比例 。
	public static final String INPUTSTREAM_ID = "inputstreamid";
	public static final String STRINGSTREAM_ID = "stringstreamid";

	public static final String TASKTYPE_SERVER = "FrontServletTask";
	public static final String TASKTYPE_ALLINONE = "AllBrowseTask";
	public static final String WISIIBEAN = "wisiibean";
	private static final ResourceBundle res = ResourceBundle
			.getBundle("resource."+SystemUtil.FINAL_BUNDLE_NAME);

	public static String getConfByName(String key) {
		try
		{
			return res.getString(key);
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String getBaseURL() {
		return getURL("base.baseurl");
	}

	/**
	 * 原功能 // 获得baseurl 即模板的根路径得到路径后再拼上/xml即数据文件的存放路径 20081113 刘晓 修改
	 * 将方法改为工程的根路径+拼装base.baseurl 方法思路： 取得根路径，判断是b/s服务端还是客户端 还是单机版
	 * 通过配置文件来进行根目录的识别
	 * 
	 * 
	 */
	public static String getURL(String ss) {
		String path = getConfByName(ss);
		if (path == null) {
			path = "";
		}

		if (path == null || path.trim().equals("")
				|| path.trim().startsWith("./")) {
			String relatePath = path;
			if (relatePath != null && path.trim().startsWith("./")) {
				relatePath = relatePath.trim().substring(2);
			}

			URL url = SystemUtil.class.getClassLoader()
					.getResource("resource/wddeconfig.properties");
			path = url.getPath();
			path = getURLDecoderdecode(path);
			// path="http://192.168.0.23:8080/wdems2/wisii/jar/wdemsresource.jar!";

			if (path.indexOf("WEB-INF") != -1) {

				path = path.substring(0, path.indexOf("WEB-INF")) + relatePath;
			} else if (path.indexOf("wdemsresource.jar!") != -1) {

				path = path.substring(0, path.indexOf(SystemUtil.getConfByName(
						"base.appletJarUrl").trim().substring(2)))
						+ relatePath;
			} else {

				path = SystemUtil.goToTheUpperLevel(SystemUtil
						.goToTheUpperLevel(path));
				path = SystemUtil.goToTheUpperLevel(SystemUtil
						.goToTheUpperLevel(path));

			}

		}
		if (path != null && !path.trim().endsWith("/")
				&& !path.trim().endsWith("\\")) {
			path = path + "/";
		}

		path = path.replaceAll("%20", " ");

		return path;
	}

	// 初始化encodecharset
	public static String getEncodeCharset() throws Exception {
		String charset = "GBK";
		String webserver = getConfByName("base.webserver");
		if (webserver != null && webserver.trim().equalsIgnoreCase("tcs")) {
			charset = "ISO-8859-1";
		}
		return charset;
	}

	// --------------------------------------------------add by zkl.
	/**
	 * 将得到的路径按照 "/"向上返回一级目录 add by liuxiao 20081106 例： d:/c1/c2/c3/c4
	 */
	public static String goToTheUpperLevel(String path) {
		// path=path.replace("\\", File.separator);
		// path=path.replace("/", File.separator);
		int s = path.lastIndexOf("/");
		return path.substring(0, s);
	}

	public static boolean isFile(URL url) {
		File ss = null;
		try {
			ss = new File(url.toURI());

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss.isFile();
	}

	private static Map fontMap;

	private static void initFonts() {

		String font_baseuri = getBaseURL() + getConfByName("font.filesurl");

		Font f = null;
		InputStream in = null;
		try {

			String fontName;
			String fontFile;
			String[] allfont = getConfByName("font.define").split(",");

			for (int i = 0; i < allfont.length; i++) {

				fontName = getConfByName("font." + allfont[i] + ".name");
				fontFile = getConfByName("font." + allfont[i] + ".file");

				try {

					in = new FileInputStream(font_baseuri + fontFile);
				} catch (FileNotFoundException ef) {
					if (font_baseuri.startsWith("file:")) {
						font_baseuri = font_baseuri.substring(5);

					}
					in = new FileInputStream(font_baseuri + fontFile);
				}

				f = Font.createFont(Font.TRUETYPE_FONT, in);

				fontMap.put(fontName, f);

				in.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}

	}

	public static Font getFont(String family) {

		if (fontMap == null || fontMap.size() < 2) {
			fontMap = new Hashtable();
			initFonts();
		}

		return (Font) fontMap.get(family);

	}

	// ---------------------add by liuxiao 20080102
	// start-----------------------//
	/**
	 * 对字符串进行URLDecoder.encode(strEncoding)编码
	 * 
	 * @param String
	 *            src 要进行编码的字符串
	 * 
	 * @return String 进行编码后的字符串
	 */

	public static String getURLEncode(String src) {
		String requestValue = "";
		try {

			requestValue = URLEncoder.encode(src, SystemUtil.SYS_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestValue;
	}

	/**
	 * 对字符串进行URLDecoder.decode(strEncoding)解码
	 * 
	 * @param String
	 *            src 要进行解码的字符串
	 * 
	 * @return String 进行解码后的字符串
	 */

	public static String getURLDecoderdecode(String src) {
		String requestValue = "";
		try {

			requestValue = URLDecoder.decode(src, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestValue;
	}

	/**
	 * 该方法用于将输入流复制成两个输入流。输出一个输入流数组，长度为2
	 * 
	 * @author liuxiao
	 * @param cInputStream
	 * @return InputStream[]
	 * 
	 */
	public static InputStream[] getCopyInputStream(InputStream cInputStream) {
		try {
			byte[] bytes = new byte[1];
			InputStream[] vArrayInputStream = new InputStream[2];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (cInputStream.read(bytes) != -1) {
				baos.write(bytes);
			}
			baos.flush();
			baos.close();
			vArrayInputStream[0] = new ByteArrayInputStream(baos.toByteArray());
			vArrayInputStream[1] = new ByteArrayInputStream(baos.toByteArray());
			return vArrayInputStream;
		} catch (NullPointerException e) {

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 相差天数
	 * 
	 * @return days
	 */
	public static int days(long startDate, long endDate) {
		int days = (int) ((endDate - startDate) / (1000 * 24 * 60 * 60) + 0.5);
		return days;
	}

	public static URL getImagesPath(String filename) {
		return SystemUtil.class.getClassLoader().getResource(
				"images/" + filename);
	}

	/**
	 * 读取文件
	 * 
	 * @param filename
	 */
	public static InputStream readFileByLines(String filename) {

		File file = null;
		if (filename.startsWith("http")) {
			URL effURL = null;
			try {
				effURL = new URL(filename);
			} catch (MalformedURLException e1) {
				// TODO 自动生成 catch 块
				e1.printStackTrace();
			}

			try {
				URLConnection connection = effURL.openConnection();
				connection.setAllowUserInteraction(false);
				connection.setDoInput(true);

				connection.connect();
				return new StreamSource(connection.getInputStream(), filename)
						.getInputStream();

			} catch (FileNotFoundException fnfe) {
				System.out.println("File not found: " + effURL);

			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();

			}
		} else {
			file = new File(filename);
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {

				if (filename.startsWith("file:")) {
					filename = filename.substring(6);
					file = new File(filename);
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e2) {
						e.printStackTrace();
					}

				}
			}
		}
		return null;

	}

	/**
	 * 从头开始复制一定数量的元素到新数组
	 * 
	 * @param original
	 *            源数组。
	 * @param newLength
	 *            要复制的数组元素的数量。
	 * @return 返回复制后的数组
	 */
	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length,
				newLength));
		return copy;
	}

	/**
	 * 从一个位置开始复制到另一个位置的元素到新数组
	 * 
	 * @param original
	 *            源数组
	 * @param from
	 *            源数组中的起始位置。
	 * @param to
	 *            源数组中的终点位置。
	 * @return 返回复制后的数组
	 */
	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length
				- from, newLength));
		return copy;
	}

	/**
	 * 大写字符：R 小写字符：typed r 小键盘字符：全部要转为大写
	 * 如：inset→INSERT、home→HOME、delete→DELETE、page up→PAGE_UP、up->UP
	 * 辅助键：均转化为小写表示 如：ctrl T 帮助键：F1 取消键：esc->ESCAPE 数字键盘：0->NUMPAD0
	 * 
	 * @param stroke
	 * @return
	 */
	public final static String buildStroke(final String stroke) {
		String[] elements = stroke.split(" ");
		StringBuilder temp = new StringBuilder();
		for (String s : elements) {
			if ("ctrl".equalsIgnoreCase(s) || "alt".equalsIgnoreCase(s)
					|| "meta".equalsIgnoreCase(s)
					|| "shift".equalsIgnoreCase(s)
					|| "control".equalsIgnoreCase(s)) {
				temp.append(s.toLowerCase());
			} else if ("altGraph".equalsIgnoreCase(s)) {
				temp.append("altGraph");
			} else if (!"".equalsIgnoreCase(s))
				temp.append(s.toUpperCase());
			if ("page".equalsIgnoreCase(s))
				temp.append("_");
			else
				temp.append(" ");
		}

		return temp.toString().trim();
	}

	/**
	 * @author liuxiao
	 * @param stroke
	 * @return 可用的url
	 * 
	 *         servlet的来源有两种可能，一种是以 http打头的绝对路径，还有一中是/打头的相对路径。
	 *         所以在这个方法里面做了区分，并且只在FrontServletTask这个类和AllBroeseTask这个类中引用
	 */
	public final static String servletUrl(final String servletUrl,
			final String backUrl) {

		StringBuilder a = new StringBuilder();

		if (servletUrl.startsWith("http")) {

		} else {
			a.append(CommincateFactory.serverUrl);
		}
		a.append(servletUrl.replaceAll(" ", "%20"));
		if (backUrl != null)
			a.append(backUrl);

		return a.toString();
	}
	public static boolean isOnWeb()
	{
		String iss=getConfByName("base.runpos");
		if(iss!=null&&"b".equalsIgnoreCase(iss))
			return true;
		else return false;
	}
	public static String getClassesPath() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("license.lic");
		if (url == null)
			throw new RuntimeException("未找到license.lic");
		try {
			String path = URLDecoder.decode(url.getPath(), getFileEncode());
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
				if (path.startsWith("/"))
					path = path.substring(1);
				else if (path.startsWith("file:/")) {
					path = path.substring(6);
				}
			} else if (path.startsWith("file:/")) {
				path = path.substring(5);
			}

			return path.substring(0, path.lastIndexOf("/") + 1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getFileEncode() {
		return System.getProperty("file.encoding");
	}
	
}
