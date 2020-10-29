/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.serviceprinting.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.PropertyConfigurator;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.manager.Context;
import org.isf.serviceprinting.manager.PrintManager;
import org.isf.serviceprinting.print.Medical4Print;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @deprecated in favor of new reports StockCard and StockLedger
 */
public class MedicalPrintSelection extends JDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel selectionPanel;
	private JPanel buttonsPanel;
	private JComboBox medicalBox;
	private JComboBox medicalTypeBox;
	private JButton okButton;
	private JButton closeButton;
	private JPanel jContentPane;
	private JCheckBox isExpiring;
	private JPanel formatPanel;
	private ButtonGroup formatGroup;
	private JCheckBox javaCheck;
	private JCheckBox pdfCheck;
	private JCheckBox printCheck;
	private JCheckBox docWordCheck;
	private JCheckBox docOpenCheck;
	private String formatSelected="Java";
	
	private PrintManager printManager = Context.getApplicationContext().getBean(PrintManager.class);
	private MedicalBrowsingManager medicalBrowsingManager = Context.getApplicationContext().getBean(MedicalBrowsingManager.class);
	private MedicalTypeBrowserManager medicalManager = Context.getApplicationContext().getBean(MedicalTypeBrowserManager.class);
	
	public MedicalPrintSelection(JFrame owner){
		super(owner, true);
		initialize();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		int pfrmWidth=700;
		int pfrmHeight=300;
		setBounds(screensize.width / 4, screensize.height / 4, pfrmWidth,
				pfrmHeight);

		this.setContentPane(getJContentPane());
		this.setTitle("Movement Stock Inserting");
		pack();
	}
	private JPanel getJContentPane(){
		jContentPane=new JPanel();
		jContentPane.setLayout(new BoxLayout(jContentPane,BoxLayout.Y_AXIS));
		jContentPane.add(getSelectionPanel());
		jContentPane.add(getFormatPanel());
		jContentPane.add(getButtonsPanel());
		validate();
		return jContentPane;
	}
	private JPanel getSelectionPanel(){
		selectionPanel=new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.Y_AXIS));
		selectionPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),"Selection Panel"));
		JPanel label1Panel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel("Select a Pharmaceutical"));
		selectionPanel.add(label1Panel);
		selectionPanel.add(getMedicalBox());
		JPanel label2Panel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(new JLabel("Select a Pharmaceutical Type"));
		selectionPanel.add(label2Panel);
		selectionPanel.add(getMedicalTypeBox());
		JPanel label3Panel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		label3Panel.add(getIsExpiringCheck());
		selectionPanel.add(label3Panel);
		return selectionPanel;
	}
	private JPanel getFormatPanel(){
		formatPanel=new JPanel();
		formatPanel.setLayout(new BoxLayout(formatPanel,BoxLayout.Y_AXIS));
		formatPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),"Output"));
		javaCheck=new JCheckBox("Java Preview");
		javaCheck.setActionCommand("Java");
		javaCheck.addActionListener(this);
		javaCheck.setSelected(true);
		pdfCheck=new JCheckBox("Pdf Document");
		pdfCheck.setActionCommand("Pdf");
		pdfCheck.addActionListener(this);
		printCheck=new JCheckBox("Print");
		printCheck.setActionCommand("Print");
		printCheck.addActionListener(this);
		//docWordCheck=new JCheckBox("Doc (Word)");
		//docOpenCheck=new JCheckBox("Doc (Open Office)");
		
		formatGroup=new ButtonGroup();
		formatGroup.add(javaCheck);
		formatGroup.add(pdfCheck);
		formatGroup.add(printCheck);
		formatGroup.add(docWordCheck);
		formatGroup.add(docOpenCheck);
		
		JPanel javaCheckPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,30,0));
		javaCheckPanel.add(javaCheck);
		formatPanel.add(javaCheckPanel);
		JPanel pdfCheckPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,30,0));
		pdfCheckPanel.add(pdfCheck);
		formatPanel.add(pdfCheckPanel);
		JPanel printCheckPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,30,0));
		printCheckPanel.add(printCheck);
		formatPanel.add(printCheckPanel);
		/*JPanel docWordCheckPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,30,0));
		docWordCheckPanel.add(docWordCheck);
		formatPanel.add(docWordCheckPanel);
		JPanel docOpenCheckPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,30,0));
		docOpenCheckPanel.add(docOpenCheck);
		formatPanel.add(docOpenCheckPanel);*/
		return formatPanel;
	}
	private JPanel getButtonsPanel(){
		buttonsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		buttonsPanel.add(getOkButton());
		buttonsPanel.add(getCloseButton());
		return buttonsPanel;
	}
	private JComboBox getMedicalBox() {
		medicalBox = new JComboBox();
		ArrayList<Medical> medical;
		try {
			medical = medicalBrowsingManager.getMedicals();
		} catch (OHServiceException e1) {
			medical = null;
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
		medicalBox.addItem("All");
		if (null != medical) {
			for (Medical aMedical : medical) {
				medicalBox.addItem(aMedical);
			}
		}
		medicalBox.addMouseListener(new MouseListener() {
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				medicalBox.setEnabled(true);
				medicalBox.setEditable(true);
				medicalTypeBox.setSelectedIndex(0);
				medicalTypeBox.setEnabled(false);
			}
		});
		medicalBox.setEditable(true);
		medicalBox.setEnabled(true);
		//medicalBox.addActionListener(this);
		return medicalBox;

	}
	private JComboBox getMedicalTypeBox() {
		medicalTypeBox = new JComboBox();
		ArrayList<MedicalType> medical;
		
		medicalTypeBox.addItem("All");
		
		try {
			medical = medicalManager.getMedicalType();
			
			for (MedicalType aMedicalType : medical) {
				medicalTypeBox.addItem(aMedicalType);
			}
		} catch (OHServiceException e1) {
			OHServiceExceptionUtil.showMessages(e1);
		}
		
		medicalTypeBox.addMouseListener(new MouseListener() {
			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				medicalTypeBox.setEnabled(true);
				medicalBox.setSelectedIndex(0);
				medicalBox.setEditable(false);
				medicalBox.setEnabled(false);
			}
		});
		medicalTypeBox.setEnabled(false);
		return medicalTypeBox;
	}
	private JCheckBox getIsExpiringCheck(){
		isExpiring=new JCheckBox("Expiring Pharmaceutical");
		return isExpiring;
	}
	
	private JButton getOkButton(){
		okButton=new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Medical> pMedicals;
				ArrayList<Medical4Print> pMedicals4Print;
				boolean expiring=isExpiring.isSelected();
				String medical=null;
				if(medicalBox.getSelectedItem() instanceof Medical){
					medical=((Medical)medicalBox.getSelectedItem()).getDescription();
				}else if(!(((String)medicalBox.getSelectedItem()).equalsIgnoreCase("All")) ){
					medical=(String)medicalBox.getSelectedItem();
				}
				String medicalType=null;
				if(!(medicalTypeBox.getSelectedItem() instanceof String)){
					medicalType=((MedicalType)medicalTypeBox.getSelectedItem()).getCode();
				}
				int format = 0;
				if(formatSelected.equalsIgnoreCase("Java")){
					format=PrintManager.toDisplay;
				}else if(formatSelected.equalsIgnoreCase("Pdf")){
					format=PrintManager.toPdf;
				}else if(formatSelected.equalsIgnoreCase("Print")){
					format=PrintManager.toPrint;
				}
				try {
                    pMedicals = medicalBrowsingManager.getMedicals(medical, medicalType, expiring);
                }catch (OHServiceException ex) {
                        OHServiceExceptionUtil.showMessages(ex);
                        return;
                }

				pMedicals4Print = convertToPrint(pMedicals);
				try {
					printManager.print("pharmaceutical", pMedicals4Print,format);
				} catch (OHServiceException e1) {
					JOptionPane.showMessageDialog(MedicalPrintSelection.this, e1.getMessage());
				}
			}
		});
		return okButton;
	}
	private JButton getCloseButton(){
		closeButton=new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return closeButton;
	}
	private ArrayList<Medical4Print> convertToPrint(ArrayList<Medical> medicals){
		ArrayList<Medical4Print> toPrint=new ArrayList<Medical4Print>();
		for(Medical medical:medicals){
			toPrint.add(new Medical4Print(medical));
		}
		return toPrint;
	}
	public void actionPerformed(ActionEvent e) {
		formatSelected = e.getActionCommand();
	}
	
}
