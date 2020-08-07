package acp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.nqadmin.swingSet.SSDataGrid;
import com.nqadmin.swingSet.datasources.*;

public class OtherLogs   extends MyInternalFrame{
	private static final long serialVersionUID = 1L;

	public OtherLogs(final SSConnection conn){
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
        final SSDataGrid table = new SSDataGrid();
        final String[] fields ={"to_char(mssl_dt_event,'dd.mm.yyyy hh24:mi:ss')","mssl_desc"};
        final String[] fieldnames ={"Время","Описание"};

		setTitle("Другие протоколы");
		setMaximizable(true);
        setResizable(true);
        setSize(640,480);
        setClosable(true);
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5,5,5,5));
        cp.add(scrollPane,BorderLayout.CENTER);
//        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_logs","mssl_ref_id=0","mssl_id"));
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_logs","mssl_ref_id=0 and rownum<=10","mssl_id"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        acp.Utils.resizeColumns(table);

        JButton btn_close = new JButton(acp.Utils.Str("Exit"));
        JPanel btn = new JPanel();
        btn.setLayout(new BorderLayout(2,2));
        btn.setBorder(new EmptyBorder(2,2,2,2));
        cp.add(btn,BorderLayout.SOUTH);
        btn.add(btn_close,BorderLayout.EAST);
        btn_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
		/*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
		//setLocation( (screenSize.width-getWidth()) / 2 , (screenSize.height-getHeight()) / 2 );
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
}
