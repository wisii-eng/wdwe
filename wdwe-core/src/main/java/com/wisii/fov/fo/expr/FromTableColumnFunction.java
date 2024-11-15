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
 *//* $Id: FromTableColumnFunction.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.fo.expr;

import java.util.List;
import com.wisii.fov.fo.Constants;
import com.wisii.fov.fo.FObj;
import com.wisii.fov.fo.FOPropertyMapping;
import com.wisii.fov.fo.flow.Table;
import com.wisii.fov.fo.flow.TableCell;
import com.wisii.fov.fo.flow.TableColumn;
import com.wisii.fov.fo.flow.TableFObj;
import com.wisii.fov.fo.properties.Property;

/**
 * Class modelling the from-table-column Property Value function. See Sec.
 * 5.10.4 of the XSL-FO spec.
 */
public class FromTableColumnFunction extends FunctionBase {

    /**
     * @return 1 (maximum arguments for the from-table-column function)
     */
    public int nbArgs() {
        return 1;
    }

    /**
     * @return true (allow padding of arglist with property name)
     */
    public boolean padArgsWithPropertyName() {
        return true;
    }

    /**
     *
     * @param args array of arguments, which should either be empty, or the
     * first of which should contain an NCName corresponding to a property name
     * @param pInfo PropertyInfo object to be evaluated
     * @return the Property corresponding to the property name specified, or, if
     * none, for the property for which the expression is being evaluated
     * @throws PropertyException for incorrect arguments, and (for now) in all
     * cases, because this function is not implemented
     */
    public Property eval(Property[] args,
                         PropertyInfo pInfo) throws PropertyException {

        FObj fo = pInfo.getPropertyList().getFObj();

        /* obtain property Id for the property for which the function is being
         * evaluated */
        int propId = 0;
        if (args.length == 0) {
            propId = pInfo.getPropertyMaker().getPropId();
        } else {
            String propName = args[0].getString();
            propId = FOPropertyMapping.getPropertyId(propName);
        }

        /* make sure we have a correct property id ... */
        if (propId != -1) {
            /* obtain column number for which the function is being evaluated: */
            int columnNumber = -1;
            int span = 0;
            if (fo.getNameId() != Constants.FO_TABLE_CELL) {
                // climb up to the nearest cell
                do {
                    fo = (FObj) fo.getParent();
                } while (fo.getNameId() != Constants.FO_TABLE_CELL
                          && fo.getNameId() != Constants.FO_PAGE_SEQUENCE);
                if (fo.getNameId() == Constants.FO_TABLE_CELL) {
                    //column-number is available on the cell
                    columnNumber = ((TableCell) fo).getColumnNumber();
                    span = ((TableCell) fo).getNumberColumnsSpanned();
                } else {
                    //means no table-cell was found...
                    throw new PropertyException("from-table-column() 仅仅只能在fo:table-cell或它的子元素中使用 ");
                }
            } else {
                //column-number is only accurately available through the propertyList
                columnNumber = pInfo.getPropertyList().get(Constants.PR_COLUMN_NUMBER)
                                    .getNumeric().getValue();
                span = pInfo.getPropertyList().get(Constants.PR_NUMBER_COLUMNS_SPANNED)
                                    .getNumeric().getValue();
            }

            /* return the property from the column */
            Table t = ((TableFObj) fo).getTable();
            List cols = t.getColumns();
            if (cols == null) {
                //no columns defined => no match: return default value
                return pInfo.getPropertyList().get(propId, false, true);
            } else {
                if (t.isColumnNumberUsed(columnNumber)) {
                    //easiest case: exact match
                    return ((TableColumn) cols.get(columnNumber - 1)).getProperty(propId);
                } else {
                    //no exact match: try all spans...
                    while (--span > 0 && !t.isColumnNumberUsed(++columnNumber)) {
                        //nop: just increment/decrement
                    }
                    if (t.isColumnNumberUsed(columnNumber)) {
                        return ((TableColumn) cols.get(columnNumber - 1)).getProperty(propId);
                    } else {
                        //no match: return default value
                        return pInfo.getPropertyList().get(propId, false, true);
                    }
                }
            }
        } else {
            throw new PropertyException("不正确的参数输入");
        }
    }

}
