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
 * ExplortXmlDataToFileDoit.java
 * 北京汇智互联版权所有
 */
package com.wisii.component.createinitview.openView;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wisii.component.setting.WisiiBean;
import com.wisii.edit.util.EditUtil;
import com.wisii.edit.util.EngineUtil;
import com.wisii.fov.render.awt.viewer.Command;

/**
 * 类功能说明：
 * 作者：zhangqiang
 * 日期:2016-4-10
 */
public class ExplortXmlDataToFileDoit extends Command
{
	public ExplortXmlDataToFileDoit(String name, String iconName)
	{
		super(name, iconName);
	}
	public void action(ActionEvent e)
	{
		String xmlstr = EditUtil.getXml();
		if (xmlstr == null || xmlstr.isEmpty())
		{
			WisiiBean wisiibean = EngineUtil.getEnginepanel().getWisiibean();
			if (wisiibean != null)
			{
				xmlstr = wisiibean.getXmlString();
			}
			if (xmlstr == null || xmlstr.isEmpty())
			{
				JOptionPane.showMessageDialog(EngineUtil.getEnginepanel(),
						"无可保存XML内容");
				return;
			}
		}
		Profile profile = new Profile();// 每次运行程序时创建配置文件Profile对象
		String latestPath = (profile.read() ? profile.latestPath : "C:/");// 读取配置文件里的参数Profile并赋值给latestPath
		JFileChooser filechosser = new JFileChooser(latestPath);
		// 创建文件过滤
		FileFilter filter = new FileNameExtensionFilter("xml文件(*.xml)", "xml");
		filechosser.setFileFilter(filter); // 为文件对话框设置文件过滤器
		if (filechosser.showSaveDialog(EngineUtil.getEnginepanel()) == JFileChooser.APPROVE_OPTION)
		{
			File savefile = filechosser.getSelectedFile();
			FileOutputStream out = null;
			try
			{
				String fname = filechosser.getName(savefile);	//从文件名输入框中获取文件名
				
				//假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
				if(fname.indexOf(".xml")==-1){
					savefile=new File(filechosser.getCurrentDirectory(),fname+".xml");
				}

				out = new FileOutputStream(savefile);
				out.write(xmlstr.getBytes("UTF-8"));
				latestPath = savefile.getParent();//每次退出文件选择器后更新目录Properties
				profile.write(latestPath);
				JOptionPane.showMessageDialog(EngineUtil.getEnginepanel(),
						"导出XML文件成功");
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(EngineUtil.getEnginepanel(),
						"导出XML文件失败" + ex.getMessage());
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
class Profile {
	// 设置默认的最新路径
	String latestPath = "C:/";
	// 在当前工程目录下创建setLatestPath.properties配置文件
	File file = new File("./setXMLLatestPath.properties");

	public Profile() {
	}

	boolean create() {
		boolean flag = true;
		if (file != null) {
			File directory = file.getParentFile(); // 获得文件的父目录
			if (!directory.exists()) { // 父目录不存在时
				flag = directory.mkdirs(); // 创建父目录
			} else { // 存在目录
				if (!file.exists()) {// 配置文件不存在时
					try {
						flag = file.createNewFile();// 创建配置文件
					} catch (IOException e) {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 读取属性文件中最新打开文件的目录
	 * 
	 * @return
	 */
	public boolean read() {
		Properties properties; // 声明属性集
		FileInputStream inputStream = null; // 声明文件输入流
		boolean b = true; // 声明boolean返回值
		if (!file.exists()) { // 配置文件不存在时
			b = create(); // 调用create()方法创建一个配置文件
			if (b) { // 配置文件创建成功后
				b = write(latestPath);// 调用write（）将latestPath写入配置文件
			} else {
				// 创建失败即不存在配置文件时弹出对话框提示错误
				JOptionPane.showConfirmDialog(null, "对不起，不存在配置文件！", "错误", JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			try {
				inputStream = new FileInputStream(file);
				properties = new Properties();
				properties.load(inputStream);// 读取属性
				latestPath = properties.getProperty("latestPath");// 读取配置参数latestPath的值
				inputStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				b = false;
			}
		}
		return b;
	}

	/**
	 * 将最新打开文件的目录保存到属性文件中
	 * 
	 * @param latestPath
	 * @return
	 */
	public boolean write(String latestPath) {
		this.latestPath = latestPath;
		Properties properties = null;
		FileOutputStream outputStream = null;
		boolean flag = true;
		try {
			outputStream = new FileOutputStream(file);
			properties = new Properties();
			properties.setProperty("latestPath", latestPath);
			properties.store(outputStream, null); // 将属性写入
			outputStream.flush();
			outputStream.close();
		} catch (IOException ioe) {
			flag = false;
			ioe.printStackTrace();
		}
		return flag;
	}
}
