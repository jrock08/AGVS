package agvs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.border.*;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.lang.Object.*;
import java.awt.Font.*;
import java.util.Vector;
import java.io.*;
import java.util.*;
import agvs.audio.*;

/**
 * 
 * TestWindow class
 * Generates a TestWindow for a trial.
 * 
 * @author Kayleigh
 *
 */

public class TestWindow implements MouseListener {
	/** f - Creates a window for the trial */
	private JWindow f = new JWindow(); //create Frame
	/** map, fakebutton1, fakebutton2, target - Image Icons for the background and three types of buttons used in each trial */
	private ImageIcon map;
	private ImageIcon fakebutton1;
	private ImageIcon fakebutton2;
	private ImageIcon target;
	/** startButton - Button for starting the trial */
	private TestButton startButton;
	/** startPanel, searchPanel - Two panels that can be switched out, one for the start button screen and one for the trial window */
	private JPanel startPanel;
	private JPanel searchPanel;
	/** buttons - Vector of Target and Fake Target buttons*/
	private Vector<TestButton> buttons;
	/** htargetPositions - Vector containing all possible x coordinates for the targets */
	private Vector<Integer> htargetPositions;
	/** vtargetPositions - Vector containing all possible y coordinates for the targets */
	private Vector<Integer> vtargetPositions;
	
	/** usedXPositions, usedYPositions - positions already used for the target in the trial */
	private Vector<Integer> usedXPositions = new Vector<Integer>();
	private Vector<Integer> usedYPositions = new Vector<Integer>();
	
	/** numberTrials - number of trials to run */
	private int numberTrials;
	/** countTrials - count the trials performed so far*/
	private int countTrials;
	/** starttime, endtime - keeps track of the start time and end time for each trial, used for calculating the elapsed time */
	private Date starttime;
	private Date endtime;
	/** dataFile - FileOutputStream for the participant data file */
	private FileOutputStream dataFile;
	/** pID - participant ID */
	private int pID;
	/** hcond, vcond - horizontal and vertical cue */
	private Condition hcond;
	private Condition vcond;
	
	/** resX, resY - X and Y resolution for the window */
	private int resX; //= 1024; //1280
	private int resY; //= 768; //1024
	
	/** file - participant data file */
	private String file;
	
	/** invert - 0 to play horizontal first, 1 to play vertical first */
	private int invert;
	
