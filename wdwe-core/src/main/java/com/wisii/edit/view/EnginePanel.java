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
 * EnginePanel.java
 * 北京汇智互联版权所有
 */
package com.wisii.edit.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.wisii.component.createinitview.MessageListener;
import com.wisii.component.createinitview.listener.ReceiveDateToAWT;
import com.wisii.component.mainFramework.commun.CommCommunicate;
import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.MutiDataBean;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.components.decorative.WdemsGlassPane;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.render.awt.EditorRenderer;
import com.wisii.fov.render.awt.viewer.PreviewPanel;
import com.wisii.fov.render.awt.viewer.StatusListener;
import com.wisii.fov.render.awt.viewer.Translator;
import com.wisii.fov.render.java2d.Java2DRenderer;
import com.wisii.fov.util.WDWEUtil;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-6
 */
public class EnginePanel extends JPanel implements StatusListener{
	private WisiiBean wisiibean;
	private WiseToolBar toolbar;
	private StatusBar statusbar;
	private MessageListener messagelistener = new AskMainPanel();
	private Java2DRenderer renderer;
	private PreviewPanel previewPanel; // 显示页面的panel 。
	WdemsGlassPane glasspane;
	public EnginePanel(WisiiBean wisiibean) {
		this.wisiibean = wisiibean;
		super.setLayout(new BorderLayout());
		init();
	}

	private void init() {
		if(renderer==null)
		{
			renderer=new EditorRenderer(this);
			renderer.getUserAgent().setRendererOverride(renderer);
		}
		else
		{
			renderer.clearViewportList();
		}
		initscale();
		initStatusBar();
		initToolBar();
		initEdit();
	}
    private void initscale()
 {
		double percent = 100d;
		if (wisiibean != null) {
			percent = wisiibean.getPercent();
			if (percent <= 0) {
				try {
					percent = Double.parseDouble(SystemUtil
							.getConfByName("base.percent"));
				} catch (NumberFormatException e) {
					percent = 100d;
				}

			}
		} else {
			try {
				percent = Double.parseDouble(SystemUtil
						.getConfByName("base.percent"));
			} catch (NumberFormatException e) {
				percent = 100d;
			}
		}

		if (percent > 0) {
			if (percent > SystemUtil.MAX_PERCENT) {
				percent = SystemUtil.MAX_PERCENT;
			} else if (percent < SystemUtil.MIN_PERCENT) {
				percent = SystemUtil.MIN_PERCENT;
			}

		} else {
			percent = 100d;
		}
		if (renderer != null) {
			renderer.setScaleFactor(percent / 100);
		}
	}
    /*
     * 初始化工具栏
     */
    private void initToolBar()
 {

		String newsettingid = wisiibean.getSettingId();
		// 如果已经初始化，不再初始化
		if (toolbar != null) {
			String oldsetingid = toolbar.getSettingid();
			if ((oldsetingid == newsettingid)
					|| (oldsetingid != null && oldsetingid.equals(newsettingid))) {
				return;
			}
		}
		toolbar = new WiseToolBar(newsettingid, renderer.getScaleFactor());
		String _toolbarlocation = toolbar.getToolBarLocation();
		// 设置ToolBar的位置
		if (_toolbarlocation.equalsIgnoreCase("left")) {
			toolbar.setOrientation(SwingConstants.VERTICAL);
			add(toolbar, BorderLayout.WEST);
		} else if (_toolbarlocation.equalsIgnoreCase("right")) {
			toolbar.setOrientation(SwingConstants.VERTICAL);
			add(toolbar, BorderLayout.EAST);
		} else if (_toolbarlocation.equalsIgnoreCase("hide")) {
		} else {
			add(toolbar, BorderLayout.NORTH);
		}
	}
/*
 * 初始化状态栏
 */
	private void initStatusBar() {
		if (statusbar == null) {
			statusbar = new StatusBar();
			add(statusbar, BorderLayout.SOUTH);
		}
	}
	/*
	 * 初始化编辑相关
	 */
	public void initEdit() 
	{
		
		renderer.getUserAgent().setWisiibean(wisiibean);
		if(previewPanel==null){
		previewPanel = new PreviewPanel(renderer);
		}
		else
		{
			previewPanel.init(renderer);
		}
	}

	public void setWisiibean(WisiiBean wisiibean) {
		if (wisiibean == null) {
			return;
		}
		if (wisiibean != this.wisiibean) {
			this.wisiibean = wisiibean;
            init();
		}
	}
	public WiseToolBar getToolbar() {
		return toolbar;
	}

