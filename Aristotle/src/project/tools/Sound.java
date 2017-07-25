/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.tools;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import static com.sun.prism.impl.Disposer.cleanUp;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    public static synchronized void play(final String fileName) 
    {
        // Note: use .wav files             
        new Thread(() -> {
            try {
                try (Clip clip = AudioSystem.getClip()) {
                    try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(fileName))) {
                        clip.open(inputStream);
                        System.out.println("hello");
                        clip.loop(2);
                        Thread.sleep(11000);
                    }
                    clip.flush();
                }
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException | InterruptedException e) {
            } finally {
                cleanUp();
            }
        }).start();
    }
}
