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
 * PrintRenderer.java
 *
 * 改版履历:2007.04.20
 *
 * 版本信息:1.0
 *
 * Copyright:WISe Internat Information Co.,Ltd.
 */

package com.wisii.fov.render.print;

import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Vector;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;

import com.wisii.component.setting.MutiDataBean;
import com.wisii.component.setting.PrintRef;
import com.wisii.component.setting.PrintSetting;
import com.wisii.component.setting.WisiiBean;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.area.PageViewport;
import com.wisii.fov.command.plugin.FOMethod;
import com.wisii.fov.fonts.FontInfo;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.util.PrintUtil;

/**
 * Renderer that prints through java.awt.PrintJob. The actual printing is
 * handled by Java2DRenderer since both PrintRenderer and AWTRenderer need to
 * support printing.
 */
public class PrintRenderer extends Java2DRenderer implements Pageable {
	/** The MIME type for AWT-Rendering */
	public static final String MIME_TYPE = MimeConstants.MIME_WISII_PRINT;

	/** 打印范围中的全部页面。 */
	private static final int ALL = 0;

	/** 打印范围中的偶数页面 */
	private static final int EVEN = 1;

	/** 打印范围中的奇数页面 */
	private static final int ODD = 2;

	/** 打印的开始页面的页号 */
	private int _startNumber = 0;

	/** 打印的结束页面的页号 */
	private int _endNumber = -1;

	/** 打印模式。0:全部打印；1:打印偶数页；2:打印奇数页 */
	private int _mode = ALL;


	/** 打印设置对话框。true:显示；false:不显示 */
	private boolean _isShowDialog = false;

	/** true:打印结束 */
	private boolean _isPrintDone = false;

	/**
	 * 标识从服务端返回的PageViewport数据是否为当前显示页面的PageViewport.true:是当前页面的数据；false:
	 * 不是当前页面的数据
	 */
	private boolean _isFirstData = true;

	/** 后台打印时候的标示 */
	private boolean _isBackPrint = true; // true:是后台打印，false：不是

	/** 打印对话框中是否选择打印 */
	public boolean printOK = false;

	// ［刘晓添加　20090726 ］
	private WisiiBean wisiibean;

	/**
	 * @return the wisiibean
	 */
	public WisiiBean getWisiibean() {
		return wisiibean;
	}

	/**
	 * @param wisiibean
	 *            the wisiibean to set
	 */
	public void setWisiibean(WisiiBean wisiibean) {
		this.wisiibean = wisiibean;
	}

	/** 构造函数。设置字体信息 */
	public PrintRenderer() {
		setupFontInfo();
	}

	/** 初始化打印属性 */
	public void initializePrinterJob(int start, int end, int mode, int copies,
			boolean showDialog, WisiiBean wisiibean) {
		this._startNumber = start;
		this._endNumber = end;
		this._mode = mode;
		this._isShowDialog = showDialog;
		this.wisiibean = wisiibean;
	}

	/**
	 * @see com.wisii.fov.render.java2d.Java2DRenderer#renderPage(PageViewport
	 *      pageViewport)
	 */
	public void renderPage(PageViewport pageViewport) throws IOException {
		if (!_isBackPrint && _isFirstData) {

			// 前台打印并且切换背景时的当前页不需要保留。
			this._isFirstData = false;
		} else {
			super.renderPage(pageViewport);
		}
	}

