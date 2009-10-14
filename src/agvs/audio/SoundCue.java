package agvs.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class SoundCue extends Thread {
	
	/**
	 * Assists in playing back audio.
	 * 
	 * @param filename
	 *            filename of a file to get the AudioInputStream of
	 * @return AudioInputStream belonging to filename
	 * 
	 */
	public static AudioInputStream getInputStream(String dir, String filename) {
		try {
			File soundFile = new File(dir + "/"
					+ filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile);
			if( stream == null){
				System.out.println("Eeek, error reading audio input stream!");
			}
			return stream;

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static void playCues(String dirA, String soundA, String dirB, String soundB ){
		
		try {
			AudioInputStream streamA = getInputStream(dirA, soundA);
			AudioInputStream streamB = getInputStream(dirB, soundB);
			
			final Clip lineA, lineB;
			DataLine.Info infoA = new DataLine.Info(Clip.class, streamA.getFormat());
			DataLine.Info infoB = new DataLine.Info(Clip.class, streamB.getFormat());
			if( !AudioSystem.isLineSupported(infoA)){
				System.out.println("Error: LineA is not supported!");
				System.exit(1);
			}
			if( !AudioSystem.isLineSupported(infoB)){
				System.out.println("Error: LineB is not supported!");
				System.exit(1);
			}
			try{
				lineA = (Clip) AudioSystem.getLine(infoA);
				lineA.open(streamA);
				lineB = (Clip) AudioSystem.getLine(infoB);
				lineB.open(streamB);
				
				lineB.addLineListener(new LineListener() {
				      public void update(LineEvent event) {
				        if (event.getType() == LineEvent.Type.STOP) {
				          lineB.stop();
				        }
				      }
				  	});
				
				
				final Clip temp = lineB;
				lineA.addLineListener(new LineListener() {
				      public void update(LineEvent event) {
				        if (event.getType() == LineEvent.Type.STOP) {
				          lineB.start();
				        }
				      }
				  	});
				lineA.start();
			}catch(Exception e){
				System.out.println("ERROR: Cannot open and play sound cues!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}