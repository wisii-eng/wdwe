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
 *//**
 * 
 */
package com.wisii.component.setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;
import com.wisii.component.startUp.Start;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.component.validate.validatexml.SchemaDTDXml;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.command.plugin.FOMethod;

/**
 * 该类为Wdwe的接口Bean类
 * 
 * @author liuxiao
 * @version 2.0
 */
public class WisiiBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final String XML = "xml";
	public static final String XSD = "xsd";
	public static final String FO = "fo";
	public static final String XSL = "xslt";
	public static final String DOCID = "docid";
	public static final String EDITTEMPLATEID = "edittempid";
	public static final String USERPARA = "userpara";
	public static final String AUTHORITHID = "authorityid";
	public static final String BASEURL = "baseurl";
	public static final String CURPAGENUM = "currentpagenumber";
	public static final String PERCENT = "percent";
	public static final String SELECTEDLAYERS = "selectedlayers";
	public static final String EDITCOMPONENTS = "editcomponents";
	public static final String SERVLET = "servlet";
	public static final String SETIINGID = "settingid";
	public static final String TASK = "task";
	public static final String TEMPLATEPARA = "templatepara";
	public static final String SERVERURL = "serverurl";
	public static final String SUBMITADD = "submitadd";
	public static final String GRAPHICURL = "graphicurl";
	private static final String EDIT = "edit";
	private static final String PRINTBYFLASH = "PRINTBYFLASH";
	private String xmlhead = null;
	// xslt中编辑部分的代码
	private String editString;
	/** xml数据用String表示 */
	private String xmlString = null;
	/** xml数据用String表示 */
	private String xsdString = null;
	/** xml数据用String表示 */
	private String xslString = null;
	/** fo数据用字符串表示 */
	private String foString = null;
	/** 文档ID，用于标识当前文档，在事件机制的时候用参数docID传递给用户 打印参数 */
	private String docID;
	/** 用户参数，用于在事件机制的时候用传递给用户 打印参数 */
	private String userPara;
	// 输入数据
	/** xml数据用文件名表示 */
	private String xmlFileName = null;
	/** xsl数据用文件名表示 */
	private String xslFileName = null;
	/** fo数据用字符串表示 */
	private String foFileName = null;
	// 验证信息
	/** xsd数据用文件名表示 */
	private String xsdFileName = null;
	/** 层号 打印参数 */
	private String selectedLayers = "";
	/* 【liuxiao add 20090616 start】 */
	/** 用于权限组件间中描述文档的标志 */
	private String editTemplateId;
	/** 用于权限组件间中描述权限 */
	private String authorityId;
	/* 【liuxiao add 20090616 end 】 */
	// 可编辑控件的权限标志组合，通过该参数可控制可编辑控件，与editTemplateId和authorityId组合2选1
	private String editableauthoritys;
	/** 此参数指定xml文档的显示为文档实际大小的百分比 */
	private int percent = -2;
	private String xmlnm;
	/** xsl中document()方法引用外部xml文件所对应baseURL */
	private URL baseurl = null;
	/** output mode */
	private String outputmode = MimeConstants.MIME_WISII_WDDE_PREVIEW;
	// 仅用于标志初始是否可编辑
	private boolean mode = false;
	/** 打印参数 */
	private PrintSetting printSetting;
	private String settingId;
	/** 是否进行整体验证 */
	private boolean isWholeValidate = false;
	/** 模板参数 */
	private Map<String, String> templatePara;
	private String outputfilename;
	private String submitAdd;
	private String graphicurl;//图片上传地址。
	// 用于大数据处理
	private MutiDataBean mdb;
	private Map swingUserPara;
	private boolean printbyflash = false;
	private Source xmlsource;
	public WisiiBean()
	{
	}
	/**
	 * 该构造用于在applet中直接初始化wisiibean用，在弹出窗口方式的客户端情况下使用
	 * 直接在wisiibean内部得到applet中的大部分参数减少静态变量
	 * 
	 */
	public WisiiBean(Start apl)
	{
		// 测试代码
		// this.selectedLayers=;
		/*
		 * this.docID="0909898789";
		 * this.userPara="用户参数";
		 * this.settingId="default";
		 * 
		 * 
		 * 
		 * 
		 * 
		 * this.allinonearg="servlet=/servlet/AllClientServlet?filename=G51,awt";
		 */
		setDocID(apl.getParameter(WisiiBean.DOCID));
		setUserPara(apl.getParameter(WisiiBean.USERPARA));
		setSettingId(apl.getParameter(WisiiBean.SETIINGID));
		setSelectedLayers(apl.getParameter(WisiiBean.SELECTEDLAYERS));
		setEditableauthoritys(apl.getParameter(WisiiBean.EDITCOMPONENTS));
		setPercent(apl.getParameter(WisiiBean.PERCENT));
		/** 【刘晓添加 用于权限标志 20090616 ，在这里面写便于权限触发器来读取这个变量】 */
		setEditTemplateId(apl.getParameter(WisiiBean.EDITTEMPLATEID));
		setAuthorityId(apl.getParameter(WisiiBean.AUTHORITHID));
		setPrintbyFlash(apl.getParameter(WisiiBean.PRINTBYFLASH));
	}
	public Set getparaLayers(String layer)
	{
		Set layers = new HashSet();
		if (layer == null || layer.equalsIgnoreCase("")
				|| "null".equalsIgnoreCase(layer))
			return null;
		String[] ss = layer.split(",");
		for (int i = 0; i < ss.length; i++)
		{
			try
			{
				layers.add(new Integer(ss[i]));
			}
			catch (NumberFormatException e)
			{
				layers.add(0);
			}
		}
		return layers;
	}
	public boolean parseOptions(String[] args)
	{
		String nv = SystemUtil.getConfByName("base.isolationNv");
		for (int i = 0; i < args.length; i++)
		{
			String ss = args[i].toLowerCase();
			if (ss.equals("awt"))
			{ // 做什么用的？
				parseAWTOutputOption(args[i]);
			}
			else if (ss.equals("edit"))
			{
				parseAWTOutputOption(args[i]);
			}
			else if (ss.equals("-awt"))
			{
				parseAWTOutputOption(args[i]);
			}
			else if (ss.equals("wdde"))// cancle
			{
				parseWDDEOutputOption(args, i);
			}
			else if (ss.startsWith("print" + nv))
			{
				parsePrintOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("fo"))
			{
				parseFOInputOption(devideNandV(args[i]));
			}
			else if (ss.equals("-fo"))
			{
				i = i + parseFOInputOption(args[i + 1]);
			}
			else if (ss.startsWith("xsl"))
			{
				parseXSLInputOption(devideNandV(args[i]));
			}
			else if (ss.equals("-xsl"))
			{
				i = i + parseXSLInputOption(args[i + 1]);
				// } else if (ss.equals("-isPrint")) {
				// i = i + parseIsPrintOption(args, i);
			}
			else if (ss.startsWith("xml"))
			{
				parseXMLInputOption(devideNandV(args[i]));
			}
			else if (ss.equals("-xml"))
			{
				i = i + parseXMLInputOption(args[i + 1]);
			}
			else if (ss.startsWith("xsd"))
			{
				parseXSDInputOption(devideNandV(args[i]));
			}
			else if (ss.equals("-xsd"))
			{
				i = i + parseXSDInputOption(args[i + 1]);
			}
			else if (ss.startsWith("pcl"))
			{
				parsePCLOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("pdf"))
			{
				parsePDFOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("flash"))
			{
				parseFlashOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("ps"))
			{
				parsePostscriptOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("tiff"))
			{
				parseTIFFOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("png"))
			{
				parsePNGOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("txt"))
			{
				parseTextOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("rtf"))
			{
				parseRtfOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("afp"))
			{
				parseAFPOutputOption(devideNandV(args[i]));
				/*
				 * del by liuxiao 20090616 } else if (ss.startsWith("editable"))
				 * {
				 * parseEditableOption(devideNandV(ss));
				 */
				/** 【liuxiao 20090616 添加编辑参数的读取】 */
			}
			else if (ss.startsWith("edittempid"))
			{
				parseEditTempIdOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("authorityid"))
			{
				parseAuthorityIdOption(devideNandV(args[i]));
				/*--------------------------------------*/
			}
			else if (ss.startsWith("printsquence"))
			{
				parsePrintSequenceOption(devideNandV(args[i]));
			}
			else if (ss.startsWith(PrintSetting.JUSTPRINTREF))
			{
				parseJustprintref(devideNandV(args[i]));
			}
			else if (ss.startsWith("docid"))
			{
				parseDocIDOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("percent"))
			{
				parsePercentOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("excursionx"))
			{
				parseExcursionXOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("excursiony"))
			{
				parseExcursionYOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("scalex"))
			{
				parseScaleXOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("scaley"))
			{
				parseScaleYOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("isselectedheightcheckbox"))
			{
				parseIsSelectedHeightCheckBoxOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("heightaddabs"))
			{
				parseHeightAddABSOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("submitadd"))
			{
				parseSubmitAddOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("graphicurl"))
			{
				parseGraphicURLOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("printer"))
			{
				parsePrinterOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("orientationrequested"))
			{
				parseOrientationRequestedOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("userpara"))
			{
				parseUserParaOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("settingid"))
			{
				setSettingId(devideNandV(args[i]));
			}
			else if (ss.startsWith("selectedlayers"))
			{
				parseSelectedLayersOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith(WisiiBean.EDITCOMPONENTS))
			{
				parseEditableauthoritysOutputOption(devideNandV(args[i]));
			}
			else if (ss.startsWith("templatepara"))
			{
				parseTemplateparaOption(devideNandV(args[i]));
			}
			else if (ss.startsWith(MutiDataBean.TOTALPAGECOUNT))
			{
				mdb = MutiDataBean.parseTotalPageCount(devideNandV(args[i]),
						mdb);
			}
			else if (ss.startsWith(MutiDataBean.ASKID))
			{
				mdb = MutiDataBean.parseAskId(devideNandV(args[i]), mdb);
			}
			else if (ss.equals("at"))
			{ // areaTree输出
				i = i + parseAreaTreeOption(args, i);
			}
			else if (ss.startsWith("foout"))// 走 fo
			{// Fo输出
				parseFOOutputOption(devideNandV(args[i]));
			}
			else if (ss.equalsIgnoreCase("url"))
			{
				i = i + parseURLOption(args, i);
			}
			else if (ss.equalsIgnoreCase(WisiiBean.PRINTBYFLASH))
			{
				setPrintbyFlash(devideNandV(args[i]));
			}
		}
		return true;
	}// end parseOptions
	private int parseUserParaOutputOption(String ss)
	{
		this.setUserPara(ss);
		return 1;
	}
	private int parseSubmitAddOutputOption(String ss)
	{
		this.setSubmitAdd(ss);
		return 1;
	}
	private int parseGraphicURLOutputOption(String ss)
	{
		this.setGraphicurl(ss);
		return 1;
	}
	private int parseOrientationRequestedOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setOrientationRequested(ss);
		return 1;
	}
	private int parseHeightAddABSOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setHeightAddABS(ss);
		return 1;
	}
	private int parsePrinterOutputOption(String ss)
	{
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		String[] as = ss.split("\\.");
		if (as.length == 1)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length > 3)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			this.printSetting.addPrintRef(rf);
		}
		return 1;
	}
	private int parseSelectedLayersOutputOption(String ss)
	{
		this.selectedLayers = ss;
		return 1;
	}
	private void parseTemplateparaOption(String ss)
	{
		if (this.templatePara == null)
			this.templatePara = new HashMap();
		String[] sss = ss.split(",");
		for (int i = 0; i < sss.length; i++)
		{
			String[] aa = sss[i].split("=");
			if (aa.length > 1)
			{
				this.templatePara.put(aa[0], aa[1]);
			}
		}
	}
	private int parseIsSelectedHeightCheckBoxOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setSelectedHeightCheckBox(ss);
		return 1;
	}
	private int parseScaleYOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setScaleY(ss);
		return 1;
	}
	private int parseScaleXOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setScaleX(ss);
		return 1;
	}
	private int parseExcursionYOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setExcursionY(ss);
		return 1;
	}
	private int parseExcursionXOutputOption(String ss)
	{
		parsePrintSetting();
		this.printSetting.setExcursionX(ss);
		return 1;
	}
	private int parsePercentOutputOption(String ss)
	{
		this.setPercent(ss);
		return 1;
	}
	private int parseDocIDOutputOption(String ss)
	{
		this.setDocID((ss));
		parsePrintSetting();
		this.printSetting.setJobName(ss);
		return 1;
	}
	private int parsePrintSequenceOption(String ss)
	{
		parsePrintSetting();
		// 方法未完成，需要修改
		if (ss != null && ss.length() > 0 && !ss.equalsIgnoreCase("null"))
		{
			setOutputMode(MimeConstants.MIME_WISII_PRINTSEQUENCE);
			this.printSetting.setPrintSquence(ss);
		}
		return 1;
	}
	private int parseJustprintref(String ss)
	{
		parsePrintSetting();
		// 方法未完成，需要修改
		if (ss != null && !ss.equalsIgnoreCase("null"))
			this.printSetting.setJustprintref(ss);
		return 1;
	}
	private void parsePrintSetting()
	{
		if (this.printSetting == null)
			this.printSetting = new PrintSetting();
	}
	private int parseFOInputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_XSL_FO);
		setFoFile(SystemUtil.getURLDecoderdecode(ss));
		return 1;
	}
	private int parseXMLInputOption(String ss)
	{
		this.setXmlFile(SystemUtil.getURLDecoderdecode(ss));
		return 1;
	}
	private int parseXSDInputOption(String ss)
	{
		this.setXsdFile(SystemUtil.getURLDecoderdecode(ss));
		return 1;
	}
	private int parseXSLInputOption(String ss)
	{
		this.setXslFile(SystemUtil.getURLDecoderdecode(ss));
		return 1;
	}
	private int parseAWTOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_WISII_AWT_PREVIEW);
		this.mode = ss.equals(EDIT);
		return 0;
	}
	private int parsePrintOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_WISII_PRINT);
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		String[] as = ss.split("\\.");
		if (as.length == 1)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length > 3)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			this.printSetting.addPrintRef(rf);
		}
		return 0;
	}
	private int parseAreaTreeOption(String[] args, int i)
	{
		setOutputMode(MimeConstants.MIME_WISII_AREA_TREE);
		if ((i + 1 == args.length))
		{
			return 0;
		}
		else if ((i + 2 == args.length))
		{
			// only output file is specified
			parsePrintSetting();
			this.printSetting.setOutputFileName(args[i + 1]);
			return 1;
		}
		else
		{
			return 2;
		}
	}
	private void parseFOOutputOption(String args)
	{
		setOutputMode(MimeConstants.MIME_XSL_FO);
		setOutputfilename(args);
	}
	private int parsePCLOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_PCL);
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		int type = getType(ss);
		if (type == 2)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			rf.setOutputMode(PrintRef.OUTPUTMode.PCL);
			this.printSetting.addPrintRef(rf);
		}
		else if (type == 1)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			rf.setOutputMode(PrintRef.OUTPUTMode.PCL);
			this.printSetting.addPrintRef(rf);
		}
		else if (type == 0)
		{
			this.printSetting.setOutputFileName(ss);
		}
		return 1;
	}
	private int getType(String s)
	{
		if (s == null)
		{
			return -1;
		}
		char[] cs = s.toCharArray();
		int dotcount = 0;
		StringBuffer sb = null;
		boolean isalldigit = true;
		for (char c : cs)
		{
			if (c == '.')
			{
				dotcount++;
				sb = new StringBuffer();
			}
			else
			{
				if (c < '0' || c > '9')
				{
					isalldigit = false;
				}
				if (sb != null)
				{
					sb.append(c);
				}
			}
		}
		// 如果以ps或pcl结尾，则表示时文件
		if (sb != null && sb.length() > 0)
		{
			String end = sb.toString().toLowerCase();
			if (end.equals("ps") || end.equals("pcl"))
			{
				return 0;
			}
		}
		// 如果有三个点则表示是ip地址,且除点意外是数字
		if (dotcount == 3 && isalldigit)
		{
			return 1;
		}
		// 否则是打印机名
		else
		{
			return 2;
		}
	}
	private int parsePDFOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_PDF);
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		String[] as = ss.split("\\.");
		if (as.length == 1)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length > 3)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length == 2)
		{
			this.printSetting.setOutputFileName(ss);
		}
		return 1;
	}
	private int parseFlashOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_FLASH);
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		String[] as = ss.split("\\.");
		if (as.length == 1)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length > 3)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			this.printSetting.addPrintRef(rf);
		}
		else if (as.length == 2)
		{
			this.printSetting.setOutputFileName(ss);
		}
		return 1;
	}
	private int parsePostscriptOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_POSTSCRIPT);
		parsePrintSetting();
		PrintRef rf = new PrintRef();
		int type = getType(ss);
		if (type == 2)
		{
			// printer
			rf.setPrinter(PrintRef.PRINTER_NAME, ss);
			rf.setOutputMode(PrintRef.OUTPUTMode.PS);
			this.printSetting.addPrintRef(rf);
		}
		else if (type == 1)
		{
			rf.setPrinter(PrintRef.PRINTER_IP, ss);
			rf.setOutputMode(PrintRef.OUTPUTMode.PS);
			this.printSetting.addPrintRef(rf);
		}
		else if (type == 0)
		{
			this.printSetting.setOutputFileName(ss);
		}
		return 1;
	}
	private int parseTIFFOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_TIFF);
		parsePrintSetting();
		this.printSetting.setOutputFileName(ss);
		return 1;
	}
	private int parsePNGOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_PNG);
		parsePrintSetting();
		this.printSetting.setOutputFileName(ss);
		return 1;
	}
	private int parseAFPOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_AFP);
		parsePrintSetting();
		this.printSetting.setOutputFileName(ss);
		return 1;
	}
	private int parseTextOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_PLAIN_TEXT);
		parsePrintSetting();
		this.printSetting.setOutputFileName(ss);
		return 1;
	}
	private void parseRtfOutputOption(String ss)
	{
		setOutputMode(MimeConstants.MIME_RTF);
		setOutputfilename(ss);
	}
	private int parseWDDEOutputOption(String[] args, int i)
	{
		setOutputMode(MimeConstants.MIME_WISII_WDDE_PREVIEW);
		return 0;
	}
	private int parseURLOption(String[] args, int i)
	{
		if ((i + 1 >= args.length))
		{
			return 1;
		}
		else
		{
			try
			{
				this.setBaseurl(new URL(args[i + 1]));
			}
			catch (MalformedURLException e)
			{
			}
			return 1;
		}
	}
	private void parseEditTempIdOption(String ss)
	{
		this.setEditTemplateId(ss);
	}
	private void parseAuthorityIdOption(String ss)
	{
		this.setAuthorityId(ss);
	}
	/*----------------------------------------------------------------*/
	private String devideNandV(String a)
	{
		String nv = SystemUtil.getConfByName("base.isolationNv");
		return a.substring(a.indexOf(nv) + nv.length());
	}
	public void setOutputMode(String mime)
	{
		outputmode = mime;
	}
	public String getOutputFormat()
	{
		return outputmode;
	}
	public URL getBaseurl()
	{
		return baseurl;
	}
	public void setBaseurl(URL baseurl)
	{
		this.baseurl = baseurl;
	}
	public boolean isEditable()
	{
		return (editString != null && !editString.isEmpty())
				&& !(editTemplateId == null && authorityId == null && (editableauthoritys == null
						|| "null".equals(editableauthoritys) || editableauthoritys
							.isEmpty()));
	}
	/*----------------------------------*/
	public int getPercent()
	{
		return percent;
	}
	public void setTotalPageCount(String tpc)
	{
		MutiDataBean.parseTotalPageCount(tpc, mdb);
	}
	public int getTotalPageCount()
	{
		if (mdb == null)
			return 0;
		return mdb.getTotalPageCount();
	}
	public void setTotalPageCount(int tpc)
	{
		if (mdb == null)
			mdb = new MutiDataBean();
		mdb.setTotalPageCount(tpc);
	}
	public void setPercent(int percent)
	{
		if (percent == -1 || percent == 0)
		{
			this.percent = percent;
		}
		else if (percent <= 20)
			this.percent = 20;
		else if (percent >= 500)
			this.percent = 500;
		else
			this.percent = percent;
	}
	public void setPercent(String percent)
	{
		try
		{
			this.setPercent(Integer.parseInt(percent));
		}
		catch (Exception e)
		{
			this.percent = 100;
		}
	}
	// 88----------------------------------xml------------------------------------------------//
	public String getXmlString()
	{
		return xmlString;
	}
	public void setXmlFile(String xmlFileName)
	{
		if (xmlString != null && !xmlString.isEmpty())
			return;
		this.xmlFileName = xmlFileName;
		try
		{
			this.xmlString = FOMethod.readFile(xmlFileName,
					SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.print("读取文件时问题：" + e.getMessage());
		}
		if (this.xmlString != null && !this.xmlString.isEmpty())
		{
			xmlsource = null;
		}
	}
	public void setXmlFile(File xmlFile)
	{
		this.xmlFileName = xmlFile.getName();
		try
		{
			this.xmlString = FOMethod.readFile(xmlFile, "UTF-8");
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + xmlFile.getAbsolutePath(),
					"编码为" + SystemUtil.FILE_CHARSET,
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
		if (this.xmlString != null && !this.xmlString.isEmpty())
		{
			xmlsource = null;
		}
	}
	public void setXml(InputStream xmlStream)
	{
		// 将流转成String
		try
		{
			this.xmlString = FOMethod.readInputStream(xmlStream);
		}
		catch (IOException e)
		{
			StatusbarMessageHelper.output("编码转换错误" + SystemUtil.FILE_CHARSET
					+ ":", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
		}
		if (this.xmlString != null && !this.xmlString.isEmpty())
		{
			xmlsource = null;
		}
	}
	public void setXml(String xmlString)
	{
		this.xmlString = xmlString;
		if (this.xmlString != null && !this.xmlString.isEmpty())
		{
			xmlsource = null;
		}
	}
	// 88----------------------------------xml------------------------------------------------//
	// 88----------------------------------xsd------------------------------------------------//
	public String getXsdString()
	{
		return xsdString;
	}
	// -------------------zmz add end -------------------------
	public void setXsd(String xsdString)
	{
		this.xsdString = xsdString;
	}
	// ---------------pw add end--------------------------------
	public void setXsdFile(String xsdFileName)
	{
		if (xsdFileName == null || xsdFileName.isEmpty())
		{
			return;
		}
		this.xsdFileName = xsdFileName;
		try
		{
			this.xsdString = FOMethod.readFile(xsdFileName,
					SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + xsdFileName + "编码为"
					+ SystemUtil.FILE_CHARSET, e.getMessage(),
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setXsdFile(File xsdFile)
	{
		this.xsdFileName = xsdFile.getName();
		try
		{
			this.xsdString = FOMethod
					.readFile(xsdFile, SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + xsdFile.getAbsolutePath(),
					"编码为" + SystemUtil.FILE_CHARSET,
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setXsd(InputStream xsdstream)
	{
		// 将流转成String
		try
		{
			this.xsdString = FOMethod.readInputStream(xsdstream);
		}
		catch (IOException e)
		{
			StatusbarMessageHelper.output("编码转换错误" + SystemUtil.FILE_CHARSET
					+ ":", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	// 88----------------------------------xsl------------------------------------------------//
	// 88----------------------------------xsl------------------------------------------------//
	public String getXslString()
	{
		return xslString;
	}
	public String getWholeXslString()
	{
		return xslString + editString;
	}
	public void setXslFile(String xslFileName)
	{
		if (xslString != null && !xslString.isEmpty())
			return;
		this.xslFileName = xslFileName;
		try
		{
			readInputStream(FOMethod.readFile(xslFileName),
					SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + xslFileName + "编码为"
					+ SystemUtil.FILE_CHARSET, e.getMessage(),
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setXslFile(File xslFile)
	{
		this.xslFileName = xslFile.getName();
		try
		{
			readInputStream(new FileInputStream(xslFile),
					SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			StatusbarMessageHelper.output("编码转换错误" + xslFile.getAbsolutePath(),
					"编码为" + SystemUtil.FILE_CHARSET,
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setXsl(InputStream xslstream)
	{
		// 将流转成String
		try
		{
			readInputStream(xslstream, SystemUtil.FILE_CHARSET);
		}
		catch (IOException e)
		{
			StatusbarMessageHelper.output("编码转换错误" + SystemUtil.FILE_CHARSET
					+ ":", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setXsl(String xslString)
	{
		StringBuffer xslt = new StringBuffer();
		StringBuffer edit = new StringBuffer();
		boolean iseditpart = false;
		int c = 0;
		if ((c = xslString.indexOf("<wdems:wdems")) != -1)
		{
			iseditpart = true;
			xslt.append(xslString.substring(0, c));
			xslString = xslString.substring(c);
		}
		if ((c = xslString.indexOf("</wdems:wdems>")) != -1)
		{
			c += 14;
			iseditpart = false;
			edit.append(xslString.substring(0, c));
			xslString = xslString.substring(c);
		}
		if (iseditpart)
		{
			edit.append(xslString);
		}
		else
			xslt.append(xslString);
		this.xslString = xslt.toString();
		this.editString = edit.toString();
	}
	// 88----------------------------------xsl------------------------------------------------//
	// 88----------------------------------FO------------------------------------------------//
	public String getFoString()
	{
		return foString;
	}
	public void setFo(String foString)
	{
		this.foString = foString;
	}
	public void setFo(InputStream foInputStream)
	{
		// 将流转成String
		try
		{
			this.foString = FOMethod.readInputStream(foInputStream);
		}
		catch (IOException e)
		{
			StatusbarMessageHelper.output("编码转换错误" + SystemUtil.FILE_CHARSET
					+ ":", e.getMessage(), StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setFoFile(String foFileName)
	{
		if (foString != null && !foString.equalsIgnoreCase(""))
			return;
		this.foFileName = foFileName;
		setOutputfilename(this.foFileName);
		try
		{
			readfoInputStream(FOMethod.readFile(foFileName),
					SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + foFileName + "编码为"
					+ SystemUtil.FILE_CHARSET, e.getMessage(),
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public void setFoFile(File file)
	{
		this.foFileName = file.getName();
		try
		{
			this.xsdString = FOMethod.readFile(file, SystemUtil.FILE_CHARSET);
		}
		catch (Exception e)
		{
			StatusbarMessageHelper.output("编码转换错误" + file.getAbsolutePath(),
					"编码为" + SystemUtil.FILE_CHARSET,
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
	}
	public String getFoFileName()
	{
		return foFileName;
	}
	public String getDocID()
	{
		return docID;
	}
	public void setDocID(String docID)
	{
		if (docID == null || "".equalsIgnoreCase(docID))
			return;
		this.docID = docID;
	}
	public PrintSetting getPrintSetting()
	{
		if (printSetting == null)
			printSetting = new PrintSetting();
		return printSetting;
	}
	public void setPrintSetting(PrintSetting printSetting)
	{
		this.printSetting = printSetting;
	}
	public String getSettingId()
	{
		return settingId;
	}
	public void setSettingId(String settingId)
	{
		if (settingId == null || settingId.length() == 0)
			return;
		this.settingId = settingId;
	}
	public void setSelectedLayers(String selectedLayers)
	{
		if (selectedLayers == null || selectedLayers.isEmpty())
		{
			this.selectedLayers = "";
		}
		this.selectedLayers = selectedLayers;
	}
	public String getSelectedLayers()
	{
		return selectedLayers;
	}
	public String getUserPara()
	{
		return userPara;
	}
	public void setUserPara(String userPara)
	{
		if (userPara == null || "".equalsIgnoreCase(userPara))
			return;
		this.userPara = userPara;
	}
	public String getEditTemplateId()
	{
		return editTemplateId;
	}
	public void setEditTemplateId(String editTemplateId)
	{
		if (editTemplateId == null || "".equalsIgnoreCase(editTemplateId))
			return;
		this.editTemplateId = editTemplateId;
	}
	public String getAuthorityId()
	{
		return authorityId;
	}
	public void setAuthorityId(String authorityId)
	{
		if (authorityId == null || "".equalsIgnoreCase(authorityId))
			return;
		this.authorityId = authorityId;
	}
	public boolean isWholeValidate()
	{
		return isWholeValidate;
	}
	public void setWholeValidate(boolean isWholeValidate)
	{
		this.isWholeValidate = isWholeValidate;
	}
	public Map getTemplatePara()
	{
		return templatePara;
	}
	public String getTemplateParaString()
	{
		return templatePara.toString();
	}
	public void setTemplatePara(Map templatePara)
	{
		this.templatePara = templatePara;
	}
	public void putTemplatePara(String key, String value)
	{
		if (templatePara == null)
			templatePara = new HashMap();
		templatePara.put(key, value);
	}
	/* 【刘晓添加 20070706 end】 */
	public boolean validateCommon()
	{
		if (this.xsdString == null)
		{
			StatusbarMessageHelper.output("xsdString为null", "验证需要有schema文件",
					StatusbarMessageHelper.LEVEL.DEBUG);
			return false;
		}
		if (this.xmlString == null)
		{
			StatusbarMessageHelper.output("xmlString为null", "验证需要有xml文件",
					StatusbarMessageHelper.LEVEL.DEBUG);
			return false;
		}
		return SchemaDTDXml.checkXml(xmlString, xsdString);
	}
	private String getRealPath(String serverBaseUrl, String baseUrl,
			String filePath)
	{
		if (filePath == null)
		{
			return null;
		}
		filePath = filePath.trim().replace('\\', '/');
		filePath = removeFirstChar(filePath, '/');
		boolean isAbsolutePath = false;
		try
		{
			File file = new File(filePath);
			isAbsolutePath = file.isAbsolute();
		}
		catch (Exception e)
		{
		}
		String realPath = null;
		if (isAbsolutePath)
		{
			realPath = filePath;
			realPath = realPath.replaceAll("%20", " ");
		}
		else
		{
			if (baseUrl != null)
			{
				// 整理baseurl部分
				baseUrl = baseUrl.trim().replace('\\', '/');
				baseUrl = removeFirstChar(baseUrl, '.');
				baseUrl = removeFirstChar(baseUrl, '/');
				if (!baseUrl.endsWith("/"))
				{
					baseUrl = baseUrl + "/";
				}
				boolean isBaseUrlAbsolutePath = false;
				try
				{
					File baseFile = new File(baseUrl);
					isBaseUrlAbsolutePath = baseFile.isAbsolute();
				}
				catch (Exception e)
				{
				}
				if (isBaseUrlAbsolutePath)
				{
					realPath = baseUrl + filePath;
					realPath = realPath.replaceAll("%20", " ");
				}
				else
				{
					if (serverBaseUrl != null)
					{
						// 整理serverBaseUrl部分
						serverBaseUrl = serverBaseUrl.trim().replace('\\', '/');
						if (!serverBaseUrl.endsWith("/"))
						{
							serverBaseUrl = serverBaseUrl + "/";
						}
						realPath = serverBaseUrl + baseUrl + filePath;
						realPath = realPath.replaceAll("%20", " ");
					}
					else
					{
						StatusbarMessageHelper
								.output("serverBaseUrl is null.",
										"这个种方式的调用方式为：wisiiBean.setServerBaseUrl(path); 请检查",
										StatusbarMessageHelper.LEVEL.DEBUG);
					}
				}
			}
		}
		return realPath;
	}
	private String removeFirstChar(String str, char c)
	{
		if (str == null)
		{
			return "";
		}
		if (str.equals(""))
		{
			return "";
		}
		String result = null;
		if (str.charAt(0) == c)
		{
			if (str.length() == 1)
			{
				result = "";
			}
			else
			{
				str = str.substring(1);
				result = removeFirstChar(str, c);
			}
		}
		else
		{
			result = str;
		}
		return result;
	}
	public static void main(String[] args)
	{
		/*
		 * String wisii_wideImgDataDef =
		 * "<images><image dataType=\"func\" alpha =\"30\" high=\"50\" wide=\"40\" name=\"test1\">"
		 * + "<functionName>test.Test.testOut</functionName>"
		 * + "<returnType>Stream</returnType >"
		 * + "<parameter objType=\"String\" objName=\"p1\">v1</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p2\">v2</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p3\">v3</parameter>"
		 * + "</image>"
		 * + "<image dataType=\"func\" name=\"test2\">"
		 * + "<functionName>test.Test.testOut</functionName>"
		 * + "<returnType>Stream</returnType>"
		 * + "<parameter objType=\"String\" objName=\"p1\">v21</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p2\" >v22</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p3\">v23</parameter>"
		 * +
		 * "</image><image dataType=\"func\" alpha =\"50\" high=\"50\" wide=\"40\" name=\"test3\">"
		 * + "<functionName >test.Test.testOut</functionName>"
		 * + "<returnType>Stream</returnType >"
		 * + "<parameter objType=\"String\" objName=\"p1\">v31</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p2\" >v32</parameter>"
		 * + "<parameter objType =\"String\" objName =\"p3\">v33</parameter>"
		 * + "</image></images>";
		 * System.out.println(SystemUtil.getURLEncode(wisii_wideImgDataDef));
		 */
		WisiiBean bean = new WisiiBean();
		/*
		 * bean.setXsl(
		 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>sdjdsfjhdsdfkjfljfjkdskjhfadhfdsf<wdems:wdems xmlns:wdems=\"http >gfgfg</wdems:wdems>adfadfadfadsfadsf"
		 * );
		 * System.out.println(bean.getEditString());
		 * System.out.println(bean.getXslString());
		 */
		// bean.getRealPath("/xml", "http://localhost:8080/wdems",
		// "http://192.168.0.23:8080/wdems2/Servlet/Servlet");
		try
		{
			FOMethod.readFile("http://localhost:8081/piccPr",
					SystemUtil.FILE_CHARSET);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getXmlFileName()
	{
		return xmlFileName;
	}
	public void setXmlFileName(String xmlFileName)
	{
		this.xmlFileName = xmlFileName;
	}
	public String getXslFileName()
	{
		return xslFileName;
	}
	public void setXslFileName(String xslFileName)
	{
		this.xslFileName = xslFileName;
	}
	public String getXsdFileName()
	{
		return xsdFileName;
	}
	public void setXsdFileName(String xsdFileName)
	{
		this.xsdFileName = xsdFileName;
	}
	public void setFoFileName(String foFileName)
	{
		this.foFileName = foFileName;
	}
	private void readInputStream(InputStream stream, String charest)
			throws IOException
	{
		String line = null;
		StringBuffer xslt = new StringBuffer();
		StringBuffer edit = new StringBuffer();
		InputStreamReader read;
		boolean iseditpart = false;
		read = new InputStreamReader(stream, charest);
		BufferedReader reader = new BufferedReader(read);
		while ((line = reader.readLine()) != null)
		{
			int c = 0;
			if ((c = line.indexOf("<wdems:wdems")) != -1)
			{
				iseditpart = true;
				xslt.append(line.substring(0, c));
				line = line.substring(c);
			}
			if ((c = line.indexOf("</wdems:wdems>")) != -1)
			{
				c += 14;
				iseditpart = false;
				edit.append(line.substring(0, c));
				line = line.substring(c);
			}
			if (iseditpart)
			{
				edit.append(line);
			}
			else
				xslt.append(line);
		}
		this.xslString = xslt.toString();
		this.editString = edit.toString();
	}
	private void readfoInputStream(InputStream stream, String charest)
			throws IOException
	{
		String line = null;
		StringBuffer fo = new StringBuffer();
		InputStreamReader read;
		read = new InputStreamReader(stream, charest);
		BufferedReader reader = new BufferedReader(read);
		while ((line = reader.readLine()) != null)
		{
			fo.append(line);
		}
		this.foString = fo.toString();
	}
	public String getEditString()
	{
		return editString;
	}
	public String getXmlnm()
	{
		return xmlnm;
	}
	public void setXmlnm(String xmlnm)
	{
		this.xmlnm = xmlnm;
	}
	public String getOutputfilename()
	{
		return outputfilename;
	}
	public void setOutputfilename(String outputfilename)
	{
		getPrintSetting().setOutputFileName(outputfilename);
		this.outputfilename = outputfilename;
	}
	public String getSubmitAdd()
	{
		return submitAdd;
	}
	public void setSubmitAdd(String submitAdd)
	{
		this.submitAdd = submitAdd;
	}
	
	
	public String getGraphicurl() {
		return graphicurl;
	}
	public void setGraphicurl(String graphicurl) {
		this.graphicurl = graphicurl;
	}
	public boolean isMode()
	{
		return mode;
	}
	public MutiDataBean getMdb()
	{
		return mdb;
	}
	public void setMdb(MutiDataBean mdb)
	{
		this.mdb = mdb;
	}
	public String getXmlhead()
	{
		return xmlhead;
	}
	public void setXmlhead(String xmlhead)
	{
		this.xmlhead = xmlhead;
	}
	public String getEditableauthoritys()
	{
		return editableauthoritys;
	}
	public void setEditableauthoritys(String editableauthoritys)
	{
		this.editableauthoritys = editableauthoritys;
	}
	private int parseEditableauthoritysOutputOption(String ss)
	{
		this.editableauthoritys = ss;
		return 1;
	}
	public Set getparaEditableauthoritys(String layer)
	{
		Set layers = new HashSet();
		if (layer == null || layer.equalsIgnoreCase("")
				|| "null".equalsIgnoreCase(layer))
			return null;
		String[] ss = layer.split(",");
		for (int i = 0; i < ss.length; i++)
		{
			String authority = ss[i].trim();
			if (authority != null && authority.length() > 0)
			{
				layers.add(ss[i].trim());
			}
		}
		return layers;
	}
	public Map getSwingUserPara()
	{
		if (swingUserPara == null)
		{
			return null;
		}
		return new HashMap(swingUserPara);
	}
	public void setSwingUserPara(Map swingUserPara)
	{
		if (swingUserPara != null)
		{
			swingUserPara = new HashMap(swingUserPara);
		}
		this.swingUserPara = swingUserPara;
	}
	public void setPrintbyFlash(String printbyFlash)
	{
		if (printbyFlash == null)
		{
			return;
		}
		this.printbyflash = Boolean.parseBoolean(printbyFlash);
	}
	public boolean isPrintbyFlash()
	{
		return printbyflash;
	}
	public final Source getXmlSource()
	{
		return xmlsource;
	}
	public final void setXmlSource(Source xmlsource)
	{
		this.xmlsource = xmlsource;
		if (this.xmlsource != null)
		{
			xmlString = null;
		}
	}
}
