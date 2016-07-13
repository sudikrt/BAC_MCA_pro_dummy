import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
public class sender {
 	static ServerSocket MyService;
 	static  Socket clientSocket = null;
 	BufferedInputStream input;
 	TargetDataLine targetDataLine;
	BufferedOutputStream out;
   	ByteArrayOutputStream byteArrayOutputStream;
   	AudioFormat audioFormat; 
	SourceDataLine sourceDataLine;  
  	byte tempBuffer[] = new byte[10000];
	sender() throws LineUnavailableException{ 
     		try {
      			audioFormat = getAudioFormat();
      			DataLine.Info dataLineInfo =  new DataLine.Info( SourceDataLine.class,audioFormat);
      			sourceDataLine = (SourceDataLine)
         			AudioSystem.getLine(dataLineInfo);
         			sourceDataLine.open(audioFormat);
         			sourceDataLine.start();
   			MyService = new ServerSocket(500);
   			clientSocket = MyService.accept();
   			captureAudio();
   			input = new BufferedInputStream(clientSocket.getInputStream()); 
   			out=new BufferedOutputStream(clientSocket.getOutputStream());
			while(input.read(tempBuffer)!=-1){   
    				sourceDataLine.write(tempBuffer,0,10000);
   			}
  		} catch (IOException e) {
   		   	e.printStackTrace();
  		}
	}
private AudioFormat getAudioFormat(){
      	float sampleRate = 8000.0F;   
      	int sampleSizeInBits = 16;   
     	int channels = 1;     
      	boolean signed = true;     
      	boolean bigEndian = false;  
      	return new AudioFormat(
                        sampleRate,
                        sampleSizeInBits,
                        channels,
                        signed,
                        bigEndian);
}
 public void captureAudio() {
  	try {
      		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
   		System.out.println("Available mixers:");
   		for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
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
  	} 
	catch (Exception e) {
   		System.out.println(e);
   		VoiceChat.txArea.append("User Closed chat"+"\n");
		VoiceChat.request.setEnabled(true);
		VoiceChat.stop.setEnabled(false);
  	}
}
public static void sClose()
{
	try{
		clientSocket.close();
		MyService.close();
	}
	catch(Exception er){
	}
}
class CaptureThread extends Thread {
	byte tempBuffer[] = new byte[10000];
  	public void run() {   
   		try {
    			while (true) {
     				int cnt = targetDataLine.read(tempBuffer, 0,
       				tempBuffer.length);
     				out.write(tempBuffer);    
    			}
       		} 
		catch (Exception e) {
    			System.out.println(e);
    			VoiceChat.txArea.append("User Closed chat"+"\n");
			VoiceChat.request.setEnabled(true);
			VoiceChat.stop.setEnabled(false);
   		}
  	}
 }

}

