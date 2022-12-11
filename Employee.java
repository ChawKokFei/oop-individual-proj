import java.io.*;

public class Employee implements Serializable {
    //instance variable
	private String name, jobTitle, phoneNumber, userId, userPassword;
    public static final long serialVersionUID = 3L;
    
    //constructor
    public Employee() {
    	setName("New");
        setJobTitle("null");
        setPhoneNumber("null");
        setUserId("null");
        setUserPassword("null");
    }
    public Employee(String name, String jobTitle, String phoneNumber, String userId, String userPassword) {
        setName(name);
        setJobTitle(jobTitle);
        setPhoneNumber(phoneNumber);
        setUserId(userId);
        setUserPassword(userPassword);
    }
    
    //setter
    public void setName(String name) {
        this.name = name;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    
    //getter
    public String getName() {
        return name;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserPassword() {
        return userPassword;
    }
}
