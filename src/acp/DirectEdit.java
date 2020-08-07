package acp;

import java.awt.*;
import java.awt.event.*;
//import java.beans.PropertyVetoException;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import oracle.jdbc.OraclePreparedStatement;
import oracle.xdb.XMLType;
import org.w3c.dom.*;
import com.nqadmin.swingSet.datasources.*;

public class DirectEdit  extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
	protected Object dispose;
	protected Object SSConnection;
	public DirectEdit(final Document doc, final SSConnection conn, final long record_id){

	
        //setSize(640,480);
        //setBounds(250,200,860,640);
		setTitle(acp.Utils.Str("DirectEditTitle"));
 	    setMaximizable(true);
        setResizable(true);
        setClosable(false);
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        /*Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);*/

        final JTextArea txt = new JTextArea();

        
        Container cp = getContentPane();
        JPanel cnt = new JPanel();
        cp.add(cnt);
        
        cnt.setLayout(new BorderLayout());
        JScrollPane txtView = new JScrollPane(txt);
        cnt.add(txtView,BorderLayout.CENTER);
        cnt.setBorder(new EmptyBorder(2,2,2,2));
        JPanel btn = new JPanel();
        btn.setBorder(new EmptyBorder(5,5,5,5));
        btn.setLayout(new BorderLayout());
        cnt.add(btn,BorderLayout.SOUTH);
        JButton btn_save = new JButton(acp.Utils.Str("Save"));
        btn.add(btn_save,BorderLayout.EAST);
        btn_save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				try {
					OraclePreparedStatement stmt = 
					    (OraclePreparedStatement) conn.getConnection().
					    prepareStatement("update mss_options set msso_config = ? where msso_id="+record_id);
					XMLType poXML = XMLType.createXML(conn.getConnection(), txt.getText());
					stmt.setObject(1,poXML);
					stmt.execute();
				} catch (SQLException e1) {
					Utils.ErrorMsg(Utils.Str("Error"), e1.getMessage());
				}		

				Utils.Test(Utils.Str("SaveOk"));
				///////
				
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
				///////
				
				
				dispose();
				
				
			}
        });
        JButton btn_cancel = new JButton(Utils.Str("Cancel"));
        btn.add(btn_cancel,BorderLayout.WEST);
        btn_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
///////
				
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
				///////////
				
				dispose();
				
			}
        });
        
        txt.setText(Utils.XML2String(doc));

        doLayout();
        setVisible(true);
	}
}

