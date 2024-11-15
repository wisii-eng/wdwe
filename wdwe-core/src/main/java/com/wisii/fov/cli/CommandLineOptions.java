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
 */package com.wisii.fov.cli;

// java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.Version;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.awt.AWTRenderer;
import com.wisii.fov.render.server.ServerRenderer;
import com.wisii.fov.util.CommandLineLogger;

/** 分析、记录和处理命令行参数 */
public class CommandLineOptions
{
	/** Used to indicate that only the result of the XSL transformation should be output */
	public static final int RENDER_NONE = -1;

	// These following constants are used to describe the input (either .FO, .XML/.XSL or intermediate format)
	/** (input) not set */
	public static final int NOT_SET = 0;
	/** input: fo file */
	public static final int FO_INPUT = 1;
	/** input: xml+xsl file */
	public static final int XSLT_INPUT = 2;
    /** input: Area Tree XML file */
    public static final int AREATREE_INPUT = 3;
    
//add by huangzl
	/** display the background */
	public static final String CHECK_YES = "yes";
	/** hide the background */
	public static final String CHECK_NO = "no";
	/** input: FO file as string */
	public static final int FOFILE_INPUT = 0;
	/** input: FO file's name */
	public static final int FONAME_INPUT = 1;
	/** input: xml file as string */
	public static final int XMLFILE_INPUT = 0;
	/** input: XML file's name */
	public static final int XMLNAME_INPUT = 1;
    /** input: XSL file as string */
    public static final int XSLFILE_INPUT = 0;
    /** input: XSL file's name */
    public static final int XSLNAME_INPUT = 1;
	/** output mode: -awt */
	public static final String AWT_OUTPUT = "-awt";
    /** output mode: -wdde */
    public static final String WDDE_OUTPUT = "-wdde";
	/** output mode: -print */
	public static final String PRINT_OUTPUT = "-print";
//add end.

	/* for area tree XML output, only down to block area level */
	private Boolean suppressLowLevelAreas = Boolean.FALSE;
	/* fo file's absolute path */
	private String fopath = null;
    /** 以字符串的形式提供FO文件 */
	private String foFile = null;
	/* xsltfile's absolute path (xslt transformation as input) */
	private String xslpath = null;
	/** 以字符串的形式提供XSL文件 */
    private String xslFile = null;
	/* xml file's absolute path (xslt transformation as input) */
	private String xmlpath = null;
	/** 以字符串的形式提供XML文件 */
    private String xmlFile = null;
    
    /* area tree input file */
    private File areatreefile = null;
    
	/* output file */
	private File outfile = null;
	/* input mode */
	private int inputmode = NOT_SET;
	/* output mode */
	private String outputmode = MimeConstants.MIME_WISII_WDDE_PREVIEW;//chg by huangzl
	/* rendering options (for the user agent) */
	private Map renderingOptions = new java.util.HashMap();
	/* target resolution (for the user agent) */
	private int targetResolution = 0;
    /** 客户端当前显示页面的页码. */
	private int _currentPageNumber = 0;

	private FovFactory factory = FovFactory.newInstance();
	private FOUserAgent foUserAgent;

	private InputHandler inputHandler;

	private Log log;

	private Vector xsltParams = null;

	/** the base URL */
	private String baseUrl = null;

    /** the base URL.当传入参数是文件路径时，从传入参数中获取文件所在的路径 */
	private String selectedFilePath = null;

	/** the mark of hide/display the background*/
	private String _backcolor = CHECK_YES;
    /** the mark of editable */
	private String _xmlEditable = CHECK_NO;

	/* fo mode */
	private int fomode = FONAME_INPUT;
	/* xml mode */
	private int xmlmode = XMLNAME_INPUT;
    /* xml mode */
	private int xslmode = XSLNAME_INPUT;

    /** The renderer that will render the pages. */
	private Renderer _renderer;

	/** 构造方法：创建FovFactory，Log     */
	public CommandLineOptions()
	{
		LogFactory logFactory = LogFactory.getFactory();
		log = LogFactory.getLog("FOV");
	}

