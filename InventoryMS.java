import javax.swing.*;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.Vector;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
    
public class InventoryMS extends Frame implements Serializable {
	//instance variables
	public static final long serialVersionUID = 5L; 
	private String[] tableColumns = {"Id", " ", "Name", "Price (RM)", "Quantity", "Expiry date", "Supplier"};
	private String[] stockTableColumns = {"Id", "Name"};
	private Vector<String> dateSelection = new Vector<String>();
	private Vector<ItemStock> stock = new Vector<ItemStock>();
    private ArrayList<Item> inventory = new ArrayList<Item>();
    private ArrayList<Item> inventoryTemp = new ArrayList<Item>();
    private String name, supplier, date;
    private double price;
    private int quantity, checkBoxColumnIndex, selectedTab;
    private boolean selected;
    private boolean panelOpened = false;
    private static final int lowStockQuantity = 3; //change this to change low stock limit (smaller than)
    private String dateTemp;
    
    private JPanel tempPanel, stockPanel;
    private JLabel searchLabel;
    private JTextField searchTextField, dateTextField;
    private JTabbedPane tabbedPane;
    private DefaultTableModel inventoryTable, stockTableModel;
    private JTable table, stockTable;
    private JScrollPane sp, stockSP;
    private JButton saveDateButton, rowButton, clearButton, selectButton, deleteButton, checkStockButton, displayAllButton;
    private JComboBox<String> dateCB;
    private JMenuBar inventoryMenu;
    private JMenu inventoryFile, export, importFile;
    private JMenuItem save, saveAs, open, openLastSaved, newFile, exportTxt, importTxt;
    private GridBagConstraints gbc;
    private TableColumnModel columnModel, stockColumnModel;
    private DefaultListSelectionModel selectionModel; 
    private TableRowSorter<TableModel> tRSorter;
    
    //constructor
    public InventoryMS(String name) {
    	super(name);
    	displayGUI();
    }
    
