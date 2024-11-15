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
 *//**
 * AbstractPrintActionDoit.java
 * 北京汇智互联版权所有
 */
package com.wisii.component.createinitview.openView;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JOptionPane;
import com.wisii.component.setting.MutiDataBean;
import com.wisii.component.setting.PrintSetting;
import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.util.EngineUtil.PrintState;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.fov.render.awt.EditorRenderer;
import com.wisii.fov.render.awt.viewer.Command;
import com.wisii.fov.render.awt.viewer.Translator;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.render.print.FOPrintPageSetup;
import com.wisii.fov.render.print.FOPrinterJob;
import com.wisii.fov.util.PrintUtil;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-9
 */
public class AbstractPrintActionDoit extends Command {
	public AbstractPrintActionDoit(String name, String iconName) {
		super(name, iconName);
	}
	public void action(ActionEvent e) 
	{
		initbeforeprint();
		print();
		clearafterprint();
	}
	protected void initbeforeprint()
	{
		EngineUtil.getEnginepanel().getFOUserAgent().setIsview(false);
	}
	protected void clearafterprint()
	{
		EngineUtil.getEnginepanel().getFOUserAgent().setIsview(true);
	}
	protected void print() {
		if (PrintUtil.isPrintService()) {
			HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
			final EnginePanel enginepanel = EngineUtil.getEnginepanel();
			final WisiiBean wisiibean = enginepanel.getWisiibean();
			FOPrintPageSetup pro = new FOPrintPageSetup(wisiibean
					.getPrintSetting().getExcursionX(), wisiibean
					.getPrintSetting().getExcursionY(), wisiibean
					.getPrintSetting().getScaleX(), wisiibean.getPrintSetting()
					.getScaleY(), FOPrintPageSetup.MM, wisiibean
					.getPrintSetting().isSelectedHeightCheckBox(), wisiibean
					.getPrintSetting().getHeightAddABS());
			attributes.add(pro); // 把打印调整属性添加到属性集中
			attributes.add(wisiibean.getPrintSetting()
					.getOrientationRequested());
			attributes.add(wisiibean.getPrintSetting().getJobName());
			PrinterJob pj = FOPrinterJob.getPrinterJob();
			Java2DRenderer renderer = enginepanel.getRenderer();
			pj.setPageable((EditorRenderer) renderer);
			String printer = wisiibean.getPrintSetting().getPrinter();
			try {
				PrintService defaultService = FOMethod.getPrinter(printer);
				wisiibean.getPrintSetting()
						.setPrinter(defaultService.getName());
				pj.setPrintService(defaultService);
			} catch (PrinterException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"打印服务不支持2D打印或打印服务不可用", "请注意",
						JOptionPane.WARNING_MESSAGE);
				EngineUtil.setPrintstate(PrintState.PrintServiceUnSupport);
			}

			if (pj.printDialog(attributes)) {
				pro = (FOPrintPageSetup) attributes.get(FOPrintPageSetup.class);
				// 获取属性集中的属性
				wisiibean.getPrintSetting().setPrinter(
						pj.getPrintService().getName());// liuxiao
				printer = pj.getPrintService().getName();
				wisiibean.getPrintSetting().setPageCe(0);
				wisiibean.getPrintSetting().setPageCS(0);
				try {
					String d = attributes.get(PageRanges.class).toString();
					wisiibean.getPrintSetting().setPageCsACe(d);
				} catch (NullPointerException e) {
				}
				wisiibean.getPrintSetting().setExcursion(
						pro.getX(FOPrintPageSetup.MM),
						pro.getY(FOPrintPageSetup.MM));
				wisiibean.getPrintSetting().setScale(
						pro.getXscale(FOPrintPageSetup.MM),
						pro.getYscale(FOPrintPageSetup.MM));

				wisiibean.getPrintSetting().setSelectedHeightCheckBox(
						pro.isSelectedHeightCheckBox());
				wisiibean.getPrintSetting().setHeightAddABS(
						pro.getheightAddABS());
				renderer.setPrintProperties(wisiibean.getPrintSetting()
						.getExcursionX() * PrintSetting.INCH / PrintSetting.PT,
						wisiibean.getPrintSetting().getExcursionY()
								* PrintSetting.INCH / PrintSetting.PT,
						wisiibean.getPrintSetting().getScaleX(), wisiibean
								.getPrintSetting().getScaleY(), wisiibean
								.getPrintSetting().isSelectedHeightCheckBox(),
						wisiibean.getPrintSetting().getHeightAddABS()
								* PrintSetting.INCH / PrintSetting.PT);
				wisiibean.getPrintSetting().setOrientationRequested(
						(OrientationRequested) attributes
								.get(OrientationRequested.class));
				renderer.setOrientation(getPageOrientation(wisiibean
						.getPrintSetting().getOrientationRequested()));
				try {
					pj.print(attributes);
					EngineUtil.setPrintstate(PrintState.Success);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"打印发生异常，请确认打印机是否可用", "请注意",
							JOptionPane.WARNING_MESSAGE);
					EngineUtil.setPrintstate(PrintState.OtherException);
				}
			}

		} else {
			JOptionPane.showMessageDialog(null,
					"请检查本地打印服务是否启动！\n\r控制面板->管理工具->服务->Print Spooler", "请注意",
					JOptionPane.WARNING_MESSAGE);
			EngineUtil.setPrintstate(PrintState.PrintServiceUnUsable);
		}

	}

	private int getPageOrientation(final OrientationRequested or) {
		int i = 0;
		if (or.equals(OrientationRequested.LANDSCAPE)) {
			// pageFormat.setOrientation(PageFormat.LANDSCAPE); //0
			i = 0;
		} else if (or.equals(OrientationRequested.PORTRAIT)) {
			// pageFormat.setOrientation(PageFormat.PORTRAIT); //1
			i = 1;
		} else if (or.equals(OrientationRequested.REVERSE_PORTRAIT)) {

		} else if (or.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
			// pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE); //2
			i = 2;
		}
		return i;
	}
}
