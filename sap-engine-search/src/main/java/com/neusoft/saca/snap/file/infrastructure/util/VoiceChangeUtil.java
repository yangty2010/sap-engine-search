package com.neusoft.saca.snap.file.infrastructure.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

import java.io.File;

public class VoiceChangeUtil {
	
	public static void amr2mp3(File source,File target){
        AudioAttributes audio = new AudioAttributes();  
//        Encoder encoder = new Encoder(new MyFFMpegLocator());  
        Encoder encoder = new Encoder();  
  
        audio.setCodec("libmp3lame"); 
        audio.setBitRate(new Integer(128000));//手机播放最佳比特率 128kb/s
        EncodingAttributes attrs = new EncodingAttributes();  
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
  
        try {  
            encoder.encode(source, target, attrs);  
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } catch (InputFormatException e) {  
            e.printStackTrace();  
        } catch (EncoderException e) {  
            e.printStackTrace();  
        }
	}
}
