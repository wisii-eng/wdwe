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
 */package com.wisii.fov.render.print;

import javax.print.attribute.PrintRequestAttribute;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FOPageSelection implements PrintRequestAttribute
{
    public static final FOPageSelection ALL = new FOPageSelection(0);
    public static final FOPageSelection RANGE = new FOPageSelection(1);
    public static final FOPageSelection SELECTION = new FOPageSelection(2);
    private int pages;

    public FOPageSelection(int i)
    {
        pages = i;
    }


    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value when it is added to an attribute
     * set.
     *
     * @return Printing attribute class (category), an instance of class
     *   {@link java.lang.Class java.lang.Class}.
     * @todo Implement this javax.print.attribute.Attribute method
     */
    public Class getCategory()
    {
        return null;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     *
     * @return Attribute category name.
     * @todo Implement this javax.print.attribute.Attribute method
     */
    public String getName()
    {
        return "";
    }
}
