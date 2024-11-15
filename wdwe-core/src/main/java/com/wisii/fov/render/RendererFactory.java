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
 */package com.wisii.fov.render;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.util.Service;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.area.AreaTreeHandler;
import com.wisii.fov.fo.FOEventHandler;

/** Factory for FOEventHandlers and Renderers. */
public class RendererFactory
{
    /** the logger */
    private static Log log = LogFactory.getLog(RendererFactory.class);

    private Map rendererMakerMapping = new java.util.HashMap();
    private Map eventHandlerMakerMapping = new java.util.HashMap();

    /** 构造方法：从Service中查找所有可用的Renderers和FOEventHandlers。     */
    public RendererFactory()
    {
        discoverRenderers();
        discoverFOEventHandlers();
    }

    /**
     * Add a new RendererMaker. If another maker has already been registered for a
     * particular MIME type, this call overwrites the existing one.
     * @param maker the RendererMaker
     */
    public void addRendererMaker(AbstractRendererMaker maker)
    {
        String[] mimes = maker.getSupportedMimeTypes();
        for (int i = 0; i < mimes.length; i++)
        {
            //This overrides any renderer previously set for a MIME type
            if (rendererMakerMapping.get(mimes[i]) != null)
                log.trace("Overriding renderer for " + mimes[i] + " with " + maker.getClass().getName());
            rendererMakerMapping.put(mimes[i], maker);
        }
    }

    /**
     * Add a new FOEventHandlerMaker. If another maker has already been registered for a
     * particular MIME type, this call overwrites the existing one.
     * @param maker the FOEventHandlerMaker
     */
    public void addFOEventHandlerMaker(AbstractFOEventHandlerMaker maker)
    {
        String[] mimes = maker.getSupportedMimeTypes();
        for (int i = 0; i < mimes.length; i++)
        {
            //This overrides any event handler previously set for a MIME type
            if (eventHandlerMakerMapping.get(mimes[i]) != null)
                log.trace("Overriding FOEventHandler for " + mimes[i] + " with " + maker.getClass().getName());
            eventHandlerMakerMapping.put(mimes[i], maker);
        }
    }

    /**
     * Returns a RendererMaker which handles the given MIME type.
     * @param mime the requested output format
     * @return the requested RendererMaker or null if none is available
     */
    public AbstractRendererMaker getRendererMaker(String mime)
    {
        AbstractRendererMaker maker = (AbstractRendererMaker)rendererMakerMapping.get(mime);
        return maker;
    }

    /**
     * Returns a FOEventHandlerMaker which handles the given MIME type.
     * @param mime the requested output format
     * @return the requested FOEventHandlerMaker or null if none is available
     */
    public AbstractFOEventHandlerMaker getFOEventHandlerMaker(String mime)
    {
        AbstractFOEventHandlerMaker maker = (AbstractFOEventHandlerMaker)eventHandlerMakerMapping.get(mime);
        return maker;
    }

    /**
     * Creates a Renderer object based on render-type desired
     * @param userAgent the user agent for access to configuration
     * @param outputFormat the MIME type of the output format to use (ex. "application/pdf").
     * @return the new Renderer instance
     * @throws FOVException if the renderer cannot be properly constructed
     */
    public Renderer createRenderer(FOUserAgent userAgent, String outputFormat) throws FOVException
    {
       if (userAgent.getRendererOverride() != null)
        {
            return userAgent.getRendererOverride(); // AWT、PageNum显示时CommandOptions中已创建的AWTRenderer
        }
        else
        {
            AbstractRendererMaker maker = getRendererMaker(outputFormat);//-print时
            if (maker == null)
				throw new UnsupportedOperationException("没有合适的Renderer" );

            Renderer rend = maker.makeRenderer(userAgent);// PrintRenderer
            rend.setUserAgent(userAgent);
            String mimeType = rend.getMimeType(); //Always use main MIME type for this
            Configuration userRendererConfig = null;
            if (mimeType != null)
                userRendererConfig = userAgent.getUserRendererConfig(mimeType);
            if (userRendererConfig != null)
            {
                try
                {
                    ContainerUtil.configure(rend, userRendererConfig);
                }
                catch (ConfigurationException e)
                {
                    throw new FOVException(e);
                }
            }
            return rend;
        }
    }

