package acp;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;

import com.nqadmin.swingSet.datasources.SSConnection;

public class About extends MyInternalFrame{
	private static final long serialVersionUID = 1L;

	public About(SSConnection conn){
		String t = null;
        setTitle(acp.Utils.Str("AboutTitle"));
		setSize(600,400);
        //setResizable(true);
        setClosable(true);
        Container cp = getContentPane();
        cp.setLayout(new SpringLayout());
        JEditorPane txt = new JEditorPane();
        txt.setEditorKit(new HTMLEditorKit());
        txt.setEditable(false);
        txt.setBorder(new LineBorder(Color.BLACK));
        t = "<html><head></head><body style=\"font: Sans 10pt\">";
        t += "<table width=\"100%\">";
        t += "<tr><td colspan=\"3\" align=\"center\"><h3>"+Utils.GetVarString(conn, "cert_system")+"</h3></td></tr>";
//      t += "<tr><td colspan=\"3\" align=\"center\"><b>Автоматизированная система расчетов за услуги связи \"Самотлор\" (версия ПО 2.0)</b></td></tr>";

        t += "<tr><td colspan=\"3\" align=\"center\"><h4>Комплекс \""+Utils.GetVarString(conn, "cert_product")+"\"</h4></td></tr>";
//      t += "<tr><td colspan=\"3\" align=\"center\"><b>Сервер предварительной обработки входных данных</b><br></td></tr>";

        t += "<tr><td colspan=\"2\">Релиз ПО:</td>";
        t += "<td>"+Utils.GetVarString(conn, "version_mss")+" от "+Utils.GetVarDate(conn, "version_mss","dd.mm.yyyy")+"</td></tr>";

        t += "<tr><td colspan=\"2\">Технические условия:</td>";
        t += "<td>"+Utils.GetVarString(conn, "cert_tu")+"</td></tr>";

        t += "<tr><td colspan=\"2\">Заводской номер:</td>";
        t += "<td>"+Utils.GetVarString(conn, "cert_partnumber")+"</td></tr>";
        
        t += "<tr><td colspan=\"3\">Контактная информация:</td></tr>";
        t += "<tr><td colspan=\"3\">Межрегиональный филиал информационно-сетевых технологий ОАО \"Уралсвязьинформ\"</td></tr>";
        t += "<tr><td colspan=\"3\">"+Utils.GetVarString(conn, "cert_address")+"</td></tr>";

        t += "<tr><td width=\"30\">&nbsp;</td><td colspan=\"2\" width=\"90%\">тел.: "+Utils.GetVarString(conn, "cert_phone")+"</td></tr>";
        t += "<tr><td>&nbsp;</td><td colspan=\"2\">факс: "+Utils.GetVarString(conn, "cert_fax")+"</td></tr>";
        t += "<tr><td>&nbsp;</td><td colspan=\"2\">e-mail: <a href=\""+Utils.GetVarString(conn, "cert_email")+"\">"+Utils.GetVarString(conn, "cert_email")+"</a></td></tr>";
        t += "<tr><td>&nbsp;</td><td colspan=\"2\">support e-mail: <a href=\""+Utils.GetVarString(conn, "cert_email_support")+"\">"+Utils.GetVarString(conn, "cert_email_support")+"</a></td></tr>";
        t += "<tr><td>&nbsp;</td><td colspan=\"2\"><a href=\""+Utils.GetVarString(conn, "cert_www")+"\">"+Utils.GetVarString(conn, "cert_www")+"</a></td></tr>";
      
        t += "</table></body></html>";
        txt.setText(t);
        cp.add(txt);
        
        SpringUtilities.makeCompactGrid(cp,1,1,5,5,5,5);
       /* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/getWidth(),(screenSize.height-getHeight())/getHeight());*/
        Point newLocation = new Point((acp.Main.desktop.getWidth()-getWidth())/2,(acp.Main.desktop.getHeight()-getHeight())/2);
        setLocation(newLocation);
        //setLocation((setSize.width-getWidth())/2,(setSize.height-getHeight())/2);
        doLayout();
        setVisible(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
}