	public StatusBar getStatusbar() {
		return statusbar;
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		// 覆写设置布局方法，防止调用者设置布局
		return;
	}
	public  void openGlass()
	{
		toolbar.refreshState();
		if(glasspane==null)
		{
			glasspane=new WdemsGlassPane();
		}
		remove(previewPanel);
		add(glasspane,BorderLayout.CENTER);
		repaint();
	}
	public  void closeGlass()
	{
		remove(glasspane);
		add(previewPanel,BorderLayout.CENTER);
		updateUI();
		toolbar.refreshState();
	}
	private class AskMainPanel implements MessageListener {

		public boolean buttonContral(String buttonName, boolean isButtonActive) {
			// TODO： 控制按钮的显示及隐藏
			return false;
		}

		public boolean refresh() {
			wisiibean.setXml(EditUtil.getXml());
			doreLayout();
			return false;
		}

		/**
		 * 用于询问用户
		 * 
		 * @param msg
		 *            要询问的信息
		 * @return 选择是 return true,选择否 ，return false
		 */
		public boolean askIfItIs(String msg) {
			int result = JOptionPane.NO_OPTION;

			result = JOptionPane.showConfirmDialog(
					EnginePanel.this, msg, "请确认",
					JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) // 点击是
			{

				return true;

			} else // 点击否
			{
				return false;
			}

		}

	}

	/**
	 * @return the mainPanel
	 */
	public MessageListener getMessageListener() {
		return messagelistener;
	}
	private class ReLayout extends Thread
	{
		public void run() {
			doreLayout();
		}
	}

