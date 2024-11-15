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

// Imported java.io classes
import java.io.OutputStream;
import java.util.Vector;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.StringReader;
import java.io.FileOutputStream;
import java.io.File;

// Imported TraX classes
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.Fov;
import com.wisii.fov.apps.FovFactory;
import com.wisii.fov.apps.FODocURIResolver;

/**
 * Class for handling files input from command line either with XML and XSLT files (and optionally xsl
 * parameters) or FO File input alone
 */
public class InputHandler implements ErrorListener, Renderable
{
	private InputStream sourcefile = null;  // either FO or XML/XSLT usage
	private String sourcedir =null; // Use to save the absolute path of the file.
	private InputStream stylesheet = null; // for XML/XSLT usage
	private String styledir = null;
	private Vector xsltParams = null;

	private String sourcefileAsString = null;
    private String stylefileAsString = null;

	/** the base URL */
	private String baseUrl = null;

    /** the mark of hide/display the background*/
	private String backgroundmode = CommandLineOptions.CHECK_YES;

	/** the logger */
	protected Log log = LogFactory.getLog(InputHandler.class);

	public InputHandler()
	{
		
	}
	
	/**
	 * Constructor for FO input
	 * @param fofile the file to read the FO document.
	 */
	public InputHandler(InputStream fofile)
	{
		sourcefile = fofile;
	}
	
	
	/**
	 * Constructor for XML->XSLT->FO input
	 * @param xmlfile XML file
	 * @param xsltfile XSLT file
	 * @param params Vector of command-line parameters (name, value, name, value, ...) for XSL stylesheet, null if none
	 */
	public InputHandler(String xmlfile, String xsltfile, Vector params, boolean isXmlPath, boolean isXslPath)
	{ //chg by huangzl
		this.xsltParams = params;
		if(isXmlPath)
		{//input: the absolute path of xml file
			this.sourcedir = xmlfile;
		}
		else
		{//input: the content of xml file as string.
			this.sourcefileAsString = xmlfile;
		}
        if(isXslPath)
        {
            this.styledir = xsltfile;
        }
        else
        {
            this.stylefileAsString = xsltfile;
        }
	}

	//lzy123
	/**
	 * Constructor for FO input
	 * @param fofile the file to read the FO document.
	 */
	public InputHandler(String fofile, boolean isPath)
	{//chg by huangzl
		if(isPath)
		{
			this.sourcedir = fofile;
		}
		else
		{
			this.sourcefileAsString = fofile;
		}
	}

	/**
	 * Generate a document, given an initialized Fov object
	 * @param userAgent the user agent
	 * @param outputFormat the output format to generate (MIME type, see MimeConstants)
	 * @param out the output stream to write the generated output to (may be null if not applicable)
	 * @throws FOVException in case of an error during processing
	 */
	public void renderTo(FOUserAgent userAgent, String outputFormat, OutputStream out) throws FOVException
	{		
        long times = 0;
        if(SystemUtil.PRINT_RUN_TIME)
        {
            times = System.currentTimeMillis();
//            System.err.println("TIME_创建FOV、FOTreeBuilder对象开始。时间（毫秒）：" + times);
        }

		FovFactory factory = userAgent.getFactory();
		Fov fov;
		if (out != null)
			fov = factory.newFov(outputFormat, userAgent, out);
		else
			fov = factory.newFov(outputFormat, userAgent);

		// Resulting SAX events (the generated FO) must be piped through to FOV
		Result res = new SAXResult(fov.getDefaultHandler());
        if(SystemUtil.PRINT_RUN_TIME)
        {
            long timeEnd = System.currentTimeMillis();
//            System.err.println("TIME_创建FOV、FOTreeBuilder对象结束。时间（毫秒）：" + timeEnd);
            System.err.println("TIME_创建FOV、FOTreeBuilder对象。消耗时间（毫秒）：" + (timeEnd - times));
        }

		transformTo(res);
	}

	/** @see com.wisii.fov.cli.Renderable */
	public void renderTo(FOUserAgent userAgent, String outputFormat) throws FOVException
	{
		renderTo(userAgent, outputFormat, null);
	}

	/**
	 * In contrast to render(Fov) this method only performs the XSLT stage and saves the intermediate XSL-FO file to the output file.
	 * @param out OutputStream to write the transformation result to.
	 * @throws FOVException in case of an error during processing
	 */
	public void transformTo(OutputStream out) throws FOVException
	{
		Result res = new StreamResult(out);
		transformTo(res);
	}

