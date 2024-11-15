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
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.OrientationRequested;

/**
 * 每组打印参数描述,对象
 */
public class PrintRef implements Serializable {
    public static final float NAF=-100000f;
	public static final int PRINTER_IP = 2;

	public static final int PRINTER_NAME = 1;

	public enum OUTPUTMode {
		PCL, PS, PNG, TIFF, TXT, PDF, PRINT,AFP,AWT,FLASH;
	}

	/*
	 * 组号，为上述信息分配组号默认为0；如果为没有分组号信息， 则直接选择默认的打印参数，否则就按照组号的设置进行打印
	 * 
	 */
	private int groupId = 0;

	/* 打印机 */
	private String printerIP = null;

	private String printerName = null;

	/* 进纸槽（纸张来源 */
	private String mediaPosition = null;

	/* 输出模式 */
	private OUTPUTMode outputMode = OUTPUTMode.PRINT;

	/* 命令行参数；针对需要输入命令行参数来控制打印机的打印方式 */
	private String commonLine = null;

	//打印份数
	private int copies = 1;
	/* 页面纵向的偏移量。正数时，向上偏移；负数时，向下偏移 */
	private float excursionX = 0;

	/* 页面横向的偏移量。正数时，向左偏移；负数时，向右偏移 */
	private float excursionY =0;

	/* 页面横向的缩放比例 */
	private float scaleX = 100;

	/* 页面纵向的的缩放比例 */
	private float scaleY =100;

	/* 是否在打印纸的height上使用缩放 */
	private boolean isSelectedHeightCheckBox = false;

	/* 打印纸的height增加的值 */
	private float heightAddABS = NAF;
	/* 打印的方向 默认为横向打印 */
	private OrientationRequested orientationRequested = null;

	/* 打印到文件生成相应的文件 */
	private File outputFile = null;
	// 任务名称
	private JobName jobName = null;

	public PrintRef() {

		super();
	}

	public void setValue(int index, String value) {
		if (value == null || value.trim().length() == 0) {
			return;
		}
		switch (index) {
		case 0: {
			setGroupId(toInt(value, 0));
			break;
		}
		case 1: {
			setOutputMode(value);
			break;
		}
		case 2: {
			printerName = value;
			break;
		}
		case 3: {
			setCommonLine(value);
			break;
		}
		case 4: {
			setCopies(toInt(value, 1));
			break;
		}
		case 5: {
			setExcursionX(toFloat(value, 0.0f));
			break;
		}
		case 6: {
			setExcursionY(toFloat(value, 0.0f));
			break;
		}
		case 7: {
			setScaleX(toFloat(value, 100f));
			break;
		}
		case 8: {
			setScaleY(toFloat(value, 100f));
			break;
		}
		case 9: {
			setSelectedHeightCheckBox(toBoolean(value, false));
			break;
		}
		case 10: {
			setHeightAddABS(toFloat(value, 0.0f));
			break;
		}
		case 11: {
			setOrientationRequested(value);
			break;
		}
		case 12: {
			setOutputFile(value);
			break;
		}
		case 13: {
			setJobName(value);
			break;
		}
		default: {

		}
		}
	}

	/**
	 * @return commonLine
	 */
	public String getCommonLine() {
		return commonLine;
	}

	/**
	 * @param commonLine
	 *            要设置的 commonLine
	 */
	public void setCommonLine(String commonLine) {
		this.commonLine = commonLine;
	}

	/**
	 * @return groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            要设置的 groupId
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return mediaPosition
	 */
	public String getMediaPosition() {
		return mediaPosition;
	}

	/**
	 * @param mediaPosition
	 *            要设置的 mediaPosition
	 */
	public void setMediaPosition(String mediaPosition) {
		this.mediaPosition = mediaPosition;
	}

	/**
	 * @return outputMode
	 */
	public OUTPUTMode getOutputMode() {
		return outputMode;
	}

	/**
	 * @param outputMode
	 *            要设置的 outputMode
	 */
	private void setOutputMode(String outputMode) {
		OUTPUTMode mode = OUTPUTMode.valueOf(outputMode.toUpperCase());
		if (mode != null) {
			this.outputMode = mode;
		}
	}

	public void setOutputMode(OUTPUTMode outputMode) {
		this.outputMode = outputMode;
	}

	/**
	 * @param printerIp
	 *            要设置的 printerIp
	 */
	public void setPrinter(int type, String printer) {

		if (type == PRINTER_IP)
			this.printerIP = printer;
		if (type == PRINTER_NAME)
			this.printerName = printer;
	}

	/**
	 * @return the printerName
	 */
	public String getPrinter() {
		if (printerIP != null && !printerIP.equalsIgnoreCase(""))
			return printerIP;
		else if (printerName != null && !printerName.equalsIgnoreCase(""))
			return printerName;
		else
			return null;
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}

	private int toInt(String value, int def) {
		try {
			int inte = Integer.parseInt(value);
			return inte;
		} catch (Exception e) {
			return def;
		}
	}

	private float toFloat(String value, float def) {
		try {
			float tofloat = Float.parseFloat(value);
			return tofloat;
		} catch (Exception e) {
			return def;
		}
	}

	private boolean toBoolean(String value, boolean def) {
		try {
			boolean tofloat = Boolean.parseBoolean(value);
			return tofloat;
		} catch (Exception e) {
			return def;
		}
	}

	public float getExcursionX() {
		return excursionX;
	}

	public void setExcursionX(float excursionX) {
		this.excursionX = excursionX;
	}

	public float getExcursionY() {
		return excursionY;
	}

	public void setExcursionY(float excursionY) {
		this.excursionY = excursionY;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public boolean isSelectedHeightCheckBox() {
		return isSelectedHeightCheckBox;
	}

	public void setSelectedHeightCheckBox(boolean isSelectedHeightCheckBox) {
		this.isSelectedHeightCheckBox = isSelectedHeightCheckBox;
	}

	public float getHeightAddABS() {
		return heightAddABS;
	}

	public void setHeightAddABS(float heightAddABS) {
		this.heightAddABS = heightAddABS;
	}

	public JobName getJobName() {
		return jobName;
	}

	private void setJobName(String jobName) {
		this.jobName = new JobName(jobName, null);
	}

	public OrientationRequested getOrientationRequested() {
		return orientationRequested;
	}

	private void setOrientationRequested(String value) {
		String lvalue = value.toLowerCase();
		if (lvalue.equals("portrait")) {
			this.orientationRequested = OrientationRequested.PORTRAIT;
		} else if (lvalue.equals("landscape")) {
			this.orientationRequested = OrientationRequested.LANDSCAPE;
		} else if (lvalue.equals("reverse_landscape")) {
			this.orientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
		} else if (lvalue.equals("reverse_portrait")) {
			this.orientationRequested = OrientationRequested.REVERSE_PORTRAIT;
		}
	}

	public File getOutputFile() {
		return outputFile;
	}

	private void setOutputFile(String outputFile) {

		this.outputFile = new File(outputFile);
	}
}
