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
 * 
 */
package com.wisii.component.setting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxiao
 * 
 */
public class MutiDataBean implements Serializable {

	public static final String TOTALPAGECOUNT = "totalpagecount";
	public static final String CURRPAGENUM = "currpagenum";
	public static final String CURRPAGECOUNT = "currpagecount";
	public static final String ASKID = "askid";


	/* 总页数 */
	private int totalPageCount=0;
	/* 当前页码 */
	private int currPageNum=1;
	/* 当前份数 */
	private int currPart=1;
	/*份数与页数对照*/
	private Map <Integer,PartDataBean>partAndPages=new HashMap();
	/* 当前请求标志，供回传 */
	private String askId;
	/*目标页*/
	private int spPage=1;
	/*是否找到当前页*/
	private boolean isfind=false;
	/*大数据的时候用来记录已经打印了多少页的变量*/
	private  int MutiPrintCount=-1; 
		public MutiDataBean() {
			
		}
	
	public int getTotalPageCount() {
		return totalPageCount;
	}
	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public void setTotalPageCount(String totalPageCount) {
		if (totalPageCount != null && !"".equalsIgnoreCase(totalPageCount))
			try {
				this.totalPageCount = Integer.parseInt(totalPageCount);
			} catch (Exception e) {
			}
	}

	public int getCurrPageNum() {
		return currPageNum;
	}

	public void setCurrPageNum(int currPageNum) {
		this.currPageNum = currPageNum;
	}

	public void setCurrPageNum(String currPageNum) {
		if (currPageNum != null && !"".equalsIgnoreCase(currPageNum))
			try {
				this.currPageNum = Integer.parseInt(currPageNum);
			} catch (Exception e) {
			}
	}


	public void addPartAndPages(int pages)
	{

		if (currPart != 0/* && partAndPages.get(currPart) == null*/)
		{
			partAndPages.put(currPart, new PartDataBean(pages));
		}

	}
	public PartDataBean getPartAndPages(int part) {
		return partAndPages.get(part);
	}


	public String getAskId() {
		return askId;
	}

	public void setAskId(String askId) {
		this.askId = askId;
	}

	public static MutiDataBean parseAskId(String as, MutiDataBean mdb) {
		MutiDataBean ss = null;
		if (mdb == null)
			ss = new MutiDataBean();
		ss.setAskId(as);
		return ss;
	}

	public static MutiDataBean parseTotalPageCount(String as, MutiDataBean mdb) {
		
		if (mdb == null)
			mdb = new MutiDataBean();
		mdb.setTotalPageCount(as);
		return mdb;
	}

	public int getCurrPart() {
		return currPart;
	}

	public void setCurrPart(int currPart) {
		this.currPart = currPart;
	}

	public int getSpPage() {
		return spPage;
	}

	public void setSpPage(int spPage) {
		this.spPage = spPage;
	}
	public int  getStartPages(int part) {
		return  ((PartDataBean)partAndPages.get(part)).getStartPages();
	}
	public int getEndPages(int part) {
		try{
		return ((PartDataBean)partAndPages.get(part)).getEndPages();
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	public int getParts(int pages)
	{
		//得到一共有多少份
		int parts=partAndPages.size();
		//最后一份的最末页
		int p1=((PartDataBean)partAndPages.get(parts)).getEndPages();
		//如果请求的页数没有被记录过则向后请求一页
		if(pages>p1) return parts+1;
		else
			//否则就在已有内容中查找
		{
			 return findParts(1 ,parts,pages);
		}
		
	}
	private int findParts(int from ,int to,int pages)
	{
		if (from == to)
			return from;
		int middle = ((from+to)/2);
		PartDataBean pdb = partAndPages.get(middle);
		// int eparts=((PartDataBean)partAndPages.get((to+from)/2)).startPages;
		if (pages >= pdb.startPages && pages <= pdb.endPages)
		{
			return middle;
		} else if (pages > pdb.endPages)
		{
			if(to==middle+1)
			{
				return to;
			}
			return findParts(middle, to, pages);
		} else
		{
			if(from==middle-1)
			{
				return from;
			}
			return findParts(from, middle, pages);
		}
	}
	public int isExist(int pages)
	{
		PartDataBean cpdb= partAndPages.get(currPart);
//		if(cpdb==null)
//		{
//			return -1;
//		}
		int start=cpdb.getStartPages();
		int end=cpdb.getEndPages();
		//如果在范围之内返回true
		if(pages>=start&&pages<=end)
			return pages-start;
		else
		return -1;
	}
	public int getNumber(int parts,int pages)
	{
		PartDataBean ss=partAndPages.get(parts);
		//如果不是之前已经记录过的内容
		if(ss==null)
			{//取最后一个的最末页作为开始页
			int c= partAndPages.size();
			if(c==0) return 1;
			ss=partAndPages.get(partAndPages.size());
			return pages-ss.getEndPages();
			
			}
		else
		{//如果是之前已经记录过的内容，取
			return pages-ss.getStartPages()+1;
		}
		
	
	}
	class PartDataBean
	{
		public PartDataBean(){}
		/*开始页数*/
		private int startPages=0;
		/*结尾页数*/
		private int endPages=0;
		/*总页数*/
		private int pagecount=0;
		
		
		public PartDataBean(int startPages, int endPages,int pagecount)
		{
			this.startPages=startPages;
			this.endPages=endPages;
			this.pagecount=pagecount;
		}
		
		public PartDataBean(int pagecount)
		{
			this.pagecount=pagecount;
			PartDataBean pdb=(PartDataBean)partAndPages.get(currPart-1);
			if(pdb==null) this.startPages=1;
			else
				this.startPages=pdb.endPages+1;
			
			this.endPages=this.startPages+pagecount-1;
			
		}
		public int getStartPages() {
			return startPages;
		}
		public void setStartPages(int startPages) {
			this.startPages = startPages;
		}
		public int getEndPages() {
			return endPages;
		}
		public void setEndPages(int endPages) {
			this.endPages = endPages;
		}
		public int getPagecount() {
			return pagecount;
		}
		public void setPagecount(int pagecount) {
			this.pagecount = pagecount;
		}
		@Override
		public String toString()
		{
			return "startPages["+startPages+"]endPages["+endPages+"]pagecount["+pagecount+"]";
		}
		
	}
	public boolean isIsfind() {
		return isfind;
	}

	public void setIsfind(boolean isfind) {
		this.isfind = isfind;
	}

	public  int getMutiPrintCount() {
		return MutiPrintCount;
	}

	public  void addMutiPrintCount(int s) {
		MutiPrintCount+=s;
	}
	
	public  void setMutiPrintCount(int s) {
		MutiPrintCount=s;
	}
}
