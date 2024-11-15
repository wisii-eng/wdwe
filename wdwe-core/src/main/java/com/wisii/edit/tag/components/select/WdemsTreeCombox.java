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
 * @WdemsTreeCombox.java
 * 汇智互联版权所有，未经许可，不得使用
 */
package com.wisii.edit.tag.components.select;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import com.wisii.edit.tag.components.select.DataTable.DataCell;
import com.wisii.edit.tag.components.select.datasource.TableOrViewInfo;
import com.wisii.edit.tag.schema.wdems.Select;

/**
 * 类功能描述：用于创建下拉控件，且下拉的Pop-Panel是树形式
 * 
 * 作者：李晓光
 * 创建日期：2009-6-26
 */
@SuppressWarnings("serial")
public class WdemsTreeCombox extends AbstractWdemsCombox {
	private WdemsTree tree = null;
	private int valueNumber = -1;
	private Object result=null;
	private DataRow selectdata;
	public WdemsTreeCombox(Select select){
		super(select);
		init();
	}

	public int getValueNumber() {
		return valueNumber;
	}
	public void setValueNumber(int valueNumber) {
		this.valueNumber = valueNumber;
	}
	
	private void init() {
		setValueNumber(getViewInfo().getfirstValueNm());
		WdemsPopPanel pop = new WdemsPopPanel();
		pop.setMainComp(createMainComp());
		setPopPanel(pop);
	}
	private JComponent createMainComp(){
		tree = createTree();
		if(getSelectRange() > 1){
			
		}else{
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() % 1 != 0)
						return;
					Point p = e.getPoint();
					
					TreePath path = tree.getPathForLocation(p.x, p.y);
					if(path == null)
						return;
					
					WdemsTreeNode node  = (WdemsTreeNode)path.getLastPathComponent();
					if(node == null)
						return;
					if (path == tree.getSelectionPath()) {
				        Object select=node.getUserObject();
				        if(!(select instanceof DataRow)){
				        	return;
				        }
						selectdata = (DataRow)select;
						distroyPopup();
						pop.fireActionPerformed();
					}
				}
			});
		}
		JScrollPane pane = new JScrollPane(tree);
		return pane;
	}
	private WdemsTree createTree()
 {
		if (isSwingDS()) {
			return createSwingDSTree();

		} else {
			return createOtherTree();
		}
	}

	private WdemsTree createSwingDSTree() {
		String[] columns = dataSource.getSwingDS().getColumns();
		int i = 0;
		int parentcolumnindex = 0;
		int codeindex = 0;
		for (String column : columns) {
			if ("parentCode".equals(column)) {
				parentcolumnindex = i;
			} else if ("code".equals(column)) {
				codeindex = i;
			}
			i++;
		}

		List<List<String>> rowdatas = dataSource.getSwingDS().getData(null, -1,
				-1, null);
		String rootcode = dataSource.getSwingDS().getRootcode();
		if (rowdatas == null || rowdatas.isEmpty() || rootcode == null
				|| rootcode.isEmpty()) {
			return new WdemsTree(getDisplayStyle());
		}

		return new WdemsTree(getDisplayStyle(), getSwingDSNode(rootcode,
				rowdatas, codeindex, parentcolumnindex));
	}
	private WdemsTree createOtherTree()
	{
		TableOrViewInfo info=dataSource.getTableOrViewInfo();
		if(info==null)
		{
			return new WdemsTree(getDisplayStyle());
		}
		List<List<String>> rowdatas = info.getData(-1, -1,
				null, null,null,null);
		
		if (rowdatas == null || rowdatas.isEmpty()) {
			return new WdemsTree(getDisplayStyle());
		}
		
		return new WdemsTree(getDisplayStyle(), getOtherRootNode(rowdatas,
				info));
	}
    private WdemsTreeNode getOtherRootNode(List<List<String>> rowdatas,TableOrViewInfo info)
    {
    	WdemsTreeNode root=new  WdemsTreeNode("根节点");
    	int codeindex = info.getfirstValueNm()-1;
    	for(List<String> rowdata:rowdatas)
    	{
    		int size=rowdata.size();
    		if(rowdata.get(size-1)==null)
    		{
    			SwingDSNodeValue nodevalue = new SwingDSNodeValue();
    			nodevalue.addCells(new ArrayList<Object>(rowdata));
    			WdemsTreeNode child=new WdemsTreeNode(nodevalue);
    			List<List<String>> nrowdatas=new ArrayList<List<String>>(rowdatas);
    			nrowdatas.remove(rowdata);
    			getSwingDSChildNode(rowdata.get(codeindex),nrowdatas,codeindex,size-1,child);
    			root.add(child);
    		}
    	}
    	return root;
    }
	private WdemsTreeNode getSwingDSNode(String pcode,
			List<List<String>> rowdatas, int codeindex, int parentindex) {
		if(rowdatas.isEmpty())
		{
			return null;
		}
		WdemsTreeNode node = null;
		List<List<String>> nrowdatas=new ArrayList<List<String>>(rowdatas);
		for (int i = 0; i < rowdatas.size(); i++) {
			List<String> rowdata = rowdatas.get(i);
			String code = rowdata.get(codeindex);
			if (code.equals(pcode)) {
				SwingDSNodeValue nodevalue = new SwingDSNodeValue();
				nodevalue.addCells(new ArrayList<Object>(rowdata));
				node = new WdemsTreeNode(nodevalue);
				nrowdatas.remove(rowdata);
				getSwingDSChildNode(code,nrowdatas,codeindex,parentindex,node);
				break;
			} 
		}
		return node;
	}
	private void getSwingDSChildNode(String pcode,
			List<List<String>> rowdatas, int codeindex, int parentindex,WdemsTreeNode parent) {
		if(rowdatas.isEmpty())
		{
			return;
		}
		List<DefaultMutableTreeNode> nodes= new ArrayList<DefaultMutableTreeNode>();
		List<List<String>> nrowdatas=new ArrayList<List<String>>(rowdatas);
		for (int i = 0; i < rowdatas.size(); i++) {
			List<String> rowdata = rowdatas.get(i);
			String code = rowdata.get(codeindex);
			String parcode = rowdata.get(parentindex);
			if (parcode!=null&&parcode.equals(pcode)) {
				SwingDSNodeValue nodevalue = new SwingDSNodeValue();
				nodevalue.addCells(new ArrayList<Object>(rowdata));
				WdemsTreeNode node=new WdemsTreeNode(nodevalue);
				nodes.add(node);
				nrowdatas.remove(rowdata);
				getSwingDSChildNode(code,nrowdatas,codeindex,parentindex,node);
			} 
		}
		if (parent != null) {
			if (!nodes.isEmpty()) {
				parent.addChildren(nodes);
			}
		}
	}
	
	private class WdemsTreeNode extends DefaultMutableTreeNode{
		WdemsTreeNode(Object value)	{
			super(value);
		}
		public void addChildren(List<DefaultMutableTreeNode> nodes){
			removeAllChildren();
			int index = 0;
			for (DefaultMutableTreeNode node : nodes) {
				insert(node, index++);
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
	private class SwingDSNodeValue extends DataRow
	{
		private String tostring;
		@Override
		public String toString() {
			if (tostring == null) {

				List<Integer> showcolumns = getShowColumns();
				List<DataCell> rowdata = getDatas();
				if(rowdata==null||rowdata.isEmpty())
				{
					return "无内容";
				}
				StringBuffer sb = new StringBuffer();
				if (showcolumns == null || showcolumns.isEmpty()) {

					for (DataCell dc : rowdata) {
						sb.append(dc.getValue());
					}
				} else {
					for (int c : showcolumns) {
						if(c<=rowdata.size()){
						sb.append(rowdata.get(c-1).getValue());
						}
					}
				}
				tostring = sb.toString();
			}
			return tostring;
		}
	}
	public DataRow getSelectData() {
		return selectdata;
	}
}
