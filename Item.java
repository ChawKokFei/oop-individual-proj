import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.*;

//Item to store the item details for each and every item
public class Item implements Serializable {
    //instance variable
	private String name, expiryDate, supplier;
    private double price;
    private int quantity;
    private boolean wrongFormat, selected;
    public static final long serialVersionUID = 2L;
    
    //constructor
    public Item() {
    	setSelected(false);
    	setName("New");
        setExpiryDate("00-00-0000");
        setSupplier("null");
        setPrice(0.0);
        setQuantity(0);
    }
    public Item(Boolean selected, String name, double price, int quantity, String expiryDate, String supplier) {
    	setSelected(selected);
    	setName(name);
        setExpiryDate(expiryDate);
        setSupplier(supplier);
        setPrice(price);
        setQuantity(quantity);
    }
    
    //setter
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    public void setName(String name) {
        this.name = name;
    }
	@SuppressWarnings("finally")
	public boolean setExpiryDate(String expiryDate){
        try {
            new SimpleDateFormat("dd-mm-yyyy").parse(expiryDate); 
            wrongFormat = false;
        } catch(ParseException ex) {
            wrongFormat = true;
        } finally {
	        this.expiryDate = expiryDate;
	        return wrongFormat;
        }
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    //getter
    public Boolean getSelected() {
        return selected;
    }
    public String getName() {
        return name;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public String getSupplier() {
        return supplier;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
}
