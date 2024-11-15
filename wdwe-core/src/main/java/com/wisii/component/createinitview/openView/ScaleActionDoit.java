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
 * ScaleActionDoit.java
 * 北京汇智互联版权所有
 */
package com.wisii.component.createinitview.openView;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.render.awt.viewer.Command;
import com.wisii.fov.render.awt.viewer.PreviewPanel;

/**
 * 类功能说明：
 *
 * 作者：zhangqiang
 * 日期:2013-1-8
 */
public class ScaleActionDoit extends Command {


	public ScaleActionDoit(String name, String iconName) {
		super(name);
	}


	/**
	 * 如果没有备份文件就不回滚，如果有备份文件回滚，将回滚前的文件生成回滚文件，将xml写库之后触发重载
	 */
	public void action(ActionEvent e) {
		JComboBox _scale=(JComboBox) e.getSource();
		int index = _scale.getSelectedIndex();
		if (index == 0) {
			setScaleToFitWindow();
		} else if (index == 1) {
			setScaleToFitWidth();
		} else {
			String item = ((String) _scale.getSelectedItem()).trim();
			setZoom(item); // 设置显示比例

		}
	}
	/** 设置全屏显示 */
	private void setScaleToFitWindow() {
		PreviewPanel _previewPanel=EngineUtil.getEnginepanel().getPreviewPanel();
		
		if (_previewPanel != null) {
			Container root = EngineUtil.getEnginepanel().getRootPane().getParent();
			// 获取屏幕的DPI显示
			double dpi = Toolkit.getDefaultToolkit().getScreenResolution(); // dpi
			double scale = SystemUtil.DEFAULT_TARGET_RESOLUTION / dpi; // 72.0/96.0;

			double oo = _previewPanel.getScaleToFit((root.getWidth()
					- SystemUtil.SPACE_WIDTH - 2 * SystemUtil.BORDER_SPACING)
					* scale, (root.getHeight()
					- SystemUtil.SPACE_HEIGHT - 2 * SystemUtil.BORDER_SPACING)
					* scale) ;
			_previewPanel.setScaleFactor(oo);

		}
	}
	/** 设置页宽显示 */
	private void setScaleToFitWidth() {
		PreviewPanel _previewPanel = EngineUtil.getEnginepanel()
				.getPreviewPanel();
		if (_previewPanel != null) {
			// 获取屏幕的DPI显示
			double dpi = Toolkit.getDefaultToolkit().getScreenResolution(); // dpi
			double scale = SystemUtil.DEFAULT_TARGET_RESOLUTION / dpi; // 72.0/96.0;
			Container root = EngineUtil.getEnginepanel().getRootPane().getParent();
			_previewPanel
					.setScaleFactor(_previewPanel.getScaleToFit(
							(root.getWidth() - /*
													 * SystemUtil.SPACE_LEFT_RIGHT
													 */SystemUtil.SPACE_WIDTH - 2 * SystemUtil.BORDER_SPACING)
									* scale, Double.MAX_VALUE));

		}
	}
	private void setZoom(final String setPercent) {
		PreviewPanel _previewPanel = EngineUtil.getEnginepanel()
				.getPreviewPanel();
		if (setPercent == null || setPercent.trim().length() == 0) {
			_previewPanel.setScaleFactor(1); // 默认显示比例：100%
		} else {
			String percent = setPercent.trim();
			int indexofPercent = percent.indexOf('%');
			if (indexofPercent == -1) {
				indexofPercent = percent.length();
			}

			double dPercent = 0.0;
			try {
				dPercent = Double.parseDouble(percent.substring(0,
						indexofPercent));
				if (dPercent < SystemUtil.MIN_PERCENT) { // 最小显示比例：20%
					dPercent = SystemUtil.MIN_PERCENT;
				} else if (dPercent > SystemUtil.MAX_PERCENT) { // 最大显示比例：500%
					dPercent = SystemUtil.MAX_PERCENT;
				}
				_previewPanel.setScaleFactor(dPercent/100);
				
			} catch (Exception e) { // 异常情况，使用默认显示比例
				_previewPanel.setScaleFactor(1); // 默认显示比例：100%
			}
		}
	}
}
