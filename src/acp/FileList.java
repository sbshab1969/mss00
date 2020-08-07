package acp;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;
import com.nqadmin.swingSet.SSDataGrid;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;



public class FileList extends MyInternalFrame{
	private static final long serialVersionUID = 1L;
    private boolean flt_dt_flag = false;
    private boolean flt_file_flag = false;
    private boolean flt_owner_flag = false;
    private boolean flt_records_flag = false;
    //private boolean flt_time_flag = false;
	private static final long DATE_BEFORE = 7L;



    public FileList(final SSConnection conn) {
    	setMaximizable(true);
        final Calendar dt1flt;
        final Calendar dt2flt;
        final SimpleDateFormat formatdt = new SimpleDateFormat("dd.MM.yy");
        final String dt1str;
        final String dt2str;


        final String[] fields ={"mssf_id","mssf_name","mssf_md5","mssf_dt_work",
     		                            "extract(mssf_statistic,'statistic/records/all/text()').getStringval()"};
        final String[] fieldnames ={"ID","Имя файла","MD5","Обработано","Записей"};
        final SSDataGrid table = new SSDataGrid();

        setTitle("Загруженые файлы");
 	    setSize(640,480);
        setResizable(true);
        setClosable(true);
        Container cp = this.getContentPane();
        BorderLayout springLayout = new BorderLayout();
        cp.setLayout(springLayout);
        
        // Панель для фильтра       
        JPanel flt = new JPanel();
        flt.setLayout(new SpringLayout());
        flt.setBorder(new LineBorder(Color.DARK_GRAY));    
        
        
        /* Фильтр по дате */
        JPanel flt_dt_work = new JPanel();
        flt_dt_work.setLayout(new SpringLayout());
        flt_dt_work.setBorder(new LineBorder(Color.BLACK));
        flt.add(flt_dt_work);
        
        
        // Начальная дата
        dt1flt = new GregorianCalendar(); // Инициализация датой текущая-константа 
        dt1flt.add(Calendar.DAY_OF_YEAR,(int) - DATE_BEFORE);
        dt1str = formatdt.format(dt1flt.getTime()); 
        
        JLabel dt1label = new JLabel(acp.Utils.Str("Begin"), JLabel.TRAILING);
        final JFormattedTextField dt1 = new JFormattedTextField(formatdt);
        dt1.setFocusLostBehavior(JFormattedTextField.PERSIST);
        dt1.setText(dt1str);
        try {
			dt1.commitEdit();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
        dt1label.setLabelFor(dt1);
        // Конечная дата
        dt2flt = new GregorianCalendar(); // Инициализация текущей датой
        dt2str = formatdt.format(dt2flt.getTime()); 

        JLabel dt2label = new JLabel(acp.Utils.Str("End"), JLabel.TRAILING);
        final JFormattedTextField dt2 = new JFormattedTextField(formatdt);
        dt2.setFocusLostBehavior(JFormattedTextField.PERSIST);
        dt2.setText(dt2str);
        /*try {
        	dt2.commitEdit();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}*/
        dt2label.setLabelFor(dt2);
        
        JCheckBox flt_dt_check = new JCheckBox();
        flt_dt_check.setSelected(flt_dt_flag);
        flt_dt_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_dt_flag	=!flt_dt_flag;
			}
        });
        flt_dt_work.add(flt_dt_check);
        flt_dt_work.add(new JLabel(acp.Utils.Str("DateWork")));
        flt_dt_work.add(dt1label);
        flt_dt_work.add(dt1);
        flt_dt_work.add(dt2label);
        flt_dt_work.add(dt2);
        SpringUtilities.makeCompactGrid(flt_dt_work,1,6,3,3,5,1);

 /* Панель для фильтров имени файла и пользователя */
        JPanel flt_file_owner = new JPanel();
        flt_file_owner.setLayout(new SpringLayout());
        flt.add(flt_file_owner);
        JPanel flt_file = new JPanel();
        flt_file.setBorder(new LineBorder(Color.BLACK));
        flt_file.setLayout(new SpringLayout());
        flt_file_owner.add(flt_file);
        JPanel flt_owner = new JPanel();
        flt_owner.setBorder(new LineBorder(Color.BLACK));
        flt_owner.setLayout(new SpringLayout());
        flt_file_owner.add(flt_owner);

 /* Фильтр по имени файла */
        JCheckBox flt_file_check = new JCheckBox();
        flt_file_check.setSelected(flt_file_flag);
        flt_file_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_file_flag =!flt_file_flag;
			}
        });
        flt_file.add(flt_file_check);
        JLabel filenamelabel = new JLabel(acp.Utils.Str("FileName"), JLabel.TRAILING);
        flt_file.add(filenamelabel);
        final JTextField filename = new JTextField();
        filenamelabel.setLabelFor(filename);
        flt_file.add(filename);
        SpringUtilities.makeCompactGrid(flt_file,1,3,3,3,5,1);

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
        filenamelabel.setLabelFor(owner);
        flt_owner.add(owner);
        SpringUtilities.makeCompactGrid(flt_owner,1,3,3,3,5,2);
        SpringUtilities.makeCompactGrid(flt_file_owner,1,2,0,3,5,2);

 /* Фильтр по количеству записей */
        JPanel flt_records = new JPanel();
        flt_records.setBorder(new LineBorder(Color.BLACK));
        flt_records.setLayout(new SpringLayout());
        flt.add(flt_records);
        JCheckBox flt_records_check = new JCheckBox();
        flt_records_check.setSelected(flt_records_flag);
        flt_records_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_records_flag =!flt_records_flag;
			}
        });
        flt_records.add(flt_records_check);
        // Минимальное значение
        JLabel minlabel = new JLabel(acp.Utils.Str("Begin"), JLabel.TRAILING);
        final JFormattedTextField min = new JFormattedTextField(new NumberFormatter());
        min.setFocusLostBehavior(JFormattedTextField.COMMIT);
        minlabel.setLabelFor(min);
        flt_records.add(new JLabel(acp.Utils.Str("RecordCount")));
        flt_records.add(minlabel);
        flt_records.add(min);
        // Максимальное значение
        JLabel maxlabel = new JLabel(acp.Utils.Str("End"), JLabel.TRAILING);
        final JFormattedTextField max = new JFormattedTextField(new NumberFormatter());
        max.setFocusLostBehavior(JFormattedTextField.COMMIT);
        maxlabel.setLabelFor(max);
        flt_records.add(maxlabel);
        flt_records.add(max);
        SpringUtilities.makeCompactGrid(flt_records,1,6,3,3,5,1);

 /* Фильтр по времени обработки */
        //JPanel flt_time = new JPanel();
        //flt_time.setBorder(new LineBorder(Color.BLACK));
        //flt_time.setLayout(new SpringLayout());
        //flt.add(flt_time);
        //JCheckBox flt_time_check = new JCheckBox();
        //flt_time_check.setSelected(flt_time_flag);
        /*flt_time_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_time_flag =!flt_time_flag;
			}
        });
        //flt_time.add(flt_time_check);
 
        // Минимальное значение
        //JLabel mintimelabel = new JLabel(acp.Utils.Str("Begin"), JLabel.TRAILING);
        final JFormattedTextField mintime = new JFormattedTextField(new NumberFormatter());
        //mintimelabel.setLabelFor(mintime);
        //flt_time.add(new JLabel(acp.Utils.Str("TimeWork")));
        //flt_time.add(mintimelabel);
        //flt_time.add(mintime);
        // Максимальное значение
        JLabel maxtimelabel = new JLabel(acp.Utils.Str("End"), JLabel.TRAILING);
        final JFormattedTextField maxtime = new JFormattedTextField(new NumberFormatter());
        //maxlabel.setLabelFor(maxtime);
        //flt_time.add(maxtimelabel);
        //flt_time.add(maxtime);
        
        // Начальная дата
     /*  dt1fltti = new GregorianCalendar(); // Инициализация датой текущая-константа 
        //dt1fltti.add(Calendar.DAY_OF_YEAR,(int) - DATE_BEFORE);
        //dt1fltti.add(Calendar.,(int) - DATE_BEFORE);
        dt1strti = formatti.format(dt1fltti.getTime()); 
        
        JLabel dt1labelti = new JLabel(acp.Utils.Str("Begin"), JLabel.TRAILING);
        final JFormattedTextField dt1ti = new JFormattedTextField(formatti);
        dt1ti.setText(dt1strti);
        try {
			dt1ti.commitEdit();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
        dt1labelti.setLabelFor(dt1ti);
        // Конечная дата
        dt2fltti = new GregorianCalendar(); // Инициализация текущей датой
        dt2strti = formatti.format(dt2fltti.getTime()); 

        JLabel dt2labelti = new JLabel(acp.Utils.Str("End"), JLabel.TRAILING);
        final JFormattedTextField dt2ti = new JFormattedTextField(formatti);
        dt2ti.setText(dt2strti);
        try {
			dt2ti.commitEdit();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
        dt2labelti.setLabelFor(dt2ti);
        
        JCheckBox flt_time_check = new JCheckBox();
        flt_time_check.setSelected(flt_time_flag);
        flt_time_check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				flt_time_flag =!flt_time_flag;
			}
        });
        flt_time.add(flt_time_check);
        flt_time.add(new JLabel(acp.Utils.Str("DateWork")));
        flt_time.add(dt1labelti);
        flt_time.add(dt1ti);
        flt_time.add(dt2labelti);
        flt_time.add(dt2ti);*/
        
       //SpringUtilities.makeCompactGrid(flt_time,1,6,3,3,5,1);
       //SpringUtilities.makeCompactGrid(flt_time,0,0,0,0,0,0);
 /* Собственно фильтр */
        JPanel flt_button = new JPanel();
        flt_button.setLayout(new SpringLayout());
        flt.add(flt_button);
        
        
        JButton fltbtn = new JButton(acp.Utils.Str("Search"));
        fltbtn.addActionListener(new ActionListener(){
 		public void actionPerformed(ActionEvent e) {
 			String where ="";
 			if (flt_dt_flag){
 				if (where.length()>0) where+=" and ";
 				//dt1flt.setTime((Date) dt1.getValue());
 				Date dtt1=dt1flt.getTime();
 				//dt2flt.setTime((Date) dt2.getValue());
 				Date dtt2=dt2flt.getTime();
 				if (dtt1.compareTo(dtt2)<0){
 				    
 					if (dt2.getText().length()>0){
 					where+="trunc(mssf_dt_work) between '"+dt1.getText()+"' and '"+dt2.getText()+"'";
 					}else{
 					where+="trunc(mssf_dt_work) between '"+dt1.getText()+"' and '12.10.3010'";   
 					}
 					}else{
 					where+="trunc(mssf_dt_work) between '"+dt2.getText()+"' and '"+dt1.getText()+"'";
 				}
 			}
 			if (flt_file_flag){
 				if (where.length()>0) where+=" and ";
 				if (filename.getText().length()>=0){
 					where+="upper(mssf_name) like upper('%"+filename.getText()+"%')";
 				}else{
 					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 					return;
 				}
 			}
 			if (flt_owner_flag){
 				if (where.length()>0) where+=" and ";
 				if (owner.getText().length()>=0){
 					where+="upper(mssf_owner) like upper('%"+owner.getText()+"%')";
 				}else{
 					JOptionPane.showMessageDialog(null,acp.Utils.Str("MinLength"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 					return;
 				}
 			}
 			if (flt_records_flag){

 				if (where.length()>0) where+=" and ";
 				if (min.getText().length()>0){
 					where+="to_number(extract(mssf_statistic,'statistic/records/all/text()').getstringval())>="+min.getValue();
 				}
 				
 				if (max.getText().length()>0){
 					if (min.getText().length()>0) where+=" and ";
 					where+="to_number(extract(mssf_statistic,'statistic/records/all/text()').getstringval())<="+max.getValue();
 				}
 				
 				
 			}
 			/*if (flt_time_flag){
 				if (where.length()>0) where+=" and ";
 				if (mintime.getText().length()>0){
 					where+="to_number(extract(mssf_statistic,'statistic/seconds/all/text()').getstringval())>="+mintime.getValue();
 				}
 				if (maxtime.getText().length()>0){
 					if (mintime.getText().length()>0) where+=" and ";
 					where+="to_number(extract(mssf_statistic,'statistic/seconds/all/text()').getstringval())<="+maxtime.getValue();
 				}*/
 			

 				/*if (where.length()>0) where+=" and ";
 				dt1fltti.setTime((Date) dt1ti.getValue());
 				Date dtt1ti=dt1fltti.getTime();
 				dt2fltti.setTime((Date) dt2ti.getValue());
 				Date dtt2ti=dt2fltti.getTime();
 				if (dtt1ti.compareTo(dtt2ti)<0){
 					where+="trunc(mssf_dt_work) between '"+dt1ti.getText()+"' and '"+dt2ti.getText()+"'";
 				}else{
 					where+="trunc(mssf_dt_work) between '"+dt2ti.getText()+"' and '"+dt1ti.getText()+"'";
 				}*/
             // }
 			if (where.length()==0){
 	    	   	JOptionPane.showMessageDialog(null,acp.Utils.Str("NoFilter"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 	    	   	return;
 			}
 			SSJdbcRowSetImpl rs_flt = new SSJdbcRowSetImpl(conn);
 			rs_flt.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_files",where,"mssf_dt_work desc"));
 			try {
 				rs_flt.execute();
 			} catch (SQLException ex) {
 				JOptionPane.showMessageDialog(null,ex.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
 			}
             table.setSSRowSet(rs_flt);
             acp.Utils.resizeColumns(table);
 		}
        });
        
        JButton fl_1 = new JButton("Информация о файле");
        fl_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow()>=0){
					SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) table.getSSRowSet();
					int rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						Utils.ErrorMsg(Utils.Str("Error"),e1.getMessage());
					}
					if (rec_id>0){
						FileInfo fileInfo = new FileInfo(conn,rec_id);
						getDesktopPane().add(fileInfo);
						try {
							fileInfo.setSelected(true);
						} catch (java.beans.PropertyVetoException e1) {}
						//fileInfo.Modal(true);
					}
				}else{
					JOptionPane.showMessageDialog(null,acp.Utils.Str("NoSelectRecord"),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
				}
			}
		});
        
	    JButton fl_2 = new JButton("Закрыть");
	    fl_2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        });
        
        flt_button.add(new JPanel());
        flt_button.add(fltbtn);
        flt_button.add(fl_1);
        flt_button.add(fl_2);
        SpringUtilities.makeCompactGrid(flt_button,1,4,3,3,5,2);
        

        
        /* Компоновка всех панелей фильтров */
        
        SpringUtilities.makeCompactGrid(flt,4,1,3,3,2,1);
        cp.add(flt, BorderLayout.NORTH);
        SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
        try {
// 	   	  String where = "mssf_dt_work between '"+dt1str+"' and '"+dt2str+"'";
 	   	  String where = "trunc(mssf_dt_work)=trunc(sysdate)";
          rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_files",where,"mssf_dt_work desc"));
          rs.execute();
        } catch (SQLException ex) {
               JOptionPane.showMessageDialog(null,ex.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
        }
        String where = "1=1 and rownum<=10";
        rs.setCommand(acp.Utils.SetQuery(fields,fieldnames,"mss_files",where,"mssf_dt_work desc"));
        table.setHeaders(fieldnames);
        table.setVisible(true);
        table.setMessageWindow(this);
        table.setSSRowSet(rs);
        table.setUneditableColumns(acp.Utils.GetFields(fieldnames));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        acp.Utils.resizeColumns(table);
        
        
        JScrollPane scrollPane = new JScrollPane(table);
        cp.add(scrollPane, BorderLayout.CENTER);
        
        
        table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				SSJdbcRowSetImpl rst = (SSJdbcRowSetImpl) ((SSDataGrid) e.getSource()).getSSRowSet();
				if (e.getClickCount()==2){
					int rec_id = 0;
					try {
						rst.absolute(table.getSelectedRow()+1);
						rec_id = rst.getInt("ID");
					} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
					}
					if (rec_id>0){
						FileInfo fileInfo = new FileInfo(conn,rec_id);
						getDesktopPane().add(fileInfo);
						try {
							fileInfo.setSelected(true);
						} catch (java.beans.PropertyVetoException e1) {}
						//fileInfo.Modal(true);
					}
				}
			}
		});
        
        SpringUtilities.makeCompactGrid(cp,2,1,5,5,1,5);
        
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
  
        ///////////////
        
        //setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
		
	}
}
