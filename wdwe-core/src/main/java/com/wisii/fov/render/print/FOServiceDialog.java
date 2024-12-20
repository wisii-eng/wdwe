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
 *//*
 * @(#)ServiceDialog.java	1.28 04/01/06
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.wisii.fov.render.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilePermission;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;
import sun.print.SunAlternateMedia;
import com.wisii.component.startUp.SystemUtil;


/**
 * A class which implements a cross-platform print dialog.
 *
 * @version 1.4 02/03/01
 * @author  Chris Campbell
 */
public class FOServiceDialog extends JDialog implements ActionListener {

/**
     * Waiting print status (user response pending).
     */
    public final static int WAITING = 0;

/**
     * Approve print status (user activated "Print" or "OK").
     */
    public final static int APPROVE = 1;

/**
     * Cancel print status (user activated "Cancel");
     */
    public final static int CANCEL = 2;

    private static final String strBundle = "com.wisii.fov.render.print.resources.serviceui";
    private static final Insets panelInsets = new Insets(6, 6, 6, 6);
    private static final Insets compInsets = new Insets(3, 6, 3, 6);

    private static ResourceBundle messageRB;
    private JTabbedPane tpTabs;
    private JButton btnCancel, btnApprove;
    private PrintService[] services;
    private int defaultServiceIndex;
    private PrintRequestAttributeSet asOriginal;
    private HashPrintRequestAttributeSet asCurrent;
    private PrintService psCurrent;
    private DocFlavor docFlavor;
    private int status;

    private ValidatingFileChooser jfc;

    private GeneralPanel pnlGeneral;
    private PageSetupPanel pnlPageSetup;
    private AppearancePanel pnlAppearance;
    private MyExcursion myExcursion;

    private boolean isAWT = false;


    static
    {
        initResource();
    }


/**
     * Constructor for the "standard" print dialog (containing all relevant
     * tabs)
     */
    public FOServiceDialog(GraphicsConfiguration gc,
                           int x, int y,
                           PrintService[] services,
                           int defaultServiceIndex,
                           DocFlavor flavor,
                           PrintRequestAttributeSet attributes,
                           Dialog dialog)
    {
        super(dialog, getMsg("dialog.printtitle"), true, gc);
        initPrintDialog(x, y, services, defaultServiceIndex,
                        flavor, attributes);
    }



/**
     * Constructor for the "standard" print dialog (containing all relevant
     * tabs)
     */
    public FOServiceDialog(GraphicsConfiguration gc,
                           int x, int y,
                           PrintService[] services,
                           int defaultServiceIndex,
                           DocFlavor flavor,
                           PrintRequestAttributeSet attributes,
                           Frame frame)
    {
        super(frame, getMsg("dialog.printtitle"), true, gc);
        initPrintDialog(x, y, services, defaultServiceIndex,
                        flavor, attributes);
    }


/**
     * Initialize print dialog.
     */
    void initPrintDialog(int x, int y,
                         PrintService[] services,
                         int defaultServiceIndex,
                         DocFlavor flavor,
                         PrintRequestAttributeSet attributes)
    {
        this.services = services;
        this.defaultServiceIndex = defaultServiceIndex;
        this.asOriginal = attributes;
        this.asCurrent = new HashPrintRequestAttributeSet(attributes);
        this.psCurrent = services[defaultServiceIndex];
        this.docFlavor = flavor;
        FOPageSelection pages =
            (FOPageSelection) attributes.get(FOPageSelection.class);
        if(pages != null)
        {
            isAWT = true;
        }

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        tpTabs = new JTabbedPane();
        tpTabs.setBorder(new EmptyBorder(5, 5, 5, 5));

        String gkey = getMsg("tab.general");
        int gmnemonic = getVKMnemonic("tab.general");
        pnlGeneral = new GeneralPanel();
        tpTabs.add(gkey, pnlGeneral);
        tpTabs.setMnemonicAt(0, gmnemonic);

        String pkey = getMsg("tab.pagesetup");
        int pmnemonic = getVKMnemonic("tab.pagesetup");
        pnlPageSetup = new PageSetupPanel();
        tpTabs.add(pkey, pnlPageSetup);
        tpTabs.setMnemonicAt(1, pmnemonic);

        String akey = getMsg("tab.appearance");
        int amnemonic = getVKMnemonic("tab.appearance");
        pnlAppearance = new AppearancePanel();
        tpTabs.add(akey, pnlAppearance);
        tpTabs.setMnemonicAt(2, amnemonic);

        FOPrintPageSetup page = (FOPrintPageSetup) attributes.get(FOPrintPageSetup.class);

//		myExcursion = new MyExcursion(page.getX(FOPrintPageSetup.MM),
//									  page.getY(FOPrintPageSetup.MM),
//									  page.getXscale(FOPrintPageSetup.MM),
//									  page.getYscale(FOPrintPageSetup.MM));

        myExcursion = new MyExcursion(page.getX(FOPrintPageSetup.MM),
                                      page.getY(FOPrintPageSetup.MM),
                                      page.getXscale(FOPrintPageSetup.MM),
                                      page.getYscale(FOPrintPageSetup.MM),
                                      page.isSelectedHeightCheckBox(),
                                      page.getheightAddABS());


        tpTabs.add(getMsg("tab.Excursion"), myExcursion);
        tpTabs.setMnemonicAt(3, amnemonic);

        c.add(tpTabs, BorderLayout.CENTER);

        updatePanels();

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        btnApprove = createExitButton("button.print", this);
        pnlSouth.add(btnApprove);
        getRootPane().setDefaultButton(btnApprove);
        btnCancel = createExitButton("button.cancel", this);
        handleEscKey(btnCancel);
        pnlSouth.add(btnCancel);
        c.add(pnlSouth, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                dispose(CANCEL);
            }
        });

        getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
        setResizable(false);
        setLocation(x, y);
        pack();
    }

/**
     * Constructor for the solitary "page setup" dialog
     */
    public FOServiceDialog(GraphicsConfiguration gc,
                           int x, int y,
                           PrintService ps,
                           DocFlavor flavor,
                           PrintRequestAttributeSet attributes,
                           Dialog dialog)
    {
        super(dialog, getMsg("dialog.pstitle"), true, gc);
        initPageDialog(x, y, ps, flavor, attributes);
    }

/**
     * Constructor for the solitary "page setup" dialog
     */
    public FOServiceDialog(GraphicsConfiguration gc,
                           int x, int y,
                           PrintService ps,
                           DocFlavor flavor,
                           PrintRequestAttributeSet attributes,
                           Frame frame)
    {
        super(frame, getMsg("dialog.pstitle"), true, gc);
        initPageDialog(x, y, ps, flavor, attributes);
    }


/**
     * Initialize "page setup" dialog
     */
    void initPageDialog(int x, int y,
                        PrintService ps,
                        DocFlavor flavor,
                        PrintRequestAttributeSet attributes)
    {
        this.psCurrent = ps;
        this.docFlavor = flavor;
        this.asOriginal = attributes;
        this.asCurrent = new HashPrintRequestAttributeSet(attributes);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        pnlPageSetup = new PageSetupPanel();
        c.add(pnlPageSetup, BorderLayout.CENTER);

        pnlPageSetup.updateInfo();

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        btnApprove = createExitButton("button.ok", this);
        pnlSouth.add(btnApprove);
        getRootPane().setDefaultButton(btnApprove);
        btnCancel = createExitButton("button.cancel", this);
        handleEscKey(btnCancel);
        pnlSouth.add(btnCancel);
        c.add(pnlSouth, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                dispose(CANCEL);
            }
        });

        getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
        setResizable(false);
        setLocation(x, y);
        pack();
    }

/**
     * Performs Cancel when Esc key is pressed.
     */
    private void handleEscKey(JButton btnCancel)
    {
        Action cancelKeyAction = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose(CANCEL);
            }
        };
        KeyStroke cancelKeyStroke =
            KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE, false);
        InputMap inputMap =
            btnCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = btnCancel.getActionMap();

        if(inputMap != null && actionMap != null)
        {
            inputMap.put(cancelKeyStroke, "cancel");
            actionMap.put("cancel", cancelKeyAction);
        }
    }


/**
     * Returns the current status of the dialog (whether the user has selected
     * the "Print" or "Cancel" button)
     */
    public int getStatus()
    {
        return status;
    }

/**
     * Returns an AttributeSet based on whether or not the user cancelled the
     * dialog.  If the user selected "Print" we return their new selections,
     * otherwise we return the attributes that were passed in initially.
     */
    public PrintRequestAttributeSet getAttributes()
    {
        if(status == APPROVE)
        {
            return asCurrent;
        }
        else
        {
            return asOriginal;
        }
    }

/**
     * Returns a PrintService based on whether or not the user cancelled the
     * dialog.  If the user selected "Print" we return the user's selection
     * for the PrintService, otherwise we return null.
     */
    public PrintService getPrintService()
    {
        if(status == APPROVE)
        {
            return psCurrent;
        }
        else
        {
            return null;
        }
    }

/**
     * Sets the current status flag for the dialog and disposes it (thus
     * returning control of the parent frame back to the user)
     */
    public void dispose(int status)
    {
        this.status = status;

        super.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        boolean approved = false;

        if(source == btnApprove)
        {
        	initPrintServer();
            approved = true;

            if(pnlGeneral != null)
            {
                if(pnlGeneral.isPrintToFileSelected())
                {
                    approved = showFileChooser();
                }
                else
                {
                    asCurrent.remove(Destination.class);
                }
            }
        }

        dispose(approved ? APPROVE : CANCEL);
    }
    private void initPrintServer()
    {

                // to preserve the state of Print To File
                if(pnlGeneral.isPsChange()&&pnlGeneral.isPrintToFileSelected() &&
                   psCurrent.isAttributeCategorySupported(
                       Destination.class))
                {
                    Destination dest = (Destination) asOriginal.get(
                        Destination.class);
                    if(dest != null)
                    {
                        asCurrent.add(dest);
                    }
                    else
                    {
                        asCurrent.add(
                            new Destination((new File("out.prn")).toURI()));
                    }
                }
                else
                {
                    asCurrent.remove(Destination.class);
                }
    
    }
/**
     * Displays a JFileChooser that allows the user to select the destination
     * for "Print To File"
     */
    private boolean showFileChooser()
    {
        Class dstCategory = Destination.class;

        Destination dst = (Destination) asCurrent.get(dstCategory);
        if(dst == null)
        {
            dst = (Destination) asOriginal.get(dstCategory);
            if(dst == null)
            {
                dst = (Destination) psCurrent.getDefaultAttributeValue(dstCategory);
                if(dst == null)
                {
                    dst = new Destination((new File("out.prn")).toURI());
                }
            }
        }

        File fileDest;
        if(dst != null)
        {
            try
            {
                fileDest = new File(dst.getURI());
            }
            catch(Exception e)
            {
                // all manner of runtime exceptions possible
                fileDest = new File("out.prn");
            }
        }
        else
        {
            fileDest = new File("out.prn");
        }

        ValidatingFileChooser jfc = new ValidatingFileChooser();
        jfc.setApproveButtonText(getMsg("button.ok"));
        jfc.setDialogTitle(getMsg("dialog.printtofile"));
        jfc.setSelectedFile(fileDest);

        int returnVal = jfc.showDialog(this, null);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            fileDest = jfc.getSelectedFile();

            try
            {
                asCurrent.add(new Destination(fileDest.toURI()));
            }
            catch(Exception e)
            {
                asCurrent.remove(dstCategory);
            }
        }
        else
        {
            asCurrent.remove(dstCategory);
        }

        return(returnVal == JFileChooser.APPROVE_OPTION);
    }

