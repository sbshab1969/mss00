package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.*;

import com.nqadmin.swingSet.datasources.*;

public class VarEdit  extends MyInternalFrame{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public VarEdit(final SSConnection conn, final long record_id){
		boolean editable = true;
		GridBagConstraints cons = new GridBagConstraints();
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);

       // setResizable(true);
        setClosable(true);
		setTitle(acp.Utils.Str("VarEditTitle"));

		if (record_id==0){
			setTitle(acp.Utils.Str("VarAddTitle"));
			editable = true;
		}else{
			setTitle(acp.Utils.Str("VarEditTitle"));
	
			try {
				String query = "select mssv_name, mssl_desc, mssv_type, mssv_last_modify";//mssv_editable";
				//String query = "select mssv_name, mssv_type, mssv_last_modify";
				//String query = "select mssv_name, mssv_type";
				query+=", mssv_valuen, mssv_valuev, mssv_valued";
				query+=" from  mss_logs, mss_vars where mssv_id="+record_id;
				rs.setCommand(query);
				rs.execute();
				rs.first();
				//if (rs.getString("MSSV_EDITABLE").equals("N")){
				if (rs.getString("mssv_last_modify").equals("N")){	
					editable = false;
				}else{
					editable = true;
				}
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		
			if (!editable){
				setTitle(acp.Utils.Str("VarEditTitle")+" ["+acp.Utils.Str("ReadOnly")+"]");
			}
		}
		
		Container cp = getContentPane();
        cp.setLayout(new GridBagLayout());
		cons.fill = GridBagConstraints.LINE_START;

		// Название
		JLabel lbl_name = new JLabel(acp.Utils.Str("Name"));
		final JTextField edt_name =new JTextField(30);
		if (record_id!=0){
			try {
				edt_name.setText(rs.getString("MSSV_NAME"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		lbl_name.setLabelFor(edt_name);
		edt_name.setEditable(editable);

		cons.gridx = 0;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_name,cons);

		cons.gridx++;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_name,cons);

		// Описание
		/*JLabel lbl_desc = new JLabel(acp.Utils.Str("Desc"));
		final JTextArea edt_desc = new JTextArea(3,30);
		if (record_id!=0){
			try {
				edt_desc.setText(rs.getString("MSSL_DESC"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		edt_desc.setLineWrap(true);
		edt_desc.setRows(3); edt_desc.setColumns(30);
		edt_desc.setBorder(new LineBorder(Color.BLACK,1));
		lbl_desc.setLabelFor(edt_desc);
		//edt_desc.setEditable(editable);
		edt_desc.setEditable(false);

		cons.gridx = 0;				cons.gridy = 1;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_desc,cons);

		cons.gridx++;				cons.gridy = 1;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_desc,cons);*/
		
		
		// Число
		final JLabel lbl_valuen = new JLabel(acp.Utils.Str("Number"));
		final JTextField edt_valuen = new JTextField(30);
		if (record_id!=0){
			try {
				edt_valuen.setText(rs.getString("MSSV_VALUEN"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		lbl_valuen.setLabelFor(edt_valuen);
		edt_valuen.setEditable(editable);

		cons.gridx = 0;				cons.gridy = 3;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_valuen,cons);

		cons.gridx++;				cons.gridy = 3;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_valuen,cons);

		// Строка
		final JLabel lbl_valuev = new JLabel(acp.Utils.Str("String"));
		final JTextField edt_valuev = new JTextField(30);
		if (record_id!=0){
			try {
				edt_valuev.setText(rs.getString("MSSV_VALUEV"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		lbl_valuev.setLabelFor(edt_valuev);
		edt_valuev.setEditable(editable);

		cons.gridx = 0;				cons.gridy = 4;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_valuev,cons);

		cons.gridx++;				cons.gridy = 4;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_valuev,cons);

		// Дата
		Date dt = null;
		if (record_id!=0){
			try {
				dt = rs.getDate("MSSV_VALUED");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}

		final JLabel lbl_valued = new JLabel(acp.Utils.Str("Date"));
		final SimpleDateFormat formatdt = new SimpleDateFormat("dd.MM.yyyy");
		final JFormattedTextField edt_valued = new JFormattedTextField(formatdt);
		if (dt!=null){
			String dt1str = formatdt.format(dt); 
			edt_valued.setText(dt1str);
		}else{
			edt_valued.setText("00.00.0000");
		}
		lbl_valued.setLabelFor(edt_valued);
		edt_valued.setEditable(editable);

		cons.gridx = 0;				cons.gridy = 5;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_valued,cons);

		cons.gridx++;				cons.gridy = 5;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_valued,cons);

		 // Тип переменной
		String tp = null;
		if (record_id!=0){
			try {
				tp = rs.getString("MSSV_TYPE");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		JLabel lbl_type = new JLabel(acp.Utils.Str("Type"));
		Vector <String> opts = new Vector<String>();
		final Vector <String> keys = new Vector<String>();
		opts.add("Число");	keys.add("N");
		opts.add("Строка");	keys.add("V");
		opts.add("Дата");	keys.add("D");
		opts.add("Комбинированный");	keys.add("U");
		@SuppressWarnings("unchecked")
		final JComboBox edt_type = new JComboBox(opts);
		edt_type.setSelectedIndex(keys.indexOf(tp));
		lbl_type.setLabelFor(edt_type);
		edt_type.setEnabled(editable);
		
		
		
		cons.gridx = 0;				cons.gridy = 2;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_type,cons);

		cons.gridx++;				cons.gridy = 2;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_type,cons);

		
		JButton btn_cancel = new JButton(acp.Utils.Str("Cancel"));
		 btn_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//VarList VarList = new VarList(conn);
				//getDesktopPane().add(VarList);
				dispose();
			}
		 });
		 edt_type.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if (keys.get(edt_type.getSelectedIndex())=="V"){
						edt_valuev.setEditable(true);
						edt_valuen.setEditable(false);
						edt_valued.setEditable(false);
					}else if (keys.get(edt_type.getSelectedIndex())=="N"){
						edt_valuen.setEditable(true);
						edt_valuev.setEditable(false);
						edt_valued.setEditable(false);
					}else if (keys.get(edt_type.getSelectedIndex())=="D"){
						edt_valued.setEditable(true);
						edt_valuev.setEditable(false);
						edt_valuen.setEditable(false);
					}else if (keys.get(edt_type.getSelectedIndex())=="U"){
						edt_valuev.setEditable(true);
						edt_valuen.setEditable(true);
						edt_valued.setEditable(true);
					}
				
					}	
				});
		  
