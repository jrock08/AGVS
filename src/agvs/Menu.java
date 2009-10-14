package agvs;


import agvs.TestWindow; 
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JComboBox;
import java.io.*;
/** 
	The Menu class is the initial GUI for setting up an AGVS experiment
*/

public class Menu{
	
	/** settingsName - File used to record previous participant ID*/
	private String settingsName = "Settings/AGVSSettings";
	
	/** settings - Properties variable used to record previous participant ID*/
	private Properties settings;
	/** NUMASSIGNS - maximum number of participants to record data from */
	private final int NUMASSIGNS = 1200;
	
	private JLabel conditionLabel;
	private JLabel conditionLabel1;
	private JLabel conditionLabel2;
	
	// Initialize all swing objects.
	/** f - JFrame for GUI*/
    private JFrame f = new JFrame("AGVS Experiment"); //create Frame
    /** doneSetup - button to complete setup */
    private JButton doneSetup;
    
    /** participantID, numTrials, numConditions - used to get input from user for initial settings */
    private JSpinner participantID; // = new JSpinner();
    private JSpinner numTrials;
    private JSpinner numConditions;
    
    /** participant, trials, conditions, vertical, horizontal, blank - JLabels used to label user input */
    private JLabel participant;
    private JLabel trials;
    private JLabel conditions;
    private JLabel vertical;
    private JLabel horizontal;
    private JLabel blank;
    
    /** chooseConditions - used to continue with the experiment setup and choose the number of conditions as specified in the previous menu */
    private JButton chooseConditions;
    /** startExperiment - starts the experiment */
    private JButton startExperiment;
    
    /** Screen resolution */
    private JComboBox res;
    
    /** JComboBox resolution choice */
    private int resChoice;
    /** resX, resY - x and y screen resolution */
    private int resX;
    private int resY;
    
    //Experiment Information!!
    /** vertConditions, horzConditions - Vectors of JComboBoxes to choose conditions for the experiment */
    private Vector<JComboBox> vertConditions;
    private Vector<JComboBox> horzConditions;
    private Vector<JComboBox> noiseConditions;
    /** v, h - vectors to hold all available conditions */
    private Vector<Condition> v;
    private Vector<Condition> h;
    
    /** invert - decides whether to play horizontal cue first or vertical (true for vertical first, false for horizontal first) */
    private Vector invert = new Vector();
    private Vector nConditions = new Vector();
    
    /** invertComboBox - JComboBoxes that allows the user to decide whether to invert the order of the cues */
    private Vector<JComboBox> invertComboBox = new Vector<JComboBox>();
    
    /** vConditions, hConditions - Vectors of conditions chosen by the user */
    private Vector<Condition> vConditions = new Vector<Condition>();
    private Vector<Condition> hConditions = new Vector<Condition>();
    
    /** conds - number of conditions */
    private int conds;
    /** pID - participant ID */
    private int pID;
    /** inv - designates whether the condition is inverted or not */
    private int inv;
    /** noi - designates whether the condition has noise or not */
    private int noi;
    /** nTrials - number of trials */
    private int nTrials;
    
    /** current condition, starts off as training (no sound) */
    private Condition currentVCondition;
    private Condition currentHCondition;
    
    private Container cp;

    /** mb, mnuFile, mnuItemQuit, menuHelp, mnuItemAbout - Menubar items */
    private JMenuBar mb = new JMenuBar(); // Menubar
    private JMenu mnuFile = new JMenu("File"); // File Entry on Menu bar
    private JMenuItem mnuItemQuit = new JMenuItem("Quit"); // Quit sub item
    private JMenu mnuHelp = new JMenu("Help"); // Help Menu entry
    private JMenuItem mnuItemAbout = new JMenuItem("About"); // About Entry
    //private JMenuItem mnuItemTrain = new JMenuItem("Start Training"); // Start Training Task