	/**
	 * Constructor for the TestWindow
	 * Initializes various GUI components such as the searchPanel and the startPanel
	 * Creates a data file with the participant ID in the data directory- (pID)data.txt
	 * 
	 * @param numTrials - number of trials
	 * @param ParticipantID - participant ID number
	 * @param hcondition - horizontal cue
	 * @param vcondition - vertical cue
	 */
	public TestWindow( int numTrials, int ParticipantID, Condition hcondition, Condition vcondition, int inv, int noise, int x, int y ){
		//System.out.println(hcondition.getDesc() + " " + vcondition.getDesc());
		pID = ParticipantID;
		hcond = hcondition;
		vcond = vcondition;
		invert = inv;
		resX = x;
		resY = y;
		countTrials = 0;
		file = "data/" + Integer.toString(pID) + "data.txt";
		htargetPositions = new Vector<Integer>();
		vtargetPositions = new Vector<Integer>();
		buttons = new Vector<TestButton>();
		
		printConditionHeader( file );

		//Initialize valid target positions
		for( int i = 0; i < 31; i++)
			usedXPositions.addElement(new Integer(i*(resX/31)));
		for( int i = 0; i < 31; i++)
			usedYPositions.addElement(new Integer(i*(resY/31)));
		
		numberTrials = numTrials;
		java.net.URL imgURL = getClass().getResource("images/map.jpg");
		java.net.URL fake1 = getClass().getResource("images/fakeimage1.gif");
		java.net.URL fake2 = getClass().getResource("images/fakeimage2.gif");
		java.net.URL tar = getClass().getResource("images/targetimage.gif");
		
		if( imgURL != null ){
			map = new ImageIcon(imgURL);
		}
		else{
			System.out.println("background image not found!");
			System.exit(1);
		}
		if( fake1 != null ){
			fakebutton1 = new ImageIcon(fake1);
		}
		else{
			System.out.println("fakeimage1 not found!");
			System.exit(1);
		}
		if( fake2 != null ){
			fakebutton2 = new ImageIcon(fake2);
		}
		else{
			System.out.println("fakeimage2 not found!");
			System.exit(1);
		}
		if( tar != null ){
			target = new ImageIcon(tar);
		}
		else{
			System.out.println("targetimage not found!");
			System.exit(1);
		}
		
		// Initialize startPanel
		startPanel = new JPanel( new GridBagLayout()) {
			public void paintComponent(Graphics g) {
				// Scale map image to size of component
				Dimension d = getSize();
				g.drawImage(map.getImage(), 0, 0, d.width, d.height, null);

				setOpaque(false);
				super.paintComponent(g);
			}
		};

		startPanel.setPreferredSize(new Dimension(resX, resY));
		// Initialize Start button and set properties
		startButton = new TestButton("Start", Color.RED);
		//startButton.setBackground(Color.RED);
		startButton.setType(TestButton.START_BUTTON);
		startButton.setPreferredSize(new Dimension(75, 45));
		startButton.addMouseListener(this);

		startPanel.add(startButton);

		// Initialize Search Panel
		searchPanel = new JPanel( ) {//new GridLayout(31, 31)) {
			public void paintComponent(Graphics g) {
				// Scale map image to size of component
				Dimension d = getSize();
				g.drawImage(map.getImage(), 0, 0, d.width, d.height, null);

				setOpaque(false);
				super.paintComponent(g);
			}
		};
		searchPanel.setLayout(null);
		searchPanel.setPreferredSize(new Dimension(resX, resY));
		// Add start panel to the window
		f.getContentPane().add(startPanel);
		
	}
	
	/**
	 * Prints the trial data to the participant data file in an Excel friendly format
	 * Comma is used as the delimiter
	 * 
	 * @param outfile - participant data file
	 * @param clickedbutton - TestButton clicked by the participant
	 * @param targetbutton - target button
	 * @param hit - true if target was selected, false if not
	 */
	public void printTrialData(String outfile, TestButton clickedbutton, TestButton targetbutton, boolean hit){
		try {
			dataFile = new FileOutputStream( outfile, true);
		}catch(Exception x){
			System.out.println("Error opening datafile: " + Integer.toString(pID) + "data.txt");
		}
		
		long start = starttime.getTime();
		long end = endtime.getTime();
		
		long timeelapsed = end - start;
		
		PrintStream out = new PrintStream(dataFile);
		if(!hit)
			out.println( countTrials + ", " + targetbutton.getX() + ", " + targetbutton.getY() + ", " + clickedbutton.getX() + ", " + clickedbutton.getY() + ", " + starttime.getTime() + ", " + endtime.getTime() + ", " + timeelapsed + ", MISS!");
		else
			out.println( countTrials + ", " + targetbutton.getX() + ", " + targetbutton.getY() + ", " + clickedbutton.getX() + ", " + clickedbutton.getY() + ", " + starttime.getTime() + ", " + endtime.getTime() + ", " + timeelapsed + ", HIT!");
		
		try{
			out.close();
			dataFile.close();
		}catch(Exception x){
			System.out.println("ERROR: Closing dataFile in printTrialData failed!");
		}
	}
	
