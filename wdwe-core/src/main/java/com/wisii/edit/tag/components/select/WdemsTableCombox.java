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
 * @WedmsTabelCombox.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import com.wisii.component.startUp.SystemUtil;
import com.wisii.component.validate.validatexml.SchemaObj;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.edit.tag.components.select.datasource.SwingDataSource;
import com.wisii.edit.tag.components.select.datasource.TableOrViewInfo;
import com.wisii.edit.tag.schema.wdems.Select;
import com.wisii.edit.tag.util.LocationUtil;

/**
 * 类功能描述：用于创建下拉控件，且下拉的Pop-Panel是表格形式
 * 
 * 作者：李晓光
 * 创建日期：2009-6-12
 */
@SuppressWarnings("serial")
public class WdemsTableCombox extends AbstractWdemsCombox {
	private WdemsTable table = null;
	private final JLabel labLeft = new JLabel(createText("上一页"));
	private final JLabel labRight = new JLabel(createText("下一页"));
	private final JLabel labMessage = new JLabel(new ImageIcon(SystemUtil.getImagesPath("Close.gif")), JLabel.LEADING);
	private final static String COMMAND = "OK";
	private final JLabel labAllPage = new JLabel("共 " + 1 + " 页");
	private final JLabel labFirst = new JLabel("第");
	private final JTextField txtPage = new JTextField("1", 2);
	private final JLabel labLast = new JLabel("页");
	private Vector<Object> colums = null;
	private List<List<String>> datas;
	
	private Object result=null;
	public WdemsTableCombox(Select select){
		super(select);
		init();
	}
	@Override
	protected void initPopToShow() {
		super.initPopToShow();
	}
	private void initTableHeader()
	{
		if(colums == null) {
			colums = createTableHeader();
		}
		if(colums == null || colums.isEmpty()){
			colums = new Vector<Object>();
			colums.add("");
		}
	}
	@Override
	protected void loadOtherContent() {

		super.loadOtherContent();
		TableOrViewInfo info = dataSource.getTableOrViewInfo();

		int pagecount = getShowLines();
		int pagenumber = minIndex / pagecount + 1;
		List<Integer> showcolumns = getShowColumns();
		datas = info.getData(pagenumber, pagecount, null, getSearchSequence(),cascadeInfo,cascadeValue);
		if (datas == null || datas.isEmpty()) {
			return;
		}
		if (showcolumns == null || showcolumns.isEmpty()) {
			List<String> rowdata = datas.get(0);
			showcolumns = new ArrayList<Integer>();
			for (int i = 0; i < rowdata.size(); i++) {
				showcolumns.add(i + 1);
			}
		}
		Vector rows = new Vector();
		for (List<String> rowdata : datas)
		{
			Vector row = new Vector();
			for (Integer showcolumn : showcolumns)
			{
				if (showcolumn > 0 && showcolumn <= rowdata.size())
				{
					row.add(rowdata.get(showcolumn - 1));
				}
			}
			rows.add(row);
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rows, new Vector<Object>(colums));

	}
	private void init(){
		WdemsPopPanel pop = new WdemsPopPanel();
		if(getSelectRange() > 1) {
			pop.setControlComp(createBtnPanel());
		}
			pop.setMainComp(createMainComp());
	
		setPopPanel(pop);
		addListener();
		addPropertyChangeListener(new PropertyListenerImp());
		bindKey(pop);
		WdemsActioinHandler.bindActionsWhenAncestor(this, BindType.Select);
		initTableHeader();
	}
	private void addListener(){
		if(!isEdit() || searchSequence == null || searchSequence.size() == 0)return;
		txt.getDocument().addDocumentListener(imp);
	}
	private void bindKey(final WdemsPopPanel pop){		
		ActionMap am = this.getActionMap();
		am.put(WdemsAction.up, new WdemsAction(WdemsAction.up));
		am.put(WdemsAction.down, new WdemsAction(WdemsAction.down));
		am.put(WdemsAction.page_down, new WdemsAction(WdemsAction.page_down));
		am.put(WdemsAction.page_up, new WdemsAction(WdemsAction.page_up));
		am.put(WdemsAction.space, new WdemsAction(WdemsAction.space));
		
		txt.addActionListener(new WdemsAction(WdemsAction.enter));
	}
	private void showPopWithFocus(){
		initPopToShow();
		txt.requestFocus();
	}
	
