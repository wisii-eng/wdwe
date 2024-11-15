package com.wisii;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.wisii.component.setting.WisiiBean;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.util.WDWEUtil;

public class TestGenerate {
	
	@Test
	public void pdf(){
	    Path xslPath = Paths.get("src/test/resources","hello.xsl");
	    Path xmlPath = Paths.get("src/test/resources","hello.xml");
	    Path outPath = Paths.get("target/hello.pdf").toAbsolutePath();
		WisiiBean bean = new WisiiBean();
		try {
			bean.setOutputMode(MimeConstants.MIME_PDF);
			bean.setOutputfilename(outPath.toString());
			bean.setXslFile(xslPath.toFile());
			bean.setXmlFile(xmlPath.toFile());
			WDWEUtil.renderTo(bean);
			System.out.println("文档生成位置: " + outPath.toAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void pdf2(){
		WisiiBean bean = new WisiiBean();
		try {
			bean.setOutputMode(MimeConstants.MIME_PDF);
			File outfile = new File("d:/test/hello.pdf");
			bean.setOutputfilename(outfile.getAbsolutePath());
			bean.setXsl("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xsl:stylesheet xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:wisii=\"http://www.wisii.com/wisii\" version=\"1.0\"><xsl:output method=\"xml\" encoding=\"UTF-8\" indent=\"no\" version=\"1.0\"/><xsl:template match=\"/\"><fo:root><fo:layout-master-set><fo:simple-page-master master-name=\"946347538\" page-height=\"29.7cm\" page-width=\"21cm\" margin-top=\"15mm\" margin-bottom=\"17.5mm\" margin-left=\"21.7mm\" margin-right=\"21.7mm\"><fo:region-body margin-top=\"10.4mm\" margin-bottom=\"7.9mm\" margin-left=\"10mm\" margin-right=\"10mm\" overflow=\"hidden\" writing-mode=\"lr-tb\"></fo:region-body></fo:simple-page-master></fo:layout-master-set><fo:page-sequence master-reference=\"946347538\" font-family=\"'宋体'\" font-size=\"12pt\" white-space-treatment=\"preserve\" white-space-collapse=\"false\"><fo:flow flow-name=\"xsl-region-body\"><fo:block white-space-treatment=\"preserve\" color=\"rgb(0,0,0,255,0)\" line-height=\"14.4pt\" white-space-collapse=\"false\"><fo:inline font-size=\"16pt\">你好，</fo:inline><xsl:variable name=\"content3\" select=\"root/name\"/><fo:inline font-size=\"16pt\"><xsl:value-of select=\"$content3\"/></fo:inline></fo:block></fo:flow></fo:page-sequence></fo:root></xsl:template></xsl:stylesheet>");
			bean.setXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><name>WiseDoc WebEngine</name></root>");
			WDWEUtil.renderTo(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