	/**
	 * mouseClicked
	 * Handles the action when a button in the trial window is clicked
	 * Clicking on the start button will start a new trial
	 * Clicking on target or fake target will record data for the trial and return to the startPanel
	 */
	public void mouseClicked( MouseEvent e){
		
		//Button Clicked
		TestButton button = (TestButton) e.getSource();
		
		//If the start button was clicked, generate a new trial window
		if( button.getType() == TestButton.START_BUTTON ){
			//draw up test window
			f.getContentPane().remove(startPanel);
			f.getContentPane().add(searchPanel);
			
			//create vectors of valid x and y values 
			InitializeHTargetPositions();
			InitializeVTargetPositions();
			
			//remove all elements from the button vector
			//buttons.removeAllElements();
			
			
			//generate a target for the actual TARGET_BUTTON
			generateTargets( target, TestButton.TARGET_BUTTON, 0 );
			
			//generate targets for both types of fake buttons
			for(int i = 1; i < 31; i=i+2){
				generateTargets( fakebutton1, TestButton.FAKE_BUTTON, i );
				generateTargets( fakebutton2, TestButton.FAKE_BUTTON, i+1 );
			}
			
			//repaint the screen
			f.getContentPane().repaint();
			f.setVisible(true);
			
			//record the time the experiment started
			starttime = new Date();
			TestButton targetbutton = (TestButton) buttons.get(0);
			
			//better system for gettting sound file
			
			String hsound = hcond.getSoundFile(resX, resY, targetbutton.getX(), targetbutton.getY(), Condition.HORIZONTAL);
			String vsound = vcond.getSoundFile(resX, resY, targetbutton.getX(), targetbutton.getY(), Condition.VERTICAL);
			
			//play sound using WavPlayer
			if( invert == 0){
				SoundCue.playCues(hcond.getDir(), hsound, vcond.getDir(), vsound);
			}
			else{
				SoundCue.playCues(vcond.getDir(), vsound, hcond.getDir(), hsound);
			}
			
		}
		//if the button pressed was a fake button
		else if( button.getType() == TestButton.FAKE_BUTTON){
			
			//record end time for trial
			endtime = new Date();
			
			TestButton targetbutton = (TestButton) buttons.get(0);
			TestButton clickedbutton = (TestButton) e.getComponent();
			
			//print the Trial Data to the participant data file
			printTrialData( file, clickedbutton, targetbutton, false);
			
			//remove all components of the searchPanel and return to the startPanel
			searchPanel.removeAll();
			f.getContentPane().remove(searchPanel);
			f.getContentPane().add(startPanel);
			
			//repaint
			f.getContentPane().repaint();
			f.setVisible(true);
			//increase the trial count
			countTrials++;
		}
		//if the button clicked was the start button
		else if( button.getType() == TestButton.TARGET_BUTTON){
			
			//record trial end time
			endtime = new Date();
			
			TestButton targetbutton = (TestButton) buttons.get(0);
			TestButton clickedbutton = (TestButton) e.getComponent();
			
			//print Trial Data to the participant data file
			printTrialData( file, clickedbutton, targetbutton, true);
			
			//remove all components from the searchPanel and set the screen to the startPanel
			searchPanel.removeAll();
			f.getContentPane().remove(searchPanel);
			f.getContentPane().add(startPanel);
			
			//repaint the screen
			f.getContentPane().repaint();
			f.setVisible(true);
			
			//increase the trial count
			countTrials++;
			
		}
		
		//check to see if you've completed the right number of trials
		//System.out.println( "countTrials = " + countTrials + " numberTrials = " + numberTrials);
		if( (countTrials >= numberTrials) ){
			//System.out.println("Closing test window now!");
			f.dispose();
		}
		
		//close data file
		try{
			dataFile.close();
		}catch(Exception x){
			System.out.println("ERROR: Closing file " + dataFile);
		}
	}
	
