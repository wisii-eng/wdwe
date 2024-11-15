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
 */package com.wisii.fov.apps;

// Java
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.Version;
import com.wisii.component.mainFramework.ListListener;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.fo.FOEventHandler;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.render.Renderer;
import com.wisii.fov.render.RendererFactory;
import com.wisii.fov.render.XMLHandlerRegistry;
/**
 * This is the user agent for FOV. It is the entity through which you can
 * interact with the XSL-FO processing and is used by the processing to obtain
 * user configurable options.
 * <p>
 * Renderer specific extensions (that do not produce normal areas on the output)
 * will be done like so: <br>
 * The extension will create an area, custom if necessary <br>
 * this area will be added to the user agent with a key <br>
 * the renderer will know keys for particular extensions <br>
 * eg. bookmarks will be held in a special hierarchical area representing the
 * title and bookmark structure <br>
 * These areas may contain resolvable areas that will be processed with other
 * resolvable areas
 */
public class FOUserAgent {
	/** Defines the default target resolution (72dpi) for FOV */
	// public static final float DEFAULT_TARGET_RESOLUTION = 72.0f; //dpi
	// public static final float DEFAULT_TARGET_RESOLUTION =
	// Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
	private static Log log = LogFactory.getLog("FOV");

	private FovFactory factory;

	/** The base URL for all URL resolutions, especially for external-graphics */
	private String baseURL;

	/** A user settable URI Resolver */
	private URIResolver uriResolver = null;

	private float targetResolution = 72;

	private Map rendererOptions = new java.util.HashMap();

	private File outputFile = null;

	private Renderer rendererOverride = null;

	private FOEventHandler foEventHandlerOverride = null;

	/**
	 * Producer: Metadata element for the system/software that produces the
	 * document. (Some renderers can store this in the document.)
	 */
	protected String producer = "Wisii FOV Version " + Version.getVersion();

	/**
	 * Creator: Metadata element for the user that created the document. (Some
	 * renderers can store this in the document.)
	 */
	protected String creator = null;

	/**
	 * Creation Date: Override of the date the document was created. (Some
	 * renderers can store this in the document.)
	 */
	protected Date creationDate = null;

	/** Author of the content of the document. */
	protected String author = null;

	/** Title of the document. */
	protected String title = null;

	/** Set of keywords applicable to this document. */
	protected String keywords = null;

	private List pageViewportList = new ArrayList();

	private ListListener listlistener = null;

	private Map tranlatetable; // add by zkl,07-04-19,translate table.

	private Map tableinfo; // dynamic table infomation.add by zkl.07-04-19.
	private Set editableauthoritys = null;
	//是否不打印背景层
	private boolean isPrintNoBack = false;
	//是否不显示背景层
	private boolean isViewNoBack = false;
	private Set allLayers = new HashSet();
	private Set selectLayers = new HashSet();
	//是否处于显示状态，false为打印状态
	private boolean isview=true;
	public Set getAllLayers(){
		return new HashSet(this.allLayers);		
	}
	public void addLayer(Object layer){
		this.allLayers.add(layer);
	}
	public void setAllLayers(Set layers){
		if(layers == null){
			this.allLayers.clear();
			return;
		}
		this.allLayers = layers;
	}
	public void setPrintNoBack(boolean isPrintNoBack){
		this.isPrintNoBack = isPrintNoBack;
	}
	 private WisiiBean wisiibean;
	/**
	 * @return the wisiibean
	 */
	public WisiiBean getWisiibean() {
		return wisiibean;
	}
	/**	
	 * @param wisiibean the wisiibean to set
	 */
	public void setWisiibean(WisiiBean wisiibean) {
		this.wisiibean = wisiibean;
		Set set = wisiibean.getparaLayers(wisiibean.getSelectedLayers());
		setSelectLayers(set);
	}
	
	public boolean isViewNoBack() {
		return isViewNoBack;
	}
	public void setViewNoBack(boolean isViewNoBack) {
		this.isViewNoBack = isViewNoBack;
	}
	public Set getSelectLayers() {
		return new HashSet(selectLayers);
	}

