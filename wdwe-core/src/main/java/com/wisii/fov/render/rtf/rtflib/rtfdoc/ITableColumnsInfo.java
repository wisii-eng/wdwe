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
 *//*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.wisii.com/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: ITableColumnsInfo.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.rtf.rtflib.rtfdoc;

/*
 * This file is part of the RTF library of the FOV project, which was originally
 * created by Bertrand Delacretaz <bdelacretaz@codeconsult.ch> and by other
 * contributors to the jfor project (www.jfor.org), who agreed to donate jfor to
 * the FOV project.
 */

/**  Used to get information about tables, for example when handling nested tables
 *  @author Bertrand Delacretaz bdelacretaz@codeconsult.ch
 */

public interface ITableColumnsInfo {
    /** value for invalid column width */
    float INVALID_COLUMN_WIDTH = 200f;

    /** reset the column iteration index, meant to be called when creating a new row */
    void selectFirstColumn();

    /** increment the column iteration index */
    void selectNextColumn();

    /** get current column width according to column iteration index
     *  @return INVALID_COLUMN_WIDTH if we cannot find the value
     */
    float getColumnWidth();

    /** @return current column iteration index */
    int getColumnIndex();

    /** @return number of columns */
    int getNumberOfColumns();
    
    /**
     * 
     * @return true, if it's the first of multiple spanning columns
     */
    boolean getFirstSpanningCol();
}