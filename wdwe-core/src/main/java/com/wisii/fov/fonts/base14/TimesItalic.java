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
 */
package com.wisii.fov.fonts.base14;


import java.util.Map;

import com.wisii.fov.fonts.FontType;
import com.wisii.fov.fonts.Typeface;
import com.wisii.fov.fonts.CodePointMapping;

public class TimesItalic extends Typeface {
    private final static String fontName = "Times-Italic";
    private final static String encoding = "WinAnsiEncoding";
    private final static int capHeight = 653;
    private final static int xHeight = 441;
    private final static int ascender = 683;
    private final static int descender = -205;
    private final static int firstChar = 32;
    private final static int lastChar = 255;
    private final static int[] width;
    private final CodePointMapping mapping =
        CodePointMapping.getMapping("WinAnsiEncoding");

    private final static Map kerning;


    private boolean enableKerning = false;

    static {
        width = new int[256];
        
              width[0x41] = 611;
              width[0xc6] = 889;
              width[0xc1] = 611;
              width[0xc2] = 611;
              width[0xc4] = 611;
              width[0xc0] = 611;
              width[0xc5] = 611;
              width[0xc3] = 611;
              width[0x42] = 611;
              width[0x43] = 667;
              width[0xc7] = 667;
              width[0x44] = 722;
              width[0x45] = 611;
              width[0xc9] = 611;
              width[0xca] = 611;
              width[0xcb] = 611;
              width[0xc8] = 611;
              width[0xd0] = 722;
              width[0x80] = 500;
              width[0x46] = 611;
              width[0x47] = 722;
              width[0x48] = 722;
              width[0x49] = 333;
              width[0xcd] = 333;
              width[0xce] = 333;
              width[0xcf] = 333;
              width[0xcc] = 333;
              width[0x4a] = 444;
              width[0x4b] = 667;
              width[0x4c] = 556;
      
              width[0x4d] = 833;
              width[0x4e] = 667;
              width[0xd1] = 667;
              width[0x4f] = 722;
              width[0x8c] = 944;
              width[0xd3] = 722;
              width[0xd4] = 722;
              width[0xd6] = 722;
              width[0xd2] = 722;
              width[0xd8] = 722;
              width[0xd5] = 722;
              width[0x50] = 611;
              width[0x51] = 722;
              width[0x52] = 611;
              width[0x53] = 500;
              width[0x8a] = 500;
              width[0x54] = 556;
              width[0xde] = 611;
              width[0x55] = 722;
              width[0xda] = 722;
              width[0xdb] = 722;
              width[0xdc] = 722;
              width[0xd9] = 722;
              width[0x56] = 611;
              width[0x57] = 833;
              width[0x58] = 611;
              width[0x59] = 556;
              width[0xdd] = 556;
              width[0x9f] = 556;
              width[0x5a] = 556;
              width[0x8e] = 556;
              width[0x61] = 500;
              width[0xe1] = 500;
              width[0xe2] = 500;
              width[0xb4] = 333;
              width[0xe4] = 500;
              width[0xe6] = 667;
              width[0xe0] = 500;
              width[0x26] = 778;
              width[0xe5] = 500;
              width[0x5e] = 422;
              width[0x7e] = 541;
              width[0x2a] = 500;
              width[0x40] = 920;
              width[0xe3] = 500;
              width[0x62] = 500;
              width[0x5c] = 278;
              width[0x7c] = 275;
              width[0x7b] = 400;
              width[0x7d] = 400;
              width[0x5b] = 389;
              width[0x5d] = 389;
      
              width[0xa6] = 275;
              width[0x95] = 350;
              width[0x63] = 444;
      
              width[0xe7] = 444;
              width[0xb8] = 333;
              width[0xa2] = 500;
              width[0x88] = 333;
              width[0x3a] = 333;
              width[0x2c] = 250;
              width[0xa9] = 760;
              width[0xa4] = 500;
              width[0x64] = 500;
              width[0x86] = 500;
              width[0x87] = 500;
              width[0xb0] = 400;
              width[0xa8] = 333;
              width[0xf7] = 675;
              width[0x24] = 500;
      
      
              width[0x65] = 444;
              width[0xe9] = 444;
              width[0xea] = 444;
              width[0xeb] = 444;
              width[0xe8] = 444;
              width[0x38] = 500;
              width[0x85] = 889;
              width[0x97] = 889;
              width[0x96] = 500;
              width[0x3d] = 675;
              width[0xf0] = 500;
              width[0x21] = 333;
              width[0xa1] = 389;
              width[0x66] = 278;
      
              width[0x35] = 500;
      
              width[0x83] = 500;
              width[0x34] = 500;
      
              width[0x67] = 500;
              width[0xdf] = 500;
              width[0x60] = 333;
              width[0x3e] = 675;
              width[0xab] = 500;
              width[0xbb] = 500;
              width[0x8b] = 333;
              width[0x9b] = 333;
              width[0x68] = 500;
      
              width[0x2d] = 333;
              width[0x69] = 278;
              width[0xed] = 278;
              width[0xee] = 278;
              width[0xef] = 278;
              width[0xec] = 278;
              width[0x6a] = 278;
              width[0x6b] = 444;
              width[0x6c] = 278;
              width[0x3c] = 675;
              width[0xac] = 675;
      
              width[0x6d] = 722;
              width[0xaf] = 333;
      
              width[0xb5] = 500;
              width[0xd7] = 675;
              width[0x6e] = 500;
              width[0x39] = 500;
              width[0xf1] = 500;
              width[0x23] = 500;
              width[0x6f] = 500;
              width[0xf3] = 500;
              width[0xf4] = 500;
              width[0xf6] = 500;
              width[0x9c] = 667;
      
              width[0xf2] = 500;
              width[0x31] = 500;
              width[0xbd] = 750;
              width[0xbc] = 750;
              width[0xb9] = 300;
              width[0xaa] = 276;
              width[0xba] = 310;
              width[0xf8] = 500;
              width[0xf5] = 500;
              width[0x70] = 500;
              width[0xb6] = 523;
              width[0x28] = 333;
              width[0x29] = 333;
              width[0x25] = 833;
              width[0x2e] = 250;
              width[0xb7] = 250;
              width[0x89] = 1000;
              width[0x2b] = 675;
              width[0xb1] = 675;
              width[0x71] = 500;
              width[0x3f] = 500;
              width[0xbf] = 500;
              width[0x22] = 420;
              width[0x84] = 556;
              width[0x93] = 556;
              width[0x94] = 556;
              width[0x91] = 333;
              width[0x92] = 333;
              width[0x82] = 333;
              width[0x27] = 214;
              width[0x72] = 389;
              width[0xae] = 760;
      
              width[0x73] = 389;
              width[0x9a] = 389;
              width[0xa7] = 500;
              width[0x3b] = 333;
              width[0x37] = 500;
              width[0x36] = 500;
              width[0x2f] = 278;
              width[0x20] = 250;
      
      
              width[0xa3] = 500;
              width[0x74] = 278;
              width[0xfe] = 500;
              width[0x33] = 500;
              width[0xbe] = 750;
              width[0xb3] = 300;
              width[0x98] = 333;
              width[0x99] = 980;
              width[0x32] = 500;
              width[0xb2] = 300;
              width[0x75] = 500;
              width[0xfa] = 500;
              width[0xfb] = 500;
              width[0xfc] = 500;
              width[0xf9] = 500;
              width[0x5f] = 500;
              width[0x76] = 444;
              width[0x77] = 667;
              width[0x78] = 444;
              width[0x79] = 444;
              width[0xfd] = 444;
              width[0xff] = 444;
              width[0xa5] = 500;
              width[0x7a] = 389;
              width[0x9e] = 389;
              width[0x30] = 500;
   
        kerning = new java.util.HashMap();
        Integer first, second;
        Map pairs;
        
        first = new Integer(79);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(84);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(88);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(107);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(112);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        first = new Integer(80);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-80));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-80));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-90));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-135));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-80));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-135));
  
        first = new Integer(86);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(79);
        pairs.put(second, new Integer(-30));
  
        second = new Integer(58);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(71);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-129));
  
        second = new Integer(59);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(45);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-60));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-129));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-111));
  
        first = new Integer(118);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(97);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-74));
  
        first = new Integer(32);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(147);
        pairs.put(second, new Integer(0));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-75));
  
        second = new Integer(84);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(145);
        pairs.put(second, new Integer(0));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-35));
  
        first = new Integer(97);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(119);
        pairs.put(second, new Integer(0));
  
        second = new Integer(116);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(112);
        pairs.put(second, new Integer(0));
  
        second = new Integer(103);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(98);
        pairs.put(second, new Integer(0));
  
        second = new Integer(118);
        pairs.put(second, new Integer(0));
  
        first = new Integer(70);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-105));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-45));
  
        second = new Integer(114);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-75));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-115));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-135));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-75));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-135));
  
        first = new Integer(85);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-25));
  
        first = new Integer(100);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(100);
        pairs.put(second, new Integer(0));
  
        second = new Integer(119);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(118);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(83);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(122);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        first = new Integer(68);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-35));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(146);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(148);
        pairs.put(second, new Integer(0));
  
        second = new Integer(100);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(32);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(146);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(114);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(116);
        pairs.put(second, new Integer(-30));
  
        second = new Integer(108);
        pairs.put(second, new Integer(0));
  
        second = new Integer(115);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(118);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(58);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(32);
        pairs.put(second, new Integer(0));
  
        first = new Integer(119);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(97);
        pairs.put(second, new Integer(0));
  
        second = new Integer(104);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-74));
  
        first = new Integer(75);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(79);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-35));
  
        first = new Integer(82);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(79);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(85);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(84);
        pairs.put(second, new Integer(0));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-18));
  
        first = new Integer(145);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(0));
  
        second = new Integer(145);
        pairs.put(second, new Integer(-111));
  
        first = new Integer(103);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(105);
        pairs.put(second, new Integer(0));
  
        second = new Integer(114);
        pairs.put(second, new Integer(0));
  
        second = new Integer(97);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(103);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(66);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(85);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(98);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(117);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(108);
        pairs.put(second, new Integer(0));
  
        second = new Integer(98);
        pairs.put(second, new Integer(0));
  
        second = new Integer(118);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(81);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(85);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(44);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(148);
        pairs.put(second, new Integer(-140));
  
        second = new Integer(32);
        pairs.put(second, new Integer(0));
  
        second = new Integer(146);
        pairs.put(second, new Integer(-140));
  
        first = new Integer(102);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(148);
        pairs.put(second, new Integer(0));
  
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(146);
        pairs.put(second, new Integer(92));
  
        second = new Integer(97);
        pairs.put(second, new Integer(0));
  
        second = new Integer(102);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(108);
        pairs.put(second, new Integer(0));
  
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(84);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(79);
        pairs.put(second, new Integer(-18));
  
        second = new Integer(119);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(58);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(114);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(104);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(59);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(45);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-92));
  
        first = new Integer(121);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(0));
  
        second = new Integer(97);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-55));
  
        first = new Integer(120);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(101);
        pairs.put(second, new Integer(0));
  
        first = new Integer(101);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(119);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-30));
  
        second = new Integer(112);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(103);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(98);
        pairs.put(second, new Integer(0));
  
        second = new Integer(120);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(118);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(99);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(107);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(104);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(108);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(87);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(79);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(58);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(104);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(59);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(45);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-60));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-70));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-92));
  
        first = new Integer(104);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        first = new Integer(71);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(105);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(118);
        pairs.put(second, new Integer(0));
  
        first = new Integer(65);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(79);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(146);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(119);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-95));
  
        second = new Integer(67);
        pairs.put(second, new Integer(-30));
  
        second = new Integer(112);
        pairs.put(second, new Integer(0));
  
        second = new Integer(81);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(71);
        pairs.put(second, new Integer(-35));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-105));
  
        second = new Integer(118);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(148);
        pairs.put(second, new Integer(0));
  
        second = new Integer(85);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(84);
        pairs.put(second, new Integer(-37));
  
        first = new Integer(147);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(0));
  
        second = new Integer(145);
        pairs.put(second, new Integer(0));
  
        first = new Integer(78);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(65);
        pairs.put(second, new Integer(-27));
  
        second = new Integer(46);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(0));
  
        first = new Integer(115);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(119);
        pairs.put(second, new Integer(0));
  
        first = new Integer(111);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(119);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(103);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(120);
        pairs.put(second, new Integer(0));
  
        second = new Integer(118);
        pairs.put(second, new Integer(-10));
  
        first = new Integer(114);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-45));
  
        second = new Integer(100);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(107);
        pairs.put(second, new Integer(0));
  
        second = new Integer(114);
        pairs.put(second, new Integer(0));
  
        second = new Integer(99);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(112);
        pairs.put(second, new Integer(0));
  
        second = new Integer(103);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(108);
        pairs.put(second, new Integer(0));
  
        second = new Integer(113);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(118);
        pairs.put(second, new Integer(0));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(45);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(105);
        pairs.put(second, new Integer(0));
  
        second = new Integer(109);
        pairs.put(second, new Integer(0));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(117);
        pairs.put(second, new Integer(0));
  
        second = new Integer(116);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-111));
  
        second = new Integer(110);
        pairs.put(second, new Integer(0));
  
        second = new Integer(115);
        pairs.put(second, new Integer(-10));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-37));
  
        first = new Integer(108);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(119);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        first = new Integer(76);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(148);
        pairs.put(second, new Integer(0));
  
        second = new Integer(146);
        pairs.put(second, new Integer(-37));
  
        second = new Integer(87);
        pairs.put(second, new Integer(-55));
  
        second = new Integer(89);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(121);
        pairs.put(second, new Integer(-30));
  
        second = new Integer(84);
        pairs.put(second, new Integer(-20));
  
        second = new Integer(86);
        pairs.put(second, new Integer(-55));
  
        first = new Integer(148);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(32);
        pairs.put(second, new Integer(0));
  
        first = new Integer(109);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(117);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        first = new Integer(89);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(45);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(105);
        pairs.put(second, new Integer(-74));
  
        second = new Integer(79);
        pairs.put(second, new Integer(-15));
  
        second = new Integer(58);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-50));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-92));
  
        second = new Integer(59);
        pairs.put(second, new Integer(-65));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-92));
  
        first = new Integer(74);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(111);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(97);
        pairs.put(second, new Integer(-35));
  
        second = new Integer(65);
        pairs.put(second, new Integer(-40));
  
        second = new Integer(117);
        pairs.put(second, new Integer(-35));
  
        second = new Integer(46);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(101);
        pairs.put(second, new Integer(-25));
  
        second = new Integer(44);
        pairs.put(second, new Integer(-25));
  
        first = new Integer(46);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(148);
        pairs.put(second, new Integer(-140));
  
        second = new Integer(146);
        pairs.put(second, new Integer(-140));
  
        first = new Integer(110);
        pairs = (Map)kerning.get(first);
        if (pairs == null) {
            pairs = new java.util.HashMap();
            kerning.put(first, pairs);
        }
        
        second = new Integer(117);
        pairs.put(second, new Integer(0));
  
        second = new Integer(121);
        pairs.put(second, new Integer(0));
  
        second = new Integer(118);
        pairs.put(second, new Integer(-40));
  
    }

    public TimesItalic() {
        this(false);
    }

    public TimesItalic(boolean enableKerning) {
        this.enableKerning = enableKerning;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getFontName() {
        return fontName;
    }

    public FontType getFontType() {
        return FontType.TYPE1;
    }

    public int getAscender(int size) {
        return size * ascender;
    }

    public int getCapHeight(int size) {
        return size * capHeight;
    }

    public int getDescender(int size) {
        return size * descender;
    }

    public int getXHeight(int size) {
        return size * xHeight;
    }

    public int getFirstChar() {
        return firstChar;
    }

    public int getLastChar() {
        return lastChar;
    }

    public int getWidth(int i,int size) {
        return size * width[i];
    }

    public int[] getWidths() {
        int[] arr = new int[getLastChar() - getFirstChar() + 1];
        System.arraycopy(width, getFirstChar(), arr, 0, getLastChar() - getFirstChar() + 1);
        return arr;
    }


    public boolean hasKerningInfo() {
        return enableKerning;
    }

    public java.util.Map getKerningInfo() {
        return kerning;
    }
  

    public char mapChar(char c) {
        char d = mapping.mapChar(c);
        if(d != 0) {
            return d;
        } else {
            return '#';
        }
    }

    public boolean hasChar(char c) {
        return (mapping.mapChar(c) > 0);
    }

}
  