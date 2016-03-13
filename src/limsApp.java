import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

	/*
	Program goals:
	interpret and edit an inventory file, 
		product name, product SKU, quantity on hand
	display the file's information in a table, 
	add or remove a quantity of product
	add a new product type to the file
		password protected
		
	Three panes:
		Status displays table of info
		Quantity allows to increase/decrease order quantity
		Product allows new product to be added w/ 0 quantity
			
	*/


public class limsApp {
	
	char[] password = {'a', 'b', 'c', '1', '2', '3', '!'};
	ArrayList<Object[]> dataList;
	Object dataArray[][];
	JTable jtbStatusTable;
	String[] headings = {"SKU", "Name", "Quantity"};
	
	limsApp() {
			// Model - Other variable declarations and initializations
		dataList = new ArrayList<Object[]>();
		
			// Populate data with information from lims.data
			// Every line is an entry - this allows for new information
			// to be added quickly by the addition of new cases.
		try (FileInputStream fin = new FileInputStream("lims.data"))
		{
			int inputChar;
			String thisWord = "";
			int indexCounter = 0;
			
			String entrySKU = "";
			String entryName = "";
			int entryQuantity = 0;
			
			do {
				inputChar = fin.read();
				
				switch (inputChar) {
				
				case ' ':
				case '\r':
				case '\n':
				case -1:
					switch (indexCounter) {
					case 0:
						entrySKU = thisWord;
						break;
						
					case 1:
						entryName = thisWord;
						break;
						
					case 2:
						entryQuantity = Integer.parseInt(thisWord);
						break;
						
					default:
						break;
					}

					indexCounter++;
					
					if ((inputChar == '\n') || (inputChar == -1)) {

						Object[] dataElement = {entrySKU, entryName, entryQuantity};
						dataList.add(dataElement);
						
						indexCounter = 0;
					}
					thisWord = "";
					break;
					
				case '_':
					thisWord += ' ';
					break;
					
				default:
					thisWord += (char) inputChar;
				}
			} while (inputChar != -1);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("I/O Error");
		}

		dataArray = new Object[dataList.size()][3];
		updateDataArray();
		
			// View - GUI initialization
		
			// Frame jfWindow serves as a top-level container
		JFrame jfWindow = new JFrame("Loux Inventory Management System");
		jfWindow.setSize(500, 500);
		jfWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
			// jtpMainPane holds all other components
		JTabbedPane jtpMainPane = new JTabbedPane(JTabbedPane.TOP, 
		JTabbedPane.SCROLL_TAB_LAYOUT); 
		
			// The status and product tabs have their own panels
		JPanel jpnStatus = new JPanel();
		
			// The status panel has a scrollable table showing ware info
		jtbStatusTable = new JTable(dataArray,headings);
		JScrollPane jspStatusTablePane = new JScrollPane(jtbStatusTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
			// The quantity panel allows a quantity to be changed
		JPanel jpnQuantity = new JPanel();
		
			// The quantity search panel allows for input to find a ware
		JPanel jpnQuantitySearch = new JPanel();
		JTextField jtfQuantitySearch = new JTextField();
		JButton jbtQuantitySearch = new JButton("Search SKU");
		JLabel jlbQuantityName = new JLabel("Name:");
		JLabel jlbQuantitySKU = new JLabel("SKU:");
		JLabel jlbQuantityQuantity = new JLabel("On Hand:");
		JTextField jtfQuantityName = new JTextField(4);
		JTextField jtfQuantitySKU = new JTextField(4);
		JTextField jtfQuantityQuantity = new JTextField(4);
		
			// The quantity change panel allows for inventory levels to
			// be modified
		JPanel jpnQuantityChange = new JPanel();
		JTextField jtfQuantityChangeAmount = new JTextField(6);
		JButton jbtQuantityAdd = new JButton("Add");
		JButton jbtQuantitySubtract = new JButton("Subtract");
		
			// The product add panel allows a new product to be
			// added to the database
		JPanel jpnProductAdd = new JPanel();
		JLabel jlbProductName = new JLabel("Name:");
		JLabel jlbProductSKU = new JLabel("SKU:");
		JLabel jlbProductPassword = new JLabel("Password:");
		JTextField jtfProductName = new JTextField(4);
		JTextField jtfProductSKU = new JTextField(4);
		JPasswordField jpfProductAddPassword = new JPasswordField(4);
		JButton jbtProductAdd = new JButton("Add");

			// The search panel allows a product to be found and deleted
		JPanel jpnProductDelete = new JPanel();
		JTextField jtfProductSearch = new JTextField();
		JButton jbtProductSearch = new JButton("Search SKU");
		JLabel jlbProductSearchName = new JLabel("Name:");
		JLabel jlbProductSearchSKU = new JLabel("SKU:");
		JLabel jlbProductSearchQuantity = new JLabel("Quantity:");
		JTextField jtfProductSearchName = new JTextField(4);
		JTextField jtfProductSearchSKU = new JTextField(4);
		JTextField jtfProductSearchQuantity = new JTextField(4);
		JLabel jlbProductDeletePassword = new JLabel("Password:");
		JPasswordField jpfProductDeletePassword = new JPasswordField(4);
		JButton jbtProductDelete = new JButton("Delete Product");
		
			// Format panel layouts
		jpnQuantitySearch.setLayout(new GridLayout(4,2,8,8));
		jpnQuantityChange.setLayout(new GridLayout(3,1,8,8));
		jpnQuantity.setLayout(new GridLayout(2,2,8,8));
		
			// jpnProductAdd and jpnProductDelete use BoxLayout
			// Each pair of components gets its own box
		
		Box boxAddResultName = Box.createHorizontalBox();
		boxAddResultName.add(jlbProductName);
		boxAddResultName.add(Box.createRigidArea(new Dimension(6,0)));
		boxAddResultName.add(jtfProductName);

		Box boxAddSearchSKU = Box.createHorizontalBox();
		boxAddSearchSKU.add(jlbProductSKU);
		boxAddSearchSKU.add(Box.createRigidArea(new Dimension(6,0)));
		boxAddSearchSKU.add(jtfProductSKU);

		Box boxAddResultPassword = Box.createHorizontalBox();
		boxAddResultPassword.add(jlbProductPassword);
		boxAddResultPassword.add(Box.createRigidArea(new Dimension(6,0)));
		boxAddResultPassword.add(jpfProductAddPassword);

		Box boxProductSearch = Box.createHorizontalBox();
		boxProductSearch.add(jtfProductSearch);
		boxProductSearch.add(Box.createRigidArea(new Dimension(6,0)));
		boxProductSearch.add(jbtProductSearch);

		Box boxProductSearchName = Box.createHorizontalBox();
		boxProductSearchName.add(jlbProductSearchName);
		boxProductSearchName.add(Box.createRigidArea(new Dimension(6,0)));
		boxProductSearchName.add(jtfProductSearchName);

		Box boxProductSearchSKU = Box.createHorizontalBox();
		boxProductSearchSKU.add(jlbProductSearchSKU);
		boxProductSearchSKU.add(Box.createRigidArea(new Dimension(6,0)));
		boxProductSearchSKU.add(jtfProductSearchSKU);

		Box boxProductSearchQuantity = Box.createHorizontalBox();
		boxProductSearchQuantity.add(jlbProductSearchQuantity);
		boxProductSearchQuantity.add(Box.createRigidArea(new Dimension(6,0)));
		boxProductSearchQuantity.add(jtfProductSearchQuantity);

		Box boxProductDeletePassword = Box.createHorizontalBox();
		boxProductDeletePassword.add(jlbProductDeletePassword);
		boxProductDeletePassword.add(Box.createRigidArea(new Dimension(6,0)));
		boxProductDeletePassword.add(jpfProductDeletePassword);
		
			// These pairs of components are put into more boxes
		Box boxProductAdd = Box.createVerticalBox();
		boxProductAdd.add(boxAddResultName);
		boxProductAdd.add(boxAddSearchSKU);
		boxProductAdd.add(boxAddResultPassword);
		boxProductAdd.add(jbtProductAdd);
		
		Box boxProductDelete = Box.createVerticalBox();
		boxProductDelete.add(boxProductSearch);
		boxProductDelete.add(boxProductSearchName);
		boxProductDelete.add(boxProductSearchSKU);
		boxProductDelete.add(boxProductSearchQuantity);
		boxProductDelete.add(boxProductDeletePassword);
		boxProductDelete.add(jbtProductDelete);
		
			// Create borders
		jpnQuantitySearch.setBorder(BorderFactory.createEtchedBorder());
		jpnQuantityChange.setBorder(BorderFactory.createEtchedBorder());
		jpnProductAdd.setBorder(BorderFactory.createEtchedBorder());
		jpnProductDelete.setBorder(BorderFactory.createEtchedBorder());
		
			// Make result text fields read-only
		jtfQuantityName.setEditable(false);
		jtfQuantitySKU.setEditable(false);
		jtfQuantityQuantity.setEditable(false);
		jtfProductSearchName.setEditable(false);
		jtfProductSearchSKU.setEditable(false);
		jtfProductSearchQuantity.setEditable(false);
		
			// Add components to each other
		jfWindow.add(jtpMainPane);

		jpnStatus.add(jspStatusTablePane);
		jtpMainPane.addTab("Status", jpnStatus);

		jpnQuantitySearch.add(jtfQuantitySearch);
		jpnQuantitySearch.add(jbtQuantitySearch);
		jpnQuantitySearch.add(jlbQuantityName);
		jpnQuantitySearch.add(jtfQuantityName);
		jpnQuantitySearch.add(jlbQuantitySKU);
		jpnQuantitySearch.add(jtfQuantitySKU);
		jpnQuantitySearch.add(jlbQuantityQuantity);
		jpnQuantitySearch.add(jtfQuantityQuantity);
		jpnQuantity.add(jpnQuantitySearch);
		
		jpnProductAdd.add(boxProductAdd);
		jpnProductDelete.add(boxProductDelete);
		
		jpnQuantityChange.add(jtfQuantityChangeAmount);
		jpnQuantityChange.add(jbtQuantityAdd);
		jpnQuantityChange.add(jbtQuantitySubtract);
		jpnQuantity.add(jpnQuantityChange);
		jpnQuantity.add(jpnProductAdd);
		
		jpnQuantity.add(jpnProductDelete);
		
		jtpMainPane.addTab("Quantity", jpnQuantity);
		
			// Show window
		jfWindow.setVisible(true);
		
			// Controller responds to user input
		
			// Search for item by SKU
		jbtQuantitySearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemSKU = jtfQuantitySearch.getText();
				int index;
				
				for (index = 0; index < dataList.size(); index++) {
					Object[] dataElement = dataList.get(index);
					
					if (((String) dataElement[0]).equals(itemSKU)) {
						jtfQuantitySKU.setText((String) dataElement[0]);
						jtfQuantityName.setText((String) dataElement[1]);
						jtfQuantityQuantity.setText( Integer.toString((int) dataElement[2]) );
						index = dataList.size();
					}
				}
			}
		});
		
			// Add quantity to selected item
		jbtQuantityAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemSKU = jtfQuantitySearch.getText();
				int index;
				
				for (index = 0; index < dataList.size(); index++) {
					Object[] dataElement = dataList.get(index);
					
					if (((String) dataElement[0]).equals(itemSKU)) {
						dataElement[2] = (int) dataElement[2] +
						    Integer.parseInt(jtfQuantityChangeAmount.getText());
						updateDataArray();

						jspStatusTablePane.remove(jtbStatusTable);
						
						jtbStatusTable.setValueAt((int) dataElement[2], index, 2);

						index = dataList.size() + 1;
						updateFile();
					}
				}
				
				if (index == dataList.size()) {
					jtfQuantitySearch.setText("Not Found");
				}
			}
		});
		
			// Subtract quantity from selected item
		jbtQuantitySubtract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemSKU = jtfQuantitySearch.getText();
				int index;
				
				for (index = 0; index < dataList.size(); index++) {
					Object[] dataElement = dataList.get(index);
					
					if (((String) dataElement[0]).equals(itemSKU)) {
						dataElement[2] = (int) dataElement[2] -
						    Integer.parseInt(jtfQuantityChangeAmount.getText());
						updateDataArray();

						jspStatusTablePane.remove(jtbStatusTable);
						
						jtbStatusTable.setValueAt((int) dataElement[2], index, 2);

						index = dataList.size() + 1;
						updateFile();
					}
				}
				
				if (index == dataList.size()) {
					jtfQuantitySearch.setText("Not Found");
				}
			}
		});
		
			// Allows creation of new products in database
		jbtProductAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					// Validate password
				if (Arrays.equals(password, jpfProductAddPassword.getPassword())) {
					
					String newSKU = jtfProductSKU.getText();
					String newName = jtfProductName.getText();
					int newQuantity = 0;

					Object[] newElement = {newSKU,
							newName, 
							newQuantity};
					
					DefaultTableModel statusModel = (DefaultTableModel) jtbStatusTable.getModel();
					statusModel.addRow(newElement);
					dataList.add(newElement);
					updateFile();
				}
			}
		});
		
			// Another search button to help confirm product
		jbtProductSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemSKU = jtfProductSearch.getText();
				int index;
				
				for (index = 0; index < dataList.size(); index++) {
					Object[] dataElement = dataList.get(index);
					
					if (((String) dataElement[0]).equals(itemSKU)) {
						jtfProductSearchSKU.setText((String) dataElement[0]);
						jtfProductSearchName.setText((String) dataElement[1]);
						jtfProductSearchQuantity.setText( Integer.toString((int) dataElement[2]) );
						index = dataList.size();
					}
				}
			}
		});
		
			// Allows products to be deleted
		jbtProductDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					// Validate password
				if (Arrays.equals(password, jpfProductDeletePassword.getPassword())) {
					
					String itemSKU = jtfProductSearch.getText();
					int index;
					
					for (index = 0; index < dataList.size(); index++) {
						Object[] dataElement = dataList.get(index);
						
						if (((String) dataElement[0]).equals(itemSKU)) {

							DefaultTableModel statusModel = (DefaultTableModel) jtbStatusTable.getModel();
							statusModel.removeRow(index);
							dataList.remove(index);
							index = dataList.size()+1;
							updateFile();
						}
					}
				}
			}
		});
	}
	
	private void updateDataArray() {
		
		dataArray = new Object[dataList.size()][3];
		
			// Convert dataList to dataArray
		for (int i = 0; i < dataList.size(); i++) {
			Object[] dataElement = dataList.get(i);
	
			dataArray[i][0] = dataElement[0];
			dataArray[i][1] = dataElement[1];
			dataArray[i][2] = dataElement[2];
		}
	}
	
	private void updateFile() {
		try (FileOutputStream fout = new FileOutputStream("lims.data")) {
			
			for (int i=0; i < dataList.size(); i++) {
				Object[] element = dataList.get(i);
				
				String name = (String) element[0];
				String sku = (String) element[1];
				String quantity = (Integer.toString((int) element[2]));
				
					// Interpret word by word
				for (int j = 0; j < name.length(); j++) {
					fout.write(name.charAt(j));
				}
				
				fout.write(' ');

				for (int j = 0; j < sku.length(); j++) {
					if (sku.charAt(j) != ' ')
						fout.write(sku.charAt(j));
					else
						fout.write('_');
				}

				fout.write(' ');

				for (int j = 0; j < quantity.length(); j++) {
					fout.write(quantity.charAt(j));
				}

				if (i + 1 != dataList.size()) fout.write('\n');
				
			}
			
		} catch (IOException e) {
			System.out.println("IO error");
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new limsApp();
			}
		});
	}
}