    /** Constructor for the GUI */
    public Menu(){
    	
    	//load settings if possible
		int lastParticipantID = 0;
		try{
			FileReader in = new FileReader(new File(settingsName));
			BufferedReader input = new BufferedReader(in);
			String lastParticipantStr = input.readLine();
			//System.out.println("lastParticipantStr = " + lastParticipantStr);
			if (lastParticipantStr != null) {
				try {
					lastParticipantID = Integer.parseInt(lastParticipantStr);
				} catch (Exception e) {
				}
			}
			input.close();
			in.close();
		}catch( Exception e ){
			System.out.println("Error loading file " + settingsName + " :" + e.toString());
		}
		
		
		// Set menubar
    	cp = new Container();
    	cp.setLayout(new GridLayout(0,2,3,3));
        f.setJMenuBar(mb);
        
        settings = new Properties();
        
		//Build Menus
        mnuFile.add(mnuItemQuit);  // Create Quit line
        //mnuFile.add(mnuItemTrain); // Add Training Line
        mnuHelp.add(mnuItemAbout); // Create About line
        mb.add(mnuFile);        // Add Menu items to form
        mb.add(mnuHelp);
        mnuItemQuit.setEnabled(false); //Disable Quit until end of experiment

        
        participant = new JLabel("Participant ID");
        Font f1 = participant.getFont().deriveFont(16.0f);
        participant.setFont(f1);
        // sets participantID, numTrials and numConditions for user input
        participantID = new JSpinner(new SpinnerNumberModel(
				lastParticipantID + 1, 0, NUMASSIGNS, 1));
        
        trials = new JLabel("Num of Trials");
        trials.setFont(f1);
        numTrials = new JSpinner(new SpinnerNumberModel( 15, 0, 100, 1));
        
        conditions = new JLabel("Num of Conditions");
        conditions.setFont(f1);
        numConditions = new JSpinner( new SpinnerNumberModel( 5, 0, 10, 1));
        
        
        chooseConditions = new JButton("Choose Conditions");
        chooseConditions.setFont(f1);
        chooseConditions.addActionListener(new ListenButtonConditions());
        
     // Set up res JComboBox
		String resChoices[] = {"640 x 480", "800 x 600", "1024 x 768", "1152 x 864", "1152 x 870", "1280 x 1024", "1600 x 1200" };
        res = new JComboBox(resChoices);
        
        
        blank = new JLabel();
        JLabel resLabel = new JLabel("Resolution");
        resLabel.setFont(f1);

        // Setup Main Frame and add components
       
        cp.add(participant);
        cp.add(participantID);
        cp.add(trials);
        cp.add(numTrials);
        cp.add(conditions);
        cp.add(numConditions);
        cp.add(resLabel);
        cp.add(res);
        cp.add(chooseConditions);
        cp.add(blank);
        f.add(cp);
        
		// Allows the Swing App to be closed
        f.addWindowListener(new ListenCloseWdw());
        
		//Add Menu listener
        mnuItemQuit.addActionListener(new ListenMenuQuit());
        
        //f.setPreferredSize(new Dimension( 400, 400 ));
    }
    
    /**
     * setResolution
     * Takes an int and sets the x and y resolution based on the int
     * 
     * @param choice - index from JComboBox choice
     */
    public void setResolution( int choice ){
    	if( choice == 0 ){
    		resX = 640; resY = 480;
    	}
    	else if( choice == 1 ){
    		resX = 800; resY = 600;
    	}
    	else if( choice == 2 ){
    		resX = 1024; resY = 768;
    	}
    	else if( choice == 3 ){
    		resX = 1152; resY = 864;
    	}
    	else if( choice == 4 ){
    		resX = 1152; resY = 870;
    	}
    	else if( choice == 5 ){
    		resX = 1280; resY = 1024;
    	}
    	else if( choice == 6 ){
    		resX = 1600; resY = 1200;
    	}
    }
    
