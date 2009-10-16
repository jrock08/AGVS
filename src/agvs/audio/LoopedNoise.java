package agvs.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

/**
 * 
 * plays a noise in the background to distract the participant
 * 
 * @author Jason
 *
 */
public class LoopedNoise extends Thread{
	
	/*
	public static void main(String[] args){
		Clip p = LoopedNoise.loopNoise("Sounds/Noise","tank.wav");
		try{
		Thread.sleep(30*1000);
		}catch(Exception e){}
		LoopedNoise.endLoop(p);
	}*/
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
	
	public static final Clip loopNoise(String dir, String filename){
		final Clip loop;
		try{
			AudioInputStream streamNoise = getInputStream(dir,filename);
			
			DataLine.Info infoLoop = new DataLine.Info(Clip.class, streamNoise.getFormat());
			if( !AudioSystem.isLineSupported(infoLoop)){
				System.out.println("Error: LineA is not supported!");
				System.exit(1);
			}
			try{
				loop = (Clip)AudioSystem.getLine(infoLoop);
				loop.open(streamNoise);
				loop.loop(Clip.LOOP_CONTINUOUSLY);
				return loop;
			}catch(Exception e){
				System.err.println("Couldn't open and play the noise");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	public static void endLoop(Clip loop){
		loop.stop();
	}

}
