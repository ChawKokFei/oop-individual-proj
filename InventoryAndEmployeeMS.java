import javax.swing.SwingUtilities;

//this is the main class to run the inventory management system
public class InventoryAndEmployeeMS {
    public static void main (String [] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI("Login menu");
            }
        });
    }
}