    /**
     * ListenButtonConditions is called when the user clicks chooseConditions
     * Creates JComboBoxes to allow users to choose the conditions for each trial
     * 
     * @author Kayleigh
     *
     */
    public class ListenButtonConditions implements ActionListener{
    	public void actionPerformed( ActionEvent e ){
    		//disables the chooseConditions button
    		chooseConditions.setVisible(false);
    		cp.removeAll();
    		
    		int resChoice = res.getSelectedIndex();
    		setResolution( resChoice );
    		
    		cp.setLayout(new GridLayout(0, 5, 3, 3));
    		JLabel conditionchoices = new JLabel( "Condition Choices");
    		Font f1 = conditionchoices.getFont().deriveFont(16.0f);
            conditionchoices.setFont(f1);
    		JLabel blank2 = new JLabel(" ");
    		JLabel blank3 = new JLabel(" ");
    		JLabel blank4 = new JLabel(" ");
    		JLabel blank5 = new JLabel(" ");
    		JLabel blank6 = new JLabel(" ");
    		
    		cp.add(conditionchoices);
    		cp.add(blank);
    		cp.add(blank2);
    		cp.add(blank4);
    		cp.add(blank6);
    		
    		//gets available conditions
    		Vector<Condition> c = populateConditions("Condition");
    		v = c;
    		h = c;
    		
    		//Uses the descriptions to create an array of condition choices for the JComboBox
    		String conditionChoices[] = new String[c.size()];
    		for( int i = 0; i < c.size(); i++ ){
    			conditionChoices[i] = c.get(i).getDesc();
    		}
    		String invertChoices[] = new String[2];
    		invertChoices[0] = "Horizontal First";
    		invertChoices[1] = "Vertical First";
    		
    		String noiseChoices[] = new String[2];
    		noiseChoices[0] = "No Noise";
    		noiseChoices[1] = "Noise";
    		
    		JLabel invert = new JLabel("Invert");
            invert.setFont(f1);
    		vertical = new JLabel("Vertical Cue");
    		vertical.setFont(f1);
    		horizontal = new JLabel("Horizontal Cue");
    		horizontal.setFont(f1);
    		JLabel noise = new JLabel("Background Noise");
    		noise.setFont(f1);
    		
    		
    		cp.add(blank3);
    		cp.add(vertical);
    		cp.add(horizontal);
    		cp.add(invert);
    		cp.add(noise);
    		
    		conds = getNumConditions();
    		
    		vertConditions = new Vector<JComboBox>();
    		horzConditions = new Vector<JComboBox>();
    		noiseConditions = new Vector<JComboBox>();
    		
    		
    		
    		JLabel train = new JLabel("Training");
    		train.setFont(f1);
    		//JLabel train2 = new JLabel("Training Condition 2");
    		//populate from conditions list when started up
    		for( int i=0; i < conds; i++){
    			if( i == 0 )
    				cp.add(train);
    			else{
    				JLabel cond = new JLabel("Condition " + (i-1));
    				cond.setFont(f1);
    				cp.add(cond);
    			}
    				
    			//get conditions from conditions list populated at startup
    			JComboBox vertBox = new JComboBox(conditionChoices);
    			JComboBox horzBox = new JComboBox(conditionChoices);
    			JComboBox invBox = new JComboBox(invertChoices);
    			JComboBox noiBox = new JComboBox(noiseChoices);
    			vertConditions.add(vertBox);
    			horzConditions.add(horzBox);
    			invertComboBox.add(invBox);
    			noiseConditions.add(noiBox);
    			cp.add( (JComboBox) vertConditions.get(i) );
    			cp.add( (JComboBox) horzConditions.get(i) );
    			cp.add( (JComboBox) invertComboBox.get(i) );
    			cp.add( (JComboBox) noiseConditions.get(i) );
    			
    		}
    		
    		//Creates a button to continue from the setup screens to the experiment screents
    		doneSetup = new JButton("Setup Complete");
    		doneSetup.setFont(f1);
    		doneSetup.addActionListener(new ListenDoneSetup() );
    		cp.add(doneSetup);
    		//f.setPreferredSize(new Dimension( 400, 800 ));
    		f.setBounds(0, 0, 720, 550);
    		cp.setPreferredSize(new Dimension( 720, 550 ));
    		cp.repaint();
    		cp.setVisible(true);
    	}
    }
    
