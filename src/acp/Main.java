package acp;

import com.nqadmin.swingSet.datasources.SSConnection;



import java.awt.Point;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.swing.*;


public class Main extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	static JDesktopPane desktop;
    private SSConnection conn = null;
    private JMenuItem mntmSid;
    private JMenu menu_1;

    public Main() {
        super(acp.Utils.Str("MainTitle"));
        //setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
       desktop = new JDesktopPane();
/*    	try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception  e) {
	  e.printStackTrace();
        }
*/
       desktop = new JDesktopPane();
       setSize(830,560);
       setLocationRelativeTo(null);
       //BorderLayout springLayout = new BorderLayout();
      //desktop.setLayout(springLayout);
        //desktop.setLayout(new FlowLayout());
       /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());
        setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);*/
        //setSize(screenSize);
        setJMenuBar(createMenuBar());
        setContentPane(desktop);
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {                                  
        if (conn!=null) OraDisconnect();
    }                                 
    
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
// Пункт меню Файл
        JMenu menu = new JMenu(acp.Utils.Str("MenuFile"));
       // menuBar.add(menu);
// Пункт меню Файл -> Выход
        JMenuItem menuItem = new JMenuItem(acp.Utils.Str("MenuExit"));
        menuItem.setActionCommand("quit");
        menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK ));
        menuItem.addActionListener(this);
        menu.add(menuItem);
// Пункт меню Протоколы
        menu = new JMenu(acp.Utils.Str("MenuLogs"));
        menuBar.add(menu);
// Пункт меню Протоколы -> Загруженные файлы
        menuItem = new JMenuItem(acp.Utils.Str("MenuFileLogs"));
        menuItem.setActionCommand("filelogs");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
// Пункт меню Протоколы -> Другие протоколы
        menuItem = new JMenuItem(acp.Utils.Str("MenuOtherLogs"));
        menuItem.setActionCommand("otherlogs");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
// Пункт меню Настройки
        menu = new JMenu(acp.Utils.Str("MenuOptions"));
        menuBar.add(menu);
// Пункт меню Настройки -> Константы
        menuItem = new JMenuItem("Системные константы");
        menuItem.setActionCommand("const");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
// Пункт меню Настройки -> Переменные
        menuItem = new JMenuItem("Системные переменные");
        menuItem.setActionCommand("vars");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
// Пункт меню Справочники
        menu = new JMenu(acp.Utils.Str("MenuReference"));
        menuBar.add(menu);
// Пункт меню Справочники -> Источники
        menuItem = new JMenuItem(acp.Utils.Str("MenuRefSource"));
        menuItem.setActionCommand("src");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
// Пункт меню Справочники -> Конфигурации источников
        menuItem = new JMenuItem("Настройки конфигурации источников");
        menuItem.setActionCommand("cfg");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
        menu.add(new JSeparator());        
// Пункт меню Справочники -> Редактирование таблиц
        JMenu submenu = new JMenu(acp.Utils.Str("MenuEditTables"));
        
        // Пункт меню Справочники -> Редактирование таблиц -> Местные номера 
        menuItem = new JMenuItem(acp.Utils.Str("MenuEditLocalNmbs"));
        menuItem.setActionCommand("edit_lclnmbs");
        menuItem.addActionListener(this);
        submenu.add(menuItem);        
     
        // Пункт меню Справочники -> Редактирование таблиц -> SID-pref 
        mntmSid = new JMenuItem("Таблица соответствия SID - префикс");
        mntmSid.setActionCommand("edit_sidpref");
        mntmSid.addActionListener(this);
        submenu.add(mntmSid);        
        
        // Пункт меню Справочники -> Редактирование таблиц -> trace-SOP 
        menuItem = new JMenuItem("Таблица соответствия трасса - СОП");
        menuItem.setActionCommand("edit_tracesop");
        menuItem.addActionListener(this);
        submenu.add(menuItem);        
        menu.add(submenu);

// Пункт меню Файл  
        menu_1 = new JMenu(acp.Utils.Str("MenuFile"));
       // menuBar.add(menu_1);
     // Пункт меню Файл -> Выход
        menuItem = new JMenuItem(acp.Utils.Str("MenuExit"));
        menuItem.setActionCommand("quit");
        menuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK ));
        menuItem.addActionListener(this);
        menu_1.add(menuItem);
        
        
// Пункт меню Помощь
        menuBar.add(Box.createHorizontalGlue());
        menu = new JMenu(acp.Utils.Str("MenuHelp"));
        menuBar.add(menu);
