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
 *//**
 * @EPSData.java
 * 北京汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.image.io;

/**
 * 类功能描述：
 *
 * 作者：zhangqiang
 * 创建日期：2012-2-24
 */
public  class EPSData {
    public long[] bbox;
    public boolean isAscii; // True if plain ascii eps file

    // offsets if not ascii
    public long psStart = 0;
    public long psLength = 0;
    public long wmfStart = 0;
    public long wmfLength = 0;
    public long tiffStart = 0;
    public long tiffLength = 0;

    /** raw eps file */
    public byte[] rawEps;
    /** eps part */
    public byte[] epsFile;
    public byte[] preview = null;
}