    /**
     * ListenDoneSetup is called when the user clicks the doneSetup button
     * Sets the experiment up to run
     * 
     * @author Kayleigh
     *
     */
    public class ListenDoneSetup implements ActionListener{
        public void actionPerformed(ActionEvent e){
        	pID = getParticipantID();
        	nTrials = getNumTrials();
        	
        	//gets the conditions chosen by the user and puts them in vConditions and hConditions
        	for( int i = 0; i < vertConditions.size(); i++){
        		JComboBox tempv = (JComboBox)vertConditions.get(i);
        		int condv = tempv.getSelectedIndex();
        		JComboBox temph = (JComboBox)horzConditions.get(i);
        		int condh = temph.getSelectedIndex();
        		JComboBox tempi = (JComboBox)invertComboBox.get(i);
        		int inv = tempi.getSelectedIndex();
        		JComboBox tempn = (JComboBox)noiseConditions.get(i);
        		int noi = tempn.getSelectedIndex();
        		vConditions.add( v.get(condv));
        		hConditions.add( h.get(condh));
        		invert.add(new Integer(inv));
        		nConditions.add(new Integer(noi));
        		
        	}
        	
        	//sets properties
        	//settings.setProperty("LastParticipant", Integer.toString(pID));
        	//settings.setProperty("NumberTrials", Integer.toString(nTrials));
        	cp.removeAll();
        	
        	
        	//add button to start experiment
        	startExperiment = new JButton( "Start Training" );
        	Font f1 = startExperiment.getFont().deriveFont(16.0f);
        	startExperiment.setFont(f1);
        	startExperiment.addActionListener(new ListenStartExperiment());
        	startExperiment.setBounds(230, 200, 150, 50);
        	
        	//sets the participant data file and prints the participant ID to the file
        	String file = "data/" + Integer.toString(pID) + "data.txt";
        	printParticipantHeader( file );
        	
        	getNextCondition();
        	
        	SetExperimentLabels();
        	
        	cp.setLayout(new GridLayout(0,2,3,3));
        	cp.add(startExperiment);
        	cp.add(conditionLabel1);
        	cp.add(conditionLabel);
        	cp.add(conditionLabel2);
        	
        	conditionLabel1.setBounds(150, 50, 600, 30);
        	conditionLabel.setBounds(150,100,600,30);
        	conditionLabel2.setBounds(150, 150, 600, 30);
        	cp.repaint();
        	cp.setVisible(true);
        	
        }
    }
    /**
     * SetExperimentLabels
     * Sets up the condition labels for the "Next Trial" Screen
     */
    
    public void SetExperimentLabels(){
    	
    	//if horizontal cue is being played first
    	
    	//System.out.println("conditionLabel here!");
    	conditionLabel1 = new JLabel("");
    	//conditionLabel1 = new JLabel( "Condition " + currentHCondition.getConditionnum()+ currentVCondition.getConditionnum() +":");
    	if( currentHCondition.getConditionnum() == 0 && currentVCondition.getConditionnum() == 0){//no cues
    		conditionLabel = new JLabel (" No auditory cues.");
   			conditionLabel2 = new JLabel("");
   		}
   		else if( currentHCondition.getConditionnum() == 0){//no horizontal cue
    		conditionLabel = new JLabel("Vertical cue only.");
    		conditionLabel1.setText( currentVCondition.getConditionlabel() );
   			conditionLabel2 = new JLabel("");
   		}
   		else if ( currentVCondition.getConditionnum() == 0 ){ //no vertical cue
   			conditionLabel = new JLabel("Horizontal cue only.");
   			conditionLabel1.setText( currentHCondition.getConditionlabel() );
   			conditionLabel2 = new JLabel("");
   		}
   		else if( inv == 0){ //horizontal cue is being played first
   			if( currentHCondition.getConditionlabel().equals( currentVCondition.getConditionlabel()) ){
   				conditionLabel1.setText(currentHCondition.getConditionlabel());
   			}
   			else{
   				conditionLabel1.setText("Cues: " + currentHCondition.getConditionlabel() + ", " + currentVCondition.getConditionlabel() );
   			}
   			conditionLabel = new JLabel( "Horizontal cue played first, " );//+ currentHCondition.getDesc() );
    		conditionLabel2 = new JLabel( "followed by a vertical cue. " );//+ currentVCondition.getDesc() );
    	}
    	else{//if vertical cue is being played first
    		
    		if( currentHCondition.getConditionlabel().equals( currentVCondition.getConditionlabel()) ){
   				conditionLabel1.setText(currentHCondition.getConditionlabel());
   			}
    		else{
    			conditionLabel1.setText("Cues:" + currentVCondition.getConditionlabel() + ", " + currentHCondition.getConditionlabel() );
    		}
    		conditionLabel = new JLabel( "Vertical cue played first " );
    		conditionLabel2 = new JLabel( "followed by a horizontal cue. " );
    	}
    	
    	Font f = conditionLabel.getFont().deriveFont(16.0f);
    	conditionLabel.setFont(f);
    	conditionLabel1.setFont(f);
    	conditionLabel2.setFont(f);
    	
    }
    