	public void setSelectLayers(Set selectLayers) {
		if (selectLayers == null) {
			this.selectLayers.clear();
		} else {
			this.selectLayers = selectLayers;
		}
	}
	public boolean isIsview() {
		return isview;
	}
	public void setIsview(boolean isview) {
		this.isview = isview;
	}
	/* 如果用户在指定了layer参数，但是想在初始化时显示所有车，但打印仅仅打印指定层，则选用该方法。
	 * 如：用户指定1，2层，则界面显示所有层0，1，2层，但打印时仅仅打印1，2层。 */
	public Set getCheckLayers() {
		boolean isnoback = isViewNoBack;
		if (!isview) {
			isnoback = isPrintNoBack;
		}
		if (isnoback&&(isview||canTaoda())) {
//			if (selectLayers == null || selectLayers.isEmpty()) {
//				return getAllLayers();
//			} else {
				return getSelectLayers();
//			}
		} else {
			return getAllLayers();
		}
	}
	public boolean canTaoda()
	{
		if(selectLayers == null || selectLayers.isEmpty())
		{
			return false;
		}
		if(allLayers==null||allLayers.isEmpty()||allLayers.size()==1)
		{
			return false;
		}
		Set alayer=new HashSet(allLayers);
		for(Object layer:selectLayers)
		{
			alayer.remove(layer);
		}
		return !alayer.isEmpty();
	}
	/**
	 * Main constructor. <b>This constructor should not be called directly.
	 * Please use the methods from FovFactory to construct FOUserAgent
	 * instances!</b>
	 * 
	 * @param factory
	 *            the factory that provides environment-level information
	 * @see com.wisii.component.createareatree.apps.FovFactory
	 */
	public FOUserAgent(FovFactory factory) {
		if (factory == null)
			throw new NullPointerException("FOVfactory 参数不能为空");
		this.factory = factory;
	}

	/** @return the associated FovFactory instance */
	public FovFactory getFactory() {
		return this.factory;
	}

	public void setPageViewportList(Object o) {
		listlistener.getListern().add(o);
		listlistener.listener();
	}

	public void registryListListener(ListListener l) {
		listlistener = l;
	}

	// ---------------------------------------------- rendering-run dependent
	// stuff

	/**
	 * Sets an explicit renderer to use which overrides the one defined by the
	 * render type setting.
	 * 
	 * @param renderer
	 *            the Renderer instance to use
	 */
	public void setRendererOverride(Renderer renderer) {
		this.rendererOverride = renderer;
	}

	/**
	 * Returns the overriding Renderer instance, if any.
	 * 
	 * @return the overriding Renderer or null
	 */
	public Renderer getRendererOverride() {
		return rendererOverride;
	}

	/**
	 * Sets an explicit FOEventHandler instance which overrides the one defined
	 * by the render type setting.
	 * 
	 * @param handler
	 *            the FOEventHandler instance
	 */
	public void setFOEventHandlerOverride(FOEventHandler handler) {
		this.foEventHandlerOverride = handler;
	}

	/**
	 * Returns the overriding FOEventHandler instance, if any.
	 * 
	 * @return the overriding FOEventHandler or null
	 */
	public FOEventHandler getFOEventHandlerOverride() {
		return this.foEventHandlerOverride;
	}

	/**
	 * Sets the producer of the document.
	 * 
	 * @param producer
	 *            source of document
	 */
	public void setProducer(String producer) {
		this.producer = producer;
	}

	/**
	 * Returns the producer of the document
	 * 
	 * @return producer name
	 */
	public String getProducer() {
		return producer;
	}

	/**
	 * Sets the creator of the document.
	 * 
	 * @param creator
	 *            of document
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Returns the creator of the document
	 * 
	 * @return creator name
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Sets the creation date of the document.
	 * 
	 * @param creationDate
	 *            date of document
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Returns the creation date of the document
	 * 
	 * @return creation date of document
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the author of the document.
	 * 
	 * @param author
	 *            of document
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the author of the document
	 * 
	 * @return author name
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the title of the document. This will override any title coming from
	 * an fo:title element.
	 * 
	 * @param title
	 *            of document
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the title of the document
	 * 
	 * @return title name
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the keywords for the document.
	 * 
	 * @param keywords
	 *            for the document
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Returns the keywords for the document
	 * 
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * Returns the renderer options
	 * 
	 * @return renderer options
	 */
	public Map getRendererOptions() {
		return rendererOptions;
	}

