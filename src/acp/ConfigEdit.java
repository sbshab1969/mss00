package acp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;

// xmlparserv2.jar
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

// xdb.jar
import oracle.xdb.XMLType;

// ojdbc6.jar
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.CLOB;

// swingset.jar
import com.nqadmin.swingSet.datasources.*;

public class ConfigEdit extends MyInternalFrame {

	public boolean changed = false;
	private static final long serialVersionUID = 1L;

	private Document doc = null;
	private JTree tree;
	private DefaultTreeModel treeModel;

	private JScrollPane treeView;
	private JSplitPane spliter;
	private JPanel cont;
	private JPanel cont1;
	private JPanel cont2;
	private JPanel cont3;

	CLOB clob;
	private String maintitle;

	public ConfigEdit(final SSConnection conn, final long record_id) {
		setTitle(acp.Utils.Str("ConfigEditTitle"));
		setMaximizable(true);
		setResizable(true);
		setClosable(true);

		final SSJdbcRowSetImpl rs = new SSJdbcRowSetImpl(conn);
		String query = "select t.msso_config.getclobval(), t.msso_name from mss_options t " + 
                   "where msso_id=" + record_id;
		try {
			rs.setCommand(query);
			rs.execute();
			rs.first();
  		clob = (CLOB) rs.getObject(1);
			maintitle = rs.getString(2);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(),
			    acp.Utils.Str("Error"), JOptionPane.ERROR_MESSAGE);
		}

		try {
			Reader reader = clob.getCharacterStream();
	    InputSource is = new InputSource(reader);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			Element root = doc.getDocumentElement();
			root.normalize();
			createTree(maintitle, doc, conn, record_id);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
			    acp.Utils.Str("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (ParserConfigurationException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(),
			    acp.Utils.Str("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (SAXException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
			    acp.Utils.Str("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),
			    acp.Utils.Str("Error"), JOptionPane.ERROR_MESSAGE);
		}

		doLayout();
		setVisible(true);
		try {
			setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}
	}

	private void createTree(final String str, final Document doc,
	    final SSConnection conn, final long record_id) {
		Element root = doc.getDocumentElement();
		System.out.println("root: " + root.getNodeName());
		
		NodeList childs = root.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			final Node cfg = childs.item(i);
			if (!(cfg instanceof Text || cfg instanceof Comment)) {
				String nodeName = cfg.getNodeName().trim();
				System.out.println("child root (cfg): " + nodeName);
				if (nodeName.equalsIgnoreCase("ats") || 
						nodeName.equalsIgnoreCase("eml.mailer") || 
						nodeName.equalsIgnoreCase("sverka.ats")) {
					
					DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeInfo(str, cfg));
					treeModel = new DefaultTreeModel(top);
					tree = new JTree(treeModel);
					treeView = new JScrollPane(tree);
					cont = new JPanel();
					cont.setLayout(new BorderLayout());
					cont1 = new JPanel();
					cont1.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), acp.Utils.Str("EditConfig")));
					cont2 = new JPanel();
					cont3 = new JPanel();
					cont3.setBorder(new EmptyBorder(5, 5, 5, 5));
					cont3.setLayout(new BorderLayout());

