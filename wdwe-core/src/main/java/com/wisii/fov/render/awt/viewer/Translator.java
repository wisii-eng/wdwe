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
 *//* $Id: Translator.java,v 1.2 2007/04/29 02:17:59 csy Exp $ */

package com.wisii.fov.render.awt.viewer;

//Java
import java.util.ResourceBundle;
import java.util.Locale;
import java.io.Serializable;

/**
 * AWT Viewer's localization class, backed up by <code>java.util.ResourceBundle</code>.
 * Originally contributed by:
 * Stanislav.Gorkhover@jCatalog.com
 */
public class Translator implements Serializable{

    private ResourceBundle bundle;
    private static String bundleBaseName = "com/wisii/fov/render/awt/viewer/resources/Viewer";
    private static Translator translator;
    public static Translator getInstanceof()
    {
        if(translator==null)
        {
        	translator=new Translator(Locale.getDefault());
        }
        return translator;
        
    }

    /**
     * Constructor for a given <code>Locale</code>.
     * @param locale Locale to use
     */
    private Translator(Locale locale) {
        bundle = ResourceBundle.getBundle(bundleBaseName, locale);
    }

    /**
     * Returns localized <code>String</code> for a given key.
     * @param key the key
     * @return the localized String
     */
    public String getString(String key) {
        return bundle.getString(key);
    }
}

