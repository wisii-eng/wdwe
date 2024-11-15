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
 * @HardWareInfoGetFrame.java
 * 汇智互联版权所有，未经许可，不得使用
 */

package com.wisii.fov.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.startUp.SystemUtil;


/**
 * 类功能描述：
 * 
 * 作者：zhangqiang 创建日期：2009-8-5
 */
public class HardWareInfoGet extends JDialog {
	private JTextField pathtf = new JTextField(30);
	JButton generatorbutton = new JButton("产生");
	private JCheckBox regC = new JCheckBox("客户端注册");
	private JCheckBox regS = new JCheckBox("服务端注册");
	private JButton pathbutton = new JButton("选择");
	private String clientpath="wdwe_client_HWI.txt";
	private String serverpath="wdwe_server_HWI.txt";
	
	public HardWareInfoGet() {
		pathbutton.setEnabled(false);
		setResizable(false);
		setLayout(new BorderLayout());
		generatorbutton.setEnabled(false);
		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		regC.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pathbutton.setEnabled(regC.isSelected() || regS.isSelected());

			}
		});
		regS.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pathbutton.setEnabled(regC.isSelected() || regS.isSelected());

			}
		});
		cbPanel.add(regC);
		cbPanel.add(regS);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		JPanel filesetPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		filesetPanel.add(pathtf);
		pathtf.addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				String text = pathtf.getText();
				if (text == null || text.trim().equals("")) {
					generatorbutton.setEnabled(false);
				} else {
					generatorbutton.setEnabled(true);
				}

			}
		});

		pathbutton.addActionListener(new ActionListener() {
			// 【刘晓注掉20090828】
			// @Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showSaveDialog(HardWareInfoGet.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					pathtf.setText(file.getAbsolutePath());
					generatorbutton.setEnabled(true);
				}
			}
		});
		filesetPanel.add(pathbutton);

		p.add(cbPanel);
		p.add(filesetPanel);

		add(p, BorderLayout.CENTER);
		generatorbutton.addActionListener(new ActionListener() {

			// 【刘晓注掉20090828】
			// @Override
			public void actionPerformed(ActionEvent e) {
				boolean issavesucessfull =false;
				StringBuilder sb=new StringBuilder();
				sb.append("产生成功");
				//如果单机版，则只出客户端
				if (CommincateFactory.serverUrl == null||(CommincateFactory.serverUrl != null&&regC.isSelected())) {
					 issavesucessfull = HardwareInfoSave.save(pathtf
							.getText().trim()+"\\"+clientpath, HardWareInfoGetter.getDiskID(),
							HardWareInfoGetter.getMac(), HardWareInfoGetter
									.getProcessorid());
					 sb.append("，客户端文件产生在"+pathtf.getText().trim()+"\\"+clientpath+'\n');
				} 
				else {
					if(regS.isSelected())
					{
					WdemsDateType input = CommincateFactory.makeComm(
							CommincateFactory.serverUrl
									+ CommincateFactory.requestUrl).send(
							SystemUtil.SER_GETHARDINFO, null);
					Map ss = (Map) input.getReturnDateType();
					 issavesucessfull = HardwareInfoSave.save(pathtf
							.getText().trim()+"\\"+serverpath, (String) ss.get("a"),
							(String) ss.get("b"), (String) ss.get("c"));
					 sb.append("，服务端文件产生在"+pathtf.getText().trim()+"\\"+serverpath+'\n');
					}
				}

				if (issavesucessfull) {
					
					
					JOptionPane.showMessageDialog(HardWareInfoGet.this, sb.toString());
					setVisible(Boolean.FALSE);
				} else {
					JOptionPane.showMessageDialog(HardWareInfoGet.this, "产生失败");
				}
				
			}
		});
		JButton exitbutton = new JButton("退出");
		exitbutton.addActionListener(new ActionListener() {

			// 【刘晓注掉20090828】
			// @Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(Boolean.FALSE);
			}
		});
		JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonpanel.add(generatorbutton);
		buttonpanel.add(exitbutton);
		add(buttonpanel, BorderLayout.SOUTH);
		Dimension size = getPreferredSize();
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		setLocation((screenSize.width - size.width) / 2,
				(screenSize.height - size.height) / 2);
		pack();
		setVisible(Boolean.TRUE);
	}

	public static void main(String[] args) {
		new HardWareInfoGet();
	}
}
