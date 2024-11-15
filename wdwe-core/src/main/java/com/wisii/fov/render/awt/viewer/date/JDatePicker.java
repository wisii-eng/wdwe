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
 */package com.wisii.fov.render.awt.viewer.date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.accessibility.Accessible;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

import com.sun.java.swing.plaf.motif.MotifComboBoxUI;
import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;
import com.wisii.edit.message.StatusbarMessageHelper;
import com.wisii.edit.message.StatusbarMessageHelper.LEVEL;
import com.wisii.edit.tag.action.Actions;
import com.wisii.edit.tag.action.ValidationMessage;
import com.wisii.edit.tag.components.WdemsTagComponent;
import com.wisii.edit.tag.components.action.WdemsActioinHandler;
import com.wisii.edit.tag.components.action.schema.KeyManager.BindType;
import com.wisii.fov.area.inline.TextArea;
import com.wisii.fov.render.awt.viewer.EditAreaInterface;

/**
 * <p>
 * Title:OpenSwing
 * </p>
 * <p>
 * Description: JDatePicker 日期选择框<BR>
 * 履历:<BR>
 * 2004/03/26 根据网友caiyj的建议引入了recoon写的关于JDateDocument的校验方法<BR>
 * 2004/04/02 根据网友caiyj提交的BUG,修正了做为TableCellEditor时日期选择面板弹不出问题<BR>
 * 2005/04/17 修正了弹出面板不能显示当前输入框中的日期<BR>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author <a href="mailto:sunkingxie@hotmail.com"'>Sunking</a>
 * @version 1.0
 */
public class JDatePicker extends JComboBox implements Serializable,KeyListener, FocusListener ,ItemListener, WdemsTagComponent
{
    /**
     * 日期格式类型
     */
    /*public static final int STYLE_CN_DATE = 0;

    public static final int STYLE_CN_DATE1 = 1;

    public static final int STYLE_CN_DATETIME = 2;

    public static final int STYLE_CN_DATETIME1 = 3;*/

    /**
     * 日期格式类型
     */
   /* private int formatStyle = STYLE_CN_DATE;*/
    private String format = "yyyy-MM-dd";
    private Object result=null;

	/**
     * 当前设置日期格式
     */
    private SimpleDateFormat simpleFormat = new SimpleDateFormat(format);

    /**
     * 只有一个值的ComboBoxModel
     */
    private final SingleObjectComboBoxModel model = new SingleObjectComboBoxModel();

    JDateDocument dateDocument = null;

    private com.wisii.fov.area.inline.TextArea textArea;

    private EditAreaInterface editEvent;

    /*private CreateInitView currentdialog;*/

    //焦点丢失时是否执行
    private boolean focusFlag = true;
    private String dateFormat = null;
    private final DatePickerEditor editor = new DatePickerEditor();
    
    /**
     * 构造式
     */
    /*public JDatePicker(CreateInitView dialog) throws UnsupportedOperationException
    {
        this(STYLE_CN_DATE , new Date());
    }*/

    public JDatePicker(final int formatStyle) throws UnsupportedOperationException
    {
        this(formatStyle, new Date());
    }
    public JDatePicker(final String format){
    	setBorder(BorderFactory.createLineBorder(Color.GRAY));
         // 设置可编辑
         this.setEditable(true);
         setFormat(format);
         setEditor(editor);
         setBorder(null);
         model.setDateFormat(simpleFormat);
         this.setSelectedItem(new Date());
         // 设置Model为单值Model
         this.setModel(model);
         WdemsActioinHandler.bindActionsWhenAncestor(this, BindType.Date);
    }
    private JDatePicker(final int formatStyle, final Date initialDatetime) throws UnsupportedOperationException
    {

        /*this.setStyle(formatStyle);*/
        // 设置可编辑
        this.setEditable(true);
        // 设置编辑器属性(只能输入正确日期)
        JTextField textField = ((JTextField) getEditor().getEditorComponent());
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        dateDocument = new JDateDocument(textField, this.simpleFormat);
        textField.setDocument(dateDocument);
        // 设置Model为单值Model
        this.setModel(model);
        // 设置当前选择日期
        this.setSelectedItem(initialDatetime == null ? new Date() : initialDatetime);
        textField.addKeyListener(this);
        textField.addFocusListener(this);
        this.addKeyListener(this);
        this.addItemListener(this);
//        this.addFocusListener(this);
    }

