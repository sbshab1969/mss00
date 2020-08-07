package acp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import com.nqadmin.swingSet.SSDataGrid;
import com.nqadmin.swingSet.datasources.*;

public class AdvFileInfo extends MyInternalFrame{

	private static final long serialVersionUID = 1L;
	public AdvFileInfo(final SSConnection conn, final long file_id){
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
        final SSDataGrid table = new SSDataGrid();
        final String[] fields ={"to_char(mssl_dt_event,'dd.mm.yyyy hh24:mi:ss')","mssl_desc"};
        final String[] fieldnames ={"Время обработки","Описание"};

		setTitle(acp.Utils.Str("AdvFileInfoTitle"));
        setResizable(true);
 	    setMaximizable(true);
        setClosable(true);
        setSize(640, 480);
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        cp.add(scrollPane,BorderLayout.CENTER);
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_logs","mssl_ref_id="+file_id,"mssl_id"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        acp.Utils.resizeColumns(table);

        JButton btn_close = new JButton(acp.Utils.Str("Exit"));
        JPanel btn = new JPanel();
        cp.add(btn,BorderLayout.SOUTH);
        btn.add(btn_close);
        btn_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
		/*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
		//setLocation( (screenSize.width-getWidth()) / 2 , (screenSize.height-getHeight()) / 2 );
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
}
