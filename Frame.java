import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;

public abstract class Frame {
	//instance variable
	private JFrame frame;
	private Dimension dimension;
	private int locationOffset;
	
	//constructor
	public Frame() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public Frame(String name) {
		JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//getter
	public JFrame getFrame() {
		return frame;
	}
	
	//public method
	public void applyFrameLocationOffset(int num) {
		dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocationOffset(num);
		frame.setLocation(dimension.width/2 - (locationOffset + frame.getSize().width/2), dimension.height/2 - (locationOffset + frame.getSize().height/2));
	}
	
	//private method
	private void setLocationOffset(int num) {
		locationOffset = num;
	}
}
