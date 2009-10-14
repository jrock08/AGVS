package agvs;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * Edited by Kayleigh O'Connor
 *
 */

public class TestButton extends JButton{
	public static final int START_BUTTON = 0;

	public static final int FAKE_BUTTON = 1;

	public static final int TARGET_BUTTON = 2;
	
	/**
	 * The type of button this is, either: START_BUTTON, FAKE_BUTTON, or
	 * TARGET_BUTTON
	 */
	private int type;
	
	public TestButton(){
		
		setPreferredSize( new Dimension( 54, 33 ));
	}
	/**
	 * Create a text button
	 * 
	 * @param text
	 *            Text displayed on button
	 */
	public TestButton(String text, Color c) {
		super(text);
		setBackground(c);
		setPreferredSize(new Dimension(75, 45));
	}

	
	/**
	 * Create a button with an image
	 * 
	 * @param icon
	 *            Icon to display on button
	 */
	public TestButton(Icon icon, int resX, int resY) {
		super(icon);
		int x, y;
		x = (resX/31) + 5;
		y = (resY/31) - 3;
		setOpaque(false);
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setPreferredSize(new Dimension(x, y)); //50, 30 for 1280 x 1024
	}
	
	/**
	 * 
	 * @return type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * sets type
	 * @param pType
	 */
	public void setType( int pType ){
		type = pType;
	}
}