	private JComponent createMainComp(){
		table = createTable();
		table.setFocusable(Boolean.FALSE);
		if(getSelectRange() > 1){
			table.setSelectionModel(new MySelectionModel(getSelectRange()));
		}else{
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionAllowed(Boolean.TRUE);
			table.setColumnSelectionAllowed(Boolean.FALSE);
			table.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() % 1 != 0)
						return;
					int i = table.rowAtPoint(e.getPoint());
					if(i == table.getSelectedRow()) {
						distroyPopup();
						
						selectModel.clear();
						selectModel.add(getID(i));
						pop.fireActionPerformed();
						processCascade();
					}
				}
			});
		}
		table.setStyle(getDisplayStyle());
		JScrollPane pane = new JScrollPane(table);
		JPanel panel = new JPanel(new BorderLayout());
		if(getShowColumns().size() == 1)
			panel.add(table, BorderLayout.CENTER);
		else	
			panel.add(pane, BorderLayout.CENTER);
		int count = getDataCount();
		if(!isSchema() && count > getShowLines()) {
			panel.add(createOptionPanel(), BorderLayout.SOUTH);
		}
		initLabActions(labLeft, labRight);
		initTextAction(txtPage);
		return panel;
	}
	@Override
	protected void calcPopupSize() {
		if(getShowColumns().size() == 1){
			pop.setPreferredSize(fitTableColumns(table));
		}
	}
	public Dimension fitTableColumns(JTable myTable){
		  JTableHeader header = myTable.getTableHeader();
		     int rowCount = myTable.getRowCount();
		     int width = 0;
		     int height = (rowCount) * myTable.getRowHeight();
		     Enumeration columns = myTable.getColumnModel().getColumns();
		     while(columns.hasMoreElements()){
		         TableColumn column = (TableColumn)columns.nextElement();
		         int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
		         int w = (int)myTable.getTableHeader().getDefaultRenderer()
		                 .getTableCellRendererComponent(myTable, column.getIdentifier()
		                         , false, false, -1, col).getPreferredSize().getWidth();
		         for(int row = 0; row<rowCount; row++){
		             int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,
		               myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
		             w = Math.max(w, preferedWidth);
		         }
		         header.setResizingColumn(column); // 此行很重要
		         column.setWidth(w+myTable.getIntercellSpacing().width);
		         width = w+myTable.getIntercellSpacing().width + 10;
		     }
		     return new Dimension(Math.max(width, pop.getMinwidth()), height + 10);
	}
	private JPanel createOptionPanel(){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
		labLeft.setToolTipText(createText("上一页"));
		labRight.setToolTipText(createText("下一页"));
		
		labLeft.setDisplayedMnemonic(KeyEvent.VK_PAGE_UP);
		labLeft.setDisplayedMnemonic(KeyEvent.VK_PAGE_DOWN);
		txtPage.setHorizontalAlignment(JTextField.CENTER);
		p.add(labLeft);
		p.add(labRight);
		p.add(labFirst);
		p.add(txtPage);
		p.add(labLast);
		p.add(labAllPage);
		initLabActions(labLeft, labRight);
		initTextAction(txtPage);
		return p;
	}
	
	private JPanel createBtnPanel() {
		JPanel main = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JButton btnOK = new JButton("确定");		
		JButton btnCancel = new JButton("取消");
		Border outer = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border inner = BorderFactory.createLineBorder(Color.GRAY);
		labMessage.setBorder(BorderFactory.createCompoundBorder(outer, inner));
		btnOK.setActionCommand(COMMAND);
		Insets inset = new Insets(0, 0, 0, 0);
		btnOK.setMargin(inset);
		btnCancel.setMargin(inset);
		panel.add(labMessage);
		panel.add(btnOK);
		panel.add(btnCancel);
		
		main.add(labMessage, BorderLayout.CENTER);
		main.add(panel, BorderLayout.EAST);
		
		addLis(btnOK, btnCancel);
		return main;
	}
	private void addLis(AbstractButton... buttons) {
		ActionListener lis = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pop.setVisible(false);
				if(COMMAND.equalsIgnoreCase(e.getActionCommand())) {
					pop.fireActionPerformed();
				}
			}
		};
		for (AbstractButton btn : buttons) {
			btn.addActionListener(lis);
		}
	}
	
	private String createText(String text){
		return text;		
	}
	private void initLabActions(JComponent...comps){
		MouseListener mlis = new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isConsumed())
					return;
				if(e.getComponent() == labLeft){
					pageUp();
				} else{
					pageDown();
				}
				e.consume();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
			
		};
		for (JComponent comp : comps) {
			comp.addMouseListener(mlis);
		}
	}
	private void pageUp(){
		int index = 0;
		if(getMinIndex() == 0)
			return;
		index = getMinIndex() - getShowLines();					
		index = (index < 0 ) ? 0 : index;
		loadPage(index);
	}
	private void pageDown(){
		int index = 0;
		if(getMinIndex() == getMaxIndex() - 1)
			return;
		index = getMinIndex() + getShowLines();
		if(index >= getMaxIndex())
			return;
		loadPage(index);
	}

	private void loadPage(int index) {
		setMinIndex(index);
		if (contentType == ContentType.Search) {
			loadSearchContent();
		} else {
			loadContent();
		}
	}
	protected void loadSearchContent()
 {
		if (isSchema()) {
			//
			loadSearchSchema();
		} else if (dataSource.isSwingDS()) {
			loadSearchSwingDS();
		} else {
			loadOtherSearchContent();
		}
	}
	private void loadSearchSchema() {
		//schema暂时不支持搜索，如支持搜索，修修改此处的代码
		loadSchema();
		
	}
	private void loadSearchSwingDS() {
		String searchtxt = txt.getText();
		SwingDataSource swingds=dataSource.getSwingDS();
		int pagecount = getShowLines();
		int pagenumber = minIndex/pagecount+1;
		datas=swingds.getData(null, pagenumber, pagecount, searchtxt);
		if (datas == null || datas.isEmpty()) {
			return;
		}
		Vector rows = null;
		List<Integer> showcolumns = getShowColumns();
		if (showcolumns == null || showcolumns.isEmpty()) {
			rows = new Vector(datas);
		} else {
			rows = new Vector();
			for (List<String> rowdata : datas) {
				Vector row=new Vector();
                for(Integer showcolumn:showcolumns)
                {
                	row.add(rowdata.get(showcolumn));
                }
                rows.add(row);
			}
		}
		// showColumns
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rows, new Vector<Object>(colums));
	}
	private void loadOtherSearchContent()
	{

		TableOrViewInfo info = dataSource.getTableOrViewInfo();
		int pagecount = getShowLines();
		int pagenumber = minIndex / pagecount + 1;
		List<Integer> showcolumns = getShowColumns();
		String searchtxt = txt.getText();
		datas = info.getData(pagenumber, pagecount, searchtxt, getSearchSequence(),cascadeInfo,cascadeValue);
		if (datas == null || datas.isEmpty()) {
			return;
		}
		if (showcolumns == null || showcolumns.isEmpty()) {
			List<String> rowdata = datas.get(0);
			showcolumns = new ArrayList<Integer>();
			for (int i = 0; i < rowdata.size(); i++) {
				showcolumns.add(i + 1);
			}
		}
		Vector rows = new Vector();
		for (List<String> rowdata : datas) {
			Vector row = new Vector();
			for (Integer showcolumn : showcolumns) {
				if(showcolumn>0&&showcolumn<=rowdata.size()){
				row.add(rowdata.get(showcolumn - 1));
				}
			}
			rows.add(row);
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rows, new Vector<Object>(colums));

	
	}
	@Override
	protected void loadSwingDS() {
		super.loadSwingDS();
		SwingDataSource swingds = dataSource.getSwingDS();
		int pagecount = getShowLines();
		int pagenumber = minIndex / pagecount + 1;
		datas = swingds.getData(null, pagenumber, pagecount, null);
		if (datas == null || datas.isEmpty()) {
			return;
		}
		List<Integer> showcolumns = getShowColumns();
		if (showcolumns == null || showcolumns.isEmpty()) {
			List<String> rowdata = datas.get(0);
			showcolumns = new ArrayList<Integer>();
			for (int i = 0; i < rowdata.size(); i++) {
				showcolumns.add(i + 1);
			}
		}
		Vector rows = new Vector();
		for (List<String> rowdata : datas) {
			Vector row = new Vector();
			for (Integer showcolumn : showcolumns) {
				row.add(rowdata.get(showcolumn - 1));
			}
			rows.add(row);
		}
		// showColumns
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rows, new Vector<Object>(colums));
	}
	@Override
	protected void loadSchema() {
		super.loadSchema();
		Vector<Object> rows = new Vector<Object>();
		List<String> list = SchemaObj.getEnum(getXpath());
		for (String s : list) {
			Vector<Object> row = new Vector<Object>(1);
			
			row.add(s);
			rows.add(row);
		}
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.setDataVector(rows, new Vector<Object>(colums));
	}
	private void submitSingleRow(){
		int row = table.getSelectedRow();
		if(row == -1)
			return;
		
		distroyPopup();
		selectModel.clear();
		selectModel.add(getID(row));
		pop.fireActionPerformed();
		processCascade();
	}
	private void initTextAction(final JTextField field){
		ActionListener lis = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String text = field.getText();
				if(text == null || text.isEmpty())return;
				text = text.trim();
				
				if(!LocationUtil.isNumbers(text)){
					setMinIndex(getMinIndex());
					field.setText(getInt(getMinIndex()) + "");
					return;
				}
			
				Integer page = Integer.parseInt(text);
				
				if(page > getInt(getMaxIndex())) {
					page = getInt(getMaxIndex());
				}else if(page <= 0){
					page = 1;
				}
				
				int index = (page - 1) * getShowLines();
				setMinIndex(index);
					if(contentType == ContentType.Search) {
						loadSearchContent();
					} else {
						loadContent();
					}
				
			}
		};
		field.addActionListener(lis);
	}

	private WdemsTable createTable(){
		WdemsTable table = new WdemsTable();
		return table;
	}
	
	private Vector<Object> createTableHeader(){
		String[] info = getColumnNames();
		Collection<Integer> columns = getShowColumns();
		Vector<Object> result = new Vector<Object>(columns.size());
		if(!isShowHeader(info)) {
			return new Vector<Object>();
		}
		
		int length = info.length;
		for (Integer col : columns) {
			if(col <= 0 || col > length) {
				continue;
			}
			result.add(info[col - 1]);
		}
		if(result.isEmpty()) {
			return new Vector<Object>();
		}
		return result;
	}
	private boolean isShowHeader(String[] info){
		if(info == null || info.length <= 0)
			return Boolean.FALSE;
		for (String s : info) {
			if(s != null)
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	private class PropertyListenerImp implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent e) {
			String name = e.getPropertyName();
			if(MAX_INDEX.equals(name)) {
				labAllPage.setText("共 " + getInt((Integer)e.getNewValue()) + " 页");
				int length = e.getNewValue().toString().trim().length();
				int columns = txtPage.getColumns();
				if(length + 1 == columns)
					return;
				else {
				}
				txtPage.setColumns(length);
			}else if(MIN_INDEX.equals(name)){
				//最小索引起始值是0
				txtPage.setText("" + getInt((Integer)e.getNewValue() + 1));
			}
		}
	}
	private int getInt(int i){
		int line = getShowLines();
		double f = Math.ceil((float)i / line); 
		return (int)f;
	}
	private class MySelectionModel extends DefaultListSelectionModel{
		private int maxSelectCount = 1;
		MySelectionModel(int maxSelectCount){
			setMaxCount(maxSelectCount);
		}
		public int getMaxCount() {
			return maxSelectCount;
		}

		public void setMaxCount(int maxCount) {
			if(maxCount <= 0) {
				maxCount = 1;
			}
			this.maxSelectCount = maxCount;
		}
		@Override
		public void addSelectionInterval(int start, int end) {
			int size = getSelectionModel().size();
			if(size >= getMaxCount()){
				labMessage.setText("<html>最大选中项数目【<font color=red>" + getMaxCount()+ "</font>】。</html>");
				return;
			}
			if (start == -1 || end == -1)
				return;
			if(needSwap(start, end)){
				int temp = start;
				start = end;
				end = temp;
			}
			if(getSelectionMode() == SINGLE_SELECTION) {
				end = start;
			}
			int count = end - start + 1;
			int remain = getMaxCount() - size;
			count = Math.min(count, remain);
			end = start + count - 1;
			updateSelectionModel(start, end, Boolean.TRUE);
			super.addSelectionInterval(start, end);
		}
		@Override
		public void setSelectionInterval(int start, int end) {
			getSelectionModel().clear();
			int size = getSelectionModel().size();
			if(size >= getMaxCount()){
				labMessage.setText("<html>最大选中项数目【<font color=red>" + getMaxCount()+ "</font>】。</html>");
				return;
			} else {
				labMessage.setText("");
			}
			if (start == -1 || end == -1)
				return;
			
			int count = end - start + 1;
			int remain = getMaxCount() - size;
			if(needSwap(start, end)){
				count = start - end + 1;
			}
			count = Math.min(count, remain);
			
			if(needSwap(start, end)){
				end = start - count + 1;
			} else {
				end = start + count - 1;
			}
			/*System.err.println("set start = " + start + "  end = " + end);*/
			updateSelectionModel(start, end, Boolean.TRUE);
			super.setSelectionInterval(start, end);
		}
		@Override
		public void removeIndexInterval(int start, int end) {
			updateSelectionModel(start, end, Boolean.FALSE);
			super.removeIndexInterval(start, end);
		}
		@Override
		public void removeSelectionInterval(int start, int end) {
			updateSelectionModel(start, end, Boolean.FALSE);
			super.removeSelectionInterval(start, end);
		}
		private boolean needSwap(int start, int end){
			return (start - end) > 0;
		}
		private void updateSelectionModel(int start, int end, Boolean isAdd){
			int min = start;
			int max = end;
			if(needSwap(start, end)){
				min = start;
				max = end;
			}
			for (int i = min; i <= max; i++) {
				int id = getID(i);					
				if(isAdd) {
					selectModel.add(id);
				} else {
					selectModel.remove(id);
				}
			}
		}
	}
	private class WdemsAction extends AbstractAction{
		private final static String page_down = "wdems.nextPage";//"page_down";
		private final static String page_up = "wdems.previousPage";//"page_up";
		private final static String down = "wdems.selectNextRow";//"down";
		private final static String up = "wdems.selectPreviousRow";//"up";
		private final static String space = "wdems.spacePopup";//"space";
		private final static String enter = "wdems.submit";//"enter";
		private String actionName = "";
		WdemsAction(String name){
			super(name);
			this.actionName = name;
		}
		String getActionName(){
			return this.actionName;
		}
		public void actionPerformed(ActionEvent e) {
			String key = getActionName();
			if(key == page_down){
				if(!pop.isShowing())
					return;
				pageDown();
			}else if(key == page_up){
				if(!pop.isShowing())
					return;
				pageUp();
			}else if(key == down){
				if(!pop.isShowing()){
					showPopWithFocus();
					return;
				}
				Action action = table.getActionMap().get("selectNextRow");
				e.setSource(table);
				action.actionPerformed(e);
			}else if(key == up){
				if(!pop.isShowing())
					return;
				Action action = table.getActionMap().get("selectPreviousRow");
				e.setSource(table);
				action.actionPerformed(e);
			}else if(key == space){
				if(isEdit())
					return;
				
				showPopWithFocus();
			}else if(key == enter){
				if(!pop.isShowing())
					return;
				submitSingleRow();
			}
		}
	}
	public Object getActionResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setActionResult(Object result) {
		this.result = result;
	}
	protected List<String> getSelectData(int index)
	{
		if(index<0||datas==null||datas.isEmpty()||datas.size()<=index)
		{
			return Collections.EMPTY_LIST;
		}
		return datas.get(index);
	}
}
