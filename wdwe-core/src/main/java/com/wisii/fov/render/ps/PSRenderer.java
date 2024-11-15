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
 */package com.wisii.fov.render.ps;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.print.DocFlavor;

import sun.print.PSPrinterJob;
import sun.print.PSStreamPrintService;

import com.wisii.component.setting.PrintRef;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.util.PrintUtil;

/**
 *
 * <p>PSRenderer: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company:www.wisii.com</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PSRenderer extends Java2DRenderer implements Pageable
{
    /** The MIME type for PostScript */
    public static final String MIME_TYPE = "application/postscript";
    private OutputStream _out;
    private OutputStream _bout;
    private PSPrinterJob ps = new PSPrinterJob();
    private PSPageDefinition currentPageDefinition;
   
    public void startRenderer(OutputStream out) throws IOException
    {
        super.startRenderer(out);
        _bout = out;
//      如果没有传入流，则往内存流中写数据
        if(_bout == null)
        {
            _bout = new ByteArrayOutputStream();
        }
            _out = new PSPrintStream(_bout, this);
    }

    /** @see com.wisii.fov.render.java2d.Java2DRenderer#stopRenderer() */
    public void stopRenderer() throws IOException
    {
        super.stopRenderer();
        try
        {
            ps.setPageable(this);
            try
            {
                PSStreamPrintService pss = new PSStreamPrintService(_out);
                ps.setPrintService(pss);
                ps.print();
            }
            catch(Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            _out.flush();
            printPS(userAgent.getWisiibean());
            _out.close();

        }
        catch(IOException ex)
        {
        }

    }

    /** @see java.awt.print.Pageable#getPageFormat(int) */
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
    {
        try
        {
            if(pageIndex >= getNumberOfPages())
            {
                return null;
            }

            PageFormat pageFormat = new PageFormat();
            Paper paper = new Paper();

            Rectangle2D dim = getPageViewport(pageIndex).getViewArea();
            double width = dim.getWidth();
            double height = dim.getHeight();

            // if the width is greater than the height assume lanscape mode
            // and swap the width and height values in the paper format
            
            this.currentPageDefinition = PSPageDefinition.getPageDefinition(
                    Math.round(width), Math.round(height), 1000);

                if(this.currentPageDefinition == null)
                {
                    this.currentPageDefinition = PSPageDefinition.getDefaultPageDefinition();
                    log.warn("Paper type could not be determined. Falling back to: "
                             + this.currentPageDefinition.getName());
                }
                width=this.currentPageDefinition.physicalPageSize.width;
                height=this.currentPageDefinition.physicalPageSize.height;
            if(width > height)
            {
                paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
                paper.setSize(height / 1000d, width / 1000d);
                pageFormat.setOrientation(PageFormat.LANDSCAPE);
            }
            else
            {
                paper.setImageableArea(0, 0, width / 1000d, height / 1000d);
                paper.setSize(width / 1000d, height / 1000d);
                pageFormat.setOrientation(PageFormat.PORTRAIT);
            }

            //打印方向设置
            paper = setStatePaper(width, height, paper);
            pageFormat.setOrientation(this.getOrientation());
            pageFormat.setPaper(paper);
            return pageFormat;
        }
        catch(FOVException fovEx)
        {
            throw new IndexOutOfBoundsException(fovEx.getMessage());
        }
    }
   

    /** @see java.awt.print.Pageable#getPrintable(int) */
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
    {
        return this;
    }

    /**
     * @return the dimensions of the specified page
     * @param pageNum the page number
     * @exception FOVException If the page is out of range or has not been rendered.
     */
    public Dimension getPageImageSize(int pageNum) throws FOVException
    {
        int bitmapWidth = 0;
        int bitmapHeight = 0;
        Rectangle2D bounds = null;
        bounds = getPageViewport(pageNum).getViewArea();

        pageWidth = (int)Math.round(bounds.getWidth() / 1000f);
        pageHeight = (int)Math.round(bounds.getHeight() / 1000f);
        //获取屏幕的DPI显示
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //dpi
        userAgent.setTargetResolution(dpi);
        double scaleX = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
                        / (userAgent.getTargetPixelUnitToMillimeter());
        double scaleY = scaleFactor * (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
                        / (userAgent.getTargetPixelUnitToMillimeter());

        bitmapWidth = (int)((pageWidth * scaleX) + 0.5);
        bitmapHeight = (int)((pageHeight * scaleY) + 0.5);
        return new Dimension(bitmapWidth, bitmapHeight);
    }

    /** @see com.wisii.fov.render.Renderer#setupFontInfo(com.wisii.fov.fonts.FontInfo) */
    public void setupFontInfo()
    {
        setupFontInfo(new FontInfo());
    }

    /** @see com.wisii.fov.render.AbstractRenderer */
    public String getMimeType()
    {
        return MIME_TYPE;
    }

    public String getPrinterName()
	{
		if (printref != null)
		{
			return printref.getPrinter();
		} else
		{
			return null;
		}
	}

    public Map getTable()
    {
        return table;
    }

    public String getPaperEntry(String mediaUsage)
    {
        if(mediaUsage == null || mediaUsage == "")
        {
        	mediaUsage="0";
        }
        //如果以“#”开始，则证明直接是打印控制命令，无需去对照表中找
        if (mediaUsage.startsWith("#"))
		{
			if (mediaUsage.length() == 1)
			{
				return null;
			} else
			{
				return mediaUsage.substring(1);
			}
		}
        if(table == null)
        {
            return "";
        }
        PrintRef info = (PrintRef)table.get(mediaUsage);
        if(info == null)
        {
            return "";
        }
        return info.getCommonLine();
    }

    public void printPS(WisiiBean wb)
    {
        String pname = getPrinterName();
//      如果没有传打印机名称则不打印
        if(pname != null && !pname.trim().equals(""))
        {
            PrintUtil.print(getPrinterName(), new ByteArrayInputStream(((ByteArrayOutputStream)_bout).toByteArray()),
                                  DocFlavor.INPUT_STREAM.POSTSCRIPT,wb.getPrintSetting().getJobName());
        }

    }
}