    /**
     * 设置日期格式 STYLE_CN_DATE STYLE_CN_DATE1 STYLE_CN_DATETIME STYLE_CN_DATETIME1
     *
     * @param formatStyle
     *            int
     */
    /*public void setStyle(int formatStyle) throws UnsupportedOperationException
    {
        this.formatStyle = formatStyle;
        dateFormat = getDateFormat(formatStyle);
        model.setDateFormat(dateFormat);
        if (dateDocument != null)
        {
            dateDocument.setDateFormat(dateFormat);
        }
    }*/
    /* 【添加：START】 by 李晓光  2009-7-13 */
    public String getFormat() {
		return format;
	}

	public void setFormat(final String format) {
		this.format = format;
		if(simpleFormat == null) {
			simpleFormat = new SimpleDateFormat();
		}
		if(format != null && !"".equalsIgnoreCase(format)) {
			simpleFormat.applyPattern(format);
		} else {
			simpleFormat = new SimpleDateFormat();
		}
		if (dateDocument != null) {
			dateDocument.setDateFormat(simpleFormat);
		}
		editor.setPattern(format);
	}
	/* 【添加：END】 by 李晓光  2009-7-13 */
    /**
     * 取得指定类型的日期格式
     *
     * @param formatStyle
     *            int
     * @return SimpleDateFormat
     * @throws UnsupportedOperationException
     */
   /* private static SimpleDateFormat getDateFormat(int formatStyle)
            throws UnsupportedOperationException
    {
        switch (formatStyle) {
        case STYLE_CN_DATE:
            return new SimpleDateFormat("yyyy/MM/dd");
        case STYLE_CN_DATE1:
            return new SimpleDateFormat("yyyy-MM-dd");
        case STYLE_CN_DATETIME:
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        case STYLE_CN_DATETIME1:
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        default:
            throw new UnsupportedOperationException("invalid formatStyle parameter!");
        }
    }*/

    /**
     * 取得日期格式 STYLE_CN_DATE STYLE_CN_DATE1 STYLE_CN_DATETIME STYLE_CN_DATETIME1
     *
     * @return int
     */
   /* public int getStyle()
    {
        return formatStyle;
    }*/

    /**
	 * @return the inputFormat
	 */
	public String getDateFormat() {
		if(dateFormat == null || "".equalsIgnoreCase(dateFormat)) {
			this.dateFormat = getFormat();
		}
		return dateFormat;
	}
	/**
	 * @param inputFormat the inputFormat to set
	 */
	public void setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
     * 取得当前选择的日期
     *
     * @return Date
     */
    public Date getSelectedDate() throws ParseException
    {
        return simpleFormat.parse(getSelectedItem().toString());
    }

    /**
     * 取得当前选择的日期字符串
     *
     * @return Date
     */
    public String getSelectedDateString()
    {
        return getSelectedItem().toString();
    }

    /**
     * 设置当前选择的日期
     *
     * @return Date
     */
    public void setSelectedDate(final Date date) throws ParseException
    {
        this.setSelectedItem(simpleFormat.format(date));
    }

    /**
     * 设置当前选择的日期(字符串格式 yyyy-MM-dd)
     *
     * @return Date
     */
    public void setSelectedDateString(String date) throws ParseException
    {
        if(date.length()>10 ) {
			date=date.substring(0, 10);
		}
    	this.setSelectedItem(date);
    }

    @Override
	public void setSelectedItem(final Object anObject)
    {
        model.setSelectedItem(anObject);
        super.setSelectedItem(anObject);
    }

    public void itemStateChanged(final ItemEvent e)
    {
//        if(oldstr == null)
//        {
//            oldstr = this.getSelectedItem().toString();
//        }
//        String currentstr = this.getSelectedItem().toString();
//
//        if(currentdialog._isSubData) //已经提交过了，按钮为不可用状态
//        {
//            if(currentstr.equals(oldstr))
//            {
////                currentdialog._subDataButton.setEnabled(false);
//                currentdialog._subDataCloseButton.setEnabled(false);
//            }
//            else
//            {
////                currentdialog._subDataButton.setEnabled(true);
//                currentdialog._subDataCloseButton.setEnabled(true);
//            }
//        }
    }
    /* 【添加：START】  by	2009-10-14*/
    public JCalendarPanel getDatePane(){
    	Accessible as = getAccessibleContext().getAccessibleChild(0);
    	if(!(as instanceof DatePopup))
    		return null;
    	DatePopup pop = (DatePopup)as;
    	return pop.calendarPanel;
    }
    public void nextYear(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.nextYear();
    }
    public void previousYear(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.previousYear();
    }
    public void nextMouth(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.nextMouth();
    }
    public void previousMouth(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.previousMouth();
    }
    public void nextDay(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.oneDay(1);
    }
    public void previousDay(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.oneDay(-1);
    }
    public void nextWeek(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.oneDay(7);
    }
    public void previousWeek(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.oneDay(-7);
    }
    public void submit(){
    	JCalendarPanel calendarPanel = getDatePane();
    	if(calendarPanel != null)
    		calendarPanel.submit();
    }
    /* 【添加：END】  by	2009-10-14*/

