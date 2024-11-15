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
 */package com.wisii.component.createinitview.openView;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.validate.validatexml.SchemaDTDXml;
import com.wisii.edit.EditStatusControl;
import com.wisii.edit.data.MaintainData;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.render.awt.viewer.Command;

public class WholeValidateActionDoit extends Command
 {

	public WholeValidateActionDoit(String name, String iconName) {
		super(name, iconName);

	}

	public void action(ActionEvent e) {

		// 判断是否有错误域
		if (WdemsTagManager.Instance.hasWrongValidationComponents()) {
			if (!dealWrongValidateComponent())
				return;
		}
		// 判断是否有整体验证

		if (!EditStatusControl.isWholeValidated()) {
			if (!wholeValidateHandle()) {
				return;
			}
		}
		StatusbarMessageHelper.output("整体验证成功", "",
				StatusbarMessageHelper.LEVEL.INFO);

	}

	/**
	 * 判断是否有验证错误的对象
	 * 
	 * @param com
	 *            当前主界面的panel
	 * @return 返回false的时候则证明没有错误项或者用户取消了修改错误项，返回true则证明用户放弃了更改。
	 */
	protected boolean dealWrongValidateComponent() {

		if (WdemsTagManager.Instance.hasWrongValidationComponents()/* true */) {
			JOptionPane jop = new JOptionPane("是否丢弃错误项",
					JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION);

			JDialog dialog = jop.createDialog(EngineUtil.getEnginepanel(),
					"有验证错误");

			dialog.setVisible(true);

			Object selectValue = jop.getValue();

			if (selectValue == null)
				return false;

			if (selectValue instanceof Integer) {
				Integer sv = (Integer) selectValue;
				if (sv == JOptionPane.YES_OPTION) {
					WdemsTagManager.Instance.setAllWrongValComBackIniValue();
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	protected boolean wholeValidateHandle() {
		WisiiBean wisiibean = EngineUtil.getEnginepanel().getWisiibean();
		String xsdstring = wisiibean.getXsdString();
		if (xsdstring == null) {
			return true;
		}

		try {
			boolean ss = SchemaDTDXml.checkXml(MaintainData.Xquery("/"),
					xsdstring);
			if (!ss) {
				JOptionPane.showMessageDialog(null, "整体验证失败", "请注意",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			EditStatusControl.wholeValidated();
			return true;

		} catch (Exception e) {

			return false;

		}

	}

}
