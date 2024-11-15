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

import java.awt.print.PrinterJob;
import java.awt.AWTError;

public abstract class FOPrinterJob extends PrinterJob
{

    public static PrinterJob getPrinterJob() {
		SecurityManager security = System.getSecurityManager();

		try
		{
//			Class.forName("com.wisii.fov.render.print.FOWPrinterJob");

            if (security != null)
            {
                security.checkPermission(new RuntimePermission("com.wisii.fov.render.print.FOWPrinterJob"));
            }
		}
		catch(Exception ex)
		{
			return PrinterJob.getPrinterJob();//调用系统PrinterJob的getPrinterJob()方法
		}


        if (security != null) {
            security.checkPrintJobAccess();
        }
        return (PrinterJob) java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
            public Object run() {
                String nm = "com.wisii.fov.render.print.FOWPrinterJob";
                try {
                    return com.wisii.fov.render.print.FOWPrinterJob.class.newInstance();
                } catch (InstantiationException e) {
                 throw new AWTError("Could not instantiate PrinterJob: " + nm);
                } catch (IllegalAccessException e) {
                    throw new AWTError("Could not access PrinterJob: " + nm);
                }catch (Exception e)
				{
					return PrinterJob.getPrinterJob();
				}
            }
        });
    }

}