// Пункт меню Помощь -> О программе
        menuItem = new JMenuItem(acp.Utils.Str("MenuHelpAbout"));
        menuItem.setActionCommand("about");
        menuItem.addActionListener(this);
        menu.add(menuItem);        
        return menuBar;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("quit")) System.exit(0);
        if (e.getActionCommand().equals("filelogs")){
            OraConnect();
        	FileList FileList = new FileList(conn);
        	desktop.add(FileList);
        	desktop.getDesktopManager().activateFrame(FileList);
        	Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        	desktop.setLocation(newLocation);
        	FileList.Modal(false);
        }
        if (e.getActionCommand().equals("otherlogs")){
            OraConnect();
        	OtherLogs othlogs = new OtherLogs(conn);
        	desktop.add(othlogs);
        	desktop.getDesktopManager().activateFrame(othlogs);
        	othlogs.Modal(false);
        }
        if (e.getActionCommand().equals("vars")){
        	OraConnect();
        	VarList VarList = new VarList(conn);
        	desktop.add(VarList);
        	desktop.getDesktopManager().activateFrame(VarList);
        	VarList.Modal(false);
        }
        if (e.getActionCommand().equals("const")){
        	OraConnect();
        	ConstList ConstList = new ConstList(conn);
        	desktop.add(ConstList);
        	desktop.getDesktopManager().activateFrame(ConstList);
        	ConstList.Modal(false);

        }
        if (e.getActionCommand().equals("src")){
            OraConnect();
        	SourceList SourceList = new SourceList(conn);
        	desktop.add(SourceList);
        	desktop.getDesktopManager().activateFrame(SourceList);
        	SourceList.Modal(false);
        }
        if (e.getActionCommand().equals("cfg")){
        	OraConnect();
        	ConfigList ConfigList = new ConfigList(conn);
        	desktop.add(ConfigList);
        	desktop.getDesktopManager().activateFrame(ConfigList);
        	ConfigList.Modal(false);
        }
        if (e.getActionCommand().equals("edit_lclnmbs")){
        	OraConnect();
        	Vector<String> prm = new Vector<String>();
        	prm.add("/config/ats/fmt/local_nmbs/field");
        	prm.add("min");
        	prm.add("max");
        	EditTable EditTable = new EditTable(conn, prm);
        	desktop.add(EditTable);
        	desktop.getDesktopManager().activateFrame(EditTable);
        	EditTable.Modal(false);
        }
        if (e.getActionCommand().equals("edit_sidpref")){
        	OraConnect();
        	Vector<String> prm = new Vector<String>();
        	prm.add("/config/ats/fmt/sid_pref/field");
        	prm.add("key");
        	prm.add("value");
        	EditTable EditTable = new EditTable(conn, prm);
        	desktop.add(EditTable);
        	desktop.getDesktopManager().activateFrame(EditTable);
        	EditTable.Modal(false);
        }
        if (e.getActionCommand().equals("edit_tracesop")){
        	OraConnect();
        	Vector<String> prm = new Vector<String>();
        	prm.add("/config/ats/fmt/trace_sop/field");
        	prm.add("key");
        	prm.add("value");
        	EditTable EditTable = new EditTable(conn, prm);
        	desktop.add(EditTable);
        	desktop.getDesktopManager().activateFrame(EditTable);
        	EditTable.Modal(false);
        }
        if (e.getActionCommand().equals("about")){
        	OraConnect();
        	About MyAbout = new About(conn);
        	desktop.add(MyAbout);
        	desktop.getDesktopManager().activateFrame(MyAbout);
        	MyAbout.Modal(false);
        }
    }

	private static void createAndShowGUI() {
//        JFrame.setDefaultLookAndFeelDecorated(true);
        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void OraDisconnect() {}

    private boolean OraConnect() {
        String serv = null;
        String pass = null;
        String login = null;
        String driver = null;
        boolean result = false;
        
        if (conn==null){
        	Properties props = new Properties();
        	FileInputStream fis = null;
			try {
				fis = new FileInputStream("oracle.conf");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(desktop,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
            try {
				props.loadFromXML(fis);
			} catch (InvalidPropertiesFormatException e) {
				JOptionPane.showMessageDialog(desktop,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(desktop,e.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
			}
     

            login = props.getProperty("User","user");
            pass = props.getProperty("Password","pass");
            serv = props.getProperty("ConnectionString","jdbc:oracle:thin:@<server>:<port>:<sid>");
            driver = props.getProperty("Driver", "oracle.jdbc.driver.OracleDriver");
            try {
                    conn = new SSConnection(serv,login,pass);
                    conn.setDriverName(driver);
                    conn.createConnection();
            } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(desktop,ex.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
            }catch(ClassNotFoundException cnfe){
                     cnfe.printStackTrace();
            }
            if (conn!=null){
                result=true;
            }else{
                result=false;
            }
        }else{
            result=false;
        }
        return result;
    }
}