    /**
     * <p>
     * Title: JDatePicker
     * </p>
     * <p>
     * Description: DatePopup 选择框弹出的日期选择面板
     * </p>
     * <p>
     * Copyright: Copyright (c) 2004
     * </p>
     * <p>
     * Company:
     * </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com"'>Sunking</a>
     * @version 1.0
     */
    class DatePopup extends BasicComboPopup implements ChangeListener
    {
        JCalendarPanel calendarPanel = null;
        
        public DatePopup(final JComboBox box)
        {
            super(box);
            setLayout(new BorderLayout());
            calendarPanel = new JCalendarPanel();
            calendarPanel.addDateChangeListener(this);
            add(calendarPanel, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder());
        }

        /**
         * 显示弹出面板
         */
        @Override
		protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
        {
            if (propertyName.equals("visible"))
            {
                if (oldValue.equals(Boolean.FALSE) && newValue.equals(Boolean.TRUE))
                { // SHOW
                    try
                    {
                        String strDate = comboBox.getSelectedItem().toString();
                        Date selectionDate = simpleFormat.parse(strDate);
                        calendarPanel.setSelectedDate(selectionDate);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else if (oldValue.equals(Boolean.TRUE) && newValue.equals(Boolean.FALSE))
                { // HIDE
                }
            }
            super.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void stateChanged(final ChangeEvent e)
        {
            Date selectedDate = (Date) e.getSource();
            String strDate = simpleFormat.format(selectedDate);
            if (comboBox.isEditable() && comboBox.getEditor() != null)
            {
                comboBox.configureEditor(comboBox.getEditor(), strDate);
            }
            comboBox.setSelectedItem(strDate);
            comboBox.setPopupVisible(false);
        }
    }

    /**
     * 更新UI
     */
    @Override
	public void updateUI()
    {
        ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);
        if (cui instanceof MetalComboBoxUI)
        {
            cui = new MetalDateComboBoxUI();
        }
        else if (cui instanceof MotifComboBoxUI)
        {
            cui = new MotifDateComboBoxUI();
        }
        else
        {
            cui = new WindowsDateComboBoxUI();
        }
        setUI(cui);
    }
    // UI Inner classes -- one for each supported Look and Feel
    /**
     * <p>
     * Title: OpenSwing
     * </p>
     * <p>
     * Description: MetalDateComboBoxUI
     * </p>
     * <p>
     * Copyright: Copyright (c) 2004
     * </p>
     * <p>
     * Company:
     * </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    class MetalDateComboBoxUI extends MetalComboBoxUI
    {
        @Override
		protected ComboPopup createPopup()
        {
            return new DatePopup(comboBox);
        }
    }

    /**
     *
     * <p>
     * Title: OpenSwing
     * </p>
     * <p>
     * Description: WindowsDateComboBoxUI
     * </p>
     * <p>
     * Copyright: Copyright (c) 2004
     * </p>
     * <p>
     * Company:
     * </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    class WindowsDateComboBoxUI extends WindowsComboBoxUI
    {
        @Override
		protected ComboPopup createPopup()
        {
            return new DatePopup(comboBox);
        }
    }

    /**
     *
     * <p>
     * Title: OpenSwing
     * </p>
     * <p>
     * Description: MotifDateComboBoxUI
     * </p>
     * <p>
     * Copyright: Copyright (c) 2004
     * </p>
     * <p>
     * Company:
     * </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    class MotifDateComboBoxUI extends MotifComboBoxUI
    {
        @Override
		protected ComboPopup createPopup()
        {
            return new DatePopup(comboBox);
        }
    }
//
//    /**
//     * 测试JDatePicker
//     */
//    public static void main(String args[])
//    {
//
//        JFrame f = OpenSwingUtil.createDemoFrame("JDatePicker Demo");
//        JPanel c = new JPanel();
//        c.add(new JLabel("From:"));
//        JDatePicker datePickerFrom = new JDatePicker(JDatePicker.STYLE_CN_DATETIME);
//        c.add(datePickerFrom);
//        c.add(new JLabel("To:"));
//        Date d = new Date();
//        d.setTime(d.getTime() + 10000000000L);
//        JDatePicker datePickerTo = new JDatePicker(JDatePicker.STYLE_CN_DATE1, d);
////        datePickerTo.setEditable(false);
//        c.add(datePickerTo);
//        f.getContentPane().add(c, BorderLayout.NORTH);
//
//        f.getContentPane().add(new JDatePicker(), BorderLayout.SOUTH);
//
//        final JTable table = new JTable(20, 10);
//        JComboBox editor = new JDatePicker();
//        editor.setBorder(null);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setDefaultEditor(Object.class, new DefaultCellEditor(editor));
//        JScrollPane sp = new JScrollPane(table);
//        f.getContentPane().add(sp, BorderLayout.CENTER);
//
//        // f.setSize(600, 400);
//        f.setVisible(true);
//    }

    public void keyPressed(final KeyEvent e)
    {
        if(e.getKeyCode() ==  KeyEvent.VK_ESCAPE)
        {
            editEvent.cnacelEdit();
            //如果当前已经提交了数据就设置提交按钮为不可用
//           if(currentdialog._isSubData)
//           {
//               currentdialog._subDataButton.setEnabled(false);
//               currentdialog._subDataCloseButton.setEnabled(false);
//           }
        }
        else if(e.getKeyCode() ==  KeyEvent.VK_ENTER)
        {
            editEvent.editComplete();
            focusFlag = false;
        }
    }

    public void keyReleased(final KeyEvent e)
    {

    }

    public void keyTyped(final KeyEvent e)
    {

    }

    public void focusGained(final FocusEvent e)
    {

    }

    public void focusLost(final FocusEvent e)
    {
        if(focusFlag)
        {
             editEvent.editComplete();
        }
    }

    public TextArea getTextArea()
    {
        return textArea;
    }

    public void setTextArea(final TextArea textArea)
    {
        this.textArea = textArea;
    }

    public void setEditEvent(final EditAreaInterface editEvent)
    {
        this.editEvent = editEvent;
    }

    public boolean isFocusFlag()
    {
        return focusFlag;
    }

    public void setFocusFlag(final boolean focusFlag)
    {
        this.focusFlag = focusFlag;
    }
    /* --------------------WdemsTagComponent接口实现------------------------ */  
	public JComponent getComponent() {
		return this;
	}

	public Object getValue() {
		try {
			JFormattedTextField field = (JFormattedTextField)getEditor().getEditorComponent();
			String str = field.getText();
			if(str == null || "".equals(str.trim())){
				return null;
			}
			return getSelectedDate();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void iniValue(Object value) {
		if(value instanceof String){
			value = createDate(value + "");
		}
		if(!(value instanceof Date)) {
			value = new Date();
		}
		try {
			setSelectedDate((Date)value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setValue(Object value) {
		if(value instanceof Date){
			try {
				setSelectedDate((Date)value);
			} catch (ParseException e) {
				
			}
		}	
	}
	private Date createDate(String str){
		try {
			SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
			return format.parse(str);
		} catch (ParseException e) {
			StatusbarMessageHelper.output("解析日期", e.getMessage(), LEVEL.INFO);
		} catch (IllegalArgumentException e){
			StatusbarMessageHelper.output("日期格式不合理", e.getMessage(), LEVEL.INFO);
		}
		return null;
	}
	public void addActions(final Actions action) {
		addActionListener(action);
	}

	public void showValidationState(final ValidationMessage vAction) {
		StatusbarMessageHelper.output(vAction.getWrongMessage(), "", StatusbarMessageHelper.LEVEL.DEBUG);
	}	
	/* --------------------WdemsTagComponent接口实现------------------------ */
	@Override
	public void setFont(Font font) {
		if(isEditable()){
			ComboBoxEditor editor = getEditor();
			editor.getEditorComponent().setFont(font);
		}else{
			super.setFont(font);
		}
	}
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		JComponent comp = this;
		if(isEditable()){
			comp = (JComponent)this.getEditor().getEditorComponent();
		}else{
			comp = (JComponent)this.getRenderer().getListCellRendererComponent(new JList(), null, 0, false, false);
		}
		comp.addMouseListener(l);
	}
	public Object getActionResult() {
		
		return result;
	}
	public void setActionResult(Object result) {
		this.result=result;
		
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
}
