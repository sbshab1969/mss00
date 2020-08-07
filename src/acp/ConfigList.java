package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import com.nqadmin.swingSet.SSDBComboBox;
import com.nqadmin.swingSet.SSDataGrid;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;


public class ConfigList extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
    private boolean flt_name_flag = false;
    private boolean flt_owner_flag = false;
    private boolean flt_source_flag = false;
    public ConfigList(final SSConnection conn){
    	setResizable(true);
    	//setModal(true);
    	setMaximizable(true);
        final SSDataGrid table = new SSDataGrid();
        final String[] fields ={"msso_id","msso_name","msso_comment","to_char(msso_dt_begin,'dd.mm.yyyy')",
        						"to_char(msso_dt_end,'dd.mm.yyyy')","msso_owner"};
        final String[] fieldnames ={"ID","Конфигурация","Описание","Начало действия","Конец действия","Имя пользователя"};
        
        setTitle("Настройки конфигурации источников");
        setSize(700,400);
        setClosable(true);
        Container cp = getContentPane();
        BorderLayout springLayout = new BorderLayout();
        //cp.setLayout(new SpringLayout());
        cp.setLayout(springLayout);
// Панель для фильтра       
        JPanel flt = new JPanel();
        flt.setLayout(new SpringLayout());
        flt.setBorder(new LineBorder(Color.DARK_GRAY));
        
/* Панель для фильтров конфигурации и пользователя */
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

 /* Фильтр по конфигурации */
        JCheckBox flt_name_check = new JCheckBox();
        flt_name_check.setSelected(flt_name_flag);
        flt_name_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_name_flag =!flt_name_flag;
			}
        });
        flt_name.add(flt_name_check);
        JLabel namelabel = new JLabel(acp.Utils.Str("ConfigName"), JLabel.TRAILING);
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

/* Фильтр по источнику */
        JPanel flt_source = new JPanel();
        flt_source.setBorder(new LineBorder(Color.BLACK));
        flt_source.setLayout(new SpringLayout());

        JCheckBox flt_source_check = new JCheckBox();
        flt_source_check.setSelected(flt_source_flag);
        flt_source_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_source_flag =!flt_source_flag;
			}
        });

        flt_source.add(flt_source_check);
        JLabel sourcelabel = new JLabel("Источник", SwingConstants.TRAILING);
        flt_source.add(sourcelabel);

        String query = "select msss_id, msss_name from mss_source order by msss_dt_modify desc";
        final SSDBComboBox source = new SSDBComboBox(conn,query,"msss_id","msss_name");
        try {
			source.execute();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);			
		}
		sourcelabel.setLabelFor(source);
		flt_source.add(source);
		SpringUtilities.makeCompactGrid(flt_source,1,3,3,3,5,2);
		//SpringUtilities.makeCompactGrid(flt_source,1,2,3,3,5,2);
        //cp.add(flt_source, BorderLayout.NORTH);

/* Собственно фильтр */
        JPanel flt_button = new JPanel();
        flt_button.setLayout(new SpringLayout());
        flt.add(flt_button);

        JButton fltbtn = new JButton(acp.Utils.Str("Search"));
        fltbtn.setLayout(new SpringLayout());
        fltbtn.addActionListener(new ActionListener(){
     		public void actionPerformed(ActionEvent e) {
     			String where ="";
     			if (flt_name_flag){
     				if (where.length()>0) where+=" and ";
     				if (name.getText().length()>=0){
     					where+="upper(msso_name) like upper('%"+name.getText()+"%')";
     				}else{
     					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     					return;
     				}
     			}
     			if (flt_owner_flag){
     				if (where.length()>0) where+=" and ";
     				if (owner.getText().length()>=0){
     					where+="upper(msso_owner) like upper('%"+owner.getText()+"%')";
     				}else{
     					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     					return;
     				}
     			}
     			if (flt_source_flag){
     				if (where.length()>0) where+=" and ";
     				if (source.getSelectedValue()>=0){
     					where+="msso_msss_id="+source.getSelectedValue();
     				}else{
     					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoFilter"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     					return;
     				}
     			}
     			if (where.length()==0){
     	    	   	JOptionPane.showMessageDialog(null,acp.Utils.Str("NoFilter"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
     	    	   	return;
     			}
     			SSJdbcRowSetImpl rs_flt = new SSJdbcRowSetImpl(conn);
     			rs_flt.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_options",where,"msso_dt_modify desc"));
                table.setSSRowSet(rs_flt);
                acp.Utils.resizeColumns(table);
     		}
            });
        flt_button.add(flt_source);
        JPanel panel_1 = new JPanel();
        flt_button.add(panel_1);
        flt_button.add(fltbtn);
        SpringUtilities.makeCompactGrid(flt_button,1,3,3,3,5,0);

/* Компоновка всех панелей фильтров */
        SpringUtilities.makeCompactGrid(flt,2,1,3,3,0,3);
        cp.add(flt, BorderLayout.NORTH);
        //cp.add(flt);
        
        final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
       	String where = "1=1";
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_options",where,"msso_dt_modify desc"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
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
						
						ConfigRecord rec_add = new ConfigRecord(conn,0,table);
						getDesktopPane().add(rec_add);
						//rec_add.Modal(true);
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
				        //dispose();
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						ConfigRecord rec_add = new ConfigRecord(conn,rec_id,table);
						getDesktopPane().add(rec_add);
				        try {
				        	rec_add.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
						//rec_add.Modal(true);
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
				        //dispose();
					}
				}
			}
        });
		
        JScrollPane scrollPane = new JScrollPane(table);
        cp.add(scrollPane, BorderLayout.CENTER);
        //cp.add(scrollPane);

