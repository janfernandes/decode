package com.opus.audio.decode.core;
//
//import javax.sound.sampled.*;
//import javax.swing.*;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//
//public class SoundClipTest extends JFrame {
//
//    public SoundClipTest() {
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setTitle("Test Sound Clip");
//        this.setSize(300, 200);
//        this.setVisible(true);
//
//        try {
//            // Open an audio input stream.
//            Converter converter = new Converter();
//            String inputPathOGG = "C:\\Users\\ferna\\OneDrive\\Documentos\\fileOGG.ogg";
//            File file = new File(inputPathOGG);
//            AudioInputStream audioIn = converter.createOgg(file);
//
//
////            String wavPath = "C:\\Users\\ferna\\OneDrive\\Documentos\\teste.wav";
////            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(wavPath));
//            // Get a sound clip resource.
//            Clip clip = AudioSystem.getClip();
//            // Open audio clip and load samples from the audio input stream.
//            clip.open(audioIn);
//            clip.start();
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        new SoundClipTest();
//    }
//}
