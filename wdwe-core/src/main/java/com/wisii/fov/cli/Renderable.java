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
 */package com.wisii.fov.cli;

import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;

/**
 * The interface is used by the AWT preview dialog to reload a document.
 */
public interface Renderable
{
    /**
     * Renders the pre-setup document.
     * @param userAgent the user agent
     * @param outputFormat the output format to generate (MIME type, see MimeConstants)
     * @exception FOVException if the FO processing fails
     */
    void renderTo(FOUserAgent userAgent, String outputFormat) throws FOVException;

    /**
         * Rerenders the pre-setup document.
         * @param userAgent the user agent
         * @param outputFormat the output format to generate (MIME type, see MimeConstants)
         * @param stylesheet 模板文件（文件的路径或者文件的字符串）
         * @param isStylesheetDir true:stylesheet参数值是文件的路径; false:stylesheet参数值是文件的字符串
         * @param source xml数据文件（文件的路径或者文件的字符串）
         * @param issourceDir true:source参数值是文件的路径; false:source参数值是文件的字符串
         * @exception FOVException if the FO processing fails
     */
    public void renderNew(FOUserAgent userAgent, String outputFormat, String stylesheet,  boolean isStylesheetDir,
                          String source, boolean issourceDir, String background) throws FOVException;
}