/**
     * Updates each of the top level panels
     */
    private void updatePanels()
    {
        pnlGeneral.updateInfo();
        pnlPageSetup.updateInfo();
        pnlAppearance.updateInfo();
    }

/**
     * Initialize ResourceBundle
     */
    private static void initResource()
    {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    messageRB = ResourceBundle.getBundle(strBundle);
                    return null;
                }
                catch(java.util.MissingResourceException e)
                {
                    throw new Error("Fatal: Resource for ServiceUI " +
                                    "is missing");
                }
            }
        }
        );
    }

/**
     * Returns message string from resource
     */
    private static String getMsg(String key)
    {
        try
        {
            return messageRB.getString(key);
        }
        catch(java.util.MissingResourceException e)
        {
            throw new Error("Fatal: Resource for ServiceUI is broken; " +
                            "there is no " + key + " key in resource");
        }
    }

/**
     * Returns mnemonic character from resource
     */
    private static char getMnemonic(String key)
    {
        String str = getMsg(key + ".mnemonic");
        if((str != null) && (str.length() > 0))
        {
            return str.charAt(0);
        }
        else
        {
            return(char) 0;
        }
    }

/**
     * Returns the mnemonic as a KeyEvent.VK constant from the resource.
     */
    private static int getVKMnemonic(String key)
    {
        String str = getMsg(key + ".vkMnemonic");
        if((str != null) && (str.length() > 0))
        {
            try
            {
                return Integer.parseInt(str);
            }
            catch(NumberFormatException nfe)
            {}
        }
        return 0;
    }

/**
     * Returns URL for image resource
     */
    private static URL getImageResource(final String key)
    {
        URL url = (URL) java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction()
        {
            public Object run()
            {
                URL url = FOServiceDialog.class.getResource(
                    "resources/" + key);
                return url;
            }
        });

        if(url == null)
        {
            throw new Error("Fatal: Resource for ServiceUI is broken; " +
                            "there is no " + key + " key in resource");
        }

        return url;
    }

/**
     * Creates a new JButton and sets its text, mnemonic, and ActionListener
     */
    private static JButton createButton(String key, ActionListener al)
    {
        JButton btn = new JButton(getMsg(key));
        btn.setMnemonic(getMnemonic(key));
        btn.addActionListener(al);

        return btn;
    }

/**
     * Creates a new JButton and sets its text, and ActionListener
     */
    private static JButton createExitButton(String key, ActionListener al)
    {
        String str = getMsg(key);
        JButton btn = new JButton(str);
        btn.addActionListener(al);
        btn.getAccessibleContext().setAccessibleDescription(str);
        return btn;
    }

/**
     * Creates a new JCheckBox and sets its text, mnemonic, and ActionListener
     */
    private static JCheckBox createCheckBox(String key, ActionListener al)
    {
        JCheckBox cb = new JCheckBox(getMsg(key));
        cb.setMnemonic(getMnemonic(key));
        cb.addActionListener(al);

        return cb;
    }

/**
     * Creates a new JRadioButton and sets its text, mnemonic,
     * and ActionListener
     */
    private static JRadioButton createRadioButton(String key,
                                                  ActionListener al)
    {
        JRadioButton rb = new JRadioButton(getMsg(key));
        rb.setMnemonic(getMnemonic(key));
        rb.addActionListener(al);

        return rb;
    }

/**
     * Creates a  pop-up dialog for "no print service"
     */
    public static void showNoPrintService(GraphicsConfiguration gc)
    {
        Frame dlgFrame = new Frame(gc);
        JOptionPane.showMessageDialog(dlgFrame,
                                      getMsg("dialog.noprintermsg"));
        dlgFrame.dispose();
    }

/**
     * Sets the constraints for the GridBagLayout and adds the Component
     * to the given Container
     */
    private static void addToGB(Component comp, Container cont,
                                GridBagLayout gridbag,
                                GridBagConstraints constraints)
    {
        gridbag.setConstraints(comp, constraints);
        cont.add(comp);
    }

/**
     * Adds the AbstractButton to both the given ButtonGroup and Container
     */
    private static void addToBG(AbstractButton button, Container cont,
                                ButtonGroup bg)
    {
        bg.add(button);
        cont.add(button);
    }




