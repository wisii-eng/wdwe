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
 *//* $Id: RGBColorFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import com.wisii.fov.datatypes.PercentBaseContext;
import com.wisii.fov.datatypes.PercentBase;
import com.wisii.fov.fo.properties.ColorProperty;
import com.wisii.fov.fo.properties.Property;

/**
 * Implements the rgb() function.
 */
class RGBColorFunction extends FunctionBase {

    /** @see com.wisii.fov.fo.expr.Function#nbArgs() */
    public int nbArgs() {
    	/* 【修改】 by 李晓光 2009-2-2 原因：由于要设置颜色的层、透明度，而多出来两个参数。 */
        return 5;//3【原始值】;
    }

    /**
     * @return an object which implements the PercentBase interface.
     * Percents in arguments to this function are interpreted relative
     * to 255.
     */
    public PercentBase getPercentBase() {
        return new RGBPercentBase();
    }

    /** @see com.wisii.fov.fo.expr.Function */
    public Property eval(Property[] args,
                         PropertyInfo pInfo) throws PropertyException {
    	/* 【添加：START】 by 李晓光2009-2-2*/
    	StringBuilder s = new StringBuilder("rgb(");
    	int length = args.length;
    	for (int i = 0; i < length; i++) {
			s.append(args[i]);
			if(i != length - 1)
				s.append(",");
		}
    	s.append(")");
    	/* 【添加：END】 by 李晓光2009-2-2*/
    	return new ColorProperty(s.toString());
    	/* 【删除：START】 by 李晓光2009-2-2*/
        /*return new ColorProperty("rgb(" + args[0] + "," + args[1] + "," + args[2] + ")");*/
    	/* 【删除：END】 by 李晓光2009-2-2*/
    }

    static class RGBPercentBase implements PercentBase {
        public int getDimension() {
            return 0;
        }

        public double getBaseValue() {
            return 255f;
        }

        public int getBaseLength(PercentBaseContext context) {
            return 0;
        }

    }
}
