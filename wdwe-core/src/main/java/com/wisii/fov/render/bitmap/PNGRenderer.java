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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.wisii.com/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: PNGRenderer.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.bitmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.render.awt.viewer.PreviewPanel;
import com.wisii.fov.render.java2d.Java2DGraphicsState;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.util.Sutil;

/**
 * PNG Renderer This class actually does not render itself, instead it extends
 * <code>com.wisii.fov.render.java2D.Java2DRenderer</code> and just encode
 * rendering results into PNG format using Batik's image codec
 */
public class PNGRenderer extends Java2DRenderer {

    /** The MIME type for png-Rendering */
    public static final String MIME_TYPE = MimeConstants.MIME_PNG;

    /** The file syntax prefix, eg. "page" will output "page1.png" etc */
    private String filePrefix;

    /** The output directory where images are to be written */
    private File outputDir;

//    /** The OutputStream for the first Image */
//    private OutputStream firstOutputStream;
    
//    /** png数组流 add by lizhenyou 2008-4-15 */
//    private OutputStream[] ops = null;
//    //套打图片数据流
//    private OutputStream[] fgops=null;
    private static int printdpi=200;
    private List<Integer> unsucesspages=new ArrayList<Integer>();
    private List<Integer> unsucessfgpages=new ArrayList<Integer>();
    /** @see com.wisii.fov.render.AbstractRenderer */
    public String getMimeType() {
        return MIME_TYPE;
    }

    /** @see com.wisii.fov.render.Renderer#startRenderer(java.io.OutputStream) */
    public void startRenderer(OutputStream outputStream) throws IOException {
        log.info("rendering areas to PNG");
        
        setOutputDirectory();
//        this.firstOutputStream = outputStream;
    }

    /**
     * Sets the output directory, either from the outfile specified on the
     * command line, or from the directory specified in configuration file. Also
     * sets the file name syntax, eg. "page"
     */
    private void setOutputDirectory() {

        // the file provided on the command line
        File f = getUserAgent().getOutputFile();
        if (f == null) {
            //No filename information available. Only the first page will be rendered.
            outputDir = null;
            filePrefix = null;
        } else {
            outputDir = f.getParentFile();

            // extracting file name syntax
            String s = f.getName();
            int i = s.lastIndexOf(".");
            if (s.charAt(i - 1) == '1') {
                i--; // getting rid of the "1"
            }
            filePrefix = s.substring(0, i);
        }

    }

