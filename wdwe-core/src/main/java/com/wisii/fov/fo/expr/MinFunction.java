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
 *//* $Id: MinFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.datatypes.Numeric;
import com.wisii.fov.fo.properties.Property;

/**
 * Class for managing the "min" Number Function. See Sec. 5.10.1 in the XSL-FO
 * standard.
 */
public class MinFunction extends FunctionBase {

    /**
     * @return 2 (the number of arguments required for the min function)
     */
    public int nbArgs() {
        return 2;
    }

    /**
     * Handle "numerics" if no proportional/percent parts
     * @param args array of arguments to be processed
     * @param pInfo PropertyInfo to be processed
     * @return the minimum of the two args elements passed
     * @throws PropertyException for invalid operands
     */
    public Property eval(Property[] args,
                         PropertyInfo pInfo) throws PropertyException {
        Numeric n1 = args[0].getNumeric();
        Numeric n2 = args[1].getNumeric();
        if (n1 == null || n2 == null) {
			throw new PropertyException("min方法的输入参数为无效数字");
        }
        return (Property) NumericOp.min(n1, n2);
       }

}