/**
     * The "General" tab.  Includes the controls for PrintService,
     * PageRange, and Copies/Collate.
     */
    private class GeneralPanel
        extends JPanel
    {

        private PrintServicePanel pnlPrintService;
        private PrintRangePanel pnlPrintRange;
        private CopiesPanel pnlCopies;

        public GeneralPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);

            c.fill = GridBagConstraints.BOTH;
            c.insets = panelInsets;
            c.weightx = 1.0;
            c.weighty = 1.0;

            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlPrintService = new PrintServicePanel();
            addToGB(pnlPrintService, this, gridbag, c);

            c.gridwidth = GridBagConstraints.RELATIVE;
            pnlPrintRange = new PrintRangePanel();
            addToGB(pnlPrintRange, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlCopies = new CopiesPanel();
            addToGB(pnlCopies, this, gridbag, c);
        }

        public boolean isPrintToFileSelected()
        {
            return pnlPrintService.isPrintToFileSelected();
        }

        public void updateInfo()
        {
            pnlPrintService.updateInfo();
            pnlPrintRange.updateInfo();
            pnlCopies.updateInfo();
        }
        private boolean isPsChange()
        {
        	return  pnlPrintService.isPsChange();
        }
    }


    private class PrintServicePanel
        extends JPanel implements ActionListener, ItemListener, PopupMenuListener
    {
        private final String strTitle = getMsg("border.printservice");
        private FilePermission printToFilePermission;
        private JButton btnProperties;
        private JCheckBox cbPrintToFile;
        private JComboBox cbName;
        private JLabel lblType, lblStatus, lblInfo;
        private ServiceUIFactory uiFactory;
        private boolean changedService = false;
        private boolean ispschanged = false;

        public PrintServicePanel()
        {
            super();
            uiFactory = psCurrent.getServiceUIFactory();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            String[] psnames = new String[services.length];
            for(int i = 0; i < psnames.length; i++)
            {
                psnames[i] = services[i].getName();
            }
            cbName = new JComboBox(psnames);
            cbName.setSelectedIndex(defaultServiceIndex);
            cbName.addItemListener(this);
            cbName.addPopupMenuListener(this);

            c.fill = GridBagConstraints.BOTH;
            c.insets = compInsets;

            c.weightx = 0.0;
            JLabel lblName = new JLabel(getMsg("label.psname"), JLabel.TRAILING);
            lblName.setDisplayedMnemonic(getMnemonic("label.psname"));
            lblName.setLabelFor(cbName);
            addToGB(lblName, this, gridbag, c);
            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.RELATIVE;
            addToGB(cbName, this, gridbag, c);
            c.weightx = 0.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            btnProperties = createButton("button.properties", this);
            addToGB(btnProperties, this, gridbag, c);

            c.weighty = 1.0;
            lblStatus = addLabel(getMsg("label.status"), gridbag, c);
            lblStatus.setLabelFor(null);

            lblType = addLabel(getMsg("label.pstype"), gridbag, c);
            lblType.setLabelFor(null);

            c.gridwidth = 1;
            addToGB(new JLabel(getMsg("label.info"), JLabel.TRAILING),
                    this, gridbag, c);
            c.gridwidth = GridBagConstraints.RELATIVE;
            lblInfo = new JLabel();
            lblInfo.setForeground(Color.black);
            lblInfo.setLabelFor(null);

            addToGB(lblInfo, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            cbPrintToFile = createCheckBox("checkbox.printtofile", this);
            addToGB(cbPrintToFile, this, gridbag, c);
        }

        public boolean isPrintToFileSelected()
        {
            return cbPrintToFile.isSelected();
        }

        private JLabel addLabel(String text,
                                GridBagLayout gridbag, GridBagConstraints c)
        {
            c.gridwidth = 1;
            addToGB(new JLabel(text, JLabel.TRAILING), this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            JLabel label = new JLabel();
            label.setForeground(Color.black);
            addToGB(label, this, gridbag, c);

            return label;
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();

            if(source == btnProperties)
            {
                if(uiFactory != null)
                {
                    JDialog dialog = (JDialog) uiFactory.getUI(
                        ServiceUIFactory.MAIN_UIROLE,
                        ServiceUIFactory.JDIALOG_UI);

                    if(dialog != null)
                    {
                        dialog.show();
                    }
                    else
                    {
                        // REMIND: may want to notify the user why we're
                        //         disabling the button
                        btnProperties.setEnabled(false);
                    }
                }
            }
        }

        public void itemStateChanged(ItemEvent e)
        {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                int index = cbName.getSelectedIndex();

                if((index >= 0) && (index < services.length))
                {
                    if(!services[index].equals(psCurrent))
                    {
                        psCurrent = services[index];
                        uiFactory = psCurrent.getServiceUIFactory();
                        changedService = true;
                        ispschanged = true;
                        // to preserve the state of Print To File
//                        if(isPrintToFileSelected() &&
//                           psCurrent.isAttributeCategorySupported(
//                               Destination.class))
//                        {
//                            Destination dest = (Destination) asOriginal.get(
//                                Destination.class);
//                            if(dest != null)
//                            {
//                                asCurrent.add(dest);
//                            }
//                            else
//                            {
//                                asCurrent.add(
//                                    new Destination((new File("out.prn")).toURI()));
//                            }
//                        }
//                        else
//                        {
//                            asCurrent.remove(Destination.class);
//                        }
                    }
                }
            }
        }
        private boolean isPsChange()
        {
        	return ispschanged;
        }
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
            changedService = false;
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
        {
            if(changedService)
            {
                changedService = false;
                updatePanels();
            }
        }

        public void popupMenuCanceled(PopupMenuEvent e)
        {
        }

        /**
         * We disable the "Print To File" checkbox if this returns false
         */
        private boolean allowedToPrintToFile()
        {
            try
            {
                throwPrintToFile();
return true;
            }
            catch(SecurityException e)
            {
                return false;
            }
        }

        /**
         * Break this out as it may be useful when we allow API to
         * specify printing to a file. In that case its probably right
         * to throw a SecurityException if the permission is not granted.
         */
        private void throwPrintToFile()
        {
            SecurityManager security = System.getSecurityManager();
            if(security != null)
            {
                if(printToFilePermission == null)
                {
                    printToFilePermission =
                        new FilePermission("<<ALL FILES>>", "read,write");
                }
                security.checkPermission(printToFilePermission);
            }
        }

        public void updateInfo()
        {
            Class dstCategory = Destination.class;
            boolean dstSupported = false;
            boolean dstSelected = false;
            boolean dstAllowed = allowedToPrintToFile();

            // setup Destination (print-to-file) widgets
            if(psCurrent.isAttributeCategorySupported(dstCategory))
            {
                dstSupported = true;
            }
            Destination dst = (Destination) asCurrent.get(dstCategory);
            if(dst != null)
            {
                dstSelected = true;
            }
            cbPrintToFile.setEnabled(dstSupported && dstAllowed);
            cbPrintToFile.setSelected(dstSelected && dstAllowed);

            // setup PrintService information widgets
            Attribute type = psCurrent.getAttribute(PrinterMakeAndModel.class);
            if(type != null)
            {
                lblType.setText(type.toString());
            }
            Attribute status =
                psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
            if(status != null)
            {
                lblStatus.setText(getMsg(status.toString()));
            }
            Attribute info = psCurrent.getAttribute(PrinterInfo.class);
            if(info != null)
            {
                lblInfo.setText(info.toString());
            }
            btnProperties.setEnabled(uiFactory != null);
        }
    }


    private class PrintRangePanel
        extends JPanel implements ActionListener, FocusListener
    {
        private final String strTitle = getMsg("border.printrange");
        private final PageRanges prAll = new PageRanges(1, Integer.MAX_VALUE);
        private JRadioButton rbAll, rbPages, rbSelect;
        private JFormattedTextField tfRangeFrom, tfRangeTo;
        private JLabel lblRangeTo;
        private boolean prSupported;

        public PrintRangePanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.BOTH;
            c.insets = compInsets;
            c.gridwidth = GridBagConstraints.REMAINDER;

            ButtonGroup bg = new ButtonGroup();
            JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEADING));
            rbAll = createRadioButton("radiobutton.rangeall", this);
            rbAll.setSelected(true);
            bg.add(rbAll);
            pnlTop.add(rbAll);
            addToGB(pnlTop, this, gridbag, c);

            // Selection never seemed to work so I'm commenting this part.
            /*
if (isAWT) {
             JPanel pnlMiddle  =
              new JPanel(new FlowLayout(FlowLayout.LEADING));
             rbSelect =
              createRadioButton("radiobutton.selection", this);
             bg.add(rbSelect);
             pnlMiddle.add(rbSelect);
             addToGB(pnlMiddle, this, gridbag, c);
                }
             */

            JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.LEADING));
            rbPages = createRadioButton("radiobutton.rangepages", this);
            bg.add(rbPages);
            pnlBottom.add(rbPages);
            DecimalFormat format = new DecimalFormat("####0");
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);
            format.setMinimumIntegerDigits(0);
            format.setMaximumIntegerDigits(5);
            format.setParseIntegerOnly(true);
            format.setDecimalSeparatorAlwaysShown(false);
            NumberFormatter nf = new NumberFormatter(format);
            nf.setMinimum(new Integer(1));
            nf.setMaximum(new Integer(Integer.MAX_VALUE));
            nf.setAllowsInvalid(true);
            nf.setCommitsOnValidEdit(true);
            tfRangeFrom = new JFormattedTextField(nf);
            tfRangeFrom.setColumns(4);
            tfRangeFrom.setEnabled(false);
            tfRangeFrom.addActionListener(this);
            tfRangeFrom.addFocusListener(this);
            tfRangeFrom.setFocusLostBehavior(
                JFormattedTextField.PERSIST);
            tfRangeFrom.getAccessibleContext().setAccessibleName(
                getMsg("radiobutton.rangepages"));
            pnlBottom.add(tfRangeFrom);
            lblRangeTo = new JLabel(getMsg("label.rangeto"));
            lblRangeTo.setEnabled(false);
            pnlBottom.add(lblRangeTo);
            NumberFormatter nfto;
            try
            {
                nfto = (NumberFormatter) nf.clone();
            }
            catch(CloneNotSupportedException e)
            {
                nfto = new NumberFormatter();
            }
            tfRangeTo = new JFormattedTextField(nfto);
            tfRangeTo.setColumns(4);
            tfRangeTo.setEnabled(false);
            tfRangeTo.addFocusListener(this);
            tfRangeTo.getAccessibleContext().setAccessibleName(
                getMsg("label.rangeto"));
            pnlBottom.add(tfRangeTo);
            addToGB(pnlBottom, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            FOPageSelection select = FOPageSelection.ALL;

            setupRangeWidgets();

            if(source == rbAll)
            {
                asCurrent.add(prAll);
            }
            else if(source == rbSelect)
            {
                select = FOPageSelection.SELECTION;
            }
            else if(source == rbPages ||
                    source == tfRangeFrom ||
                    source == tfRangeTo)
            {
                updateRangeAttribute();
                select = FOPageSelection.RANGE;
            }

if(isAWT)
            {
                asCurrent.add(select);
            }
        }

        public void focusLost(FocusEvent e)
        {
            Object source = e.getSource();

            if((source == tfRangeFrom) || (source == tfRangeTo))
            {
                updateRangeAttribute();
            }
        }

        public void focusGained(FocusEvent e)
        {}

        private void setupRangeWidgets()
        {
            boolean rangeEnabled = (rbPages.isSelected() && prSupported);
            tfRangeFrom.setEnabled(rangeEnabled);
            tfRangeTo.setEnabled(rangeEnabled);
            lblRangeTo.setEnabled(rangeEnabled);
        }

        private void updateRangeAttribute()
        {
            String strFrom = tfRangeFrom.getText();
            String strTo = tfRangeTo.getText();

            int min;
            int max;

            try
            {
                min = Integer.parseInt(strFrom);
            }
            catch(NumberFormatException e)
            {
                min = 1;
            }

            try
            {
                max = Integer.parseInt(strTo);
            }
            catch(NumberFormatException e)
            {
                max = min;
            }

            if(min < 1)
            {
                min = 1;
                tfRangeFrom.setValue(new Integer(1));
            }

            if(max < min)
            {
                max = min;
                tfRangeTo.setValue(new Integer(min));
            }

            PageRanges pr = new PageRanges(min, max);
            asCurrent.add(pr);
        }

        public void updateInfo()
        {
            Class prCategory = PageRanges.class;
            prSupported = false;

            if(psCurrent.isAttributeCategorySupported(prCategory) ||
               isAWT)
            {
                prSupported = true;
            }

            FOPageSelection select = FOPageSelection.ALL;
            int min = 1;
            int max = 1;

            PageRanges pr = (PageRanges) asCurrent.get(prCategory);
            if(pr != null)
            {
                if(!pr.equals(prAll))
                {
                    select = FOPageSelection.RANGE;

                    int[][] members = pr.getMembers();
                    if((members.length > 0) &&
                       (members[0].length > 1))
                    {
                        min = members[0][0];
                        max = members[0][1];
                    }
                }
            }

if(isAWT)
            {
                select = (FOPageSelection) asCurrent.get(
                    FOPageSelection.class);
            }

            if(select == FOPageSelection.ALL)
            {
                rbAll.setSelected(true);
            }
            else if(select == FOPageSelection.SELECTION)
            {
                // Comment this for now -  rbSelect is not initialized
                // because Selection button is not added.
                // See PrintRangePanel above.

                //rbSelect.setSelected(true);
            }
            else
            { // RANGE
                rbPages.setSelected(true);
            }
            tfRangeFrom.setValue(new Integer(min));
            tfRangeTo.setValue(new Integer(max));
            rbAll.setEnabled(prSupported);
            rbPages.setEnabled(prSupported);
            setupRangeWidgets();
        }
    }


    private class CopiesPanel
        extends JPanel implements ActionListener, ChangeListener
    {
        private final String strTitle = getMsg("border.copies");
        private SpinnerNumberModel snModel;
        private JSpinner spinCopies;
        private JLabel lblCopies;
        private JCheckBox cbCollate;
        private boolean scSupported;

        public CopiesPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = compInsets;

            lblCopies = new JLabel(getMsg("label.numcopies"), JLabel.TRAILING);
            lblCopies.setDisplayedMnemonic(getMnemonic("label.numcopies"));
            lblCopies.getAccessibleContext().setAccessibleName(
                getMsg("label.numcopies"));
            addToGB(lblCopies, this, gridbag, c);

            snModel = new SpinnerNumberModel(1, 1, 999, 1);
            spinCopies = new JSpinner(snModel);
            lblCopies.setLabelFor(spinCopies);
// REMIND
            ((JSpinner.NumberEditor) spinCopies.getEditor()).getTextField().setColumns(3);
            spinCopies.addChangeListener(this);
            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(spinCopies, this, gridbag, c);

            cbCollate = createCheckBox("checkbox.collate", this);
            cbCollate.setEnabled(false);
            addToGB(cbCollate, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            if(cbCollate.isSelected())
            {
                asCurrent.add(SheetCollate.COLLATED);
            }
            else
            {
                asCurrent.add(SheetCollate.UNCOLLATED);
            }
        }

        public void stateChanged(ChangeEvent e)
        {
            updateCollateCB();

            asCurrent.add(new Copies(snModel.getNumber().intValue()));
        }

        private void updateCollateCB()
        {
            int num = snModel.getNumber().intValue();
if(isAWT)
            {
                cbCollate.setEnabled(true);
            }
            else
            {
                cbCollate.setEnabled((num > 1) && scSupported);
            }
        }

        public void updateInfo()
        {
            Class cpCategory = Copies.class;
            Class csCategory = CopiesSupported.class;
            Class scCategory = SheetCollate.class;
            boolean cpSupported = false;
            scSupported = false;

            // setup Copies spinner
            if(psCurrent.isAttributeCategorySupported(cpCategory))
            {
                cpSupported = true;
            }
            CopiesSupported cs =
                (CopiesSupported) psCurrent.getSupportedAttributeValues(
                    cpCategory, null, null);
            if(cs == null)
            {
                cs = new CopiesSupported(1, 999);
            }
            Copies cp = (Copies) asCurrent.get(cpCategory);
            if(cp == null)
            {
                cp = (Copies) psCurrent.getDefaultAttributeValue(cpCategory);
                if(cp == null)
                {
                    cp = new Copies(1);
                }
            }
            spinCopies.setEnabled(cpSupported);
            lblCopies.setEnabled(cpSupported);

            int[][] members = cs.getMembers();
            int min, max;
            if((members.length > 0) && (members[0].length > 0))
            {
                min = members[0][0];
                max = members[0][1];
            }
            else
            {
                min = 1;
                max = Integer.MAX_VALUE;
            }
            snModel.setMinimum(new Integer(min));
            snModel.setMaximum(new Integer(max));

            int value = cp.getValue();
            if((value < min) || (value > max))
            {
value = min;
            }
            snModel.setValue(new Integer(value));

            // setup Collate checkbox
            if(psCurrent.isAttributeCategorySupported(scCategory))
            {
                scSupported = true;
            }
            SheetCollate sc = (SheetCollate) asCurrent.get(scCategory);
            if(sc == null)
            {
                sc = (SheetCollate) psCurrent.getDefaultAttributeValue(scCategory);
                if(sc == null)
                {
                    sc = SheetCollate.UNCOLLATED;
                }
            }
            cbCollate.setSelected(sc == SheetCollate.COLLATED);
            updateCollateCB();
        }
    }