	/**
	 * Parse the command line arguments.
	 * @param args the command line arguments.
	 * @throws FOVException for general errors
	 * @throws FileNotFoundException if an input file wasn't found
	 * @throws IOException if the the configuration file could not be loaded
	 */
	public void parse(String[] args) throws FOVException, IOException
	{
		boolean optionsParsed = true;
		try
		{
			optionsParsed = parseOptions(args);
			if (optionsParsed)
			{
				generateAbsolutePath();
				checkSettings();

				//Factory config is set up, now we can create the user agent
				foUserAgent = factory.newFOUserAgent();
                if(baseUrl != null && !"".equals(baseUrl))
                {
                    if(!baseUrl.trim().endsWith("/") && !baseUrl.trim().endsWith("\\"))
                    {
                        baseUrl = baseUrl + "/";
                    }
                    foUserAgent.setBaseURL(this.baseUrl);
                }
                else if(selectedFilePath != null && !"".equals(selectedFilePath))
                {// 当没有设置base url时，把文件所在的路径作为base url，读取配置文件
                    foUserAgent.setBaseURL(selectedFilePath);
                }

				foUserAgent.getRendererOptions().putAll(renderingOptions);
				if (targetResolution != 0)
				{
					foUserAgent.setTargetResolution(targetResolution);
				}
				addXSLTParameter("fov-output-format", getOutputFormat());
				addXSLTParameter("fov-version", Version.getVersion());

                if(CHECK_NO.equalsIgnoreCase(_backcolor))
                {
                    addXSLTParameter("backcolor", "transparent");
                }
                if(CHECK_YES.equalsIgnoreCase(_xmlEditable))
                {
                    addXSLTParameter("XmlEditable", "yes");
                }

			}
			else
			{
				throw new FOVException("非法参数。请参考用户手册。");
			}
		}
		catch (FOVException e)
		{
			//printUsage();
			throw e;
		}
		catch (java.io.FileNotFoundException e)
		{
			//printUsage();
			throw e;
		}

		inputHandler = createInputHandler();
        if(baseUrl != null && !"".equals(baseUrl))
        {
            inputHandler.setDocBaseURL(baseUrl + SystemUtil.CONFRELATIVEPATH);
        }
        else if(selectedFilePath != null && !"".equals(selectedFilePath))
        {// 当没有设置base url时，把文件所在的路径作为base url，读取配置文件
           inputHandler.setDocBaseURL(selectedFilePath  + SystemUtil.CONFRELATIVEPATH);
        }

		if (MimeConstants.MIME_WISII_WDDE_PREVIEW.equalsIgnoreCase(outputmode) ||
            MimeConstants.MIME_WISII_PRINT.equalsIgnoreCase(outputmode) ||
            MimeConstants.MIME_WISII_PAGENUM.equalsIgnoreCase(outputmode))
		{// WDDE应用（B/S架构）
			_renderer = new ServerRenderer();
			_renderer.setUserAgent(foUserAgent);
            ((ServerRenderer)_renderer).setOutputMode(outputmode);
		}
        else if(MimeConstants.MIME_WISII_AWT_PREVIEW.equalsIgnoreCase(outputmode))
        {// 本地的FOV应用
            AWTRenderer renderer = new AWTRenderer(true);
            renderer.setRenderable(inputHandler); //set before user agent!
            renderer.setUserAgent(foUserAgent);
        }
	}

	/**
	 * @return the InputHandler instance defined by the command-line options.
	 */
	public InputHandler getInputHandler()
	{
		return inputHandler;
	}

	/**
	 * Get the logger.
	 * @return the logger
	 */
	public Log getLogger() {
		return log;
	}

	private void addXSLTParameter(String name, String value) {
		if (xsltParams == null) {
			xsltParams = new Vector();
		}
		xsltParams.addElement(name);
		xsltParams.addElement(value);
	}

