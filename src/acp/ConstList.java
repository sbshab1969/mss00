package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.*;
import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

public class ConstList  extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	private boolean flt_name_flag  =false;

	public ConstList(final SSConnection conn) {
        setTitle(acp.Utils.Str("ConstListTitle"));
 	    setMaximizable(true);
        setResizable(true);
        setClosable(true);
        setSize(640,480);
 

        final SSDataGrid table = new SSDataGrid();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final String[] fields ={"mssc_id","mssc_name","mssc_value"};
        final String[] fieldnames ={"ID","Имя константы","Значение"};
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
        btn_del.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
			    int rec_id = 0;
				if (table.getSelectedRow()>=0){
				if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecord"),acp.Utils.Str("DeleteDialogTitle"),1)==0){
					//rec_id = 0;
					SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) table.getSSRowSet();
					rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						SSJdbcRowSetImpl rsq = new SSJdbcRowSetImpl(conn);
						rsq.setCommand("delete from mss_const where mssc_id="+rec_id);
						try {
							rsq.execute();
						} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
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
     				if (edt_name.getText().length()>1){
     					where+="upper(mssc_name) like upper('%"+edt_name.getText()+"%')";
     				}else{
     					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     					return;
     				}
     			}
     			if (where.length()==0){
     	    	   	JOptionPane.showMessageDialog(null,acp.Utils.Str("NoFilter"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     	    	   	return;
     			}
     			SSJdbcRowSetImpl rs_flt = new SSJdbcRowSetImpl(conn);
     			rs_flt.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_const",where,"mssc_id"));
                table.setSSRowSet(rs_flt);
                acp.Utils.resizeColumns(table);
			}
		});
        
       	String where = "1=1";
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_const",where,"mssc_id"));
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
						
						ConstEdit constEdit = new ConstEdit(conn,0);
						getDesktopPane().add(constEdit);
				       // constEdit.Modal(true);
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
				        //dispose();
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						ConstEdit constEdit = new ConstEdit(conn,rec_id);
						getDesktopPane().add(constEdit);
				        try {
				        	constEdit.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
				        constEdit.Modal(true);
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
						ConstEdit constEdit = new ConstEdit(conn,rec_id);
						getDesktopPane().add(constEdit);
						try {
							constEdit.setSelected(true);
						} catch (java.beans.PropertyVetoException e1) {}
						//constEdit.Modal(true);
						table.setSSRowSet(rst);
						acp.Utils.resizeColumns(table);
						//dispose();
					}
				}else
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
        });

        btn_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					
				ConstEdit constEdit = new ConstEdit(conn,0);
				getDesktopPane().add(constEdit);
				try {
		        	constEdit.setSelected(true);
		        } catch (java.beans.PropertyVetoException e1) {}
		        //constEdit.Modal(true);
		        table.setSSRowSet(rs);
		        acp.Utils.resizeColumns(table);
		        //dispose();

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

