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
 */package com.wisii.fov.image;

// Java
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// FOV
import com.wisii.fov.image.analyser.ImageReaderFactory;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.datatypes.URISpecification;

/**
 * Create FovImage objects (with a configuration file - not yet implemented).
 */
public final class ImageFactory {

    /**
     * logging instance
     */
    protected static Log log = LogFactory.getLog(FovImage.class);

    private HashMap imageMimeTypes = new HashMap();

    private ImageCache cache = new ContextImageCache(true);

    /**
     * Main constructor for the ImageFactory.
     */
    public ImageFactory() {
        /* @todo The mappings set up below of image mime types to implementing
         * classes should be made externally configurable
         */
//        ImageProvider jaiImage = new ImageProvider("JAIImage", com.wisii.fov.image.JAIImage.class);
//        ImageProvider jimiImage = new ImageProvider("JIMIImage", com.wisii.fov.image.JimiImage.class);
        ImageProvider imageIoImage = new ImageProvider(
                "ImageIOImage", com.wisii.fov.image.ImageIOImage.class);
        ImageProvider gifImage = new ImageProvider("GIFImage", com.wisii.fov.image.GifImage.class);
        ImageProvider jpegImage = new ImageProvider("JPEGImage", com.wisii.fov.image.JpegImage.class);
        ImageProvider jpegImageIOImage = new ImageProvider(
                "JPEGImage", com.wisii.fov.image.JpegImageIOImage.class);
        ImageProvider bmpImage = new ImageProvider("BMPImage", com.wisii.fov.image.BmpImage.class);
        ImageProvider epsImage = new ImageProvider("EPSImage", com.wisii.fov.image.EPSImage.class);
        ImageProvider pngImage = new ImageProvider("PNGImage", com.wisii.fov.image.PNGImage.class);
        ImageProvider tiffImage = new ImageProvider("TIFFImage", com.wisii.fov.image.TIFFImage.class);
        ImageProvider xmlImage = new ImageProvider("XMLImage", com.wisii.fov.image.XMLImage.class);
        ImageProvider emfImage = new ImageProvider("EMFImage", com.wisii.fov.image.EmfImage.class);

        ImageMimeType imt = new ImageMimeType("image/gif");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(imageIoImage);
        imt.addProvider(gifImage);

        imt = new ImageMimeType("image/jpeg");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(jpegImageIOImage);
        imt.addProvider(jpegImage);

        imt = new ImageMimeType("image/bmp");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(bmpImage);

        imt = new ImageMimeType("image/eps");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(epsImage);

        imt = new ImageMimeType("image/png");
        imageMimeTypes.put(imt.getMimeType(), imt);
        //Image I/O is faster and more memory-efficient than own codec for PNG
        imt.addProvider(imageIoImage);
        imt.addProvider(pngImage);

        imt = new ImageMimeType("image/tga");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(imageIoImage);

        imt = new ImageMimeType("image/tiff");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(tiffImage); //Slower but supports CCITT embedding
        imt.addProvider(imageIoImage); //Fast but doesn't support CCITT embedding

        imt = new ImageMimeType("image/svg+xml");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(xmlImage);

        imt = new ImageMimeType("text/xml");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(xmlImage);

        imt = new ImageMimeType("image/emf");
        imageMimeTypes.put(imt.getMimeType(), imt);
        imt.addProvider(emfImage);
    }

    /**
     * Get the url string from a wrapped url.
     *
     * @param href the input wrapped url
     * @return the raw url
     */
    public static String getURL(String href) {
        return URISpecification.getURL(href);
    }

    /**
     * Get the image from the cache or load.
     * If this returns null then the image could not be loaded
     * due to an error. Messages should be logged.
     * Before calling this the getURL(url) must be used.
     *
     * @param url the url for the image
     * @param context the user agent context
     * @return the fov image instance
     */
    public FovImage getImage(String url, FOUserAgent context) {
        return cache.getImage(url, context);
    }

    /**
     * Release an image from the cache.
     * This can be used if the renderer has its own cache of
     * the image.
     * The image should then be put into the weak cache.
     *
     * @param url the url for the image
     * @param context the user agent context
     */
    public void releaseImage(String url, FOUserAgent context) {
        cache.releaseImage(url, context);
    }

    /**
     * Release the context and all images in the context.
     *
     * @param context the context to remove
     */
    public void removeContext(FOUserAgent context) {
        cache.removeContext(context);
    }

