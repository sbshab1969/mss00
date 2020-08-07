package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

public class SourceList extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
    private boolean flt_name_flag = false;
    private boolean flt_owner_flag = false;
    
	public SourceList(final SSConnection conn){
		setMaximizable(true);
        final SSDataGrid table = new SSDataGrid();
        final String[] fields ={"msss_id","msss_name","msss_owner"};
        final String[] fieldnames ={"ID","Имя источника","Пользователь"};
        
        setTitle("Источники");

        setSize(640,480);
        setResizable(true);
        setClosable(true);
        Container cp = getContentPane();
        BorderLayout springLayout = new BorderLayout();
        //cp.setLayout(new SpringLayout());
        cp.setLayout(springLayout);
// Панель для фильтра       
        JPanel flt = new JPanel();
        flt.setLayout(new SpringLayout());
        flt.setBorder(new LineBorder(Color.DARK_GRAY));
        
/* Панель для фильтров имени источника и пользователя */
        JPanel flt_name_owner = new JPanel();
        flt_name_owner.setLayout(new SpringLayout());
        flt.add(flt_name_owner);
        JPanel flt_name = new JPanel();
        flt_name.setBorder(new LineBorder(Color.BLACK));
        flt_name.setLayout(new SpringLayout());
        flt_name_owner.add(flt_name);
        JPanel flt_owner = new JPanel();
        flt_owner.setBorder(new LineBorder(Color.BLACK));
        flt_owner.setLayout(new SpringLayout());
        flt_name_owner.add(flt_owner);

 /* Фильтр по имени источника */
        JCheckBox flt_name_check = new JCheckBox();
        flt_name_check.setSelected(flt_name_flag);
        flt_name_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_name_flag =!flt_name_flag;
			}
        });
        flt_name.add(flt_name_check);
        JLabel namelabel = new JLabel(acp.Utils.Str("SourceName"), JLabel.TRAILING);
        flt_name.add(namelabel);
        final JTextField name = new JTextField();
        namelabel.setLabelFor(name);
        flt_name.add(name);
        SpringUtilities.makeCompactGrid(flt_name,1,3,3,3,5,1);

 /* Фильтр по владельцу */       
        JCheckBox flt_owner_check = new JCheckBox();
        flt_owner_check.setSelected(flt_owner_flag);
        flt_owner_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_owner_flag =!flt_owner_flag;
			}
        });
        flt_owner.add(flt_owner_check);
        JLabel ownerlabel = new JLabel(acp.Utils.Str("Owner"), JLabel.TRAILING);
        flt_owner.add(ownerlabel);
        final JTextField owner = new JTextField();
        ownerlabel.setLabelFor(owner);
        flt_owner.add(owner);
        SpringUtilities.makeCompactGrid(flt_owner,1,3,3,3,5,0);
        SpringUtilities.makeCompactGrid(flt_name_owner,1,2,3,3,5,1);

/* Собственно фильтр */
        JPanel flt_button = new JPanel();
        flt_button.setLayout(new SpringLayout());
        flt.add(flt_button);
        JButton fltbtn = new JButton(acp.Utils.Str("Search"));
        fltbtn.addActionListener(new ActionListener(){
 		public void actionPerformed(ActionEvent e) {
 			String where ="";
 			if (flt_name_flag){
 				if (where.length()>0) where+=" and ";
 				if (name.getText().length()>=0){
 					where+="upper(msss_name) like upper('%"+name.getText()+"%')";
 				}else{
 					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 					return;
 				}
 			}
 			if (flt_owner_flag){
 				if (where.length()>0) where+=" and ";
 				if (owner.getText().length()>=0){
 					where+="upper(msss_owner) like upper('%"+owner.getText()+"%')";
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
 			rs_flt.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_source",where,"msss_dt_modify desc"));
 			try {
 				rs_flt.execute();
 			} catch (SQLException ex) {
 				JOptionPane.showMessageDialog(null,ex.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 			}
             table.setSSRowSet(rs_flt);
             acp.Utils.resizeColumns(table);
 		}
        });
        flt_button.add(new JPanel());
        flt_button.add(fltbtn);
        SpringUtilities.makeCompactGrid(flt_button,1,2,3,3,5,0);

/* Компоновка всех панелей фильтров */
        SpringUtilities.makeCompactGrid(flt,2,1,3,3,0,1);
        cp.add(flt, BorderLayout.NORTH);
        //cp.add(flt);

        final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
       	String where = "1=1";
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_source",where,"msss_dt_modify desc"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        acp.Utils.resizeColumns(table);
        table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2){
					int rec_id = 0;
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						

						SourceRecord rec_add = new SourceRecord(conn,0,table);
						getDesktopPane().add(rec_add);
						//rec_add.Modal(true);
				        table.setSSRowSet(rs);
				        //dispose();
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						SourceRecord rec_add = new SourceRecord(conn,rec_id, table);
						getDesktopPane().add(rec_add);
				        try {
				        	rec_add.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
						//rec_add.Modal(true);
				        table.setSSRowSet(rs);
				        //dispose();
					}
				}
			}
        });
        JScrollPane scrollPane = new JScrollPane(table);
        cp.add(scrollPane, BorderLayout.CENTER);
        //cp.add(scrollPane);
