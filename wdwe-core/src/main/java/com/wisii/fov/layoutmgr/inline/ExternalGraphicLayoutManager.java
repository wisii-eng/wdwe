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
 */package com.wisii.fov.layoutmgr.inline;

//Himport java.awt.geom.Rectangle2D;
import com.wisii.fov.area.Area;
import com.wisii.fov.area.Trait;
import com.wisii.fov.area.inline.Image;
import com.wisii.fov.fo.flow.ExternalGraphic;

/**
 * LayoutManager for the fo:external-graphic formatting object
 */
public class ExternalGraphicLayoutManager extends AbstractGraphicsLayoutManager {

    private ExternalGraphic fobj;

    /**
     * Constructor
     *
     * @param node the fo:external-graphic formatting object that creates the area
     */
    public ExternalGraphicLayoutManager(ExternalGraphic node) {
        super(node);
        fobj = node;
    }

    /**
     * Get the inline area created by this element.
     *
     * @return the inline area
     */
    protected Area getChildArea() {

        String fov_src_type = fobj.getSrc_type();
        /* 【添加：START】 by 李晓光 2009-2-3 */
        Image image = null;
        /* 【添加：END】 by 李晓光 2009-2-3 */
        if("func-by-param".equals(fov_src_type))
        {
            /*return new Image(fobj.getSrc_type(),fobj.getAphla(),fobj.getIamgeByte());*///【删除】by 李晓光 2009-2-3
        	image = new Image(fobj.getSrc_type(),fobj.getAphla(),fobj.getIamgeByte());//【添加】by 李晓光 2009-2-3
        }
        else if("bin-data-str".equals(fov_src_type))//src_type = "bin-data-str"
        {
            //扩展接口，节点内容为图片信息二进制值的某种封装形式的字符串
            return null;
        }
        else
        {
            /*return new Image(fobj.getSrc() , fobj.getAphla());*///【删除】by 李晓光 2009-2-3
        	image = new Image(fobj.getSrc() , fobj.getAphla());//【添加】by 李晓光 2009-2-3
        }
        /* 【添加：START】 by 李晓光 2009-2-3 */
        image.addTrait(Trait.IMAGE_LAYER, new Integer(fobj.getLayer()));
        return image;
        /* 【添加：END】 by 李晓光 2009-2-3 */
//        return new Image(fobj.getSrc());
    }
}

