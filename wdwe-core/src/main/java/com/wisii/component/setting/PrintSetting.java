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
 */package com.wisii.component.setting;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.OrientationRequested;

import com.alibaba.fastjson2.JSON;
import com.wisii.component.startUp.SystemUtil;

/**
 * 打印设置类
 * 
 * 增加打印参数: 打印参数包括如下几个参数:
 * 
 * printSetting 中有：
 * 
 * 页面纵向的偏移量。正数时，向上偏移；负数时，向下偏移 excursionX
 * 
 * 页面横向的偏移量。正数时，向左偏移；负数时，向右偏移 excursionY
 * 
 * 页面横向的缩放比例 scaleX
 * 
 * 页面纵向的的缩放比例 scaleY
 * 
 * 是否在打印纸的height上使用缩放 isSelectedHeightCheckBox
 * 
 * 打印纸的height增加的值 heightAddABS
 * 
 * 打印的方向 默认为横向打印 orientationRequested
 * 
 * 可以不设制，如果不设制，则需要保持cookie中存有如上参数
 * 
 * PrintRef，这个为分组打印中的参数,调用addPrintRef(PrintRef)方法即向PrintSetting中增加一组打印参数
 * 
 * 同时也可以调用setPrintSquence(String)将打印分组信息加入到PrintSetting中 此参数的格式如下：
 * 
 * "1,PS,HP LaserJet 6P,/MediaPosition 0#2,PS,HP LaserJet 6P,/MediaPosition
 * 2#3,PS,HP LaserJet 6P,/MediaPosition 1";
 * 
 * 每组参数间已#分割 一组参数中需要有四个参数 之间以，分割 参数分别为：组号，输出格式，打印机名，命令行参数
 * 
 * 目前不支持 组号不连续，组号默认为０，不填即为默认， 组号重复则以最后一个设置为准。
 * 
 * 
 * @author liuxiao
 * 
 */
public class PrintSetting implements Serializable {

	public static final String PRINTSQUEN = "printsquence";
	public static final String EXCURSIONX = "excursionx";
	public static final String EXCURSIONY = "excursiony";
	public static final String SCALEX = "scalex";
	public static final String SCALEY = "scaley";
	public static final String ISSELECTEDHEIGHTCHECKBOX = "isselectedheightcheckbox";
	public static final String HEIGHTADDABS = "heightaddabs";
	public static final String ORIENTATIONREQUESTED = "orientationrequested";
	public static final String PRINTER = "printer";
	public static final String JUSTPRINTREF = "justprintref";
	public static final String COPIES = "copies";
	/* 打印页数 */
	private String pageCount = null;
	/* 打印开始页*/
	private int pageCS = 0;
	/* 打印 结束页 */
	private int pageCe = 0;
	
	/* 打印分数 */
	private int copies = 1;

	/* 每组打印参数,默任大小为1 */
	private Map printRefMap = new HashMap();

	/* 打印最大偏移量 */
	public static final float MAX_excursion = 500.0f;

	/* 打印最小偏移量 */
	public static final float MIN_excursion = -500.0f;

	/* 最小显示比例 。 */
	public static final float MIN_PERCENT = 20.0f;

	/* 最大显示比例 。 */
	public static final float MAX_PERCENT = 500.0f;

	/** *英寸 */
	public static final int INCH = 72000;

	/** *点 */
	public static final int PT = 25400;

	/* 页面纵向的偏移量。正数时，向上偏移；负数时，向下偏移 */
	private float excursionX = 0.0f;

	/* 页面横向的偏移量。正数时，向左偏移；负数时，向右偏移 */
	private float excursionY = 0.0f;

	/* 页面横向的缩放比例 */
	private float scaleX = 100.0f;

	/* 页面纵向的的缩放比例 */
	private float scaleY = 100.0f;

	/* 是否在打印纸的height上使用缩放 */
	private boolean isSelectedHeightCheckBox = false;

	/* 打印纸的height增加的值 */
	private float heightAddABS = 0.0f;

	/* 打印的方向 默认为横向打印 */
	private OrientationRequested orientationRequested = OrientationRequested.PORTRAIT;


	/* 打印机名 */
	private String printer;

	/* 打印到文件生成相应的文件 */
	private File outputFile = null;
	// 任务名称
	private JobName jobName = null;
	//是否只打印设置了分组属性的页面，即printRefMap中有设置的页面
    private boolean justprintref = false;

	public String toSerializable() {
		return JSON.toJSONString(this);
	}