	/** @see com.wisii.fov.render.java2d.Java2DRenderer#stopRenderer() */
	public void stopRenderer() throws IOException {
		super.stopRenderer();
		_isPrintDone = false;
		if (PrintUtil.isPrintService()) {
			PrinterJob printerJob = FOPrinterJob.getPrinterJob();
			printerJob.setPageable(this);
			Vector numbers = null;
			if (wisiibean.getMdb() != null) {
				MutiDataBean mb = wisiibean.getMdb();
				// 设置当前页码
				int c = mb.getCurrPageNum() + getNumberOfPages();
				mb.setCurrPageNum(c);
				// 设置当前的份数信息
				mb.addPartAndPages(getNumberOfPages());

			}
			printerJob.setJobName("Wise Doc Data Editor");
			if (_endNumber == -1) {
				// was not set on command line
				_endNumber = getNumberOfPages();
			}

			numbers = getInvalidPageNumbers();
			for (int i = numbers.size() - 1; i > -1; i--) {
				removePage(Integer.parseInt((String) numbers.elementAt(i)));
			}

			try {
				// 设置打印属性
				HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
				FOPrintPageSetup pro;
				float ExcursionX = getExcursionX();
				float ExcursionY = getExcursionY();
				float ScaleX = getScaleX();
				float ScaleY = getScaleY();
				boolean isSelectedHeightCheckBox = isSelectedHeightCheckBox();
				float HeightAddABS = getHeightAddABS();
				pro = new FOPrintPageSetup(ExcursionX, ExcursionY, ScaleX,
						ScaleY, FOPrintPageSetup.MM, isSelectedHeightCheckBox,
						HeightAddABS);
				attributes.add(pro);
				OrientationRequested orrequest = getOrientationRequested();
				attributes.add(orrequest);
				JobName jobname = getJobName();
				attributes.add(jobname);

				if (wisiibean.getMdb() != null
						&& wisiibean.getPrintSetting().getPageCe() != 0
						&& wisiibean.getPrintSetting().getPageCS() != 0) {
					MutiDataBean mb = wisiibean.getMdb();

					// 判断那几页打印那几也不打印
					// 得到当前一共要出多少页
					int ct = getNumberOfPages();
					int dsd = ct + mb.getMutiPrintCount();
					if (dsd < wisiibean.getPrintSetting().getPageCS()) {
						mb.addMutiPrintCount(ct);
						return;
					}

					if (mb.getMutiPrintCount() >= wisiibean.getPrintSetting()
							.getPageCe()) {
						mb.addMutiPrintCount(ct);
						return;
					}
					int page = mb.getMutiPrintCount();

					int A = wisiibean.getPrintSetting().getPageCS();
					int B = wisiibean.getPrintSetting().getPageCe();
					// 计算起始页，并打到尾
					if ((page + ct) >= A && (page + ct) <= B) {
						if (page >= (A - 1)) {
							attributes.add(new PageRanges(1, ct));
						} else if (page < (A - 1)) {
							attributes.add(new PageRanges((A - page), ct));
						}

					} else if ((page + ct) > B) {
						if (page >= (A - 1)) {
							attributes.add(new PageRanges(1, (B - page)));
						} else if (page < (A - 1)) {
							attributes
									.add(new PageRanges(A - page, (B - page)));
						}
					}
					mb.addMutiPrintCount(ct);
				}
				String printname = getPrinter();
				boolean ok = true;
				if (!_isShowDialog || (ok = printerJob.printDialog(attributes))) {
					if (_isShowDialog) {
						pro = (FOPrintPageSetup) attributes
								.get(FOPrintPageSetup.class);
						printname = printerJob.getPrintService().toString();
						ExcursionX = pro.getX(FOPrintPageSetup.MM);
						ExcursionY = pro.getY(FOPrintPageSetup.MM);
						// 获取属性集中的属性
						wisiibean.getPrintSetting().setPrinter(printname);
						wisiibean.getPrintSetting().setExcursion(ExcursionX,
								ExcursionY);
						ScaleX = pro.getXscale(FOPrintPageSetup.MM);
						ScaleY = pro.getYscale(FOPrintPageSetup.MM);
						wisiibean.getPrintSetting().setScale(ScaleX, ScaleY);
						isSelectedHeightCheckBox = pro
								.isSelectedHeightCheckBox();
						wisiibean.getPrintSetting().setSelectedHeightCheckBox(
								isSelectedHeightCheckBox);
						HeightAddABS = pro.getheightAddABS();
						wisiibean.getPrintSetting().setHeightAddABS(
								HeightAddABS);
						orrequest = (OrientationRequested) attributes
								.get(OrientationRequested.class);
						wisiibean.getPrintSetting().setOrientationRequested(
								orrequest);

					}
				}
				printOK = ok;
				float excursionX1 = ExcursionX * PrintSetting.INCH
						/ PrintSetting.PT;
				float excursionY1 = ExcursionY * PrintSetting.INCH
						/ PrintSetting.PT;

				setPrintProperties(excursionX1, excursionY1, ScaleX, ScaleY,
						isSelectedHeightCheckBox, HeightAddABS
								* PrintSetting.INCH / PrintSetting.PT);
				setOrientation(wisiibean.getPrintSetting().getPageOrientation(
						orrequest));
				PrintService choosePsver = FOMethod.getPrinter(printname);
				printerJob.setPrintService(choosePsver);
				wisiibean.getPrintSetting().setPrinter(choosePsver.getName());
				if (ok)
				{
					int copies = getCopies();
//					if(copies>1)
//					{
//						Copies c=new Copies(copies);
//						attributes.add(c);
//					}
					String paperSource=this.getPaperEntry();
					if (paperSource != null && !paperSource.isEmpty())
					{
						String[] cmds = paperSource.split(";");
						// if(paperSource.)
						try
						{
							int ps = Integer.parseInt(cmds[0]);
							//if(ps>=0){
							MediaTray tray=null;
							if(ps==0){
							 tray=MediaTray.TOP;
							}
							else if(ps==1){
							 tray=MediaTray.MIDDLE;
							}
							else if(ps==2){
								 tray=MediaTray.BOTTOM;
								}
							else if(ps==3){
								 tray=MediaTray.ENVELOPE;
								}
							else if(ps==4){
								 tray=MediaTray.MANUAL;
								}
							else if(ps==5){
								 tray=MediaTray.LARGE_CAPACITY;
								}
							else if(ps==6){
								 tray=MediaTray.MAIN;
								}
							else if(ps==7){
								 tray=MediaTray.SIDE;
								}
							if(tray!=null)
							{
								attributes.add(tray);
							}
						} catch (Exception e)
						{

						}
						//增加双面打印功能，0：单面打印，1：双面（2 面）打印，长边装订，2：双面（2 面）打印，短边装订
						if (cmds.length > 1)
						{
							try
							{
								int dp = Integer.parseInt(cmds[1]);
								Sides size=null;
								
								if(dp==0){
									size=Sides.ONE_SIDED;
								}
								else if(dp==1){
									size=Sides.TWO_SIDED_LONG_EDGE;
								}
								else if(dp==2){
									size=Sides.TWO_SIDED_SHORT_EDGE;
									}
								if(size!=null)
								{
									attributes.add(size);
								}
							} catch (Exception e)
							{

							}
						}
						
					}
					for (int i = 0; i < copies; i++) {
						boolean isprint = false;
						int count=0;
						
						//打印机某些情况下会报错java.awt.print.PrinterException: Printer is not accepting job.
						//为了防止打印不成功，遇到这种情况时重复打印,为了防止死循环，最多重复500次
						//
						while (!isprint&&count<500) {
							try {
								printerJob.setPrintService(choosePsver);
								//attributes.add(arg0)
								printerJob.print(attributes);
								isprint = true;
							} catch (Exception e) {
								count++;
								isprint = false;
								try {
									Thread.currentThread().sleep(100);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				}
				_isPrintDone = ok;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("不能打印: " + e.getClass().getName() + ": "
						+ e.getMessage());
			}

			clearViewportList();

		} else {
			JOptionPane.showMessageDialog(null,
					"请检查本地打印服务是否启动！\n\r控制面板->管理工具->服务->Print Spooler", "请注意",
					JOptionPane.WARNING_MESSAGE);

		}
		_isPrintDone = printOK;
	}

	/** 获取无效页面的页号的列表 */
	private Vector getInvalidPageNumbers() {
		Vector vec = new Vector();
		int max = getNumberOfPages();
		boolean isValid;

		for (int i = 0; i < max; i++) {
			isValid = true;
			if (i < _startNumber || i > _endNumber) {
				isValid = false;
			} else if (_mode != ALL) {
				// if(_mode == EVEN && ((i + 1) % 2 != 0))
				if ((_mode == EVEN && ((i + 1) & 1) != 0)) {
					isValid = false;
				}
				// else if(_mode == ODD && ((i + 1) % 2 != 1))
				else if ((_mode == ODD && ((i + 1) & 1) != 1)) {
					isValid = false;
				}
			}
			if (!isValid) {
				vec.add(Integer.toString(i));
			}
		}

		return vec;
	}

	/**
	 * 从pageViewportList中删除指定的页面
	 * 
	 * @param pageNum
	 *            页面的页码
	 */
	private void removePage(int pageNum) {
		if (pageNum < 0 || pageNum > pageViewportList.size() - 1) {
			return;
		}
		pageViewportList.remove(pageNum);
	}

	/** @see java.awt.print.Pageable#getPageFormat(int) */
	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		try {
			if (pageIndex >= getNumberOfPages()) {
				return null;
			}

			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();

			Rectangle2D dim = getPageViewport(pageIndex).getViewArea();
			double width = dim.getWidth();
			double height = dim.getHeight();

			height = this.setPagerHeight(height);// 设置打印纸的高度

			// if the width is greater than the height assume lanscape mode
			// and swap the width and height values in the paper format
			if (width > height) {
				paper.setImageableArea(0, 0, height / 1000d, width / 1000d);
				paper.setSize(height / 1000d, width / 1000d);
				pageFormat.setOrientation(PageFormat.LANDSCAPE);
			} else {
				paper.setImageableArea(0, 0, width / 1000d, height / 1000d);
				paper.setSize(width / 1000d, height / 1000d);
				pageFormat.setOrientation(PageFormat.PORTRAIT);
			}

			paper = setStatePaper(width, height, paper);
			pageFormat.setOrientation(this.getOrientation());

			pageFormat.setPaper(paper);
			return pageFormat;
		} catch (FOVException fovEx) {
			throw new IndexOutOfBoundsException(fovEx.getMessage());
		}
	}

	/** @see com.wisii.fov.render.Java2DRenderer#clearViewportList() */
	public void clearViewportList() {
		super.clearViewportList();
		this._isFirstData = true;
	}

	/** @see java.awt.print.Pageable#getPrintable(int) */
	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {

		return this;
	}

	/** @see com.wisii.fov.render.Renderer#setupFontInfo(com.wisii.fov.fonts.FontInfo) */
	public void setupFontInfo() {
		setupFontInfo(new FontInfo());
	}

	/** @see com.wisii.fov.render.AbstractRenderer */
	public String getMimeType() {
		return MIME_TYPE;
	}

	/** 打印结束，返回true，否则返回false */
	public boolean isPrintDone() {
		return _isPrintDone;
	}

	public void setisBackPrint(boolean f) {
		_isBackPrint = f;
	}

	private String getPrinter() {
		if (printref != null && printref.getPrinter() != null) {
			return printref.getPrinter();
		}
		return wisiibean.getPrintSetting().getPrinter();
	}

	private int getCopies() {
		if (printref != null && printref.getCopies() > 0) {
			return printref.getCopies();
		}
		return wisiibean.getPrintSetting().getCopies();
	}

	private float getExcursionX() {
		if (printref != null && printref.getExcursionX() != PrintRef.NAF) {
			return printref.getExcursionX();
		}
		return wisiibean.getPrintSetting().getExcursionX();
	}

	private float getExcursionY() {
		if (printref != null && printref.getExcursionY() != PrintRef.NAF) {
			return printref.getExcursionY();
		}
		return wisiibean.getPrintSetting().getExcursionY();
	}

	private float getScaleX() {
		if (printref != null && printref.getScaleX() != PrintRef.NAF) {
			return printref.getScaleX();
		}
		return wisiibean.getPrintSetting().getScaleX();
	}

	private float getScaleY() {
		if (printref != null && printref.getScaleY() != PrintRef.NAF) {
			return printref.getScaleY();
		}
		return wisiibean.getPrintSetting().getScaleY();
	}

	private boolean isSelectedHeightCheckBox() {
		if (printref != null && printref.isSelectedHeightCheckBox()) {
			return printref.isSelectedHeightCheckBox();
		}
		return wisiibean.getPrintSetting().isSelectedHeightCheckBox();
	}

	private float getHeightAddABS() {
		if (printref != null && printref.getHeightAddABS() != PrintRef.NAF) {
			return printref.getHeightAddABS();
		}
		return wisiibean.getPrintSetting().getHeightAddABS();
	}

	private JobName getJobName() {
		if (printref != null && printref.getJobName() != null) {
			return printref.getJobName();
		}
		return wisiibean.getPrintSetting().getJobName();
	}

	private OrientationRequested getOrientationRequested() {
		if (printref != null && printref.getOrientationRequested() != null) {
			return printref.getOrientationRequested();
		}
		return wisiibean.getPrintSetting().getOrientationRequested();
	}
	private String getPaperEntry()
	{
		if (printref != null && printref.getCommonLine() != null) {
			return printref.getCommonLine();
		}
		return "";
	}

}