/* Панель добавления записи */
        JPanel add_panel = new JPanel();
        add_panel.setBorder(null);
        add_panel.setLayout(new SpringLayout());
        JButton delbtn = new JButton(acp.Utils.Str("Delete"));
        add_panel.add(delbtn);
        delbtn.addActionListener(new ActionListener(){
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
					rsq.setCommand("delete from mss_options where msso_id="+rec_id);
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
        
        JButton cpybtn = new JButton(Utils.Str("Copy"));
        cpybtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int rec_id = 0;
				if (table.getSelectedRow()>=0){
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
				        final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
						try {
					        String query = "insert into mss_options (select msso_seq.nextval,msso_name||'_copy',msso_config,msso_dt_begin,msso_dt_end";
					        query += ",msso_comment, sysdate, sysdate, user, msso_msss_id from mss_options where msso_id="+rec_id+")";
							rsq1.setCommand(query);
							rsq1.execute();
						} catch (SQLException e1) {
//							JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						}
				        table.setSSRowSet(rs);
				        acp.Utils.resizeColumns(table);
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
			}
        });
        add_panel.add(cpybtn);
        JButton addbtn = new JButton(Utils.Str("Add"));
        addbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ConfigRecord rec_add = new ConfigRecord(conn,0,table);
				getDesktopPane().add(rec_add);
		        try {
		        	rec_add.setSelected(true);
		        } catch (java.beans.PropertyVetoException e1) {}
				//rec_add.Modal(true);
		        table.setSSRowSet(rs);
		        acp.Utils.resizeColumns(table);
		        //dispose();
			}
        });
        /////////////////////////////////////////////////////////////////////////////////////////////////      
              
              JButton chabtn = new JButton("Изменить");
              chabtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()>=0){
					SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) table.getSSRowSet();
					int rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					}
					catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						ConfigRecord rec_add = new ConfigRecord(conn,rec_id,table);
						getDesktopPane().add(rec_add);
				        try {
				        	rec_add.setSelected(true);
				        } catch (java.beans.PropertyVetoException e1) {}
						//rec_add.Modal(true);
				        table.setSSRowSet(rst);
				        acp.Utils.resizeColumns(table);
				        //dispose();
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
			}
               });
              add_panel.add(chabtn);

        
        add_panel.add(addbtn);
        
       JPanel panel = new JPanel();   
       add_panel.add(panel);
       panel.setLayout(new SpringLayout());
              
        
       JButton refbtn = new JButton("Обновить");
       add_panel.add(refbtn);
       
       refbtn.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			table.setSSRowSet(rs);
		}
       });
       
       JButton extbtn = new JButton(acp.Utils.Str("Close"));
        extbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
        add_panel.add(extbtn);
       
        SpringUtilities.makeCompactGrid(add_panel,1,7,3,3,5,2);
        //SpringUtilities.makeCompactGrid(panel,1,2,1,3,5,2);
        cp.add(add_panel,BorderLayout.PAGE_END);
        
         
//        SpringUtilities.makeCompactGrid(cp,3,1,5,5,1,1);
       // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //setLocation((setSize.width-getWidth())/2,(setSize.height-getHeight())/2);
        //setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}


}