    /**
     * ListenStartExperiment
     * Sets up the trials for the current condition
     * Chooses the condition randomly from the list of user selected conditions
     * 
     * @author Kayleigh
     *
     */
    public class ListenStartExperiment implements ActionListener{
    	public void actionPerformed( ActionEvent e ){
    		 
    		
    		
        	//sets up the test window to run the trials
    		if( vConditions.size() == conds-1){ //training condition with 5 trials
    			TestWindow train = new TestWindow( 5, pID, currentHCondition, currentVCondition, inv, noi, resX, resY );
    			train.launchFrame();
    		}else{ //any other condition with the number of trials specified by the experimenter
    			TestWindow train = new TestWindow( nTrials, pID, currentHCondition, currentVCondition, inv, noi, resX, resY );
    			train.launchFrame();
    		}
        	
        	
        	//sets the button to start the next trial
        	cp.removeAll();
        	cp.remove(startExperiment);
        	
        	//if there are no more conditions to run, finished!
        	//System.out.println("(2)vconditions size: " + vConditions.size() + " hconditions size: " + hConditions.size() + " r: " + r);
        	if( vConditions.size() <= 0){
        		cp.removeAll();
        		JLabel thanks = new JLabel( "Thank you for participating!");
        		Font f1 = thanks.getFont().deriveFont(18.0f);
        		thanks.setFont(f1);
        		cp.add(thanks);
        		thanks.setBounds(230, 175, 400, 30);
        		
        		mnuItemQuit.setEnabled(true);
        		cp.repaint();
        		cp.setVisible(true);
        	}
        	else{
        		getNextCondition();
            	
        		//set labels to display with next trial button, tells which condition is first and what number condition it is
        		SetExperimentLabels();
            	startExperiment.setText("Next Trial");
            	
            	//startExperiment.addActionListener(new ListenStartExperiment());
            	startExperiment.setBounds(230, 200, 150, 50);
            	cp.add(conditionLabel1);
            	cp.add(conditionLabel);
            	cp.add(conditionLabel2);
            	conditionLabel1.setBounds(150, 50, 600, 30);
            	conditionLabel.setBounds(150,100,600,30);
            	conditionLabel2.setBounds(150, 150, 600, 30);
            	cp.add(startExperiment);
            	cp.repaint();
            	cp.setVisible(true);
        		
        	}
        	
    	}
    }
    
    /**
     * saveSettings
     * saves the current settings of the program in the variable settings
     */
    public void saveSettings(){
    	//   	 attempt to store the properties
    	
    	
    	try{
    		FileOutputStream out = new FileOutputStream(settingsName);
    		PrintStream output = new PrintStream(out);
    		output.println(Integer.toString(pID));
    		//System.out.println( "writing to file ...." + pID);
    		output.close();
    		out.close();
    	}catch(Exception x){
    		System.err.println("There was a problem storing " + settingsName
					+ ": " + x.getMessage());
    	}
    	/*try {
			FileOutputStream propOut = new FileOutputStream(new File(settingsName));
			settings.store(propOut, "AGVS Settings");
			System.out.println("Saved settings!");
		} catch (Exception x) {
			System.err.println("There was a problem storing " + settingsName
					+ ": " + x.getMessage());
		}*/
    }
    
