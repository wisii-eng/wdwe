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
 */package com.wisii.fov.fo.flow;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import org.xml.sax.Locator;
import com.wisii.fov.apps.FOVException;
import com.wisii.fov.apps.FOUserAgent;
import com.wisii.fov.fo.FONode;
import com.wisii.fov.fo.PropertyList;
import com.wisii.fov.fo.ValidationException;
import com.wisii.fov.image.FovImage;
import com.wisii.fov.image.ImageFactory;
import com.wisii.fov.render.java2d.XmlImageObj;
import com.wisii.fov.image.analyser.ImageReaderFactory;
import com.wisii.fov.fo.expr.NumericProperty;

/**
 * External graphic formatting object. This FO node handles the external
 * graphic. It creates an image inline area that can be added to the area tree.
 */
public class ExternalGraphic extends AbstractGraphics {
	// The value of properties relevant for fo:external-graphic.
	// All but one of the e-g properties are kept in AbstractGraphics
	private String src = "";

	// End of property values

	// Additional values
	private String url = "";

	private int intrinsicWidth = 0;

	private int intrinsicHeight = 0;

	/**
	 * add by lzy fo:external-graphic节点新增属性 aphla 图片的透明度 [0，255] src_type
	 * 图片数据来源类型 具体取值参见：FO规范中external-graphic节点扩充方案.doc iamgeByte[] 图片数据的字节数组
	 */
	private int aphla = 0;

	private String src_type = "";

	private byte iamgeByte[];

	// add end

	/**
	 * Create a new External graphic node.
	 * 
	 * @param parent
	 *            the parent of this node
	 */
	public ExternalGraphic(FONode parent) {
		super(parent);
	}

	/**
	 * @see com.wisii.fov.fo.FObj#bind(PropertyList)
	 */
	public void bind(PropertyList pList) throws FOVException {
		super.bind(pList);
		// src_type 取值 "",func-by-param,bin-data-str
		// 参见FO规范中external-graphic节点扩充方案.doc
		src_type = pList.get(PR_SRC_TYPE).getString();
		aphla = pList.get(PR_APHLA).getNumber().intValue();

		if ("func-by-param".equals(src_type)) {
			// Funcbind(pList);
		} else if ("bin-data-str".equals(src_type))// src_type = "bin-data-str"
		{
			// 扩展接口，节点内容为图片信息二进制值的某种封装形式的字符串
		} else {
			URLbind(pList);
		}

		if (!(aphla <= 255 && aphla >= 0)) {
			aphla = 0;
		}

		// TODO Report to caller so he can decide to throw an exception
	}

	// 原Fov的实现，图片是从URL中得到的
	private void URLbind(PropertyList pList) throws FOVException {
		src = pList.get(PR_SRC).getString();
		// Additional processing: preload image
		url = ImageFactory.getURL(getSrc());

		FOUserAgent userAgent = getUserAgent();
		ImageFactory fact = userAgent.getFactory().getImageFactory();
		FovImage fovimage = fact.getImage(url, userAgent);
		if (fovimage == null) {
			getLogger().error("Image not available: " + getSrc());
			src = "";
		} else {
			// load dimensions
			if (!fovimage.load(FovImage.DIMENSIONS)) {
				getLogger().error("Cannot read image dimensions: " + getSrc());
			}
			this.intrinsicWidth = fovimage.getIntrinsicWidth();
			this.intrinsicHeight = fovimage.getIntrinsicHeight();
		}
	}

