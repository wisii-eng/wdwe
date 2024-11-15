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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;

import com.wisii.component.startUp.SystemUtil;
import com.wisii.image.ImageInfo;
import com.wisii.image.io.ImageReaderFactory;

//import net.coobird.thumbnailator.Thumbnails;
//import net.coobird.thumbnailator.Thumbnails.Builder;

@SuppressWarnings("serial")
public class GraphicUploadDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField imgPathTextField;
	private WiseSpinner imgWidthText;
	private WiseSpinner imgHeightText;
	private String picturePath = null;
	private int imgWidth = 1;
	private int imgHeight = 1;
	private ImageIcon icon;
	private BufferedImage img;
	private GraphicCom graphicCom;
	private ImageIcon oldIcon;
	private BufferedImage oldImg;
	private ImageInfo imageInfo;
	private double dpiHorizontal;
	private double dpiVertical;

	private double mmWidth = 0.2;
	private double mmHeight = 0.2;

	private double oldWidth = 0.0;
	private double oldHeight = 0.0;

	private String latestPath;
	private Profile profile;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			GraphicUploadDialog dialog = new GraphicUploadDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public GraphicUploadDialog() {
		setBounds(100, 100, 640, 480);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		final JPanel viewPanel = new JPanel();
		scrollPane.setViewportView(viewPanel);
		viewPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 255)), "\u9884\u89C8", TitledBorder.LEFT,
				TitledBorder.ABOVE_TOP, null, null));
		scrollPane.setBounds(10, 70, 450, 350);
		contentPanel.add(scrollPane);

		final JPanel btnPanel = new JPanel();
		btnPanel.setBounds(10, 10, 604, 50);
		contentPanel.add(btnPanel);

		imgPathTextField = new JTextField();
		imgPathTextField.setHorizontalAlignment(SwingConstants.LEFT);
		imgPathTextField.setColumns(35);
		btnPanel.add(imgPathTextField);

		profile = new Profile();// 每次运行程序时创建配置文件Profile对象
		latestPath = (profile.read() ? profile.latestPath : "C:/");// 读取配置文件里的参数Profile并赋值给latestPath
		final JLabel imgLb = new JLabel(icon);
		JButton imgSelectButton = new JButton("选择图片");
		imgSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser(latestPath); // 创建文件对话框
				// accessory 通常用于显示已选中文件的预览图像
				fileChooser.setAccessory(new ImagePreviewer(fileChooser));
				// 创建文件过滤
				FileFilter filter = new FileNameExtensionFilter("图像文件(*.gif;*.jpg;*.jpeg;*.png)", "gif", "jpg", "jpeg",
						"png");
				fileChooser.setFileFilter(filter); // 为文件对话框设置文件过滤器
				
				int returnValue = fileChooser.showOpenDialog(null);// 打开文件选择对话框
				if (returnValue == JFileChooser.APPROVE_OPTION) { // 判断是否选择了文件
					File file = fileChooser.getSelectedFile(); // 获得文件对象
					String filesize = SystemUtil.getConfByName("base.imagesize");
					double imagesize = Double.parseDouble(filesize);
					if (file.length() / 1024.0 > imagesize) {
						JOptionPane.showMessageDialog(null, "请选择小于等于" + filesize + "KB的图片文件。");
						return;
					}
					picturePath = file.getAbsolutePath();
					imgPathTextField.setText(picturePath);
					latestPath = file.getParent();//每次退出文件选择器后更新目录Properties
					profile.write(latestPath);

					FileInputStream fin = null;
					BufferedInputStream bin = null;
					try {
						fin = new FileInputStream(picturePath);
						bin = new BufferedInputStream(fin);
						imageInfo = ImageReaderFactory.make(bin);
						oldImg = ImageIO.read(new FileInputStream(picturePath));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						IOUtils.closeQuietly(fin);
						IOUtils.closeQuietly(bin);
					}
					icon = new ImageIcon(imageInfo.getImage());
					oldIcon = new ImageIcon(oldImg);
					int iconWidth = imageInfo.getWidth();
					int iconHeight = imageInfo.getHeight();
//					try {
//						img = Thumbnails.of(picturePath).size(icon.getIconWidth(), icon.getIconHeight())
//								.asBufferedImage();
//					} catch (IOException e2) {
//						e2.printStackTrace();
//					}
					setImg(img);
					Dimension size = new Dimension(iconWidth, iconHeight);
					setImgWidth(iconWidth);
					setImgHeight(iconHeight);
					dpiHorizontal = imageInfo.getDpiHorizontal();// 水平分辨率
					dpiVertical = imageInfo.getDpiVertical();// 垂直分辨率
					mmWidth = iconWidth / dpiHorizontal * 25.4;
					mmHeight = iconHeight / dpiVertical * 25.4;
					oldWidth = mmWidth;
					oldHeight = mmHeight;
					imgWidthText.setValue(mmWidth);
					imgHeightText.setValue(mmHeight);
					imgLb.setIcon(icon);
					imgLb.setSize(size);
					imgLb.setOpaque(true);
					viewPanel.add(imgLb);
					viewPanel.updateUI();
				}
			}
		});
		
		btnPanel.add(imgSelectButton);

		JButton imgUploadButton = new JButton("上传");
		imgUploadButton.addActionListener(new GraphicUploadAction("upload", null, this));
		btnPanel.add(imgUploadButton);

		JPanel changePanel = new JPanel();
		changePanel.setLayout(null);
		changePanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 255)), "\u56FE\u7247\u8C03\u6574",
				TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
		changePanel.setBounds(470, 70, 145, 350);
		contentPanel.add(changePanel);

		final JCheckBox checkBox = new JCheckBox("");
		checkBox.setBounds(6, 31, 21, 21);
		changePanel.add(checkBox);

		JLabel label = new JLabel("锁定纵横比");
		label.setBounds(30, 31, 70, 21);
		changePanel.add(label);

		JLabel labe2 = new JLabel("宽mm");
		labe2.setBounds(10, 60, 40, 23);
		changePanel.add(labe2);

		imgWidthText = new WiseSpinner(new SpinnerNumberModel(getMmWidth(), 0.2, 1000000.0, 0.5));
		imgWidthText.setBounds(52, 60, 86, 21);

		imgWidthText.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				WiseSpinner source = (WiseSpinner) e.getSource();
