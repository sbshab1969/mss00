package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

public class ConfigRecord extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	public boolean result = false;
	public ConfigRecord(final SSConnection conn, final long record_id, final SSDataGrid table) {
        final Calendar dt1flt;
        final Calendar dt2flt;
        final SimpleDateFormat formatdt = new SimpleDateFormat("dd.MM.yyyy");
        String dt1str;
        String dt2str;
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);

        if (record_id==0){
			setTitle(acp.Utils.Str("RecordAddTitle"));
		}else{
			setTitle(acp.Utils.Str("RecordEditTitle"));
			rs.setCommand("select * from mss_options where msso_id="+record_id);
			try {
				rs.execute();
				rs.first();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
		}
// 	    setSize(320,240);
        //setResizable(true);
        setClosable(true);
        Container cp = getContentPane();
        cp.setLayout(new SpringLayout());
// Выбор источника        
        JPanel src = new JPanel();
        src.setLayout(new SpringLayout());
        JLabel srclabel = new JLabel(acp.Utils.Str("SourceName"), JLabel.TRAILING);
        src.add(srclabel);
       	String query = "select msss_id, msss_name from mss_source order by msss_dt_modify desc";
        final SSDBComboBox srcname = new SSDBComboBox(conn,query,"msss_id","msss_name");
        try {
        	srcname.execute();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);			
		}
		srclabel.setLabelFor(srcname);
        src.add(srcname);
        SpringUtilities.makeCompactGrid(src,1,2,0,0,5,1);
        cp.add(src);
// Название конфигурации        
        JPanel cfg = new JPanel();
        cfg.setLayout(new SpringLayout());
        JLabel cfglabel = new JLabel(acp.Utils.Str("ConfigName"), JLabel.TRAILING);
        cfg.add(cfglabel);
        final JTextField cfgname = new JTextField();
        cfglabel.setLabelFor(cfgname);
        cfg.add(cfgname);
        SpringUtilities.makeCompactGrid(cfg,1,2,0,0,5,1);
        cp.add(cfg);
        
// Период действия конфигурации        
        JPanel dt = new JPanel();
        dt.setLayout(new SpringLayout());
        JLabel dtlabel = new JLabel(acp.Utils.Str("Date"), JLabel.TRAILING);
        dt.add(dtlabel);
        // дата начала
        JLabel dt1label = new JLabel(acp.Utils.Str("Begin"), JLabel.TRAILING);
        dt.add(dt1label);
        dt1flt = new GregorianCalendar();
        dt1str = formatdt.format(dt1flt.getTime()); 
        final JFormattedTextField dt1 = new JFormattedTextField(formatdt);
        dt1.setText(dt1str);
        dt1label.setLabelFor(dt1);
        dt.add(dt1);
        // дата окончания
        JLabel dt2label = new JLabel(acp.Utils.Str("End"), JLabel.TRAILING);
        dt.add(dt2label);
        dt2flt = new GregorianCalendar();
        dt2str = formatdt.format(dt2flt.getTime()); 
        final JFormattedTextField dt2 = new JFormattedTextField(formatdt);
        dt2.setText(dt2str);
        dt2label.setLabelFor(dt2);
        dt.add(dt2);
        SpringUtilities.makeCompactGrid(dt,1,5,0,0,5,1);
        cp.add(dt);
// Панель комментария
        final JTextArea cmnt = new JTextArea(5,20);
        JScrollPane scrollPane = new JScrollPane(cmnt);
        cp.add(scrollPane);
// Кнопка редактирования конфигурации        
        JPanel cfg2 = new JPanel();
        cfg2.setLayout(new SpringLayout());
        JLabel cfg2label = new JLabel(acp.Utils.Str("EditConfig"), JLabel.TRAILING);
        cfg2.add(cfg2label);
        cfg2.add(new JPanel());
        
