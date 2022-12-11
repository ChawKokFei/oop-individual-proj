import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class LoginGUI extends Frame {
    //instance variable
	private ArrayList<Employee> employee = new ArrayList<Employee>();
    private String tempId, tempPassword;
    
    private JPanel panel_1, panel_2, cMPanel;
    private JLabel titleLabel, usernameLabel, passwordLabel;
    private JTextField username;
    private JPasswordField password;
    private JButton loginButton, inventoryButton;
    private GridBagConstraints gbc;
    private CardLayout cl;
    
    private static Font font1 = new Font("Calibri", Font.PLAIN, 15);
    private static Border line = BorderFactory.createLineBorder(Color.black, 2);
    
    //GUI methods
    public LoginGUI(String name) {
        super(name);
    	displayGUI();
    }
    public void displayGUI() {
    	getFrame().setLayout(new GridLayout(0, 1));
    	getFrame().setSize(500, 500);
    	getFrame().setMinimumSize(new Dimension(400, 250));
    	applyFrameLocationOffset(0);
        
        buildLoginPanel();
        
        getFrame().getRootPane().setDefaultButton(loginButton);
        getFrame().add(cMPanel);
        getFrame().pack();
        getFrame().setVisible(true);
    }
    private void buildLoginPanel() {
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 0.5;
        gbc.weighty = 8;
        cl = new CardLayout();
        
        //create panel
        cMPanel = new JPanel(cl);
        panel_1 = new JPanel();
        panel_1.setLayout(new GridBagLayout());
        panel_1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel_2 = new JPanel(new GridLayout(1, 0, 5, 5));
        panel_2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //create label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        titleLabel = new JLabel("   Inventory Management System Login   ");
        titleLabel.setBorder(BorderFactory.createCompoundBorder(line, BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        titleLabel.setFont(font1);
        panel_1.add(titleLabel, gbc);
        
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        usernameLabel = new JLabel("Enter your username:");
        panel_1.add(usernameLabel, gbc);
        
        gbc.gridy = 2;
        passwordLabel = new JLabel("Enter your password:");
        panel_1.add(passwordLabel, gbc);
        
        //create text field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        username = new JTextField(20);
        panel_1.add(username, gbc);
        
        gbc.gridy = 2;
        password = new JPasswordField(20);
        panel_1.add(password, gbc);
        
        //create button
        gbc.weighty = 8;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.PAGE_START;
        loginButton = new JButton("Login");
        loginButton.addActionListener(new loginButtonListener());
        panel_1.add(loginButton, gbc);
        
        inventoryButton = new JButton("Inventory system");
        inventoryButton.setFont(new Font("Calibri", Font.BOLD, 25));
        inventoryButton.addActionListener(new inventoryButtonListener());
        panel_2.add(inventoryButton);
        
        cMPanel.add(panel_1, "loginPanel");
        cMPanel.add(panel_2, "choosePanel");
        cl.show(cMPanel, "loginPanel");
    }
    
    //input file
    private void inputFile() {
        File test = new File("Employee Details.ser");
        if (!test.isFile() || !test.canRead()){
            employee.add(new Employee("admin", "-", "-", "adminid", "adminpassword"));
        } else {
            try {
                ObjectInputStream infile = new ObjectInputStream (new FileInputStream("Employee Details.ser"));
                employee = (ArrayList<Employee>) infile.readObject();
                infile.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }
    
    //inner class
    private class inventoryButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
        	getFrame().dispose();
            new InventoryMS("Inventory Menu");	
        }
    }
    private class loginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            inputFile();
            tempId = username.getText();
            tempPassword = password.getText();
            for (int i = 0; i < employee.size(); ++i) {
                if (employee.get(0).getUserId().equals(tempId.trim()) && employee.get(0).getUserPassword().equals(tempPassword)) {
                	cl.show(cMPanel, "choosePanel");
                    break;
                } else if (employee.get(i).getUserId().equals(tempId.trim()) && employee.get(i).getUserPassword().equals(tempPassword)) {
                	cl.show(cMPanel, "choosePanel");
                    break;
                } else {
                    JOptionPane.showMessageDialog(getFrame(), "Invalid ID or password.");
                    break;
                }
            }
        }
    }
}	
