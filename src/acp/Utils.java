package acp;

import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import com.nqadmin.swingSet.*;
import com.nqadmin.swingSet.datasources.*;

public class Utils {
	private static final float MAX_RESIZEABLE_WIDTH = 2f;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void resizeColumns(SSDataGrid table) {
	    TableColumnModel columnModel = table.getColumnModel();
	    TableModel model = table.getModel();
	    int maxWidth = Math.round((float)table.getWidth() / (float)model.getColumnCount() * MAX_RESIZEABLE_WIDTH);
	    for(int ci = 0; ci < table.getColumnCount(); ++ci) {
	        TableColumn column = columnModel.getColumn(ci);
	        if(column.getMaxWidth() > 0) {
	            int preferredWidth = 0;
	            for(int ri = 0; ri < model.getRowCount(); ++ri) {
	                Object val = model.getValueAt(ri, ci);
	                int width = (int) (table.getCellRenderer(ri, ci)).getTableCellRendererComponent(table, val, false, false, ci, ri).getPreferredSize().getWidth();
	                if(preferredWidth < width) {
	                    preferredWidth = width;
	                }
	            }
	            if(preferredWidth > maxWidth) {
	                preferredWidth = maxWidth;
	            }
	            column.setPreferredWidth(preferredWidth);
	        }
	    }
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static String SetQuery(String[] fields, String[] fieldnames, String from, String where, String order){
	    String query ="select ";
	    for (int i=0; i<fields.length; i++){
	         query+=fields[i]+" "+"\""+fieldnames[i]+"\"";
	         if (i!=fields.length-1) query+=",";
	    }
	    query+=" from "+from; 
	    if (! where.equals("")) query+=" where "+where;
	    if (! order.equals("")) query+=" order by "+order;
		return query;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static int[] GetFields(String[] fieldnames){
		int res[]= new int[fieldnames.length];
		for (int i=0; i<fieldnames.length; i++){
			res[i]=i;
		}
		return res;
	}
	
	public static String DescByKey(String key){
		String txt = null;
		try{
			txt = java.util.ResourceBundle.getBundle("acp/config_fields").getString(key);
		}catch(RuntimeException e){
			txt = key;
		}	
		return txt;
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static String XML2String(Document doc){
		
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		}
		//Удаление лишней информации (текст между тегами, комментарии)
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		CleanXML(childs);
		doc.normalizeDocument();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
		String xmlString = result.getWriter().toString();
		return xmlString;
	}
	
	public static void CleanXML(NodeList nodes){
		for (int i=0; i<nodes.getLength(); i++){
			Node cfg = nodes.item(i);
			if ((cfg instanceof Text || cfg instanceof Comment)){
				cfg.setNodeValue(cfg.getNodeValue().trim());
			}
			if (cfg.getChildNodes().getLength()>0){
				CleanXML(cfg.getChildNodes());
			}
		}
	}
	
//	public static String GetFullPath(Node param){
//		String res = param.getNodeName();
//		if (param.getParentNode()!=null){
//			return GetFullPath(param.getParentNode())+"/"+res;
//		}else{
//			return null;
//		} 
//	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static boolean ExistSubNodes(Node param, String attr){
		NodeList childs = param.getChildNodes();
		if (childs.getLength()>0){
			for (int i=0; i<childs.getLength(); i++){
				if (childs.item(i).getNodeName().equals(attr)) 
					return true;
			}
		}
		return false;
	}

	public static boolean ExistAttrs(Node param, String attr){
		NamedNodeMap attrs = param.getAttributes();
    
		if (attrs.getNamedItem(attr) != null) {
    	return true;
    }
/*		if (attrs.getLength()>0){
			for (int i=0; i<attrs.getLength(); i++){
				if (attrs.item(i).getNodeName().equals(attr)) 
					return true;
			}
		}
*/		return false;
	}

//	public static String ExtractPropName(String full_name, String prefix, String separator){
//		String res=null;
//		if (full_name.substring(0,prefix.length()+1).compareToIgnoreCase(prefix+separator)==0){
//			res=full_name.substring(prefix.length()+separator.length());
//		}
//		return res;
//	}
	
	public static int MyConfirmDialog(String message, String title, int initialValue){
		int res=initialValue;
		Object[] options = {acp.Utils.Str("Yes"),acp.Utils.Str("No")}; 
		res=JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[initialValue]);
		return res;
	}