// Редактирование конфигурации
        JButton btn_cfg = new JButton(acp.Utils.Str("Edit"));
        btn_cfg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				final ConfigEdit cfgedit = new ConfigEdit(conn,record_id);
				getDesktopPane().add(cfgedit);
				getDesktopPane().getDesktopManager().maximizeFrame(cfgedit);
				try {
					cfgedit.setSelected(true);
				} catch (java.beans.PropertyVetoException e1) {}
				cfgedit.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
				cfgedit.addInternalFrameListener(new InternalFrameListener(){
					public void internalFrameActivated(InternalFrameEvent arg0) {}
					public void internalFrameClosed(InternalFrameEvent e) {}
						public void internalFrameClosing(InternalFrameEvent e) {
						if (cfgedit.changed){
							if (acp.Utils.MyConfirmDialog(acp.Utils.Str("ConfirmClose"),acp.Utils.Str("Warning"),1)==0){
								cfgedit.dispose();
							}
						}else{
							cfgedit.dispose();
						}
					}

					public void internalFrameDeactivated(InternalFrameEvent e) {}

					public void internalFrameDeiconified(InternalFrameEvent e) {}

					public void internalFrameIconified(InternalFrameEvent e) {}

					public void internalFrameOpened(InternalFrameEvent e) {}
				});
				//cfgedit.Modal(true);
			}
        });
        cfg2.add(btn_cfg);
        if (record_id==0) btn_cfg.setEnabled(false);

        SpringUtilities.makeCompactGrid(cfg2,1,3,0,0,5,1);
        cp.add(cfg2);
        cp.add(new JSeparator());
        
// Заполнение данных 
        if (record_id!=0){
        	try {
        		dt1str = formatdt.format(rs.getDate("msso_dt_begin"));
        		dt1.setText(dt1str);
        		if (rs.getDate("msso_dt_end")!=null){
            		dt2str = formatdt.format(rs.getDate("msso_dt_end"));
            		dt2.setText(dt2str);
        		}else{
            		dt2.setText("");
        		}
                cmnt.setWrapStyleWord(true);
        		cmnt.setText(rs.getString("msso_comment"));
				cfgname.setText(rs.getString("msso_name"));
	    		srcname.setSelectedValue(rs.getInt("msso_msss_id"));
        	} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
        }
        
/* Панель для кнопок */
        JPanel btn = new JPanel();
        btn.setLayout(new SpringLayout());
        JButton btn_ok = new JButton();
		if (record_id==0){
			btn_ok.setText(acp.Utils.Str("Add"));
		}else{
			btn_ok.setText(acp.Utils.Str("Save"));
		}	
		btn_ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (cfgname.getText().trim().equals("") || cmnt.getText().trim().equals("")){
					acp.Utils.Test(acp.Utils.Str("IsEmpty"));
				}else{
					final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
					String query;
					try {
						if (record_id!=0){
							query = "update mss_options set msso_name='"+cfgname.getText()+"',";
							query+= " msso_comment='"+cmnt.getText()+"', msso_msss_id="+srcname.getSelectedValue()+",";
							query+= " msso_dt_begin='"+dt1.getText()+"', msso_dt_end='"+dt2.getText()+"',";
							query+= " msso_dt_modify=sysdate, msso_owner=user";
							query+= " where msso_id="+record_id;
						}else{
							String exmptyxml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
							query = "insert into mss_options (msso_id,msso_name,msso_comment,msso_msss_id,msso_config,";
							query+= "msso_dt_begin,msso_dt_end,msso_dt_create,msso_dt_modify,msso_owner) values(";
							query+= "msso_seq.nextval,'"+cfgname.getText()+"',";
							query+= "'"+cmnt.getText()+"',"+srcname.getSelectedValue()+",'"+exmptyxml+"',";
							query+= "'"+dt1.getText()+"','"+dt2.getText()+"',sysdate,sysdate,user)";
						}
						rsq1.setCommand(query);
						rsq1.execute();
					} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(desktop,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					acp.Utils.resizeColumns(table);
					//ConfigList ConfigList = new ConfigList(conn);
					//getDesktopPane().add(ConfigList);
					dispose();
				}
			}
        });
        JButton btn_cancel = new JButton(acp.Utils.Str("Cancel"));
        btn_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//ConfigList ConfigList = new ConfigList(conn);
				//getDesktopPane().add(ConfigList);
				dispose();
			}
        });
        btn.add(btn_ok);
        btn.add(new JPanel());
        btn.add(btn_cancel);
        SpringUtilities.makeCompactGrid(btn,1,3,0,0,5,1);
        cp.add(btn);
        SpringUtilities.makeCompactGrid(cp,7,1,5,5,1,5);
        pack();
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
}
