package agvs;

import java.util.*;
import java.io.*;

/**
 * 
 * Condition.java
 * 
 * Condition .txt file Format:
 * 		Sound directory for condition sound files
 * 		Description of the condition
 * 		isSpatialized (0 if not spatialized, 1 if it is)
 * 		Number of quadrants (number of horizontal quadrants if vquadrants is not 0)
 * 		Number of vertical quadrants (0 if the condition doesn't deal with both directions)
 * 		List of sound files, 1 per line
 * 
 * This class stores information about each of the experiment conditions.
 * 
 * @author Kayleigh
 *
 */

public class Condition {
	
	/**
	 * HORIZONTAL and VERTICAL variables
	 * variables defining values for horizontal and vertical cue
	 */
	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;
	
	/**
	 * dir - folder containing the sound files for the condition
	 */
	private String dir;
	/**
	 * desc - description of the condition for using as labels in the gui
	 */
	private String desc;
	/**
	 * quadrants - number of quadrants that the condition deals with
	 * 			   if vquadrants is not zero, this is the number of horizontal quadrants
	 */
	private int quadrants;
	/**
	 * spatialized - declares if the sound is spatialized or not
	 * 				 is 0 if mono, 1 if spatialized
	 */
	private int spatialized;
	/**
	 * vquadrants - number of vertical quadrants
	 * 				is 0 if this is irrelevant for the condition
	 */
	private int vquadrants;
	/**
	 * soundFiles - vector containing ordered list of sound files
	 * 			    left to right / top to bottom
	 */
	private Vector<String> soundFiles;
	
	/**
	 * conditionnum - integer noting which condition number this is
	 * 
	 */
	private int conditionnum;
	
	/**
	 * conditionlabel - string to be displayed to the subject as a description of the condition
	 * 
	 */
	private String conditionlabel;
	/**
	 * Constructor
	 * @param file - condition file containing relevant condition information
	 * 				 Place these in the Condition folder
	 */
	public Condition(String file){
		String line;
		soundFiles = new Vector<String>();
		try{
			FileReader in = new FileReader(new File(file));
			BufferedReader input = new BufferedReader(in);
			conditionnum = new Integer(input.readLine());
			conditionlabel = input.readLine();
			dir = input.readLine();
			desc = input.readLine();
			spatialized = new Integer(input.readLine());
			quadrants = new Integer( input.readLine() );
			vquadrants = new Integer( input.readLine() );
			
			while( (line = input.readLine()) != null ){
				//System.out.println( line );
				soundFiles.add(line);
			}
			
		}catch(Exception x){
			System.out.println("Error reading file " + file + "!");
			System.exit(1);
		}
	}
	
	/**
	 * getSoundFile
	 * 
	 * Returns the appropriate sound file for the condition given the resolution of the screen 
	 * and the position of the target
	 * 
	 * @param resX - width of window
	 * @param resY - height of window
	 * @param tarX - x coordinate of target
	 * @param tarY - y coordinate of target
	 * @param type - horizontal or vertical cue
	 * @return
	 */
	public String getSoundFile( int resX, int resY, int tarX, int tarY, int type ){
		
		String sound = "";
		int quadsizex, quadsizey;
		int qx, qy; //quadrant target is in
		//deals with both horizontal and vertical space

			quadsizex = resX/quadrants;
			qx = tarX/quadsizex;
			
			if( vquadrants != 0 ){
				quadsizey = resY/vquadrants;
				qy = tarY/quadsizey;
			}
			else{
				quadsizey = resY;
				qy = 0;
			}
			
			
			//add qx and qy together to get index
			if( vquadrants != 0 ){
				//System.out.println(qx + " " + qy + " " + ( qx +  qy ));
				sound = soundFiles.get( (qx  + ( qy * quadrants) ) );
			}
			//use qx as index
			else if( type == Condition.HORIZONTAL ){
				sound = soundFiles.get(qx);
			}
			//use qy as index
			else if( type == Condition.VERTICAL ){
				sound = soundFiles.get(qy);
			}
			
			
			
		return sound;
	}
	/**
	 * getConditionnum
	 * @return conditionnum
	 */
	public int getConditionnum(){ return conditionnum; }
	/**
	 * get Conditionlabel
	 * @return conditionlabel
	 */
	public String getConditionlabel(){ return conditionlabel; }
	
	/**
	 * getDir
	 * @return dir
	 */
	public String getDir(){ return dir; }
	/**
	 * getDesc
	 * @return desc
	 */
	public String getDesc(){ return desc; }
	/**
	 * getQuadrants
	 * @return quadrants
	 */
	public int getQuadrants(){ return quadrants; }
	/**
	 * getVQuadrants
	 * @return vquadrants
	 */
	public int getVQuadrants(){ return vquadrants; }
	/**
	 * getSpatialized
	 * @return spatialized
	 */
	public int getSpatialized(){ return spatialized; }
	/**
	 * getSoundFiles
	 * @return soundFiles
	 */
	public Vector<String> getSoundFiles(){ return soundFiles; }
}