					JButton extbtn = new JButton(Utils.Str("Close"));
					cont3.add(extbtn, BorderLayout.EAST);
					extbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							if (changed) {
								if (acp.Utils.MyConfirmDialog(acp.Utils.Str("ConfirmClose"),
										acp.Utils.Str("Warning"), 1) == 0) {
									dispose();
								}
							} else {
								dispose();
							}
						}
					});

					JButton save = new JButton(acp.Utils.Str("Save"));
					cont3.add(save, BorderLayout.CENTER);
					save.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								OraclePreparedStatement stmt = 
     							(OraclePreparedStatement) conn.getConnection().prepareStatement(
								        "update mss_options set msso_config = ? where msso_id=" + record_id);
                String xmlString = Utils.XML2String(doc);
                System.out.println(xmlString);
                
								XMLType poXML = XMLType.createXML(conn.getConnection(),xmlString);
								stmt.setObject(1, poXML);
								stmt.execute();
							} catch (SQLException e1) {
								Utils.ErrorMsg(Utils.Str("Error"), e1.getMessage());
							}
							changed = false;
							Utils.Test(Utils.Str("SaveOk"));

						}
					});
					
					JButton convert = new JButton(acp.Utils.Str("DirectEdit"));
					cont3.add(convert, BorderLayout.WEST);
					convert.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DirectEditMsg"),
							    acp.Utils.Str("Warning"), 1) == 0) {
								DirectEdit de = new DirectEdit(doc, conn, record_id);
								getDesktopPane().add(de);
								getDesktopPane().getDesktopManager().maximizeFrame(de);
								dispose();
								try {
									de.setSelected(true);
								} catch (java.beans.PropertyVetoException e1) {
								}
								// de.Modal(true);
							}

						}
					});

					Container cp = getContentPane();

					cont.add((new JScrollPane(cont1)), BorderLayout.CENTER);
					cont.add(cont2, BorderLayout.NORTH);
					cont.add(cont3, BorderLayout.SOUTH);

					spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
					spliter.setResizeWeight(0.5);
					spliter.setLeftComponent(treeView);
					spliter.setRightComponent(cont);
					cp.add(spliter);

					tree.removeAll();
					tree.addTreeSelectionListener(new TreeSelectionListener() {
						public void valueChanged(TreeSelectionEvent e) {
							DefaultMutableTreeNode treeNode = 
									(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
							if (treeNode == null)
								return;
//							Object nodeInfo = treeNode.getUserObject();
//							NodeInfo info = (NodeInfo) nodeInfo;
//							PutAttributes(treeNode, info.GetNode());
  						PutAttributes(treeNode);
							spliter.resetToPreferredSizes();
							spliter.setResizeWeight(0.5);
						}
					});

					if (cfg.getChildNodes().getLength() > 0) {
						NodeList params = cfg.getChildNodes();
						for (int j = 0; j < params.getLength(); j++) {
							Node param = params.item(j);
							PutNode(top, param);
						}
					}
					tree.expandPath(new TreePath(top.getPath()));
					tree.setSelectionRow(0);
				}
			}
		}
	}

	private void PutNode(DefaultMutableTreeNode top, Node param) {
		if (!(param instanceof Text || param instanceof Comment)) {
			String title = Utils.DescByKey(param.getNodeName().trim());
			System.out.println("child cfg: " + title);

			DefaultMutableTreeNode treeItem = new DefaultMutableTreeNode(new NodeInfo(title, param));
			top.add(treeItem);
			
			if (param.getChildNodes().getLength() > 0) {
				NodeList subparams = param.getChildNodes();
				for (int i = 0; i < subparams.getLength(); i++) {
					Node subparam = subparams.item(i);
					if (!subparam.getNodeName().equals("field"))
						PutNode(treeItem, subparam);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private void PutAttributes(final DefaultMutableTreeNode top, final Node param) {

	private void PutAttributes(final DefaultMutableTreeNode top) {
		final Node param = ((NodeInfo) top.getUserObject()).GetNode();    
		final NamedNodeMap attrs = param.getAttributes();
		
		System.out.println("top = " + ((NodeInfo) top.getUserObject()));
    System.out.println("param = " + param.getNodeName());
		
		int bl = 0;
		GridBagConstraints cons = new GridBagConstraints();
		cont1.removeAll(); // Чистим верхнюю панель
		cont2.removeAll(); // Чистим нижнюю панель
		cont1.setLayout(new GridBagLayout());
		cons.fill = GridBagConstraints.LINE_START;

		if (TestForFields(param)) {
			JPanel tbl = new JPanel();
			cons.gridx = 0;
			cons.gridy = 0;
			cons.insets = new Insets(0, 5, 0, 5);
			cons.gridwidth = 3;
			cons.anchor = GridBagConstraints.NORTH;
			cont1.add(tbl, cons);
			cons.gridwidth = 1;
			CreateTable(tbl, param);
		} else {
			if (attrs.getLength() > 0) { // Если есть свойства
				for (int i = 0; i < attrs.getLength(); i++) {
					final Node at = attrs.item(i);

					String key = param.getNodeName().trim() + "." + at.getNodeName();
					String title = acp.Utils.DescByKey(key);
					JLabel lbl = new JLabel(title, JLabel.TRAILING);
					
					JTextField edt = new JTextField(at.getNodeValue(), 30);
					edt.addKeyListener(new KeyListener() { // Изменение свойства
						public void keyPressed(KeyEvent e) {
						}

						public void keyReleased(KeyEvent e) {
							Object fld = e.getSource();
							at.setNodeValue(((JTextField) fld).getText());
							UpdateNode(top);
							treeModel.reload();
							changed = true;
						}

						public void keyTyped(KeyEvent e) {
						}
					});
					lbl.setLabelFor(edt);

					JButton btn_del = new JButton(acp.Utils.Str("Delete"));
					btn_del.addActionListener(new ActionListener() { 
						// Удаление свойства (кнопочка справа от редактирования)
						    public void actionPerformed(ActionEvent e) {
							    if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecord"),
							        acp.Utils.Str("DeleteItem"), 1) == 0) {
								    attrs.removeNamedItem(at.getNodeName());
								    
								    UpdateNode(top);  //  !!!!!!
								    
//								    PutAttributes(top, param);
							      PutAttributes(top);
								    spliter.resetToPreferredSizes();
								    spliter.setResizeWeight(0.5);
								    changed = true;
							    }
						    }
					    });

					cons.gridx = 0;
					cons.gridy = i + 1;
					cons.insets = new Insets(0, 5, 0, 5);
					cons.anchor = GridBagConstraints.EAST;
					cont1.add(lbl, cons);

					cons.gridx++;
					cons.gridy = i + 1;
					cons.insets = new Insets(2, 2, 2, 2);
					cons.fill = GridBagConstraints.WEST;
					cont1.add(edt, cons);

					cons.gridx++;
					cons.gridy = i + 1;
					cons.insets = new Insets(2, 2, 2, 2);
					cons.anchor = GridBagConstraints.EAST;
					cont1.add(btn_del, cons);
				}
			}
		}
		cont2.setLayout(new BorderLayout());

		final JPanel chg = new JPanel();
		chg.setBorder(new TitledBorder(new LineBorder(Color.BLACK), acp.Utils.Str("PropValid")));
		cont2.add(chg, BorderLayout.NORTH);
		chg.setVisible(false);
		chg.setLayout(new GridBagLayout());
		cons.fill = GridBagConstraints.LINE_START;

		JPanel btn1 = new JPanel();
		cont2.add(btn1, BorderLayout.WEST);

		JPanel btn = new JPanel();
		cont2.add(btn, BorderLayout.EAST);

		JButton btn_del = new JButton(acp.Utils.Str("Delete")); // Удаление
		// раздела (узла
		// в дереве)
		btn1.add(btn_del);
		btn_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecord"),
				    acp.Utils.Str("DeleteItem"), 1) == 0) {
					
					param.getParentNode().removeChild(param);
					
					TreePath currentSelection = tree.getSelectionPath();
					if (currentSelection != null) {
						DefaultMutableTreeNode currentNode = 
								(DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
						MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
						if (parent != null) {
//							tree.setSelectionPath(tree.getPathForRow(0));
							treeModel.removeNodeFromParent(currentNode);
//							treeModel.reload();
//	  	  			tree.expandPath(new TreePath(top.getPath()));
							tree.setSelectionRow(0);
							changed = true;
						}
					}
				}
			}
		});

		JButton btn_add = new JButton(acp.Utils.Str("Add")); // Показать/скрыть
		// панель
		// доступных
		// свойств/подразделов
		btn.add(btn_add);
		btn_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chg.setVisible(!chg.isVisible());
			}
		});

		if (acp.Utils.MBParagraph(param.getNodeName())) { // Если есть подразделы

			final Vector<String> pars = acp.Utils.GetParagraphs(param.getNodeName()); // Получить
			                                                                          // список
			final JComboBox cmbpars = new JComboBox();
			for (int i = 0; i < pars.size(); i++) {
				cmbpars.addItem(acp.Utils.DescByKey(pars.get(i).toString()));
			}
			JButton add_par = new JButton(acp.Utils.Str("AddParagraph")); // Добавить подраздел
			add_par.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean exist = false;
					if (acp.Utils.ExistSubNodes(param, pars.get(cmbpars.getSelectedIndex()).toString())) { 
						// если есть уже такое
						if (acp.Utils.MyConfirmDialog(acp.Utils.Str("ExistItem"),
						    acp.Utils.Str("Warning"), 1) == 0) { // Будем добавлять?
							exist = true;
						}
					} else {
						exist = true;
					}
					if (exist) {
						Element item = param.getOwnerDocument().
								           createElement(pars.get(cmbpars.getSelectedIndex()).toString());
						param.appendChild(item); // Добавляем узел в XML
						
						if (acp.Utils.ExistSubNodes(param,pars.get(cmbpars.getSelectedIndex()).toString())) { 
							// Проверяем добавлено или нет
							JOptionPane.showMessageDialog(null, acp.Utils.Str("GoodAdd"),
							    acp.Utils.Str("Info"), JOptionPane.INFORMATION_MESSAGE);
							
							String title = acp.Utils.DescByKey(pars.get(cmbpars.getSelectedIndex()).toString());
							DefaultMutableTreeNode item_new = new DefaultMutableTreeNode(new NodeInfo(title, item)); 
							// Добавляем ветку в дерево
							top.add(item_new);
							
							treeModel.reload();
							tree.expandPath(new TreePath(top.getPath()));
							changed = true;
						}
					}
				}
			});

			cons.gridx = 0;
			cons.gridy = bl + 1;
			cons.insets = new Insets(2, 2, 2, 2);
			cons.anchor = GridBagConstraints.EAST;
			chg.add(cmbpars, cons);

			cons.gridx++;
			cons.gridy = bl + 1;
			cons.insets = new Insets(2, 2, 2, 2);
			cons.anchor = GridBagConstraints.WEST;
			chg.add(add_par, cons);

			bl++;
		}

		if (acp.Utils.MBProperty(param.getNodeName())) {

			final Vector<String> props = acp.Utils.GetProperties(param.getNodeName());
			final JComboBox cmbprops = new JComboBox();
			for (int i = 0; i < props.size(); i++) {
				cmbprops.addItem(acp.Utils.DescByKey(param.getNodeName() + "." + props.get(i).toString()));
			}
			JButton add_prop = new JButton(acp.Utils.Str("AddAttribute"));
			add_prop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean exist = false;
					if (acp.Utils.ExistAttrs(param, props.get(cmbprops.getSelectedIndex()).toString())) {
						if (acp.Utils.MyConfirmDialog(acp.Utils.Str("ExistItem"),
						    acp.Utils.Str("Warning"), 1) == 0) {
							exist = true;
						}
					} else {
						exist = true;
					}
					if (exist) {
						Attr new_attr = param.getOwnerDocument().createAttribute(
						    props.get(cmbprops.getSelectedIndex()).toString());
						param.getAttributes().setNamedItem(new_attr);
						
						if (acp.Utils.ExistAttrs(param,props.get(cmbprops.getSelectedIndex()).toString())) {
							JOptionPane.showMessageDialog(null, acp.Utils.Str("GoodAdd"),
							    acp.Utils.Str("Info"), JOptionPane.INFORMATION_MESSAGE);
							
//							PutAttributes(top, param);
  						PutAttributes(top);
							spliter.resetToPreferredSizes();
							spliter.setResizeWeight(0.5);
							UpdateNode(top);
							treeModel.reload();
							tree.expandPath(new TreePath(top.getPath()));
							changed = true;
						}
					}
				}
			});

			cons.gridx = 0;
			cons.gridy = bl + 1;
			cons.insets = new Insets(2, 2, 2, 2);
			cons.anchor = GridBagConstraints.EAST;
			chg.add(cmbprops, cons);

			cons.gridx++;
			cons.gridy = bl + 1;
			cons.insets = new Insets(2, 2, 2, 2);
			cons.anchor = GridBagConstraints.WEST;
			chg.add(add_prop, cons);
		}
		// }
	}

	private void CreateTable(final JPanel tbl, final Node param) {
		// Рисуем таблицу
		boolean fst = true;
		int j = 0;
		final Vector<NewValue> fields = new Vector<NewValue>();

		GridBagConstraints consrec = new GridBagConstraints();
		tbl.removeAll();
		tbl.setLayout(new SpringLayout());
		consrec.fill = GridBagConstraints.LINE_START;

		for (int i = 0; i < param.getChildNodes().getLength(); i++) {
			final Node nd = param.getChildNodes().item(i);
			if (!(nd instanceof Text || nd instanceof Comment)) {
				final NamedNodeMap attr = nd.getAttributes();
				JPanel zag = new JPanel();
				zag.setLayout(new GridBagLayout());
				if (fst) { // Создадим подписи
					for (j = 0; j < attr.getLength(); j++) {
						String title = Utils.DescByKey(nd.getNodeName().trim() + "."
						    + attr.item(j).getNodeName());
						NewValue field = new NewValue();
						field.set_field(attr.item(j).getNodeName());
						field.set_value("");
						fields.add(field);
						consrec.gridx = j;
						consrec.gridy = 0;
						consrec.insets = new Insets(0, 0, 0, 0);
						consrec.anchor = GridBagConstraints.CENTER;
						JTextField titlepnl = new JTextField(title, 15);
						titlepnl.setEditable(false);
						zag.add(titlepnl, consrec);
					}
					consrec.gridx = j + 1;
					consrec.gridy = 0;
					consrec.insets = new Insets(0, 0, 0, 0);
					consrec.anchor = GridBagConstraints.CENTER;
					JTextField titlepnl = new JTextField("Действия", 8);
					titlepnl.setEditable(false);
					zag.add(titlepnl, consrec);
					tbl.add(zag);
					fst = false;
				}
				j = 0;
				final JPanel rec = new JPanel();
				rec.setLayout(new GridBagLayout());
				for (j = 0; j < attr.getLength(); j++) {
					final Node at = attr.item(j);
					JTextField edt = new JTextField(at.getNodeValue(), 15);
					consrec.gridx = j;
					consrec.gridy = 0;
					consrec.insets = new Insets(0, 0, 0, 0);
					consrec.anchor = GridBagConstraints.EAST;
					rec.add(edt, consrec);
					edt.addKeyListener(new KeyListener() { // Изменение свойства
						public void keyPressed(KeyEvent e) {
						}

						public void keyReleased(KeyEvent e) {
							Object fld = e.getSource();
							at.setNodeValue(((JTextField) fld).getText());
							changed = true;
						}

						public void keyTyped(KeyEvent e) {
						}
					});

				}
				JButton btn_del = new JButton(Utils.Str("Delete"));
				consrec.gridx = j + 1;
				consrec.gridy = 0;
				consrec.insets = new Insets(0, 0, 0, 0);
				consrec.anchor = GridBagConstraints.EAST;
				rec.add(btn_del, consrec);
				btn_del.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { // Удаление
						// строки
						if (acp.Utils.MyConfirmDialog(acp.Utils.Str("DeleteRecord"),
						    acp.Utils.Str("DeleteItem"), 1) == 0) {
							rec.removeAll();
							tbl.remove(rec);
							tbl.updateUI();
							SpringUtilities.makeCompactGrid(tbl, tbl.getComponentCount(), 1, 3, 3, 5, 0);
							nd.getParentNode().removeChild(nd);
							changed = true;
						}
					}
				});
				tbl.add(rec);
			}
		}

		JPanel foot = new JPanel();
		foot.setLayout(new GridBagLayout());
		int k = 0;
		for (final NewValue field : fields) {
			final JTextField edt = new JTextField(field.get_value(), 15);
			consrec.gridx = k++;
			consrec.gridy = 0;
			consrec.insets = new Insets(0, 0, 0, 0);
			consrec.anchor = GridBagConstraints.EAST;
			foot.add(edt, consrec);
			edt.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
					field.set_value(edt.getText());
				}

				public void keyTyped(KeyEvent e) {
				}
			});
		}
		JButton btn_add = new JButton(Utils.Str("Add"));
		consrec.gridx = k;
		consrec.gridy = 0;
		consrec.insets = new Insets(0, 0, 0, 0);
		consrec.anchor = GridBagConstraints.EAST;
		foot.add(btn_add, consrec);
		btn_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element item = param.getOwnerDocument().createElement("field");
				param.appendChild(item); // Добавляем поле
				for (NewValue field : fields) {
					Attr new_attr = item.getOwnerDocument().createAttribute(field.get_field());
					item.getAttributes().setNamedItem(new_attr);
					new_attr.setValue(field.get_value());
				}
				CreateTable(tbl, param);
				tbl.updateUI();
				changed = true;
			}
		});

		final JButton btn_imp = new JButton(Utils.Str("Import"));
		consrec.gridx = 0;
		consrec.gridy = 1;
		consrec.insets = new Insets(0, 0, 0, 0);
		consrec.anchor = GridBagConstraints.WEST;
		foot.add(btn_imp, consrec);
		btn_imp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("Excel text files", "csv", "txt"));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (fc.showOpenDialog(btn_imp) == JFileChooser.APPROVE_OPTION) {
					File vDir = fc.getSelectedFile();
					if (vDir.isFile()) {
						try {
							FileReader fr = new FileReader(vDir.getAbsolutePath());
							BufferedReader br = new BufferedReader(fr);
							String line;
							String[] flds;
							boolean skipmore = false;
							while ((line = br.readLine()) != null) {
								flds = line.split(";");
								if (flds.length < fields.size()) {
									Utils.ErrorMsg(Utils.Str("Error"),
									    "Количество полей в импортируемом файле меньше необходимого !");
									break;
								}
								if (flds.length > fields.size()) {
									if (!skipmore) {
										if (Utils.MyConfirmDialog("Количество полей в импортируемом файле больше " + 
										    "необходимого !\nХотите пропускать лишние?",
										        Utils.Str("Warning"), 0) == 0)
											skipmore = true;
										else {
											skipmore = false;
											break;
										}
									}
								}
								Element item = param.getOwnerDocument().createElement("field");
								param.appendChild(item); // Добавляем поле
								for (int f = 0; f < fields.size(); f++) {
									Attr new_attr = item.getOwnerDocument().createAttribute(fields.get(f).get_field());
									item.getAttributes().setNamedItem(new_attr);
									new_attr.setValue(flds[f]);
								}
							}
							fr.close();
						} catch (FileNotFoundException e1) {
							Utils.ErrorMsg(Utils.Str("Error"), e1.getMessage());
						} catch (IOException e1) {
							Utils.ErrorMsg(Utils.Str("Error"), e1.getMessage());
						}
						CreateTable(tbl, param);
						tbl.updateUI();
						changed = true;
					}
				} else
					Utils.ErrorMsg(Utils.Str("Error"), "Не задано имя файла для импорта!");
			}
		});

		final JButton btn_exp = new JButton(Utils.Str("Export"));
		consrec.gridx = 2;
		consrec.gridy = 1;
		consrec.insets = new Insets(0, 0, 0, 0);
		consrec.anchor = GridBagConstraints.EAST;
		foot.add(btn_exp, consrec);
		btn_exp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showSaveDialog(btn_exp) == JFileChooser.APPROVE_OPTION) {
					File vDir = fc.getSelectedFile();
					try {
						FileWriter fw = new FileWriter(vDir.getAbsolutePath());
						for (int i = 0; i < param.getChildNodes().getLength(); i++) {
							final Node nd = param.getChildNodes().item(i);
							if (!(nd instanceof Text || nd instanceof Comment)) {
								final NamedNodeMap attr = nd.getAttributes();
								String line = "";
								for (int j = 0; j < attr.getLength(); j++) {
									line += attr.item(j).getNodeValue();
									if (j < attr.getLength() - 1)
										line += ";";
								}
								fw.write(line + "\n");
							}
						}
						fw.close();
					} catch (IOException e1) {
						Utils.ErrorMsg(Utils.Str("Error"), e1.getMessage());
					}
				}
			}
		});

		tbl.add(foot);
		SpringUtilities.makeCompactGrid(tbl, tbl.getComponentCount(), 1, 3, 3, 5, 0);
	}

	private boolean TestForFields(Node attr) {
		for (int i = 0; i < attr.getChildNodes().getLength(); i++)
			if (attr.getChildNodes().item(i).getNodeName().equals("field"))
				return true;
		return false;
	}

	private class NewValue {
		private String _field;
		private String _value;

		public void set_field(String _field) {
			this._field = _field;
		}

		public String get_field() {
			return _field;
		}

		public void set_value(String _value) {
			this._value = _value;
		}

		public String get_value() {
			return _value;
		}
	}

	private class NodeInfo {
		public String Title;
		public Node Param;

		public NodeInfo(String title, Node param) {
			Title = title;
			Param = param;
			NamedNodeMap attr = param.getAttributes();
			if (attr.getLength() > 0) { // Если есть свойства
				Title += " [ ";

				for (int i = 0; i < attr.getLength(); i++) {
					Node at = attr.item(i);
					if (i > 0)
						Title += ", ";
					Title += at.getNodeValue();
				}
				Title += " ]";
			}
		}

		public String toString() {
			return Title;
		}

		public Node GetNode() {
			return Param;
		}

		public void SetTitle(String str) {
			Title = str;
		}
	}

	static void UpdateNode(DefaultMutableTreeNode top) {
		NodeInfo item = (NodeInfo) top.getUserObject();
		String Title = acp.Utils.DescByKey(item.GetNode().getNodeName().trim());

		NamedNodeMap attr = item.GetNode().getAttributes();
		if (attr.getLength() > 0) { // Если есть свойства
			Title += " [ ";

			for (int i = 0; i < attr.getLength(); i++) {
				Node at = attr.item(i);
				if (i > 0)
					Title += ", ";
				Title += at.getNodeValue();
			}
			Title += " ]";
		}
		item.SetTitle(Title);
	}

}
