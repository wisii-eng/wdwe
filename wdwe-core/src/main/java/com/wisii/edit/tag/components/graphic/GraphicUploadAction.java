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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.wisii.component.mainFramework.commun.CommincateFactory;
import com.wisii.component.mainFramework.commun.WdemsDateType;
import com.wisii.component.setting.WisiiBean;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.render.awt.viewer.Command;

public class GraphicUploadAction extends Command {

	private GraphicUploadDialog updialog;

	public GraphicUploadAction(String name, Icon icon, GraphicUploadDialog updialog) {
		super(name, icon);
		this.updialog = updialog;
	}

	@Override
	public void action(ActionEvent e) {
		getDialog().hide();
		// GraphicProgressDialog dialog = new GraphicProgressDialog();
		// dialog.setLocationRelativeTo(EngineUtil.getEnginepanel());
		// dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// dialog.setResizable(false);
		// dialog.setVisible(true);

		WisiiBean wisiibean = EngineUtil.getEnginepanel().getWisiibean();
//		int width = updialog.getImgWidth();
//		int height = updialog.getImgHeight();
		BufferedImage bufferImage =null;
		String filename = null;
		try {
			bufferImage = updialog.getImg();
			String picturePath = updialog.getPicturePath();
			filename = picturePath.substring(picturePath.lastIndexOf(File.separator) + 1);
//			Image image = updialog.getImg();
//			Graphics g = bufferImage.getGraphics();// 获得绘图上下文对象
//			g.drawImage(image, 0, 0, width, height, null);// 在缓冲图像上绘制图像
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "请先选择要上传的图片！");// 显示提示信息
			getDialog().show();
			return;
		}
		String add = wisiibean.getGraphicurl();
		
		if (add == null || "null".equalsIgnoreCase(add)) {
			add = System.getProperty("user.dir") + File.separator + "graphics";

			
			String fileExtName = filename.substring(filename.indexOf(".") + 1);// 文件扩展名,不含点
			String pathAndname = add + File.separator + filename;
			File upfile = new File(pathAndname);
			if (!upfile.exists()) {// 判断文件是否存在
				try {
					upfile.createNewFile(); // 创建文件
					ImageIO.write(bufferImage, fileExtName, upfile);// 将缓冲图像保存到磁盘
					updialog.getGraphicCom().setValue(filename);// 更新EnginePanel里图片域的值
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "上传成功！");
						}
					});
					StatusbarMessageHelper.output("上传成功", wisiibean.getDocID(), StatusbarMessageHelper.LEVEL.INFO);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "上传失败\n" + e1.getMessage());// 显示提示信息
				}
			} else {
				int result = JOptionPane.showConfirmDialog(null, "文件" + filename + "已存在,点<是>覆盖上传,点<否>自动重命名上传", "警告",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					try {
						ImageIO.write(bufferImage, fileExtName, upfile);
						updialog.getGraphicCom().setValue(filename);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, "上传成功！");
							}
						});
						StatusbarMessageHelper.output("上传成功", wisiibean.getDocID(), StatusbarMessageHelper.LEVEL.INFO);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "上传失败\n" + e1.getMessage());// 显示提示信息
					}
				} else if (result == JOptionPane.NO_OPTION) {
					try {
						String pathAndname2 = add + File.separator + filename.substring(0, filename.indexOf("."))
								+ System.currentTimeMillis() + "." + fileExtName;
						String filename2 = pathAndname2.substring(pathAndname2.lastIndexOf(File.separator) + 1);
						File newFile = new File(pathAndname2);
						if (!newFile.exists()) {
							upfile.createNewFile();
						}
						ImageIO.write(bufferImage, fileExtName, newFile);
						updialog.getGraphicCom().setValue(filename2);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, "上传成功！");
							}
						});
						StatusbarMessageHelper.output("上传成功", wisiibean.getDocID(), StatusbarMessageHelper.LEVEL.INFO);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "上传失败\n" + e1.getMessage());// 显示提示信息
					}
				}

			}
			return;
		}
		add = SystemUtil.getURLDecoderdecode(add);
		WdemsDateType out = null;
		Map<String, Object> sas = new HashMap();
		sas.put("graphic", new ImageIcon(bufferImage));
		String picturePath = updialog.getPicturePath();
		filename = picturePath.substring(picturePath.lastIndexOf(File.separator) + 1);
		sas.put("graphicname", filename);
//System.out.println("GraphicUploadAction.action()"+add);
		if (add.length() > 1) {
			if (add.startsWith("http")) {
				out = CommincateFactory.makeComm(add).send(SystemUtil.SER_SUBMITXML, sas);
			} else
				out = CommincateFactory.makeComm(CommincateFactory.serverUrl + add).send(SystemUtil.SER_SUBMITXML, sas);
		} else {
			out = CommincateFactory.makeComm(CommincateFactory.serverUrl + CommincateFactory.requestUrl).send(
					SystemUtil.SER_SUBMITXML, sas);
		}
		updialog.getGraphicCom().setValue(filename);// 更新EnginePanel里图片域的值
		Object ss = out.getReturnDateType();
		if (ss instanceof LinkedList) {
			if (ss != null && ((LinkedList) ss).size() == 1) {
				StatusbarMessageHelper.output("上传过程中出现错误，上传未成功", ((LinkedList) ss).poll().toString(),
						StatusbarMessageHelper.LEVEL.INFO);
				return;
			}
		} else if (ss != null && !"".equalsIgnoreCase(ss.toString()) && "rename".equals(ss.toString().substring(0, 6))) {
			String outpath = ss.toString().substring(6);
			updialog.getGraphicCom().setValue(outpath);// 更新EnginePanel里图片域的值
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "上传成功！");
				}
			});
			StatusbarMessageHelper.output("上传成功", wisiibean.getDocID(), StatusbarMessageHelper.LEVEL.INFO);
			return;
		} else if (ss != null && !"".equalsIgnoreCase(ss.toString()) && !"sucess".equals(ss.toString())) {
			StatusbarMessageHelper.output("上传过程中出现错误，上传未成功", ss.toString(), StatusbarMessageHelper.LEVEL.INFO);
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "上传成功！");
			}
		});
		StatusbarMessageHelper.output("上传成功", wisiibean.getDocID(), StatusbarMessageHelper.LEVEL.INFO);
		getDialog().dispose();
	}

	public GraphicUploadDialog getDialog() {
		return updialog;
	}
}