	private void Funcbind(PropertyList pList, String Imagesrc)
			throws FOVException {
		src = pList.get(PR_SRC).getString();

		// 做为参数，调用刘晓的方法 得到一个图片数据。
		XmlImageObj imageObj = new XmlImageObj();
		imageObj.setName(src);
		imageObj.setStr(Imagesrc);

		if (!imageObj.tramsformImageXml()) {
			return;
		}

		String aphla = imageObj.getAlpha();
		Object returnObject = null;
		try {

			returnObject = imageObj.execute();

			int pas = Integer.parseInt(aphla);
			if (pas <= 255 && pas >= 0) {
				this.aphla = pas;
			}
		} catch (NumberFormatException ex) {
			this.aphla=0;
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
		
			e.printStackTrace();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
		
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		}

		// 使用完毕
		InputStream input = null;
		if (returnObject instanceof InputStream) {
			input = (InputStream) returnObject;
		} else {
			// new FOVException("图片必须是输入流的形式。");
		}

		if (input == null) {
			return;
			// throw new FOVException("图片流不能为null");
		}

		// double contentHeight = imageObj.getHigh() * 72 /25.4 * 1000;
		// double contentWidth = imageObj.getWide() * 72 /25.4 * 1000;
		// 取XML中的定义高度和宽度，单位mm
		double contentHeight = imageObj.getHigh();
		double contentWidth = imageObj.getWide();

		if (contentHeight != 0) {
			// 单位换算成pt
			super.setContentHeight(new NumericProperty(
					contentHeight * 72 / 25.4 * 1000, 1));
		}
		if (contentWidth != 0) {
			// 单位换算成pt
			super.setContentHeight(new NumericProperty(
					contentHeight * 72 / 25.4 * 1000, 1));
		}

		// 把InputStream转换成ByteArrayInputStream
		InputStream vArrayInputStream = null;
		try {
			byte[] bytes = new byte[1];

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (input.read(bytes) != -1) {
				baos.write(bytes);
			}
			baos.flush();
			baos.close();
			iamgeByte = baos.toByteArray();
			vArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		FovImage.ImageInfo imageInfo = ImageReaderFactory.make(url,
				vArrayInputStream, getUserAgent());

		this.intrinsicWidth = (int) (imageInfo.width * 72000 / imageInfo.dpiHorizontal); // 真实的宽度
																							// 单位:pt
		this.intrinsicHeight = (int) (imageInfo.height * 72000 / imageInfo.dpiVertical); // 真实的高度
																							// 单位:pt
	}

	/**
	 * See FONode类的addCharacters方法
	 */
	protected void addCharacters(char[] data, int start, int end,
			PropertyList pList, Locator locator) throws FOVException {
		// 解析fo:external-graphic节点的内容,data = fo:external-graphic节点的内容
		if ("func-by-param".equals(src_type)) {
			String fov_imagesrc = new String(data);
			Funcbind(pList, fov_imagesrc);
		}
	}

	/**
	 * @see com.wisii.fov.fo.FONode#startOfNode
	 */
	protected void startOfNode() throws FOVException {
		checkId(getId());
		getFOEventHandler().image(this);
	}

	/**
	 * @see com.wisii.fov.fo.FONode#validateChildNode(Locator, String, String)
	 *      XSL Content Model: empty
	 */
	protected void validateChildNode(Locator loc, String nsURI, String localName)
			throws ValidationException {
		invalidChildError(loc, nsURI, localName);
	}

	/**
	 * @return the "src" property.
	 */
	public String getSrc() {
		return src;
	}

	// add by lzy
	public int getAphla() {
		return aphla;
	}

	public String getSrc_type() {
		return src_type;
	}

	public byte[] getIamgeByte() {
		return iamgeByte;
	}

	// add end

	/**
	 * @return Get the resulting URL based on the src property.
	 */
	public String getURL() {
		return url;
	}

	/** @see com.wisii.fov.fo.FONode#getLocalName() */
	public String getLocalName() {
		return "external-graphic";
	}

	/**
	 * @see com.wisii.fov.fo.FObj#getNameId()
	 */
	public int getNameId() {
		return FO_EXTERNAL_GRAPHIC;
	}

	/**
	 * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicWidth()
	 */
	public int getIntrinsicWidth() {
		return this.intrinsicWidth;
	}

	/**
	 * @see com.wisii.fov.fo.flow.AbstractGraphics#getIntrinsicHeight()
	 */
	public int getIntrinsicHeight() {
		return this.intrinsicHeight;
	}

}
