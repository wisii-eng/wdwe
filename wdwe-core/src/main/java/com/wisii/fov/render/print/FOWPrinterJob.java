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

import sun.awt.windows.WPrinterJob;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.print.PrinterException;
import java.awt.GraphicsConfiguration;
import javax.print.PrintService;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.print.ServiceDialog;
//import sun.print.DialogTypeSelection;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import java.awt.print.PrinterJob;
import java.awt.Rectangle;


public class FOWPrinterJob /*extends WPrinterJob*/
{
//    public FOWPrinterJob(){}
//
//    public boolean printDialog(PrintRequestAttributeSet printrequestattributeset)
//        throws HeadlessException
//    {
//        if(GraphicsEnvironment.isHeadless())
//        {
//            throw new HeadlessException();
//        }
////        DialogTypeSelection dialogtypeselection = (DialogTypeSelection)printrequestattributeset.get(sun/print/DialogTypeSelection.class);
//        FODialogTypeSelection dialogtypeselection = (FODialogTypeSelection)printrequestattributeset.get(FODialogTypeSelection.class);
//        if(dialogtypeselection == FODialogTypeSelection.NATIVE)
//        {
//            attributes = printrequestattributeset;
//            try
//            {
////                debug_println("calling setAttributes in printDialog");
//                setAttributes(printrequestattributeset);
//            }
//            catch(PrinterException printerexception) { }
//            boolean flag = printDialog();
//            attributes = printrequestattributeset;
//            return flag;
//        }
//        final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//        PrintService printservice = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
//
////            final GraphicsConfiguration val$gc;
////            final RasterPrinterJob this$0;
//
//            public Object run()
//            {
//                PrintService printservice2 = getPrintService();
//                if(printservice2 == null)
//                {
//                    ServiceDialog.showNoPrintService(gc);
//                    return null;
//                } else
//                {
//                    return printservice2;
//                }
//            }
//
//
//            {
////                this$0 = RasterPrinterJob.this;
////                val$gc = graphicsconfiguration;
////                super();
//            }
//        });
//        if(printservice == null)
//        {
//            return false;
//        }
//        Object obj = null;
//        Object aobj[];
//        if(printservice instanceof StreamPrintService)
//        {
//            StreamPrintServiceFactory astreamprintservicefactory[] = lookupStreamPrintServices(null);
//            aobj = new StreamPrintService[astreamprintservicefactory.length];
//            for(int i = 0; i < astreamprintservicefactory.length; i++)
//            {
//                aobj[i] = astreamprintservicefactory[i].getPrintService(null);
//            }
//
//        } else
//        {
//            aobj = (PrintService[])(PrintService[])AccessController.doPrivileged(new PrivilegedAction() {
//
////                final RasterPrinterJob this$0;
//
//                public Object run()
//                {
//                    PrintService aprintservice[] = PrinterJob.lookupPrintServices();
//                    return aprintservice;
//                }
//
//
//            {
////                this$0 = RasterPrinterJob.this;
////                super();
//            }
//            });
//            if(aobj == null || aobj.length == 0)
//            {
//                aobj = new PrintService[1];
//                aobj[0] = printservice;
//            }
//        }
//        Rectangle rectangle = gc.getBounds();
//        int j = rectangle.x + rectangle.width / 3;
//        int k = rectangle.y + rectangle.height / 3;
//        PrintService printservice1;
//        try
//        {
//            printservice1 = FOServiceUI.printDialog(gc, j, k, ((PrintService []) (aobj)), printservice, javax.print.DocFlavor.SERVICE_FORMATTED.PAGEABLE, printrequestattributeset);
//        }
//        catch(IllegalArgumentException illegalargumentexception)
//        {
//            printservice1 = FOServiceUI.printDialog(gc, j, k, ((PrintService []) (aobj)), ((PrintService) (aobj[0])), javax.print.DocFlavor.SERVICE_FORMATTED.PAGEABLE, printrequestattributeset);
//        }
//        if(printservice1 == null)
//        {
//            return false;
//        }
//        if(!printservice.equals(printservice1))
//        {
//            try
//            {
//                setPrintService(printservice1);
//            }
//            catch(PrinterException printerexception1) { }
//        }
//        return true;
//    }

}