	/**
	 * 
	 * @param icon - image to use to generate the button
	 * @param type - designates the type of TestButton to create
	 */
	private void generateTargets( ImageIcon icon, int type, int i ){
		
		int x = (resX/31) + 5;
		int y = (resY/31) - 3;
		int a, b;
		Integer h, v;
		
		if( type == TestButton.TARGET_BUTTON){
			a = rand( usedXPositions.size() - 1);
			h = (Integer) usedXPositions.get(a);
			b = rand( usedYPositions.size() - 1 );
			v = (Integer) usedYPositions.get(b);
			
			//remove the x and y position from the vectors
			usedXPositions.remove(a);
			usedYPositions.remove(b);
			
			//remove objects from valid fake target positions for this subtrial
			htargetPositions.remove(h);
			vtargetPositions.remove(v);
			
		}else{
		//choose button positions randomly from vector
			//System.out.println( "htargetPositions: " + htargetPositions.size() + " vtargetPositions: " + vtargetPositions.size());
			
			a = rand( htargetPositions.size() - 1);
			h = (Integer) htargetPositions.get(a);
			b = rand( vtargetPositions.size() - 1 );
			v = (Integer) vtargetPositions.get(b);
			
			//remove the x and y position from the vectors
			htargetPositions.remove(a);
			vtargetPositions.remove(b);
		}
		/*usedXPositions.add(h);
		usedYPositions.add(v);*/
		
		if( countTrials == 0 ){
			Image img = icon.getImage();
			img = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
			icon.setImage(img);
			TestButton button = new TestButton( icon, resX, resY );
			//get button size
			Dimension size = button.getPreferredSize();
			//set the position of the button
			button.setBounds( h.intValue(), v.intValue(), size.width, size.height);
			//set button type
			button.setType( type );
			button.addMouseListener(this);
			//add to the searchPanel
			searchPanel.add(button);
			//add button to the button vector
			buttons.add(button);
		}
		else{
			Dimension size = buttons.get(i).getPreferredSize();
			buttons.get(i).setBounds( h.intValue(), v.intValue(), size.width, size.height);
			searchPanel.add(buttons.get(i));
		}
		
	}
	/**
	 * Generates a random integer between 0 and number
	 * 
	 * @return random integer
	 */
	private int rand( int number) {
		return (int) Math.round(Math.random() * number);
	}
	
	/**
	 * This function populates targetPositions with all of the target x
	 * coordinates that relate to degrees, starting with -15 going to 15, this
	 * is used to randomly select the target's x positions
	 */
	private void InitializeHTargetPositions() {
		
		for( int i = 0; i < 31 ; i++ ){
			htargetPositions.addElement(new Integer( i * (resX/31)));
		}
		
	}

	private void InitializeVTargetPositions(){
		
		for( int i = 0; i < 31; i++){
			vtargetPositions.addElement(new Integer(i * (resY/31)));
		}
		
	}
	
	public void mousePressed( MouseEvent e){ }
	public void mouseExited( MouseEvent e){ }
	public void mouseEntered( MouseEvent e){ }
	public void mouseReleased( MouseEvent e){ }
	
	/**
	 * Prints the vertical and horizontal cue to the participant data file before recording trial data for the participant
	 * 
	 * @param file - participant data file
	 */
	public void printConditionHeader( String file ){
		try {
			dataFile = new FileOutputStream( "data/" + Integer.toString(pID) + "data.txt", true);
			PrintStream out = new PrintStream(dataFile);
			
			out.println();
			if( invert == 0){
				out.println( "Horizontal Cue, " + hcond.getDesc() );
				out.println( "Vertical Cue, " + vcond.getDesc() );
			}
			else{
				out.println( "Vertical Cue, " + vcond.getDesc() );
				out.println( "Horizontal Cue, " + hcond.getDesc() );
			}
			out.println( );
			out.println( "Trial #, TargetPosX, TargetPosY, ClickedX, ClickedY, StartTime, EndTime, ElapsedTime, Hit/Miss");
			
			out.close();
			dataFile.close();
		}catch(Exception x){
			System.out.println("Error opening datafile: " + Integer.toString(pID) + "data.txt in constructor");
		}
	}
	
	/**
	 * launchFrame
	 * launches the TestWindow
	 */
	public void launchFrame(){
        f.pack();
        f.setVisible(true);
    }
}