//				int tempw = getImgWidth();
//				int temph = getImgHeight();
				double tempmmw = getMmWidth();
				double newMmWidth = (Double) source.getValue();
				if (oldWidth == newMmWidth) {
					return;
				}
				int newImgWidth = (int) (newMmWidth * 1.0 / 25.4 * dpiHorizontal);
				setImgWidth(newImgWidth);
				setMmWidth(newMmWidth);
				boolean selected = checkBox.isSelected();
				BufferedImage zoomImage = null;
				// if (tempw == imageInfo.getWidth()) {
				// setImg(ImageUtil.toBufferedImage(imageInfo.getImage()));
				// } else {
				if (selected) {
					double s = newMmWidth / tempmmw;
//					Builder<BufferedImage> keepAspectRatio = Thumbnails.of(getImg()).scale(s);
//					try {
//						zoomImage = keepAspectRatio.asBufferedImage();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
					setImgHeight(zoomImage.getHeight());
					double dh = (getImgHeight() * 1.0) / dpiVertical * 25.4;
					setMmHeight(dh);
					// imgHeightText.setValue(dh);
				} else {
//					Builder<BufferedImage> keepAspectRatio = Thumbnails.of(getImg()).size(newImgWidth, getImgHeight())
//							.keepAspectRatio(false);
//					try {
//						zoomImage = keepAspectRatio.asBufferedImage();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
				}
				ImageIcon newimageIcon = new ImageIcon(zoomImage);
				setIcon(newimageIcon);
				setImg(zoomImage);
				imgLb.setIcon(newimageIcon);
			}
			// }
		});
		changePanel.add(imgWidthText);

		JLabel labe3 = new JLabel("高mm");
		labe3.setBounds(10, 85, 40, 23);
		changePanel.add(labe3);

		imgHeightText = new WiseSpinner(new SpinnerNumberModel(getMmHeight(), 0.2, 1000000.0, 0.5));
		imgHeightText.setBounds(52, 85, 86, 21);
		imgHeightText.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				WiseSpinner source = (WiseSpinner) e.getSource();