    /** @see com.wisii.fov.render.Renderer#stopRenderer() */
    public void stopRenderer() throws IOException {

        super.stopRenderer();
        while(!unsucesspages.isEmpty()||!unsucessfgpages.isEmpty())
        {
        	try {
				Thread.currentThread().sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
////        ops = new ByteArrayOutputStream[pageViewportList.size()];
//        
       userAgent.setPrintNoBack(false);
//      //  this.antialiasing=false;
//        boolean isonlytao=userAgent.getWisiibean().isOnlyTaoDa();
//        userAgent.setPrintNoBack(isonlytao);
//        for (int i = 0; i < pageViewportList.size(); i++) {
//        	 
//          
////        	ops[i] = new ByteArrayOutputStream();
////        	 
////            if (ops[i] == null) {
////                log.warn("No filename information available."
////                        + " Stopping early after the first page.");
////                break;
////            }
//            try {
//                // Do the rendering: get the image for this page
//                RenderedImage image = (RenderedImage) getPageImage((PageViewport) pageViewportList
//                        .get(i));
//                long old=System.currentTimeMillis();
//                ByteArrayOutputStream b=new ByteArrayOutputStream();
//                ImageIO.write(image, "PNG", b);
//                OutputStream os = getCurrentOutputStream(i);
//                os.write(b.toByteArray());
//                System.out.println("aaa:"+(System.currentTimeMillis()-old));
////                // Encode this image
////                log.debug("Encoding page " + (i + 1));
////                ImageWriterParams params = new ImageWriterParams();
////                params.setResolution(printdpi);
////                params.setJPEGQuality(1,true);
////                // Encode PNG image
////                ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor(getMimeType());
////                log.debug("Writing image using " + writer.getClass().getName());
////                writer.writeImage(image, ops[i], params);
//                
//            } finally {
//                //Only close self-created OutputStreams
////                if (ops[i] != firstOutputStream) {
////                    IOUtils.closeQuietly(ops[i]);
////                }
//            }
//        }
//        if(isonlytao)
//        {
//        	return;
//        }
//        Set deflayers=userAgent.getDefaultLayers();
//        Set alllayer=userAgent.getAllLayers();
//        System.out.println(alllayer);
//        if(deflayers==null||deflayers.isEmpty()||alllayer==null||alllayer.isEmpty())
//        {
//        	return;
//        }
//
//        Set inall=new HashSet();
//        for(Object d:deflayers)
//        {
//        	if(alllayer.contains(d))
//        	{
//        		inall.add(d);
//        	}
//        }
//        //选中的层不是模板的所有层，则生成套打图片 
//        if(inall.size()>0&&inall.size()<alllayer.size())
//        {
////         fgops= new ByteArrayOutputStream[pageViewportList.size()];
//         userAgent.setPrintNoBack(true);
//         for (int i = 0; i < pageViewportList.size(); i++) {
//
//          // OutputStream os = getCurrentFGOutputStream(i);
////        	 fgops[i] = new ByteArrayOutputStream();
//       	 
////           if (fgops[i] == null) {
////               log.warn("No filename information available."
////                       + " Stopping early after the first page.");
////               break;
////           }
//           try {
//               // Do the rendering: get the image for this page
//               RenderedImage image = (RenderedImage) getPageImage((PageViewport) pageViewportList
//                       .get(i));
//   
//               // Encode this image
//               log.debug("Encoding page " + (i + 1));
////               ImageWriterParams params = new ImageWriterParams();
////               params.setResolution(printdpi);
////               params.setJPEGQuality(1,true);
//               ByteArrayOutputStream b=new ByteArrayOutputStream();
//               ImageIO.write(image, "PNG", b);
//               OutputStream os = getCurrentFGOutputStream(i);
//               os.write(b.toByteArray());
////               ImageIO.write(image, "PNG", os);
//               //ImageIO.getImageWriters(type, formatName)
//              // ImageIO.getImageWritersByFormatName(getMimeType()).;
//               // Encode PNG image
//               //ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor(getMimeType());
////               log.debug("Writing image using " + writer.getClass().getName());
//               //writer.writeImage(image, fgops[i], params);
//               
//           } finally {
//               //Only close self-created OutputStreams
////               if (ops[i] != firstOutputStream) {
////                   IOUtils.closeQuietly(ops[i]);
////               }
//           }
//       }
//         userAgent.setPrintNoBack(false);
//        }
    }

    /**
     * Builds the OutputStream corresponding to this page
     * @param 0-based pageNumber
     * @return the corresponding OutputStream
     */
    private OutputStream getCurrentOutputStream(int pageNumber) {

//        if (pageNumber == 0) {
//            return firstOutputStream;
//        }

        if (filePrefix == null) {
            return null;
        } else {
            File f = new File(outputDir,
                    filePrefix + (pageNumber + 1) + ".png");
            try {
                OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                return os;
            } catch (FileNotFoundException e) {
                new FOVException("Can't build the OutputStream\n" + e);
                return null;
            }
        }
    }
    private OutputStream getCurrentFGOutputStream(int pageNumber) {

//      if (pageNumber == 0) {
//          return firstOutputStream;
//      }

      if (filePrefix == null) {
          return null;
      } else {
          File f = new File(outputDir,
                  filePrefix + (pageNumber + 1) + "fg.png");
          try {
              OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
              return os;
          } catch (FileNotFoundException e) {
              new FOVException("Can't build the OutputStream\n" + e);
              return null;
          }
      }
  }
    /**
	 * Generates a desired page from the renderer's page viewport list.
	 * 
	 * @param pageViewport
	 *            the PageViewport to be rendered
	 * @return the <code>java.awt.image.BufferedImage</code> corresponding to
	 *         the page or null if the page doesn't exist.
	 */
	public BufferedImage getPageImage(PageViewport pageViewport) {
		this.currentPageViewport = pageViewport;
		try {
			Rectangle2D bounds = pageViewport.getViewArea();
			pageWidth = (int) Math.round(bounds.getWidth() / 1000f);
			pageHeight = (int) Math.round(bounds.getHeight() / 1000f);

			log.info("Rendering Page " + pageViewport.getPageNumberString()
					+ " (pageWidth " + (int) (pageWidth * 25.4 / 72) + "mm"
					+ ", pageHeight " + (int) (pageHeight * 25.4 / 72) + "mm"
					+ ")");

			// 获取屏幕的DPI显示
			//int dpi = Toolkit.getDefaultToolkit().getScreenResolution(); // dpi
			userAgent.setTargetResolution(printdpi);
			double scaleX = scaleFactor
					* (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
					/ (userAgent.getTargetPixelUnitToMillimeter());
			double scaleY = scaleFactor
					* (25.4 / SystemUtil.DEFAULT_TARGET_RESOLUTION)
					/ (userAgent.getTargetPixelUnitToMillimeter());
			PreviewPanel.setShowPercent(scaleX);
			int bitmapWidth = (int) ((pageWidth * scaleX) + 0.5);
			int bitmapHeight = (int) ((pageHeight * scaleY) + 0.5);
			imageWidth = bitmapWidth;
			imageHeight = bitmapHeight;
			   int[] cmap = new int[256];
               int i=0;
               for (int r=0; r < 256; r += 51) {
                   for (int g=0; g < 256; g += 51) {
                       for (int b=0; b < 256; b += 51) {
                           cmap[i++] = (r<<16)|(g<<8)|b;
                       }
                   }
               }
               // And populate the rest of the cmap with gray values
               int grayIncr = 256/(256-i);
               
               // The gray ramp will be between 18 and 252
               int gray = grayIncr*3;
               for (; i < 256; i++) {
                   cmap[i] = (gray<<16)|(gray<<8)|gray;
                   gray += grayIncr;
               }
              cmap[0]=Color.white.getRGB();
               IndexColorModel  colorModel = new IndexColorModel(8, 256, cmap, 0, false, -1,
                                                DataBuffer.TYPE_BYTE);
			BufferedImage currentPageImage = new BufferedImage(bitmapWidth,
					bitmapHeight, BufferedImage.TYPE_BYTE_INDEXED,colorModel);
			Graphics2D graphics = currentPageImage.createGraphics();
//			graphics.se

//			graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			RenderingHints printHints = new RenderingHints(
	                RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	        printHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        printHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        printHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	        printHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
	        printHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	        printHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        printHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHints(printHints);
//			if (antialiasing) {
//				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						RenderingHints.VALUE_ANTIALIAS_ON);
//				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			}
//			if (qualityRendering) {
//				graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
//						RenderingHints.VALUE_RENDER_QUALITY);
//			}

			// transform page based on scale factor supplied
			AffineTransform at = graphics.getTransform();
			at.scale(scaleX, scaleY);
			graphics.setTransform(at);

			// draw page frame
		//	graphics.setColor(new Color(255,255,255));
			//graphics.fillRect(0, 0, pageWidth, pageHeight);

			state = new Java2DGraphicsState(graphics, this.fontInfo, at);
			try {
				// reset the current Positions
				currentBPPosition = 0;
				currentIPPosition = 0;
				

				renderPageAreas(pageViewport.getPage());
				// 客户端当前时间
				Object all = Sutil.getF("yuyu");
				if (all == null) {
					drawTest((Graphics2D) graphics.create(), pageViewport
							.getTextGlyphVector(false),  currentPageImage.getWidth(),
							currentPageImage.getHeight(), scaleX,
							scaleY);
				} else {
					long ad = (Long) all;
					long ed = System.currentTimeMillis();// 当前时间
					if (Sutil.gc() > ad || ed > ad) {// 绘制
						drawTest((Graphics2D) graphics.create(), pageViewport
								.getTextGlyphVector(true), currentPageImage.getWidth(),
								currentPageImage.getHeight(),scaleX, scaleY);
					}
				}
			} finally {
				state = null;
			}
			return currentPageImage;
		} finally {
			this.currentPageViewport = null;
		}
	}
//	public OutputStream[] getOps()
//	{
//		return ops;
//	}
//    public OutputStream[] getFgOps()
//    {
//         return fgops;    
//    }
    public void renderPage(PageViewport pageViewport) throws IOException {
		// PageViewport clonep=(PageViewport) pageViewport.clone();
		pageViewportList.add(pageViewport);
		
		unsucesspages.add(numberOfPages);
		RenderedImage image = (RenderedImage) getPageImage(pageViewport);
		int index=numberOfPages;
		numberOfPages++;
		Thread t = new PNGFileGenerate(index, image, false);
		t.start();


	}
    private void outtoFile(Integer index,RenderedImage image,boolean isfg)
    {

		// boolean isonlytao=userAgent.getWisiibean().isOnlyTaoDa();
		// userAgent.setPrintNoBack(isonlytao);
    	OutputStream os=null;
		try {
			// Do the rendering: get the image for this page
			// RenderedImage image = (RenderedImage) getPageImage(pageViewport);
			long old = System.currentTimeMillis();
//			ByteArrayOutputStream b=new ByteArrayOutputStream();
			
			if (isfg) {
				 os = getCurrentFGOutputStream(index);
			
			} else {
				 os = getCurrentOutputStream(index);
			}
			if(os==null)
			{
				return;
			}
			//BufferedOutputStream bb = new BufferedOutputStream(os);
			ImageIO.write(image, "PNG", os);
//			ImageIO.w

			if (isfg) {
					unsucessfgpages.remove(index);
			} else {
					unsucesspages.remove(index);
			}
			System.out.println("aaa:" + (System.currentTimeMillis() - old));


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(os!=null){
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			// Only close self-created OutputStreams
			// if (ops[i] != firstOutputStream) {
			// IOUtils.closeQuietly(ops[i]);
			// }
		}

	}
    private class PNGFileGenerate extends Thread
    {
    	int index;
    	boolean isfg=false;
    	RenderedImage image;
    	private PNGFileGenerate(int index,RenderedImage image)
    	{
    
    		this(index,image,false);
    	}
    	private PNGFileGenerate(int index,RenderedImage image,boolean isfg)
    	{
    		this.index=index;
    		this.image=image;
    		this.isfg=isfg;
    	}
    	public boolean isFG()
    	{
    		return isfg;
    	}
		public void run() {
			outtoFile(index,image,isfg);
			
		}
    	
    }

}
