package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

public class EditTable extends MyInternalFrame{
	private static final long serialVersionUID = 1L;

	public EditTable(final SSConnection conn, final Vector<String> params) {
        final SSDataGrid table = new SSDataGrid();
        final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
        String[] path = params.get(0).split("/");
        setTitle(Utils.DescByKey(path[path.length-2]));
 	    setMaximizable(true);
        setResizable(true);
        setClosable(true);
        setSize(640, 480);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        JPanel fltpnl = new JPanel();
        cp.add(fltpnl,BorderLayout.NORTH);
        JPanel btn = new JPanel();
        cp.add(btn,BorderLayout.SOUTH);
        btn.setLayout(new BorderLayout());
        
        JPanel btn_rec = new JPanel();
        btn_rec.setLayout(new FlowLayout());
        btn.add(btn_rec,BorderLayout.WEST);
        
        JButton btn_edit = new JButton(acp.Utils.Str("Edit"));
        btn_rec.add(btn_edit,FlowLayout.LEFT);
        btn_edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()>=0){
				int rec_id = 0;

				try {
					rs.absolute(table.getSelectedRow()+1);
					rec_id = rs.getInt("ID");
				} catch (SQLException e1) {
					//Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
				if (rec_id>0){
					UpdateRecord ur = new UpdateRecord(conn, params,rs);
		        	getDesktopPane().add(ur);
		        	try {
						ur.setSelected(true);
					} catch (PropertyVetoException e1) {
						Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
					}
		        	//ur.Modal(true);
			        table.setSSRowSet(rs);
			        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			        Utils.resizeColumns(table);
				}
			
			}else{
				JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
				}
        });
        
        JPanel btn_act = new JPanel();
        btn_act.setLayout(new FlowLayout());
        btn.add(btn_act,BorderLayout.EAST);
        
        JButton btn_close =  new JButton(acp.Utils.Str("Close"));
        btn_act.add(btn_close);
        btn_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
        
        JScrollPane sp = new JScrollPane(table);
		final String[] fields = new String[params.size()+2];
		final String[] fieldnames = new String[params.size()+2];
        String tbl = CreateTbl(0, params, fields, fieldnames);
       // final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
       	final String where = "";
        rs.setCommand(Utils.SetQuery(fields,fieldnames,tbl,where,""));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(Utils.GetFields(fieldnames));
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Utils.resizeColumns(table);
        
       	String query = "select msss_id, msss_name from mss_source order by msss_dt_modify desc";
        final SSDBComboBox srcname = new SSDBComboBox(conn,query,"msss_id","msss_name");
        try {
        	srcname.execute();
		} catch (SQLException e1) {
			Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
		} catch (Exception e1) {
			Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
		}
		
		JLabel lblNewLabel = new JLabel("Источник");
		fltpnl.add(lblNewLabel);
		
		fltpnl.add(srcname);
		srcname.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (srcname.getSelectedIndex()>=0){
			       	String tblnew = CreateTbl(srcname.getSelectedValue(), params, fields, fieldnames);
			        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,tblnew,where,""));
			        table.setSSRowSet(rs);
			        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			        Utils.resizeColumns(table);
				}
			}
		});

        table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2){
					int rec_id = 0;
					
					try {
						rs.absolute(table.getSelectedRow()+1);
						rec_id = rs.getInt("ID");
					} catch (SQLException e1) {
						//Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
					}
					if (rec_id>0){
						UpdateRecord ur = new UpdateRecord(conn, params,rs);
			        	getDesktopPane().add(ur);
			        	try {
							ur.setSelected(true);
						} catch (PropertyVetoException e1) {
							Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
						}
			        	//ur.Modal(true);
				        table.setSSRowSet(rs);
				        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				        Utils.resizeColumns(table);
				        //dispose();
					}
				}
			}
        });

        cp.add(sp, BorderLayout.CENTER);
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
	
	private String CreateTbl(long src, Vector<String> params, String[] fields, String[] fieldnames){
        String[] path = params.get(0).split("/");
        String res = "table(mss.spr_options("+src+",'"+params.get(0)+"'";
		fields[0] = "CONFIG_ID";
		fieldnames[0] = "ID";
		fields[params.size()] = "to_char(DATE_BEGIN,'dd/mm/yyyy hh24:mi:ss')";
		fieldnames[params.size()] = "Дата начала";
		fields[params.size()+1] = "to_char(DATE_END,'dd/mm/yyyy hh24:mi:ss')";
		fieldnames[params.size()+1] = "Дата окончания";
        for (int i=1; i<params.size(); i++){
        	fields[i] = "P"+i;
        	fieldnames[i] = Utils.DescByKey(path[path.length-1]+"."+params.get(i));
        	res += ",'"+params.get(i)+"'";
        }
        for (int i=params.size(); i<=5; i++){
        	res += ",null";
        }
       	res += "))";
		return res;
	}
	
	private class UpdateRecord extends MyInternalFrame{
		private static final long serialVersionUID = 1L;

		public UpdateRecord(final SSConnection conn, final Vector<String> params, final SSJdbcRowSetImpl rs) {
			setTitle(acp.Utils.Str("EditConfig"));
			//setResizable(true);
			setClosable(true);
			Container cp = getContentPane();
			cp.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.LINE_START;
	        String[] path = params.get(0).split("/");
	        final Vector<String> oldvalues = new Vector<String>();
	        final Vector<String> values = new Vector<String>();
	        
	        int i;
			for (i = 1; i<params.size(); i++){
				JLabel lbl = new JLabel(Utils.DescByKey(path[path.length-1]+"."+params.get(i)));
				cons.gridx = 0;				cons.gridy = i-1;
				cons.insets = new Insets(2,3,2,3);
				cons.anchor = GridBagConstraints.EAST;
				cp.add(lbl,cons);
				try {
					final int v = i-1;
					final JTextField edt = new JTextField(rs.getString(i+1),10);
					lbl.setLabelFor(edt);
					values.add(edt.getText());
					oldvalues.add(edt.getText());
					edt.addKeyListener(new KeyListener(){
						public void keyPressed(KeyEvent e) {}
						public void keyReleased(KeyEvent e) {	values.set(v, edt.getText());	}
						public void keyTyped(KeyEvent e) {}
					});

					cons.gridx = 1;				cons.gridy = i-1;
					cons.insets = new Insets(2,3,2,3);
					cons.anchor = GridBagConstraints.WEST;
					cp.add(edt,cons);
				
				} catch (SQLException e) {
					Utils.ErrorMsg(Utils.Str("Error"),e.getMessage());
				}
			}
			JButton btncnl = new JButton(Utils.Str("Cancel"));
			cons.gridx = 0;				cons.gridy = i;
			cons.insets = new Insets(2,3,2,3);
			cons.anchor = GridBagConstraints.WEST;
			cp.add(btncnl,cons);
			btncnl.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			JButton btnsave = new JButton(Utils.Str("Save"));
			cons.gridx = 1;				cons.gridy = i;
			cons.insets = new Insets(2,3,2,3);
			cons.anchor = GridBagConstraints.EAST;
			cp.add(btnsave,cons);
			btnsave.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
			        SSJdbcRowSetImpl rsq1 = new SSJdbcRowSetImpl(conn);
			        for (int j = 1; j<params.size(); j++ ){
				        String query = "update mss_options set ";
			        	String where = "";
			        	for (int i = 1; i<params.size(); i++ )
								where += "[@"+params.get(i)+"=\""+oldvalues.get(i-1)+"\"]";
			        	query += "msso_config = updatexml(msso_config,";
			        	query += "'"+params.get(0)+where+"/@"+params.get(j)+"'";
			        	query += ",'"+values.get(j-1)+"')";
			        	try {
							query += " where msso_id = "+rs.getInt("ID");
						} catch (SQLException e2) {
							e2.printStackTrace();
						}
			        	oldvalues.set(j-1,values.get(j-1));
			        	try {
				        	rsq1.setCommand(query);
				        	rsq1.execute();
						} catch (SQLException e1) {
//							Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
						}
			        }
			       	
					dispose();
				}
			});

			pack();
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
}
