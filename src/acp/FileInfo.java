package acp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

public class FileInfo  extends MyInternalFrame{

	private static final long serialVersionUID = 1L;
	public FileInfo(final SSConnection conn, final long record_id){
		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);

		setTitle(acp.Utils.Str("FileInfoTitle"));
		//setMaximizable(true);
        //setResizable(true);
        setClosable(true);
        Container cp = this.getContentPane();
        cp.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.LINE_START;
        
		try {
			String query = "select mssf_id,mssf_name,mssf_md5";
			query+=",to_char(mssf_dt_create, 'dd.mm.yyyy hh24:mi:ss')";
			query+=",to_char(mssf_dt_work, 'dd.mm.yyyy hh24:mi:ss')";
			query+=",mssf_owner";
			query+=",extract(t.mssf_statistic,'statistic/records/all/text()').getStringVal()";
			query+=",extract(t.mssf_statistic,'statistic/records/error/text()').getStringVal()";
//			query+=",extract(t.mssf_statistic,'statistic/time/all/text()').getStringVal()";
//			query+=",extract(t.mssf_statistic,'statistic/time/error/text()').getStringVal()";
			query+=",s.msso_name";
			query+=" from mss_files t, mss_options s where mssf_id="+record_id;
			query+=" and s.msso_id=t.mssf_msso_id";
			rs.setCommand(query);
			rs.execute();
			rs.first();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		}

		// Выводим ИД
		AddLine(cp, cons, 0, acp.Utils.Str("fi_id_file"), 1, rs);

		// Выводим имя файла
		AddLine(cp, cons, 1, acp.Utils.Str("fi_filename"), 2, rs);

		// Выводим хеш файла
		AddLine(cp, cons, 2, acp.Utils.Str("fi_file_md5"), 3, rs);

		// Выводим дату создания
		AddLine(cp, cons, 3, acp.Utils.Str("fi_dt_create"), 4, rs);

		// Выводим дату обработки
		AddLine(cp, cons, 4, acp.Utils.Str("fi_dt_work"), 5, rs);

		// Выводим автора обработки
		AddLine(cp, cons, 5, acp.Utils.Str("fi_user"), 6, rs);

		// Выводим кол-во записей
		AddLine(cp, cons, 6, acp.Utils.Str("fi_all_records"), 7, rs);

		// Выводим кол-во ошибок
		AddLine(cp, cons, 7, acp.Utils.Str("fi_error_records"), 8, rs);

		// Выводим название конфигурации
		AddLine(cp, cons, 8, acp.Utils.Str("fi_config"), 9, rs);

		JButton adv = new JButton(acp.Utils.Str("More"));
		cons.gridx = 0;				cons.gridy = 10;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		cp.add(adv,cons);

		adv.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				long fid = 0;
				try {
					fid = rs.getInt(1);
				} catch (SQLException e1) {
					acp.Utils.Test(e1.getMessage());
				}
				AdvFileInfo advfileInfo = new AdvFileInfo(conn,fid);
				getDesktopPane().add(advfileInfo);
				try {
					advfileInfo.setSelected(true);
				} catch (java.beans.PropertyVetoException e1) {}
			    //advfileInfo.Modal(true);
			}
		});
		
		JButton btn_close = new JButton(acp.Utils.Str("Close"));
		cons.gridx = 1;				cons.gridy = 10;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		cp.add(btn_close,cons);

		btn_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		pack();
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
	
	private void AddLine(Container comp, GridBagConstraints cons, int row, String title, int index, SSJdbcRowSetImpl rs){
		
		JLabel lbl = new JLabel(title);
		cons.gridx = 0;				cons.gridy = row;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.EAST;
		comp.add(lbl,cons);

		JLabel text = new JLabel();
		text.setForeground(new Color(0, 0, 128));
		try {
			text.setText(rs.getString(index));
		} catch (SQLException e1) {
			acp.Utils.Test(e1.getMessage());
		}
		cons.gridx = 1;				cons.gridy = row;
		cons.insets = new Insets(2,5,2,5);
		cons.fill = GridBagConstraints.LINE_START;
		cons.anchor = GridBagConstraints.WEST;
		comp.add(text,cons);
	}
}