	public PrintSetting unSerializable(String dd) {

		return JSON.parseObject(dd, PrintSetting.class);

	}

	/**
	 * 根据PrintSetting将返回参数格式："printer"+printer+"&excursionX=" + excursionX +
	 * "&excursionY=" + excursionY + "&scaleX=" + scaleX + "&scaleY=" + scaleY +
	 * "&isSelectedHeightCheckBox=" + isSelectedHeightCheckBox +
	 * "&heightAddABS=" + heightAddABS + "&orientationRequested=" +
	 * orientationRequested + "&printSquence=" ; // return "PS,HP LaserJet
	 * 6P,/MediaPosition 0#PS,HP LaserJet 6P,/MediaPosition 2#PS,HP LaserJet
	 * 6P,/MediaPosition 1";
	 * 
	 */
	public String toString() {
		String basePs = null;
		basePs = "printer=" + printer
				+ SystemUtil.getConfByName("base.devidegroup") + "excursionX="
				+ excursionX + SystemUtil.getConfByName("base.devidegroup")
				+ "excursionY=" + excursionY
				+ SystemUtil.getConfByName("base.devidegroup") + "scaleX="
				+ scaleX + SystemUtil.getConfByName("base.devidegroup")
				+ "scaleY=" + scaleY
				+ SystemUtil.getConfByName("base.devidegroup")
				+ "isSelectedHeightCheckBox=" + isSelectedHeightCheckBox
				+ SystemUtil.getConfByName("base.devidegroup")
				+ "heightAddABS=" + heightAddABS
				+ SystemUtil.getConfByName("base.devidegroup")
				+ "orientationRequested=" + orientationRequested
				+ SystemUtil.getConfByName("base.devidegroup")
				+ "printSquence=";

		String printSquence = "";
		if (printRefMap != null) {
			Iterator it = ((Collection) printRefMap.values()).iterator();
			while (it.hasNext()) {
				PrintRef rf = (PrintRef) it.next();
				printSquence += rf.getGroupId() + "," + rf.getOutputMode()
						+ "," + rf.getPrinter() + "," + rf.getCommonLine();
				printSquence += "#";
			}

		}
		basePs += printSquence;
		return basePs;
	}

	/**
	 * @param printSquence
	 *            the printSquence to set 在原有命令行参数的基础上面增加一个组号的参数 1,PS,HP
	 *            LaserJet 6P,/MediaPosition 0#2,PS,HP LaserJet 6P,
	 *            /MediaPosition 2#3,PS,HP LaserJet 6P,/MediaPosition 1
	 */
	public void setPrintSquence(String printSquence) {

		if (printSquence == null || printSquence.isEmpty()||"null".equalsIgnoreCase(printSquence)) {
			return;
		}
		String[] rowDate = printSquence.split(SystemUtil
				.getConfByName("base.print.devidegroup"));
		if (printRefMap.size() == 0 || printRefMap.get("0") == null) {
			printRefMap.put("0", getPrintRef(new String[]{"0", "AWT", "", "0"}));
		}

		for (int i = 0; i < rowDate.length; i++) {
			String[] columDate = rowDate[i].split(SystemUtil
					.getConfByName("base.print.devideitem"));

			PrintRef pr;
			if(columDate.length < 3)
			{
				continue;
			}
			 else {
				pr = getPrintRef(columDate);
				
			}

			printRefMap.put(columDate[0], pr);
		}

	}
    private PrintRef getPrintRef(String[] columDates)
    {
    	PrintRef ref = new PrintRef();
    	int len = columDates.length;
    	for(int i=0;i<len;i++)
    	{
    		ref.setValue(i, columDates[i]);
    	}
    	return ref;
    }
	/**
	 * @return
	 */
	public int getCopies() {
		return copies;
	}

	/**
	 * @param copies
	 */
	public void setCopies(int copies) {
		this.copies = copies;
	}

	/**
	 * @param copies
	 */
	public void setCopies(String copies) {
		try {
			this.copies = Integer.parseInt(copies);
		} catch (Exception e) {
			this.copies = 1;
		}
	}

	/**
	 * @return
	 */
	public String getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount
	 */
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return
	 */
	public Map getPrintRefMap() {
		return printRefMap;
	}

	/**
	 * 添加一行打印参数
	 * 
	 * @param printRefMap
	 */
	public void addPrintRef(PrintRef pr) {
		printRefMap.put(pr.getGroupId() + "", pr);
	}