    //gui method
    private void displayGUI() {
    	getFrame().setLayout(new GridLayout(0, 1));
    	getFrame().setMinimumSize(new Dimension(500, 500));
    	getFrame().setPreferredSize(new Dimension(1000, 700));
    	applyFrameLocationOffset(0);
    	
    	inputDateToDateSelection();
        
        buildMenuBar();
        getFrame().setJMenuBar(inventoryMenu);
        
        getFrame().pack();
        
        refreshFrame();
    }
    private void buildATabbedPane(String name) {
    	//initialise
    	tabbedPane = new JTabbedPane();
    	
    	//setting up
    	tabbedPane.add("Inventory Details" + name, tempPanel);
    	tabbedPane.add("Inventory stock list", stockPanel);
    	tabbedPane.setSelectedIndex(selectedTab);
    }
    private void buildAInventoryTable() { 	
    	//initialise
    	checkBoxColumnIndex = 1;
    	inventoryTable = new DefaultTableModel(tableColumns, 0) {
    		@Override
    		public Class<?> getColumnClass(int column) {
    			switch (column) {
    				case 0: return Integer.class;
    				case 1: return Boolean.class;
    				case 2: return String.class;
    				case 3: return Double.class;
    				case 4: return Integer.class;
    				case 5: return String.class;
    				default: return String.class;
    			}
    		}
    	};
    	table = new JTable(inventoryTable);
    	sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	gbc = new GridBagConstraints();
    	dateTextField = new JTextField("[Enter date] dd-mm-yyyy");
    	if (dateSelection.size() == 0) {
    		dateSelection.add("[Select date to display]");
    	}
    	dateCB = new JComboBox<String>(dateSelection);
    	saveDateButton = new JButton("Save date");
    	rowButton = new JButton("New row");
    	deleteButton = new JButton("Delete selected");
    	checkStockButton = new JButton("Display low stock");
    	displayAllButton = new JButton("Display all");
    	searchLabel = new JLabel("Specify a word to search: ");
    	searchTextField = new JTextField();
    	clearButton = new JButton(new CheckBoxSelectionAction("Clear highlighted", false));
    	selectButton = new JButton(new CheckBoxSelectionAction("Select highlighted", true));
        
        //setting up
    	//assign the data from input file to inventory table
        for (int i = 0; i < inventory.size(); ++i) {
            Object[] data = {(i + 1),
            	inventory.get(i).getSelected(),
        		inventory.get(i).getName(),
                inventory.get(i).getPrice(),
                inventory.get(i).getQuantity(),
                inventory.get(i).getExpiryDate(),
                inventory.get(i).getSupplier()};
            inventoryTable.addRow(data);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(20);
        table.getColumn("Id").setCellRenderer(new RowRenderer());
        tRSorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(tRSorter);

        columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(30);
        columnModel.getColumn(1).setPreferredWidth(20);
        columnModel.getColumn(2).setPreferredWidth(450);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(200);
        
        dateTextField.addMouseListener(new DateTextFieldMouseClickedListener());
        saveDateButton.addActionListener(new SaveDateListener());
        dateCB.setEditable(false);
        dateCB.setMaximumRowCount(8);
        dateCB.addActionListener(new selectDateCBListener());
        rowButton.addActionListener(new RowButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());
        checkStockButton.addActionListener(new DisplayLowStockListener());
        displayAllButton.addActionListener(new DisplayAllListener());
        searchLabel.setFont(rowButton.getFont());
        searchTextField.getDocument().addDocumentListener(new searchFilterListener());
        tempPanel = new JPanel(new GridBagLayout());
        tempPanel.setBackground(Color.white);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 10;
        gbc.weighty = 10;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 0;
        tempPanel.add(sp, gbc);
        
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        tempPanel.add(dateCB, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        tempPanel.add(dateTextField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        tempPanel.add(saveDateButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        tempPanel.add(selectButton, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        tempPanel.add(clearButton, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 2;
        tempPanel.add(deleteButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        tempPanel.add(rowButton, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        tempPanel.add(checkStockButton, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 3;
        tempPanel.add(displayAllButton, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 4;
        tempPanel.add(searchLabel, gbc);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        gbc.gridy = 4;
        tempPanel.add(searchTextField, gbc);
    }
    private void buildMenuBar() {
    	//initialise
    	inventoryMenu = new JMenuBar();
    	buildFileMenu();
    	
    	//setting up
    	inventoryMenu.add(inventoryFile);
    }
    private void buildFileMenu() {
    	//initialise
    	save = new JMenuItem("Save");
    	saveAs = new JMenuItem("Save as");
    	open = new JMenuItem("Open");
    	openLastSaved = new JMenuItem("Open last saved (not save as)");
    	newFile = new JMenuItem("New");
    	exportTxt = new JMenuItem("Text file");
    	importTxt = new JMenuItem("Text file");
    	inventoryFile = new JMenu("File");
    	export = new JMenu("Export");
    	importFile = new JMenu("Import");
    	
    	//setting up
    	save.setMnemonic(KeyEvent.VK_S);
    	save.addActionListener(new SaveListener());
    	saveAs.addActionListener(new SaveAsListener());
    	open.addActionListener(new OpenListener());
    	open.setMnemonic(KeyEvent.VK_O);
    	openLastSaved.addActionListener(new OpenLastSavedListener());
    	newFile.addActionListener(new NewListener());
    	newFile.setMnemonic(KeyEvent.VK_N);
    	exportTxt.addActionListener(new ExportTextListener());
    	importTxt.addActionListener(new ImportTextListener());
    	inventoryFile.add(newFile);
    	inventoryFile.addSeparator();
    	inventoryFile.add(open);
    	inventoryFile.add(openLastSaved);
    	inventoryFile.addSeparator();
    	inventoryFile.add(save);
    	inventoryFile.add(saveAs);
    	inventoryFile.addSeparator();
    	importFile.add(importTxt);
    	inventoryFile.add(importFile);
    	export.add(exportTxt);
    	inventoryFile.add(export);
    }
    private void buildAStockTable() {
    	//initialise
    	stockPanel = new JPanel(new GridBagLayout());
		stockTableModel = new DefaultTableModel(stockTableColumns, 0) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		stockTable = new JTable(stockTableModel);
		stockSP = new JScrollPane(stockTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//setting up
		stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		stockTable.getTableHeader().setReorderingAllowed(false);
		stockTable.setRowHeight(20);
		stockTable.getColumn("Id").setCellRenderer(new RowRenderer());
		
		stockColumnModel = stockTable.getColumnModel();
		stockColumnModel.getColumn(0).setPreferredWidth(30);
		stockColumnModel.getColumn(1).setPreferredWidth(450);
		
		gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
		stockPanel.add(stockSP, gbc);
    }
    
    //function methods
    private void addRowToTable() {
        //add new row to inventory details table
    	try {
	    	Object[] data = {inventoryTable.getRowCount() + 1, false, "New", 0.0, 0, "00-00-0000", "null"};
	        inventoryTable.addRow(data);
        } catch (NullPointerException npe) {
        	JOptionPane.showMessageDialog(getFrame(), "Please open or create a new file");
        }
    }
    private void retrieveDataFromTable() {
        //get data from inventory details table and add to inventoryTemp
    	inventoryTemp.clear();
    	for (int i = 0; i < inventoryTable.getRowCount(); ++i) {
            for (int j = 1; j < inventoryTable.getColumnCount(); ++j) {
            	try {
	            	if (j == 1) {
	            		selected = (Boolean)inventoryTable.getValueAt(i, j);
	            	} else if (j == 2) {
	            		name = (String)inventoryTable.getValueAt(i, j);
	            	} else if (j == 3) {
		                price = (Double)inventoryTable.getValueAt(i, j);
	            	} else if (j == 4) {          		
		                quantity = (Integer)inventoryTable.getValueAt(i, j);
	            	} else if (j == 5) {
		                date = (String)inventoryTable.getValueAt(i, j);
	            	} else if (j == 6) {
		                supplier = (String)inventoryTable.getValueAt(i, j);
	                }
            	} catch(ClassCastException cce) {
            		JOptionPane.showMessageDialog(getFrame(), "Wrong data format, please check your table again");
            		return;
            	}
            }
            inventoryTemp.add(new Item(selected, name, price, quantity, date, supplier));
        }
    }
    private void refreshFrame() {
    	//to refresh/reset frame after changes done to panel
    	if (panelOpened == false) {
	        selectedTab = 0;
	        buildAInventoryTable();
	        buildAStockTable();
	        buildATabbedPane("");
	        getFrame().getContentPane().add(tabbedPane);
	        panelOpened = true;
	        getFrame().setVisible(true);
        } else {
        	getFrame().getContentPane().removeAll();
        	buildAInventoryTable();
        	buildAStockTable();
        	buildATabbedPane("");
        	getFrame().revalidate();
        	getFrame().repaint();
        	getFrame().getContentPane().add(tabbedPane);
        	getFrame().setVisible(true);
        }
    	transferQuantityToStockTable();
    }
    private void refreshFrame(String name) {
    	//to refresh/reset frame after changes done to panel
    	//receive name argument and add to tab name
    	if (panelOpened == false) {
	        selectedTab = 0;
	        buildAInventoryTable();
	        buildAStockTable();
	        buildATabbedPane(name);
	        getFrame().getContentPane().add(tabbedPane);
	        panelOpened = true;
	        getFrame().setVisible(true);
        } else {
        	getFrame().getContentPane().removeAll();
        	buildAInventoryTable();
        	buildAStockTable();
        	buildATabbedPane(name);
        	getFrame().revalidate();
        	getFrame().repaint();
        	getFrame().getContentPane().add(tabbedPane);
        	getFrame().setVisible(true);
        }
    	transferQuantityToStockTable();
    }
    private void autoAddAColumnToTable(String date) {
    	//add new column to stock table
    	stockTableModel.addColumn(date.trim());
    	stockColumnModel.getColumn(0).setPreferredWidth(30);
		stockColumnModel.getColumn(1).setPreferredWidth(450);
    }
	private void saveDateTextFieldtoDateSelection() {
		//get text from dateTextField and add to dateSelection
		Boolean dateExist = false;
		if (dateSelection.size() == 1) {
			dateSelection.add(dateTextField.getText().trim());
		} else if (dateSelection.size() > 1) {
			for (int i = 1; i < dateSelection.size(); ++i) {
				if (dateTextField.getText().trim().equals(dateSelection.get(i))) {
					dateExist = true;
					break;
				} else {
					dateExist = false;
				}
			}
			if (dateExist == false) {
				dateSelection.add(dateTextField.getText().trim());
			}
		}
	}
	private void transferQuantityToStockTable() {
		//get all saved inventory details file and display their name and quantity in stock table
		stock.clear();
		Boolean nameExist = false;
		if (dateSelection.size() > 1) {
			inputFile(dateSelection.get(dateSelection.size() - 1) + "_autoSaved.ser");
			for (int i = 0; i < inventory.size(); ++i) {
				stock.add(new ItemStock(inventory.get(i).getName()));
			}
			inventory.clear();
			for (int i = 0; i < stock.size(); ++i) {
				for (int j = 1; j < dateSelection.size(); ++j) {
					inputFile(dateSelection.get(j) + "_autoSaved.ser");
					if (i == 0) {
						autoAddAColumnToTable(dateSelection.get(j));
					}
					for (int a = 0; a < inventory.size(); ++a) {
						if (stock.get(i).getName().equals(inventory.get(a).getName())) {
							stock.get(i).setQuantity(inventory.get(a).getQuantity());
							nameExist = true;
							break;
						} else {
							nameExist = false;
						}
					}
					if (nameExist == false) {
						stock.get(i).setQuantity(0);
					}
					inventory.clear();
				}
				Vector<Object> data = new Vector<Object>();
				data.add(i + 1);
				data.add(stock.get(i).getName());
		        for (int b = 0; b < stock.get(i).getQuantity().size(); ++b) {
		        	data.add(stock.get(i).getQuantity().get(b));
		        }
				stockTableModel.addRow(data);
			}
		}
	}
	
    //output and input file
    private void inputFile() {
    	//input inventory details file from Inventory.ser to inventory arrayList
    	File test = new File("Inventory.ser");
        if (!test.isFile() || !test.canRead()){
                inventory.add(new Item(false, "New", 0.0, 0, "00-00-0000", "null"));
        } else {
            try {
                ObjectInputStream infile = new ObjectInputStream (new FileInputStream("Inventory.ser"));
                inventory = (ArrayList<Item>)infile.readObject();
                infile.close();
            } catch (IOException ioe) {
            	JOptionPane.showMessageDialog(getFrame(), "Invalid file");
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }
    private void inputFile(String name) {
    	//input inventory details file from name passed in as argument to inventory arrayList
    	try {
            ObjectInputStream infile = new ObjectInputStream (new FileInputStream(name));
            inventory = (ArrayList<Item>)infile.readObject();
            infile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            JOptionPane.showMessageDialog(getFrame(), "Invalid file");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
    private void inputTextFile(String name) {
    	//input inventory details text file to inventory arrayList
    	inventory.clear();
    	try (BufferedReader reader = new BufferedReader(new FileReader(new File(name)));){
    		String line = reader.readLine().trim();
    		String[] columnName = line.split(",");
    		for (int i = 0; i < columnName.length; ++i) {
    			if (columnName[i].compareTo(tableColumns[i]) != 0) {
        			JOptionPane.showMessageDialog(getFrame(), "Invalid file");
        			return;
        		}
    		}
	    	try {	
    			while ((line = reader.readLine().trim()) != null) {
	    			String[] data = line.split(",");
	    			inventory.add(new Item(Boolean.parseBoolean(data[1]), data[2],
						Double.parseDouble(data[3]),
						Integer.parseInt(data[4]),
						data[5], data[6]));
	    		}
	    	} catch(NullPointerException npe) {}
	    	reader.close();
    	} catch (Exception ex){
    		JOptionPane.showMessageDialog(getFrame(), "Invalid file");
    		ex.printStackTrace();
    	}
    }
    private void outputFile() {
        //write to Inventory.ser
    	try {    
            ObjectOutputStream outfile = new ObjectOutputStream (new FileOutputStream("Inventory.ser"));
            outfile.writeObject(inventoryTemp);
            outfile.flush();
            outfile.close();
        } catch(IOException ioe) {
        	JOptionPane.showMessageDialog(getFrame(), "Invalid file");
            ioe.printStackTrace();
        }
    }
    private void outputFile(String name) {
        //write to name + .ser
    	try {    
            ObjectOutputStream outfile = new ObjectOutputStream (new FileOutputStream(name + ".ser"));
            outfile.writeObject(inventoryTemp);
            outfile.flush();
            outfile.close();
        } catch(IOException ioe) {
        	JOptionPane.showMessageDialog(getFrame(), "Invalid file");
            ioe.printStackTrace();
        }
    }
    private void outputTextFile(String name) {
    	//write to text file with user defined name 
    	try (PrintWriter writer = new PrintWriter(new FileWriter(new File(name + ".txt")))) {
    		StringJoiner sjoiner = new StringJoiner(",");
    		for (int i = 0; i < inventoryTable.getColumnCount(); ++i) {
    			sjoiner.add(inventoryTable.getColumnName(i));
    		}
    		writer.println(sjoiner.toString());
    		for (int i = 0; i < inventoryTable.getRowCount(); ++i ) {
    			sjoiner = new StringJoiner(",");
    			for (int j = 0; j < inventoryTable.getColumnCount(); ++j) {
    				Object temp = inventoryTable.getValueAt(i, j);
    				String data;
    				if (temp == null) {
    					data = "null";
    				} else {
    					data = temp.toString();
    				}
    				sjoiner.add(data);
    			}
    			writer.println(sjoiner.toString());
    		}
    		writer.flush();
    		writer.close();
    	} catch (IOException ioe) {
    		JOptionPane.showMessageDialog(getFrame(), "Invalid file");
    		ioe.printStackTrace();
    	}
    }
    private void outputDateFromDateSelection() {
    	//write to date.ser from dateSelection
    	try {
    		ObjectOutputStream outfile = new ObjectOutputStream (new FileOutputStream("date.ser"));
    		outfile.writeObject(dateSelection);
    		outfile.flush();
    		outfile.close();
    	} catch(IOException ioe ) {
    		JOptionPane.showMessageDialog(getFrame(), "Invalid file");
    		ioe.printStackTrace();
    	}
    }
    private void inputDateToDateSelection() {
    	//read date.ser and assign to dateSelection
    	File test = new File("date.ser");
        if (!test.isFile() || !test.canRead()){
        	dateSelection.add("[Select date]");
        } else {
	    	try {
	            ObjectInputStream infile = new ObjectInputStream (new FileInputStream("date.ser"));
	            dateSelection = (Vector<String>)infile.readObject();
	            infile.close();
	        } catch (IOException ioe) {
	        	JOptionPane.showMessageDialog(getFrame(), "Invalid file");
	            ioe.printStackTrace();
	        } catch (ClassNotFoundException cnfe) {
	            cnfe.printStackTrace();
	        }
        }
    }
    
    //inner class
    private class RowButtonListener implements ActionListener {
        //add new row to inventory details table
    	public void actionPerformed(ActionEvent ae) {
            addRowToTable();
        }
    }
    private class SaveListener implements ActionListener {
        //save file Inventory.ser + date_autoSaved.ser
    	public void actionPerformed(ActionEvent ae) {
        	if (dateSelection.size() != 1) {
        		try {
	            	retrieveDataFromTable();
                  outputDateFromDateSelection();
	            	outputFile();
	            	outputFile(dateTemp + "_autoSaved");
	            	//transfer the data from table to inventoryTemp and to inventory to prevent data loss after refreshFrame
	            	Iterator<Item> itr = inventoryTemp.iterator();
		    		inventory.clear();
		    		while (itr.hasNext()) {
		    			inventory.add((Item)itr.next());
		    		}
		        	inventoryTemp.clear();
		        	refreshFrame(" " + dateTemp);
	                JOptionPane.showMessageDialog(getFrame(), "File saved");
	            } catch (NullPointerException npe) {
	            	JOptionPane.showMessageDialog(getFrame(), "Please open a file");
	            }
        	} else {
        		JOptionPane.showMessageDialog(getFrame(), "Please enter the date");
        	}
        }
    }
    private class SaveAsListener implements ActionListener {
        //save as file (user defined name)(ser)
    	public void actionPerformed(ActionEvent ae) {
        	try {
            	retrieveDataFromTable();
            	JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save file");
                JOptionPane.showMessageDialog(getFrame(), "Please enter the file name without its file extension");
                int status = fileChooser.showSaveDialog(getFrame());
                if (status == JFileChooser.APPROVE_OPTION) {
                	String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                	outputFile(fileName);
                	JOptionPane.showMessageDialog(getFrame(), "File saved");
                }
                inventoryTemp.clear();
            } catch (NullPointerException npe) {
            	JOptionPane.showMessageDialog(getFrame(), "Please open a file");
            } 
        }
    }
    private class OpenListener implements ActionListener {
        //open file (ser)
    	public void actionPerformed(ActionEvent ae) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open ser file");
            if (fileChooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            	String fileName = fileChooser.getSelectedFile().getPath();
            	inputFile(fileName);
            	refreshFrame();
            }
        }
    }
    private class OpenLastSavedListener implements ActionListener {
        //open last saved file
    	public void actionPerformed(ActionEvent ae) {
        	inputFile();
        	refreshFrame();
        }
    }
    private class NewListener implements ActionListener {
        //create new file
    	public void actionPerformed(ActionEvent ae) {
        	//clear so that the data wont stack
        	inventory.clear();
        	inventory.add(new Item(false, "New", 0.0, 0, "00-00-0000", "null"));
        	refreshFrame();
        }
    }
    private class ExportTextListener implements ActionListener {
    	//export text file
    	public void actionPerformed(ActionEvent ae) {
    		try {
    			Object test = inventoryTable.getValueAt(0, 0);
            	JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save file");
                JOptionPane.showMessageDialog(getFrame(), "Please enter the file name without its file extension");
                int status = fileChooser.showSaveDialog(getFrame());
                if (status == JFileChooser.APPROVE_OPTION) {
                	String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                	outputTextFile(fileName);
                	JOptionPane.showMessageDialog(getFrame(), "File saved");
                }
                inventoryTemp.clear();
            } catch(NullPointerException npe) {
            	JOptionPane.showMessageDialog(getFrame(), "Please open a file");
            }
    	}
    }
    private class ImportTextListener implements ActionListener {
    	//import text file
    	public void actionPerformed(ActionEvent ae) {
    		JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open text file");
            if (fileChooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            	String fileName = fileChooser.getSelectedFile().getPath();
            	inputTextFile(fileName);
            	refreshFrame();
            }
    	}
    }
    private class DeleteButtonListener implements ActionListener {
    	//delete selected row in inventory details table
    	public void actionPerformed(ActionEvent ae) {
    		int count = 0;
    		int[] temp = new int[inventoryTable.getRowCount()];
			for (int i = 0; i < inventoryTable.getRowCount(); ++i) {
	    		if ((Boolean)inventoryTable.getValueAt(i, checkBoxColumnIndex).equals(true)) {
					temp[count] = i;
					count++;
				}
			}
			for (int i = 0; i < count; ++i) {
				inventoryTable.removeRow(temp[i]);
				for (int j = i + 1; j < count; ++j) {
					temp[j] = temp[j] - 1;
				}
			}
    	}
    }
    private class DisplayLowStockListener implements ActionListener {
    	//display item with low stock, by default it is set to 3
    	public void actionPerformed(ActionEvent ae) {
    		searchTextField.setText(null);
    		tRSorter.setRowFilter(RowFilter.numberFilter(ComparisonType.BEFORE, lowStockQuantity, 4));
    	}
    }
    private class DisplayAllListener implements ActionListener {
    	//return searchTextField to null and display all data in inventory details table 
    	public void actionPerformed(ActionEvent ae) {
    		searchTextField.setText(null);
    		tRSorter.setRowFilter(null);
    	}
    }
    private class selectDateCBListener implements ActionListener {
    	public void actionPerformed(ActionEvent ae) {
	    	//select a date in dateCB to display its table
    		if (!((String)dateCB.getSelectedItem()).equals("[Select date]")) {	
    			inputFile((String)dateCB.getSelectedItem() + "_autoSaved.ser");
    			dateTemp = (String)dateCB.getSelectedItem();
	    		refreshFrame(" " + dateTemp);
	    	}
    	}
    }
    private class DateTextFieldMouseClickedListener extends MouseAdapter {
    	//click the dateTextField to set to ""
    	public void mouseClicked(MouseEvent me) {
        	dateTextField.setText("");
        }
    }
    private class SaveDateListener implements ActionListener {
    	public void actionPerformed(ActionEvent ae) {
	    	//save date entered in dateTextField to date.ser and auto save the table
    		try {
    			new SimpleDateFormat("dd-mm-yyyy").parse(dateTextField.getText().trim());
	    		if (!dateTextField.getText().trim().equals("[Enter date]")) {	
	    			saveDateTextFieldtoDateSelection();
		    		retrieveDataFromTable();
		    		outputFile(dateSelection.get(dateSelection.size() - 1) + "_autoSaved");
		    		outputDateFromDateSelection();
		    		//transfer the data from table to inventoryTemp and to inventory to prevent data loss after refreshFrame
		    		Iterator<Item> itr = inventoryTemp.iterator();
		    		inventory.clear();
		    		while (itr.hasNext()) {
		    			inventory.add((Item)itr.next());
		    		}
		        	inventoryTemp.clear();
		        	//display the date in the tab
		        	refreshFrame(" " + dateTextField.getText());
		    	} else {
		    		JOptionPane.showMessageDialog(getFrame(), "Please enter the date");
		    	}
	    	} catch(ParseException pe) {
		    	JOptionPane.showMessageDialog(getFrame(), "Invalid date format");
    		}
    	}
    }
	private class RowRenderer extends JLabel implements TableCellRenderer {
        //make sure the first column (id) is treated as label and does not rearrange
		public Component getTableCellRendererComponent(JTable aTable, Object color,
            boolean isSelected, boolean hasFocus, int row, int column) {
        	setFont(table.getFont());
            setText(Integer.toString(row + 1));
            return this;
        }
    }
    private class searchFilterListener implements DocumentListener {
    	//search data in inventory details table
    	@Override
    	public void insertUpdate(DocumentEvent de) {
    		String text = searchTextField.getText();
    		if (text.trim().length() == 0) {
    			tRSorter.setRowFilter(null);
    		} else {
    			tRSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    		}
    	}
    	@Override
    	public void removeUpdate(DocumentEvent de) {
    		String text = searchTextField.getText();
    		if (text.trim().length() == 0) {
    			tRSorter.setRowFilter(null);
    		} else {
    			tRSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    		}
    	}
    	@Override
    	public void changedUpdate(DocumentEvent de) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    private class CheckBoxSelectionAction extends AbstractAction {
    	//set check box value by passing in boolean value, if false then uncheck, if true then check
    	private boolean value;
    	public CheckBoxSelectionAction(String name, boolean value) {
    		super(name);
    		this.value = value;
    	}
    	public void actionPerformed(ActionEvent ae) {
    		for (int i = 0; i < inventoryTable.getRowCount(); ++i) {
    			selectionModel = (DefaultListSelectionModel) table.getSelectionModel();
    			if (selectionModel.isSelectedIndex(i)) {
    				inventoryTable.setValueAt(value, i, checkBoxColumnIndex);
    			}
    		}
    	}
    }
}