////////////////////////////////////////////////////////////////////
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
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecords"),acp.Utils.Str("DeleteDialogTitle"),1)==0){
				        final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
				        String query;
						try {
					        query = "delete from mss_options where msso_msss_id="+rec_id;
							rsq1.setCommand(query);
							rsq1.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
						try {
					        query = "delete from mss_source where msss_id="+rec_id;
							rsq1.setCommand(query);
							rsq1.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
			}
        });
        
        JButton btn_edit = new JButton(acp.Utils.Str("Edit"));
        btn_rec.add(btn_edit,FlowLayout.CENTER);
        btn_edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int rec_id = 0;
				if (table.getSelectedRow()>=0){
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						SourceRecord rec_add = new SourceRecord(conn,rec_id, table);
						getDesktopPane().add(rec_add);
				        try {
				        	rec_add.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
						//rec_add.Modal(true);
				        table.setSSRowSet(rs);
				        //dispose();
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
				}
        });
        
        JButton btn_add = new JButton(acp.Utils.Str("Add"));
        btn_rec.add(btn_add,FlowLayout.RIGHT);
        btn_add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				SourceRecord rec_add = new SourceRecord(conn,0,table);
				getDesktopPane().add(rec_add);
		        try {
		        	rec_add.setSelected(true);
		        } catch (java.beans.PropertyVetoException e1) {}
				//rec_add.Modal(true);
		        table.setSSRowSet(rs);
		        //dispose();
		        }
        });
        
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
        
        
        
        
   /* Панель добавления записи */
    /*    JPanel add_panel = new JPanel();
        add_panel.setLayout(new SpringLayout());
        JButton delbtn = new JButton(acp.Utils.Str("Delete"));
        add_panel.add(delbtn);
        delbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int rec_id = 0;
				if (table.getSelectedRow()>=0){
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecords"),acp.Utils.Str("DeleteDialogTitle"),1)==0){
				        final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
				        String query;
						try {
					        query = "delete from mss_options where msso_msss_id="+rec_id;
							rsq1.setCommand(query);
							rsq1.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
						try {
					        query = "delete from mss_source where msss_id="+rec_id;
							rsq1.setCommand(query);
							rsq1.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
			}
        });
        
        add_panel.add(new JPanel());
        JButton addbtn = new JButton(acp.Utils.Str("Add"));
        addbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				SourceRecord rec_add = new SourceRecord(conn,0,table);
				getDesktopPane().add(rec_add);
		        try {
		        	rec_add.setSelected(true);
		        } catch (java.beans.PropertyVetoException e1) {}
				rec_add.Modal(true);
		        table.setSSRowSet(rs);
		        dispose();}
        });
        add_panel.add(addbtn);

        add_panel.add(new JPanel());
        JButton editbtn = new JButton(acp.Utils.Str("Edit"));
        editbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				int rec_id = 0;
				if (table.getSelectedRow()>=0){
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						SourceRecord rec_add = new SourceRecord(conn,rec_id, table);
						getDesktopPane().add(rec_add);
				        try {
				        	rec_add.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
						rec_add.Modal(true);
				        table.setSSRowSet(rs);
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
				dispose();}
        });
        add_panel.add(editbtn);
        
        
        
        add_panel.add(new JPanel());
        JButton extbtn = new JButton(acp.Utils.Str("Close"));
        extbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
        add_panel.add(extbtn);
        
      /*  SpringUtilities.makeCompactGrid(add_panel,1,7,3,3,5,2);
        cp.add(add_panel);*/

        SpringUtilities.makeCompactGrid(cp,3,1,5,5,1,1);
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
}