	/**
	 * @return the excursionX
	 */
	public float getExcursionX() {

		if (excursionX < MIN_excursion) {
			excursionX = MIN_excursion;
		} else if (excursionX > MAX_excursion) {
			excursionX = MAX_excursion;
		}

		return excursionX;
	}

	/**
	 * @param excursionX
	 *            the excursionX to set
	 */
	public void setExcursion(float excursionX, float excursionY) {
		this.excursionX = excursionX;
		this.excursionY = excursionY;
	}

	/**
	 * @param excursionX
	 *            the excursionX to set
	 */
	public void setExcursion(String texcursionX, String texcursionY) {

		if (texcursionX != null) {
			try {
				excursionX = Float.parseFloat(texcursionX);

			} catch (Exception e) {
				excursionX = 0.0f;
			}
		} else {
			excursionX = 0.0f;
		}
		if (texcursionY != null) {
			try {
				excursionY = Float.parseFloat(texcursionY);

			} catch (Exception e) {
				excursionY = 0.0f;
			}
		} else {
			excursionY = 0.0f;
		}

	}

	public void setExcursionX(String texcursionX) {

		if (texcursionX != null) {
			try {
				excursionX = Float.parseFloat(texcursionX);

			} catch (Exception e) {
				excursionX = 0.0f;
			}
		} else {
			excursionX = 0.0f;
		}

	}

	public void setExcursionY(String texcursionY) {

		if (texcursionY != null) {
			try {
				excursionY = Float.parseFloat(texcursionY);

			} catch (Exception e) {
				excursionY = 0.0f;
			}
		} else {
			excursionY = 0.0f;
		}

	}

	/**
	 * @return the excursionY
	 */
	public float getExcursionY() {

		if (excursionY < MIN_excursion) {
			excursionY = MIN_excursion;
		} else if (excursionY > MAX_excursion) {
			excursionY = MAX_excursion;
		}

		return excursionY;
	}

	/**
	 * @return the heightAddABS
	 */
	public float getHeightAddABS() {
		return heightAddABS;
	}

	/**
	 * @param heightAddABS
	 *            the heightAddABS to set
	 */
	public void setHeightAddABS(float heightAddABS) {

		this.heightAddABS = heightAddABS;
	}

	/**
	 * @param heightAddABS
	 *            the heightAddABS to set
	 */
	public void setHeightAddABS(String theightAddABS) {
		try {
			if (theightAddABS != null) {
				heightAddABS = Float.parseFloat(theightAddABS);
			}
		} catch (Exception e) {
			heightAddABS = 0.0f;
		}

	}

	/**
	 * @return the isSelectedHeightCheckBox
	 */
	public boolean isSelectedHeightCheckBox() {
		return isSelectedHeightCheckBox;
	}

	/**
	 * @param isSelectedHeightCheckBox
	 *            the isSelectedHeightCheckBox to set
	 */
	public void setSelectedHeightCheckBox(boolean isSelectedHeightCheckBox) {
		this.isSelectedHeightCheckBox = isSelectedHeightCheckBox;
	}

	/**
	 * @param isSelectedHeightCheckBox
	 *            the isSelectedHeightCheckBox to set
	 */
	public void setSelectedHeightCheckBox(String tisSelectedHeightCheckBox) {

		if (tisSelectedHeightCheckBox != null) {
			try {

				isSelectedHeightCheckBox = Boolean.valueOf(
						tisSelectedHeightCheckBox).booleanValue();
			} catch (Exception e) {
				isSelectedHeightCheckBox = false;
			}
		}

	}

	/**
	 * @return the orientationRequested
	 */
	public OrientationRequested getOrientationRequested() {
		return orientationRequested;
	}

	/**
	 * @param orientationRequested
	 *            the orientationRequested to set
	 */
	public void setOrientationRequested(
			OrientationRequested orientationRequested) {

		this.orientationRequested = orientationRequested;
	}

	/**
	 * @param orientationRequested
	 *            the orientationRequested to set
	 */
	public void setOrientationRequested(String torientationRequested) {
		String or = torientationRequested;
		if (or != null) {
			if (or.equalsIgnoreCase("landscape")) {
				this.orientationRequested = OrientationRequested.LANDSCAPE;
			} else if (or.equalsIgnoreCase("portrait")) {
				this.orientationRequested = OrientationRequested.PORTRAIT;
			} else if (or.equalsIgnoreCase("reverse-landscape")) {
				this.orientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
			}
		}

	}

