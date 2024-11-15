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
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintRequestAttribute;

public final class FODialogTypeSelection extends EnumSyntax
    implements PrintRequestAttribute
{

    public static final FODialogTypeSelection NATIVE;
    public static final FODialogTypeSelection COMMON;
    private static final String myStringTable[] = {
        "native", "common"
    };
    private static final FODialogTypeSelection myEnumValueTable[];

    protected FODialogTypeSelection(int i)
    {
        super(i);
    }

    protected String[] getStringTable()
    {
        return myStringTable;
    }

    protected EnumSyntax[] getEnumValueTable()
    {
        return myEnumValueTable;
    }

    public final Class getCategory()
    {
        return null;
    }

    public final String getName()
    {
        return "dialog-type-selection";
    }

    static
    {
        NATIVE = new FODialogTypeSelection(0);
        COMMON = new FODialogTypeSelection(1);
        myEnumValueTable = (new FODialogTypeSelection[] {
            NATIVE, COMMON
        });
    }
}