    /**
     * Creates FOEventHandler instances based on the desired output.
     * @param userAgent the user agent for access to configuration
     * @param outputFormat the MIME type of the output format to use (ex. "application/pdf").
     * @param out the OutputStream where the output is written to (if applicable)
     * @return the newly constructed FOEventHandler
     * @throws FOVException if the FOEventHandler cannot be properly constructed
     */
    public FOEventHandler createFOEventHandler(FOUserAgent userAgent, String outputFormat, OutputStream out) throws FOVException
    {
        if (userAgent.getFOEventHandlerOverride() != null)
            return userAgent.getFOEventHandlerOverride();
        else
        {
            AbstractFOEventHandlerMaker maker = getFOEventHandlerMaker(outputFormat);
            if (maker == null)
            {
                AbstractRendererMaker rendMaker = getRendererMaker(outputFormat);
                if (rendMaker == null && userAgent.getRendererOverride() == null)
                {
                 throw new UnsupportedOperationException(
						"没有FOEventHandler和Renderer可被使用."
                            );
                }
                else
                {
                    if (out == null && userAgent.getRendererOverride() == null && rendMaker.needsOutputStream())
                    {
                        throw new FOVException("没有设置OutputStream");
                    }
                    //Found a Renderer so we need to construct an AreaTreeHandler.
                    return new AreaTreeHandler(userAgent, outputFormat, out);
                }
            }
            else
                return maker.makeFOEventHandler(userAgent, out);
        }
    }

    /** 从 classes\META-INF\services\com.wisii.fov.render.Renderer 文件中读取可用的各Renderer类并动态记录它们。     */
//del by huangzl
	/*
    private void discoverRenderers()
    {
    }*/
//add by huangzl
//	private void discoverRenderers()
	{
//del by huangzl.
//		addRendererMaker(new com.wisii.fov.render.txt.TXTRendererMaker());
//		addRendererMaker(new com.wisii.fov.render.awt.AWTRendererMaker());
//		addRendererMaker(new com.wisii.fov.render.xml.XMLRendererMaker());
//		addRendererMaker(new com.wisii.fov.render.print.PrintRendererMaker());
		//20080724liuxiao注掉这句把service文件夹放进来
//		addRendererMaker(new com.wisii.fov.render.xml.XMLRendererMaker());
//del end.
	}
//add end

    /**
 * Discovers Renderer implementations through the classpath and dynamically
 * registers them.
 */
private void discoverRenderers()
{
    // add mappings from available services
    Iterator providers
        = Service.providers(Renderer.class);
    if(providers != null)
    {
        while(providers.hasNext())
        {
            AbstractRendererMaker maker = (AbstractRendererMaker)providers.next();
            try
            {
                if(log.isDebugEnabled())
                {
                    log.debug("Dynamically adding maker for Renderer: "
                              + maker.getClass().getName());
                }
                addRendererMaker(maker);
            }
            catch(IllegalArgumentException e)
            {
                log.error("Error while adding maker for Renderer", e);
            }
        }
    }
}

    /** 从 classes\META-INF\services\com.wisii.fov.fo.FOEventHandler 文件中读取可用的各FOEventHandler类并动态记录它们。    */
    private void discoverFOEventHandlers()
    {
        // add mappings from available services
        Iterator providers = Service.providers(FOEventHandler.class);
        if (providers != null)
        {
            while (providers.hasNext())
            {
                AbstractFOEventHandlerMaker maker = (AbstractFOEventHandlerMaker)providers.next();
                try
                {
                    if (log.isDebugEnabled())
                        log.debug("Dynamically adding maker for FOEventHandler: " + maker.getClass().getName());
                    addFOEventHandlerMaker(maker);
                }
                catch (IllegalArgumentException e)
                {
                    log.error("Error while adding maker for FOEventHandler", e);
                }
            }
        }
    }
}
