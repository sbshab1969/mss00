package acp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;

import com.nqadmin.swingSet.SSDataGrid;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

public class SourceRecord extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	public SourceRecord(final SSConnection conn, final long record_id, final SSDataGrid table){
		if (record_id==0){
			setTitle(acp.Utils.Str("RecordAddTitle"));
		}else{
			setTitle(acp.Utils.Str("RecordEditTitle"));
		}
 	    setSize(240,100);
        setResizable(false);
        setClosable(false);
        Container cp = getContentPane();
        cp.setLayout(new SpringLayout());
/* Панель для записи */
        JPanel rec = new JPanel();
        rec.setLayout(new SpringLayout());
        cp.add(rec);
        JLabel lbl = new JLabel(acp.Utils.Str("SourceName"), JLabel.TRAILING);
        rec.add(lbl);
        final JTextField rec_edit = new JTextField(10);
        lbl.setLabelFor(rec_edit);
        rec.add(rec_edit);
        SpringUtilities.makeCompactGrid(rec,1,2,2,2,5,0);
/* Панель для кнопок */
        JPanel btn = new JPanel();
        btn.setLayout(new SpringLayout());
        cp.add(btn);
        JButton btn_ok = new JButton();
		if (record_id==0){
			btn_ok.setText(acp.Utils.Str("Add"));
			
		}else{
			btn_ok.setText(acp.Utils.Str("Edit"));
			final SSJdbcRowSetImpl rsq = new SSJdbcRowSetImpl(conn);
			try {
		        String query = "select * from mss_source where msss_id="+record_id;
				rsq.setCommand(query);
				rsq.execute();
				rsq.first();
				rec_edit.setText(rsq.getString("msss_name"));
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
	
		}
		btn_ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        final SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
		        String query;
				try {
					if (record_id!=0){
						query = "update mss_source set msss_name='"+rec_edit.getText()+"',";
						query+= " msss_dt_modify=sysdate, msss_owner=user";
						query+= " where msss_id="+record_id;
					}else{
						query = "insert into mss_source(msss_id,msss_name,msss_dt_create,";
						query+= "msss_dt_modify,msss_owner) values(msss_seq.nextval,'"+rec_edit.getText()+"',";
						query+= "sysdate,sysdate,user)";
					}
					rsq1.setCommand(query);
					rsq1.execute();
				} catch (SQLException e1) {
//					JOptionPane.showMessageDialog(desktop,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
		        acp.Utils.resizeColumns(table);
		        //SourceList SourceList = new SourceList(conn);
				//getDesktopPane().add(SourceList);
		        dispose();
			}
		});
        JButton btn_cancel = new JButton(acp.Utils.Str("Cancel"));
        btn_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        //SourceList SourceList = new SourceList(conn);
				//getDesktopPane().add(SourceList);
				dispose();
			}
        });
        btn.add(btn_ok);
        btn.add(new JPanel());
        btn.add(btn_cancel);
        SpringUtilities.makeCompactGrid(btn,1,3,2,2,5,3);

        SpringUtilities.makeCompactGrid(cp,2,1,5,5,1,1);
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        doLayout();
        setVisible(true);
	}
}