	/**
	 * parses the commandline arguments
	 * @return true if parse was successful and processing can continue, false
	 * if processing should stop
	 * @exception FOVException if there was an error in the format of the options
	 */
	private boolean parseOptions(String[] args) throws FOVException {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-fo"))
                    i = i + parseFOInputOption(args, i);
                else if (args[i].equals("-xsl"))
                    i = i + parseXSLInputOption(args, i);
                else if (args[i].equals("-xslc"))
                    i = i + parseXSLCInputOption(args, i);
                else if (args[i].equals("-xml"))
                    i = i + parseXMLInputOption(args, i);
                else if (args[i].equals("-awt"))
                    i = i + parseAWTOutputOption(args, i);
//                i = i + parseAreaTreeOption(args, i);//areaTree输出
                
                
                //add by lizhenyou 2008-3-26
                else if (args[i].equals("-at")) { //areaTree输出
                    i = i + parseAreaTreeOption(args, i);
                }
                else if (args[i].equals("-foout")) {//Fo输出
                    i = i + parseFOOutputOption(args, i);
                }
                
                else if (args[i].equals("-atin")) {
                    i = i + parseAreaTreeInputOption(args, i);
                }
                
                //add end
                
                else if (args[i].equals("-print"))
                {
                    i = i + parsePrintOutputOption(args, i);
                }

                else if(args[i].equals("-pcl"))
                {
                    i = i + parsePCLOutputOption(args, i);
                }

                else if (args[i].equals("-pdf"))
                {
                    i = i + parsePDFOutputOption(args, i, null);
                }
                
                else if(args[i].equals("-ps"))
                {
                    i = i + parsePostscriptOutputOption(args, i);
                }
                
                else if (args[i].equals("-wdde"))
                    i = i + parseWDDEOutputOption(args, i);
                else if (args[i].equalsIgnoreCase("-currentpage"))
                    i = i + parsePageNumberOption(args, i);
                else if (args[i].equalsIgnoreCase("-url"))
                    i = i + parseURLOption(args, i);
                else if (args[i].equals("-foc"))
                {
                    i = i + parseFOCInputOption(args, i);
                }
                else if (args[i].equals("-xmlc"))
                {
                    i = i + parseXMLCInputOption(args, i);
                }
                else if (args[i].equals("-background"))
                {
                    i = i + parseBackgroundOption(args, i);
                }
                else if (args[i].equals("-editable"))
                {
//                    i = i + parseEditableOption(args, i);
                }
                else if (args[i].equalsIgnoreCase("-pagenum"))
                {
                    i = i + parsePageNumOutputOption(args, i);
                }
            }
            return true;
	}    // end parseOptions

	private int parseFOInputOption(String[] args, int i) throws FOVException
	{
		inputmode = FO_INPUT;
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定FO文件");
		}
		else
		{
			// fofile = new File(args[i + 1]);//chg by huangzl
            this.fopath = args[i + 1];

            if(fopath.lastIndexOf('/') != -1)
            {// 获取文件所在的路径
                selectedFilePath = fopath.substring(0, fopath.lastIndexOf('/') + 1);
            }
            else if(fopath.lastIndexOf('\\') != -1)
            {
                selectedFilePath = fopath.substring(0, fopath.lastIndexOf('\\') + 1);
            }

			return 1;
		}
	}

	private int parseXSLInputOption(String[] args, int i) throws FOVException
	{
		inputmode = XSLT_INPUT;
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定XSL文件");
		}
		else
		{
            this.xslpath = args[i + 1];

            if(xslpath.lastIndexOf('/') != -1)
            { // 获取文件所在的路径
                selectedFilePath = xslpath.substring(0, xslpath.lastIndexOf('/') + 1);
            }
            else if(xslpath.lastIndexOf('\\') != -1)
            {
                selectedFilePath = xslpath.substring(0, xslpath.lastIndexOf('\\') + 1);
            }

			return 1;
		}
	}

	private int parseXMLInputOption(String[] args, int i) throws FOVException
	{
		inputmode = XSLT_INPUT;
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定XML文件");
		}
		else
		{
            this.xmlpath = args[i + 1];

            if(selectedFilePath == null)
            {// 如果模板的路径和xml数据的路径不一致，以模板的路径为准
                if(xmlpath.lastIndexOf('/') != -1)
                { // 获取文件所在的路径
                    selectedFilePath = xmlpath.substring(0, xmlpath.lastIndexOf('/') + 1);
                }
                else if(xmlpath.lastIndexOf('\\') != -1)
                {
                    selectedFilePath = xmlpath.substring(0, xmlpath.lastIndexOf('\\') + 1);
                }
            }
			return 1;
		}
	}

	private int parseWDDEOutputOption(String[] args, int i) throws FOVException
	{
		setOutputMode(MimeConstants.MIME_WISII_WDDE_PREVIEW);
		return 0;
	}

    private int parseAWTOutputOption(String[] args, int i) throws FOVException
    {
        setOutputMode(MimeConstants.MIME_WISII_AWT_PREVIEW);
        return 0;
    }


	private int parsePrintOutputOption(String[] args, int i) throws FOVException
	{
		setOutputMode(MimeConstants.MIME_WISII_PRINT);
		return 0;
	}

    private int parseAreaTreeOption(String[] args, int i) throws FOVException
    {
    	//del by lizhenyou 2008-3-26
//        setOutputMode(MimeConstants.MIME_FOV_AREA_TREE);
//        return 0;
    	//del end
		setOutputMode(MimeConstants.MIME_WISII_AREA_TREE);
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("you must specify the area-tree output file");
		} else if ((i + 2 == args.length) || (args[i + 2].charAt(0) == '-'))
		{
			// only output file is specified
			outfile = new File(args[i + 1]);
			return 1;
		}
		else
		{
			// mimic format and output file have been specified
//			mimicRenderer = args[i + 1];
//			outfile = new File(args[i + 2]);
			return 2;
		}
    }
    
    private int parseFOOutputOption(String[] args, int i) throws FOVException {
        setOutputMode(MimeConstants.MIME_XSL_FO);
        if ((i + 1 == args.length)
                || (args[i + 1].charAt(0) == '-')) {
            throw new FOVException("you must specify the FO output file");
        } else {
            outfile = new File(args[i + 1]);
            return 1;
        }
    }
    
    private int parseAreaTreeInputOption(String[] args, int i) throws FOVException {
        inputmode = AREATREE_INPUT;
        if ((i + 1 == args.length)
                || (args[i + 1].charAt(0) == '-')) {
            throw new FOVException("you must specify the Area Tree file for the '-atin' option");
        } else {
            areatreefile = new File(args[i + 1]);
            return 1;
        }
    }
    

	private int parseURLOption(String[] args, int i) throws FOVException
	{
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定文件的URL base");
		}
		else
		{
			this.baseUrl = args[i + 1];
			return 1;
		}
	}

	private void generateAbsolutePath()
    {
        if(baseUrl != null && !"".equals(baseUrl))
        {
            if(fopath != null && !(fopath.startsWith(SystemUtil.HTTPSCHEME) || fopath.startsWith(SystemUtil.FILESCHEME)))
            {
                fopath = baseUrl + SystemUtil.FORELATIVEPATH + fopath;
            }
            if(xslpath != null && !(xslpath.startsWith(SystemUtil.HTTPSCHEME) || xslpath.startsWith(SystemUtil.FILESCHEME)))
            {
                xslpath = baseUrl + SystemUtil.TEMPLATEPATH + xslpath;
            }
            if(xmlpath != null && !(xmlpath.startsWith(SystemUtil.HTTPSCHEME) || xmlpath.startsWith(SystemUtil.FILESCHEME)))
            {
                xmlpath = baseUrl + SystemUtil.XMLPATH + xmlpath;
            }
        }
	}

	private int parseXMLCInputOption(String[] args, int i) throws FOVException
	{
		inputmode = XSLT_INPUT;
		xmlmode = XMLFILE_INPUT;
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定XML文件");
		}
		else
		{
			this.xmlFile = args[i + 1];
			return 1;
		}
	}

    private int parseXSLCInputOption(String[] args, int i) throws FOVException
    {
        inputmode = XSLT_INPUT;
        xslmode = XSLFILE_INPUT;
        if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
        {
            throw new FOVException("必须指定XML文件");
        }
        else
        {
            this.xslFile = args[i + 1];
            return 1;
        }
    }


	private int parseFOCInputOption(String[] args, int i) throws FOVException
	{
		inputmode = FO_INPUT;
		fomode = FOFILE_INPUT;
		if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
		{
			throw new FOVException("必须指定FO文件");
		}
		else
		{
			this.foFile = args[i + 1];
			return 1;
		}
	}

    private int parsePageNumberOption(String[] args, int i) throws FOVException
    {
        if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
         {
             throw new FOVException("必须指定页码");
         }
         else
         {
             try
             {
                 _currentPageNumber = Integer.parseInt(args[i + 1]);
             }
             catch(NumberFormatException parseEx)
             {
                 throw new FOVException("必须指定页码");
             }
             return 1;
         }
    }

	private void setOutputMode(String mime) throws FOVException
	{
            outputmode = mime;
	}
