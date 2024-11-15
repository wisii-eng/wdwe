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
 */package com.wisii.fov.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import com.wisii.component.createrender.TcpIpPrint;
public class PrintUtil
 {
	public static boolean isPrintService() {
		try {
			PrintService defaultService = PrintServiceLookup
					.lookupDefaultPrintService();
			PrintServiceAttributeSet pset = defaultService.getAttributes();
			Attribute attr = pset.get(PrinterIsAcceptingJobs.class);

			if (PrinterIsAcceptingJobs.ACCEPTING_JOBS.equals(attr)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static void print(String pname, InputStream input,
			DocFlavor docfloavor, JobName jobName) {
		String as[] = pname.split("\\.");
		if (as.length == 4)
			printbyIP(pname, input, docfloavor, jobName);
		else
			printbyname(pname, input, docfloavor, jobName);
	}
	private static void printbyIP(String pname, InputStream input,
			DocFlavor docfloavor, JobName jobName) {
		TcpIpPrint ss = new TcpIpPrint(pname);
		System.out.println(ss.card_status(new BufferedInputStream(input)));

	}

	private static void printbyname(String pname, InputStream input,
			DocFlavor docfloavor, JobName jobName) {
		PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService ps = null;
		for (int i = 0; i < pss.length; i++) {
			String ss = pss[i].getName();
			if (ss.toLowerCase().equals(pname.toLowerCase())) {
				ps = pss[i];
				break;
			}

		}
		if (ps == null) {
			System.out.println("没有找到打印机：" + pname + "，使用默认打印机");
			ps = PrintServiceLookup.lookupDefaultPrintService();

		}
		if (ps != null) {
			try {
				DocAttributeSet das = new HashDocAttributeSet();
				DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
				Doc myDoc = new SimpleDoc(input, flavor, das);
				DocPrintJob job = ps.createPrintJob();
				PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
				pras.add(jobName);
				job.print(myDoc, pras);

			} catch (Exception e) {
				System.out.println("打印不成功");
				e.printStackTrace();
			}

		} else
			System.out.println("打印机名错误请核实");
	}

}
