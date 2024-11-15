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
 *//* $Id: ImageLoader.java,v 1.6 2007/09/10 02:14:24 hzl Exp $ */

package com.wisii.fov.image;

import java.io.File;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.fov.apps.FOUserAgent;

/**
 * Class to load images.
 */
class ImageLoader {

    private String url;
    private ImageCache cache;
    private boolean valid = true;
    private FOUserAgent userAgent;
    private FovImage image = null;

    /**
     * Main constructor.
     * @param url URL to the image
     * @param cache Image cache
     * @param ua User agent
     */
    public ImageLoader(String url, ImageCache cache, FOUserAgent ua) {
        if(url.startsWith(SystemUtil.HTTPSCHEME) || url.startsWith(SystemUtil.FILESCHEME))
        {// 绝对路径
            this.url = url;
        }
        else
        {// 相对base url的路径，并且图片必须放到base url路径下的graphics目录中
            this.url = SystemUtil.GRAPHICSRELATIVEPATH + url;
        }
        this.cache = cache;
        this.userAgent = ua;
    }

    /**
     * Loads the image.
     * @return the loaded image
     */
    public synchronized FovImage loadImage() {
        if (!valid || image != null) {
            return image;
        }
        ImageFactory imageFactory = userAgent.getImageFactory();
        image = imageFactory.loadImage(url, userAgent);
        if (image == null) {
            cache.invalidateImage(url, userAgent);
            valid = false;
        }
        return image;
    }

}
