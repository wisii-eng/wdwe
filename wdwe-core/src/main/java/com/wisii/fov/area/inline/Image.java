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
 *//* $Id: Image.java,v 1.2 2008/01/08 07:26:56 lzy Exp $ */

package com.wisii.fov.area.inline;

import com.wisii.fov.area.Area;

/**
 * Image area for external-graphic.
 * This area holds information for rendering an image.
 * The url of the image is used as a key to reference the image cache.
 */
public class Image extends Area {
    private String url = "";

    /**
     * add by lzy
     * fo:external-graphic节点新增属性
     * Aphla
     图片的透明度[0,255]
     * Src_type
     图片数据来源类型
     */
    private int Aphla = 0;
    private String Src_type = "";
    private byte[] imagebyte;
    //add end


    /**
     * Create a new image with the given url.
     *
     * @param u the url of the image
     */
    public Image(String u) {
        url = u;
    }

    public Image(String u , int aphla) {
       url = u;
       Aphla = aphla;
   }

    public Image(String src_type,int aphla ,byte[] b)
    {
        Aphla = aphla;
        Src_type = src_type;
        imagebyte = b;
    }

    /**
     * Get the url of this image.
     * This url is used as a key to locate the actual image data.
     *
     * @return the url of this image
     */
    public String getURL() {
        return url;
    }

    public int getAphla() {
        return Aphla;
    }

    public String  getSrc_type()
    {
        return Src_type;
    }

    public byte[] getImagebyte()
    {
        return imagebyte;
    }
}

