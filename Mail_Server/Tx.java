import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.lang.*;
import javax.sound.sampled.TargetDataLine;
public class Tx {
 	boolean stopCapture = false;
 	ByteArrayOutputStream byteArrayOutputStream;
 	AudioFormat audioFormat;
 	TargetDataLine targetDataLine;
 	AudioInputStream audioInputStream;
 	BufferedOutputStream out = null;
 	BufferedInputStream in = null;
 	static  Socket sock = null;
 	SourceDataLine sourceDataLine;
	public Tx(String ss)
	{
		captureAudio(ss);
	}
	private void captureAudio(String st1) {
 		try {
	   		st1=st1.toString().trim();
   			sock = new Socket(st1, 500);
   			out = new BufferedOutputStream(sock.getOutputStream());
   			in = new BufferedInputStream(sock.getInputStream());
   			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
   			System.out.println("Available mixers:");
   			for (int cnt = 0; cnt < mixerInfo.length; cnt++) 
   			{
       				System.out.println(mixerInfo[cnt].getName());
   			}
			VoiceChat.txArea.append("VoiceChat running"+"\n");
   			audioFormat = getAudioFormat();
   			DataLine.Info dataLineInfo = new DataLine.Info(
     			TargetDataLine.class, audioFormat);
   			Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);
   			targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
   			targetDataLine.open(audioFormat);
   			targetDataLine.start();
   			Thread captureThread = new CaptureThread();
   				captureThread.start();
   			DataLine.Info dataLineInfo1 = new DataLine.Info(
     			SourceDataLine.class, audioFormat);
   			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
   			sourceDataLine.open(audioFormat);
   			sourceDataLine.start();
   			Thread playThread = new PlayThread();
   				playThread.start();

  		} 
  		catch(Exception e1){
			System.out.println(e1);

		}
  	}

 	class CaptureThread extends Thread {
		byte tempBuffer[] = new byte[10000];
  		public void run() {
   			byteArrayOutputStream = new ByteArrayOutputStream();
   			stopCapture = false;
   			try {
    				while (!stopCapture) {
     					int cnt = targetDataLine.read(tempBuffer, 0,tempBuffer.length);
     					out.write(tempBuffer);
     					if (cnt > 0) {
      						byteArrayOutputStream.write(tempBuffer, 0, cnt);
     					}
    				}
    				byteArrayOutputStream.close();
   			} catch (Exception e) {
   				 System.out.println(e);
   				VoiceChat.txArea.append("User Closed chat"+"\n");
				VoiceChat.request.setEnabled(true);
				VoiceChat.stop.setEnabled(false);
			 }
  		}
 	}
	public static void sClose()
	{		
		try{
			sock.close();
		}
		catch(Exception ec){}
	}
 	private AudioFormat getAudioFormat() {
  		float sampleRate = 8000.0F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
 	}
 	class PlayThread extends Thread {
  		byte tempBuffer[] = new byte[10000];
  		public void run() {
   			try {
    				while (in.read(tempBuffer) != -1) {
     					sourceDataLine.write(tempBuffer, 0, 10000);
    				}
   			} catch (IOException e) {
    				e.printStackTrace();
   			}
  		}
 	}

}