	/**
	 * @return the scaleX
	 */
	public float getScaleX() {

		if (scaleX < MIN_PERCENT) {
			scaleX = MIN_PERCENT;
		} else if (scaleX > MAX_PERCENT) {
			scaleX = MAX_PERCENT;
		}
		return scaleX;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScale(String tscaleX, String tscaleY) {
		if (tscaleX != null) {
			try {
				scaleX = Float.parseFloat(tscaleX);

			} catch (Exception e) {
				scaleX = 0.0f;
			}
		} else {
			scaleX = 0.0f;
		}
		if (tscaleY != null) {
			try {
				scaleY = Float.parseFloat(tscaleY);

			} catch (Exception e) {
				scaleY = 0.0f;
			}
		} else {
			scaleY = 0.0f;
		}

	}

	public void setScaleX(String tscaleX) {
		if (tscaleX != null) {
			try {
				scaleX = Float.parseFloat(tscaleX);

			} catch (Exception e) {
				scaleX = 0.0f;
			}
		} else {
			scaleX = 0.0f;
		}
	}

	public void setScaleY(String tscaleY) {
		if (tscaleY != null) {
			try {
				scaleY = Float.parseFloat(tscaleY);

			} catch (Exception e) {
				scaleY = 0.0f;
			}
		} else {
			scaleY = 0.0f;
		}

	}

	/**
	 * @return the scaleY
	 */
	public float getScaleY() {

		if (scaleY < MIN_PERCENT) {
			scaleY = (float) MIN_PERCENT;
		} else if (scaleY > MAX_PERCENT) {
			scaleY = (float) MAX_PERCENT;
		}
		return scaleY;
	}

	public static void main(String[] arg) {
		PrintSetting ps = new PrintSetting();
		String cd = "0,AWT,HP LaserJet 6P,";
		ps.setPrintSquence(cd);

	}

	/**
	 * @return the printer
	 */
	public String getPrinter() {
		return printer;
	}

	/**
	 * @param printer
	 *            the printer to set
	 */
	public void setPrinter(String printer) {

		this.printer = printer;
		if (printRefMap != null && printRefMap.size() > 0) {
			PrintRef pr = (PrintRef) printRefMap.get("0");
			pr.setPrinter(PrintRef.PRINTER_NAME, printer);
			printRefMap.put("0", pr);
		}
	}

	/**
	 * @return outputFileName
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFileName
	 *            要设置的 outputFileName
	 */
	public void setOutputFile(File outputFileName) {
		this.outputFile = outputFileName;
	}

	/**
	 * @param outputFileName
	 *            要设置的 outputFileName
	 */
	public void setOutputFileName(String outputFile) {
		this.outputFile = new File(outputFile);
	}

	public int getPageOrientation(OrientationRequested or) {
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

	/**
	 * @return the jobName
	 */
	public JobName getJobName() {
		if(jobName==null)
		{
			jobName=new JobName("wisiiPrintJob", null); 
		}
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = new JobName(jobName, null); 

	}

	/**
	 * @return the pageCS
	 */
	public  int getPageCS() {
		return pageCS;
	}

	/**
	 * @param pageCS the pageCS to set
	 */
	public  void setPageCS(int pageCS) {
		this.pageCS = pageCS;
	}
	/**
	 * @param pageCe the pageCe to set
	 */
	public  void setPageCS(String pageCS) {
		this.pageCS = Integer.parseInt(pageCS);
	}
	/**
	 * @return the pageCe
	 */
	public  int getPageCe() {
		return pageCe;
	}

	/**
	 * @param pageCe the pageCe to set
	 */
	public  void setPageCe(int pageCe) {
		this.pageCe = pageCe;
	}
	/**
	 * @param pageCe the pageCe to set
	 */
	public  void setPageCe(String pageCe) {
		this.pageCe = Integer.parseInt(pageCe);
	}
	/**
	 * @param pageCS the pageCS to set
	 */
	public  void setPageCsACe(String pages) {
		if(pages==null||"".equalsIgnoreCase(pages)) return;
		String[] df=pages.split("-");
		setPageCS( df[0]) ;
		try{
		setPageCe( df[1]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			setPageCe( df[0]);
		}
	}

	public boolean isJustprintref() {
		return justprintref;
	}
    
	public void setJustprintref(String justprintref) {
		this.justprintref = Boolean.parseBoolean(justprintref);
	}
    
}