		JButton btn_save = new JButton();
		if (record_id==0){
			btn_save.setText(acp.Utils.Str("Add"));
		}else{
			btn_save.setText(acp.Utils.Str("Save"));
		}
		btn_save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String query;
				
				if (record_id == 0){
					
					query = "insert into mss_vars ";
					String fields = "mssv_id,mssv_name,mssv_type,mssv_len";
					String values = "mssv_seq.nextval,LOWER('"+edt_name.getText()+"')";
					//values += ",'"+edt_desc.getText()+"'";
					values += ",'"+keys.get(edt_type.getSelectedIndex())+"'";
					values += ",120";
					if (keys.get(edt_type.getSelectedIndex())=="V"){
						fields +=",mssv_valuev";
						values += ",'"+edt_valuev.getText()+"'";
					}else if (keys.get(edt_type.getSelectedIndex())=="N"){
						fields +=",mssv_valuen";
						values += ","+edt_valuen.getText();
					}else if (keys.get(edt_type.getSelectedIndex())=="D"){
						fields +=",mssv_valued";
						values += ",to_date('"+edt_valued.getText()+"','dd.mm.yyyy')";
					}else if (keys.get(edt_type.getSelectedIndex())=="U"){
						fields +=",mssv_valuev,mssv_valuen,mssv_valued";
						values += ",'"+edt_valuev.getText()+"'";
						values += ","+edt_valuen.getText();
						values += ",to_date('"+edt_valued.getText()+"','dd.mm.yyyy')";
					}
					fields +=",mssv_last_modify,mssv_owner";
					values += ",sysdate,user";
					query += "("+fields+") values ("+values+")";
				}else{
					query = "update mss_vars set";
					query += " mssv_name='"+edt_name.getText()+"'";
					//query += ",mssv_desc='"+edt_desc.getText()+"'";
					query += ",mssv_type='"+keys.get(edt_type.getSelectedIndex())+"'";
					if (keys.get(edt_type.getSelectedIndex())=="V"){
						query += ",mssv_valuev='"+edt_valuev.getText()+"'";
					}
					if (keys.get(edt_type.getSelectedIndex())=="N"){
						query += ",mssv_valuen="+edt_valuen.getText();
					}
					if (keys.get(edt_type.getSelectedIndex())=="D"){
						query += ",mssv_valued=to_date('"+edt_valued.getText()+"','dd.mm.yyyy')";
					}
					if (keys.get(edt_type.getSelectedIndex())=="U"){
						query += ",mssv_valuev='"+edt_valuev.getText()+"'";
						query += ",mssv_valuen='"+edt_valuen.getText()+"'";
						query += ",mssv_valued=to_date('"+edt_valued.getText()+"','dd.mm.yyyy')";
					}
					query += ",mssv_last_modify=sysdate";
					query += ",mssv_owner=user";
					query += " where mssv_id="+record_id;
				}
				SSJdbcRowSetImpl rsq = new SSJdbcRowSetImpl(conn);
				rsq.setCommand(query);
				try {
					rsq.execute();
				} catch (SQLException e1) {
					if(e1.getMessage().contains("unique constraint")){
	        			JOptionPane.showMessageDialog(null,acp.Utils.Str("Unique"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
	        		}
				}
				dispose();
			}
		});
		btn_save.setEnabled(editable);

		cons.gridx = 0;				cons.gridy = 6;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(btn_cancel,cons);

		cons.gridx++;				cons.gridy = 6;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(btn_save,cons);
		
		pack();
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
		Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
	    setLocation(newLocation);
		doLayout();
        setVisible(true);
	}
}
