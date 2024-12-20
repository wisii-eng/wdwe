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

/* $Id: PDFColorSpace.java 426576 2006-07-28 15:44:37Z jeremias $ */
 
package com.wisii.fov.pdf;

/**
 * PDF Color space.
 */
public interface PDFColorSpace {
    
    /**
     * Get the number of color components for this colorspace
     * @return the number of components
     */
    int getNumComponents();

    /** @return the name of the color space */
    String getName();
    
    /**
     * @return true if the color space is a device-dependent color space (like DeviceRGB, 
     *         DeviceCMYK and DeviceGray)
     */
    boolean isDeviceColorSpace();
    
    /** @return true if the color space is an RGB color space */
    boolean isRGBColorSpace();
    /** @return true if the color space is an CMYK color space */
    boolean isCMYKColorSpace();
    /** @return true if the color space is an Gray color space */
    boolean isGrayColorSpace();
    
}