	/**
	 * Transforms the input document to the input format expected by FOV using XSLT.
	 * @param result the Result object where the result of the XSL transformation is sent to
	 * @throws FOVException in case of an error during processing
	 */
	protected void transformTo(Result result) throws FOVException
	{
		try
		{
            long times = 0;
			URL absoluteUrl = null; //add by huangzl

            if(SystemUtil.PRINT_RUN_TIME)
            {
                times = System.currentTimeMillis();
//                System.err.println("TIME_创建Transformer开始。时间（毫秒）：" + times);
            }

			// Setup XSLT
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;

			if (this.styledir == null && this.stylesheet == null && this.stylefileAsString == null) // FO Input //chg by huagnzl
			{
				transformer = factory.newTransformer();
			}
			else
			{    // XML/XSLT input
                Source src = null;
                if(this.stylefileAsString != null)
                {
                    src = new StreamSource(new StringReader(this.stylefileAsString));
                }
                else
                {
                    if(this.stylesheet == null)
                    {
                        try
                        {
                            absoluteUrl = new URL(this.styledir);
                        }
                        catch(MalformedURLException mue)
                        {
                            absoluteUrl = new URL("file:" + this.styledir);
                        }

                        if(absoluteUrl != null)
                        {
                            this.stylesheet = absoluteUrl.openStream();
                        }
                        else
                        {
                            return;
                        }
                    }
                    src = new StreamSource(stylesheet);
                }
				transformer = factory.newTransformer(src);

				// Set the value of parameters, if any, defined for stylesheet
				if (xsltParams != null)
				{
					for (int i = 0; i < xsltParams.size(); i += 2)
					{
						transformer.setParameter((String) xsltParams.elementAt(i),(String) xsltParams.elementAt(i + 1));
					}
				}


                // 把背景设置为透明色（隐藏背景）
                if(CommandLineOptions.CHECK_NO.equalsIgnoreCase(backgroundmode))
                {
                    transformer.setParameter("backcolor", "transparent");
                }
                
			}

            //used to resolve URIs used in document().
			FODocURIResolver foDocURLResolver = new FODocURIResolver();
			foDocURLResolver.setBaseURL(this.baseUrl);
			transformer.setURIResolver(foDocURLResolver);

			transformer.setErrorListener(this);

			// Create a SAXSource from the input Source file
			Source src = null;
			if(this.sourcefileAsString != null)
			{
                src = new StreamSource(new StringReader(this.sourcefileAsString));
			}
			else
			{
				if(this.sourcefile == null)
				{
					try
					{
						absoluteUrl = new URL(this.sourcedir);
					}
					catch(MalformedURLException mue)
					{
						absoluteUrl = new URL("file:" + this.sourcedir);
					}

					if(absoluteUrl != null)
					{
						this.sourcefile = absoluteUrl.openStream();
					}
					else
					{
						return;
					}
				}
				src = new StreamSource(sourcefile);
			}
			// Start XSLT transformation and FOV processing
            if(SystemUtil.PRINT_RUN_TIME)
            {
                long timeEnd = System.currentTimeMillis();
//                System.err.println("TIME_创建Transformer结束。时间（毫秒）：" + timeEnd);
                System.err.println("TIME_创建Transformer。    消耗时间（毫秒）：" + (timeEnd - times));
                System.err.println("TIME_transform()方法调用开始。  时间（毫秒）：" + timeEnd);
            }
			transformer.transform(src, result);

			//close the inputStream
			if(this.stylesheet != null)
			{
				this.stylesheet.close();
				this.stylesheet = null;
			}
			if(this.sourcefile != null)
			{
				this.sourcefile.close();
				this.sourcefile = null;
			}

            this.sourcefileAsString = null;
            this.stylefileAsString = null;
		}
		catch (Exception e)
		{
			throw new FOVException(e);
		}
	}

	// --- Implementation of the ErrorListener interface ---

	/**
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exc)
	{
		log.warn(exc.toString());
	}

	/**
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exc)
	{
		log.error(exc.toString());
	}

	/**
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exc) throws TransformerException
	{
		throw exc;
	}

    /**
     * set URL Base.used to resolve URIs when document() is called
     */
    public void setDocBaseURL(String url)
    {
        this.baseUrl = url;
    }

    /**
     * set background mode
    */
    public void setBackgroundMode(String background)
    {
        this.backgroundmode = background;
	}

    /** @see com.wisii.fov.cli.Renderable */
    public void renderNew(FOUserAgent userAgent, String outputFormat, String stylesheet,  boolean isStylesheetDir,
                          String source, boolean isSourceDir, String background) throws FOVException
    {
        if(stylesheet != null && source != null)
        {
            if(isSourceDir)
            { //xml input
                this.sourcedir = source;
                this.sourcefileAsString = null;
            }
            else
            { // xmlc/foc input
                this.sourcedir = null;
                this.sourcefileAsString = source;
            }

            if(isStylesheetDir)
            { //xsl input
                this.styledir = source;
                this.stylefileAsString = null;
            }
            else
            { // xslc input
                this.styledir = null;
                this.stylefileAsString = source;
            }
        }
        
        xsltParams.remove("backcolor");
        xsltParams.remove("transparent");
        backgroundmode = background;
        this.stylesheet = null;
        this.sourcefile = null;
        this.renderTo(userAgent, outputFormat);
    }
}
