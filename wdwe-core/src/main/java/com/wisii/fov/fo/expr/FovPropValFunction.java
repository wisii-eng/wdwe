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
 *//* $Id: FovPropValFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.fo.FOPropertyMapping;
import com.wisii.fov.fo.properties.Property;


/**
 * This appears to be an artificial function, which handles the specified
 * or initial value of the property on this object.
 */
public class FovPropValFunction extends FunctionBase {

    /**
     * @return 1 (the maximum number of arguments)
     */
    public int nbArgs() {
        return 1;
    }

    /**
     *
     * @param args array of arguments, which should either be empty, or the
     * first of which should be an NCName corresponding to a property name
     * @param pInfo PropertyInfo object to be evaluated
     * @return the Property corresponding to the input
     * @throws PropertyException for incorrect parameters
     */
    public Property eval(Property[] args,
                         PropertyInfo pInfo) throws PropertyException {
        String propName = args[0].getString();
        if (propName == null) {
            throw new PropertyException("不正确的参数输入");
        }

        int propId = FOPropertyMapping.getPropertyId(propName);
        return pInfo.getPropertyList().get(propId);
    }

}