//				int tempw = getImgWidth();
//				int temph = getImgHeight();
				double tempmmh = getMmHeight();
				double newMmHeight = (Double) source.getValue();
				if (oldHeight == newMmHeight) {
					return;
				}
				int newImgHeight = (int) (newMmHeight * 1.0 / 25.4 * dpiVertical);
				setImgHeight(newImgHeight);
				setMmHeight(newMmHeight);
				boolean selected = checkBox.isSelected();
				BufferedImage zoomImage = null;
				// if (temph == imageInfo.getHeight()) {
				// setImg(ImageUtil.toBufferedImage(imageInfo.getImage()));
				// } else {
				if (selected) {
					double s = newMmHeight / tempmmh;
//					Builder<BufferedImage> keepAspectRatio = Thumbnails.of(getImg()).scale(s);
//					try {
//						zoomImage = keepAspectRatio.asBufferedImage();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
					setImgWidth(zoomImage.getWidth());
					double dw = (getImgWidth() * 1.0) / dpiHorizontal * 25.4;
					setMmWidth(dw);
					// imgWidthText.setValue(dw);
				} else {
//					Builder<BufferedImage> keepAspectRatio = Thumbnails.of(getImg()).size(getImgWidth(), newImgHeight)
//							.keepAspectRatio(false);
//					try {
//						zoomImage = keepAspectRatio.asBufferedImage();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
				}
				ImageIcon newimageIcon = new ImageIcon(zoomImage);
				setIcon(newimageIcon);
				setImg(zoomImage);
				imgLb.setIcon(newimageIcon);
			}

			// }
		});
		changePanel.add(imgHeightText);

		JButton btnOk = new JButton("确定");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnOk.setBounds(6, 125, 62, 23);
		changePanel.add(btnOk);

		JButton btnCancel = new JButton("重置");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					imgWidthText.setValue(oldWidth);
					imgHeightText.setValue(oldHeight);
					setImgWidth(oldImg.getWidth());
					setImgHeight(oldImg.getHeight());
					setMmWidth(oldWidth);
					setMmHeight(oldHeight);
					setImg(oldImg);
					imgLb.setIcon(oldIcon);
					imgLb.setOpaque(true);
					viewPanel.updateUI();
				} catch (Exception e1) {
				}
			}
		});
		btnCancel.setBounds(71, 125, 62, 23);
		changePanel.add(btnCancel);
		
		imgLb.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new ViewImage(icon, GraphicUploadDialog.this);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				imgLb.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				imgLb.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		
		imgPathTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String filesize = SystemUtil.getConfByName("base.imagesize");
				double imagesize = Double.parseDouble(filesize);
				File file = new File(imgPathTextField.getText());
				if(!file.exists()){
					return;
				}
				if (file.length() / 1024.0 > imagesize) {
					JOptionPane.showMessageDialog(null, "请选择小于等于" + filesize + "KB的图片文件。");
					return;
				}
				picturePath = file.getAbsolutePath();
				imgPathTextField.setText(picturePath);

				FileInputStream fin = null;
				BufferedInputStream bin = null;
				try {
					fin = new FileInputStream(picturePath);
					bin = new BufferedInputStream(fin);
					imageInfo = ImageReaderFactory.make(bin);
					oldImg = ImageIO.read(new FileInputStream(picturePath));
				} catch(Exception e1){
					return;
				}finally {
					IOUtils.closeQuietly(fin);
					IOUtils.closeQuietly(bin);
				}
				icon = new ImageIcon(imageInfo.getImage());
				oldIcon = new ImageIcon(oldImg);
				int iconWidth = imageInfo.getWidth();
				int iconHeight = imageInfo.getHeight();
//				try {
//					img = Thumbnails.of(picturePath).size(icon.getIconWidth(), icon.getIconHeight())
//							.asBufferedImage();
//				} catch (IOException e2) {
//					e2.printStackTrace();
//				}
				setImg(img);
				Dimension size = new Dimension(iconWidth, iconHeight);
				setImgWidth(iconWidth);
				setImgHeight(iconHeight);
				dpiHorizontal = imageInfo.getDpiHorizontal();// 水平分辨率
				dpiVertical = imageInfo.getDpiVertical();// 垂直分辨率
				mmWidth = iconWidth / dpiHorizontal * 25.4;
				mmHeight = iconHeight / dpiVertical * 25.4;
				oldWidth = mmWidth;
				oldHeight = mmHeight;
				imgWidthText.setValue(mmWidth);
				imgHeightText.setValue(mmHeight);
				imgLb.setIcon(icon);
				imgLb.setSize(size);
				imgLb.setOpaque(true);
				viewPanel.add(imgLb);
				viewPanel.updateUI();
				
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				profile.write(latestPath);// 每次退出程序时把最后一次打开的目录写入到配置文件
			}
		});
		
	}

	public GraphicUploadDialog(GraphicCom graphicCom) {
		this();
		this.graphicCom = graphicCom;
	}

	public GraphicCom getGraphicCom() {
		return graphicCom;
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public double getMmWidth() {
		return mmWidth;
	}

	public void setMmWidth(double mmWidth) {
		this.mmWidth = mmWidth;
	}

	public double getMmHeight() {
		return mmHeight;
	}

	public void setMmHeight(double mmHeight) {
		this.mmHeight = mmHeight;
	}

}

@SuppressWarnings("serial")
class ImagePreviewer extends JLabel {
	public ImagePreviewer(JFileChooser chooser) {
		setPreferredSize(new Dimension(100, 100));
		setBorder(BorderFactory.createEtchedBorder());
		chooser.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
					File f = (File) event.getNewValue();
					if (f == null) {
						setIcon(null);
						return;
					}
					ImageIcon icon = new ImageIcon(f.getPath());
					// if(icon.getIconWidth()>getWidth()){
					icon = new ImageIcon(icon.getImage().getScaledInstance(getWidth(), -1, Image.SCALE_DEFAULT));
					// }
					setIcon(icon);
				}
			}
		});
	}
}

@SuppressWarnings("serial")
class ViewImage extends JDialog {
	private ImageIcon icon2;
	JScrollPane sc;
	JLabel lb;

	public ViewImage(ImageIcon icon, Component c) {
		this.icon2 = icon;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		lb = new JLabel(icon2);
		sc = new JScrollPane(lb);
		this.setModal(true);
		this.getContentPane().add(sc);
		this.setSize(800, 600);
		this.setLocationRelativeTo(c);
		this.setVisible(true);
	}

}

class Profile {
	// 设置默认的最新路径
	String latestPath = "C:/";
	// 在当前工程目录下创建setLatestPath.properties配置文件
	File file = new File("./setImgLatestPath.properties");

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