	/**
	 * Returns the configuration subtree for a specific renderer.
	 * 
	 * @param mimeType
	 *            MIME type of the renderer
	 * @return the requested configuration subtree, null if there's no
	 *         configuration
	 */
	public Configuration getUserRendererConfig(String mimeType) {
		Configuration cfg = getFactory().getUserConfig();
		if (cfg == null || mimeType == null)
			return null;

		Configuration userRendererConfig = null;

		Configuration[] cfgs = cfg.getChild("renderers")
				.getChildren("renderer");
		for (int i = 0; i < cfgs.length; ++i) {
			Configuration child = cfgs[i];
			try {
				if (child.getAttribute("mime").equals(mimeType)) {
					userRendererConfig = child;
					break;
				}
			} catch (ConfigurationException e) {
				// silently pass over configurations without mime type
			}
		}
		log.debug((userRendererConfig == null ? "No u" : "U")
				+ "ser configuration found for MIME type " + mimeType);
		return userRendererConfig;
	}

	/**
	 * Sets the base URL.
	 * 
	 * @param baseURL
	 *            base URL
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * Returns the base URL.
	 * 
	 * @return the base URL
	 */
	public String getBaseURL() {

		if (this.baseURL == null) {
			this.baseURL = SystemUtil.getBaseURL();
		}
		return this.baseURL;
	}

	/**
	 * Sets the URI Resolver.
	 * 
	 * @param resolver
	 *            the new URI resolver
	 */
	public void setURIResolver(URIResolver resolver) {
		this.uriResolver = resolver;
	}

	/**
	 * Returns the URI Resolver.
	 * 
	 * @return the URI Resolver
	 */
	public URIResolver getURIResolver() {
		return this.uriResolver;
	}

	/**
	 * Attempts to resolve the given URI. Will use the configured resolver and
	 * if not successful fall back to the default resolver.
	 * 
	 * @param uri
	 *            URI to access
	 * @return A {@link javax.xml.transform.Source} object, or null if the URI
	 *         cannot be resolved.
	 * @see com.wisii.component.createareatree.apps.FOURIResolver
	 */
	public Source resolveURI(String uri) {
		return resolveURI(uri, getBaseURL());
	}

	/**
	 * Attempts to resolve the given URI. Will use the configured resolver and
	 * if not successful fall back to the default resolver.
	 * 
	 * @param uri
	 *            URI to access
	 * @param base
	 *            the base URI to resolve against
	 * @return A {@link javax.xml.transform.Source} object, or null if the URI
	 *         cannot be resolved.
	 * @see com.wisii.component.createareatree.apps.FOURIResolver
	 */
	public Source resolveURI(String uri, String base) {
		Source source = null;
		// RFC 2397 data URLs don't need to be resolved, just decode them.
		boolean bypassURIResolution = uri.startsWith("data:");
		if (!bypassURIResolution && uriResolver != null) {
			try {
				source = uriResolver.resolve(uri, base);
			} catch (TransformerException te) {
				log.error("Attempt to resolve URI '" + uri + "' failed: ", te);
			}
		}
		if (source == null) {
			// URI Resolver not configured or returned null, use default
			// resolver from the factory
			source = getFactory().resolveURI(uri, base);
		}
		return source;
	}

	/**
	 * Sets the output File.
	 * 
	 * @param f
	 *            the output File
	 */
	public void setOutputFile(File f) {
		this.outputFile = f;
	}

	/**
	 * Gets the output File.
	 * 
	 * @return the output File
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * Returns the conversion factor from pixel units to millimeters. This
	 * depends on the desired target resolution.
	 * 
	 * @return float conversion factor
	 * @see #getTargetResolution()
	 */
	public float getTargetPixelUnitToMillimeter() {
		return 25.4f / this.targetResolution;
	}

	/** @return the resolution for resolution-dependant output */
	public float getTargetResolution() {
		return this.targetResolution;
	}

	/**
	 * Sets the target resolution in dpi. This value defines the target
	 * resolution of bitmap images generated by the bitmap renderers (such as
	 * the TIFF renderer) and of bitmap images generated by filter effects in
	 * Apache Batik.
	 * 
	 * @param dpi
	 *            resolution in dpi
	 */
	public void setTargetResolution(int dpi) {
		this.targetResolution = dpi;
	}

	// ---------------------------------------------- environment-level stuff
	// (convenience access to FovFactory methods)

	/** @return the font base URL */
	public String getFontBaseURL() {
		String fontBaseURL = getFactory().getFontBaseURL();
		return fontBaseURL != null ? fontBaseURL : this.baseURL;
	}

