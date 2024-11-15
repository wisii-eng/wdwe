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

/* $Id: ImageRasterData.java 426576 2006-07-28 15:44:37Z jeremias $ */

package com.wisii.fov.render.afp.modca;

import java.io.IOException;
import java.io.OutputStream;
import com.wisii.fov.render.afp.tools.BinaryUtils;

/**
 * Contains the image points that define the IM image raster pattern.
 *
 * A raster pattern is the array of presentation device pels that forms
 * the image. The image data is uncompressed. Bits are grouped into
 * bytes and are ordered from left to right within each byte. Each bit
 * in the image data represents an image point and is mapped to
 * presentation device pels as specified in the IOC structured field.
 * A bit with value B'1' indicates a significant image point; a bit
 * with value B'0' indicates an insignificant image point.
 * Image points are recorded from left to right in rows that represents
 * scan lines (X direction), and rows representing scan lines are
 * recorded from top to bottom (Y direction). When the image is
 * presented, all image points in a row are presented before any
 * image points in the next sequential row are presented, and all rows
 * have the same number of image points. If the total number of image
 * points is not a multiple of 8, the last byte of the image data is
 * padded to a byte boundary. The padding bits do not represent image
 * points and are ignored by presentation devices.
 */
public class ImageRasterData extends AbstractAFPObject {

    /**
     * The image raster data
     */
    private byte[] _rasterdata;

    /**
     * Constructor for the image raster data object
     * @param rasterdata The raster image data
     */
    public ImageRasterData(byte[] rasterdata) {

        _rasterdata = rasterdata;

    }

    /**
     * Accessor method to write the AFP datastream for the Image Raster Data
     * @param os The stream to write to
     * @throws java.io.IOException
     */
    public void writeDataStream(OutputStream os)
        throws IOException {

        byte[] data = new byte[9];

        data[0] = 0x5A;

        // The size of the structured field
        byte[] x = BinaryUtils.convert(_rasterdata.length + 8, 2);
        data[1] = x[0];
        data[2] = x[1];

        data[3] = (byte) 0xD3;
        data[4] = (byte) 0xEE;
        data[5] = (byte) 0x7B;
        data[6] = 0x00;
        data[7] = 0x00;
        data[8] = 0x00;

        os.write(data);
        os.write(_rasterdata);

    }

}