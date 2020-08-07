package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

public class VarList  extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	private boolean flt_name_flag  =false;
	
	public VarList(final SSConnection conn) {
        setTitle(acp.Utils.Str("VarListTitle"));
 	    setMaximizable(true);
        setResizable(true);
        setClosable(true);
        setSize(640,480);
        
        final SSDataGrid table = new SSDataGrid();
        final String[] fields ={"mssv_id","mssv_name","mssv_valuev","mssv_valuen","mssv_valued"};
        final String[] fieldnames ={"ID","Имя переменной","Описание","Число","Дата"};
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
        
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        
        JPanel flt = new JPanel();
        flt.setBorder(new TitledBorder(new LineBorder(Color.BLACK), acp.Utils.Str("Filter")));
        cp.add(flt,BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(table);
        cp.add(scrollPane,BorderLayout.CENTER);

        JPanel btn = new JPanel();
        cp.add(btn,BorderLayout.SOUTH);
        btn.setLayout(new BorderLayout());
        
        JPanel btn_rec = new JPanel();
        btn_rec.setLayout(new FlowLayout());
        btn.add(btn_rec,BorderLayout.WEST);
        
        JButton btn_del = new JButton(acp.Utils.Str("Delete"));
        btn_rec.add(btn_del,FlowLayout.LEFT);
        JButton btn_chg = new JButton(acp.Utils.Str("Change"));
        btn_rec.add(btn_chg,FlowLayout.CENTER);
        JButton btn_add = new JButton(acp.Utils.Str("Add"));
        btn_rec.add(btn_add,FlowLayout.RIGHT);

        JPanel btn_act = new JPanel();
        btn_act.setLayout(new FlowLayout());
        btn.add(btn_act,BorderLayout.EAST);
        
        JButton btn_refresh = new JButton("Обновить");
        btn_act.add(btn_refresh);
        btn_refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				table.setSSRowSet(rs);	
			}
        });
        
        JButton btn_close =  new JButton(acp.Utils.Str("Close"));
        btn_act.add(btn_close);
        btn_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
        
        flt.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.LINE_START;

		JCheckBox chk_name = new JCheckBox();
		chk_name.setSelected(flt_name_flag);
		chk_name.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_name_flag=!flt_name_flag;
			}
		});
		
		cons.gridx = 0;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		flt.add(chk_name,cons);
        
		JLabel lbl_name = new JLabel(acp.Utils.Str("Name"));
		cons.gridx = 1;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.anchor = GridBagConstraints.EAST;
		flt.add(lbl_name,cons);

		final JTextField edt_name = new JTextField(20);
		lbl_name.setLabelFor(edt_name);
		cons.gridx = 2;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.anchor = GridBagConstraints.EAST;
		flt.add(edt_name,cons);

		JButton btn_flt = new JButton(acp.Utils.Str("Search"));
		cons.gridx = 3;				cons.gridy = 0;
		cons.insets = new Insets(2,5,2,5);
		cons.anchor = GridBagConstraints.EAST;
		flt.add(btn_flt,cons);
		
		btn_flt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
     			String where ="";
     			if (flt_name_flag){
     				if (where.length()>0) where+=" and ";
//     				if (edt_name.getText().length()>2){
     					where+="upper(mssv_name) like upper('%"+edt_name.getText()+"%')";
//     				}else{
//     					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
//     					return;
//     				}
     			}
     			if (where.length()==0){
     	    	   	JOptionPane.showMessageDialog(null,acp.Utils.Str("NoFilter"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     	    	   	return;
     			}
     			SSJdbcRowSetImpl rs_flt = new SSJdbcRowSetImpl(conn);
     			rs_flt.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_vars",where,"mssv_last_modify desc"));
                table.setSSRowSet(rs_flt);
                acp.Utils.resizeColumns(table);
			}
		});

       	String where = "1=1";
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_vars",where,"mssv_id"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        acp.Utils.resizeColumns(table);
		
        table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) ((SSDataGrid) e.getSource()).getSSRowSet();
				if (e.getClickCount()==2){
					int rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						
						VarEdit VarEdit = new VarEdit(conn,0);
						getDesktopPane().add(VarEdit);
						//VarEdit.Modal(true);
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
				        //dispose();
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						VarEdit varEdit = new VarEdit(conn,rec_id);
						getDesktopPane().add(varEdit);
				        try {
				        	varEdit.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
				        //varEdit.Modal(true);
				        table.setSSRowSet(rst);
				        acp.Utils.resizeColumns(table);
				        //dispose();
					}
				}
			}
        });

        btn_chg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()>=0){
					SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) table.getSSRowSet();
					int rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						VarEdit varEdit = new VarEdit(conn,rec_id);
						getDesktopPane().add(varEdit);
						//dispose();
						try {
							varEdit.setSelected(true);
						} catch (java.beans.PropertyVetoException e1) {}
						//varEdit.Modal(true);
						table.setSSRowSet(rst);
						acp.Utils.resizeColumns(table);
					}
				}else
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
        });
        
        btn_del.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int rec_id = 0;
				if (table.getSelectedRow()>=0){
				if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecord"),acp.Utils.Str("DeleteDialogTitle"),1)==0){
					//int rec_id = 0;
					SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) table.getSSRowSet();
					rec_id = 0;				
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						SSJdbcRowSetImpl rsq = new SSJdbcRowSetImpl(conn);
						rsq.setCommand("delete from mss_vars where mssv_id="+rec_id);
						try {
							rsq.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
						table.setSSRowSet(rst);
						acp.Utils.resizeColumns(table);
					}
				}
			}else{
				JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			   }
			}
        });
        
        btn_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				VarEdit varEdit = new VarEdit(conn,0);
				getDesktopPane().add(varEdit);
				//dispose();
		        try {
		        	varEdit.setSelected(true);
		        } catch (java.beans.PropertyVetoException e1) {}
		        //varEdit.Modal(true);
		        table.setSSRowSet(rs);
		        acp.Utils.resizeColumns(table);
			}
        });

        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        doLayout();
        setVisible(true);
	}
}