//add by huangzl
    /**
     * 解析显示、隐藏背景的参数
     * @param args String[]
     * @throws FOVException
    */
    private int parseBackgroundOption(String[] args, int i) throws FOVException
   {
       if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
        {
            throw new FOVException("必须指定显示模板");
        }
        else
        {
            this._backcolor = args[i + 1];
            return 1;
        }
   }

   /**
    * 解析控制整体编辑权限的参数
    * @param args String[]
    * @throws FOVException
    */
//   private int parseEditableOption(String[] args, int i) throws FOVException
//    {
//        if ((i + 1 == args.length) || (args[i + 1].charAt(0) == '-'))
//         {
//             throw new FOVException("必须指定编辑权限");
//         }
//         else
//         {
////             if(SystemUtil.RELEASE_USER)//true:正式用户使用，可以设置编辑权限
//        	 Calendar c = Calendar.getInstance();
//             long currentDate = c.getTime().getTime();//当前时间
//             long allowDate = Version.getAllowDate() ;//过期时间
//             long packageDate = SystemUtil.packageDate; //打包时间
//             if((packageDate <= allowDate) && (currentDate <= allowDate) && (packageDate <= currentDate))
//             {
//                 this._xmlEditable = args[i + 1];
//             }
//             return 1;
//         }
//    }

    /**
    * 解析输出模式的参数——pagenum
    * @param args String[]
    * @throws FOVException
    */
    private int parsePageNumOutputOption(String[] args, int i) throws FOVException
    {
        setOutputMode(MimeConstants.MIME_WISII_PAGENUM);
        return 0;
    }
