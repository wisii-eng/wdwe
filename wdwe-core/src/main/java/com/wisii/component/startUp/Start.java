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
 */package com.wisii.component.startUp;

import java.applet.AppletContext;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import com.wisii.AppletFrame;
import com.wisii.component.actionevent.ActionEventType;
import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.cache.database.hsql.HsqldbService;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.components.popupbrowser.WdemsPopupBrowserCom;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.view.EnginePanel;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.MimeConstants;
import com.wisii.fov.command.CMDAreaTreeObj;
import com.wisii.fov.render.RenderResult;
import com.wisii.fov.util.Sutil;
import com.wisii.fov.util.WDWEUtil;

public class Start extends JApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*-----线程池----*/
	private static ExecutorService service;
	/*-----守护线程----*/
	private Thread thread;
	private Object locked;
	/*-----判断是否跳页----*/
	private boolean isEnd = true;
	/*-----接受命令队列----*/
	private LinkedList<Object> commonQue;
	private String xmlStr = null;
	private String xslStr = null;
	private EnginePanel enginepanel;
	private static WdemsPopupBrowserCom wdemsBrowser;
	private static JSObject js;
	/**
	 * Constructor of the applet.
	 * 
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 */
	public Start()
	{
		super();
	}
	/**
	 * Called by the browser or applet viewer to inform this applet that it is
	 * being reclaimed and that it should destroy any resources that it has
	 * allocated. The <code>stop</code> method will always be called before
	 * <code>destroy</code>.
	 * <p>
	 * A subclass of <code>Applet</code> should override this method if it has
	 * any operation that it wants to perform before it is destroyed. For
	 * example, an applet with threads would use the <code>init</code> method to
	 * create the threads and the <code>destroy</code> method to kill them.
	 * <p>
	 */
	@Override
	public void destroy()
	{
		stop();
		WdemsTagManager.Instance.clearCurrentPageComponents();
		service.shutdown();
	}
	/**
	 * Returns information about this applet. An applet should override this
	 * method to return a <code>String</code> containing information about the
	 * author, version, and copyright of the applet.
	 * <p>
	 * 
	 * @return a string containing information about the author, version, and
	 *         copyright of the applet.
	 */
	@Override
	public String getAppletInfo()
	{
		return "This is startUP created by www.wisii.com.cn";
	}
	/**
	 * Called by the browser or applet viewer to inform this applet that it has
	 * been loaded into the system. It is always called before the first time
	 * that the <code>start</code> method is called.
	 * <p>
	 * A subclass of <code>Applet</code> should override this method if it has
	 * initialization to perform. For example, an applet with threads would use
	 * the <code>init</code> method to create the threads and the
	 * <code>destroy</code> method to kill them.
	 * <p>
	 */
	@Override
	public void init()
	{
		try
		{
			CommincateFactory.serverUrl = getParameter("serverurl");
		}
		catch (Exception e)
		{
		}
		clean();
		service = Executors.newFixedThreadPool(5);
		commonQue = new LinkedList();
//		js = JSObject.getWindow(this);
		// -------------end--------------------//
	}
	/**
	 * Called by the browser or applet viewer to inform this applet that it
	 * should start its execution. It is called after the <code>init</code>
	 * method and each time the applet is revisited in a Web page.
	 * <p>
	 * A subclass of <code>Applet</code> should override this method if it has
	 * any operation that it wants to perform each time the Web page containing
	 * it is visited. For example, an applet with animation might want to use
	 * the <code>start</code> method to resume animation, and the
	 * <code>stop</code> method to suspend the animation.
	 * <p>
	 */
	@Override
	public void start()
	{
		locked = null;
		thread = null;
		locked = new Object();
		// 开启守护线程
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				while (isEnd)
				{
					try
					{
						synchronized (locked)
						{
							locked.wait();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					if (isEnd)
					{
						// 读取队列
						while (commonQue.size() > 0)
						{
							Object obj = commonQue.poll();
							startExecute((WisiiBean) obj);
						}
					}
				}
			}
		});
		thread.start();
		ActionEvent(ActionEventType.init, getEnablePrintnames(), null);
		String testarg = getParameter("test");
		if (testarg != null && !testarg.isEmpty())
		{
			askWiApplet(testarg);
		}
	}
	private String getEnablePrintnames()
	{
		PrintService[] PRINTSERVICE = PrinterJob.lookupPrintServices();
		if (PRINTSERVICE == null || PRINTSERVICE.length == 0)
		{
			return null;
		}
		StringBuffer sb = new StringBuffer();
		String defprint = null;
		PrintService def = PrintServiceLookup.lookupDefaultPrintService();
		for (PrintService p : PRINTSERVICE)
		{
			if (p == def)
			{
				defprint = p.getName() + ";";
			}
			else
			{
				sb.append(p.getName() + ";");
			}
		}
		if (defprint != null)
		{
			sb.insert(0, defprint);
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	/**
	 * 单机版命令行方式直接调用 。
	 * 命令行如下：
	 * -xml,tuanxian.xml,-xsl,tuanxian.xsl,-awt[-print,-ps......]
	 */
	public static void main(String[] args)
	{
		WisiiBean wisiiBean = new WisiiBean();
		wisiiBean.parseOptions(args);
		if ((wisiiBean.getXmlString() == null || wisiiBean.getXslString() == null)
				&& wisiiBean.getFoString() == null)
		{
			System.out.println("文件未得到，请检查文件名和路径：");
			System.out.println(wisiiBean.getXmlFileName());
			System.out.println(wisiiBean.getXslFileName());
			return;
		}
		final Start s = new Start();
		if (wisiiBean.getOutputFormat().equals(
				MimeConstants.MIME_WISII_WDDE_PREVIEW)
				|| wisiiBean.getOutputFormat().equals(
						MimeConstants.MIME_WISII_AWT_PREVIEW))
		{
			AppletFrame appletF = new AppletFrame(s);
			appletF.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent arg0)
				{
					s.stop();
					s.destroy();
				}
			});
			appletF.setTitle("WDEMS PRODUCED BY WWW.WISII.COM");
			appletF.setSize(800, 500);
			appletF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			appletF.setVisible(true);
			s.startExecute(wisiiBean);
		}
		else
		{
			s.startExecute(wisiiBean);
		}
	}
	// ******************zmz add ****************************
	/**
	 * 实现和main方法一样的功能.
	 * 
	 * @param bean
	 *            参数bean里面至少要设置outputmode, xml数据和xsl数据
	 */
	public RenderResult startExecute(WisiiBean wisiiBean)
	{
		Object l = Sutil.getF("yuyu");
		long c = System.currentTimeMillis();
		long sc = Sutil.gc();
		if (l == null || c > (Long) l || sc > (Long) l)
		{
			AppletContext content = null;
			try
			{
				content = this.getAppletContext();
			}
			catch (Exception e)
			{
			}
			String mode = wisiiBean.getOutputFormat();
			if (content != null || MimeConstants.MIME_WISII_PRINT.equals(mode)
					|| MimeConstants.MIME_WISII_PRINTSEQUENCE.equals(mode))
			{
				int cf = JOptionPane.showConfirmDialog(Start.this,
						"无许可文件或许可已到期，继续将会打印背景水印，是否继续?", "许可问题",
						JOptionPane.YES_NO_OPTION);
				if (cf != JOptionPane.OK_OPTION)
				{
					return null;
				}
			}
			else if (content != null
					|| MimeConstants.MIME_WISII_WDDE_PREVIEW.equals(mode)
					|| MimeConstants.MIME_WISII_AWT_PREVIEW.equals(mode))
			{
			}
			else
			{
				System.out.println("调用Engine不成功，无许可或许可已到期");
				return null;
			}
		}
		String outmode = wisiiBean.getOutputFormat();
		if (MimeConstants.MIME_WISII_WDDE_PREVIEW.equals(outmode)
				|| MimeConstants.MIME_WISII_AWT_PREVIEW.equals(outmode))
		{
			if (enginepanel == null)
			{
				enginepanel = EngineUtil.getEnginePanel(wisiiBean);
				getContentPane().add(enginepanel);
			}
			else
			{
				enginepanel.setWisiibean(wisiiBean);
			}
			enginepanel.start();
			return null;
		}
		else if (MimeConstants.MIME_XSL_FO.equals(outmode)
				|| MimeConstants.MIME_RTF.equals(outmode))
		{
			File ff = new File(wisiiBean.getOutputfilename());
			OutputStream out;
			try
			{
				out = new java.io.BufferedOutputStream(
						new java.io.FileOutputStream(ff));
				Map fgfg = new HashMap();
				fgfg.put("para", wisiiBean);
				new CMDAreaTreeObj().execute(out, fgfg, null);
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("文件已生成：" + ff.getAbsolutePath());
			return null;
		}
		else
		{
			try
			{
				return WDWEUtil.renderTo(wisiiBean);
			}
			catch (FOVException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	// ********************zmz add end************************
	/**
	 * Called by the browser or applet viewer to inform this applet that it
	 * should stop its execution. It is called when the Web page that contains
	 * this applet has been replaced by another page, and also just before the
	 * applet is to be destroyed.
	 * <p>
	 * A subclass of <code>Applet</code> should override this method if it has
	 * any operation that it wants to perform each time the Web page containing
	 * it is no longer visible. For example, an applet with animation might want
	 * to use the <code>start</code> method to resume animation, and the
	 * <code>stop</code> method to suspend the animation.
	 * <p>
	 */
	@Override
	public void stop()
	{
		if (HsqldbService.isInit())
		{
			// 停止hsql服务
			HsqldbService dbService = HsqldbService.getInstance();
			dbService.stop();
		}
		// 关闭守护线程
		isEnd = false;
		EditStatusControl.RUNSTATUS = EditStatusControl.STATUS.READ;
		if (thread != null)
		{
			synchronized (locked)
			{
				locked.notify();
			}
		}
		thread = null;
		locked = null;
	}
	/**
	 * js调用响应事件
	 * 
	 * @throws FOVException
	 */
	public String askWiApplet(String para)
	{
		try
		{
			clean();
			// 字符串解编码
			String d = SystemUtil.getURLDecoderdecode(para);
			// 解析
			String[] parac = d.split(SystemUtil
					.getConfByName("base.devidegroup"));
			WisiiBean wb = new WisiiBean();
			if (!wb.parseOptions(parac))
				return "字符串传递错误：" + d;
			// 将任务添加至队列
			if (xmlStr != null && !xmlStr.isEmpty())
			{
				wb.setXml(xmlStr);
			}
			if (xslStr != null && !xslStr.isEmpty())
			{
				wb.setXsl(xslStr);
			}
			commonQue.add(wb);
			synchronized (locked)
			{
				locked.notify();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "start the Wisii Applet and receive commands";
	}
	/**
	 * 设置xml
	 * 
	 * @throws FOVException
	 */
	public void setXmlStr(String xml) throws FOVException
	{
		xmlStr = SystemUtil.getURLDecoderdecode(xml);
	}
	/**
	 * 设置xml
	 * 
	 * @throws FOVException
	 */
	public void setXslStr(String xsl) throws FOVException
	{
		xslStr = SystemUtil.getURLDecoderdecode(xsl);
	}
	/**
	 * @author liuxiao 用于事件机制中的回调
	 *         需要组装的参数包括
	 */
	public void ActionEvent(ActionEventType info, String para1,
			WisiiBean wisiibean)
	{
		String[] para = new String[4];
		para[1] = info.name();
		if (wisiibean != null)
		{
			para[0] = wisiibean.getDocID();
			para[3] = wisiibean.getUserPara();
			if (info == ActionEventType.afterPrintAction
					|| info == ActionEventType.afterPrintWithoutBackgroundAction)
			{
				para[2] = wisiibean.getPrintSetting().toString();
				if (para1 != null)
					para[2] += para1;
			}
			else
			{
				para[2] = para1;
			}
		}
		else
		{
			para[2] = para1;
		}
		if (info == ActionEventType.init)
		{
			Object l = Sutil.getF("yuyu");
			long c = System.currentTimeMillis();
			if (l == null || c > (Long) l)
			{
				para[3] = "false";
			}
			else
			{
				para[3] = "true";
			}
		}
		try
		{
			JSObject.getWindow(this).call("wiActionEvent", para);
		}
		catch (Exception e)
		{
		}
	}
	/**
	 * 该方法用于applet生命周期为结束其间发生的需要清楚换村的操作例如： 换文件 换文件包括： 换xml,换xslt，换xsd
	 * 等。在这里面进行清空操作
	 */
	private void clean()
	{
		EditStatusControl.init();
	}
	// 强制GC
	public void gc()
	{
		System.gc();
	}
	// 可用于判断是远程还是客户端
	public String getClientName()
	{
		return System.getenv("CLIENTNAME");
	}
	public String submitData()
	{
		EditUtil.saveXml();
		return EditUtil.getXml();
	}
	public void setPara(String parmStr)
	{
		EnginePanel enginepanel = EngineUtil.getEnginepanel();
		if (enginepanel != null)
		{
			WisiiBean wisiibean = enginepanel.getWisiibean();
			if (wisiibean != null)
			{
				String d = SystemUtil.getURLDecoderdecode(parmStr);
				// 解析
				String[] parac = d.split(SystemUtil
						.getConfByName("base.print.devideitem"));
				wisiibean.parseOptions(parac);
			}
		}
	}
	public static void callBrowser(WdemsPopupBrowserCom wb,String[] s){
		Start.wdemsBrowser = wb;
		js.call("openjsp", s);
	}
	public void setData(String s){
		Start.wdemsBrowser.setValue(s);
	}
}