	public  void doreLayout() {
		if (wisiibean.getFoString() == null) {
			if (wisiibean.getXmlString() == null) {
				StatusbarMessageHelper.output("数据没有读取到", "",
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}
			if (wisiibean.getXslString() == null) {
				StatusbarMessageHelper.output("模板数据没有读取到", "",
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}

			if (!wisiibean.getXmlString().startsWith("<?xml")) {
				StatusbarMessageHelper.output("数据格式错误", "",
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}
			if (!wisiibean.getXslString().startsWith("<?xml")) {
				StatusbarMessageHelper.output("模板格式错误", "",
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}
		}
		// render.startRenderer(outputStream)
		try {
			renderTo();
		} catch (FOVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Generate a document, given an initialized Fov object
	 * 
	 * @param userAgent
	 *            the user agent
	 * @param outputFormat
	 *            the output format to generate (MIME type, see MimeConstants)
	 * @param out
	 *            the output stream to write the generated output to (may be
	 *            null if not applicable)
	 * @throws FOVException
	 *             in case of an error during processing
	 */
	private void renderTo() throws FOVException {
		openGlass();
		int _currentPage = previewPanel.getPage();
		renderer.setCurrentPageNumber(_currentPage); // 设置重新render之前，显示的页面的页码
		renderer.clearViewportList();
		previewPanel.reInint();
		WDWEUtil.renderTo(renderer, wisiibean);
		if(_currentPage > 0) {
			goToPage(_currentPage + 1 );
		}
		
	}
	public void reRender(){
		int _currentPage = previewPanel.getPage();
		renderer.setCurrentPageNumber(_currentPage); // 设置重新render之前，显示的页面的页码
		renderer.clearViewportList();
		previewPanel.reInint();
		// 发送数据
				Map<String, WisiiBean> map = new HashMap<String, WisiiBean>(); // 是否显示背景发送给服务器

				map.put("para", wisiibean);

				ReceiveDateToAWT	awt = new ReceiveDateToAWT((EditorRenderer) renderer);
				CommCommunicate communproxy =new CommCommunicate();
				Object retu = communproxy.send(SystemUtil.SER_AREATREEOBJ, map);
				communproxy.receiveData(retu, awt);

				try {
					while (!renderer.isRenderingDone()) {
						Thread.sleep(100);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	@Override
	public void notifyPageRendered() {
		SwingUtilities.invokeLater(new ShowInfo());
	}

	/** 通知applet，已经获取当前显示的页面的PageViewport数据 */
	public void notifyCurrentPageRendered(int pageindex) {
		MutiDataBean mdb = wisiibean.getMdb();
		if (mdb != null) {
			mdb.addPartAndPages(pageindex);
			previewPanel.setCurrentPage(mdb.getNumber(mdb.getCurrPart(), mdb
					.getCurrPageNum()) - 1);
		}
		reload();

	}

	public  void reload() {
		statusbar.setProcessStatus(Translator.getInstanceof().getString("Status.Show"));
		previewPanel.reload();
	}

	@Override
	public void notifyRendererStopped() {
		MutiDataBean mdb = wisiibean.getMdb();
		if (mdb != null) {
			mdb.addPartAndPages(renderer.getNumberOfPages());
			previewPanel.setCurrentPage(mdb.getNumber(mdb.getCurrPart(), mdb
					.getCurrPageNum()) - 1);
            Translator translator=Translator.getInstanceof();
			if (!wisiibean.getMdb().isIsfind()) {
				String message = translator.getString("Status.Page") + " "
						+ mdb.getEndPages(mdb.getCurrPart()) + " "
						+ translator.getString("Status.of") + " "
						+ (mdb.getTotalPageCount());
				statusbar.setInfoStatus(message);
				goToPage(mdb.getSpPage());
				return;
			}
		}
		if (renderer.getNumberOfPages() == 0)
		{
			statusbar.setProcessStatus("无可用页");
		    return;
		}
		notifyPageRendered(); 
		closeGlass();
		StatusbarMessageHelper.output("欢迎使用WDEMS", "",
				StatusbarMessageHelper.LEVEL.INFO);
		
	}
	private class ShowInfo implements Runnable {
		public void run() {
			int page = 0;
			MutiDataBean mdb = wisiibean.getMdb();
			if (mdb == null) {
				if ((previewPanel.getPage() + 1) > renderer
						.getNumberOfPages()) {
					page = renderer.getNumberOfPages();
				} else
					page = previewPanel.getPage() + 1;
			} else {
				page = mdb.getCurrPageNum();
			}
			Translator translator=Translator.getInstanceof();
		
			String message = translator.getString("Status.Page") + " "
					+ (page) + " " + translator.getString("Status.of") + " "
					+ (getTotalPages());
			statusbar.setInfoStatus(message);
		}
	}
	public int getTotalPages() {

		MutiDataBean mdb = wisiibean.getMdb();
		if (mdb == null) {
			return renderer.getNumberOfPages();
		} else {
				return mdb.getTotalPageCount() != 0 ? mdb.getTotalPageCount()
					: renderer.getNumberOfPages();
		}
	}
	/**
	 * go到那一页
	 * 
	 * @param number
	 *            the page number to go to
	 */
	public void goToPage(int number) {
        if(number<1||number>getTotalPages())
        {
        	return;
        }
        
		// 设置预期页数
		MutiDataBean mdb = wisiibean.getMdb();
		int isE = -2;
		if (mdb != null) {
			mdb.setSpPage(number);
			/* 【计算请求的页在不在当前的缓存里面】 */
			isE = wisiibean.getMdb().isExist(number);
		}

		if (isE > -1) {
			// 如果在缓存里就直接显示
			if (isE != previewPanel.getPage()) {
				previewPanel.setPage(isE);

			}
			mdb.setCurrPageNum(number);
			notifyPageRendered();

		} else if (isE == -2) {
			int innerpage=number-1;
			if (innerpage!= previewPanel.getPage()) {
				previewPanel.setPage(innerpage);
				notifyPageRendered();
			}
		} else {
			mdb.setIsfind(false);
			// 如果不在缓存里，就要知道具体份数
			int parts = mdb.getParts(number);
			mdb.setCurrPart(parts);
			mdb.setCurrPageNum(number);
			// 请求服务端
			mutiDataSend(parts);

		}

	}
	/**
	 * 在大数据处理的时候用于多次触发
	 * 
	 * @param part
	 *            请求的份数
	 */
	private void mutiDataSend(int parts) {
		sendV(parts, wisiibean);
		new ReLayout().start();
	}
	private void sendV(int parts, WisiiBean wisiibean) {
		int c = wisiibean.getMdb().getEndPages(
				wisiibean.getMdb().getCurrPart() - 1);

		String url = SystemUtil.servletUrl(wisiibean.getXmlFileName()
				+ "&userPara=" + wisiibean.getUserPara() + "&part=" + parts
				+ "&totalPage=" + wisiibean.getMdb().getTotalPageCount()
				+ "&endPage=" + c, null);
		WdemsDateType irs = CommincateFactory.makeComm(url).send("");
		try {
			Object maw = irs.getReturnDateType();

			if (maw instanceof Map) {
				Map ss = (Map) maw;

				wisiibean.setXml((String) ss.get(WisiiBean.XML));

			} else if (maw instanceof WisiiBean) {

				wisiibean.setXml(((WisiiBean) maw).getXmlString());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public WisiiBean getWisiibean() {
		return wisiibean;
	}
	public void setScale(double scale)
	{
		renderer.setScaleFactor(scale/100);
	}
	public double getScale()
	{
		return renderer.getScaleFactor()*100;
	}

	public PreviewPanel getPreviewPanel() {
		return previewPanel;
	}
	public FOUserAgent getFOUserAgent()
	{
		return renderer.getUserAgent();
	}

	public Java2DRenderer getRenderer() {
		return renderer;
	}
	public void start()
	{
		doreLayout();
	}
	
}