/**
     * The "Page Setup" tab.  Includes the controls for MediaSource/MediaTray,
     * OrientationRequested, and Sides.
     */
    private class PageSetupPanel
        extends JPanel
    {

        private MediaPanel pnlMedia;
        private OrientationPanel pnlOrientation;
        private MarginsPanel pnlMargins;

        public PageSetupPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);

            c.fill = GridBagConstraints.BOTH;
            c.insets = panelInsets;
            c.weightx = 1.0;
            c.weighty = 1.0;

            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlMedia = new MediaPanel();
            addToGB(pnlMedia, this, gridbag, c);

            pnlOrientation = new OrientationPanel();
            c.gridwidth = GridBagConstraints.RELATIVE;
            addToGB(pnlOrientation, this, gridbag, c);

            pnlMargins = new MarginsPanel();
            pnlOrientation.addOrientationListener(pnlMargins);
            pnlMedia.addMediaListener(pnlMargins);
            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(pnlMargins, this, gridbag, c);
        }

        public void updateInfo()
        {
            pnlMedia.updateInfo();
            pnlOrientation.updateInfo();
            pnlMargins.updateInfo();
        }
    }


    private class MarginsPanel
        extends JPanel implements ActionListener, FocusListener
    {

        private final String strTitle = getMsg("border.margins");
        private JFormattedTextField leftMargin, rightMargin,
        topMargin, bottomMargin;
        private JLabel lblLeft, lblRight, lblTop, lblBottom;
        private int units = MediaPrintableArea.MM;
        // storage for the last margin values calculated, -ve is uninitialised
        private float lmVal = -1f, rmVal = -1f, tmVal = -1f, bmVal = -1f;
        // storage for margins as objects mapped into orientation for display
        private Float lmObj, rmObj, tmObj, bmObj;

        public MarginsPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.weighty = 0.0;
            c.insets = compInsets;

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            String unitsKey = "label.millimetres";
            String defaultCountry = Locale.getDefault().getCountry();
            if(defaultCountry != null &&
               (defaultCountry.equals("") ||
                defaultCountry.equals(Locale.US.getCountry()) ||
                defaultCountry.equals(Locale.CANADA.getCountry())))
            {
                unitsKey = "label.inches";
                units = MediaPrintableArea.INCH;
            }
            String unitsMsg = getMsg(unitsKey);

            DecimalFormat format;
            if(units == MediaPrintableArea.MM)
            {
                format = new DecimalFormat("###.##");
                format.setMaximumIntegerDigits(3);
            }
            else
            {
                format = new DecimalFormat("##.##");
                format.setMaximumIntegerDigits(2);
            }

            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(2);
            format.setMinimumIntegerDigits(1);
            format.setParseIntegerOnly(false);
            format.setDecimalSeparatorAlwaysShown(true);
            NumberFormatter nf = new NumberFormatter(format);
            nf.setMinimum(new Float(0.0f));
            nf.setMaximum(new Float(999.0f));
            nf.setAllowsInvalid(true);
            nf.setCommitsOnValidEdit(true);

            leftMargin = new JFormattedTextField(nf);
            leftMargin.addFocusListener(this);
            leftMargin.addActionListener(this);
            leftMargin.getAccessibleContext().setAccessibleName(
                getMsg("label.leftmargin"));
            rightMargin = new JFormattedTextField(nf);
            rightMargin.addFocusListener(this);
            rightMargin.addActionListener(this);
            rightMargin.getAccessibleContext().setAccessibleName(
                getMsg("label.rightmargin"));
            topMargin = new JFormattedTextField(nf);
            topMargin.addFocusListener(this);
            topMargin.addActionListener(this);
            topMargin.getAccessibleContext().setAccessibleName(
                getMsg("label.topmargin"));
            topMargin = new JFormattedTextField(nf);
            bottomMargin = new JFormattedTextField(nf);
            bottomMargin.addFocusListener(this);
            bottomMargin.addActionListener(this);
            bottomMargin.getAccessibleContext().setAccessibleName(
                getMsg("label.bottommargin"));
            topMargin = new JFormattedTextField(nf);
            c.gridwidth = GridBagConstraints.RELATIVE;
            lblLeft = new JLabel(getMsg("label.leftmargin") + " " + unitsMsg,
                                 JLabel.LEADING);
            lblLeft.setDisplayedMnemonic(getMnemonic("label.leftmargin"));
            lblLeft.setLabelFor(leftMargin);
            addToGB(lblLeft, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            lblRight = new JLabel(getMsg("label.rightmargin") + " " + unitsMsg,
                                  JLabel.LEADING);
            lblRight.setDisplayedMnemonic(getMnemonic("label.rightmargin"));
            lblRight.setLabelFor(rightMargin);
            addToGB(lblRight, this, gridbag, c);

            c.gridwidth = GridBagConstraints.RELATIVE;
            addToGB(leftMargin, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(rightMargin, this, gridbag, c);

            // add an invisible spacing component.
            addToGB(new JPanel(), this, gridbag, c);

            c.gridwidth = GridBagConstraints.RELATIVE;
            lblTop = new JLabel(getMsg("label.topmargin") + " " + unitsMsg,
                                JLabel.LEADING);
            lblTop.setDisplayedMnemonic(getMnemonic("label.topmargin"));
            lblTop.setLabelFor(topMargin);
            addToGB(lblTop, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            lblBottom = new JLabel(getMsg("label.bottommargin") +
                                   " " + unitsMsg, JLabel.LEADING);
            lblBottom.setDisplayedMnemonic(getMnemonic("label.bottommargin"));
            lblBottom.setLabelFor(bottomMargin);
            addToGB(lblBottom, this, gridbag, c);

            c.gridwidth = GridBagConstraints.RELATIVE;
            addToGB(topMargin, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(bottomMargin, this, gridbag, c);

        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            updateMargins(source);
        }

        public void focusLost(FocusEvent e)
        {
            Object source = e.getSource();
            updateMargins(source);
        }

        public void focusGained(FocusEvent e)
        {}

        /* Get the numbers, use to create a MPA.
         * If its valid, accept it and update the attribute set.
         * If its not valid, then reject it and call updateInfo()
         * to re-establish the previous entries.
         */
        public void updateMargins(Object source)
        {
            if(!(source instanceof JFormattedTextField))
            {
                return;
            }
            else
            {
                JFormattedTextField tf = (JFormattedTextField) source;
                Float val = (Float) tf.getValue();
                if(val == null)
                {
                    return;
                }
                if(tf == leftMargin && val.equals(lmObj))
                {
                    return;
                }
                if(tf == rightMargin && val.equals(rmObj))
                {
                    return;
                }
                if(tf == topMargin && val.equals(tmObj))
                {
                    return;
                }
                if(tf == bottomMargin && val.equals(bmObj))
                {
                    return;
                }
            }

            Float lmTmpObj = (Float) leftMargin.getValue();
            Float rmTmpObj = (Float) rightMargin.getValue();
            Float tmTmpObj = (Float) topMargin.getValue();
            Float bmTmpObj = (Float) bottomMargin.getValue();

            float lm = lmTmpObj.floatValue();
            float rm = rmTmpObj.floatValue();
            float tm = tmTmpObj.floatValue();
            float bm = bmTmpObj.floatValue();

            /* adjust for orientation */
            Class orCategory = OrientationRequested.class;
            OrientationRequested or =
                (OrientationRequested) asCurrent.get(orCategory);

            if(or == null)
            {
                or = (OrientationRequested)
                     psCurrent.getDefaultAttributeValue(orCategory);
            }

            float tmp;
            if(or == OrientationRequested.REVERSE_PORTRAIT)
            {
                tmp = lm;
                lm = rm;
                rm = tmp;
                tmp = tm;
                tm = bm;
                bm = tmp;
            }
            else if(or == OrientationRequested.LANDSCAPE)
            {
                tmp = lm;
                lm = tm;
                tm = rm;
                rm = bm;
                bm = tmp;
            }
            else if(or == OrientationRequested.REVERSE_LANDSCAPE)
            {
                tmp = lm;
                lm = bm;
                bm = rm;
                rm = tm;
                tm = tmp;
            }
            MediaPrintableArea mpa;
            if((mpa = validateMargins(lm, rm, tm, bm)) != null)
            {
                asCurrent.add(mpa);
                lmVal = lm;
                rmVal = rm;
                tmVal = tm;
                bmVal = bm;
                lmObj = lmTmpObj;
                rmObj = rmTmpObj;
                tmObj = tmTmpObj;
                bmObj = bmTmpObj;
            }
            else
            {
                if(lmObj == null || rmObj == null ||
                   tmObj == null || rmObj == null)
                {
                    return;
                }
                else
                {
                    leftMargin.setValue(lmObj);
                    rightMargin.setValue(rmObj);
                    topMargin.setValue(tmObj);
                    bottomMargin.setValue(bmObj);

                }
            }
        }

        /*
         * This method either accepts the values and creates a new
         * MediaPrintableArea, or does nothing.
         * It should not attempt to create a printable area from anything
         * other than the exact values passed in.
         * But REMIND/TBD: it would be user friendly to replace margins the
         * user entered but are out of bounds with the minimum.
         * At that point this method will need to take responsibility for
         * updating the "stored" values and the UI.
         */
        private MediaPrintableArea validateMargins(float lm, float rm,
                                                   float tm, float bm)
        {

            Class mpaCategory = MediaPrintableArea.class;
            MediaPrintableArea mpa;
            MediaPrintableArea mpaMax = null;
            MediaSize mediaSize = null;

            Media media = (Media) asCurrent.get(Media.class);
            if(media == null || !(media instanceof MediaSizeName))
            {
                media = (Media) psCurrent.getDefaultAttributeValue(Media.class);
            }
            if(media != null && (media instanceof MediaSizeName))
            {
                MediaSizeName msn = (MediaSizeName) media;
                mediaSize = MediaSize.getMediaSizeForName(msn);
            }
            if(mediaSize == null)
            {
                mediaSize = new MediaSize(8.5f, 11f, Size2DSyntax.INCH);
            }

            if(media != null)
            {
                PrintRequestAttributeSet tmpASet =
                    new HashPrintRequestAttributeSet(asCurrent);
                tmpASet.add(media);

                Object values =
                    psCurrent.getSupportedAttributeValues(mpaCategory,
                                                          docFlavor,
                                                          tmpASet);
                if(values instanceof MediaPrintableArea[] &&
                   ((MediaPrintableArea[]) values).length > 0)
                {
                    mpaMax = ((MediaPrintableArea[]) values)[0];

                }
            }
            if(mpaMax == null)
            {
                mpaMax = new MediaPrintableArea(0f, 0f,
                                                mediaSize.getX(units),
                                                mediaSize.getY(units),
                                                units);
            }

            float wid = mediaSize.getX(units);
            float hgt = mediaSize.getY(units);
            float pax = lm;
            float pay = tm;
            float paw = wid - lm - rm;
            float pah = hgt - tm - bm;

            if(paw <= 0f || pah <= 0f || pax < 0f || pay < 0f ||
               pax < mpaMax.getX(units) || paw > mpaMax.getWidth(units) ||
               pay < mpaMax.getY(units) || pah > mpaMax.getHeight(units))
            {
return null;
            }
            else
            {
                return new MediaPrintableArea(lm, tm, paw, pah, units);
            }
        }

        /* This is complex as a MediaPrintableArea is valid only within
         * a particular context of media size.
         * So we need a MediaSize as well as a MediaPrintableArea.
         * MediaSize can be obtained from MediaSizeName.
         * If the application specifies a MediaPrintableArea, we accept it
         * to the extent its valid for the Media they specify. If they
         * don't specify a Media, then the default is assumed.
         *
         * If an application doesn't define a MediaPrintableArea, we need to
         * create a suitable one, this is created using the specified (or
         * default) Media and default 1 inch margins. This is validated
         * against the paper in case this is too large for tiny media.
         */
        public void updateInfo()
        {

if(isAWT)
            {
                leftMargin.setEnabled(false);
                rightMargin.setEnabled(false);
                topMargin.setEnabled(false);
                bottomMargin.setEnabled(false);
                lblLeft.setEnabled(false);
                lblRight.setEnabled(false);
                lblTop.setEnabled(false);
                lblBottom.setEnabled(false);
                return;
            }

            Class mpaCategory = MediaPrintableArea.class;
            MediaPrintableArea mpa =
                (MediaPrintableArea) asCurrent.get(mpaCategory);
            MediaPrintableArea mpaMax = null;
            MediaSize mediaSize = null;

            Media media = (Media) asCurrent.get(Media.class);
            if(media == null || !(media instanceof MediaSizeName))
            {
                media = (Media) psCurrent.getDefaultAttributeValue(Media.class);
            }
            if(media != null && (media instanceof MediaSizeName))
            {
                MediaSizeName msn = (MediaSizeName) media;
                mediaSize = MediaSize.getMediaSizeForName(msn);
            }
            if(mediaSize == null)
            {
                mediaSize = new MediaSize(8.5f, 11f, Size2DSyntax.INCH);
            }

            if(media != null)
            {
                PrintRequestAttributeSet tmpASet =
                    new HashPrintRequestAttributeSet(asCurrent);
                tmpASet.add(media);

                Object values =
                    psCurrent.getSupportedAttributeValues(mpaCategory,
                                                          docFlavor,
                                                          tmpASet);
                if(values instanceof MediaPrintableArea[] &&
                   ((MediaPrintableArea[]) values).length > 0)
                {
                    mpaMax = ((MediaPrintableArea[]) values)[0];

                }
                else if(values instanceof MediaPrintableArea)
                {
                    mpaMax = (MediaPrintableArea) values;
                }
            }
            if(mpaMax == null)
            {
                mpaMax = new MediaPrintableArea(0f, 0f,
                                                mediaSize.getX(units),
                                                mediaSize.getY(units),
                                                units);
            }

            /*
             * At this point we now know as best we can :-
             * - the media size
             * - the maximum corresponding printable area
             * - the media printable area specified by the client, if any.
             * The next step is to create a default MPA if none was specified.
             * 1" margins are used unless they are disproportionately
             * large compared to the size of the media.
             */

            float wid = mediaSize.getX(MediaPrintableArea.INCH);
            float hgt = mediaSize.getY(MediaPrintableArea.INCH);
            float maxMarginRatio = 5f;
            float xMgn, yMgn;
            if(wid > maxMarginRatio)
            {
                xMgn = 0f;
            }
            else
            {
                xMgn = wid / maxMarginRatio;
            }
            if(hgt > maxMarginRatio)
            {
                yMgn = 0f;
            }
            else
            {
                yMgn = hgt / maxMarginRatio;
            }

            if(mpa == null)
            {
                mpa = new MediaPrintableArea(xMgn, yMgn,
                                             wid - (2 * xMgn), hgt - (2 * yMgn),
                                             MediaPrintableArea.INCH);
                asCurrent.add(mpa);
            }
            float pax = mpa.getX(units);
            float pay = mpa.getY(units);
            float paw = mpa.getWidth(units);
            float pah = mpa.getHeight(units);
            float paxMax = mpaMax.getX(units);
            float payMax = mpaMax.getY(units);
            float pawMax = mpaMax.getWidth(units);
            float pahMax = mpaMax.getHeight(units);


            boolean invalid = false;

            // If the paper is set to something which is too small to
            // accommodate a specified printable area, perhaps carried
            // over from a larger paper, the adjustment that needs to be
            // performed should seem the most natural from a user's viewpoint.
            // Since the user is specifying margins, then we are biased
            // towards keeping the margins as close to what is specified as
            // possible, shrinking or growing the printable area.
            // But the API uses printable area, so you need to know the
            // media size in which the margins were previously interpreted,
            // or at least have a record of the margins.
            // In the case that this is the creation of this UI we do not
            // have this record, so we are somewhat reliant on the client
            // to supply a reasonable default
            wid = mediaSize.getX(units);
            hgt = mediaSize.getY(units);
            if(lmVal >= 0f)
            {
                invalid = true;

                if(lmVal + rmVal > wid)
                {
                    // margins impossible, but maintain P.A if can
                    if(paw > pawMax)
                    {
                        paw = pawMax;
                    }
                    // try to centre the printable area.
                    pax = (wid - paw) / 2f;
                }
                else
                {
                    pax = (lmVal >= paxMax) ? lmVal : paxMax;
                    paw = wid - pax - rmVal;
                }
                if(tmVal + bmVal > hgt)
                {
                    if(pah > pahMax)
                    {
                        pah = pahMax;
                    }
                    pay = (hgt - pah) / 2f;
                }
                else
                {
                    pay = (tmVal >= payMax) ? tmVal : payMax;
                    pah = hgt - pay - bmVal;
                }
            }
            if(pax < paxMax)
            {
                invalid = true;
                pax = paxMax;
            }
            if(pay < payMax)
            {
                invalid = true;
                pay = payMax;
            }
            if(paw > pawMax)
            {
                invalid = true;
                paw = pawMax;
            }
            if(pah > pahMax)
            {
                invalid = true;
                pah = pahMax;
            }

            if((pax + paw > paxMax + pawMax) || (paw <= 0f))
            {
                invalid = true;
                pax = paxMax;
                paw = pawMax;
            }
            if((pay + pah > payMax + pahMax) || (pah <= 0f))
            {
                invalid = true;
                pay = payMax;
                pah = pahMax;
            }

            if(invalid)
            {
                mpa = new MediaPrintableArea(pax, pay, paw, pah, units);
                asCurrent.add(mpa);
            }

            /* We now have a valid printable area.
             * Turn it into margins, using the mediaSize
             */
            lmVal = pax;
            tmVal = pay;
            rmVal = mediaSize.getX(units) - pax - paw;
            bmVal = mediaSize.getY(units) - pay - pah;

            lmObj = new Float(lmVal);
            rmObj = new Float(rmVal);
            tmObj = new Float(tmVal);
            bmObj = new Float(bmVal);

            /* Now we know the values to use, we need to assign them
             * to the fields appropriate for the orientation.
             * Note: if orientation changes this method must be called.
             */
            Class orCategory = OrientationRequested.class;
            OrientationRequested or =
                (OrientationRequested) asCurrent.get(orCategory);

            if(or == null)
            {
                or = (OrientationRequested)
                     psCurrent.getDefaultAttributeValue(orCategory);
            }

            Float tmp;

            if(or == OrientationRequested.REVERSE_PORTRAIT)
            {
tmp = lmObj;
                lmObj = rmObj;
rmObj = tmp;
tmp = tmObj;
                tmObj = bmObj;
bmObj = tmp;
            }
            else if(or == OrientationRequested.LANDSCAPE)
            {
tmp = lmObj;
                lmObj = bmObj;
                bmObj = rmObj;
                rmObj = tmObj;
tmObj = tmp;
            }
            else if(or == OrientationRequested.REVERSE_LANDSCAPE)
            {
tmp = lmObj;
                lmObj = tmObj;
                tmObj = rmObj;
                rmObj = bmObj;
bmObj = tmp;
            }

            leftMargin.setValue(lmObj);
            rightMargin.setValue(rmObj);
            topMargin.setValue(tmObj);
            bottomMargin.setValue(bmObj);
        }
    }



    private class MediaPanel
        extends JPanel implements ItemListener
    {

        private final String strTitle = getMsg("border.media");
        private JLabel lblSize, lblSource;
        private JComboBox cbSize, cbSource;
        private Vector sizes = new Vector();
        private Vector sources = new Vector();
        private MarginsPanel pnlMargins = null;

        public MediaPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            cbSize = new JComboBox();
            cbSource = new JComboBox();

            c.fill = GridBagConstraints.BOTH;
            c.insets = compInsets;
            c.weighty = 1.0;

            c.weightx = 0.0;
            lblSize = new JLabel(getMsg("label.size"), JLabel.TRAILING);
            lblSize.setDisplayedMnemonic(getMnemonic("label.size"));
            lblSize.setLabelFor(cbSize);
            addToGB(lblSize, this, gridbag, c);
            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(cbSize, this, gridbag, c);

            c.weightx = 0.0;
            c.gridwidth = 1;
            lblSource = new JLabel(getMsg("label.source"), JLabel.TRAILING);
            lblSource.setDisplayedMnemonic(getMnemonic("label.source"));
            lblSource.setLabelFor(cbSource);
            addToGB(lblSource, this, gridbag, c);
            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(cbSource, this, gridbag, c);
        }

        private String getMediaName(String key)
        {
            try
            {
                // replace characters that would be invalid in
                // a resource key with valid characters
                String newkey = key.replace(' ', '-');
                newkey = newkey.replace('#', 'n');

                return messageRB.getString(newkey);
            }
            catch(java.util.MissingResourceException e)
            {
                return key;
            }
        }

        public void itemStateChanged(ItemEvent e)
        {
            Object source = e.getSource();

            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                if(source == cbSize)
                {
                    int index = cbSize.getSelectedIndex();

                    if((index >= 0) && (index < sizes.size()))
                    {
                        if((cbSource.getItemCount() > 1) &&
                           (cbSource.getSelectedIndex() >= 1))
                        {
                            int src = cbSource.getSelectedIndex() - 1;
                            MediaTray mt = (MediaTray) sources.get(src);
                            asCurrent.add(new SunAlternateMedia(mt));
                        }
                        asCurrent.add((MediaSizeName) sizes.get(index));
                    }
                }
                else if(source == cbSource)
                {
                    int index = cbSource.getSelectedIndex();

                    if((index >= 1) && (index < (sources.size() + 1)))
                    {
                        asCurrent.remove(SunAlternateMedia.class);
                        asCurrent.add((MediaTray) sources.get(index - 1));
                    }
                    else if(index == 0)
                    {
                        asCurrent.remove(SunAlternateMedia.class);
                        if(cbSize.getItemCount() > 0)
                        {
                            int size = cbSize.getSelectedIndex();
                            asCurrent.add((MediaSizeName) sizes.get(size));
                        }
                    }
                }
                // orientation affects display of margins.
                if(pnlMargins != null)
                {
                    pnlMargins.updateInfo();
                }
            }
        }


        /* this is ad hoc to keep things simple */
        public void addMediaListener(MarginsPanel pnl)
        {
            pnlMargins = pnl;
        }

        public void updateInfo()
        {
            Class mdCategory = Media.class;
            Class amCategory = SunAlternateMedia.class;
            boolean mediaSupported = false;

            cbSize.removeItemListener(this);
            cbSize.removeAllItems();
            cbSource.removeItemListener(this);
            cbSource.removeAllItems();
            cbSource.addItem(getMediaName("auto-select"));

            sizes.clear();
            sources.clear();

            if(psCurrent.isAttributeCategorySupported(mdCategory))
            {
                mediaSupported = true;

                Object values =
                    psCurrent.getSupportedAttributeValues(mdCategory,
                                                          docFlavor,
                                                          asCurrent);

                if(values instanceof Media[])
                {
                    Media[] media = (Media[]) values;

                    for(int i = 0; i < media.length; i++)
                    {
                        Media medium = media[i];

                        if(medium instanceof MediaSizeName)
                        {
                            sizes.add(medium);
                            cbSize.addItem(getMediaName(medium.toString()));
                        }
                        else if(medium instanceof MediaTray)
                        {
                            sources.add(medium);
                            cbSource.addItem(getMediaName(medium.toString()));
                        }
                    }
                }
            }

            boolean msSupported = (mediaSupported && (sizes.size() > 0));
            lblSize.setEnabled(msSupported);
            cbSize.setEnabled(msSupported);

if(isAWT)
            {
                cbSource.setEnabled(false);
                lblSource.setEnabled(false);
            }
            else
            {
                cbSource.setEnabled(mediaSupported);
            }

            if(mediaSupported)
            {

                Media medium = (Media) asCurrent.get(mdCategory);

                if(medium == null ||
                   !psCurrent.isAttributeValueSupported(medium,
                                                        docFlavor, asCurrent))
                {

                    medium = (Media) psCurrent.getDefaultAttributeValue(mdCategory);

                    if(medium == null)
                    {
                        if(sizes.size() > 0)
                        {
                            medium = (Media) sizes.get(0);
                        }
                    }
                    if(medium != null)
                    {
                        asCurrent.add(medium);
                    }
                }
                if(medium != null)
                {
                    if(medium instanceof MediaSizeName)
                    {
                        MediaSizeName ms = (MediaSizeName) medium;
                        cbSize.setSelectedIndex(sizes.indexOf(ms));
                    }
                    else if(medium instanceof MediaTray)
                    {
                        MediaTray mt = (MediaTray) medium;
                        cbSource.setSelectedIndex(sources.indexOf(mt) + 1);
                    }
                }
                else
                {
                    cbSize.setSelectedIndex(sizes.size() > 0 ? 0 : -1);
                    cbSource.setSelectedIndex(0);
                }

                SunAlternateMedia alt = (SunAlternateMedia) asCurrent.get(amCategory);
                if(alt != null)
                {
                    Media md = alt.getMedia();
                    if(md instanceof MediaTray)
                    {
                        MediaTray mt = (MediaTray) md;
                        cbSource.setSelectedIndex(sources.indexOf(mt) + 1);
                    }
                }

                int selIndex = cbSize.getSelectedIndex();
                if((selIndex >= 0) && (selIndex < sizes.size()))
                {
                    asCurrent.add((MediaSizeName) sizes.get(selIndex));
                }

                selIndex = cbSource.getSelectedIndex();
                if((selIndex >= 1) && (selIndex < (sources.size() + 1)))
                {
                    asCurrent.add((MediaTray) sources.get(selIndex - 1));
                }


            }
            cbSize.addItemListener(this);
            cbSource.addItemListener(this);
        }
    }


    private class OrientationPanel
        extends JPanel implements ActionListener
    {
        private final String strTitle = getMsg("border.orientation");
        private IconRadioButton rbPortrait, rbLandscape,
        rbRevPortrait, rbRevLandscape;
        private MarginsPanel pnlMargins = null;

        public OrientationPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.BOTH;
            c.insets = compInsets;
            c.weighty = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;

            ButtonGroup bg = new ButtonGroup();
            rbPortrait = new IconRadioButton("radiobutton.portrait",
                                             "orientPortrait.gif", true,
                                             bg, this);
            rbPortrait.addActionListener(this);
            addToGB(rbPortrait, this, gridbag, c);
            rbLandscape = new IconRadioButton("radiobutton.landscape",
                                              "orientLandscape.gif", false,
                                              bg, this);
            rbLandscape.addActionListener(this);
            addToGB(rbLandscape, this, gridbag, c);
            rbRevPortrait = new IconRadioButton("radiobutton.revportrait",
                                                "orientRevPortrait.gif", false,
                                                bg, this);
            rbRevPortrait.addActionListener(this);
            addToGB(rbRevPortrait, this, gridbag, c);
            rbRevLandscape = new IconRadioButton("radiobutton.revlandscape",
                                                 "orientRevLandscape.gif", false,
                                                 bg, this);
            rbRevLandscape.addActionListener(this);
            addToGB(rbRevLandscape, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();

            if(rbPortrait.isSameAs(source))
            {
                asCurrent.add(OrientationRequested.PORTRAIT);
            }
            else if(rbLandscape.isSameAs(source))
            {
                asCurrent.add(OrientationRequested.LANDSCAPE);
            }
            else if(rbRevPortrait.isSameAs(source))
            {
                asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
            }
            else if(rbRevLandscape.isSameAs(source))
            {
                asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
            }
            // orientation affects display of margins.
            if(pnlMargins != null)
            {
                pnlMargins.updateInfo();
            }
        }

        /* This is ad hoc to keep things simple */
        void addOrientationListener(MarginsPanel pnl)
        {
            pnlMargins = pnl;
        }

        public void updateInfo()
        {
            Class orCategory = OrientationRequested.class;
            boolean pSupported = false;
            boolean lSupported = false;
            boolean rpSupported = false;
            boolean rlSupported = false;

if(isAWT)
            {
                pSupported = true;
                lSupported = true;
            }
            else
            if(psCurrent.isAttributeCategorySupported(orCategory))
            {
                Object values =
                    psCurrent.getSupportedAttributeValues(orCategory,
                                                          docFlavor,
                                                          asCurrent);

                if(values instanceof OrientationRequested[])
                {
                    OrientationRequested[] ovalues =
                        (OrientationRequested[]) values;

                    for(int i = 0; i < ovalues.length; i++)
                    {
                        OrientationRequested value = ovalues[i];

                        if(value == OrientationRequested.PORTRAIT)
                        {
                            pSupported = true;
                        }
                        else if(value == OrientationRequested.LANDSCAPE)
                        {
                            lSupported = true;
                        }
                        else if(value == OrientationRequested.REVERSE_PORTRAIT)
                        {
                            rpSupported = true;
                        }
                        else if(value == OrientationRequested.REVERSE_LANDSCAPE)
                        {
                            rlSupported = true;
                        }
                    }
                }
            }
            rbPortrait.setEnabled(pSupported);
            rbLandscape.setEnabled(lSupported);
            rbRevPortrait.setEnabled(rpSupported);
            rbRevLandscape.setEnabled(rlSupported);

            OrientationRequested or = (OrientationRequested) asCurrent.get(orCategory);
            if(or == null ||
               !psCurrent.isAttributeValueSupported(or, docFlavor, asCurrent))
            {

                or = (OrientationRequested) psCurrent.getDefaultAttributeValue(orCategory);
                // need to validate if default is not supported
                if(!psCurrent.isAttributeValueSupported(or, docFlavor, asCurrent))
                {
                    or = null;
Object values =
                        psCurrent.getSupportedAttributeValues(orCategory,
                                                              docFlavor,
                                                              asCurrent);
                    if(values instanceof OrientationRequested[])
                    {
                        OrientationRequested[] orValues =
                            (OrientationRequested[]) values;
                        if(orValues.length > 1)
                        {
                            // get the first in the list
                            or = orValues[0];
                        }
                    }
                }

                if(or == null)
                {
                    or = OrientationRequested.PORTRAIT;
                }
                asCurrent.add(or);
            }

            if(or == OrientationRequested.PORTRAIT)
            {
                rbPortrait.setSelected(true);
            }
            else if(or == OrientationRequested.LANDSCAPE)
            {
                rbLandscape.setSelected(true);
            }
            else if(or == OrientationRequested.REVERSE_PORTRAIT)
            {
                rbRevPortrait.setSelected(true);
            }
            else
            { // if (or == OrientationRequested.REVERSE_LANDSCAPE)
                rbRevLandscape.setSelected(true);
            }
        }
    }


    /***
     * 增加一个属性面版，可以调节偏打印的移量和缩放比例
     * */

    private class MyExcursion
        extends JPanel implements FocusListener
    {
        float i, j, k, l,m;
        boolean ischexbox;
        String str = "";
        int units = MediaPrintableArea.MM;
        FOPrintPageSetup myMediaPrintableArea;
        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        JPanel jPanel3 = new JPanel();

        JLabel jLabel1_left = new JLabel(getMsg("excursion.left"));
        JLabel jLabel2_top = new JLabel(getMsg("excursion.top"));
        JLabel jLabel3_horizontal = new JLabel(getMsg("scale.horizontal"));
        JLabel jLabel4_vertical = new JLabel(getMsg("scale.vertical"));
        GridLayout gridLayout1 = new GridLayout(3, 1);
        JTextField topText;
        JTextField leftText;
        JTextField XzoomText;
        JTextField YzoomText;
        GridBagLayout gridBagLayout1 = new GridBagLayout();
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        GridBagLayout gridBagLayout3 = new GridBagLayout();

        //height是否跟随放大，缩小调节
        JLabel heightLable = new JLabel("同比例调节页高");
        JCheckBox heightCheckBox = new JCheckBox("同比例");

        //height的绝对高度调节
        JLabel heightABSLable = new JLabel("走纸调节(mm)");
        //增加的绝对高度
        JTextField heightABSTextField = new JTextField();

        public MyExcursion(float top, float left, float XZoon, float YZoon)
        {
            myMediaPrintableArea = new FOPrintPageSetup(top, left, XZoon, YZoon,
                                                        FOPrintPageSetup.MM);
            asCurrent.add(myMediaPrintableArea);
            i = top;
            j = left;
            k = XZoon;
            l = YZoon;
            jbInit(top, left, XZoon, YZoon);
        }

        public MyExcursion(float top, float left, float XZoon, float YZoon,boolean ch,float h)
        {
            myMediaPrintableArea = new FOPrintPageSetup(top, left, XZoon, YZoon,
                                                        FOPrintPageSetup.MM,ch,h);
            asCurrent.add(myMediaPrintableArea);
            i = top;
            j = left;
            k = XZoon;
            l = YZoon;
            this.ischexbox = ch;
            m = h;

            //设置初始值
            heightCheckBox.setSelected(ch);
//            System.out.println("setSelected 设置副选框为 " + ch);
            heightABSTextField.setText(String.valueOf(h));

            jbInit(top, left, XZoon, YZoon);
        }

        public void focusGained(FocusEvent e)
        {
            if(topText == e.getSource())
            {
                str = topText.getText();
            }
            else if(leftText == e.getSource())
            {
                str = leftText.getText();
            }
            else if(XzoomText == e.getSource())
            {
                str = XzoomText.getText();
            }
            else if(YzoomText == e.getSource())
            {
                str = YzoomText.getText();
            }
            else if(heightABSTextField == e.getSource())
            {
                //保存初始值  need add by lizhenyou
                str = heightABSTextField.getText();
            }
            else if(heightCheckBox == e.getSource())
            {
                //保存初始值  need add by lizhenyou

            }
        }

        public void focusLost(FocusEvent e)
        {
            if(topText == e.getSource())
            {
                try
                {
                    i = Float.parseFloat(topText.getText());
                    if(i < SystemUtil.MIN_excursion)
                    {
                        i = (float) SystemUtil.MIN_excursion;
                        topText.setText(String.valueOf(i));
                    }
                    else if(i > SystemUtil.MAX_excursion)
                    {
                        i = (float) SystemUtil.MAX_excursion;
                        topText.setText(String.valueOf(i));
                    }
                }
                catch(Exception et)
                {
                    topText.setText(str);
                }
            }
            else if(leftText == e.getSource())
            {
                try
                {
                    j = Float.parseFloat(leftText.getText());
                    if(j < SystemUtil.MIN_excursion)
                    {
                        j = SystemUtil.MIN_excursion;
                        leftText.setText(String.valueOf(j));
                    }
                    else if(j > SystemUtil.MAX_excursion)
                    {
                        j = SystemUtil.MAX_excursion;
                        leftText.setText(String.valueOf(j));
                    }
                }
                catch(Exception el)
                {
                    leftText.setText(str);
                }
            }
            else if(XzoomText == e.getSource())
            {
                try
                {
                    k = Float.parseFloat(XzoomText.getText());
                    if(k < SystemUtil.MIN_PERCENT)
                    {
                        k = (float)SystemUtil.MIN_PERCENT;
                        XzoomText.setText(String.valueOf(k));
                    }
                    else if(k > SystemUtil.MAX_PERCENT)
                    {
                        k = (float)SystemUtil.MAX_PERCENT;
                        XzoomText.setText(String.valueOf(k));
                    }
                }
                catch(Exception ex)
                {
                    XzoomText.setText(str);
                }
            }
            else if(YzoomText == e.getSource())
            {
                try
                {
                    l = Float.parseFloat(YzoomText.getText());
                    if(l < SystemUtil.MIN_PERCENT)
                    {
                        l = (float)SystemUtil.MIN_PERCENT;
                        YzoomText.setText(String.valueOf(l));
                    }
                    else if(l > SystemUtil.MAX_PERCENT)
                    {
                        l = (float)SystemUtil.MAX_PERCENT;
                        YzoomText.setText(String.valueOf(l));
                    }
                }
                catch(Exception ey)
                {
                    YzoomText.setText(str);
                }
            }
            else if(heightABSTextField == e.getSource())
            {
                //范围必须在??~??之间 need add by lizhenyou
                try
                {
                    m = Float.parseFloat(heightABSTextField.getText());
                }
                catch(Exception em)
                {
                    heightABSTextField.setText(str);
                }
            }
            else if(heightCheckBox == e.getSource())
            {
                //do something
            }

//			myMediaPrintableArea = new FOPrintPageSetup(i, j, k, l, FOPrintPageSetup.MM);
            myMediaPrintableArea = new FOPrintPageSetup(i, j, k, l, FOPrintPageSetup.MM ,heightCheckBox.isSelected(),m);

//            System.out.println("失去焦点 ,heightCheckBox.isSelected() " + heightCheckBox.isSelected());
//            System.out.println("失去焦点 高度m is " + m);
            asCurrent.add(myMediaPrintableArea);
        }

        private void jbInit(float top, float left, float XZoon, float YZoon)
        {
            topText = new JTextField();
            leftText = new JTextField();
            XzoomText = new JTextField();
            YzoomText = new JTextField();

            leftText.setText(String.valueOf(left));
            topText.setText(String.valueOf(top));
            YzoomText.setText(String.valueOf(YZoon));
            XzoomText.setText(String.valueOf(XZoon));

            setLayout(gridLayout1);
            jPanel1.setLayout(gridBagLayout2);
            jPanel2.setLayout(gridBagLayout1);
            jPanel3.setLayout(gridBagLayout3);
            jPanel1.setBorder(BorderFactory.createTitledBorder(getMsg("border.excursion")));
            jPanel2.setBorder(BorderFactory.createTitledBorder(getMsg("border.scale")));
            jPanel3.setBorder(BorderFactory.createTitledBorder("走纸"));


            add(jPanel1);
            add(jPanel2);
            add(jPanel3);
            pack();

            jPanel1.add(jLabel1_left, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.CENTER,
                                                        GridBagConstraints.HORIZONTAL,
                                                        new Insets(0, 30, 5, 30), 0, 0));
            jPanel1.add(jLabel2_top    , new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.CENTER,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(5, 30, 0, 30), 0, 0));
            jPanel1.add(topText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                                                        , GridBagConstraints.CENTER,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(0, 30, 5, 118), 0, 0));
            jPanel1.add(leftText, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                                         , GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH,
                                                         new Insets(5, 30, 0, 118), 0, 0));

            jPanel2.add(jLabel3_horizontal, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.CENTER,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(0, 30, 5, 42), 0, 0));
            jPanel2.add(jLabel4_vertical, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.CENTER,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(5, 30, 0, 42), 0, 0));
            jPanel2.add(XzoomText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                                                          , GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH,
                                                          new Insets(0, 43, 15, 118), 0, 0));
            jPanel2.add(YzoomText, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                                          , GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH,
                                                          new Insets(5, 43, 0, 118), 0, 0));

            //height是否跟随放大，缩小调节
            //JLabel heightLable = new JLabel("按比例调节页高：");
            //JCheckBox heightCheckBox = new JCheckBox();

            //height的绝对高度调节
            //JLabel heightABSLable = new JLabel("高度调节(单位mm)：");
            //JTextField heightABSTextField = new JFormattedTextField("0");

            jPanel2.add(heightLable, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
                                                            , GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH,
                                                            new Insets(0, 30, 0, 30), 0, 0)
                );
            jPanel2.add(heightCheckBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
                                                               , GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH,
                                                               new Insets(0, 43, 0, 118), 0, 0)
                );


            jPanel3.add(heightABSLable, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
                                                               , GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH,
                                                               new Insets(0, 30, 15, 0), 0, 0)
                );
            jPanel3.add(heightABSTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                                                   , GridBagConstraints.CENTER,
                                                                   GridBagConstraints.BOTH,
                                                                   new Insets(0, 43, 15, 118), 0, 0)
                );



            leftText.addFocusListener(this);
            topText.addFocusListener(this);
            XzoomText.addFocusListener(this);
            YzoomText.addFocusListener(this);
            heightABSTextField.addFocusListener(this);
            heightCheckBox.addFocusListener(this);
        }
    }


