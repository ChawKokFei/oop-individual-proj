import java.util.*;
import java.io.*;

//ItemStock to store the stock details for each and every item
public class ItemStock implements Serializable {
	//instance variable
	public static final long serialVersionUID = 4L;
	private String name;
	private Vector<Integer> quantity = new Vector<Integer>();
	
	//constructor
	public ItemStock() {
		this.name = "";
	}
	public ItemStock(String name) {
		this.name = name;
	}
	
	//getter
	public String getName() {
		return name;
	}
	public Vector<Integer> getQuantity() {
		return quantity;
	}
	
	//setter
	public void setName(String name) {
		this.name = name;
	}
	public void setQuantity(int quantity) {
		this.quantity.add(quantity);
	}
}