	/**
	 * Returns the conversion factor from pixel units to millimeters. This
	 * depends on the desired source resolution.
	 * 
	 * @return float conversion factor
	 * @see #getSourceResolution()
	 */
	public float getSourcePixelUnitToMillimeter() {
		return getFactory().getSourcePixelUnitToMillimeter();
	}

	/** @return the resolution for resolution-dependant input */
	public float getSourceResolution() {
		return getFactory().getSourceResolution();
	}

	/**
	 * Gets the default page-height to use as fallback, in case
	 * page-height="auto"
	 * 
	 * @return the page-height, as a String
	 * @see FovFactory#getPageHeight()
	 */
	public String getPageHeight() {
		return getFactory().getPageHeight();
	}

	/**
	 * Gets the default page-width to use as fallback, in case page-width="auto"
	 * 
	 * @return the page-width, as a String
	 * @see FovFactory#getPageWidth()
	 */
	public String getPageWidth() {
		return getFactory().getPageWidth();
	}

	/**
	 * Returns whether FOV is strictly validating input XSL
	 * 
	 * @return true of strict validation turned on, false otherwise
	 * @see FovFactory#validateStrictly()
	 */
	public boolean validateStrictly() {
		return getFactory().validateStrictly();
	}

	/**
	 * @return true if the indent inheritance should be broken when crossing
	 *         reference area boundaries (for more info, see the javadoc for the
	 *         relative member variable)
	 * @see FovFactory#isBreakIndentInheritanceOnReferenceAreaBoundary()
	 */
	public boolean isBreakIndentInheritanceOnReferenceAreaBoundary() {
		
		return getFactory().isBreakIndentInheritanceOnReferenceAreaBoundary();
	}

	/**
	 * @return the RendererFactory
	 */
	public RendererFactory getRendererFactory() {
		
		return getFactory().getRendererFactory();
	}

	/**
	 * @return the XML handler registry
	 */
	public XMLHandlerRegistry getXMLHandlerRegistry() {
		return getFactory().getXMLHandlerRegistry();
	}

	// add by
	// huangzl.在ImageLoader.loadImage()方法中，创建ImageFactory时，为了和客户端的调用保持一致，所以增加该方法。
	/** @return the image factory */
	public ImageFactory getImageFactory() {
		return getFactory().getImageFactory();
	}

	/**
	 * add by zkl.
	 * 
	 * @return
	 */
	public Map getTableinfo() {
		return tableinfo;
	}

	/**
	 * add by zkl.
	 * 
	 * @param tableinfo
	 */
	public void setTableinfo(Map tableinfo) {
		this.tableinfo = tableinfo;
	}

	/**
	 * add by zkl.
	 * 
	 * @return
	 */
	public Map getTranlatetable() {
		return tranlatetable;
	}

	/**
	 * translatetable的结构说明：
	 * 
	 * =========================================== - key - value -
	 * =========================================== - translatetableN = Map - - =
	 * key - value - =========================================== add by zkl.
	 * 
	 * @param tranlatetable
	 */
	public void setTranlatetable(Map tranlatetable) {
		this.tranlatetable = tranlatetable;
	}

	/**
	 * Default constructor
	 * 
	 * @see com.wisii.component.createareatree.apps.FovFactory
	 * @deprecated Provided for compatibility only. Please use the methods from
	 *             FovFactory to construct FOUserAgent instances!
	 */
	public FOUserAgent() {
		this(FovFactory.newInstance());
	}

	public List getPageViewportList() {
		return pageViewportList;
	}

	public static final FOUserAgent createFOUserAgent(FOUserAgent source, FOUserAgent result){
		if(source == null || result == null)
			return result;
		
		result.setEditableauthoritys(source.getEditableauthoritys());
		result.setSelectLayers(source.getSelectLayers());
		result.setAllLayers(source.getAllLayers());
		result.setIsview(source.isview);
		result.setViewNoBack(source.isViewNoBack());
		result.setPrintNoBack(source.isPrintNoBack);
		return result;
	}
	/**
	 * @返回  editableauthoritys变量的值
	 */
	public Set getEditableauthoritys()
	{
		return editableauthoritys;
	}
	/**
	 * @param editableauthoritys 设置editableauthoritys成员变量的值
	
	 * 值约束说明
	
	 */
	public void setEditableauthoritys(Set editableauthoritys)
	{
		this.editableauthoritys = editableauthoritys;
	}
	
}