    /**
     * Create an FovImage objects.
     * @param href the url for the image
     * @param ua the user agent context
     * @return the fov image instance
     */
    public FovImage loadImage(String href, FOUserAgent ua) {

        Source source = ua.resolveURI(href);
        if (source == null) {
            return null;
        }

        // Got a valid source, obtain an InputStream from it
        InputStream in = null;
        if (source instanceof StreamSource) {
            in = ((StreamSource)source).getInputStream();
        }
        if (in == null) {
            try {
                in = new java.net.URL(source.getSystemId()).openStream();
            } catch (Exception ex) {
                log.error("Unable to obtain stream from id '"
                    + source.getSystemId() + "'");
            }
        }
        if (in == null) {
            return null;
        }

        //Make sure the InputStream is decorated with a BufferedInputStream
        if (!(in instanceof java.io.BufferedInputStream)) {
            in = new java.io.BufferedInputStream(in);
        }

        // Check image type
        FovImage.ImageInfo imgInfo = null;
        try {
            imgInfo = ImageReaderFactory.make(source.getSystemId(), in, ua);
        } catch (Exception e) {
            log.error("Error while recovering image information ("
                    + href + ") : " + e.getMessage(), e);
            return null;
        }
        if (imgInfo == null) {
            try {
                in.close();
                in = null;
            } catch (Exception e) {
                log.debug("Error closing the InputStream for the image", e);
            }
            log.error("No ImageReader for this type of image (" + href + ")");
            return null;
        }
        // Associate mime-type to FovImage class
        String imgMimeType = imgInfo.mimeType;
        Class imageClass = getImageClass(imgMimeType);
        if (imageClass == null) {
            log.error("Unsupported image type (" + href + "): " + imgMimeType);
            return null;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Loading " + imgMimeType + " with " + imageClass.getName()
                        + ": " + href);
            }
        }

        // load the right image class
        // return new <FovImage implementing class>
        Object imageInstance = null;
        try {
            Class[] imageConstructorParameters = new Class[1];
            imageConstructorParameters[0] = com.wisii.fov.image.FovImage.ImageInfo.class;
            Constructor imageConstructor = imageClass.getDeclaredConstructor(
                    imageConstructorParameters);
            Object[] initArgs = new Object[1];
            initArgs[0] = imgInfo;
            imageInstance = imageConstructor.newInstance(initArgs);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            String msg;
            if (t != null) {
                msg = t.getMessage();
            } else {
                msg = ex.getMessage();
            }
            log.error("Error creating FovImage object ("
                    + href + "): " + msg, (t == null) ? ex : t);
            return null;
        } catch (InstantiationException ie) {
            log.error("Error creating FovImage object ("
                    + href + "): Could not instantiate " + imageClass.getName() + " instance");
            return null;
        } catch (Exception ex) {
            log.error("Error creating FovImage object ("
                    + href + "): " + ex.getMessage(), ex);
            return null;
        }
        if (!(imageInstance instanceof com.wisii.fov.image.FovImage)) {
            log.error("Error creating FovImage object (" + href + "): " + "class "
                    + imageClass.getName()
                    + " doesn't implement com.wisii.fov.image.FovImage interface");
            return null;
        }
        return (FovImage) imageInstance;
    }

    private Class getImageClass(String imgMimeType) {
        ImageMimeType imt = (ImageMimeType)imageMimeTypes.get(imgMimeType);
        if (imt == null) {
            return null;
        }
        return imt.getFirstImplementingClass();
    }

    /**
     * Forces all the image caches to be cleared. This should normally only be used in
     * testing environments. If you happen to think that you need to call this yourself
     * in a production environment, please notify the development team so we can look
     * into the issue. A call like this shouldn't be necessary anymore like it may have
     * been with FOV 0.20.5.
     */
    public void clearCaches() {
        cache.clearAll();
    }
}

/**
 * Basic image cache.
 * This keeps track of invalid images.
 */
class BasicImageCache implements ImageCache {

    private Set invalid = Collections.synchronizedSet(new java.util.HashSet());
    //private Map contextStore = Collections.synchronizedMap(new java.util.HashMap());

    public FovImage getImage(String url, FOUserAgent context) {
        if (invalid.contains(url)) {
            return null;
        }
        //TODO Doesn't seem to be fully implemented. Do we need it at all? Not referenced.
        return null;
    }

