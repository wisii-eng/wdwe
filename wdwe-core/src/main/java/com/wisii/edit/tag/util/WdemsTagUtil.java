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
 */package com.wisii.edit.tag.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wisii.Version;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.authority.Commision;
import com.wisii.edit.data.MaintainData;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsComponent;
import com.wisii.edit.tag.WdemsTagID;
import com.wisii.edit.tag.WdemsTagIDFactory;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.balloontip.BalloonTip;
import com.wisii.edit.tag.components.balloontip.styles.RoundedBalloonStyle;
import com.wisii.edit.tag.components.balloontip.utils.ToolTipUtils;
import com.wisii.edit.tag.schema.wdems.Connwith;
import com.wisii.edit.tag.schema.wdems.Validation.Para;
import com.wisii.edit.tag.xpath.ParseXPath;
import com.wisii.edit.tag.xpath.XPathNodes;
import com.wisii.edit.util.EngineUtil;
import com.wisii.edit.validator.BaseValidator;
import com.wisii.fov.apps.FOUserAgent;

/**
 * 有关标签的工具方法类
 * 
 * @author 闫舒寰
 * @version 1.0 2009/07/15
 */
public final class WdemsTagUtil
{
	protected static Log log = LogFactory.getLog(WdemsTagUtil.class);
	private WdemsTagUtil()
	{
	}
	/**
	 * 把String转化成InputStream
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream StringToInputStream(final String str)
	{
		byte[] bytes = null;
		try
		{
			bytes = str.getBytes(SystemUtil.FILE_CHARSET);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bytes != null)
			return new ByteArrayInputStream(bytes);
		else
			return null;
	}
	/**
	 * 根据xpath从数据库中读取出数据
	 * 
	 * @param xpath
	 * @return
	 */
	public static Object getValue(final String xpath)
	{
		if (xpath == null || xpath.equals(""))
		{
			// System.err.println("xpath maybe null:" + xpath);
			StatusbarMessageHelper.output("xpath maybe null", "xpath:" + xpath,
					StatusbarMessageHelper.LEVEL.INFO);
			return null;
			// throw new IllegalArgumentException("no xpath");
		}
		Object temp = null;
		try
		{
			String[] ss = MaintainData.queryValue(xpath);
			if (ss == null || ss.length == 0)
			{
				// System.err.println("xpath maybe wrong:" + xpath);
				StatusbarMessageHelper.output("xpath reslut is null:", "xpath:"
						+ xpath, StatusbarMessageHelper.LEVEL.INFO);
				// 这里有可能获得0长度的一个String数组，当遇到0长度的String数组则返回null
				return temp;
			}
			temp = ss[0];
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (temp == null)
		{
			// System.err.println("xpath maybe wrong:" + xpath);
			StatusbarMessageHelper.output("result is null", "xpath:" + xpath,
					StatusbarMessageHelper.LEVEL.INFO);
		}
		return temp;
	}
	/**
	 * 根据给定的xpath获得一组节点
	 * 
	 * @param xpath
	 * @return
	 */
	public static String[] getValues(final String xpath)
	{
		if (xpath == null || xpath.equals(""))
		{
			StatusbarMessageHelper.output("xpath maybe null", "xpath:" + xpath,
					StatusbarMessageHelper.LEVEL.INFO);
			return null;
		}
		Object temp = null;
		String[] ss = {};
		try
		{
			ss = MaintainData.queryValue(xpath);
			if (ss == null || ss.length == 0)
			{
				StatusbarMessageHelper.output("xpath reslut is null:", "xpath:"
						+ xpath, StatusbarMessageHelper.LEVEL.INFO);
				return ss;
			}
			temp = ss[0];
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		if (temp == null)
		{
			// System.err.println("xpath maybe wrong:" + xpath);
			StatusbarMessageHelper.output("result is null", "xpath:" + xpath,
					StatusbarMessageHelper.LEVEL.INFO);
		}
		return ss;
	}
	/**
	 * 分析xpath给出最终节点是属性节点还是正常节点
	 * 
	 * @param xpath
	 * @return
	 */
	public static String[] analysisXpath(final String xpath)
	{
		List<XPathNodes> xnList = ParseXPath.Instance.parseXPath(xpath);
		XPathNodes xn = xnList.get(xnList.size() - 1);
		StringBuilder last = new StringBuilder(xn.toString());
		if (last != null && !last.equals("") && last.length() > 0)
		{
			if (last.charAt(0) == '@')
			{
				last.deleteCharAt(0);
				return new String[] { "attribute", last.toString() };
			}
			else
				return new String[] { "text", last.toString() };
		}
		return null;
	}
	public enum ValidationType
	{
		onBlur, onEdit, onResult;
		private static Map<ValidationType, String> method = new HashMap<ValidationType, String>();
		static
		{
			method.put(onBlur, "getOnBlur");
			method.put(onEdit, "getOnEdit");
			method.put(onResult, "getOnResult");
		}
		public String getMethod(final ValidationType vType)
		{
			return method.get(vType);
		}
	}
	/**
	 * 对给定的方法进行验证
	 * 
	 * @param validationMethod
	 * @param action
	 * @return
	 */
	public static boolean doValidation(final ValidationType vType,
			final Actions action)
	{
		Object tagObj = action.getTagObject();
		// valitation name
		String vName = null;
		try
		{
			Method method = tagObj.getClass().getMethod(vType.getMethod(vType),
					null);
			Object o = method.invoke(tagObj, null);
			if (o instanceof String)
			{
				vName = (String) o;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (vName == null)
			return true;
		List<Boolean> bList = new ArrayList<Boolean>();
		// 这里需要做文字的拆分工作，要支持多个验证属性
		List<String> list = WdemsTagUtil.apartByComma(vName);
		// 这里需要分别对多个验证进行验证请求
		for (String valName : list)
		{
			// System.out.println("doing " + vType + " validation...." + " to "
			// + valName);
			Object temp = WdemsTagManager.Instance.getWdemsTags(valName);
			String vd = null;
			String validationMsg = null;
			com.wisii.edit.validator.Validation.Builder builder = null;
			if (temp instanceof com.wisii.edit.tag.schema.wdems.Validation)
			{
				com.wisii.edit.tag.schema.wdems.Validation val = (com.wisii.edit.tag.schema.wdems.Validation) temp;
				vd = val.getValidate();
				// 若验证器属性为空则不能创建验证器
				if (vd == null)
				{
					String s = "验证标签" + valName + "没有定义验证器。"
							+ "验证器属性是：validate";
					StatusbarMessageHelper.output("没有验证器", s,
							StatusbarMessageHelper.LEVEL.INFO);
					continue;
				}
				validationMsg = val.getMsg();
				builder = new com.wisii.edit.validator.Validation.Builder(vd,
						action.getXPath(), action.getValue().toString());
				builder.setMessage(validationMsg);
				List<Para> pList = val.getPara();
				for (Para para : pList)
				{
					builder.addPara(para.getXpath(), para.getContent());
				}
			}
			BaseValidator bv = builder.build();
			// 因为在上面builder中已经创建了List<para>所以这里传null。
			if (bv.validate(null))
			{
				bList.add(true);
				StatusbarMessageHelper.output("验证器：" + valName + " 验证成功",
						"doing " + vType + " validation " + "success ",
						StatusbarMessageHelper.LEVEL.INFO);
			}
			else
			{
				// FIXME 这里还需要衡量是否有一个验证不通过则不继续验证，直接返回错误信息
				action.setWrongMessage(bv.getError());
				bList.add(false);
				StatusbarMessageHelper.output(bv.getError(), "验证器：" + valName
						+ " 验证失败  " + '\n' + "doing " + vType + " validation "
						+ "failure", StatusbarMessageHelper.LEVEL.INFO);
			}
		}
		boolean result = false;
		// 如果发现全部验证正确则设置系统状态为正确的
		for (Boolean b : bList)
		{
			if (b)
			{
				result = true;
			}
			else
			{
				result = false;
				break;
			}
		}
		if (result)
		{
			// 通知系统验证状态
			// CreateInitView.EDITSTATUS = RUNSTATUS.READ_STATUS;
			action.setComponentValidationState(vType, true);
		}
		else
		{
			// CreateInitView.EDITSTATUS = RUNSTATUS.ERROR_STATUS;
			action.setComponentValidationState(vType, false);
		}
		// 需要先把当前进行的验证设置到action中
		action.setCurrentValidation(vType);
		// 让控件根据验证状态更新
		action.getTagComponent().showValidationState(action);
		if (result)
			return true;
		else
			return false;
	}
	/**
	 * 把string用','分割开
	 * 
	 * @param text
	 *            输入的是带','的字符串
	 * @return 输出的是分开好的内容
	 */
	public static List<String> apartByComma(final String text)
	{
		List<String> list = new ArrayList<String>();
		if (text == null || text.equals(""))
		{
			// 如果为空则什么也不做
		}
		else
		{
			String[] temp = text.split(",");
			for (String element : temp)
			{
				// System.out.println(element);
				list.add(element);
			}
		}
		return list;
	}
	/**
	 * 显示wdems的版本
	 * 
	 * @param com
	 *            当前主界面的panel
	 */
	public static void getWdemsVersion(final Component com)
	{
		// System.out.println(Version.getVersion());
		// System.out.println("=====================");
		// System.out.println(Version.getVersionDetial());
		// JOptionPane jop = new JOptionPane(Version.getVersion() +
		// Version.getVersionDetial(), JOptionPane.INFORMATION_MESSAGE,
		// JOptionPane.PLAIN_MESSAGE);
		// JDialog dialog = jop.createDialog(com, "关于 Wdems");
		// FIXME 这个applet不是继承自Frame，所以目前还不能设置Dialog的parent，这样就可以创建多个dialog
		final JDialog dialog = new JDialog(getFrame(com));
		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		dialog.add(new WdemsTagUtil.VersionPanel(dialog, Version.getVersion(),
				Version.getVersionDetial()), BorderLayout.CENTER);
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(com);
		dialog.setVisible(true);
	}
	private static Frame getFrame(final Component comp)
	{
		Window win = SwingUtilities.getWindowAncestor(comp);
		if (win instanceof Frame)
			return (Frame) win;
		return null;
	}
	// 版本菜单面板
	private static class VersionPanel extends JPanel
	{
		private final JTextArea textArea_1;
		private final JTextArea textArea;
		/**
		 * Create the panel
		 */
		public VersionPanel(final JDialog dialog, final String text1,
				final String text2)
		{
			super();
			setLayout(new BorderLayout());
			final JPanel panel = new JPanel();
			final FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			panel.setLayout(flowLayout);
			add(panel, BorderLayout.SOUTH);
			final JButton okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(final ActionEvent e)
				{
					dialog.setVisible(false);
				}
			});
			panel.add(okButton);
			final JPanel panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(Color.black, 1, false));
			final GridLayout gridLayout = new GridLayout(1, 2);
			gridLayout.setHgap(8);
			panel_1.setLayout(gridLayout);
			add(panel_1, BorderLayout.CENTER);
			textArea = new JTextArea();
			textArea.setText(text1);
			textArea.setEditable(false);
			panel_1.add(textArea);
			textArea_1 = new JTextArea();
			textArea_1.setText(text2);
			textArea_1.setEditable(false);
			panel_1.add(textArea_1);
			final JPanel panel_2 = new JPanel();
			panel_2.setLayout(new BorderLayout());
			add(panel_2, BorderLayout.NORTH);
			final JLabel label = new JLabel();
			label.setText("wisii 版本信息：");
			panel_2.add(label);
			//
		}
	}
	// private static Frame getFrame(final Component com) {
	// if (com instanceof Frame) {
	// Frame f = (Frame) com;
	// return f;
	// } else {
	// System.out.println(com.getClass());
	// return getFrame(com);
	// }
	// }
	/**
	 * 更新数据内容到数据库中
	 * 
	 * @param xpath
	 * @param value
	 * @return
	 */
	public static boolean updateXML(final String xpath, final Object value)
	{
		// 说明
		String s = "xpath:" + xpath + " value:" + value;
		if (xpath == null)
			return false;
		try
		{
			if (value == null)
			{
				// 可以更新值为空的元素，当值为空的时候则代表更新长度为0的String
				MaintainData.update(null, xpath, "", true);
			}
			else
			{
				MaintainData.update(null, xpath, value.toString(), true);
				StatusbarMessageHelper.output("更新数据:" + value.toString(), "",
						StatusbarMessageHelper.LEVEL.INFO);
			}
			StatusbarMessageHelper.output("更新数据成功", s,
					StatusbarMessageHelper.LEVEL.DEBUG);
		}
		catch (Exception e)
		{
			log.debug(e);
			e.printStackTrace();
			StatusbarMessageHelper.output("更新数据失败:" + value, "",
					StatusbarMessageHelper.LEVEL.INFO);
			// StatusbarMessageHelper.output("更新数据失败", s,
			// StatusbarMessageHelper.LEVEL.DEBUG);
			return false;
		}
		List<WdemsComponent> comList = WdemsTagManager.Instance
				.getWdemsComponentByXPath(xpath);
		for (WdemsComponent wc : comList)
		{
			// TODO 这个地方还需要在再次衡量一下，这样设置的时候只能保证界面控件显示的结果是当前xpath设置的值。
			// System.err.println("value:" + value);
			wc.getWdemsTagComponent().iniValue(value);
			// FIXME 这个地方需要设置控件的动态效果，每次更新数据之后都需要有动态的效果
			// wc.getWdemsTagComponent().getComponent().setBackground(Color.blue);
		}
		return true;
	}
	/**
	 * 根据关联名称取得关联对象。关联名称有可能是由逗号分隔的名称
	 * 
	 * @param connName
	 *            UI标签中的关联属性值
	 * @return 返回一组关联对象
	 */
	public static List<Connwith> getConnwithByName(final String connName)
	{
		List<Connwith> connList = new ArrayList<Connwith>();
		List<String> name = apartByComma(connName);
		for (String con : name)
		{
			Object conn = WdemsTagManager.Instance.getWdemsTags(con);
			if (conn instanceof Connwith)
			{
				Connwith connwith = (Connwith) conn;
				connList.add(connwith);
			}
		}
		return connList;
	}
	/**
	 * 根据提供的id来判断是否有权限编辑，如果id中有一处可编辑则返回true
	 * 
	 * @param id fo中id的值
	 * @return 返回true代表id中至少有一处有编辑权限，返回false代表id中没有一处可以进行编辑
	 */
	public static boolean hasAuthority(final String id, FOUserAgent userAgent)
	{
		List<WdemsTagID> wtList = WdemsTagIDFactory.Instance
				.parseWdemsTagID(id);
		for (WdemsTagID wtid : wtList)
		{
			String authrity = wtid.getAuthority();
			if (authrity == null)
				return true;
			else if (Commision.isCommision(authrity, userAgent))
				return true;
		}
		return false;
	}
	/**
	 * 目前特殊字符为backspace键和Delete键
	 * 
	 * @param ch
	 * @return 当传入的参数是特殊字符时返回true
	 */
	public static boolean hasSpecialChar(final char ch)
	{
		switch (ch)
		{
		case KeyEvent.VK_BACK_SPACE:
			return true;
		case KeyEvent.VK_DELETE:
			return true;
		default:
			return false;
		}
	}
	/**
	 * 判断当前公式中是否有设置控件是否为灰的关键词
	 * 
	 * @param expression
	 * @return
	 */
	public static boolean hasEnableFunction(final String expression)
	{
		// 设置控件可用
		String en = "wdemsF_j_setEnable";
		// 设置控件不可用
		String disEn = "wdemsF_j_setDisable";
		if (expression.startsWith(en))
		{
			return true;
		}
		if (expression.startsWith(disEn))
		{
			return true;
		}
		return false;
	}
	/**
	 * 从给定xpath中读取文件，并转化成相应的字符集，最后返回一个StringBuilder
	 * 
	 * @param path
	 * @param charSet
	 * @return
	 */
	public static StringBuilder readXmlFile(final String path,
			final String charSet)
	{
		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		File f = new File(path);
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(f);
			Reader reader = new InputStreamReader(fis, charSet);
			BufferedReader br = new BufferedReader(reader);
			String strLine;
			while ((strLine = br.readLine()) != null)
			{
				sb.append(strLine);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return sb;
	}
	/**
	 * 以泡泡形式显示控件的提示信息
	 * 
	 * @param tag
	 * @param wtc
	 */
	public static BalloonTip getHintBalloon(final Object tag,
			final WdemsComponent wc)
	{
		try
		{
			String hintType = (String) tag.getClass()
					.getMethod("getHintType", null).invoke(tag, null);
			String hint = (String) tag.getClass().getMethod("getHint", null)
					.invoke(tag, null);
			if (hintType != null && hint != null)
			{
				final BalloonTip bt = new BalloonTip(wc.getWdemsTagComponent()
						.getComponent(), hint, new RoundedBalloonStyle(5, 5,
						Color.WHITE, Color.BLACK), false);
				bt.enableClickToHide(true);
				ToolTipUtils.balloonToToolTip(bt, 500, 3000);
				if (hintType.equals("showWhenOpen"))
				{
					bt.setVisible(true);
				}
				// CustomBalloonTip cbt = new
				// 鼠标进入显示，出去则不显示
				// wtc.getComponent().addMouseListener(new MouseListener() {
				//
				// public void mouseReleased(final MouseEvent e) {}
				//
				// public void mousePressed(final MouseEvent e) {}
				//
				// public void mouseExited(final MouseEvent e) {
				// bt.setVisible(false);
				// }
				//
				// public void mouseEntered(final MouseEvent e) {
				// bt.setVisible(true);
				// }
				// public void mouseClicked(final MouseEvent e) {}
				// });
				// 垂直方向跟着滚动
				wc.getWdemsTagComponent().getComponent()
						.addAncestorListener(new AncestorListener()
						{
							public void ancestorRemoved(
									final AncestorEvent event)
							{
							}
							public void ancestorMoved(final AncestorEvent event)
							{
								bt.refreshLocation();
							}
							public void ancestorAdded(final AncestorEvent event)
							{
							}
						});
				return bt;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	// add by 李晓光 2010-8-6
	private final static Map<JComponent, BalloonTip> allHints = new LinkedHashMap<JComponent, BalloonTip>();
	private final static void addBalloonTip(BalloonTip tip)
	{
		allHints.put(tip.getAttachedComponent(), tip);
	}
	public final static BalloonTip getBalloonTip(JComponent component)
	{
		return allHints.get(component);
	}
	public final static void closeBalloonTip(JComponent component)
	{
		BalloonTip tip = getBalloonTip(component);
		if (tip != null)
		{
			tip.closeBalloon();
			remove(component);
		}
	}
	public final static void remove(JComponent component)
	{
		allHints.remove(component);
	}
	public final static void clear()
	{
		allHints.clear();
	}
	// add by 李晓光 2010-8-6
	/**
	 * （相对getHintBalloon，方法来说）新版本的以泡泡形式显示提示信息
	 * 
	 * @param wtc
	 * @param tooltip
	 */
	public static void configTooltip(final WdemsComponent wc)
	{
		// 进入编辑模式后控件消失的时间
		final int DISAPPEAR_TIME = 5000;
		String tooltip = null;
		try
		{
			tooltip = (String) wc.getTagObject().getClass()
					.getMethod("getHint", null).invoke(wc.getTagObject(), null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (tooltip == null)
		{
			return;
		}
		WdemsTagComponent wtc = wc.getWdemsTagComponent();
		// 控件为空是当有按钮情况下控件为空，而不是JComponent目前是为了逻辑上补全
		if (wtc.getComponent() == null
				|| !(wtc.getComponent() instanceof JComponent))
		{
			return;
		}
		// Java的标准的tooltip，目前用泡泡了则不用Java标准的tooltip
		// wtc.getComponent().setToolTipText(tooltip);
		// 弹出式泡泡的配置方式
		final BalloonTip bt = new BalloonTip(wtc.getComponent(), tooltip,
				new RoundedBalloonStyle(5, 5, Color.WHITE, Color.BLACK), false);
		// add by 李晓光 2010-8-6
		addBalloonTip(bt);
		// add by 李晓光 2010-8-6
		bt.setVisible(true);
		bt.enableClickToHide(true);
		// ToolTipUtils.balloonToToolTip(bt, 200, 3000);
		// 启动X秒后自动消失
		Timer sTimer = new Timer(DISAPPEAR_TIME, new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				bt.setVisible(false);
			}
		});
		sTimer.setRepeats(false);
		sTimer.start();
		// 下面是当鼠标进入控件的时候，所设置快捷键激发弹出式泡泡
		JComponent acc = bt.getAttachedComponent();
		ActionMap actionMap = acc.getActionMap();
		KeyStroke show = KeyStroke.getKeyStroke(KeyEvent.VK_UP,
				InputEvent.ALT_MASK);
		KeyStroke hide = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
				InputEvent.ALT_MASK);
		actionMap.put("show", WdemsTagUtil.getBalloonAction(bt, true));
		actionMap.put("hide", WdemsTagUtil.getBalloonAction(bt, false));
		acc.getInputMap().put(show, "show");
		acc.getInputMap().put(hide, "hide");
		// 为鼠标进入控件显示弹出式泡泡添加事件
		acc.addMouseListener(new MouseListener()
		{
			private BalloonTip balloonTip;
			private Timer showTimer;
			// 鼠标移出时不显示弹出式泡泡
			public void mouseExited(final MouseEvent e)
			{
				// 设置泡泡弹出后多长时间消失
				// this.balloonTip = bt;
				// showTimer = new Timer(2000, new ActionListener() {
				// public void actionPerformed(final ActionEvent e) {
				// balloonTip.setVisible(false);
				// }
				// });
				// showTimer.setRepeats(false);
				// showTimer.start();
				bt.setVisible(false);
			}
			// 鼠标进入时重新计算泡泡的位置，之后显示弹出式泡泡
			public void mouseEntered(final MouseEvent e)
			{
				bt.refreshLocation();
				bt.setVisible(true);
			}
			public void mouseClicked(final MouseEvent e)
			{
			}
			public void mouseReleased(final MouseEvent e)
			{
			}
			public void mousePressed(final MouseEvent e)
			{
			}
		});
		// 设置泡泡跟着主控件移动
		acc.addComponentListener(new ComponentListener()
		{
			public void componentShown(final ComponentEvent e)
			{
			}
			public void componentResized(final ComponentEvent e)
			{
				if (bt.isVisible())
				{
					bt.refreshLocation();
				}
			}
			public void componentMoved(final ComponentEvent e)
			{
				if (bt.isVisible())
				{
					bt.refreshLocation();
				}
			}
			public void componentHidden(final ComponentEvent e)
			{
				if (bt.isVisible())
				{
					bt.setVisible(false);
				}
			}
		});
	}
	// 产生快捷键动作的方法
	private static Action getBalloonAction(final BalloonTip bt,
			final Boolean show)
	{
		Action act = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				bt.setVisible(show);
			}
		};
		return act;
	}
	// just for test
	public static void main(final String[] args)
	{
		// String s = "this";
		// System.out.println(WdemsTagUtil.apartByComma(s));
		// test: dealWrongValidateComponent
		final JFrame jf = new JFrame();
		jf.setSize(400, 400);
		JButton jb = new JButton();
		jb.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				getWdemsVersion(jf);
			}
		});
		jf.add(jb);
		jf.setVisible(true);
	}
}
