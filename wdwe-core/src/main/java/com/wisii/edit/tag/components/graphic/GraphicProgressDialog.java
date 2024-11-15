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
 */package com.wisii.edit.tag.components.graphic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicProgressDialog extends JFrame {

	private final JPanel contentPanel = new JPanel();
	Timer timer;
	final JButton btnNewButton;
	final JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			GraphicProgressDialog dialog = new GraphicProgressDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public GraphicProgressDialog() {
		setBounds(100, 100, 401, 90);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(null);
		{
			JPanel panel = new JPanel();
			final JLabel lblNewLabel = new JLabel();
			panel.setBounds(0, 0, 385, 51);
			contentPanel.add(panel);
			panel.setLayout(null);

			progressBar = new JProgressBar();
			progressBar.setOrientation(JProgressBar.HORIZONTAL);// 设置其方向为水平方向
			progressBar.setMinimum(0);// 最小刻度0
			progressBar.setMaximum(100);// 最大刻度100
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int value = progressBar.getValue();
					// /当进度条运行时，就将其进度显示在标签中
					if (e.getSource() == progressBar) {
						lblNewLabel.setText("目前已完成进度：" + Integer.toString(value) + " %");
					}
				}
			});
			progressBar.setBounds(0, 5, 385, 20);
			panel.add(progressBar);

			lblNewLabel.setBounds(63, 28, 257, 15);
			panel.add(lblNewLabel);

			btnNewButton = new JButton("New button");
			btnNewButton.addActionListener(new MyActionListener());
			timer = new Timer(50, new MyActionListener());// 创建一个事件组件对象
			btnNewButton.setBounds(282, 20, 93, 23);
			panel.add(btnNewButton);
		}
	}

	class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// /当单击按钮，则计时开始
			if (e.getSource() == btnNewButton) {
				timer.start();
			}
			// /当单击事件组件，则进度条开始变化
			if (e.getSource() == timer) {
				int value = progressBar.getValue();
				if (value < 100) {
					value++;// 进度条往前运动
					progressBar.setValue(value);
				} else {
					timer.stop();
					progressBar.setValue(0);
					GraphicProgressDialog.this.dispose();
				}
			}

		}

	}
}