    public void releaseImage(String url, FOUserAgent context) {
        // do nothing
    }

    public void invalidateImage(String url, FOUserAgent context) {
        // cap size of invalid list
        if (invalid.size() > 100) {
            invalid.clear();
        }
        invalid.add(url);
    }

    public void removeContext(FOUserAgent context) {
        // do nothing
    }

    /** @see com.wisii.fov.image.ImageCache#clearAll() */
    public void clearAll() {
        invalid.clear();
    }

}

/**
 * This is the context image cache.
 * This caches images on the basis of the given context.
 * Common images in different contexts are currently not handled.
 * There are two possiblities, each context handles its own images
 * and renderers can cache information or images are shared and
 * all information is retained.
 * Once a context is removed then all images are placed into a
 * weak hashmap so they may be garbage collected.
 */
class ContextImageCache implements ImageCache {

    // if this cache is collective then images can be shared
    // among contexts, this implies that the base directory
    // is either the same or does not effect the images being
    // loaded
    private boolean collective;
    private Map contextStore = Collections.synchronizedMap(new java.util.HashMap());
    private Set invalid = null;
    private Map refStore = null;
    private ReferenceQueue refQueue = new ReferenceQueue();

    public ContextImageCache(boolean col) {
        collective = col;
        if (collective) {
            refStore = Collections.synchronizedMap(new java.util.HashMap());
            invalid = Collections.synchronizedSet(new java.util.HashSet());
        }
    }

    // sync around lookups and puts
    // another sync around load for a particular image
    public FovImage getImage(String url, FOUserAgent context) {
        ImageLoader im = null;
        // this protects the finding or creating of a new
        // ImageLoader for multi threads
        synchronized (this) {
            if (collective && invalid.contains(url)) {
                return null;
            }
            Context con = (Context) contextStore.get(context);
            if (con == null) {
                con = new Context(context, collective);
                contextStore.put(context, con);
            } else {
                if (con.invalid(url)) {
                    return null;
                }
                im = con.getImage(url);
            }
            if (im == null && collective) {
                Iterator i = contextStore.values().iterator();
                while (i.hasNext()) {
                    Context c = (Context)i.next();
                    if (c != con) {
                        im = c.getImage(url);
                        if (im != null) {
                            break;
                        }
                    }
                }
                if (im == null) {
                    Reference ref = (Reference)refStore.get(url);
                    if (ref != null) {
                        im = (ImageLoader) ref.get();
                        if (im == null) {
                            //Remove key if its value has been garbage collected
                            refStore.remove(url);
                        }
                    }
                }
            }

            if (im != null) {
                con.putImage(url, im);
            } else {
                im = con.getImage(url, this);
            }
        }

        // the ImageLoader is synchronized so images with the
        // same url will not be loaded at the same time
        if (im != null) {
            return im.loadImage();
        }
        return null;
    }

    public void releaseImage(String url, FOUserAgent context) {
        Context con = (Context) contextStore.get(context);
        if (con != null) {
            if (collective) {
                ImageLoader im = con.getImage(url);
                refStore.put(url, wrapInReference(im, url));
            }
            con.releaseImage(url);
        }
    }

    public void invalidateImage(String url, FOUserAgent context) {
        if (collective) {
            // cap size of invalid list
            if (invalid.size() > 100) {
                invalid.clear();
            }
            invalid.add(url);
        }
        Context con = (Context) contextStore.get(context);
        if (con != null) {
            con.invalidateImage(url);
        }
    }

    private Reference wrapInReference(Object obj, Object key) {
        return new SoftReferenceWithKey(obj, key, refQueue);
    }

    private static class SoftReferenceWithKey extends SoftReference {

        private Object key;

        public SoftReferenceWithKey(Object referent, Object key, ReferenceQueue q) {
            super(referent, q);
            this.key = key;
        }
    }

