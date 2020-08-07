package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;



import com.nqadmin.swingSet.datasources.*;

public class ConstEdit  extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	public ConstEdit(final SSConnection conn, final long record_id){

		GridBagConstraints cons = new GridBagConstraints();
		
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);

       
		//setResizable(true);
        setClosable(true);
		setTitle(acp.Utils.Str("ConstEditTitle"));
		if (record_id==0){
			setTitle(acp.Utils.Str("ConstAddTitle"));
		}else{
			setTitle(acp.Utils.Str("ConstEditTitle"));
	
		try {
			String query = "select mssc_name, mssc_value";
			query+=" from mss_const where mssc_id="+record_id;
			rs.setCommand(query);
			rs.execute();
			rs.first();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		
		final Container cp = getContentPane();
        cp.setLayout(new GridBagLayout());
		cons.fill = GridBagConstraints.LINE_START;

		// Название
		JLabel lbl_name = new JLabel(acp.Utils.Str("Name"));
		final JTextField edt_name =new JTextField(30);
		if (record_id!=0){
			try {
				edt_name.setText(rs.getString("MSSC_NAME"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		lbl_name.setLabelFor(edt_name);

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

		// Значение
		JLabel lbl_value = new JLabel(acp.Utils.Str("Value"));
		final JTextField edt_value = new JTextField(30);
		if (record_id!=0){
			try {
				edt_value.setText(rs.getString("MSSC_VALUE"));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
		lbl_value.setLabelFor(edt_value);

		cons.gridx = 0;				cons.gridy = 1;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(lbl_value,cons);

		cons.gridx++;				cons.gridy = 1;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(edt_value,cons);

		JButton btn_cancel = new JButton(acp.Utils.Str("Cancel"));
		 btn_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				/*ConstList ConstList = new ConstList(conn);
				getDesktopPane().add(ConstList);*/
		        dispose();
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
		        final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
		        String query;	
					if (record_id!=0){
						query = "update mss_const set mssc_name='"+edt_name.getText()+"',";
						//query += " mssc_name='"+edt_name.getText()+"'";
						query += "mssc_value='"+edt_value.getText()+"'";
						query += " where mssc_id="+record_id;
					}	
					else{
						/*query = "insert into mss_const ";
						String fields = "mssc_id,mssc_name,mssc_value";
						String values = "mssc_seq.nextval,'"+edt_name.getText()+"'";
						fields +=",mssc_value";
						values += ",'"+edt_value.getText()+"'";
						query += "("+fields+") values ("+values+")";*/			
						query = "insert into mss_const(mssc_id,mssc_name,mssc_value)";
						query+= "values(msss_seq.nextval,UPPER('"+edt_name.getText()+"'),";
						query+= "'"+edt_value.getText()+"')";
					}
					
					try {
						rsq1.setCommand(query);
						rsq1.execute();
			        	} catch (SQLException e1) {
			        		if(e1.getMessage().contains("unique constraint")){
			        			JOptionPane.showMessageDialog(null,acp.Utils.Str("Unique"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			        		}
			        	}
				//ConstList ConstList = new ConstList(conn);
				//getDesktopPane().add(ConstList);
				  dispose();	
				} 
			
		});
					
		cons.gridx = 0;				cons.gridy = 2;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(btn_cancel,cons);

		cons.gridx++;				cons.gridy = 2;	
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(btn_save,cons);
		
		pack();
		/*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
		 Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
	     setLocation(newLocation);
		//setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        doLayout();
        setVisible(true);
	}
}