	public static boolean MBParagraph(String str){
		int cnt=0;
		
		try {
			String str1 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString(str);
			StringTokenizer token = new StringTokenizer(str1,",");
			while (token.hasMoreElements()) {
				String str2="";
				try {
					str2 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString((String) token.nextElement());
				} catch (MissingResourceException e) {
				}
				if (str2!="") cnt++;
			}
		} catch (MissingResourceException e) {
		}
		if (cnt>0)
			return true;
		else
			return false;
	}
	
	public static boolean MBProperty(String str){
		int cnt=0;
		
		try {
			String str1 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString(str);
			StringTokenizer token = new StringTokenizer(str1,",");
			while (token.hasMoreElements()) {
				try {
					java.util.ResourceBundle.getBundle("acp/valid_fields").getString((String) token.nextElement());
				} catch (MissingResourceException e) {
					cnt++;
				}
			}
		} catch (MissingResourceException e) {
		}
		if (cnt>0)
			return true;
		else
			return false;
	}
	
	public static Vector<String> GetParagraphs(String str){
		Vector<String> res =  new Vector<String>();
		String key = null;
		String str2 = null;
		try {
			String str1 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString(str); // Список свойств и параграфов
			StringTokenizer token = new StringTokenizer(str1,",");
			while (token.hasMoreElements()) {
				key = (String) token.nextElement(); // Ключ из списка
				str2="";
				try {
					str2 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString(key); // Список подчиненых свойств и параграфов
				} catch (MissingResourceException e) {}
				if (str2!="") { // Если пусто то это просто свойство str
					res.add(key);
				}
			}
		} catch (MissingResourceException e) {
		}
	
		return res;
	}

	public static Vector<String> GetProperties(String str){
		Vector<String> res = new Vector<String>();
		String key = null;
		try {
			String str1 = java.util.ResourceBundle.getBundle("acp/valid_fields").getString(str);
			StringTokenizer token = new StringTokenizer(str1,",");
			while (token.hasMoreElements()) {
				try {
					key = (String) token.nextElement();
					java.util.ResourceBundle.getBundle("acp/valid_fields").getString(key);
				} catch (MissingResourceException e) {
					res.add(key);
				}
			}
		} catch (MissingResourceException e) {
		}
		return res;
	}
	
	public static String Str(String str){
		return java.util.ResourceBundle.getBundle("acp/properties").getString(str);
	}
	
	public static void Test(String str){
		JOptionPane.showMessageDialog(null,str,"",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void ErrorMsg(String title, String msg){
		JOptionPane.showMessageDialog(null,msg,title,JOptionPane.ERROR_MESSAGE);
	}

/* Функции работы с системными переменными */
	
	public static String GetVar(SSConnection conn, String varname){
		String res = null;

        try {
			CallableStatement getvar = conn.getConnection().prepareCall("{?= call GETVAR(?)}");
			getvar.registerOutParameter(1,java.sql.Types.VARCHAR );
			getvar.registerOutParameter(2,java.sql.Types.VARCHAR );
			getvar.setString(2, varname);
			getvar.execute();
			res = getvar.getString(1);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static String GetVarString(SSConnection conn, String varname){
		String res = null;

        try {
			CallableStatement getvar = conn.getConnection().prepareCall("{?= call GETVARV(?)}");
			getvar.registerOutParameter(1,java.sql.Types.VARCHAR );
			getvar.registerOutParameter(2,java.sql.Types.VARCHAR );
			getvar.setString(2, varname);
			getvar.execute();
			res = getvar.getString(1);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}

	public static int GetVarInt(SSConnection conn, String varname){
		int res = 0;

        try {
			CallableStatement getvar = conn.getConnection().prepareCall("{?= call GETVARN(?)}");
			getvar.registerOutParameter(1,java.sql.Types.NUMERIC );
			getvar.registerOutParameter(2,java.sql.Types.VARCHAR );
			getvar.setString(2, varname);
			getvar.execute();
			res = getvar.getInt(1);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static String GetVarDate(SSConnection conn, String varname, String mask){
		String res = null;

        try {
			CallableStatement getvar = conn.getConnection().prepareCall("{?= call GETVARD(?,?)}");
			getvar.registerOutParameter(1,java.sql.Types.VARCHAR );
			getvar.registerOutParameter(2,java.sql.Types.VARCHAR );
			getvar.registerOutParameter(3,java.sql.Types.VARCHAR );
			getvar.setString(2, varname);
			getvar.setString(3, mask);
			getvar.execute();
			res = getvar.getString(1);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null,e1.getMessage(),acp.Utils.Str("Error"),JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}
}

