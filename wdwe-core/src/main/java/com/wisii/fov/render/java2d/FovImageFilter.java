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
 */package com.wisii.fov.render.java2d;

/**add by lzy
 * 过滤图片颜色
 * */
import java.awt.image.*;
import java.awt.Color;

public class FovImageFilter extends RGBImageFilter {

    //背景
     private Color need_tranfor_back_color;//需要转换的背景颜色
     private Color tranfored_back_color;//转换后的背景颜色

    //前景
    private int need_tranfor_fore_color;//需要转换的前景颜色
    private Color tranfored_fore_color;//转换后的前景颜色

    private int tranfored_fore_aphla = 0; //转换后的前景颜色的透明度

//    public FovImageFilter() {
//        this(0);
//    }
//
//    public FovImageFilter(int alpha) {
//        canFilterIndexColorModel = true;//true可以接受用 filterRGB 方法的颜色过滤替代逐像素过滤
//        if (alpha < 0 && alpha > 255)
//            throw new IllegalArgumentException("bad alpha");
//
//        this.alpha = alpha;
//    }

    /**
     * back_color 需要转换的背景颜色
     * backed_color 转换后的背景颜色
     * fore_aphla 转换后的前景颜色的透明度
     * */
    public FovImageFilter(Color back_color, Color backed_color , int fore_aphla)
    {
        canFilterIndexColorModel = true;//true可以接受用 filterRGB 方法的颜色过滤替代逐像素过滤
        if ((fore_aphla < 0 && fore_aphla > 255))
            throw new IllegalArgumentException("bad tranfored_fore_aphla 取值范围应该在[0,255]之间");

        need_tranfor_back_color = back_color;
        tranfored_back_color = backed_color;
        tranfored_fore_aphla = fore_aphla;
    }

    public int filterRGB(int x, int y, int rgb) {
        DirectColorModel cm =
                (DirectColorModel) ColorModel.getRGBdefault();

        int alpha = cm.getAlpha(rgb);
        int red = cm.getRed(rgb);
        int green = cm.getGreen(rgb);
        int blue = cm.getBlue(rgb);

//        System.out.println("========================");
//        System.out.println("red : " + red);
//        System.out.println("green : " + green);
//        System.out.println("blue : " + blue);
//        System.out.println("alpha : " + alpha);

        if ((red == need_tranfor_back_color.getRed()
             && green == need_tranfor_back_color.getGreen()
             && blue == need_tranfor_back_color.getBlue()))
        {
            red = tranfored_back_color.getRed();
            green = tranfored_back_color.getGreen();
            blue = tranfored_back_color.getBlue();
            alpha = tranfored_back_color.getAlpha();
//            alpha = alpha == 0 ? 0 : this.alpha;
        }
        else
        {
            alpha = tranfored_fore_aphla;//前景色的透明度
        }
//        alpha = alpha == 0 ? 0 : this.alpha;

        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}

