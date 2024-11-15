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
 */package com.wisii.fov.render.ps;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import com.wisii.fov.apps.*;

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
public class PSPrintStream extends PrintStream
{
    private int pagenumber = -1;
    private PSRenderer ps = null;
    public PSPrintStream(OutputStream out, PSRenderer ps)
    {
        super(out);
        this.ps = ps;
    }

    public void write(byte buf[], int off, int len)
    {
        String x = new String(buf, off, len);
        String s = x;
        if(s == null)
        {
            return;
        }
//        int index = s.indexOf("/ImagingBBox null /ManualFeed false  >> setpagedevice");
//        while(index != -1)
//        {
//            try
//            {
//                pagenumber++;
//                String media = ps.getPageViewport(pagenumber).getMediaUsage();
//                String cmd = ps.getPaperEntry(media);
//                s = s.substring(0, index) +
//                    "/ImagingBBox null /ManualFeed false  " + cmd + "  >> setpagedevice" + s.substring(index + 53);
//                index = s.indexOf("/ImagingBBox null /ManualFeed false  >> setpagedevice");
//            }
        //changed by liuxiao 2008 0606
        int index = s.indexOf("/pgSave save def");
        while(index != -1)
        {
            try
            {
                pagenumber++;
                String media = ps.getPageViewport(pagenumber).getMediaUsage();
                String cmd = ps.getPaperEntry(media);
//                cmd="/MediaPosition 2";
                String a ="";
                if(cmd!=null&&!cmd.equalsIgnoreCase(""))
                {
           
                    a="<<" + cmd + "  >> setpagedevice\n";
                  // System.out.println("cmd----------"+cmd);
                }
              
                s = s.substring(0, index)+a+ s.substring(index);
                index = s.indexOf("/pgSave save def",index+100);
            }
            catch(FOVException ex)
            {
                ex.printStackTrace();
                break;
            }

        }
        //        super.print(s);
        super.write(s.getBytes(), 0, s.length());

    }
}