/**
     * The "Appearance" tab.  Includes the controls for Chromaticity,
     * PrintQuality, JobPriority, JobName, and other related job attributes.
     */
    private class AppearancePanel
        extends JPanel
    {

        private ChromaticityPanel pnlChromaticity;
        private QualityPanel pnlQuality;
        private JobAttributesPanel pnlJobAttributes;
        private SidesPanel pnlSides;

        public AppearancePanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);

            c.fill = GridBagConstraints.BOTH;
            c.insets = panelInsets;
            c.weightx = 1.0;
            c.weighty = 1.0;

            c.gridwidth = GridBagConstraints.RELATIVE;
            pnlChromaticity = new ChromaticityPanel();
            addToGB(pnlChromaticity, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlQuality = new QualityPanel();
            addToGB(pnlQuality, this, gridbag, c);

            c.gridwidth = 1;
            pnlSides = new SidesPanel();
            addToGB(pnlSides, this, gridbag, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlJobAttributes = new JobAttributesPanel();
            addToGB(pnlJobAttributes, this, gridbag, c);

        }

        public void updateInfo()
        {
            pnlChromaticity.updateInfo();
            pnlQuality.updateInfo();
            pnlSides.updateInfo();
            pnlJobAttributes.updateInfo();
        }
    }


    private class ChromaticityPanel
        extends JPanel implements ActionListener
    {
        private final String strTitle = getMsg("border.chromaticity");
        private JRadioButton rbMonochrome, rbColor;

        public ChromaticityPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weighty = 1.0;

            ButtonGroup bg = new ButtonGroup();
            rbMonochrome = createRadioButton("radiobutton.monochrome", this);
            rbMonochrome.setSelected(true);
            bg.add(rbMonochrome);
            addToGB(rbMonochrome, this, gridbag, c);
            rbColor = createRadioButton("radiobutton.color", this);
            bg.add(rbColor);
            addToGB(rbColor, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();

            // REMIND: use isSameAs if we move to a IconRB in the future
            if(source == rbMonochrome)
            {
                asCurrent.add(Chromaticity.MONOCHROME);
            }
            else if(source == rbColor)
            {
                asCurrent.add(Chromaticity.COLOR);
            }
        }

        public void updateInfo()
        {
            Class chCategory = Chromaticity.class;
            boolean monoSupported = false;
            boolean colorSupported = false;

if(isAWT)
            {
                monoSupported = true;
                colorSupported = true;
            }
            else
            if(psCurrent.isAttributeCategorySupported(chCategory))
            {
                Object values =
                    psCurrent.getSupportedAttributeValues(chCategory,
                                                          docFlavor,
                                                          asCurrent);

                if(values instanceof Chromaticity[])
                {
                    Chromaticity[] cvalues = (Chromaticity[]) values;

                    for(int i = 0; i < cvalues.length; i++)
                    {
                        Chromaticity value = cvalues[i];

                        if(value == Chromaticity.MONOCHROME)
                        {
monoSupported = true;
                        }
                        else if(value == Chromaticity.COLOR)
                        {
                            colorSupported = true;
                        }
                    }
                }
            }


            rbMonochrome.setEnabled(monoSupported);
            rbColor.setEnabled(colorSupported);

            Chromaticity ch = (Chromaticity) asCurrent.get(chCategory);
            if(ch == null)
            {
                ch = (Chromaticity) psCurrent.getDefaultAttributeValue(chCategory);
                if(ch == null)
                {
                    ch = Chromaticity.MONOCHROME;
                }
            }

            if(ch == Chromaticity.MONOCHROME)
            {
                rbMonochrome.setSelected(true);
            }
            else
            { // if (ch == Chromaticity.COLOR)
                rbColor.setSelected(true);
            }
        }
    }


    private class QualityPanel
        extends JPanel implements ActionListener
    {
        private final String strTitle = getMsg("border.quality");
        private JRadioButton rbDraft, rbNormal, rbHigh;

        public QualityPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weighty = 1.0;

            ButtonGroup bg = new ButtonGroup();
            rbDraft = createRadioButton("radiobutton.draftq", this);
            bg.add(rbDraft);
            addToGB(rbDraft, this, gridbag, c);
            rbNormal = createRadioButton("radiobutton.normalq", this);
            rbNormal.setSelected(true);
            bg.add(rbNormal);
            addToGB(rbNormal, this, gridbag, c);
            rbHigh = createRadioButton("radiobutton.highq", this);
            bg.add(rbHigh);
            addToGB(rbHigh, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();

            if(source == rbDraft)
            {
                asCurrent.add(PrintQuality.DRAFT);
            }
            else if(source == rbNormal)
            {
                asCurrent.add(PrintQuality.NORMAL);
            }
            else if(source == rbHigh)
            {
                asCurrent.add(PrintQuality.HIGH);
            }
        }

        public void updateInfo()
        {
            Class pqCategory = PrintQuality.class;
            boolean draftSupported = false;
            boolean normalSupported = false;
            boolean highSupported = false;

if(isAWT)
            {
                draftSupported = true;
                normalSupported = true;
                highSupported = true;
            }
            else
            if(psCurrent.isAttributeCategorySupported(pqCategory))
            {
                Object values =
                    psCurrent.getSupportedAttributeValues(pqCategory,
                                                          docFlavor,
                                                          asCurrent);

                if(values instanceof PrintQuality[])
                {
                    PrintQuality[] qvalues = (PrintQuality[]) values;

                    for(int i = 0; i < qvalues.length; i++)
                    {
                        PrintQuality value = qvalues[i];

                        if(value == PrintQuality.DRAFT)
                        {
                            draftSupported = true;
                        }
                        else if(value == PrintQuality.NORMAL)
                        {
                            normalSupported = true;
                        }
                        else if(value == PrintQuality.HIGH)
                        {
highSupported = true;
                        }
                    }
                }
            }

            rbDraft.setEnabled(draftSupported);
            rbNormal.setEnabled(normalSupported);
            rbHigh.setEnabled(highSupported);

            PrintQuality pq = (PrintQuality) asCurrent.get(pqCategory);
            if(pq == null)
            {
                pq = (PrintQuality) psCurrent.getDefaultAttributeValue(pqCategory);
                if(pq == null)
                {
                    pq = PrintQuality.NORMAL;
                }
            }

            if(pq == PrintQuality.DRAFT)
            {
                rbDraft.setSelected(true);
            }
            else if(pq == PrintQuality.NORMAL)
            {
                rbNormal.setSelected(true);
            }
            else
            { // if (pq == PrintQuality.HIGH)
                rbHigh.setSelected(true);
            }
        }


    }


    private class SidesPanel
        extends JPanel implements ActionListener
    {
        private final String strTitle = getMsg("border.sides");
        private IconRadioButton rbOneSide, rbTumble, rbDuplex;

        public SidesPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.BOTH;
            c.insets = compInsets;
            c.weighty = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;

            ButtonGroup bg = new ButtonGroup();
            rbOneSide = new IconRadioButton("radiobutton.oneside",
                                            "oneside.gif", true,
                                            bg, this);
            rbOneSide.addActionListener(this);
            addToGB(rbOneSide, this, gridbag, c);
            rbTumble = new IconRadioButton("radiobutton.tumble",
                                           "tumble.gif", false,
                                           bg, this);
            rbTumble.addActionListener(this);
            addToGB(rbTumble, this, gridbag, c);
            rbDuplex = new IconRadioButton("radiobutton.duplex",
                                           "duplex.gif", false,
                                           bg, this);
            rbDuplex.addActionListener(this);
            c.gridwidth = GridBagConstraints.REMAINDER;
            addToGB(rbDuplex, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();

            if(rbOneSide.isSameAs(source))
            {
                asCurrent.add(Sides.ONE_SIDED);
            }
            else if(rbTumble.isSameAs(source))
            {
                asCurrent.add(Sides.TUMBLE);
            }
            else if(rbDuplex.isSameAs(source))
            {
                asCurrent.add(Sides.DUPLEX);
            }
        }

        public void updateInfo()
        {
            Class sdCategory = Sides.class;
            boolean osSupported = false;
            boolean tSupported = false;
            boolean dSupported = false;

            if(psCurrent.isAttributeCategorySupported(sdCategory))
            {
                Object values =
                    psCurrent.getSupportedAttributeValues(sdCategory,
                                                          docFlavor,
                                                          asCurrent);

                if(values instanceof Sides[])
                {
                    Sides[] svalues = (Sides[]) values;

                    for(int i = 0; i < svalues.length; i++)
                    {
                        Sides value = svalues[i];

                        if(value == Sides.ONE_SIDED)
                        {
                            osSupported = true;
                        }
                        else if(value == Sides.TUMBLE)
                        {
                            tSupported = true;
                        }
                        else if(value == Sides.DUPLEX)
                        {
                            dSupported = true;
                        }
                    }
                }
            }
            rbOneSide.setEnabled(osSupported);
            rbTumble.setEnabled(tSupported);
            rbDuplex.setEnabled(dSupported);

            Sides sd = (Sides) asCurrent.get(sdCategory);
            if(sd == null)
            {
                sd = (Sides) psCurrent.getDefaultAttributeValue(sdCategory);
                if(sd == null)
                {
                    sd = Sides.ONE_SIDED;
                }
            }

            if(sd == Sides.ONE_SIDED)
            {
                rbOneSide.setSelected(true);
            }
            else if(sd == Sides.TUMBLE)
            {
                rbTumble.setSelected(true);
            }
            else
            { // if (sd == Sides.DUPLEX)
                rbDuplex.setSelected(true);
            }
        }
    }



    private class JobAttributesPanel
        extends JPanel implements ActionListener, ChangeListener, FocusListener
    {
        private final String strTitle = getMsg("border.jobattributes");
        private JLabel lblPriority, lblJobName, lblUserName;
        private JSpinner spinPriority;
        private SpinnerNumberModel snModel;
        private JCheckBox cbJobSheets;
        private JTextField tfJobName, tfUserName;

        public JobAttributesPanel()
        {
            super();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            setLayout(gridbag);
            setBorder(BorderFactory.createTitledBorder(strTitle));

            c.fill = GridBagConstraints.NONE;
            c.insets = compInsets;
            c.weighty = 1.0;

            cbJobSheets = createCheckBox("checkbox.jobsheets", this);
            c.anchor = GridBagConstraints.LINE_START;
            addToGB(cbJobSheets, this, gridbag, c);

            JPanel pnlTop = new JPanel();
            lblPriority = new JLabel(getMsg("label.priority"), JLabel.TRAILING);
            lblPriority.setDisplayedMnemonic(getMnemonic("label.priority"));

            pnlTop.add(lblPriority);
            snModel = new SpinnerNumberModel(1, 1, 100, 1);
            spinPriority = new JSpinner(snModel);
            lblPriority.setLabelFor(spinPriority);
// REMIND
            ((JSpinner.NumberEditor) spinPriority.getEditor()).getTextField().setColumns(3);
            spinPriority.addChangeListener(this);
            pnlTop.add(spinPriority);
            c.anchor = GridBagConstraints.LINE_END;
            c.gridwidth = GridBagConstraints.REMAINDER;
            pnlTop.getAccessibleContext().setAccessibleName(
                getMsg("label.priority"));
            addToGB(pnlTop, this, gridbag, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weightx = 0.0;
            c.gridwidth = 1;
            char jmnemonic = getMnemonic("label.jobname");
            lblJobName = new JLabel(getMsg("label.jobname"), JLabel.TRAILING);
            lblJobName.setDisplayedMnemonic(jmnemonic);
            addToGB(lblJobName, this, gridbag, c);
            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            tfJobName = new JTextField();
            lblJobName.setLabelFor(tfJobName);
            tfJobName.addFocusListener(this);
            tfJobName.setFocusAccelerator(jmnemonic);
            tfJobName.getAccessibleContext().setAccessibleName(
                getMsg("label.jobname"));
            addToGB(tfJobName, this, gridbag, c);

            c.weightx = 0.0;
            c.gridwidth = 1;
            char umnemonic = getMnemonic("label.username");
            lblUserName = new JLabel(getMsg("label.username"), JLabel.TRAILING);
            lblUserName.setDisplayedMnemonic(umnemonic);
            addToGB(lblUserName, this, gridbag, c);
            c.gridwidth = GridBagConstraints.REMAINDER;
            tfUserName = new JTextField();
            lblUserName.setLabelFor(tfUserName);
            tfUserName.addFocusListener(this);
            tfUserName.setFocusAccelerator(umnemonic);
            tfUserName.getAccessibleContext().setAccessibleName(
                getMsg("label.username"));
            addToGB(tfUserName, this, gridbag, c);
        }

        public void actionPerformed(ActionEvent e)
        {
            if(cbJobSheets.isSelected())
            {
                asCurrent.add(JobSheets.STANDARD);
            }
            else
            {
                asCurrent.add(JobSheets.NONE);
            }
        }

        public void stateChanged(ChangeEvent e)
        {
            asCurrent.add(new JobPriority(snModel.getNumber().intValue()));
        }

        public void focusLost(FocusEvent e)
        {
            Object source = e.getSource();

            if(source == tfJobName)
            {
                asCurrent.add(new JobName(tfJobName.getText(),
                                          Locale.getDefault()));
            }
            else if(source == tfUserName)
            {
                asCurrent.add(new RequestingUserName(tfUserName.getText(),
                                                     Locale.getDefault()));
            }
        }

        public void focusGained(FocusEvent e)
        {}

        public void updateInfo()
        {
            Class jsCategory = JobSheets.class;
            Class jpCategory = JobPriority.class;
            Class jnCategory = JobName.class;
            Class unCategory = RequestingUserName.class;
            boolean jsSupported = false;
            boolean jpSupported = false;
            boolean jnSupported = false;
            boolean unSupported = false;

            // setup JobSheets checkbox
            if(psCurrent.isAttributeCategorySupported(jsCategory))
            {
                jsSupported = true;
            }
            JobSheets js = (JobSheets) asCurrent.get(jsCategory);
            if(js == null)
            {
                js = (JobSheets) psCurrent.getDefaultAttributeValue(jsCategory);
                if(js == null)
                {
                    js = JobSheets.NONE;
                }
            }
            cbJobSheets.setSelected(js != JobSheets.NONE);
            cbJobSheets.setEnabled(jsSupported);

            // setup JobPriority spinner
            if(!isAWT && psCurrent.isAttributeCategorySupported(jpCategory))
            {
                jpSupported = true;
            }
            JobPriority jp = (JobPriority) asCurrent.get(jpCategory);
            if(jp == null)
            {
                jp = (JobPriority) psCurrent.getDefaultAttributeValue(jpCategory);
                if(jp == null)
                {
                    jp = new JobPriority(1);
                }
            }
            int value = jp.getValue();
            if((value < 1) || (value > 100))
            {
                value = 1;
            }
            snModel.setValue(new Integer(value));
            lblPriority.setEnabled(jpSupported);
            spinPriority.setEnabled(jpSupported);

            // setup JobName text field
            if(psCurrent.isAttributeCategorySupported(jnCategory))
            {
                jnSupported = true;
            }
            JobName jn = (JobName) asCurrent.get(jnCategory);
            if(jn == null)
            {
                jn = (JobName) psCurrent.getDefaultAttributeValue(jnCategory);
                if(jn == null)
                {
                    jn = new JobName("", Locale.getDefault());
                }
            }
            tfJobName.setText(jn.getValue());
            tfJobName.setEnabled(jnSupported);
            lblJobName.setEnabled(jnSupported);

            // setup RequestingUserName text field
            if(!isAWT && psCurrent.isAttributeCategorySupported(unCategory))
            {
                unSupported = true;
            }
            RequestingUserName un = (RequestingUserName) asCurrent.get(unCategory);
            if(un == null)
            {
                un = (RequestingUserName) psCurrent.getDefaultAttributeValue(unCategory);
                if(un == null)
                {
                    un = new RequestingUserName("", Locale.getDefault());
                }
            }
            tfUserName.setText(un.getValue());
            tfUserName.setEnabled(unSupported);
            lblUserName.setEnabled(unSupported);
        }
    }




/**
     * A special widget that groups a JRadioButton with an associated icon,
     * placed to the left of the radio button.
     */
    private class IconRadioButton
        extends JPanel
    {

        private JRadioButton rb;
        private JLabel lbl;

        public IconRadioButton(String key, String img, boolean selected,
                               ButtonGroup bg, ActionListener al)
        {
            super(new FlowLayout(FlowLayout.LEADING));
            final URL imgURL = getImageResource(img);
            Icon icon = (Icon) java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction()
            {
                public Object run()
                {
                    Icon icon = new ImageIcon(imgURL);
                    return icon;
                }
            });
            lbl = new JLabel(icon);
add(lbl);

            rb = createRadioButton(key, al);
            rb.setSelected(selected);
            addToBG(rb, this, bg);
        }

        public void addActionListener(ActionListener al)
        {
            rb.addActionListener(al);
        }

        public boolean isSameAs(Object source)
        {
            return(rb == source);
        }

        public void setEnabled(boolean enabled)
        {
            rb.setEnabled(enabled);
            lbl.setEnabled(enabled);
        }

        public boolean isSelected()
        {
            return rb.isSelected();
        }

        public void setSelected(boolean selected)
        {
            rb.setSelected(selected);
        }
    }


/**
     * Similar in functionality to the default JFileChooser, except this
     * chooser will pop up a "Do you want to overwrite..." dialog if the
     * user selects a file that already exists.
     */
    private class ValidatingFileChooser
        extends JFileChooser
    {
        public void approveSelection()
        {
            File selected = getSelectedFile();
            boolean exists;

            try
            {
                exists = selected.exists();
            }
            catch(SecurityException e)
            {
                exists = false;
            }

            if(exists)
            {
                int val;
                val = JOptionPane.showConfirmDialog(this,
                                                    getMsg("dialog.overwrite"),
                                                    getMsg("dialog.owtitle"),
                                                    JOptionPane.YES_NO_OPTION);
                if(val == JOptionPane.YES_OPTION)
                {
                    super.approveSelection();
                }
            }
            else
            {
                super.approveSelection();
            }
        }
    }
}