    public void removeContext(FOUserAgent context) {
        Context con = (Context) contextStore.get(context);
        if (con != null) {
            //mod huangzl.在B/S架构中，为保证正常序列化，renderer结束后，背景图片的AbstractFovImage.inputStream变量赋值为null，所以展示时，不能使用缓存的imageLoader；
            // 由于SoftReference引用，所以B/S架构中，缓存无效；但在单机版中，调用loadBitmap()方法，空指针异常。
//            if (collective) {
            if(false){
                Map images = con.getImages();
                Iterator iter = images.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry entry = (Entry)iter.next();
                    refStore.put(entry.getKey(),
                            wrapInReference(entry.getValue(), entry.getKey()));
                }
            }
            contextStore.remove(context);
        }
        //House-keeping (remove cleared references)
        checkReferenceQueue();
    }

    /**
     * Checks the reference queue if any references have been cleared and removes them from the
     * cache.
     */
    private void checkReferenceQueue() {
        SoftReferenceWithKey ref;
        while ((ref = (SoftReferenceWithKey)refQueue.poll()) != null) {
            refStore.remove(ref.key);
        }
    }

    class Context {
        private Map images = Collections.synchronizedMap(new java.util.HashMap());
        private Set invalid = null;
        private FOUserAgent userAgent;

        public Context(FOUserAgent ua, boolean inv) {
            userAgent = ua;
            if (inv) {
                invalid = Collections.synchronizedSet(new java.util.HashSet());
            }
        }

        public ImageLoader getImage(String url, ImageCache c) {
            if (images.containsKey(url)) {
                return (ImageLoader) images.get(url);
            }
            ImageLoader loader = new ImageLoader(url, c, userAgent);
            images.put(url, loader);
            return loader;
        }

        public void putImage(String url, ImageLoader image) {
            images.put(url, image);
        }

        public ImageLoader getImage(String url) {
            return (ImageLoader) images.get(url);
        }

        public void releaseImage(String url) {
            images.remove(url);
        }

        public Map getImages() {
            return images;
        }

        public void invalidateImage(String url) {
            invalid.add(url);
        }

        public boolean invalid(String url) {
            return invalid.contains(url);
        }

    }

    /** @see com.wisii.fov.image.ImageCache#clearAll() */
    public void clearAll() {
        this.refStore.clear();
        this.invalid.clear();
        //The context-sensitive caches are not cleared so there are no negative side-effects
        //in a multi-threaded environment. Not that it's a good idea to use this method at
        //all except in testing environments. If such a calls is necessary in normal environments
        //we need to check on memory leaks!
    }

}

/**
 * Encapsulates a class of type FovImage by holding its class name.
 * This allows dynamic loading of the class at runtime.
 */
class ImageProvider {

    private String name = null;

//    private String className = null;


    private Class clazz = null;

    /**
     * Creates an ImageProvider with a given name and implementing class.
     * The class name should refer to a class of type {@link FovImage}.
     * However, this is not checked on construction.
     * @param name The name of the provider
     * @param className The full class name of the class implementing this provider
     */
    public ImageProvider(String name, Class clazz) {
        setName(name);
        setClazz(clazz);
    }

    /**
     * Returns the provider name.
     * @return The provider name
     */
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /**
	 * @param clazz 设置clazz成员变量的值
	
	 * 值约束说明
	
	 */
	private void setClazz(Class clazz)
	{
		this.clazz = clazz;
	}

	/**
     * Returns the implementing class as a {@link Class} object.
     * @return The implementing class or null if it couldn't be loaded.
     */
    public Class getImplementingClass() {
        return clazz;
    }
}

/**
 * Holds a mime type for a particular image format plus a list of
 * {@link ImageProvider} objects which support the particular image format.
 */
class ImageMimeType {

    private String mimeType = null;

    private List providers = null;

    /**
     * Constructor for a particular mime type.
     * @param mimeType The mime type
     */
    public ImageMimeType(String mimeType) {
        setMimeType(mimeType);
    }

    /**
     * Returns the mime type.
     * @return The mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    private void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Returns the class from the first available provider.
     * @return The first available class or null if none can be found
     */
    public Class getFirstImplementingClass() {
        if (providers == null) {
            return null;
        }
        for (Iterator it = providers.iterator(); it.hasNext();) {
            ImageProvider ip = (ImageProvider)it.next();
            Class clazz = ip.getImplementingClass();
            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }

    /**
     * Adds a new provider.
     * The provider is added to the end of the current provider list.
     * @param The new provider to add
     */
    public void addProvider(ImageProvider provider) {
        if (providers == null) {
            providers = new ArrayList(4); // Assume we only have a few providers
        }
        if (!providers.contains(provider)) {
            providers.add(provider);
        }
    }
}