//add end

    private int parsePCLOutputOption(String[] args, int i) throws FOVException {
        setOutputMode(MimeConstants.MIME_PCL);
        if ((i + 1 == args.length)
                || (args[i + 1].charAt(0) == '-')) {
            throw new FOVException("you must specify the PDF output file");
        } else {
            outfile = new File(args[i + 1]);
            return 1;
        }
    }

    private int parsePDFOutputOption(String[] args, int i, String pdfAMode) throws FOVException {
        setOutputMode(MimeConstants.MIME_PDF);
        if ((i + 1 == args.length)
                || (args[i + 1].charAt(0) == '-')) {
            throw new FOVException("you must specify the PDF output file");
        } else {
            outfile = new File(args[i + 1]);
            if (pdfAMode != null) {
                if (renderingOptions.get("pdf-a-mode") != null) {
                    throw new FOVException("PDF/A mode already set");
                }
                renderingOptions.put("pdf-a-mode", pdfAMode);
            }
            return 1;
        }
    }
    
    private int parsePostscriptOutputOption(String[] args, int i) throws FOVException {
        setOutputMode(MimeConstants.MIME_POSTSCRIPT);
        if ((i + 1 == args.length)
                || (args[i + 1].charAt(0) == '-')) {
            throw new FOVException("you must specify the PostScript output file");
        } else {
            outfile = new File(args[i + 1]);
            return 1;
        }
    }

	private void setLogLevel(String level)
	{
		// Set the level for future loggers.
		LogFactory.getFactory().setAttribute("level", level);
		if (log instanceof CommandLineLogger)
		{
			// Set the level for the logger created already.
			((CommandLineLogger) log).setLogLevel(level);
		}
	}

	/** checks whether all necessary information has been given in a consistent way     */
	private void checkSettings() throws FOVException, IOException
	{
		InputStream stream= null;
		URL absoluteUrl = null;

		if (inputmode == NOT_SET)
		{
			throw new FOVException("没有指定输入模式");
		}
		if (outputmode == null)
		{
			throw new FOVException("没有指定输出模式");
		}
		if ((outputmode.equals(MimeConstants.MIME_WISII_WDDE_PREVIEW) || outputmode.equals(MimeConstants.MIME_WISII_PRINT) || outputmode.equals(MimeConstants.MIME_WISII_AWT_PREVIEW)) && outfile != null)
		{
			throw new FOVException("输出模式应指定为 " + " AWT  或PRINT");
		}

		if(inputmode == XSLT_INPUT)
		{
			// check whether xml *and* xslt file have been set
			if(xmlpath == null && xmlFile == null)
			{
				throw new FOVException("必须指定XML文件");
			}
			if(xslpath == null && xslFile == null)
			{
				throw new FOVException("必须指定XSLT文件");
			}
			// warning if fofile has been set in xslt mode
			if(fopath != null || foFile != null)
			{
				log.warn("Can't use fo file with transform mode! Ignoring.\n" +
						 "Your input is " + "\n xmlpath: "
						 + xmlpath + "\nxslpath: " + xslpath + "\n  fopath: " + fopath);
			}

//chg by huangzl
			if(xmlpath != null)
			{
//				absoluteUrl = new URL(this.xmlpath);
                try
                {
                    absoluteUrl = new URL(this.xmlpath);
                }
                catch(MalformedURLException mue)
                {
                    absoluteUrl = new URL("file:" + this.xmlpath);
                }

				try
				{
					stream = absoluteUrl.openStream();
				}
				catch(IOException e)
				{
					throw new FileNotFoundException("错误：XML文件 " + xmlpath + " 找不到 ");
				}
				finally
				{
					if(stream != null)
					{
						stream.close();
					}
				}
			}

            if(xslpath != null)
            {
//                absoluteUrl = new URL(this.xslpath);
                try
                {
                    absoluteUrl = new URL(this.xslpath);
                }
                catch(MalformedURLException mue)
                {
                    absoluteUrl = new URL("file:" + this.xslpath);
                }

                try
                {
                    stream = absoluteUrl.openStream();
                }
                catch(IOException e)
                {
                    throw new FileNotFoundException("错误：XSL文件 " + xslpath + " 找不到 ");
                }
                finally
                {
                    if(stream != null)
                    {
                        stream.close();
                    }
                }
            }
//chg end.
		}
		else if(inputmode == FO_INPUT)
		{
	//chg by huangzl
			if(fopath == null && foFile == null)
			{
				throw new FOVException("必须指定FO文件");
			}

			if(xmlpath != null || xslpath != null || xmlFile != null)
			{
				log.warn("fo input mode, but xmlfile or xslt file are set:");
				log.error("xml file: " + xmlpath);
				log.error("xslt file: " + xslpath);
			}

			if(fopath != null)
			{
//				absoluteUrl = new URL(this.fopath);
                try
                {
                    absoluteUrl = new URL(this.fopath);
                }
                catch(MalformedURLException mue)
                {
                    absoluteUrl = new URL("file:" + this.fopath);
                }

				try
				{
					stream = absoluteUrl.openStream();
				}
				catch(IOException e)
				{
					throw new FileNotFoundException("错误：FO文件 " + fopath + " 找不到 ");
				}
				finally
				{
					if(stream != null)
					{
						stream.close();
					}
				}
			}
		}
    } // end checkSettings

	/**
	 * @return the chosen output format (MIME type)
	 * @throws FOVException for invalid output formats
	 */
	public String getOutputFormat() throws FOVException //chg by huagnzl.For it is used by PreviewDialog which is not in package cli
	{
		if (outputmode == null)
			throw new FOVException("显示方式没有设置!");
		if (outputmode.equals(MimeConstants.MIME_WISII_AREA_TREE))
			renderingOptions.put("fineDetail", isCoarseAreaXml());
		return outputmode;
	}

	/**
	 * Create an InputHandler object based on command-line parameters
	 * @return a new InputHandler instance
	 * @throws IllegalArgumentException if invalid/missing parameters
	 */
	private InputHandler createInputHandler() throws IllegalArgumentException
	{
		switch (inputmode)
		{
		case FO_INPUT:
			if(fomode == FONAME_INPUT)
			{
				return new InputHandler(fopath, true);
			}
			else
			{
				return new InputHandler(foFile, false);
			}
		case XSLT_INPUT:
		{
			if(xmlmode == XMLNAME_INPUT)
			{
                if(xslmode == XSLNAME_INPUT)
                {
                    return new InputHandler(xmlpath, xslpath, xsltParams, true, true);
                }
                else
                {
                     return new InputHandler(xmlpath, xslFile, xsltParams, true, false);
                }
			}
			else
			{
                if(xslmode == XSLNAME_INPUT)
                {
                    return new InputHandler(xmlFile, xslpath, xsltParams, false, true);
                }
                else
                {
                     return new InputHandler(xmlFile, xslFile, xsltParams, false, false);
                }
			}
		}
		case AREATREE_INPUT:
		{
			try
			{
				return new AreaTreeInputHandler(new FileInputStream(areatreefile));
			} catch (FileNotFoundException e)
			{
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}
		}
            
		default:
			throw new IllegalArgumentException("创建 InputHandler 出错.");
		}
	}

	/**
	 * Get the FOUserAgent for this Command-Line run
	 * @return FOUserAgent instance
	 */
	public FOUserAgent getFOUserAgent() //chg by huangzl.For it is used by PreviewDialog which is not in package cli
	{
		return foUserAgent;
	}

    /**
     * Returns the input file.
     * @return either the fofile or the xmlfile
     */
    public String getInputFile()
    {
        switch (inputmode)
        {
        case FO_INPUT:
            return fopath;
        case XSLT_INPUT:
            return xmlpath;
        default:
            return fopath;
        }
	}

	/**
	 * Returns the stylesheet to be used for transformation to XSL-FO.
	 * @return stylesheet
	 */
	public String getXSLFile()
	{
		return xslpath;
	}

	/**
	 * Returns the input XMLC file if set.
	 * @return the input XMLC file, null if not set
	 */
	public String getXMLCFile()
	{
		return xmlFile;
	}

    /**
     * Returns the output file
     * @return the output file
     */
    public File getOutputFile()
    {
        return outfile;
    }

	/**
	 * Indicates whether the XML renderer should generate coarse area XML
	 * @return true if coarse area XML is desired
	 */
	public Boolean isCoarseAreaXml()
	{
		return suppressLowLevelAreas;
	}

//add by huangzl.
    /**
     * 设置发送PageViewport数据的writer
     * @param out 向客户端发送数据的writer
     */
    public void setWriter(JspWriter out)
    {
        ((ServerRenderer)_renderer).setWriter(out);
    }

    /**
    * 设置FOStart.
    * @param fo：FOStart中，_returnValue缓存返回给调用者的信息
    */
//   public void setFOStart(FOStart fo)
//   {
//       ((ServerRenderer)_renderer).setFOStart(fo);
//   }

//add end.
}