    /**
     * ListenMenuQuit
     * Quits the program
     * 
     * @author Kayleigh
     *
     */
    public class ListenMenuQuit implements ActionListener{
        public void actionPerformed(ActionEvent e){
        	saveSettings();
            System.exit(0);
        }
    }
	
    /**
     * ListenCloseWdw
     * Closes the window
     * 
     * @author Kayleigh
     *
     */
    public class ListenCloseWdw extends WindowAdapter{
        public void windowClosing(WindowEvent e){
        	saveSettings();
            System.exit(0);         
        }
    }
	
    /**
     * launchFrame
     * launches the Menu windows
     */
    public void launchFrame(){
        // Display Frame
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack(); //Adjusts panel to components for display
        f.setVisible(true);
    }
    
    /**
	 * Get the participant id
	 * 
	 * @return an int for the participant id selected by participantId
	 */
	private int getParticipantID() {
		return ((SpinnerNumberModel) participantID.getModel()).getNumber()
				.intValue();
	}
	
	/**
	 * Get the number of conditions
	 * 
	 * @return an int for the number of conditions selected by numConditions
	 */
	private int getNumConditions() {
		return ((SpinnerNumberModel) numConditions.getModel()).getNumber()
				.intValue();
	}
	
	/**
	 * Get the number of trials
	 * 
	 * @return an int for the number of trials selected by numTrials
	 */
	private int getNumTrials() {
		return ((SpinnerNumberModel) numTrials.getModel()).getNumber()
				.intValue();
	}
	
	private int rand( int number) {
		return (int) Math.round(Math.random() * number);
	}
    
	private Vector<Condition> populateConditions(String dir){

		File f = new File( dir );
		String[] files = null;
		if( f.exists() && f.isDirectory()){
			files = f.list();
		}
		else{
			System.out.println("Error: Directory " + dir + " does not exist!");
		}
		
		Vector<Condition> conds = new Vector<Condition>();
		for( int i=0; files != null && i < files.length; i++){
			//System.out.println( files[i] );
			Condition c = new Condition( dir + "/" + files[i] );
			conds.add(c);
		}
		
		return conds;
		
	}
	
public void getNextCondition() {
    	
    	//System.out.println("New Test Condition!");
		int r;
    	//pick next condition from the list (the first two conditions should not be random ... but the rest will be
    	if( (vConditions.size() == conds) || (vConditions.size() == (conds-1))  )
    		r = 0;
    	else if( vConditions.size() == 1 )
    		r = 0;
    	else{
    		r = rand( vConditions.size() - 1);
    	}
    	
    	//selects condition using randomly selected index
    	//System.out.println("vConditions: " + vConditions.size() + " hConditions: " + hConditions.size());
    	
    	//ARRAY INDEX OUT OF RANGE: 0 ????
    	//System.out.println("vconditions size: " + vConditions.size() + " hconditions size: " + hConditions.size() + " r: " + r);
    	Condition vcond = (Condition)vConditions.get(r);
    	Condition hcond = (Condition)hConditions.get(r);
    	inv = (Integer)invert.get(r);
    	noi = (Integer)nConditions.get(r);
    	
    	//sets the selected condition as the current conditions
    	currentVCondition = vcond;
    	currentHCondition = hcond;
    	
    	//removes conditions from the list of conditions to test
    	vConditions.remove(r);
    	hConditions.remove(r);
    	invert.remove(r);
    	nConditions.remove(r);
    }

	public void printParticipantHeader( String file ){
		try {
			FileOutputStream dataFile = new FileOutputStream( "data/" + Integer.toString(pID) + "data.txt", true);
			PrintStream out = new PrintStream(dataFile);
			
			out.println("Participant, " + pID);
			
			out.close();
			dataFile.close();
		}catch(Exception x){
			System.out.println("Error opening datafile: " + file+ " in constructor");
		}
	}
	

	public static void main(String args[]){
	    Menu gui = new Menu();
	    gui.launchFrame();
	}
}