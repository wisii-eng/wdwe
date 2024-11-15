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
 * @AbstractWdemsCombxo.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import com.wisii.component.validate.validatexml.SchemaObj;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.WdemsTagManager;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.decorative.WdemsCascadeManager;
import com.wisii.edit.tag.components.select.datasource.DataSource;
import com.wisii.edit.tag.components.select.datasource.ParseDataBuilder;
import com.wisii.edit.tag.components.select.datasource.SwingDataSource;
import com.wisii.edit.tag.components.select.datasource.TableOrViewInfo;
import com.wisii.edit.tag.schema.wdems.Select;
import com.wisii.edit.tag.util.ComponentStyleUtil;
import com.wisii.edit.tag.util.LocationUtil;

/**
 * 类功能描述：用于创建下拉控件，且下拉的Pop-Panel是表格形式
 * 
 * 作者：李晓光 创建日期：2009-6-12
 */
@SuppressWarnings({ "serial", "unchecked" })
public abstract class AbstractWdemsCombox extends JComponent implements
		WdemsTagComponent {
	public static enum ContentType {
		None, All, Search
	}

	public static enum SortType {
		/* 升序 */
		ASC("p"),
		/* 降序 */
		DESC("n"), AUTO("c");
		private String name = "";

		private SortType(final String s) {
			this.name = s;
		}

		public static final SortType getType(String name) {
			if (name == null || name.isEmpty()) {
				return AUTO;
			}
			SortType type = AUTO;
			SortType[] list = SortType.values();
			name = name.toLowerCase();
			for (SortType t : list) {
				if (t.name.equals(name)) {
					type = t;
					break;
				}
			}
			return type;
		}
	}

	public static enum DisplayStyle {
		/* 普通树 */
		TREE,
		/* 叶子节点JRedioButton */
		RADIO_TREE,
		/* 叶子节点JCheckbox */
		CHECKBOX_TREE,
		/* 普通的表 */
		TABLE,
		/* 第一列单元格显示为JRedioButton */
		RADIO_TABLE,
		/* 所有列单元格显示为JRedioButton */
		ALL_RADIO_TABLE,
		/* 第一列单元格显示为JCheckbox */
		CHECKBOX_TABLE,
		/* 所有单元格显示为JCheckbox */
		ALL_CHECKBOX_TABLE;
	}

	/**
	 * 定义选择方式，是选择单元格、行、列
	 */
	public static enum SelectModel {
		/* 按单元格选择 */
		Cell,
		/* 按行选择，可以是多行选择 */
		Line,
		/* 按列选择，可以是多列选择 */
		Column,
	}

	public AbstractWdemsCombox(final Select select) {
		init();
		setSelect(select);
	}

	/**
	 * 根据制定Select对象，创建相应的下拉控件
	 * 
	 * @param select
	 *            指定Select对象。
	 * @return {@link WdemsTagComponent} 返回创建的下拉控件。
	 */
	public final static WdemsTagComponent buildCombox(final Select select,
			String xpath) {
		String showStyle = getShwoStyle(select);
		
		WdemsTagComponent combox = null;
		if (showStyle != null && showStyle.equals("tree")) {
			combox = new WdemsTreeCombox(select);
		} else {
			combox = new WdemsTableCombox(select);
		}
		((AbstractWdemsCombox) combox).setXpath(xpath);
		return combox;
	}

	private final static String getShwoStyle(Select select) {
		DataSource datasource = WdemsTagManager.Instance.getDataSource(select
				.getSrc());
		if (datasource == null)
			return ParseDataBuilder.TABLE1;
		if(datasource.isSwingDS())
		{
			SwingDataSource swds=datasource.getSwingDS();
			return swds.getStruts();
		}
		else{
		TableOrViewInfo info = datasource.getTableOrViewInfo();
		if (info == null)
			return ParseDataBuilder.TABLE1;
		return info.getStruts();
		}
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = txt.getPreferredSize();
		dim.width += dim.height;
		return dim;
	}

	/**
	 * 获得表名称
	 * 
	 * @return
	 */
	public String getTableName() {
		TableOrViewInfo info = getViewInfo();
		if (info == null)
			return null;
		return info.getName();
	}

	/**
	 * 获得列名称[数据库中各列的列明]
	 * 
	 * @return {@link String} 返回列名集。
	 */
	public String[] getColumnNames() {
		TableOrViewInfo info = getViewInfo();
		if (info == null) {
			if (isSwingDS()) {
				return dataSource.getSwingDS().getColumns();
			} else {
				return new String[0];
			}
		}
		return info.getTablehead();
	}

	public TableOrViewInfo getViewInfo() {
		DataSource source = getDataSource();
		if (source == null)
			return null;
		return source.getTableOrViewInfo();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		txt.setFont(font);
	}

	public void showPopup() {
		if (pop == null)
			return;
		calcPopupSize();
		pop.show(this, 0, getHeight());
	}

	protected void calcPopupSize() {

	}

	public void distroyPopup() {
		if (pop == null)
			return;
		pop.setVisible(Boolean.FALSE);
	}

	public String getDelimiters() {
		return delimiters;
	}

	public void setDelimiters(String delimiters) {
		if (delimiters == null || "".equals(delimiters)) {
			delimiters = SEPARATOR;
		}
		this.delimiters = delimiters;
	}

	public int getSelectRange() {
		return selectRange;
	}

	public void setSelectRange(int selectRange) {
		if (selectRange < MIN_RANGE) {
			selectRange = MIN_RANGE;
		}
		this.selectRange = selectRange;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(final SortType sortType) {
		this.sortType = sortType;
	}

	public DisplayStyle getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(final DisplayStyle displayStyle) {
		this.displayStyle = displayStyle;
	}

	public List<Integer> getShowColumns() {
		return showColumns;
	}

	public void setShowColumns(final List<Integer> showColumns) {
		if (!(showColumns instanceof TreeSet)) {
			this.showColumns = new ArrayList<Integer>(showColumns);
		} else {
			this.showColumns = showColumns;
		}
	}

	public Integer getShowLines() {
		return showRows;
	}

	public void setShowLines(Integer showLines) {
		if (showLines == null) {
			showLines = MIN_LINES;
		} else if (showLines > MAX_LINES) {
			showLines = MAX_LINES;
		} else if (showLines < MIN_LINES) {
			showLines = MIN_LINES;
		}
		this.showRows = showLines;
	}

	public int getMinIndex() {
		return this.minIndex;
	}

	public void setMinIndex(int index) {
		if (index < 0) {
			index = 0;
		}
		if (this.minIndex == index)
			return;
		int old = this.minIndex;
		this.minIndex = index;
		firePropertyChange(MIN_INDEX, old, index);
	}

	public int getMaxIndex() {
		return this.maxIndex;
	}

	protected void setMaxIndex(int index) {
		if (index < 0) {
			index = 0;
		}
		if (this.maxIndex == index)
			return;
		int old = this.maxIndex;
		this.maxIndex = index;
		firePropertyChange(MAX_INDEX, old, index);
	}

	public Set<Integer> getSortColumns() {
		return sortColumns;
	}

	public void setSortColumns(Set<Integer> sortColumns) {
		this.sortColumns = sortColumns;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;

		updateTextFeild();
	}

	public boolean isSearchable() {
		return this.searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;

		updateTextFeild();
	}

	public Set<Integer> getSearchSequence() {
		return searchSequence;
	}

	private void updateTextFeild() {
		txt.setEditable(edit | searchable);
	}

	public void setSearchSequence(Set<Integer> searchSequence) {
		this.searchSequence = searchSequence;
	}

	/**
	 * 获得选择的数据 indexes起始索引为1
	 * 
	 * @return {@link List} 返回选择的所有数据。
	 */
	public Collection<DataRow> getSelectValues(final int... indexes) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * 获得指定列索引所对应的列名称
	 * 
	 * @param indexes
	 *            指定列索引
	 * @return {@link List} 返回列名称集合【按索引顺序返回】。
	 */
	public List<String> getColumnOf(Integer... indexes) {
		String names = getViewInfo().getColumnString();
		String[] columns = names.split(",");
		if (columns == null || columns.length == 0)
			return new ArrayList<String>();
		List<String> all = new ArrayList<String>();
		if (indexes == null || indexes.length == 0)
			return new ArrayList<String>(Arrays.asList(columns));

		for (int index : indexes) {
			if (index < 1 || index > columns.length) {
				continue;
			}
			all.add(columns[index - 1]);
		}
		return all;
	}

	public JComponent getEditor() {
		return txt;
	}

	/*---------------------------数据封装实现--------------------------------*/
	protected void setConnect(String connect) {
		this.connect = connect;
	}

	protected String getConnect() {
		return this.connect;
	}

	protected void setHint(String hint) {
		txt.setToolTipText(hint);
		arrowButton.setToolTipText(hint);
	}

	protected String getXpath() {
		return this.xpath;
	}

	protected void setXpath(String xpath) {
		this.xpath = xpath;
	}

	protected void setSchema(boolean schema) {
		this.schema = schema;
	}

	protected boolean isSchema() {
		return this.schema;
	}

	protected void setSortColumns(String sortColumns) {
		Set<Integer> set = null;
		if (sortColumns == null || sortColumns.isEmpty()) {
			set = new LinkedHashSet<Integer>();
		} else {
			set = getIndexes(sortColumns);
		}
		setSortColumns(set);
	}

	protected void setSearchSequence(String sequence) {
		Set<Integer> set = null;
		if (sequence == null || sequence.isEmpty()) {
			set = new LinkedHashSet<Integer>();
		} else {
			set = getIndexes(sequence);
		}
		setSearchSequence(set);
	}

	protected void setNext(String next) {
		this.next = next;
	}

	protected String getNext() {
		return (next == null) ? "" : next;
	}

	protected void setDataSource(final String name) {
		this.dataSource = WdemsTagManager.Instance.getDataSource(name);
	}

	protected void setPopPanel(final WdemsPopPanel pop) {
		if (this.pop != null) {
			this.pop.setVisible(Boolean.FALSE);
		}

		this.pop = pop;
	}

	/**
	 * 按格式指定列索引集合。 现在的用的规则是，列索引间用","隔开
	 * 
	 * @param columns
	 *            指定包含所有要显示列索引的字符串。
	 */
	protected void setShowColumns(final String columns) {
		if (columns == null || "".equals(columns)) {
			this.showColumns = new ArrayList<Integer>();
		} else {
			this.showColumns = processColoumns(columns);
		}
	}

	protected void setSortType(final String sortType) {
		this.sortType = SortType.getType(sortType);
	}

	protected void setDisplayStyle(final String style) {
		this.displayStyle = buildStyle(style);
	}

	/** tree-Checktree-table-Checktable */
	private DisplayStyle buildStyle(String style) {
		if (this.getClass() == WdemsTreeCombox.class) {
			if ("check".equalsIgnoreCase(style)) {
				if (getSelectRange() > 1)
					return DisplayStyle.CHECKBOX_TREE;
				else
					return DisplayStyle.RADIO_TREE;
			} else
				return DisplayStyle.TREE;
		} else if (this.getClass() == WdemsTableCombox.class) {
			if ("check".equalsIgnoreCase(style)) {
				if (getSelectRange() > 1)
					return DisplayStyle.CHECKBOX_TABLE;
				else
					return DisplayStyle.RADIO_TABLE;
			} else
				return DisplayStyle.TABLE;
		}
		return DisplayStyle.TABLE;
	}

	protected int getDataCount() {
		if (isSwingDS()) {
			return dataSource.getSwingDS().getDataCount(null);
		} else {
			return dataSource.getTableOrViewInfo().getDatacount(cascadeInfo,cascadeValue,null,null);
		}
	}

	protected int getSearchDataCount() {
		String searchtxt = txt.getText();
		if(searchtxt==null||searchtxt.trim().isEmpty())
		{
			return getDataCount();
		}
		if(isSwingDS())
		{
			return dataSource.getSwingDS().getDataCount(searchtxt);
		}
		else
		{
			return dataSource.getTableOrViewInfo().getDatacount(cascadeInfo, cascadeValue, searchtxt, getSearchSequence());
		}
	}

	protected Integer getID(int row) {
			return row;
	}

	/*---------------------------数据封装实现--------------------------------*/
	/* --------------------WdemsTagComponent接口实现------------------------ */
	public void addActions(final Actions action) {
		if (!(pop instanceof WdemsPopPanel))
			return;
		WdemsPopPanel p = pop;
		p.addActionListener(action);
	}

	public JComponent getComponent() {
		return this;
	}

	public Object getValue() {
		return getValues(null);
	}

	public void setValue(Object value) {
		// TODO 为控件设置值
		value = (value == null) ? "" : value;
		txt.setText(value.toString());
	}

	public Data<DataRow> getValues(final int... indexes) {
		return new DataImp();
	}

	public void iniValue(final Object value) {
		txt.getDocument().removeDocumentListener(imp);
		txt.setText(value + "");
		if (value != null) {
			txt.setColumns(txt.getText().length());
		} else {
			txt.setColumns(1);
		}
		txt.getDocument().addDocumentListener(imp);
	}

	public void showValidationState(ValidationMessage vAction) {
		// TODO 向状态栏中写消息
		StatusbarMessageHelper.output("下拉表验证", "无法通过验证", LEVEL.INFO);
	}

	/* --------------------WdemsTagComponent接口实现------------------------ */
	// -----------------------------Private
	// Method---------------------------------------------
	private void init() {
		this.setInheritsPopupMenu(Boolean.FALSE);
		arrowButton.setName("ComboBox.arrowButton");
		arrowButton.setMargin(new Insets(0, 0, 0, 0));
		setDoubleBuffered(Boolean.TRUE);
		LookAndFeel.installColorsAndFont(this, "ComboBox.background",
				"ComboBox.foreground", "ComboBox.font");
		LookAndFeel.installBorder(this, "ComboBox.border");
		LookAndFeel.installProperty(this, "opaque", Boolean.TRUE);
		txt.setBackground(new Color(253, 238, 238));
		setBackground(new Color(253, 238, 238));
		add(txt);
		add(arrowButton);
		configureArrowButton();
		addLis();
		setBorder(null);
		txt.setBorder(null);
		setLayout(new Handler());
		
		
	}

	private void addLis() {
		arrowButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				initPopToShow();
				txt.requestFocus();
			}
		});
		MouseAdapter lis = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (isEdit()) {
					txt.requestFocus();
				} else if (AbstractWdemsCombox.this.isRequestFocusEnabled()) {
					/* AbstractWdemsCombox.this.requestFocus(); */
					if (e.getComponent() == txt) {
						initPopToShow();
					}
					txt.requestFocus();
				}
			}
		};
		FocusListener focus = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (!txt.isEditable()) {
					txt.selectAll();
				}
			}
		};
		arrowButton.addMouseListener(lis);
		txt.addMouseListener(lis);
		txt.addFocusListener(focus);
	}

	protected void initPopToShow() {
		/* if(contentType != ContentType.All) */{
			if (!isSchema()) {
				setMaxIndex(getDataCount());
			}
			if (contentType != ContentType.All)
				selectModel.clear();
		}
		setMinIndex(0);
		loadContent();
		contentType = ContentType.All;
		showPopup();
	}

	protected void loadContent() {
		if (isSchema()) {
			loadSchema();
		} else if (isSwingDS()) {
			loadSwingDS();
		} else {
			loadOtherContent();
		}
	}

	protected void loadSchema() {

	}

	protected void loadSwingDS() {

	}

	/**
	 * 把指定的数据，以PopPane方式展示出来。
	 * 
	 * @param set
	 *            指定数据集合。
	 */
	protected void loadOtherContent() {}

	/**
	 * This public method is implementation specific and should be private. Do
	 * not call or override.
	 * 
	 * @see #createArrowButton
	 */
	private void configureArrowButton() {
		if (arrowButton != null) {
			arrowButton.setEnabled(this.isEnabled());
			arrowButton.setRequestFocusEnabled(false);
			arrowButton.resetKeyboardActions();
			arrowButton.setInheritsPopupMenu(true);
		}
		txt.setInheritsPopupMenu(true);
	}

	protected void setSelect(final Select select) {
		/* 一页中最多要显示的列 */
		setShowColumns(select.getView());
		/* 一页中最多显示的行数目 */
		setShowLines(select.getLines());
		/* 设置最多选中的行数 */
		setSelectRange(select.getMultiple());
		setSortType(select.getSort());
		setDataSource(select.getSrc());
		setDisplayStyle(select.getShowList());
		setSortColumns(select.getSortColumn());
		setEdit(select.isIsEdit());
		setSearchSequence(select.getSearchQueue());
		setSchema(SCHEMA.equalsIgnoreCase(select.getSrc()));
		setHint(select.getHint());
		setConnect(select.getConn());
		setName(select.getName());
		setNext(select.getNext());
	}

	protected void processCascade() {
		String next = getNext();
		if (next.isEmpty())
			return;
		CascadeInfo info = new CascadeInfo(next);
		String value = getCascadeValue(info);
		AbstractWdemsCombox combox = getCombox(info.getName());
		if (combox == null)
			return;
		combox.processCascade(value, info);
	}

	public void processCascade(String value, CascadeInfo info) {
		this.cascadeValue = value;
		this.cascadeInfo = info;
		selectModel.clear();
		processCascade();
	}

	private AbstractWdemsCombox getCombox(String name) {
		return WdemsCascadeManager
				.getComponent(name, AbstractWdemsCombox.class);
	}

	private String getCascadeValue(CascadeInfo info) {
		Set<Integer> select=getSelectionModel();
		if(select!=null&&!select.isEmpty()&&this instanceof WdemsTableCombox)
		{
			WdemsTableCombox tablecom=(WdemsTableCombox) this;
			int lastselect=1;
			for(Integer ind:select)
			{
				lastselect=ind;
			}
			List<String> selectrow=tablecom.getSelectData(lastselect);
			return selectrow.get(info.getPrevious());
		}
		return null;
	}

	private List<Integer> processColoumns(final String columns) {
		String[] all = columns.split(getDelimiters());
		if (all == null)
			return new ArrayList<Integer>();

		List<Integer> list = new ArrayList<Integer>();
		for (String s : all) {
			if (!isNumbers(s)) {
				continue;
			}
			list.add(Integer.parseInt(s));
		}
		return list;
	}

	// LinkedHashSet保证数据的输入和输出顺序一致。
	private Set<Integer> getIndexes(final String columns) {
		String[] all = columns.split(getDelimiters());
		if (all == null)
			return new LinkedHashSet<Integer>();
		Set<Integer> set = new LinkedHashSet<Integer>();
		for (String s : all) {
			if (!isNumbers(s)) {
				continue;
			}
			set.add(Integer.parseInt(s));
		}
		return set;
	}

	private boolean isNumbers(final String s) {
		return LocationUtil.isNumbers(s);
	}

	private class Handler implements LayoutManager {
		public void addLayoutComponent(final String name, final Component comp) {
		}

		public void removeLayoutComponent(final Component comp) {
		}

		public Dimension preferredLayoutSize(final Container parent) {
			return new Dimension(50, 18);
		}

		public Dimension minimumLayoutSize(final Container parent) {
			return preferredLayoutSize(parent);
		}

		public void layoutContainer(final Container parent) {
			JComponent cb = (JComponent) parent;
			int width = cb.getWidth();
			int height = cb.getHeight();

			Insets insets = getInsets();
			int buttonSize = height - (insets.top + insets.bottom);
			Rectangle cvb;

			if (arrowButton != null) {
				if (cb.getComponentOrientation().isLeftToRight()) {
					arrowButton.setBounds(width - (insets.right + buttonSize),
							insets.top, buttonSize, buttonSize);
				} else {
					arrowButton.setBounds(insets.left, insets.top, buttonSize,
							buttonSize);
				}

			}
			if (txt != null) {
				cvb = rectangleForCurrentValue();
				txt.setBounds(cvb);
			}
		}

		/**
		 * Returns the area that is reserved for drawing the currently selected
		 * item.
		 */
		protected Rectangle rectangleForCurrentValue() {
			int width = getWidth();
			int height = getHeight();
			Insets insets = getInsets();
			int buttonSize = height - (insets.top + insets.bottom);
			if (arrowButton != null) {
				buttonSize = arrowButton.getWidth();
			}
			if (getComponentOrientation().isLeftToRight())
				return new Rectangle(insets.left, insets.top, width
						- (insets.left + insets.right + buttonSize), height
						- (insets.top + insets.bottom));
			else
				return new Rectangle(insets.left + buttonSize, insets.top,
						width - (insets.left + insets.right + buttonSize),
						height - (insets.top + insets.bottom));
		}
	}

	private class DocumentListenerImp implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
			update();
		}

		public void removeUpdate(DocumentEvent e) {
			update();
		}

		private void update() {
			if (isSchema())
				return;
			selectModel.clear();
			setMaxIndex(getSearchDataCount());
			setMinIndex(0);
			loadSearchContent();

			if (!pop.isVisible()) {
				contentType = ContentType.Search;
				showPopup();
			}
			txt.requestFocus();
		}
	}
	protected void loadSearchContent()
	{
		
	}
	protected final JButton arrowButton = new BasicArrowButton(
			BasicArrowButton.SOUTH,
			UIManager.getColor("ComboBox.buttonBackground"),
			UIManager.getColor("ComboBox.buttonShadow"),
			UIManager.getColor("ComboBox.buttonDarkShadow"),
			UIManager.getColor("ComboBox.buttonHighlight"));

	/* 保存所有选择数据的ID，在数据库中ID是自增列 */
	public Set<Integer> getSelectionModel() {
		return selectModel;
	}

	/* 所有被选中的数据，id号，自增列 */
	protected Set<Integer> selectModel = new TreeSet<Integer>();
	protected final JTextField txt = new JTextField(6) {
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics = (Graphics2D) g;
			graphics.addRenderingHints(ComponentStyleUtil.getRenderingHints());

			super.paintComponent(graphics);
		}
	};
	protected WdemsPopPanel pop = null;
	/* 每页中最小显示行数 ，也就是说如果有10条数据，一页要显示小于5条数据是不允许的 */
	private final static int MIN_LINES = 5;
	/* 每页中最大显示行数 */
	private final static int MAX_LINES = 200;
	/* 定义列的分隔符 */
	private final static String SEPARATOR = ",";
	/* 选择数目的最小值【缺省值】 */
	private final static int MIN_RANGE = 1;
	/* 定义每页显示的行数 */
	private Integer showRows = MIN_LINES;
	/* 定义显示的列【指定列索引】 */
	private List<Integer> showColumns = Collections.EMPTY_LIST;
	/* 定义表、Tree的最大选择数目 */
	private int selectRange = MIN_RANGE;
	/* 定义字符串分隔符【定界符】 */
	private String delimiters = SEPARATOR;
	/* 定义优先匹配序列 */
	protected Set<Integer> searchSequence = null;
	/* 按那一列进行排序,列索引从1开始 */
	private Set<Integer> sortColumns = Collections.EMPTY_SET;
	/* 是否可编辑的 */
	private boolean edit = Boolean.FALSE;
	/* 是否为可检索的 */
	private boolean searchable = Boolean.FALSE;
	/* 关联标签名称 */
	private String connect = "";
	/* 定义数据描述对象 */
	/* protected Select select = null; */
	/* 定义数据顺序 */
	private SortType sortType = SortType.AUTO;
	private boolean schema = Boolean.FALSE;
	/* 定义显示样式 【表、单选表、多选表、树、单选树、多选树】 */
	private DisplayStyle displayStyle = null;
	/* 数据源，提供初始化下拉表的数据结构，列头显示信息 */
	protected DataSource dataSource = null;
	/* 用于查找Schema的Xpath */
	private String xpath = "";
	/* 用于描述级联信息 */
	private String next = "";
	/* 控制Pop的现实 */
	protected Boolean isShowPop = Boolean.FALSE;
	/* 分页显示时，第一行的索引 */
	protected int minIndex = 0;
	/* 数据总条目 */
	protected int maxIndex = 0;
	protected String cascadeValue = null;
	protected CascadeInfo cascadeInfo = null;
	/* 表示ID列 */
	protected final static String ID_COLUMN = "ID";
	protected ContentType contentType = ContentType.None;
	protected final static String MAX_INDEX = "MAX_INDEX";
	protected final static String MIN_INDEX = "MIN_INDEX";
	/* 用于表示ID列的名称 */
	/* getSrc中获得名称可能是指向DataSource的名称、也可能来自Schema */
	private final static String SCHEMA = "Schema";
	protected DocumentListenerImp imp = new DocumentListenerImp();

	public class DataImp extends DataTable {
		@Override
		public Collection<DataRow> getCellsOf(int... indexes) {
			return getRows(indexes);
		}

		@Override
		public Object getObject(int row, int column) {
			List<DataRow> rows = getRows(column);
			if (rows.isEmpty())
				return null;
			DataRow r = rows.get(row);
			if (r == null)
				return null;
			DataCell<Object> cell;
			if(r.getSize()==1){
			 cell = r.getData(0);
			}
			else
			{
				cell=r.getData(column-1);
			}
			if (cell == null)
				return null;
			return cell.getValue();
		}

		@Override
		public Object getObject(int column) {
			return getObject(0, column);
		}

		private List<DataRow> getRows(int... indexes) {
			init(indexes);
			return getDatas();
		}

		private void init(int... indexes) {
				fillTable(indexes);
		}

		private void fillTable(int... indexes){
			if (isSchema()) {
				fillTableWithSchema(indexes);
			} else {
				if (AbstractWdemsCombox.this instanceof WdemsTableCombox) {
					Set<Integer> selections = getSelectionModel();
					for (Integer i : selections) {
						DataRow row = new DataRow();
						List<String> datas = ((WdemsTableCombox) AbstractWdemsCombox.this)
								.getSelectData(i);
						for (String data : datas) {
							row.addDatas(new DataCell(data));
						}
						this.addDatas(row);
					}
				} else {
					DataRow row = ((WdemsTreeCombox) AbstractWdemsCombox.this)
							.getSelectData();
					if (row != null) {
						this.addDatas(row);
					}
				}
			} 
		}

		private void fillTableWithSchema(int... indexes) {
			List<String> list = SchemaObj.getEnum(getXpath());
			Set<Integer> selections = getSelectionModel();
			for (Integer i : selections) {
				if (i < 0 || i > list.size()) {
					continue;
				}
				DataRow row = new DataRow();
				row.addDatas(new DataCell(list.get(i)));
				this.addDatas(row);
			}
		}
	}

	static class MotifComboBoxArrowIcon implements Icon, Serializable {
		private final Color lightShadow;
		private final Color darkShadow;
		private final Color fill;

		public MotifComboBoxArrowIcon(Color lightShadow, Color darkShadow,
				Color fill) {
			this.lightShadow = lightShadow;
			this.darkShadow = darkShadow;
			this.fill = fill;
		}

		public void paintIcon(Component c, Graphics g, int xo, int yo) {
			int w = getIconWidth();
			int h = getIconHeight();

			g.setColor(lightShadow);
			g.drawLine(xo, yo, xo + w - 1, yo);
			g.drawLine(xo, yo + 1, xo + w - 3, yo + 1);
			g.setColor(darkShadow);
			g.drawLine(xo + w - 2, yo + 1, xo + w - 1, yo + 1);

			for (int x = xo + 1, y = yo + 2, dx = w - 6; y + 1 < yo + h; y += 2) {
				g.setColor(lightShadow);
				g.drawLine(x, y, x + 1, y);
				g.drawLine(x, y + 1, x + 1, y + 1);
				if (dx > 0) {
					g.setColor(fill);
					g.drawLine(x + 2, y, x + 1 + dx, y);
					g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
				}
				g.setColor(darkShadow);
				g.drawLine(x + dx + 2, y, x + dx + 3, y);
				g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
				x += 1;
				dx -= 2;
			}

			g.setColor(darkShadow);
			g.drawLine(xo + (w / 2), yo + h - 1, xo + (w / 2), yo + h - 1);

		}

		public int getIconWidth() {
			return 11;
		}

		public int getIconHeight() {
			return 11;
		}
	}

	@Override
	public void setDefaultValue(String value) {
	}

	@Override
	public boolean canInitDefaultValue() {
		return false;
	}

	@Override
	public void initByDefaultValue() {
	}

	protected boolean isSwingDS() {
		return dataSource != null && dataSource.isSwingDS();
	}
}
