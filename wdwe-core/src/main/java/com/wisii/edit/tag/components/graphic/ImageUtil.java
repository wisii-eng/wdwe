/*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wisii.edit.tag.components.graphic;
//
//import java.awt.Canvas;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.GraphicsConfiguration;
//import java.awt.GraphicsDevice;
//import java.awt.GraphicsEnvironment;
//import java.awt.HeadlessException;
//import java.awt.Image;
//import java.awt.Transparency;
//import java.awt.geom.AffineTransform;
//import java.awt.image.AffineTransformOp;
//import java.awt.image.AreaAveragingScaleFilter;
//import java.awt.image.BufferedImage;
//import java.awt.image.ConvolveOp;
//import java.awt.image.FilteredImageSource;
//import java.awt.image.Kernel;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import javax.swing.ImageIcon;
//
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
//
//public class ImageUtil {
//
//	public static BufferedImage zoomImage(BufferedImage image, int width, int height) {
//		// int width = (int) ((double) image.getWidth() * xscale);
//		// int height = (int) ((double) image.getHeight() * yscale);
//		AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(width, height);
//		FilteredImageSource filteredImageSource = new FilteredImageSource(image.getSource(), areaAveragingScaleFilter);
//		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		Graphics g = result.getGraphics();
//		Canvas canvas = new Canvas();
//		g.drawImage(canvas.createImage(filteredImageSource), 0, 0, null);
//		return result;
//	}
//
//	public static BufferedImage scale(BufferedImage bi, int width, int height) {
//		double ratio = 0.0; // 缩放比例
//		Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
//		// 计算比例
//		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
//			if (bi.getHeight() > bi.getWidth()) {
//				ratio = (new Integer(height)).doubleValue() / bi.getHeight();
//			} else {
//				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
//			}
//			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
//			itemp = op.filter(bi, null);
//		}
//		BufferedImage bufferedImage = toBufferedImage(itemp);
//		return bufferedImage;
//	}
//	
//	public static BufferedImage scale2(BufferedImage bi, int width, int height) {
//		Image image = bi.getScaledInstance(width, height, bi.SCALE_FAST);
//		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		Graphics g = tag.getGraphics();
//		g.drawImage(image, 0, 0, null); 
//		g.dispose();
//		return tag;
//	}
//	
//	
//	public static void resize(File originalFile, File resizedFile,  
//            int newWidth, float quality) throws IOException {  
//  
//        if (quality > 1) {  
//            throw new IllegalArgumentException(  
//                    "Quality has to be between 0 and 1");  
//        }  
//  
//        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());  
//        Image i = ii.getImage();  
//        Image resizedImage = null;  
//  
//        int iWidth = i.getWidth(null);  
//        int iHeight = i.getHeight(null);  
//  
//        if (iWidth > iHeight) {  
//            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight)  
//                    / iWidth, Image.SCALE_SMOOTH);  
//        } else {  
//            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight,  
//                    newWidth, Image.SCALE_SMOOTH);  
//        }  
//  
//        // This code ensures that all the pixels in the image are loaded.  
//        Image temp = new ImageIcon(resizedImage).getImage();  
//  
//        // Create the buffered image.  
//        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null),  
//                temp.getHeight(null), BufferedImage.TYPE_INT_RGB);  
//  
//        // Copy image to buffered image.  
//        Graphics g = bufferedImage.createGraphics();  
//  
//        // Clear background and paint the image.  
//        g.setColor(Color.white);  
//        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));  
//        g.drawImage(temp, 0, 0, null);  
//        g.dispose();  
//  
//        // Soften.  
//        float softenFactor = 0.05f;  
//        float[] softenArray = { 0, softenFactor, 0, softenFactor,  
//                1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };  
//        Kernel kernel = new Kernel(3, 3, softenArray);  
//        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);  
//        bufferedImage = cOp.filter(bufferedImage, null);  
//  
//        // Write the jpeg to a file.  
//        FileOutputStream out = new FileOutputStream(resizedFile);  
//  
//        // Encodes image as a JPEG data stream  
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
//  
//        JPEGEncodeParam param = encoder  
//                .getDefaultJPEGEncodeParam(bufferedImage);  
//  
//        param.setQuality(quality, true);  
//  
//        encoder.setJPEGEncodeParam(param);  
//        encoder.encode(bufferedImage);  
//    } 
//	
//	
//	
//	
//	public static BufferedImage toBufferedImage(Image image) {
//		if (image instanceof BufferedImage) {
//			return (BufferedImage) image;
//		}
//
//		// This code ensures that all the pixels in the image are loaded
//		image = new ImageIcon(image).getImage();
//
//		// Determine if the image has transparent pixels; for this method's
//		// implementation, see e661 Determining If an Image Has Transparent
//		// Pixels
//		// boolean hasAlpha = hasAlpha(image);
//
//		// Create a buffered image with a format that's compatible with the
//		// screen
//		BufferedImage bimage = null;
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		try {
//			// Determine the type of transparency of the new buffered image
//			int transparency = Transparency.OPAQUE;
//			/*
//			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
//			 */
//
//			// Create the buffered image
//			GraphicsDevice gs = ge.getDefaultScreenDevice();
//			GraphicsConfiguration gc = gs.getDefaultConfiguration();
//			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
//		} catch (HeadlessException e) {
//			// The system does not have a screen
//		}
//
//		if (bimage == null) {
//			// Create a buffered image using the default color model
//			int type = BufferedImage.TYPE_INT_RGB;
//			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
//			/*
//			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
//			 */
//			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
//		}
//
//		// Copy image to buffered image
//		Graphics g = bimage.createGraphics();
//
//		// Paint the image onto the buffered image
//		g.drawImage(image, 0, 0, null);
//		g.dispose();
//
//		return bimage;
//	}
//	public static BufferedImage toBufferedImage(Image image,int width,int height) {
//		if (image instanceof BufferedImage) {
//			return (BufferedImage) image;
//		}
//		
//		// This code ensures that all the pixels in the image are loaded
//		image = new ImageIcon(image).getImage();
//		
//		// Determine if the image has transparent pixels; for this method's
//		// implementation, see e661 Determining If an Image Has Transparent
//		// Pixels
//		// boolean hasAlpha = hasAlpha(image);
//		
//		// Create a buffered image with a format that's compatible with the
//		// screen
//		BufferedImage bimage = null;
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		try {
//			// Determine the type of transparency of the new buffered image
//			int transparency = Transparency.OPAQUE;
//			/*
//			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
//			 */
//			
//			// Create the buffered image
//			GraphicsDevice gs = ge.getDefaultScreenDevice();
//			GraphicsConfiguration gc = gs.getDefaultConfiguration();
//			bimage = gc.createCompatibleImage(width, height, transparency);
//		} catch (HeadlessException e) {
//			// The system does not have a screen
//		}
//		
//		if (bimage == null) {
//			// Create a buffered image using the default color model
//			int type = BufferedImage.TYPE_INT_RGB;
//			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
//			/*
//			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
//			 */
//			bimage = new BufferedImage(width,height, type);
//		}
//		
//		// Copy image to buffered image
//		Graphics g = bimage.createGraphics();
//		
//		// Paint the image onto the buffered image
//		g.drawImage(image, 0, 0, null);
//		g.dispose();
//		
//		return bimage;
//	}
//
//
//}
