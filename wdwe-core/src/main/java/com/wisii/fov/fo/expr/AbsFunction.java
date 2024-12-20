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
 *//* $Id: AbsFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.properties.Property;

/**
 * Class modelling the abs Number Function. See Sec. 5.10.1 of the XSL-FO spec.
 */
public class AbsFunction extends FunctionBase {

    /**
     * @return 1 (the number of arguments required for the abs function)
     */
    public int nbArgs() {
        return 1;
    }

    /**
     * @param args array of arguments to be evaluated, the first of which
     * should be a numeric value
     * @param propInfo the PropertyInfo object to be evaluated
     * @return the absolute value of the input
     * @throws PropertyException for non-numeric input
     */
    public Property eval(Property[] args,
                         PropertyInfo propInfo) throws PropertyException {
        Numeric num = args[0].getNumeric();
        if (num == null) {
            throw new PropertyException("输入参数为无效数字");
        }
        // TODO: What if it has relative components (percent, table-col units)?
        return (Property) NumericOp.abs(num);
    }

}

